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

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.bitmask.BitMask;
import choco.kernel.common.util.tools.IteratorUtils;
import static choco.kernel.common.util.tools.TaskUtils.hasEnumeratedDomain;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.AbstractTaskSConstraint;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRMakespan;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.0</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractResourceSConstraint extends AbstractTaskSConstraint implements IResource<TaskVar> {

	public static final int TASK_MASK = IntVarEvent.INSTINT_MASK + IntVarEvent.BOUNDS_MASK;
	
	protected final IRTask[] rtasks;

	protected final RMakespan makespan;

	protected final String name;

	protected final BitMask flags;

	private final int nbRegularTasks;

	private final int nbOptionalTasks;

	private final boolean enableHeights;

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
			final TaskVar[] taskvars, int nbOptionalTasks,
			boolean enableHeights, boolean enableHypotheticalDomain, final IntDomainVar... intvars) {
		super(taskvars, intvars, solver.createIntegerConstant("unit", 1));
		this.name = name;
		this.nbOptionalTasks = nbOptionalTasks;
		this.nbRegularTasks = taskvars.length - nbOptionalTasks;
		this.enableHeights = enableHeights;
		//TODO checkIntVars !
		this.indexUnit = getNbVars()-1;
		this.indexUB = indexUnit - 1;
		this.flags = new BitMask();
		this.rtasks = new RTask[getNbTasks()];
		final IEnvironment env = solver.getEnvironment();
		if(enableHypotheticalDomain) {
			for (int i = 0; i < nbRegularTasks; i++) {
				rtasks[i] = new RTask(this, i);
			}
			for (int i = nbRegularTasks; i < rtasks.length; i++) {
				rtasks[i] = hasEnumeratedDomain(taskvars[i]) ? 
						new EnumHRTask(env, this, i) : new BoundHRTask(env, this, i);
			}
		} else {
			for (int i = 0; i < rtasks.length; i++) {
				rtasks[i] = new RTask(this, i);
			}
		}
		this.makespan = new RMakespan();
	}

	public abstract void readOptions(List<String> options);
	
	public final int indexOf(TaskVar task) {
		//FIXME temporary implementation
		for (int i = 0; i < getNbTasks(); i++) {
			if(taskvars[i] == task) return i;
		}
		return -1;
	}

	public final int getNbRegularTasks() {
		return nbRegularTasks;
	}

	public final int getNbOptionalTasks() {
		return nbOptionalTasks;
	}

	@Override
	public int getFilteredEventMask(int idx) {
		return idx < taskIntVarOffset || idx >= taskIntVarOffset + nbOptionalTasks ? 
				AbstractResourceSConstraint.TASK_MASK : IntVarEvent.INSTINT_MASK;
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

	public final BitMask getFlags() {
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
		if(end > tuple[indexUB]) return false;
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
		return pretty(getRscName());
	}


	@Override
	public final String getRscName() {
		return name;
	}



	@Override
	public final List<TaskVar> asTaskList() {
		return Arrays.asList(taskvars);
	}

	

	@Override
	public List<IRTask> asRTaskList() {
		return Arrays.asList(rtasks);
	}

	@Override
	public final Iterator<TaskVar> getTaskIterator() {
		return IteratorUtils.iterator(taskvars);
	}

	private final class RMakespan implements IRMakespan {

		private RMakespan() {}

		@Override
		public IntDomainVar getMakespan() {
			return vars[indexUB];
		}

		@Override
		public void updateInf(int value) throws ContradictionException {
			vars[indexUB].updateInf(value, AbstractResourceSConstraint.this, false);

		}

		@Override
		public void updateSup(int value) throws ContradictionException {
			vars[indexUB].updateSup(value, AbstractResourceSConstraint.this, false);
		}
	}




}
