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

import java.util.Arrays;
import java.util.Comparator;

import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.ECT;
import static choco.kernel.common.util.comparator.TaskComparators.*;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaLambdaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaOmegaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.scheduling.IRMakespan;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.3</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractDisjRules implements IDisjRules {

	protected final IRTask[] rtasks;
	
	protected final UpdateManager updateManager;

	protected final IRMakespan makespan;

	private TreeMode state;

	public AbstractDisjRules(IRTask[] rtasks, IRMakespan makespan, boolean enableRemove) {
		super();
		this.rtasks = Arrays.copyOf(rtasks, rtasks.length);
		this.makespan = makespan;
		this.updateManager = new UpdateManager(this, rtasks.length, enableRemove);
		fireDomainChanged();
	}


	protected final ITask[] getTaskArray() {
		final ITask[] tasks = new ITask[rtasks.length];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = rtasks[i].getTaskVar();
		}
		return tasks;
	}


	@Override
	public void initialize() {
		updateManager.clear();
		fireDomainChanged();

	}


	@Override
	public void fireDomainChanged() {
		state = null;
	}

	protected void sortRTasks(Comparator<IRTask> cmp) {
		Arrays.sort(rtasks, cmp);
	}

	protected final static <E> void sortQueue(IBipartiteQueue<E> queue, Comparator<E> cmp) {
		queue.reset();
		queue.sort(cmp);
	}


	protected final void setupMasterTree(IVilimTree tree,TreeMode mode) {
		if(state == mode) {
			tree.reset();
		}else {
			state = mode;
			tree.setMode(state);
		}
	}

	protected final void setMakespanLB(final IThetaTree tree) throws ContradictionException {
		makespan.updateInf(tree.getTime());
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


final class UpdateManager {

	public final IDisjRules rules;

	private final IRTask[] updateL;

	private int updateCount;

	private final IRTask[] removeL;

	private int removeCount;

	protected UpdateManager(IDisjRules rules, int capacity, boolean enableRemove) {
		super();
		this.rules = rules;
		removeL = enableRemove ? new IRTask[capacity] : null;
		updateL = new IRTask[capacity];
	}

	public void clear() {
		Arrays.fill(updateL, null);
		updateCount=0;
		if(removeL != null) {
			Arrays.fill(removeL, null);
			removeCount = 0;
		}
	}

	public void storeUpdate(IRTask t, int value) {
		t.storeValue(value);
		updateL[updateCount++]=t;
	}

	public void storeRemoval(IRTask t) throws ContradictionException {
		t.remove();
		removeL[removeCount++]=t;
	}

	public void storeLambdaRemoval(IThetaLambdaTree tree) throws ContradictionException {
		final IRTask t = (IRTask) tree.getResponsibleTask();
		storeRemoval(t);
		tree.removeFromLambda(t.getTaskVar());
	}
	
	public void storeLambdaRemoval(IRTask t, IThetaLambdaTree tree) throws ContradictionException {
		storeRemoval(t);
		tree.removeFromLambda(t.getTaskVar());
	}

	public void storeOmegaRemoval(IRTask t, IThetaOmegaTree tree) throws ContradictionException {
		storeRemoval(t);
		tree.removeFromOmega(t);
	}



	public final void fireRemovals() {
		if(removeCount > 0) {
			for (int i = 0; i < removeCount; i++) {
				removeL[i].fireRemoval();
			}
			removeCount=0;
			rules.fireDomainChanged();
		}
	}


	public boolean updateEST() throws ContradictionException {
		if(updateCount > 0) {
			boolean noFixPoint=false;
			for (int i = 0; i < updateCount; i++) {
				noFixPoint |= updateL[i].updateEST();
			}
			updateCount=0;
			if(noFixPoint) rules.fireDomainChanged();
			return noFixPoint;
		}else return false;
	}

	public boolean fireAndUpdateEST() throws ContradictionException {
		fireRemovals();
		return updateEST();
	}

	public boolean updateLCT() throws ContradictionException {
		if(updateCount > 0) {
			boolean noFixPoint=false;
			for (int i = 0; i < updateCount; i++) {
				noFixPoint |= updateL[i].updateLCT();
			}
			updateCount=0;
			if(noFixPoint) rules.fireDomainChanged();
			return noFixPoint;
		}else return false;
	}

	public boolean fireAndUpdateLCT() throws ContradictionException {
		fireRemovals();
		return updateLCT();
	}

}


