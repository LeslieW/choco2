/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.branching;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractLargeIntBranching;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/* History:
 * 2008-04-23 : Creation : dom / wdeg needs to be a branching not just an heuristic to allow to deal with
 *              backtracking events !
 */
/**
 * WARNING ! This implementation suppose that the variables will not change. It copies all variables in an array
 * at the beginning !!
 */
public class DomOverWDegBranching extends AbstractLargeIntBranching implements PropagationEngineListener {
	protected static final int ABSTRACTCONTRAINT_EXTENSION =
		AbstractSConstraint.getAbstractSConstraintExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

	protected static final class DomOverWDegBranchingConstraintExtension {
		protected int nbFailure = 0;

		public int getSumWeights() {
			return nbFailure;
		}

		public void addFailure() {
			nbFailure++;
		}
	}

	protected static final int ABSTRACTVAR_EXTENSION =
		AbstractVar.getAbstractVarExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

	protected static final class DomOverWDegBranchingVarExtension {
		protected int sum_weighted = 0;

		public int getSumWeights() {
			return sum_weighted;
		}

		public void addWeight(int w) {
			sum_weighted += w;
		}
	}

	// Les variables parmis lesquelles on veut brancher !
	private IntVar[] _vars;

	// L'heuristique pour le svaleurs
	private ValIterator _valIterator;

	// L'heuristique pour le svaleurs
	private ValSelector _valSelector;

	// Le solveur
	private Solver _solver;

	//a reference to a random object when random ties are wanted
	protected Random randomBreakTies;

	private AbstractSConstraint reuseCstr;

	// Le constructeur avec :
	// * le solver pour fournir les variables
	// * l'heuristique de valeurs pour instantier une valeur
	public DomOverWDegBranching(Solver s, ValIterator valHeuri, IntVar[] vars) {
		_solver = s;

		for (Iterator iter = s.getIntConstraintIterator(); iter.hasNext();) {
			AbstractSConstraint c = (AbstractSConstraint) iter.next();
			c.setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegBranchingConstraintExtension());
		}
		for (int i = 0; i < s.getNbIntVars(); i++) {
			IntVar v = s.getIntVar(i);
			((AbstractVar) v).setExtension(ABSTRACTVAR_EXTENSION, new DomOverWDegBranchingVarExtension());
		}

		for (Iterator it = s.getIntConstantSet().iterator(); it.hasNext();) {
			int val = (Integer) it.next();
			Var v = s.getIntConstant(val);
			((AbstractVar) v).setExtension(ABSTRACTVAR_EXTENSION, new DomOverWDegBranchingVarExtension());
		}

