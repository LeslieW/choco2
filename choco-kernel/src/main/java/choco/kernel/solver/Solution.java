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

import java.util.LinkedList;
import java.util.List;

import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.measures.IMeasures;
import choco.kernel.solver.search.measures.ISearchMeasures;
import choco.kernel.solver.search.measures.MeasuresBean;
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

	protected int iterationCount;
	/**
	 * data storage for values of search variables
	 */
	protected int[] intVarValues;

	protected RealInterval[] realVarValues;

	protected int[][] setVarValues;

	protected int objectiveIntValue = Integer.MAX_VALUE;

	protected double objectiveRealValue = Double.POSITIVE_INFINITY;

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


	public final int getObjectiveValue() {
		return objectiveIntValue;
	}

	public final void recordSolutionCount(int solutionCount) {
		this.solutionCount = solutionCount;
	}

	public final void recordIterationCount(int restartCount) {
		this.iterationCount = restartCount;
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

	public final void recordIntObjective(int objectiveIntValue) {
		this.objectiveIntValue = objectiveIntValue;
	}

	public final void recordRealObjective(double objectiveRealValue) {
		this.objectiveRealValue = objectiveRealValue;
	}

	public final void recordSearchMeasures(ISearchMeasures measures){
		this.measures.copy(measures);
	}


	/**
	 * Accessor to the value of a variable in a solution
	 *
	 * @param varIndex the index of the variable among all variables of the model
	 * @return its value (whenever it is instantiated in the solution), or Integer.MAX_VALUE otherwise
	 */
	public final int getIntValue(int varIndex) {
		return intVarValues[varIndex];
	}

	public final int[] getSetValue(int varIndex) {
		return setVarValues[varIndex];
	}

	public final RealInterval getRealValue(int varIndex) {
		return realVarValues[varIndex];
	}


	final class SolutionMeasures extends MeasuresBean implements IMeasures {

		public SolutionMeasures() {
			super();
		}

		@Override
		public boolean isObjectiveOptimal() {
			return false;
		}


		@Override
		public Number getObjectiveValue() {
			return ( 
					objectiveIntValue == Integer.MAX_VALUE ? 
							( objectiveRealValue == Double.POSITIVE_INFINITY? (Number) null: Double.valueOf(objectiveRealValue) ) :
								Integer.valueOf(objectiveIntValue) 
			);
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





}


