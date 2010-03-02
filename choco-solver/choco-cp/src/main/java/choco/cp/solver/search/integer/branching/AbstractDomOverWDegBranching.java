package choco.cp.solver.search.integer.branching;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractLargeIntBranchingStrategy;
import choco.kernel.solver.branch.Extension;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntArrayList;

import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDomOverWDegBranching extends
AbstractLargeIntBranchingStrategy implements PropagationEngineListener, IRandomBreakTies {

	//*****************************************************************//
	//*********** computation of weighted degrees and failures ****//
	//***************************************************************//
	protected static final int ABSTRACTCONTRAINT_EXTENSION = AbstractSConstraint.getAbstractSConstraintExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

    protected static final int ABSTRACTVAR_EXTENSION = AbstractVar.getAbstractVarExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

    //*****************************************************************//
    //*******************  Variable selector structure ***************//
	//***************************************************************//

	protected final class DomWDegStruct {

		int currentIndex = -1;

		private int currentSize = -1;

		private int currentWeight = -1;

		public void setIndex(int idx) {
			currentIndex = idx;
			currentWeight = getVarExtension((AbstractVar) vars[idx]).get();
			currentSize = getDomMesure(idx);
		}

		public final void reset() {
			currentIndex = -1;
			currentWeight = -1;
			currentSize = -1;
		}

		public boolean isEmpty() {
			return currentIndex == -1; 
		}

		public final int compare(int idx) {
			return (getVarExtension( (AbstractVar) vars[idx]).get() * currentSize) - (currentWeight * getDomMesure(idx));
		}

	}


	protected final Solver solver;

	protected final IntDomainVar[] vars;

	private Random randomBreakTies;

	//FIXME seems useless
	protected int maxConstraintArity = Integer.MAX_VALUE;

	private AbstractSConstraint reuseCstr;

	private final TIntArrayList reuseList = new TIntArrayList();

	private final DomWDegStruct dwdegStruct = new DomWDegStruct();

	public AbstractDomOverWDegBranching(Solver solver, IntDomainVar[] _vars) {
		super();
		this.solver = solver;
		this.vars = _vars;
		initExtensions(this.solver);
		this.solver.getPropagationEngine().addPropagationEngineListener(this);
	}

	@Override
	public final void safeDelete() {
		solver.getPropagationEngine().removePropagationEngineListener(this);
	}

	protected static IntDomainVar[] buildVars(Solver s) {
		IntDomainVar[] vars = new IntDomainVar[s.getNbIntVars()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = (IntDomainVar) s.getIntVar(i);
		}
		return vars;
	}


	@Override
	public final void cancelRandomBreakTies() {
		randomBreakTies = null;
	}

	@Override
	public void setRandomBreakTies(long seed) {
		randomBreakTies = new Random(seed);
	}

	public final int getMaxConstraintArity() {
		return maxConstraintArity;
	}

	public final void setMaxConstraintArity(int maxConstraintArity) {
		this.maxConstraintArity = maxConstraintArity;
	}


	public static int computeWeightedDegreeFromScratch(Var v) {
		int weight = 0;
		final DisposableIntIterator iter= v.getIndexVector().getIndexIterator();
		while (iter.hasNext()) {
			final int idx = iter.next();
			AbstractSConstraint cstr = (AbstractSConstraint) v.getConstraint(idx);
			if (SConstraintType.INTEGER.equals(cstr.getConstraintType()) && cstr.getNbVarNotInst() > 1) {
				weight+= getConstraintExtension(cstr).get() + cstr.getFineDegree(v.getVarIndex(idx));
			}
		}
		iter.dispose();
		return weight;
	}
	//*****************************************************************//
	//*******************  Extension managment ***********************//
	//***************************************************************//

	protected static void addConstraintExtension(SConstraint c) {
		( (AbstractSConstraint) c).addExtension(ABSTRACTCONTRAINT_EXTENSION);
	}

	protected static void addVariableExtension(Var v) {
		( (AbstractVar) v).addExtension(ABSTRACTVAR_EXTENSION);
	}

	protected static void initExtensions(Solver s) {
		DisposableIterator<SConstraint> iter = s.getConstraintIterator();
        for (; iter.hasNext();) {
			addConstraintExtension(iter.next());
		}
        iter.dispose();
		for (int i = 0; i < s.getNbIntVars(); i++) {
			addVariableExtension(s.getIntVar(i));
		}
		for (Iterator<Integer> it = s.getIntConstantSet().iterator(); it.hasNext();) {
			addVariableExtension(s.getIntConstant(it.next()));
		}
	}

	protected static Extension getConstraintExtension(AbstractSConstraint c) {
		return 	c.getExtension(ABSTRACTCONTRAINT_EXTENSION);
	}

	protected static Extension getVarExtension(AbstractVar v) {
		return 	v.getExtension(ABSTRACTVAR_EXTENSION);
	}


	//*****************************************************************//
	//*******************  Weighted degrees and failures managment ***//
	//***************************************************************//

	@Override
	public void initConstraintForBranching(SConstraint c) {
		addConstraintExtension(c);
	}


	@Override
	public void initBranching() {
		for (int i = 0; i < solver.getNbIntVars(); i++) {
			// Pour etre sur, on verifie toutes les contraintes... au cas ou une d'entre elle serait deja instantiee !!
			IntDomainVar v = (IntDomainVar) solver.getIntVar(i);
			int weight = 0;
			final DisposableIntIterator iter= v.getIndexVector().getIndexIterator();
			while (iter.hasNext()) {
				final int idx = iter.next();
				reuseCstr = (AbstractSConstraint) v.getConstraint(idx);
				if (reuseCstr.getNbVarNotInst() > 1) {
					weight+= getConstraintExtension(reuseCstr).get() + reuseCstr.getFineDegree(v.getVarIndex(idx));
				}
			}
			iter.dispose();
			getVarExtension((AbstractVar) v).set(weight);
		}
		//logWeights(ChocoLogging.getChocoLogger(), Level.INFO);
	}

	protected final void updateVarWeights(AbstractVar currentVar, boolean assign) {
		for (Iterator<SConstraint> iter = currentVar.getConstraintsIterator(); iter.hasNext();) {
			reuseCstr = (AbstractSConstraint) iter.next();
			if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType()) &&
					reuseCstr.getNbVarNotInst() == 2) {
				int delta = assign ? -getConstraintExtension(reuseCstr).get() : getConstraintExtension(reuseCstr).get();
				//System.out.println("branching "+reuseCstr.pretty()+" "+getConstraintExtension(reuseCstr).getNbFailure());
				for (int k = 0; k < reuseCstr.getNbVars(); k++) {
					AbstractVar var = (AbstractVar) reuseCstr.getVar(k);
					if (var != currentVar && !var.isInstantiated()) {
						getVarExtension(var).add(delta);
					}
				}
			}
		}
	}

	protected final void addFailure(Object cause) {
		reuseCstr = (AbstractSConstraint) cause;
        // <= maxConstraintArity ? due to ClauseStore
		if(SConstraintType.INTEGER.equals(reuseCstr.getConstraintType()) && reuseCstr.getNbVars() <= maxConstraintArity) {
			try {
				getConstraintExtension(reuseCstr).add(1);
			} catch (NullPointerException npe) {
				// If there was a postCut, the extension has not been generated at the Branching creation
				initConstraintForBranching(reuseCstr);
				getConstraintExtension(reuseCstr).add(1);
			}
			//System.out.println("contradiction "+reuseCstr.pretty()+" "+getConstraintExtension(reuseCstr).getNbFailure());
			for (int k = 0; k < reuseCstr.getNbVars(); k++) {
				//FIXME add FineDegree too and check number of non instantiated variables ?
				getVarExtension( (AbstractVar) reuseCstr.getVar(k)).add(1);
			}
		}
	}

	public void contradictionOccured(ContradictionException e) {
		if (e.getDomOverDegContradictionCause() != null) {
			addFailure(e.getDomOverDegContradictionCause());
		}
	}

	//*****************************************************************//
	//*******************  Variable Selection *************************//
	//***************************************************************//

	/**
	 * measure of the domain size  of the i-th variable (by default the domain size)
	 * @param i the index of the variable 
	 */
	protected int getDomMesure(int i) {
		return  vars[i].getDomainSize();
	}


	protected final int selectBranchingIndex() {
		dwdegStruct.reset();
		if (randomBreakTies == null) {
			for (int i = 0; i < vars.length; i++) {
				//assert( vars[i].isInstantiated() 
				//		|| computeWeightedDegreeFromScratch(vars[i]) ==  getVarExtension((AbstractVar) vars[i]).getSumWeights());
				if ( ! vars[i].isInstantiated()  && 
						( dwdegStruct.isEmpty() || dwdegStruct.compare(i) > 0) ) {
					//first or best non instantiated variable
					dwdegStruct.setIndex(i);
				}
			}
			return dwdegStruct.currentIndex;
		} else {
			//redondant code with previous case, really ugly.
			reuseList.reset();
			for (int i = 0; i < vars.length; i++) {
				if ( ! vars[i].isInstantiated()) {
					final int note = dwdegStruct.compare(i);
					if(dwdegStruct.isEmpty() || note>0) {
						dwdegStruct.setIndex(i);
						reuseList.reset();
						reuseList.add(i);
					}else if(note>= 0) {
						reuseList.add(i);
					}
				}
			}
			if(reuseList.isEmpty()) {return -1;}
			else if(reuseList.size() == 1) {return reuseList.getQuick(0);}
			else {return reuseList.getQuick(randomBreakTies.nextInt(reuseList.size()));}
		}
	}

	protected int reuseIndex;

	public Object selectBranchingObject() throws ContradictionException {
		reuseIndex = selectBranchingIndex();
		return reuseIndex < 0 ? null : vars[reuseIndex];
	}


	//*****************************************************************//
	//*******************  Weights and failures Display **************//
	//***************************************************************//

	protected final void appendConstraint(StringBuilder b, SConstraint c) {
		final AbstractSConstraint cstr = (AbstractSConstraint) c;
		b.append("w=").append(getConstraintExtension(cstr).get());
		b.append("\t").append(cstr.pretty());
		b.append('\n');
	}

	protected final void appendVariable(StringBuilder b, Var v) {
		final AbstractVar var = (AbstractVar) v;
		b.append("w=").append(getVarExtension(var).get());
		b.append("\t").append(var.pretty());
		b.append('\n');
	}

	public final void logWeights(Logger logger, Level level) {
		if(logger.isLoggable(level)) {
			final StringBuilder b = new StringBuilder();
			b.append("===> Display DomWDeg weights\n");
			b.append("\n###\tConstraints\t###\n");
            DisposableIterator<SConstraint> iter = solver.getConstraintIterator();
			for (; iter.hasNext();) {
				appendConstraint(b, iter.next());
			}
            iter.dispose();
			b.append("\n###\tVariables\t###\n");
			for (int i = 0; i < solver.getNbIntVars(); i++) {
				appendVariable(b, solver.getIntVar(i));
			}
			b.append("\n###\tConstants\t###\n");
			for (Iterator<Integer> it = solver.getIntConstantSet().iterator(); it.hasNext();) {
				appendVariable(b, solver.getIntConstant(it.next()));
			}
			b.append("<=== End Display DomWDeg weights\n");
			logger.log(level, new String(b));
			ChocoLogging.flushLogs();
		}
	}

}