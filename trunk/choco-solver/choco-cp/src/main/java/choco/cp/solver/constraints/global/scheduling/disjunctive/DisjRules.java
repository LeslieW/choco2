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
package choco.cp.solver.constraints.global.scheduling.disjunctive;

import choco.cp.solver.constraints.global.scheduling.trees.AltDisjTreeTL;
import choco.cp.solver.constraints.global.scheduling.trees.DisjTreeT;
import choco.cp.solver.constraints.global.scheduling.trees.DisjTreeTL;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaLambdaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IThetaTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.ECT;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.LST;
import static choco.kernel.common.util.comparator.TaskComparators.*;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.scheduling.IRMakespan;
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
	public DisjRules(IRTask[] rtasks, IRMakespan makespan) {
		super(rtasks, makespan, false);
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
		sortQueue(queue, queueComp);
		sortRTasks(taskComp);
		setupMasterTree(disjTreeT, mode);
	}




	//****************************************************************//
	//********* Overload checking *************************************//
	//****************************************************************//


	/**
	 * Overload checking rule.
	 *@return <code>true</code> if the resource is overloaded.
	 * @throws ContradictionException 
	 */
	public void overloadChecking() throws ContradictionException {
		sortRTasks(makeRLatestCompletionTimeCmp()); 
		setupMasterTree(disjTreeT, ECT);
		for(IRTask t : rtasks) {
			final ITask i = t.getTaskVar();
			disjTreeT.insertInTheta(i);
			if(disjTreeT.getTime()> i.getLCT()) {t.fail();}
		}
		setMakespanLB(disjTreeT);
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
				updateManager.storeUpdate(rti,j.getECT());
			}
			if(rm) {disjTreeT.insertInTheta(i);}

		}
		return updateManager.updateEST();
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
				updateManager.storeUpdate(rti,j.getLST());
			}
			disjTreeT.insertInTheta(i);
		}
		setMakespanLB(disjTreeT);
		return updateManager.updateLCT();
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
			updateManager.storeUpdate(rti,disjTreeT.getTime());
			if(rm) {disjTreeT.insertInTheta(i);}
		}
		setMakespanLB(disjTreeT);
		return updateManager.updateEST();
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
			updateManager.storeUpdate(rti,disjTreeT.getTime());
			//be sure that i was active in disjTreeT
			if(rm) {disjTreeT.insertInTheta(i);}
		}
		return updateManager.updateLCT();

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
		sortQueue(rqueue, makeReverseRLatestCompletionTimeCmp());
		this.disjTreeTL.setMode(ECT);
		setMakespanLB(disjTreeTL);
		IRTask rtj=rqueue.peek();
		ITask j= rtj.getTaskVar();
		if(disjTreeTL.getTime()>j.getLCT()) {rtj.fail();}//erreur pseudo-code papier sinon on ne traite pas la tete de la queue
		do {
			rqueue.poll();
			if(rtj.isRegular()) {
				disjTreeTL.removeFromThetaAndInsertInLambda(rtj);
				if(!rqueue.isEmpty()) {
					rtj=rqueue.peek();
					j= rtj.getTaskVar();
				}
				else {break;}
				if(disjTreeTL.getTime()>j.getLCT()) {rtj.fail();}
				while(disjTreeTL.getGrayTime()>j.getLCT()) {
					final IRTask rti= (IRTask) disjTreeTL.getResponsibleTask();
					final ITask i= rti.getTaskVar();
					if(disjTreeTL.getTime()>i.getEST()) {
						updateManager.storeUpdate(rti,disjTreeTL.getTime());
					}
					disjTreeTL.removeFromLambda(i);
				}
			}
		} while (!rqueue.isEmpty());
		return updateManager.updateEST();
	}




	/**
	 * symmetric EdgeFinding rule.
	 * @throws ContradictionException
	 *
	 */
	public boolean edgeFindingLCT() throws ContradictionException  {
		sortQueue(rqueue, makeREarliestStartingTimeCmp());
		disjTreeTL.setMode(LST);
		IRTask rtj=rqueue.peek();
		ITask j= rtj.getTaskVar();
		if(disjTreeTL.getTime()<j.getEST()) {rtj.fail();}
		do {
			rqueue.poll();
			if(rtj.isRegular()) {
				disjTreeTL.removeFromThetaAndInsertInLambda(rtj);
				if(!rqueue.isEmpty()) {
					rtj = rqueue.peek();	
					j= rtj.getTaskVar();
				}
				else {break;}
				if(disjTreeTL.getTime()<j.getEST()) {rtj.fail();}
				while(disjTreeTL.getGrayTime() <j.getEST()) {
					final IRTask rti= (IRTask) disjTreeTL.getResponsibleTask();
					final ITask i= rti.getTaskVar();
					updateManager.storeUpdate(rti,disjTreeTL.getTime());
					disjTreeTL.removeFromLambda(i);
				}
			}
		} while (!rqueue.isEmpty());
		return updateManager.updateLCT();
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

