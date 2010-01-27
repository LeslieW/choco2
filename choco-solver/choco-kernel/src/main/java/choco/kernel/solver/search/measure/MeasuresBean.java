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
package choco.kernel.solver.search.measure;


public class MeasuresBean implements IMeasures {

	public int solutionCount;
	
	public int objectiveIntValue = Integer.MAX_VALUE;

	public double objectiveRealValue = Double.POSITIVE_INFINITY;
	
	public boolean objectiveOptimal; 
	
	public int timeCount;
	
	public int nodeCount ;
	
	public int backtrackCount;
	
	public int restartCount;
	
	public int failCount;
	
	public MeasuresBean() {
		super();
	}
	
	public final void reset() {
		timeCount = 0;
		nodeCount = 0;
		backtrackCount = 0; 
		restartCount = 0;
		failCount = 0;
	}
	
	public final void setSearchMeasures(ISearchMeasures toCopy) {
		timeCount = toCopy.getTimeCount();
		nodeCount = toCopy.getNodeCount();
		backtrackCount = toCopy.getBackTrackCount();
		restartCount = toCopy.getRestartCount();
		failCount = toCopy.getFailCount();
	}
	

	
	@Override
	public boolean existsSolution() {
		return solutionCount > 0;
	}

	@Override
	public int getSolutionCount() {
		return solutionCount;
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
	public boolean isObjectiveOptimal() {
		return objectiveOptimal;
	}


	@Override
	public final int getBackTrackCount() {
		return backtrackCount;
	}

	@Override
	public final int getFailCount() {
		return failCount;
	}

	@Override
	public final int getNodeCount() {
		return nodeCount;
	}

	@Override
	public final int getTimeCount() {
		return timeCount;
	}

	@Override
	public final int getRestartCount() {
		return restartCount;
	}

	
	public final void setSolutionCount(int solutionCount) {
		this.solutionCount = solutionCount;
	}

	public final void setObjectiveIntValue(int objectiveIntValue) {
		this.objectiveIntValue = objectiveIntValue;
	}

	public final void setObjectiveRealValue(double objectiveRealValue) {
		this.objectiveRealValue = objectiveRealValue;
	}

	public final void setObjectiveOptimal(boolean objectiveOptimal) {
		this.objectiveOptimal = objectiveOptimal;
	}

	public final void setRestartCount(int restartCount) {
		this.restartCount = restartCount;
	}

	public final void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}

	public final void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public final void setBacktrackCount(int backtrackCount) {
		this.backtrackCount = backtrackCount;
	}

	public final void setIterationCount(int iterationCount) {
		this.restartCount = iterationCount;
	}

	public final void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	
	

}
