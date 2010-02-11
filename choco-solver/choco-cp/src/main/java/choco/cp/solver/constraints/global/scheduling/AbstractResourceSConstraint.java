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
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.AbstractRTask;
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


	protected final IRTask[] rtasks;

	protected final String name;

	protected final BitFlags flags;

	protected final int indexUnit;

	protected final int indexUB;

	/**
	 * 
	 * Create a ressource constraint.
	 * @param solver
     * @param name the ressource name
     * @param taskvars the tasks using the resources
     * @param uppBound is an integer variable such that max(end(T))<= uppBound
     * @param otherVars other integer variables of the constraint
	 */
	public AbstractResourceSConstraint(Solver solver, String name, final TaskVar[] taskvars, final IntDomainVar uppBound, final IntDomainVar... otherVars) {
		super(taskvars, createIntVarArray(solver, otherVars, uppBound));
		this.rtasks = new RTask[getNbTasks()];
		this.name = name;
		this.indexUnit = getNbVars()-1;
		this.indexUB = indexUnit - 1;
		this.flags = new BitFlags();
		for (int i = 0; i < taskvars.length; i++) {
			rtasks[i] = new RTask(i);
		}
	}

	
	
	@Override
	public final IRTask getRTask(final int idx) {
		return rtasks[idx];
	}

	/**
	 * do not use subclass fields to compute the index because the function is called by the constructor.
	 */
	protected int getUsageIndex(final int taskIdx) {
		return indexUnit;
	}
	
	/**
	 * do not use subclass fields to compute the index because the function is called by the constructor.
	 */
	protected int getHeightIndex(final int taskIdx) {
		return indexUnit;
	}

	public final void ensureTaskConsistency() throws ContradictionException {
		for (IRTask rt : rtasks) {
			rt.updateCompulsoryPart();
		}
	}
	private static IntDomainVar[] createIntVarArray(Solver solver, final IntDomainVar[] otherVars, final IntDomainVar uppBound) {
		if(uppBound == null) {
			throw new SolverException("no makespan for resource constraint");
		}
		IntDomainVar unit = solver.createIntegerConstant("unit", 1);
		return ArrayUtils.append(otherVars, new IntDomainVar[]{uppBound, unit});
	}


	protected final void updateMakespan(final int value) throws ContradictionException {
		vars[indexUB].updateInf(value, cIndices[indexUB]);
	}	


	public final BitFlags getFlags() {
		return flags;
	}


	@Override
	public void awake() throws ContradictionException {
		ensureTaskConsistency();
		super.awake();
	}

	private void checkTask(final int varIdx) throws ContradictionException {
		if(varIdx < taskIntVarOffset) {rtasks[varIdx % getNbTasks()].updateCompulsoryPart();}
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
		checkTask(idx);
		super.awakeOnInst(idx);
	}


	@Override
	public void awakeOnSup(final int varIdx) throws ContradictionException {
		checkTask(varIdx);
		super.awakeOnSup(varIdx);
	}

	/**
	 * returns (s_i + p_i = e_i)
	 */
	protected final boolean isTaskSatisfied(int[] tuple, int tidx) {
		//start + duration = end
		return tuple[tidx] + tuple[endOffset + tidx] == tuple[startOffset + tidx];
	}
	
	
	protected boolean isRegular(int[] tuple, int tidx) {
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
	public String getRscName() {
		return name;
	}



	@Override
	public List<TaskVar> asList() {
		return Arrays.asList(taskvars);
	}


	@Override
	public Iterator<TaskVar> getTaskIterator() {
		return IteratorUtils.iterator(taskvars);
	}


	/**
	 * a simple task Wrapper for required task with unit height.
	 * 
	 */
	public final class RTask extends AbstractRTask {

		private final int eidx, didx, uidx, hidx;		

		public RTask(final int taskidx) {
			super(taskidx);
			eidx = startOffset + taskIdx;
			didx = endOffset + taskIdx;
			uidx = getUsageIndex(taskIdx);
			hidx = getHeightIndex(taskIdx);
		}

		@Override
		public final TaskVar getTaskVar() {
			return taskvars[taskIdx];
		}


		@Override
		public boolean schedule(final int startingTime, final int duration)
		throws ContradictionException {
			return 	vars[taskIdx].instantiate(startingTime, cIndices[taskIdx]) ||
			vars[didx].instantiate(duration, cIndices[didx]) ||
			vars[eidx].instantiate(startingTime + duration, cIndices[eidx]);
		}

		@Override
		public boolean setDuration(final int duration) throws ContradictionException {
			return vars[didx].instantiate(duration, cIndices[didx]);
		}

		@Override
		public boolean setStartingTime(final int startingTime)
		throws ContradictionException {
			return vars[taskIdx].instantiate(startingTime, cIndices[taskIdx]);
		}
		
		
		@Override
		public boolean setEndingTime(int startingTime)
				throws ContradictionException {
			return vars[eidx].instantiate(startingTime, cIndices[eidx]);
		}

		@Override
		public boolean setEndNotIn(int a, int b)
				throws ContradictionException {
			return vars[eidx].removeInterval(a, b, cIndices[eidx]);
		}

		@Override
		public boolean setStartNotIn(int min, int max)
				throws ContradictionException {
			return vars[taskIdx].removeInterval(min, max, cIndices[taskIdx]);
		}

		
		@Override
		public boolean setECT(final int val) throws ContradictionException {
			return vars[eidx].updateInf(val, cIndices[eidx]);
		}

		@Override
		public boolean setEST(final int val) throws ContradictionException {
			return vars[taskIdx].updateInf(val, cIndices[taskIdx]);
		}

		@Override
		public boolean setLCT(final int val) throws ContradictionException {
			return vars[eidx].updateSup(val, cIndices[eidx]);
		}

		@Override
		public boolean setLST(final int val) throws ContradictionException {
			return vars[taskIdx].updateSup(val, cIndices[taskIdx]);
		}

		@Override
		public boolean setMaxDuration(final int val) throws ContradictionException {
			return vars[didx].updateSup(val, cIndices[didx]);
		}

		@Override
		public boolean setMinDuration(final int val) throws ContradictionException {
			return vars[didx].updateInf(val, cIndices[didx]);
		}

		public final void updateCompulsoryPart()	throws ContradictionException {
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
		public void fail() throws ContradictionException {
			AbstractResourceSConstraint.this.fail();
		}


		@Override
		public boolean assign() throws ContradictionException {
			return vars[uidx].instantiate(1, cIndices[uidx]);
		}

		@Override
		public boolean isOptional() {
			return ! vars[uidx].isInstantiated();
		}

		@Override
		public boolean isRegular() {
			return vars[uidx].isInstantiatedTo(1);
		}

		@Override
		public boolean isEliminated() {
			return vars[uidx].isInstantiatedTo(0);
		}

		@Override
		public boolean remove() throws ContradictionException {
			return vars[uidx].instantiate(0, cIndices[uidx]);
		}

		@Override
		public IntDomainVar getHeight() {
			return vars[hidx];
		}

		@Override
		public boolean updateMaxHeight(final int val) throws ContradictionException {
			return vars[hidx].updateSup(val, cIndices[hidx]);
		}

		@Override
		public boolean updateMinHeight(final int val) throws ContradictionException {
			return vars[hidx].updateInf(val, cIndices[hidx]);
		}

		@Override
		public String toString() {
			return getTaskVar().getName();
		}

		@Override
		public String pretty() {
			return getTaskVar().pretty();
		}
	}

}
