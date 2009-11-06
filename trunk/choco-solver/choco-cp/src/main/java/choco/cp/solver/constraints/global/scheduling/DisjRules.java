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

import choco.cp.solver.constraints.global.scheduling.trees.DisjTreeT;
import choco.cp.solver.constraints.global.scheduling.trees.DisjTreeTL;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaLambdaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.ECT;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.LST;
import static choco.kernel.common.util.comparator.TaskComparators.*;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.Arrays;
import static java.util.Arrays.sort;
import java.util.Comparator;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public final class DisjRules extends AbstractDisjRules {

	protected final IBipartiteQueue<IRTask> rqueue;

	protected final IBipartiteQueue<ITask> queue;

	/** The data structure used for Not-First/Not Last, overload checking and detectable precedence rules. */
	protected final IThetaTree disjTreeT;

	/** The data structure used for EdgeFinding rule. */
	protected final IThetaLambdaTree disjTreeTL;


	/**
	 * Instantiates a new disjunctive.
	 * @param tasks the vars the tasks involved in the constraint
	 * @param constraint their processing times
	 */
	public DisjRules(IRTask[] rtasks) {
		super(rtasks);
		ITask[] tasks = getTaskArray();
		this.rqueue = new BipartiteQueue<IRTask>(rtasks);
		this.queue = new BipartiteQueue<ITask>(tasks);
		this.disjTreeT=new DisjTreeT(Arrays.asList(tasks));
		this.disjTreeTL=new DisjTreeTL(Arrays.asList(tasks));
	}



	@Override
	public final boolean isActive() {
		return true;
	}



	@Override
	public void remove(IRTask rtask) {
		throw new UnsupportedOperationException("The resource is not alternative");
	}


	private void setupListsAndTreeT(final Comparator<IRTask> taskComp,final Comparator<ITask> queueComp, TreeMode mode) {
		clear();
		this.queue.reset();
		sort(rtasks,taskComp);
		this.queue.sort(queueComp);
		this.setup(disjTreeT, mode);
	}




	//****************************************************************//
	//********* Overload checking *************************************//
	//****************************************************************//


	/**
	 * Overload checking rule.
	 *@return <code>true</code> if the resource is overloaded.
	 */
	public boolean overloadChecking() {
		sort(rtasks, makeRLatestCompletionTimeCmp());
		this.setup(disjTreeT, ECT);
		for(IRTask t : rtasks) {
			final ITask i = t.getTaskVar();
			disjTreeT.insertInTheta(i);
			if(disjTreeT.getTime()> i.getLCT()) {return true;}
		}
		setMakespanLB(disjTreeT);
		return false;
	}



	//****************************************************************//
	//********* NotFirst/NotLast *************************************//
	//****************************************************************//


	//TODO optimisation papier
	public boolean notFirst() throws ContradictionException {
		setupListsAndTreeT(makeReverseREarliestCompletionTimeCmp(), makeReverseEarliestCompletionTimeCmp(), LST);
		ITask j=null;
		for (IRTask rti : rtasks) {
			final ITask i = rti.getTaskVar();
			while(!queue.isEmpty()  && i.getEST() <queue.peek().getECT()) {
				j=queue.poll();
				disjTreeT.insertInTheta(j);
			}
			final boolean rm=disjTreeT.removeFromTheta(i);
			if(disjTreeT.getTime()<i.getECT()) {
				addUpdate(rti,j.getECT());
			}
			if(rm) {disjTreeT.insertInTheta(i);}

		}
		return updateEST();
	}


	/**
	 * NotLast rule.
	 * @throws ContradictionException
	 *
	 */
	public boolean notLast() throws ContradictionException {
		setupListsAndTreeT(makeRLatestCompletionTimeCmp(),makeLatestStartingTimeCmp(), ECT);
		ITask j=null;
		for (IRTask rti : rtasks) {
			final ITask i = rti.getTaskVar();
			//update tree
			while(!queue.isEmpty() && i.getLCT()> queue.peek().getLST()) {
				j=queue.poll();
				disjTreeT.insertInTheta(j);
			}
			//compute pruning
			disjTreeT.removeFromTheta(i);
			if(disjTreeT.getTime()>i.getLST()) {
				addUpdate(rti,j.getLST());
			}
			disjTreeT.insertInTheta(i);
		}
		setMakespanLB(disjTreeT);
		return updateLCT();
	}

	//	//****************************************************************//
	//	//********* detectable Precedence*********************************//
	//	//****************************************************************//
	/**
	 * DetectablePrecedence rule.
	 * @throws ContradictionException
	 *
	 */
	public boolean detectablePrecedenceEST() throws ContradictionException {
		setupListsAndTreeT(makeREarliestCompletionTimeCmp(),makeLatestStartingTimeCmp(), ECT);
		for (IRTask rti : rtasks) {
			final ITask i = rti.getTaskVar();
			while( !queue.isEmpty() && i.getECT()>queue.peek().getLST()) {
				disjTreeT.insertInTheta(queue.poll());
			}
			final boolean rm=disjTreeT.removeFromTheta(i);
			addUpdate(rti,disjTreeT.getTime());
			if(rm) {disjTreeT.insertInTheta(i);}
		}
		setMakespanLB(disjTreeT);
		return updateEST();
	}

	/**
	 * symmetric DetectablePrecedence rule.
	 * @throws ContradictionException
	 *
	 */
	public boolean detectablePrecedenceLCT() throws ContradictionException {
		setupListsAndTreeT(makeReverseRLatestCompletionTimeCmp(), makeReverseEarliestCompletionTimeCmp(), LST);
		for (IRTask rti : rtasks) {
			final ITask i = rti.getTaskVar();
			while(!queue.isEmpty() && i.getLCT()<=queue.peek().getECT()) {
				disjTreeT.insertInTheta(queue.poll());
			}
			final boolean rm=disjTreeT.removeFromTheta(i);
			addUpdate(rti,disjTreeT.getTime());
			//we have to be sure that i was active in disjTreeT
			if(rm) {disjTreeT.insertInTheta(i);}
		}
		return updateLCT();

	}

	//	//****************************************************************//
	//	//********* Edge Finding *****************************************//
	//	//****************************************************************//



	/**
	 * EdgeFinding rule.
	 * @throws ContradictionException
	 *
	 */
	public boolean edgeFindingEST() throws ContradictionException {
		clear();
		rqueue.reset();
		this.disjTreeTL.setMode(ECT);
		return edgeFindingEST(disjTreeTL, rqueue);
		
	}




	/**
	 * symmetric EdgeFinding rule.
	 * @throws ContradictionException
	 *
	 */
	public boolean edgeFindingLCT() throws ContradictionException  {
		clear();
		rqueue.reset();
		disjTreeTL.setMode(LST);
		return edgeFindingLCT(disjTreeTL, rqueue);
	}

}


final class BipartiteQueue<E> implements IBipartiteQueue<E> {

	private final E[] elementData;

	private int level;

	public BipartiteQueue(E[] elementData) {
		super();
		this.elementData = Arrays.copyOf(elementData, elementData.length);
		this.reset();
	}

	public void reset() {
		level =0;
	}

	public boolean isEmpty() {
		return level == elementData.length;
	}

	public E poll() {
		return elementData[level++];
	}

	public E peek(){
		return elementData[level];
	}

	public void sort(Comparator<? super E> cmp) {
		Arrays.sort(elementData, level,elementData.length ,cmp);
	}

}

