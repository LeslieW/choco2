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

import choco.cp.solver.constraints.BitFlags;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.0</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractResourceSConstraint extends AbstractTaskSConstraint implements IResource<TaskVar> {

	protected final IRTask[] rtasks;

	protected final RMakespan makespan;

	protected final String name;

	protected final BitFlags flags;

	//TODO init
	private final int nbRegularTasks;

	private final int nbOptionalTasks;

	private boolean enableHeights;

	private final int indexUnit;

	protected final int indexUB;

	public AbstractResourceSConstraint(Solver solver, String name, final TaskVar[] taskvars, final IntDomainVar makespan) {
		this(solver, name, taskvars, 0, false, false, makespan);
	}

	/**
	 * 
	 * Create a ressource constraint.
	 * @param solver
	 * @param name the ressource name
	 * @param taskvars the tasks using the resources
	 * @param enableHypotheticalDomain TODO
	 * @param uppBound is an integer variable such that max(end(T))<= uppBound
	 * @param otherVars = [ u_k, ...,u_n,h_1,...,h_n,v_1,v_m, ub, cste:1] 
	 */
	public AbstractResourceSConstraint(Solver solver, String name, 
			final TaskVar[] taskvars, int nbOptionalTasks
			, boolean enableHeights, boolean enableHypotheticalDomain, final IntDomainVar... intvars) {
		super(taskvars, intvars, solver.createIntegerConstant("unit", 1));
		this.name = name;
		this.nbOptionalTasks = nbOptionalTasks;
		this.nbRegularTasks = taskvars.length - nbOptionalTasks;
		this.enableHeights = enableHeights;
		//TODO checkIntVars !
		this.indexUnit = getNbVars()-1;
		this.indexUB = indexUnit - 1;
		this.flags = new BitFlags();
		this.rtasks = new RTask[getNbTasks()];
		final IEnvironment env = solver.getEnvironment();
		if(enableHypotheticalDomain) {
			for (int i = 0; i < nbRegularTasks; i++) {
				rtasks[i] = new RTask(i);
			}
			for (int i = nbRegularTasks; i < rtasks.length; i++) {
				rtasks[i] = new HRTask(i, env);
			}
		} else {
			for (int i = 0; i < rtasks.length; i++) {
				rtasks[i] = new RTask(i);
			}
		}

		this.makespan = new RMakespan();
	}

	public void checkHypotheticalDomains()throws ContradictionException{
		for(int i = nbRegularTasks; i < rtasks.length; i++){
			if(rtasks[i].isOptional())
				rtasks[i].checkConsistency();
		}
	}
	private void checkIntVars() {
		//TODO
	}

	@Override
	public int getFilteredEventMask(int idx) {
		return idx < taskIntVarOffset || idx >= taskIntVarOffset + nbOptionalTasks ? 
				EVENT_MASK : IntVarEvent.INSTINTbitvector;
	}


	@Override
	public final IRTask getRTask(final int idx) {
		return rtasks[idx];
	}

	/**
	 * do not use subclass fields to compute the index because the function is called by the constructor.
	 */
	protected final int getUsageIndex(final int taskIdx) {
		return  taskIdx < nbRegularTasks ? indexUnit : taskIntVarOffset + taskIdx - nbRegularTasks;
	}

	/**
	 * do not use subclass fields to compute the index because the function is called by the constructor.
	 */
	protected final int getHeightIndex(final int taskIdx) {
		return enableHeights ? taskIntVarOffset + nbOptionalTasks + taskIdx : indexUnit;
	}

	public final void enforceTaskConsistency() throws ContradictionException {
		for (IRTask rt : rtasks) {
			rt.checkConsistency();
		}
	}

	public final BitFlags getFlags() {
		return flags;
	}


	@Override
	public void awake() throws ContradictionException {
		enforceTaskConsistency();
		super.awake();
	}

	public final boolean checkTask(final int varIdx) throws ContradictionException {
		if(varIdx < taskIntVarOffset) {
			//avoid modulo because it is not time-efficient
			if(varIdx < startOffset) rtasks[varIdx].checkConsistency();
			else if (varIdx < endOffset) rtasks[varIdx - startOffset].checkConsistency();
			else rtasks[varIdx -endOffset].checkConsistency();
			return true;
		}
		return false;
	}



	@Override
	public void awakeOnBounds(int varIndex) throws ContradictionException {
		checkTask(varIndex);
		super.awakeOnBounds(varIndex);
	}


	@Override
	public void awakeOnInf(final int varIdx) throws ContradictionException {
		checkTask(varIdx);
		super.awakeOnInf(varIdx);
	}

	@Override
	public void awakeOnInst(final int idx) throws ContradictionException {
		if( ! checkTask(idx) && idx < taskIntVarOffset + nbOptionalTasks){
			final int tIdx = idx - taskIntVarOffset + nbRegularTasks;
			if(vars[idx].isInstantiatedTo(0)) 
				fireTaskRemoval(rtasks[tIdx]);
			else
				//assigned value 1: Becomes a regular task.
				rtasks[tIdx].assign();
		}
		super.awakeOnInst(idx);
	}


	@Override
	public void awakeOnSup(final int varIdx) throws ContradictionException {
		checkTask(varIdx);
		super.awakeOnSup(varIdx);
	}

	public void fireTaskRemoval(IRTask rtask) {
		throw new UnsupportedOperationException();
	}

	/**
	 * returns (s_i + p_i = e_i)
	 */
	protected final boolean isTaskSatisfied(int[] tuple, int tidx) {
		//start + duration = end
		return tuple[tidx] + tuple[endOffset + tidx] == tuple[startOffset + tidx];
	}


	protected final boolean isRegular(int[] tuple, int tidx) {
		return tuple[getUsageIndex(tidx)] == 1;
	}

	protected final int getHeight(int[] tuple, int tidx) {
		return tuple[getHeightIndex(tidx)];
	}


	public final boolean isCumulativeSatisfied(int tuple[],int consumption, int capacity) {
		if( capacity < consumption) return false;
		int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
		final int n = getNbTasks();
		//compute execution interval.
		for (int tidx = 0; tidx < n; tidx++) {
			if( ! isTaskSatisfied(tuple, tidx)) return false;
			else {
				start = Math.min(start,  tuple[getStartIndex(tidx)]);
				end = Math.max(end,  tuple[getEndIndex(tidx)]);
			}
		}
		if(start < end) {
			//compute the profile
			int[] load = new int[end - start];
			for (int tidx = 0; tidx < n; tidx++) {
				if( isRegular(tuple, tidx)) {
					for(int i = tuple[getStartIndex(tidx)]; i < tuple[getEndIndex(tidx)]; i++) {
						load[i - start] += getHeight(tuple, tidx);
					}
				}
			}
			//FIXME how do I handle properly task with nil duration in disjunctive
			//check profile
			for (int aLoad : load) {
				if ( aLoad > capacity ||
						(aLoad != 0 && aLoad < consumption) ) {
					return false;
				}
			}
		}
		return true;
	}

	//*****************************************************************//
	//*******************  Resource  ********************************//
	//***************************************************************//


	@Override
	public String pretty() {
		return getRscName()+": "+super.pretty();
	}


	@Override
	public final String getRscName() {
		return name;
	}



	@Override
	public final List<TaskVar> asList() {
		return Arrays.asList(taskvars);
	}


	@Override
	public final Iterator<TaskVar> getTaskIterator() {
		return IteratorUtils.iterator(taskvars);
	}

	private class RMakespan implements IRMakespan {

		@Override
		public IntDomainVar getMakespan() {
			return vars[indexUB];
		}

		@Override
		public void updateInf(int value) throws ContradictionException {
			vars[indexUB].updateInf(value, cIndices[indexUB]);

		}

		@Override
		public void updateSup(int value) throws ContradictionException {
			vars[indexUB].updateSup(value, cIndices[indexUB]);
		}


	}

	/**
	 * Link task with the usage and height variables of a resource.
	 * Handle the domain of the task in the resource 
	 * 
	 */
	public class RTask extends AbstractRTask {

		private final int eidx, didx, uidx, hidx;		

		public RTask(final int taskidx) {
			super(taskidx);
			eidx = getEndIndex(taskidx);
			didx = getDurationIndex(taskidx);
			uidx = getUsageIndex(taskIdx);
			hidx = getHeightIndex(taskIdx);
		}

		@Override
		public final TaskVar getTaskVar() {
			return taskvars[taskIdx];
		}

		@Override
		public ITask getHTask() {
			return getTaskVar();
		}

		@Override
		public void checkConsistency() throws ContradictionException {
			updateCompulsoryPart();			
		}

		@Override
		public final boolean updateDuration(int duration) throws ContradictionException {
			if( setDuration(duration)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}



		@Override
		public final boolean updateECT(int val) throws ContradictionException {
			if( setECT(val)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateEndingTime(int endingTime)
		throws ContradictionException {
			if( setEndingTime(endingTime)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateEndNotIn(int a, int b) throws ContradictionException {
			if( setEndNotIn(a, b)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateEST(int val) throws ContradictionException {
			if( setEST(val)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateLCT(int val) throws ContradictionException {
			if( setLCT(val)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}


		@Override
		public final boolean updateLST(int val) throws ContradictionException {
			if( setLST(val)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateMaxDuration(int val) throws ContradictionException {
			if( setMaxDuration(val)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateMinDuration(int val) throws ContradictionException {
			if( setMinDuration(val)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateStartingTime(int startingTime)
		throws ContradictionException {
			if( setStartingTime(startingTime)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}




		@Override
		public final boolean updateStartNotIn(int a, int b) throws ContradictionException {
			if( setStartNotIn(a, b)) {
				updateCompulsoryPart();
				return true;
			}
			return false;
		}


		@Override
		public boolean setDuration(final int duration) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[didx].instantiate(duration, cIndices[didx]);
		}

		@Override
		public boolean setStartingTime(final int startingTime)
		throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[taskIdx].instantiate(startingTime, cIndices[taskIdx]);
		}


		@Override
		public boolean setEndingTime(int endingTime)
		throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[eidx].instantiate(endingTime, cIndices[eidx]);
		}

		@Override
		public boolean setEndNotIn(int a, int b)
		throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[eidx].removeInterval(a, b, cIndices[eidx]);
		}

		@Override
		public boolean setStartNotIn(int min, int max)
		throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[taskIdx].removeInterval(min, max, cIndices[taskIdx]);
		}


		@Override
		public boolean setECT(final int val) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[eidx].updateInf(val, cIndices[eidx]);
		}

		@Override
		public boolean setEST(final int val) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[taskIdx].updateInf(val, cIndices[taskIdx]);
		}

		@Override
		public boolean setLCT(final int val) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[eidx].updateSup(val, cIndices[eidx]);
		}

		@Override
		public boolean setLST(final int val) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[taskIdx].updateSup(val, cIndices[taskIdx]);
		}

		@Override
		public boolean setMaxDuration(final int val) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[didx].updateSup(val, cIndices[didx]);
		}

		@Override
		public boolean setMinDuration(final int val) throws ContradictionException {
			assert isRegular(); //do not change the domain of optional/eliminated tasks
			return vars[didx].updateInf(val, cIndices[didx]);
		}

		public final void updateCompulsoryPart() throws ContradictionException {
			final IntDomainVar s = vars[taskIdx];
			final IntDomainVar e = vars[eidx];
			final IntDomainVar d = vars[didx];
			boolean fixPoint;
			do {
				fixPoint = false;
				fixPoint |= s.updateInf(e.getInf() - d.getSup(), cIndices[taskIdx]);
				fixPoint |= s.updateSup(e.getSup() - d.getInf(), cIndices[taskIdx]);
				fixPoint |= e.updateInf(s.getInf() + d.getInf(), cIndices[eidx]);
				fixPoint |= e.updateSup(s.getSup() + d.getSup(), cIndices[eidx]);
				fixPoint |= d.updateInf(e.getInf() - s.getSup(), cIndices[didx]);
				fixPoint |= d.updateSup(e.getSup() - s.getInf(), cIndices[didx]);
			}while (fixPoint);
		}


		@Override
		public final void fail() throws ContradictionException {
			AbstractResourceSConstraint.this.fail();
		}


		@Override
		public boolean assign() throws ContradictionException {
			return vars[uidx].instantiate(1, cIndices[uidx]);
		}

		@Override
		public final boolean isOptional() {
			return ! vars[uidx].isInstantiated();
		}

		@Override
		public final boolean isRegular() {
			return vars[uidx].isInstantiatedTo(1);
		}

		@Override
		public final boolean isEliminated() {
			return vars[uidx].isInstantiatedTo(0);
		}


		@Override
		public final boolean remove() throws ContradictionException {
			return vars[uidx].instantiate(0, cIndices[uidx]);
		}



		@Override
		public final void fireRemoval() {
			assert isEliminated(); 
			fireTaskRemoval(this);
		}

		@Override
		public final IntDomainVar getHeight() {
			return vars[hidx];
		}

		@Override
		public final boolean updateMaxHeight(final int val) throws ContradictionException {
			return vars[hidx].updateSup(val, cIndices[hidx]);
		}

		@Override
		public final boolean updateMinHeight(final int val) throws ContradictionException {
			return vars[hidx].updateInf(val, cIndices[hidx]);
		}

		@Override
		public String toString() {
			return getTaskVar().toString();
		}

		@Override
		public String pretty() {
			return getTaskVar().pretty();
		}
	}

	//FIXME adapt rtask comparators.
	public final class HRTask extends RTask implements ITask {

		private final IStateInt estH,lctH;

		public HRTask(int taskidx, IEnvironment env) {
			super(taskidx);
			estH = env.makeInt(getTaskVar().getEST());
			lctH = env.makeInt(getTaskVar().getLCT());
		}
		
		@Override
		public ITask getHTask() {
			return this;
		}

		//ITask interface : integrate the hypothetical domains for filtering algorithms.
		@Override
		public int getECT() {
			final int valR = getTaskVar().getECT();
			if(isRegular())
				return valR;
			else
			{
				final int valH = estH.get() + getTaskVar().getMinDuration();
				return valR >  valH ? valR : valH;
			}
		}

		@Override
		public int getEST() {
			final int valR = getTaskVar().getEST();
			if(isRegular())
				return valR;
			else
			{
				final int valH = estH.get();
				return valR >  valH ? valR : valH;
			}
		}

		@Override
		public int getID() {
			return getTaskVar().getID();
		}

		@Override
		public int getLCT() {
			final int valR = getTaskVar().getLCT();
			if(isRegular())
				return valR;
			else
			{
				final int valH = lctH.get();
				return valR >  valH ? valH : valR;
			}
		}

		@Override
		public int getLST() {
			final int valR = getTaskVar().getLST();
			if(isRegular())
				return valR;
			else
			{
				final int valH = lctH.get() - getTaskVar().getMinDuration();
				return valR >  valH ? valH : valR;
			}
		}

		@Override
		public int getMaxDuration() {
			final int valR = getTaskVar().getMaxDuration();
			if(isRegular())
				return valR;
			else{
				final int valH = lctH.get() - estH.get();
				return valR >  valH ? valH : valR;
			}
		}

		@Override
		public int getMinDuration() {
			return getTaskVar().getMinDuration();
		}

		@Override
		public String getName() {
			return getTaskVar().getName();
		}

		@Override
		public boolean hasConstantDuration() {
			return getTaskVar().hasConstantDuration();
		}

		@Override
		public boolean isScheduled() {
			return getTaskVar().isScheduled();
		}

		//IRTask interface : update/set operations according to the status of the task (regular, optional, eliminated).
		public boolean checkHypotheticalConsistency() {
			return getLCT() - getEST() >= getMinDuration();
		}


		private void checkHConsistency() throws ContradictionException {
			if( ! checkHypotheticalConsistency() ) {
				remove();
				fireRemoval();
			}
		}

		@Override
		public void checkConsistency() throws ContradictionException {
			super.checkConsistency();
			if(isOptional() && !checkHypotheticalConsistency()) {
				remove();
				fireRemoval();
			}
		}


		@Override
		public boolean assign() throws ContradictionException {
			final boolean assigned = super.assign();
			super.setEST(estH.get());
			super.setLCT(lctH.get());
			updateCompulsoryPart();
			return assigned;
		}

		private boolean setHEST(int val) throws ContradictionException {
			if( val > getEST() ) {
				estH.set(val);
				checkHConsistency();
				return true;
			} else return false;
		}

		@Override
		public boolean setEST(int val) throws ContradictionException {
			if( isOptional()) return setHEST(val);
			else return super.setEST(val);
		}

		private boolean setHDuration(int duration) throws ContradictionException {
			//did not find updates for estH,lctH
			if ( getLCT() - getEST() >= duration ) {
				//hypothetical duration is not consistent
				remove();
				return true;
			}
			return false;
		}
		@Override
		public boolean setDuration(int duration) throws ContradictionException {
			if( isOptional()) return setHDuration(duration);
			else return super.setDuration(duration);
		}

		private boolean setHECT(int val) throws ContradictionException {
			if( val > getECT() ) {
				estH.set(val - getMinDuration());
				checkHConsistency();
				return true;
			} else return false;
		}

		@Override
		public boolean setECT(int val) throws ContradictionException {
			if( isOptional()) return setHECT(val);
			else return super.setECT(val);
		}


		public boolean setHEndingTime(int endingTime) throws ContradictionException {
			return setHLCT(endingTime) || setHECT(endingTime);
		}

		@Override
		public boolean setEndingTime(int endingTime)
		throws ContradictionException {
			if( isOptional()) return setHEndingTime(endingTime);
			else return super.setEndingTime(endingTime);
		}

		public boolean setHEndNotIn(int a, int b) throws ContradictionException {
			if( a <= getECT()) return setHECT(b);
			else if(b >= getLCT()) return setHLCT(a);
			else return false;
		}

		@Override
		public boolean setEndNotIn(int a, int b) throws ContradictionException {
			if( isOptional()) return setHEndNotIn(a, b);
			else return super.setEndNotIn(a, b);
		}

		private boolean setHLCT(int val) throws ContradictionException {
			if( val < getLCT() ) {
				lctH.set(val);
				checkHConsistency();
				return true;
			} else return false;
		}

		@Override
		public boolean setLCT(int val) throws ContradictionException {
			if( isOptional()) return setHLCT(val);
			else return super.setLCT(val);
		}

		private boolean setHLST(int val) throws ContradictionException {
			if( val < getLST() ) {
				lctH.set(val + getMaxDuration());
				checkHConsistency();
				return true;
			} else return false;
		}

		@Override
		public boolean setLST(int val) throws ContradictionException {
			if( isOptional()) return setHLST(val);
			else return super.setLST(val);
		}


		@Override
		public boolean setMaxDuration(int val) throws ContradictionException {
			if ( isRegular() ) return super.setMaxDuration(val);
			else return false;
		}


		@Override
		public boolean setMinDuration(int val) throws ContradictionException {
			if( isOptional()) return setHDuration(val);
			else return super.setMinDuration(val);
		}


		private boolean setHStartingTime(int startingTime) throws ContradictionException {
			return setHEST(startingTime) || setHLST(startingTime);
		}


		@Override
		public boolean setStartingTime(int startingTime)
		throws ContradictionException {
			if( isOptional()) return setHStartingTime(startingTime); 
			else return super.setStartingTime(startingTime);
		}

		private boolean setHStartNotIn(int min, int max) throws ContradictionException {
			if( min <= getEST()) return setHEST(max);
			else if(max >= getLST()) return setHLST(min);
			else return false;
		}

		@Override
		public boolean setStartNotIn(int min, int max)
		throws ContradictionException {
			if( isOptional()) return setHStartNotIn(min, max);
			else return super.setStartNotIn(min, max);
		}
        public boolean updateREST(int minEST)throws ContradictionException{
        	//Applying the third rule
        	if(super.setEST(minEST)){
        		updateCompulsoryPart();
        		return true;
        	}
        	else
        		return false;
        }
        
        public boolean updateRLCT(int maxLCT)throws ContradictionException{
        	//Applying the third rule
        	if(super.setLCT(maxLCT)){
        		updateCompulsoryPart();
        		return true;
        	}
        	else
        		return false;
        }

	}

}