		s.getPropagationEngine().addPropagationEngineListener(this);
		// On sauvegarde l'heuristique
		_valIterator = valHeuri;
		_vars = vars;
	}

    /**
     * Define action to do just before a deletion.
     */
    @Override
    public void safeDelete() {
        _solver.getPropagationEngine().removePropagationEngineListener(this);
    }

    public DomOverWDegBranching(Solver s, ValIterator valHeuri) {
		this(s, valHeuri, buildVars(s));
	}


	public DomOverWDegBranching(Solver s, ValSelector valHeuri, IntVar[] vars) {
		_solver = s;

		for (Iterator iter = s.getIntConstraintIterator(); iter.hasNext();) {
			AbstractSConstraint c = (AbstractSConstraint) iter.next();
			c.setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegBranchingConstraintExtension());
		}
		for (int i = 0; i < s.getNbIntVars(); i++) {
			IntVar v = s.getIntVar(i);
			((AbstractVar) v).setExtension(ABSTRACTVAR_EXTENSION, new DomOverWDegBranchingVarExtension());
		}

		for (Iterator it = s.getIntConstantSet().iterator(); it.hasNext();) {
			int val = (Integer) it.next();
			Var v = s.getIntConstant(val);
			((AbstractVar) v).setExtension(ABSTRACTVAR_EXTENSION, new DomOverWDegBranchingVarExtension());
		}

		s.getPropagationEngine().addPropagationEngineListener(this);

		// On sauvegarde l'heuristique
		_valSelector = valHeuri;
		_vars = vars;
	}

	public DomOverWDegBranching(Solver s, ValSelector valHeuri) {
		this(s, valHeuri, buildVars(s));
	}

	private static IntVar[] buildVars(Solver s) {
		IntVar[] vars = new IntVar[s.getNbIntVars()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = s.getIntVar(i);
		}
		return vars;
	}


	public void initBranching() {
		int nb_variables = _vars.length;
		for (int variable_idx = 0; variable_idx < nb_variables; variable_idx++) {
			// On ajoute la variable et le poids
			IntVar v = _vars[variable_idx];// = _solver.getIntVar(variable_idx);

			// Pour etre sur, on verifie toutes les contraintes... au cas ou une d'entre elle serait deja instanti�e !!
			int weight = 0;
			DisposableIntIterator c = v.getIndexVector().getIndexIterator();
			int idx = 0;
			while (c.hasNext()) {
				idx = c.next();
				AbstractSConstraint cstr = (AbstractSConstraint) v.getConstraint(idx);
				if (cstr.getNbVarNotInst() > 1) {
					weight += ((DomOverWDegBranchingConstraintExtension) cstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure + cstr.getFineDegree(v.getVarIndex(idx));
				}
			}
			((DomOverWDegBranchingVarExtension) ((AbstractVar) v).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted = weight;
		}
		//logWeights(ChocoLogging.getChocoLogger(), Level.INFO);
	}

	public void initConstraintForBranching(SConstraint c) {
		((AbstractSConstraint) c).setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegBranchingConstraintExtension());
	}

	public void setBranchingVars(IntVar[] vs) {
		_vars = vs;
	}

	public void setRandomVarTies(int seed) {
		randomBreakTies = new Random(seed);
	}

	public Object selectBranchingObject() throws ContradictionException {
		int previous_Size = -1;
		int previous_Weight = -1;
		IntVar previous_Variable = null;
		if (randomBreakTies == null) {
			for (int i = 0; i < _vars.length; i++) {
				IntDomainVar var = (IntDomainVar) _vars[i];
				if (var.isInstantiated()) continue;
				else/* (!var.isInstantiated()) */{
					if (previous_Variable == null) {
						previous_Variable = var;
						previous_Size = var.getDomainSize();
						previous_Weight = ((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted;
					} else {
						if (((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted
								* previous_Size - previous_Weight * var.getDomainSize() > 0) {
							previous_Variable = var;
							previous_Size = var.getDomainSize();
							previous_Weight = ((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted;
						}
					}
				}
			}
			return previous_Variable;
		} else {
			//redondant code with previous case, really ugly.
			List<IntDomainVar> lvs = new LinkedList<IntDomainVar>();
			for (int i = 0; i < _vars.length; i++) {
				IntDomainVar var = (IntDomainVar) _vars[i];
				if (var.isInstantiated()) continue;
				else{ //if (!var.isInstantiated()) {
					if (previous_Variable == null) {
						previous_Variable = var;
						previous_Size = var.getDomainSize();
						previous_Weight = ((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted;
						lvs.add(var);
					} else {
						int note = ((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted
						* previous_Size - previous_Weight * var.getDomainSize();
						if (note > 0) {
							lvs.clear();
							lvs.add(var);
							previous_Size = var.getDomainSize();
							previous_Weight = ((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted;
						} else if (note >= 0) {
							lvs.add(var);
						}

					}
				}
			}
			if (lvs.size() == 0) return null;
			return lvs.get(randomBreakTies.nextInt(lvs.size()));
		}
	}

	IntDomainVar v;
	public void setFirstBranch(final IntBranchingDecision decision) {
		v = decision.getBranchingIntVar();
		for (Iterator<SConstraint> iter = v.getConstraintsIterator(); iter.hasNext();) {
			reuseCstr = (AbstractSConstraint) iter.next();
			if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())) {
				if (reuseCstr.getNbVarNotInst() == 2) {
					for (int k = 0; k < reuseCstr.getNbVars(); k++) {
						AbstractVar var = (AbstractVar) ((AbstractIntSConstraint) reuseCstr).getIntVar(k);
						if (var != v && !var.isInstantiated()) {
							((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted -=
								((DomOverWDegBranchingConstraintExtension) reuseCstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure;
						}
					}
				}
			}
		}
		if (_valIterator != null) {
			decision.setBranchingValue( _valIterator.getFirstVal(v));
		} else {
			decision.setBranchingValue( _valSelector.getBestVal(v));
		}
	}

	public void setNextBranch(final IntBranchingDecision decision) {
		if (_valIterator != null) {
			decision.setBranchingValue( _valIterator.getNextVal( decision.getBranchingIntVar(), decision.getBranchingValue()));
		} else {
			decision.setBranchingValue( _valSelector.getBestVal(decision.getBranchingIntVar()));
		}
	}

	public boolean finishedBranching(final IntBranchingDecision decision) {
		if (_valIterator != null) {
			v = decision.getBranchingIntVar();
			final boolean finished = !_valIterator.hasNextVal(v, decision.getBranchingValue());
			if (finished) {
				for (Iterator<SConstraint> iter = v.getConstraintsIterator(); iter.hasNext();) {
					reuseCstr = (AbstractSConstraint) iter.next();
					if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())) {
						if (reuseCstr.getNbVarNotInst() == 2) {
							for (int k = 0; k < reuseCstr.getNbVars(); k++) {
								AbstractVar var = (AbstractVar) ((AbstractIntSConstraint) reuseCstr).getIntVar(k);
								if (var != v && !var.isInstantiated()) {
									((DomOverWDegBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted +=
										((DomOverWDegBranchingConstraintExtension) reuseCstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure;
								}
							}
						}
					}
				}
			}
			return finished;
		} else {
			//return _valSelector.getBestVal((IntDomainVar) x) == null;
			return false;
		}
	}
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		decision.setIntVal();
	}
	public void goUpBranch(final IntBranchingDecision decision) throws ContradictionException {
		IntDomainVar v = (IntDomainVar) x;
		v.remVal(i);     // On le retire !! mais attention pas de selector pour les variables du coup !!!!
	}
	
	

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return decision.getBranchingObject() + LOG_DECISION_MSG_ASSIGN + decision.getBranchingValue();
	}

	public void contradictionOccured(ContradictionException e) {
		Object cause = e.getContradictionCause();
		if (cause != null && e.getContradictionType() == ContradictionException.CONSTRAINT) {
			reuseCstr = (AbstractSConstraint) cause;
			if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())) {
				try {
					((DomOverWDegBranchingConstraintExtension) reuseCstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure++;
				} catch (NullPointerException npe) {
					// If there was a postCut, the extension has not been generated at the Branching creation
					reuseCstr.setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegBranchingConstraintExtension());
					((DomOverWDegBranchingConstraintExtension) reuseCstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure++;
				}
				for (int k = 0; k < reuseCstr.getNbVars(); k++) {
					AbstractVar var = (AbstractVar) ((AbstractIntSConstraint) reuseCstr).getIntVar(k);
					DomOverWDegBranchingVarExtension extens = (DomOverWDegBranchingVarExtension) var.getExtension(ABSTRACTVAR_EXTENSION);
					extens.sum_weighted++;
				}
			}
		}
	}

	protected final void appendConstraint( StringBuilder b, SConstraint c) {
		final AbstractSConstraint cstr = (AbstractSConstraint) c;
		b.append("w=").append( ( (DomOverWDegBranchingConstraintExtension) cstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure);
		b.append("\t").append(cstr.pretty());
		b.append('\n');
	}

	protected final void appendVariable( StringBuilder b, Var v) {
		AbstractVar var = (AbstractVar) v;
		b.append("w=").append( ( (DomOverWDegBranchingVarExtension) var.getExtension(ABSTRACTVAR_EXTENSION)).sum_weighted);
		b.append("\t").append(var.pretty());
		b.append('\n');
	}
	
	public final void logWeights(Logger logger, Level level) {
		if(logger.isLoggable(level)) {
			final StringBuilder b = new StringBuilder();
			b.append("===> Display DomWDeg weights\n");
			b.append("\n###\tConstraints\t###\n");
			for (Iterator<SConstraint> iter = _solver.getIntConstraintIterator(); iter.hasNext();) {
				appendConstraint(b, iter.next());
			}
			b.append("\n###\tVariables\t###\n");
			for (int i = 0; i < _solver.getNbIntVars(); i++) {
				appendVariable(b, _solver.getIntVar(i));
			}
			b.append("\n###\tConstants\t###\n");
			for (Iterator<Integer> it = _solver.getIntConstantSet().iterator(); it.hasNext();) {
				appendVariable(b, _solver.getIntConstant(it.next()));
			}
			b.append("<=== End Display DomWDeg weights\n");
			logger.log(level, new String(b));
		}
	}
	
}
