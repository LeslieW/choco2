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

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;

import java.util.Iterator;
import java.util.Random;

/* History:
 * 2008-04-23 : Creation : dom / wdeg needs to be a branching not just an heuristic to allow to deal with
 *              backtracking events !
 */
/**
 * WARNING ! This implementation suppose that the variables will not change. It
 * copies all variables in an array at the beginning !!
 * @deprecated use {@link DomOverWDegBinBranching2} instead.
 */
@Deprecated
public class DomOverWDegBinBranching extends AbstractAssignOrForbidBranching {
	private static final int CONSTRAINT_EXTENSION = AbstractSConstraint
	.getAbstractSConstraintExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

	protected final static class ConstraintExtension {
		private int nbFailure = 0;
	}

	private static final int VAR_EXTENSION = AbstractVar
	.getAbstractVarExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

	protected final static class VarExtension {
		private int sum_weighted = 0;
	}

	// Les variables parmis lesquelles on veut brancher !
	private IntDomainVarImpl[] vars;

	

	// a reference to a random object when random ties are wanted
	protected Random randomBreakTies;

	// Le constructeur avec :
	// * le solver pour fournir les variables
	// * l'heuristique de valeurs pour instantier une valeur
	public DomOverWDegBinBranching(Solver s, ValSelector valHeuri,
			IntDomainVar[] intDomainVars) {
		super(valHeuri);
		for (Iterator<SConstraint> iter = s.getIntConstraintIterator(); iter
		.hasNext();) {
			AbstractSConstraint c = (AbstractSConstraint) iter.next();
			c.setExtension(CONSTRAINT_EXTENSION, new ConstraintExtension());
		}
		for (int i = 0; i < s.getNbIntVars(); i++) {
			IntVar v = s.getIntVar(i);
			((AbstractVar) v).setExtension(VAR_EXTENSION, new VarExtension());
		}

		for (int val : s.getIntConstantSet()) {
			Var v = s.getIntConstant(val);
			((AbstractVar) v).setExtension(VAR_EXTENSION, new VarExtension());
		}

		// On sauvegarde l'heuristique
		valSelector = valHeuri;
		vars = new IntDomainVarImpl[intDomainVars.length];
		for (int i = intDomainVars.length; --i >= 0;) {
			vars[i] = (IntDomainVarImpl) intDomainVars[i];
		}
	}

	public DomOverWDegBinBranching(Solver s, ValSelector valHeuri) {
		this(s, valHeuri, buildVars(s));
	}

	@Override
	public void initConstraintForBranching(SConstraint s) {
		((AbstractSConstraint) s).setExtension(CONSTRAINT_EXTENSION, new ConstraintExtension());        
	}

	private static IntDomainVarImpl[] buildVars(Solver s) {
		IntDomainVarImpl[] vars = new IntDomainVarImpl[s.getNbIntVars()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = (IntDomainVarImpl) s.getIntVar(i);
		}
		return vars;
	}

	@Override
	public void initBranching() {
		for (IntDomainVarImpl v : vars) {
			// Pour etre sur, on verifie toutes les contraintes... au cas ou une
			// d'entre elle serait deja instanti�e !!
			int weight = 0;
			int idx = 0;
			DisposableIntIterator c = v.getIndexVector().getIndexIterator();
			for (; c.hasNext();) {
				idx = c.next();
				AbstractSConstraint cstr = (AbstractSConstraint) v.getConstraint(idx);
				if (cstr.getNbVarNotInst() > 1) {
					weight += ((ConstraintExtension) cstr
							.getExtension(CONSTRAINT_EXTENSION)).nbFailure + cstr.getFineDegree(v.getVarIndex(idx));
				}
			}
			c.dispose();
			((VarExtension) v.getExtension(VAR_EXTENSION)).sum_weighted = weight;
		}
	}

	public void setRandomVarTies(int seed) {
		randomBreakTies = new Random(seed);
	}

