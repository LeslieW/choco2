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

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;
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

	protected int objectiveIntValue = Integer.MAX_VALUE;

	protected double objectiveRealValue = Double.POSITIVE_INFINITY;

	protected final SolutionMeasures measures;

	protected int[] limitValues;

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
		limitValues = new int[solver.getSearchStrategy().getLimitManager().getNbLimits()];
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
		if(solver.getSearchStrategy().getLimitManager().getNbLimits() >  limitValues.length) {
			realVarValues = new RealInterval[solver.getSearchStrategy().getLimitManager().getNbLimits()];
		}
	}


	public final IMeasures getMeasures() {
		return measures;
	}

	public final int getLimitValue(Limit limit) {
		return AbstractGlobalSearchLimit.getLimitValue(solutionLimits, limit);
	}

	public final int getObjectiveValue() {
		return objectiveIntValue;
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

	public final void recordIntObjective(int objectiveIntValue) {
		this.objectiveIntValue = objectiveIntValue;
	}

	public final void recordRealObjective(double objectiveRealValue) {
		this.objectiveRealValue = objectiveRealValue;
	}

	public final void recordLimit(int idx, int value){
		limitValues[idx] = value;
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
		public int getLimitValue(Limit type) {
			final int idx = solver.getSearchStrategy().getLimitManager().getLimitIndex(type);
			return idx < 0 ? -1 : limitValues[idx];
		}

		@Override
		public int getIterationCount() {
			System.err.println("not yet implemented");
			return -1;
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


