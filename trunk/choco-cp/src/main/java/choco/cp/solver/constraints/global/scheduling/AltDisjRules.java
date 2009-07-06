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

import choco.cp.solver.constraints.global.scheduling.trees.AltDisjTreeTL;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.ECT;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.LST;
import static choco.kernel.common.util.comparator.TaskComparators.*;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntProcedure;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.*;

/**
 * @author Arnaud Malapert</br> 
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public final class AltDisjRules extends AbstractDisjRules implements Iterable<IRTask>, IStateIntProcedure {

	private final IStateInt size;

	private final List<IRTask> removals = new LinkedList<IRTask>();

	private AltBipartiteQueue<IRTask> rqueue;

	protected AltDisjTreeTL altDisjTreeTL;

	public AltDisjRules(final IEnvironment env, final IRTask[] rtasks) {
		super(rtasks);
		size = env.makeIntProcedure(this, rtasks.length);
		rqueue = new AltBipartiteQueue<IRTask>(rtasks);
		altDisjTreeTL = new AltDisjTreeTL(Arrays.asList(getTaskArray()));
	}

	/**
	 * call during backtrack if necessary
	 */
	@Override
	public void apply(final int oldVal, final int newVal) {
		for (int i = oldVal; i < newVal; i++) {
			altDisjTreeTL.insert(rtasks[i].getTaskVar());
		}

	}

	protected void applyRemovals() throws ContradictionException {
		for (IRTask t : removals) {
			t.remove();
			this.remove(t);
		}

	}
	private void setupListsAndTreeTL(final Comparator<IRTask> taskComp,final Comparator<IRTask> queueComp, final TreeMode mode) {
		this.clear();
		Arrays.sort(rtasks, 0, size.get(), taskComp);
		rqueue.sort(queueComp);
		this.setup(altDisjTreeTL, mode);
	}

	@Override
	protected void clear() {
		super.clear();
		removals.clear();
		rqueue.reset();
	}


	@Override
	public boolean isActive() {
		return size.get()>0;
	}

	private void makeRemovalSwap(final IRTask[] rtasks, final IRTask rtask) {
		int index = -1;
		for (int i = 0; i < size.get(); i++) {
			if( rtasks[i] == rtask) {
				index = i;
				break;
			}
		}
		if(index== -1) {throw new NoSuchElementException("cant remove element.");}
		else {
			final int newIndex = size.get()-1;
			final IRTask tmp = rtasks[index];
			rtasks[index] = rtasks[newIndex];
			rtasks[newIndex] = tmp;
		}
	}

	private void insertInTree(final IRTask rtask) {
		if(rtask.isOptional()) {
			altDisjTreeTL.insertInLambda(rtask);
		}else if(rtask.isRegular()) {
			altDisjTreeTL.insertInTheta(rtask);
		}
	}

	private void setAsRemoval() {
		final IRTask j = (IRTask) altDisjTreeTL.getResponsibleTask();
		removals.add(j);
		altDisjTreeTL.removeFromLambda(j.getTaskVar());
	}

	public void remove(final IRTask rtask) {
		makeRemovalSwap(rtasks, rtask);
		makeRemovalSwap(rqueue.elementData, rtask);
		altDisjTreeTL.remove(rtask.getTaskVar());
		size.add(-1);
	}

	@Override
	public boolean detectablePrecedenceEST() throws ContradictionException {
		setupListsAndTreeTL(makeREarliestCompletionTimeCmp(),makeRLatestStartingTimeCmp(), ECT);
		for (IRTask rti : this) {
			final ITask i = rti.getTaskVar();
			while( !rqueue.isEmpty() && i.getECT()>rqueue.peek().getTaskVar().getLST()) {
				insertInTree(rqueue.poll());
			}
			if(rti.isRegular()) {
				final boolean rm=altDisjTreeTL.removeFromTheta(i);
				addUpdate(rti,altDisjTreeTL.getTime());
				//an additional check to avoid to find a null resp. optional activity
				if(altDisjTreeTL.getTime() > i.getLST()) {rti.fail();}
				while( altDisjTreeTL.getGrayTime() > i.getLST()) {
					setAsRemoval();
				}
				if(rm) {altDisjTreeTL.insertInTheta(i);}
			}	
		}
		setMakespanLB(altDisjTreeTL);
		applyRemovals();
		return updateEST();
	}

	@Override
	public boolean detectablePrecedenceLCT() throws ContradictionException {
		setupListsAndTreeTL(makeReverseRLatestCompletionTimeCmp(), makeReverseREarliestCompletionTimeCmp(), LST);
		for (IRTask rti : this) {
			final ITask i = rti.getTaskVar();
			while(!rqueue.isEmpty() && i.getLCT()<= rqueue.peek().getTaskVar().getECT()) {
				insertInTree(rqueue.poll());
			}
			if(rti.isRegular()) {
				final boolean rm=altDisjTreeTL.removeFromTheta(i);
				addUpdate(rti,altDisjTreeTL.getTime());
				if(altDisjTreeTL.getTime() < i.getECT()) { rti.fail();}
				while( altDisjTreeTL.getGrayTime() < i.getECT()) {
					setAsRemoval();
				}
				//we have to be sure that i was active in disjTreeT
				if(rm) {altDisjTreeTL.insertInTheta(i);}
			}
		}
		applyRemovals();
		return updateLCT();
	}

	@Override
	public boolean edgeFindingEST() throws ContradictionException {
		clear();
		altDisjTreeTL.initializeEdgeFinding(ECT, this);
		return edgeFindingEST(altDisjTreeTL, rqueue);
	}

	@Override
	public boolean edgeFindingLCT() throws ContradictionException {
		clear();
		altDisjTreeTL.initializeEdgeFinding(LST, this);
		return edgeFindingLCT(altDisjTreeTL, rqueue);
	}

	@Override
	public boolean notFirst() throws ContradictionException {
		setupListsAndTreeTL(makeReverseREarliestCompletionTimeCmp(), makeReverseREarliestCompletionTimeCmp(), LST);
		ITask j=null, ja = null;
		for (IRTask rti : this) {
			final ITask i = rti.getTaskVar();
			while(!rqueue.isEmpty()  && i.getEST() < rqueue.peek().getTaskVar().getECT()) {
				final IRTask tmp = rqueue.poll();
				ja = tmp.getTaskVar();
				if(tmp.isRegular()) { j = ja;}
				insertInTree(tmp);
			}
			if(rti.isRegular()) {
				final boolean rm= altDisjTreeTL.removeFromTheta(i);
				if( altDisjTreeTL.getTime()< i.getECT()) {
					addUpdate(rti,j.getECT());
					if(i.getLST() < j.getECT()) { rti.fail();}
					if(i.getLST() < ja.getECT()) {
						while( altDisjTreeTL.getGrayTime() > i.getECT()) {
							setAsRemoval();
						}
					}
				}
				if(rm) {altDisjTreeTL.insertInTheta(i);}

			}
		}
		applyRemovals();
		return updateEST();
	}

	@Override
	public boolean notLast() throws ContradictionException {
		setupListsAndTreeTL(makeRLatestCompletionTimeCmp(),makeRLatestStartingTimeCmp(), ECT);
		ITask j=null, ja=null;
		for (IRTask rti : this) {
			final ITask i = rti.getTaskVar();
			//update tree
			while(!rqueue.isEmpty() && i.getLCT()> rqueue.peek().getTaskVar().getLST()) {
				final IRTask tmp = rqueue.poll();
				ja = tmp.getTaskVar();				
				if(tmp.isRegular()) {j = ja;}
				insertInTree(tmp);
			}
			if(rti.isRegular()) {
				//compute pruning
				altDisjTreeTL.removeFromTheta(i);
				if(altDisjTreeTL.getTime()>i.getLST()) {
					addUpdate(rti,j.getLST());
					if(j.getLST() < i.getECT()) { rti.fail();}
					if(ja.getLST()< i.getECT()) {
						while( altDisjTreeTL.getGrayTime() > i.getLST()) {
							setAsRemoval();
						}
					}
				}
				altDisjTreeTL.insertInTheta(i);
			}
		}
		setMakespanLB(altDisjTreeTL);
		applyRemovals();
		return updateLCT();
	}

	@Override
	public boolean overloadChecking() {
		this.clear();
		Arrays.sort(rtasks, 0, size.get(), makeRLatestCompletionTimeCmp());
		this.setup(altDisjTreeTL, ECT);
		for(IRTask t : this) {
			final ITask i = t.getTaskVar();
			insertInTree(t);
			if(altDisjTreeTL.getTime()> i.getLCT()) {return true;}
			while( altDisjTreeTL.getGrayTime() >  i.getLCT()) {
				setAsRemoval();
			}
		}
		setMakespanLB(altDisjTreeTL);
		return false;
	}




	@Override
	public Iterator<IRTask> iterator() {
		return new Itr();
	}



	final class Itr implements Iterator<IRTask> {

		private int level = 0;

		@Override
		public boolean hasNext() {
			return level < size.get();
		}

		@Override
		public IRTask next() {
			return rtasks[level++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("not available");
		}


	}

	final class AltBipartiteQueue<E> implements IBipartiteQueue<E> {

		public final E[] elementData;

		private int level;

		public AltBipartiteQueue(final E[] elementData) {
			super();
			this.elementData = Arrays.copyOf(elementData, elementData.length);
			this.reset();
		}

		public void reset() {
			level =0;
		}

		public boolean isEmpty() {
			return level == size.get();
		}

		public E poll() {
			return elementData[level++];
		}

		public E peek(){
			return elementData[level];
		}

		public void sort(final Comparator<? super E> cmp) {
			Arrays.sort(elementData, level,size.get(),cmp);
		}
	}
}
