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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.Limit;
import choco.kernel.solver.search.measures.AbstractMeasures;
import choco.kernel.solver.search.measures.IMeasures;
import choco.kernel.solver.variables.real.RealInterval;

/**
 * A class storing a state of the model
 */
public class Solution {

	/**
	 * the solver owning the solution
	 */
	protected Solver solver;
	
	protected int solutionCount;
	/**
	 * data storage for values of search variables
	 */
	protected int[] intVarValues;

	protected RealInterval[] realVarValues;

	protected int[][] setVarValues;

	protected int objectiveValue;

	protected final SolutionMeasures measures;

	private List<AbstractGlobalSearchLimit> solutionLimits;
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
		solutionLimits = new LinkedList<AbstractGlobalSearchLimit>();
		measures = new SolutionMeasures();
	}

	public void setSolver(Solver s) {
		this.solver = s;
		solutionLimits.clear();
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

	public final int getLimitValue(Limit limit) {
		return AbstractGlobalSearchLimit.getLimitValue(solutionLimits, limit);
	}
	
	public final int getObjectiveValue() {
		return objectiveValue;
	}

	public final void recordSolutionCount(int solutionCount) {
		this.solutionCount = solutionCount;
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
		solutionLimits.add(new StoredLimit(limit));
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


	final class SolutionMeasures extends AbstractMeasures implements IMeasures {

		public SolutionMeasures() {
			super();
		}

		@Override
		public boolean isObjectiveOptimal() {
			return false;
		}


		@Override
		public Collection<AbstractGlobalSearchLimit> getLimits() {
			return Collections.unmodifiableCollection(solutionLimits);
		}


		@Override
		public Number getObjectiveValue() {
			
			return objectiveValue == Integer.MAX_VALUE ? null : Integer.valueOf(objectiveValue);
		}

		@Override
		public boolean existsSolution() {
			return true;
		}

		@Override
		public int getSolutionCount() {
			return solutionCount;
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


