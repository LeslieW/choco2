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
package choco.cp.solver.constraints.global.scheduling;

import choco.cp.solver.constraints.global.scheduling.trees.IThetaLambdaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import static choco.kernel.common.util.comparator.TaskComparators.makeREarliestStartingTimeCmp;
import static choco.kernel.common.util.comparator.TaskComparators.makeReverseRLatestCompletionTimeCmp;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.3</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractDisjRules implements IDisjRules {

	protected final IRTask[] rtasks;

	protected final List<Update> updateL;

	protected int makespanLB;

	protected TreeMode state;

	public AbstractDisjRules(IRTask[] rtasks) {
		super();
		this.rtasks = Arrays.copyOf(rtasks, rtasks.length);
		this.updateL = new LinkedList<Update>();
		fireDomainChanged();
	}


	protected final ITask[] getTaskArray() {
		final ITask[] tasks = new ITask[rtasks.length];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = rtasks[i].getTaskVar();
		}
		return tasks;
	}

	protected void clear() {
		updateL.clear();
	}

	@Override
	public void fireDomainChanged() {
		state = null;
		makespanLB = Integer.MIN_VALUE;
	}

	protected final void setup(IVilimTree tree,TreeMode mode) {
		if(state == mode) {
			tree.reset();
		}else {
			state = mode;
			tree.setMode(state);
		}
	}

	@Override
	public final int getMakespanLB() {
		return makespanLB;
	}

	protected final void setMakespanLB(final IThetaTree tree) {
		this.makespanLB = Math.max(this.makespanLB, tree.getTime());
	}
	
	protected final void addUpdate(final IRTask task, final int value) {
		updateL.add(new Update(task, value));		
	}

	/**
	 * Update LCT.
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final boolean updateLCT() throws ContradictionException {
		boolean noFixPoint=false;
		for (Update p : updateL) {
			noFixPoint|= p.rtask.updateLCT(p.value);
		}
		return noFixPoint;
	}


	/**
	 * Update EST.
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final boolean updateEST() throws ContradictionException {
		boolean noFixPoint=false;
		for (Update p : updateL) {
			noFixPoint|= p.rtask.updateEST(p.value);
		}
		return noFixPoint;
	}

	@Override
	public final boolean detectablePrecedence() throws ContradictionException {
		return detectablePrecedenceEST() | detectablePrecedenceLCT() ;
	}

	@Override
	public final boolean edgeFinding() throws ContradictionException {
		return edgeFindingEST() | edgeFindingLCT();
	}

	@Override
	public final boolean notFirstNotLast() throws ContradictionException {
		return notFirst() | notFirstNotLast();
	}

}

interface IBipartiteQueue<E> {

	void reset();

	boolean isEmpty();

	E poll();

	E peek();

	void sort(Comparator<? super E> cmp);
}



final class Update {

	protected final IRTask rtask;

	protected final int value;

	/**
	 * @param task
	 * @param value
	 */
	public Update(final IRTask rtask, final int value) {
		super();
		this.rtask = rtask;
		this.value = value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "task : "+rtask+" ; value : "+value;
	}
}