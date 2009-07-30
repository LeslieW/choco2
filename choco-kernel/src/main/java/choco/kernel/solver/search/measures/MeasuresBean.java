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
package choco.kernel.solver.search.measures;


public class MeasuresBean extends AbstractMeasures {

	public int timeCount= -1;
	
	public int nodeCount = -1;
	
	public int backtrackCount = -1;
	
	public int iterationCount = -1;
	
	public int failCount= -1;
	
	
	
	public MeasuresBean() {
		super();
	}
	
	public final void copy(ISearchMeasures toCopy) {
		timeCount = toCopy.getTimeCount();
		nodeCount = toCopy.getNodeCount();
		backtrackCount = toCopy.getBackTrackCount();
		iterationCount = toCopy.getRestartCount();
		failCount = toCopy.getFailCount();
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
		return iterationCount;
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
		this.iterationCount = iterationCount;
	}

	public final void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	
	

}
