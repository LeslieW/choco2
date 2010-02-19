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
import choco.cp.solver.constraints.global.scheduling.trees.AltDisjTreeTLTO;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.ECT;
import static choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode.LST;
import static choco.kernel.common.util.comparator.TaskComparators.*;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntProcedure;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
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

	////*****************************/////
	protected AltDisjTreeTLTO altDisjTreeTLTO;
	private int[] ectAfter; //Used to hold the total sum of ECT of Theta tasks after task t. 
	private int[] ectBefore; // holds the total sum of ECT of Theta tasks before task t.
	private int[] pAfter; // holds the total sum of processing time of Theta tasks after task t.
	private int[] ectWith; //holds the ECT(Theta Union task t).
	////*****************************/////

	//FIXME should try to avoid insertions/deletions in trees when the rules are off
	public AltDisjRules(final IRTask[] rtasks, IEnvironment environment) {
		super(rtasks);
		size = environment.makeIntProcedure(this, rtasks.length);
		rqueue = new AltBipartiteQueue<IRTask>(rtasks);
		final ITask[] tasks = getTaskArray();
		altDisjTreeTL = new AltDisjTreeTL(Arrays.asList(tasks));
		///*****************///
		altDisjTreeTLTO = new AltDisjTreeTLTO(Arrays.asList(tasks));
		///*****************///
	}


	/**
	 * call during backtrack if necessary
	 */
	@Override
	public void apply(final int oldVal, final int newVal) {
		for (int i = oldVal; i < newVal; i++) {
			altDisjTreeTL.insert(rtasks[i].getTaskVar());
			//Should insert tasks to TLTO tree as well
			altDisjTreeTLTO.insert(rtasks[i].getTaskVar());
		}
	}

	protected void applyRemovals() throws ContradictionException {
		for (IRTask t : removals) {
			assert(t.isEliminated()); //assertions are activated by junit
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
		int i = 0;
		while( i < size.get()) {
			if( rtasks[i] == rtask) {
				final int newIndex = size.get()-1;
				final IRTask tmp = rtasks[i];
				rtasks[i] = rtasks[newIndex];
				rtasks[newIndex] = tmp;
				return;
			}
			i++;
		}
		throw new NoSuchElementException("cant remove task from the constraint .");
	}

	private void insertInTree(final IRTask rtask) {
		if(rtask.isOptional()) {
			altDisjTreeTL.insertInLambda(rtask);
		}else if(rtask.isRegular()) {
			altDisjTreeTL.insertInTheta(rtask);
		}
	}

	private void setAsRemoval() throws ContradictionException{
		final IRTask j = (IRTask) altDisjTreeTL.getResponsibleTask();
		//Disable optional task
		j.remove();
		removals.add(j);
		altDisjTreeTL.removeFromLambda(j.getTaskVar());
	}

	/**
	 * Used by Edge Finding algorithm
	 * @param respTask: task that will be removed
	 * @param omega: specifies whether the task should be removed from Omega, or Lambda
	 */
	private void setAsRemoval(IRTask respTask, boolean omega) throws ContradictionException{
		//Disable optional task.
		respTask.remove();
		removals.add(respTask);
		if(!omega)
			altDisjTreeTLTO.removeFromLambda(respTask.getTaskVar());
		else
			altDisjTreeTLTO.removeFromOmega(respTask);
	}

	@Override
	public void remove(IRTask rtask) {
		makeRemovalSwap(rtasks, rtask);
		makeRemovalSwap(rqueue.elementData, rtask);
		//Remove disabled task from TL, and TLTO trees.
		altDisjTreeTL.remove(rtask.getTaskVar());
		altDisjTreeTLTO.remove(rtask.getTaskVar());
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
	public boolean overloadChecking(){
		this.clear();
		Arrays.sort(rtasks, 0, size.get(), makeRLatestCompletionTimeCmp());
		this.setup(altDisjTreeTL, ECT);
		for(IRTask t : this) {
			final ITask i = t.getTaskVar();
			insertInTree(t);
			if(altDisjTreeTL.getTime()> i.getLCT()) {return true;}
			while( altDisjTreeTL.getGrayTime() >  i.getLCT()) {
				try {
					setAsRemoval();
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
		}
		setMakespanLB(altDisjTreeTL);
		return false;
	}

	/**
	 * Edge Finding supporting optional activities reasoning by Sebastian Kuhnert
	 * @return true if filtering takes place
	 */
	@Override
	public boolean edgeFindingEST()throws ContradictionException{
		//rqueue should be sorted in descending order of LCT
		//While the Tasks list should be sorted in ascending order of EST 
		setupEF(ECT, makeReverseRLatestCompletionTimeCmp());
		setMakespanLB(altDisjTreeTLTO);
		IRTask rtj=rqueue.peek();
		ITask j=rtj.getTaskVar();
		if(rtj.isRegular())
			if(altDisjTreeTLTO.getTime() > j.getLCT()) {rtj.fail();}
		do{
			//Remove Task j from either Theta, or Omega, and insert it in Lambda
			if(rtj.isOptional()){
				altDisjTreeTLTO.removeFromOmegaAndInsertInLambda(rtj);
			}
			else if(rtj.isRegular()){
				altDisjTreeTLTO.removeFromThetaAndInsertInLambda(rtj);
			}
			rqueue.poll();
			if(!rqueue.isEmpty()) {
				rtj=rqueue.peek();
				j= rtj.getTaskVar();
			}
			else {break;}
			if(rtj.isRegular()){
				if(altDisjTreeTLTO.getTime() > j.getLCT()) {rtj.fail();}//Overload Rule applies.
				checkTL(j, ECT);
				checkTO(j, ECT);
				checkFTLO(j, ECT);
			}
			else if(rtj.isOptional()){
				checkSTLO(rtj, ECT);
			}
		}while(!rqueue.isEmpty());
		//Apply all removals
		applyRemovals();
		return updateEST();
	}

	private void checkFTLO(ITask j, TreeMode mode){

	}

	private void calculateECT()
	{
		// will be used in checkFTLO
		int oldESTValue;
		//Clear all lists before starting.
		ectAfter = new int[size.get()];
		ectBefore = new int[size.get()];
		pAfter = new int[size.get()];
		ectWith = new int[size.get()];
		//Set ectBefore_0 to -Infinity, the same for ectAfter_(size-1)
		ectBefore[0] = Integer.MIN_VALUE;
		ectAfter[size.get() - 1] = Integer.MIN_VALUE;
		//Set pAfter_(size-1) to zero.
		pAfter[size.get() - 1] = 0;
		//Looping over rtasks list back to front to calculate ectAfter, and pAfter
		oldESTValue = Integer.MAX_VALUE;
		for(int i = size.get() - 1 ; i >= 1; i--){
			final IRTask taskAtI = rtasks[i];
			final ITask tAtI = taskAtI.getTaskVar();
			assert oldESTValue >= tAtI.getEST();
			//THETA = 1
			if(altDisjTreeTLTO.getTaskType(taskAtI) == 1){
				pAfter[i-1] =  pAfter[i] + tAtI.getMinDuration() ;
				ectAfter[i-1] = Math.max(ectAfter[i], tAtI.getECT()+ pAfter[i]);
			}else{
				//task is not in theta, pass the values
				pAfter[i-1] =  pAfter[i];
				ectAfter[i-1] = ectAfter[i];
			}
			oldESTValue = tAtI.getEST();
		}
		//Looping over rtasks list front to back, to calculate ectBefore, and ectWith
		oldESTValue = Integer.MIN_VALUE;
		for(int i=0; i < size.get()-1; i++){
			final IRTask taskAtI = rtasks[i];
			final ITask taskAfterI = rtasks[i+1].getTaskVar();
			assert oldESTValue <= taskAtI.getTaskVar().getEST();
			if(altDisjTreeTLTO.getTaskType(taskAtI) == 1){
				ectBefore[i+1] = Math.max( taskAfterI.getECT() , ectBefore[i] + 
						taskAfterI.getMinDuration());
			}else{ ectBefore[i+1] = ectBefore[i];}
			//Calculate ECT
			final int r = ectAfter[i+1];
			final int l1 = taskAfterI.getECT() + pAfter[i+1];
			final int l2 = ectBefore[i+1] + taskAfterI.getMinDuration() + pAfter[i+1]; 
			if( r >= l1){ectWith[i+1] = Math.max(r, l2);}
			else{ectWith[i+1] = Math.max(l1, l2);}
			//Add here the steps for calculation of max ECT(theta union i2 which belongs to Tenabled\theta)
			oldESTValue = taskAtI.getTaskVar().getEST();
		}
		final ITask first = rtasks[0].getTaskVar();
		ectWith[0] = Math.max(ectAfter[0], first.getECT() + pAfter[0]);
	}

	/**
	 * Method calculates the ECT(Theta,Omega,Lambda), when the current task from the queue
	 * is optional
	 * @param j
	 */
	private void checkSTLO(IRTask j, TreeMode mode)throws ContradictionException{
			
			//Temporarily remove task j from omega, and embed it in Theta
			altDisjTreeTLTO.removeFromOmega(j);
			altDisjTreeTLTO.insertInTheta(j);
			//Cache the value
			final int ectTUO = altDisjTreeTLTO.getTime();
			int ectT = Integer.MIN_VALUE;
			int ectTL = Integer.MIN_VALUE;
			int oldESTValue = Integer.MIN_VALUE;
			//Tasks in the list are in ascending order of est
			for(IRTask rtask: this){
				final ITask i = rtask.getTaskVar();
				assert oldESTValue <= i.getEST();
				final int taskType = altDisjTreeTLTO.getTaskType(rtask);
				if(taskType == 1){
					//Theta
					ectTL = Math.max( ectTL + i.getMinDuration() , i.getECT());
					ectT = Math.max( ectT + i.getMinDuration() , i.getECT());
				}else if(altDisjTreeTLTO.isTaskRegularAndNotInTheta(rtask)){
					//Then task i belongs to Tenabled\Theta
					if(i.getLST() < ectTUO){
						final int l = ectT + i.getMinDuration();
						final int r = i.getECT();
						ectTL = Math.max(ectTL, Math.max(l, r));
					}
				}
				oldESTValue = i.getEST();
			}
			//Reverse the operation
			altDisjTreeTLTO.removeFromTheta(j.getTaskVar());
			altDisjTreeTLTO.insertInOmega(j);
			switch(mode){
			case ECT:
				final int lct = j.getTaskVar().getLCT();
				if( ectTUO > lct || ectTL > lct ){
					setAsRemoval(j, true);
				}
				break;
			case LST:
				final int est = j.getTaskVar().getEST();
				if( ectTUO < est || ectTL < est){
					setAsRemoval(j, true);
				}
				break;
			}
	}

	private void checkTL(ITask j, TreeMode mode) throws ContradictionException{
		switch(mode){
		case ECT:
			while(altDisjTreeTLTO.getGrayTime() > j.getLCT()){
				//get responsible task from Lambda
				final IRTask rti = (IRTask) altDisjTreeTLTO.getResponsibleTask();
				final ITask i = rti.getTaskVar();
				if(altDisjTreeTLTO.getTime() > i.getEST()){
					if(altDisjTreeTLTO.getTime() > i.getLST()){
						//IF task is Regular, then its time window will become incoherent 
						if(rti.isRegular()) { rti.fail();}
						//Otherwise, remove optional task from Lambda
						else if(rti.isOptional()){
							setAsRemoval(rti, false);
							continue;
						}
						else{
							throw new SolverException("Eliminated tasks should not exist in a TL tree");
						}
					}else{
						//******************
						if(rti.isRegular())
							//******************	
							addUpdate(rti,altDisjTreeTLTO.getTime());}
				}
				altDisjTreeTLTO.removeFromLambda(i);
			}
			break;
		case LST:
			while(altDisjTreeTLTO.getGrayTime() < j.getEST()){
				final IRTask rti = (IRTask) altDisjTreeTLTO.getResponsibleTask();
				final ITask i = rti.getTaskVar();
				if(altDisjTreeTLTO.getTime() < i.getLCT()){
					if(altDisjTreeTLTO.getTime() < i.getECT()){ 
						if(rti.isRegular()) { rti.fail();}
						else if(rti.isOptional()){
							setAsRemoval(rti, false);
							continue;
						}
						else{
							throw new SolverException("Eliminated tasks should not exist in a TL tree");
						}
					}
					else{
						//*****************
						if(rti.isRegular())
							//*****************
							addUpdate(rti,altDisjTreeTLTO.getTime());}
				}
				altDisjTreeTLTO.removeFromLambda(i);
			}
			break;
		default:
			throw new UnsupportedOperationException("unknown tree mode.");
		}
	}


	private void checkTO(ITask j, TreeMode mode) throws ContradictionException
	{
		switch(mode){
		case ECT:
			while(altDisjTreeTLTO.getTOTime() > j.getLCT()){
				//get responsible optional task.
				final IRTask rti= (IRTask) altDisjTreeTLTO.getResponsibleTOTask();
				//Remove optional task from OMEGA
				setAsRemoval(rti, true);
			}
			break;
		case LST:
			while(altDisjTreeTLTO.getTOTime() < j.getEST()){
				final IRTask rti = (IRTask) altDisjTreeTLTO.getResponsibleTOTask();
				setAsRemoval(rti, true);
			}
			break;
		default:
			throw new UnsupportedOperationException("unknown tree mode.");
		}
	}


	private void setupEF(final TreeMode mode,final Comparator<IRTask> queueComp){
		this.clear();
		//Initialise Edge Finding Tree
		this.altDisjTreeTLTO.initializeEdgeFinding(mode, this);
		Arrays.sort(rtasks, 0 , size.get(), makeREarliestStartingTimeCmp());
		this.rqueue.sort(queueComp);
	}

	@Override
	public boolean edgeFindingLCT()throws ContradictionException{

		//rqueue should be sorted in ascending order of EST.
		//While Tasks List should still be sorted in ascending order of EST.
		setupEF(LST, makeREarliestStartingTimeCmp());
		IRTask rtj=rqueue.peek();
		ITask j= rtj.getTaskVar();
		if(rtj.isRegular())
			if(altDisjTreeTLTO.getTime() < j.getEST()) {rtj.fail();}
		do{
			//Remove Task j from either Theta, or Omega, and insert it in Lambda
			if(rtj.isOptional()){
				altDisjTreeTLTO.removeFromOmegaAndInsertInLambda(rtj);
			}
			else if(rtj.isRegular()){
				altDisjTreeTLTO.removeFromThetaAndInsertInLambda(rtj);
			}
			rqueue.poll();
			if(!rqueue.isEmpty()) {
				rtj=rqueue.peek();
				j= rtj.getTaskVar();
			}
			else {break;}
			if(rtj.isRegular()){
				if(altDisjTreeTLTO.getTime() < j.getEST()) {rtj.fail();}
				checkTL(j, LST);
				checkTO(j, LST);
				checkFTLO(j, LST);
			}else if(rtj.isOptional()){
				checkSTLO(rtj, LST);
			}
		}while(!rqueue.isEmpty());
		applyRemovals();
		return updateLCT();
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