	public Object selectBranchingObject() throws ContradictionException {
		int bestSize = 0;
		int bestWeight = 0;
		int ties = 1;
		IntDomainVar bestVariable = null;

		for (IntDomainVar var : vars) {
			if (var.isInstantiated())
				continue;

			final int weight = ((VarExtension) ((AbstractVar) var)
					.getExtension(VAR_EXTENSION)).sum_weighted;
			final int size = var.getDomainSize();

			if (bestVariable == null || weight * bestSize >= bestWeight * size) {
				if (bestVariable != null
						&& weight * bestSize == bestWeight * size) {
					if (randomBreakTies == null) {
						continue;
					}
					ties++;
					if (randomBreakTies.nextInt(ties) > 0) {
						continue;
					}
				} else {
					ties = 1;
				}
				bestVariable = var;
				bestSize = size;
				bestWeight = weight;
			}
		}
		if (bestVariable == null) {
			return null;
		}
		return new IntVarValPair(bestVariable, valSelector
				.getBestVal(bestVariable));
	}

	private void assign(IntDomainVar v) {
		for (Iterator<SConstraint> iter = v.getConstraintsIterator(); iter
		.hasNext();) {
			final AbstractSConstraint reuseCstr = (AbstractSConstraint) iter
			.next();
			if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())
					&& reuseCstr.getNbVarNotInst() == 2) {
				for (int k = 0; k < reuseCstr.getNbVars(); k++) {
					AbstractVar var = (AbstractVar) ((AbstractIntSConstraint) reuseCstr)
					.getIntVar(k);
					if (var != v && !var.isInstantiated()) {
						((VarExtension) var.getExtension(VAR_EXTENSION)).sum_weighted -= ((ConstraintExtension) reuseCstr
								.getExtension(CONSTRAINT_EXTENSION)).nbFailure;
					}
				}
			}
		}
	}

	private void unassign(IntDomainVar v) {
		for (Iterator<SConstraint> iter = v.getConstraintsIterator(); iter
		.hasNext();) {
			final AbstractSConstraint reuseCstr = (AbstractSConstraint) iter
			.next();
			if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())) {
				if (reuseCstr.getNbVarNotInst() == 2) {
					for (int k = 0; k < reuseCstr.getNbVars(); k++) {
						AbstractVar var = (AbstractVar) ((AbstractIntSConstraint) reuseCstr)
						.getIntVar(k);
						if (var != v && !var.isInstantiated()) {
							((VarExtension) ((AbstractVar) var)
									.getExtension(VAR_EXTENSION)).sum_weighted += ((ConstraintExtension) reuseCstr
											.getExtension(CONSTRAINT_EXTENSION)).nbFailure;
						}
					}
				}
			}
		}
	}

	@Override
	public void goDownBranch(final IntBranchingDecision ctx) throws ContradictionException {
		final IntDomainVar v = ctx.getBranchingIntVar();
		if (ctx.getBranchIndex() == 0) {
			assign(v);
			v.setVal(ctx.getBranchingValue());
		} else {
			unassign(v);
			v.remVal(ctx.getBranchingValue());
		}
		// Calls to propagate() are useless since it is done in the SearchLoop
	}



	public void contradictionOccured(ContradictionException e) {
		Object cause = e.getDomOverDegContradictionCause();
		if (cause != null) {
			final AbstractSConstraint causeCstr = (AbstractSConstraint) cause;
			if (SConstraintType.INTEGER.equals(causeCstr.getConstraintType())) {
				try {
					((ConstraintExtension) causeCstr
							.getExtension(CONSTRAINT_EXTENSION)).nbFailure++;
				} catch (NullPointerException npe) {
					// If there was a postCut, the extension has not been
					// generated at the Branching creation
					causeCstr.setExtension(CONSTRAINT_EXTENSION,
							new ConstraintExtension());
					((ConstraintExtension) causeCstr
							.getExtension(CONSTRAINT_EXTENSION)).nbFailure++;
				}
				for (int k = 0; k < causeCstr.getNbVars(); k++) {
					AbstractVar var = (AbstractVar) ((AbstractIntSConstraint) causeCstr)
					.getIntVar(k);
					((VarExtension) var.getExtension(VAR_EXTENSION)).sum_weighted++;
				}
			}
		}
	}
}
