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
import choco.kernel.solver.search.measure.IMeasures;
import choco.kernel.solver.search.measure.ISearchMeasures;
import choco.kernel.solver.search.measure.MeasuresBean;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

/**
 * A class storing a state of the model
 */
public class Solution {

	public final static int NULL = Integer.MAX_VALUE;
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

	protected int objectiveIntValue = Integer.MAX_VALUE;

	protected double objectiveRealValue = Double.POSITIVE_INFINITY;

	protected final MeasuresBean measures;

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
		measures = new MeasuresBean();
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
		this.measures.setSolutionCount(solutionCount);
	}

	public final void recordIntValues() {
		final int n = solver.getNbIntVars();
		for (int i = 0; i < n; i++) {
			final IntVar v = solver.getIntVarQuick(i);
			intVarValues[i] = v.isInstantiated() ? v.getVal() : NULL;
		}
	}
	
	public final void recordSetValues() {
		final int n = solver.getNbSetVars();
		for (int i = 0; i < n; i++) {
			final SetVar v = solver.getSetVarQuick(i);
			setVarValues[i] = v.isInstantiated() ? v.getValue() : null;
		}
	}
	
	public final void recordRealValues() {
		final int n = solver.getNbRealVars();
		for (int i = 0; i < n; i++) {
			// Not always "instantiated" : for
			// instance, if the branching
			// does not contain the variable, the precision can not be
			// reached....
			final RealVar v = solver.getRealVarQuick(i);
			realVarValues[i] = v.getValue();
		}
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
		measures.setObjectiveIntValue(objectiveIntValue);
	}

	public final void recordRealObjective(double objectiveRealValue) {
		this.measures.setObjectiveRealValue(objectiveRealValue);
	}

	public final void recordSearchMeasures(ISearchMeasures measures){
		this.measures.setSearchMeasures(measures);
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

}


