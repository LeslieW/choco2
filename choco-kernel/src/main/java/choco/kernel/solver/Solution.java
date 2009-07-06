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
package choco.kernel.solver;

import java.util.Collection;
import java.util.LinkedList;

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.AbstractMeasures;
import choco.kernel.solver.search.AbstractOptimize;
import choco.kernel.solver.search.IMeasures;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

/**
 * A class storing a state of the model
 */
public class Solution {

	/**
	 * the solver owning the solution
	 */
	protected Solver solver;

	/**
	 * data storage for values of search variables
	 */
	protected int[] intVarValues;

	protected RealInterval[] realVarValues;

	protected int[][] setVarValues;

	protected int objectiveValue;

	protected final SolutionMeasures measures;
	/**
	 * Constructor
	 *
	 * @param solver the model owning the solution
	 */
	public Solution(Solver solver) {
		this.solver = solver;
		intVarValues = new int[solver.getNbIntVars()];
		setVarValues = new int[solver.getNbSetVars()][];
		realVarValues = new RealInterval[solver.getNbRealVars()];
		objectiveValue = Integer.MAX_VALUE;
		measures = new SolutionMeasures(solver);
	}

	public void setSolver(Solver s) {
		this.solver = s;
		reset();
	}
	
	public void save() {
		//record values
		for (int i = 0; i < solver.getNbIntVars(); i++) {
			final IntDomainVar vari = (IntDomainVar) solver.getIntVar(i);
			recordIntValue(i, vari.isInstantiated() ? vari.getVal() : Integer.MAX_VALUE);
			
		}
		for (int i = 0; i < solver.getNbSetVars(); i++) {
			final SetVar vari = solver.getSetVar(i);
			recordSetValue(i, vari.isInstantiated() ? vari.getValue() : null);
		}
		
		for (int i = 0; i < solver.getNbRealVars(); i++) {
			RealVar vari = solver.getRealVar(i);
			// if (vari.isInstantiated()) { // Not always "instantiated" : for
			// instance, if the branching
			// does not contain the variable, the precision can not be
			// reached....
				recordRealValue(i, vari.getValue());
			// }
		}
		//record objective
		final AbstractGlobalSearchStrategy strategy = solver.getSearchStrategy();
		if (solver.getSearchStrategy() instanceof AbstractOptimize) {
			recordIntObjective(((AbstractOptimize) strategy)
					.getObjectiveValue());
		}else {
			objectiveValue = Integer.MAX_VALUE;
		}
		//record limits
		for (AbstractGlobalSearchLimit l : strategy.limits) {
			recordLimit(l);
		}
	}
	
	/**
	 * prepare to record a new solution
	 */
	public final void reset() {
		measures.solutionLimits.clear();
		if(solver.getNbIntVars() > intVarValues.length) {
			intVarValues = new int[solver.getNbIntVars()];
		}
		if(solver.getNbSetVars() > setVarValues.length) {
			setVarValues = new int[solver.getNbSetVars()][];
		}
		if(solver.getNbRealVars() > realVarValues.length) {
			realVarValues = new RealInterval[solver.getNbRealVars()];
		}
	}
	
	
	public final IMeasures getMeasures() {
		return measures;
	}

	public final int getObjectiveValue() {
		return objectiveValue;
	}


	public final void recordIntValue(int intVarIndex, int intVarValue) {
		intVarValues[intVarIndex] = intVarValue;
	}

	public final void recordSetValue(int setVarIndex, int[] setVarValue) {
		setVarValues[setVarIndex] = setVarValue;
	}

	public final void recordRealValue(int realVarIndex, RealInterval realVarValue) {
		realVarValues[realVarIndex] = realVarValue;
	}

	public final void recordIntObjective(int intObjectiveValue) {
		objectiveValue = intObjectiveValue;
	}

	public void recordLimit(AbstractGlobalSearchLimit limit){
		measures.solutionLimits.add(new StoredLimit(limit));
	}


	/**
	 * Accessor to the value of a variable in a solution
	 *
	 * @param varIndex the index of the variable among all variables of the model
	 * @return its value (whenever it is instantiated in the solution), or Integer.MAX_VALUE otherwise
	 */
	public int getIntValue(int varIndex) {
		return intVarValues[varIndex];
	}

	public int[] getSetValue(int varIndex) {
		return setVarValues[varIndex];
	}

	public RealInterval getRealValue(int varIndex) {
		return realVarValues[varIndex];
	}

	public Collection<AbstractGlobalSearchLimit> getLimits() {
		return measures.solutionLimits;
	}
	

	final class SolutionMeasures extends AbstractMeasures {

		public final Collection<AbstractGlobalSearchLimit> solutionLimits;
		
		public SolutionMeasures(Solver solver) {
			super();
			solutionLimits = new LinkedList<AbstractGlobalSearchLimit>();
		}

		

		@Override
		public Collection<AbstractGlobalSearchLimit> getLimits() {
			return solutionLimits;
		}



		@Override
		public Number getObjectiveValue() {
			return objectiveValue;
		}

	}
	
	public static final class StoredLimit extends AbstractGlobalSearchLimit {

		/**
		 * A copy constructor.
		 */
		public StoredLimit(AbstractGlobalSearchLimit toCopy) {
			super(toCopy.getStrategy(),toCopy.getNbMax(),toCopy.getType());
			nb = toCopy.getNb();
			nbTot = toCopy.getNbTot();
			nbMax = toCopy.getNbMax();
		}

		/**
		 * synchronize the counters of the two limits.
		 */
		public final void synchronize(AbstractGlobalSearchLimit toSync) {
			nb = toSync.getNb();
			nbTot = toSync.getNbTot();
		}
		
		@Override
		public boolean endNode(AbstractGlobalSearchStrategy strategy) {
			return false;
		}

		@Override
		public boolean newNode(AbstractGlobalSearchStrategy strategy) {
			return false;
		}



	}


	
}


