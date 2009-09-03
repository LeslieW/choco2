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
import java.util.Collections;
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
	 * @param name the ressource name
	 * @param taskvars the tasks using the resources
	 * @param uppBound is an integer variable such that max(end(T))<= uppBound
	 * @param otherVars other integer variables of the constraint
	 */
	public AbstractResourceSConstraint(String name, final TaskVar[] taskvars, final IntDomainVar uppBound, final IntDomainVar... otherVars) {
		super(taskvars, createIntVarArray(otherVars, uppBound));
		this.rtasks = new IRTask[getNbTasks()];
		this.name = name;
		this.indexUB = getNbVars()-2;
		this.indexUnit = getNbVars()-1;
		this.flags = new BitFlags();
		
		for (int i = 0; i < taskvars.length; i++) {
			rtasks[i] = new RTask(i);
		}
	}

	
	@Override
	public final IRTask getRTask(final int idx) {
		return rtasks[idx];
	}
	
	protected int getUsageIndex(final int taskIdx) {
		return indexUnit;
	}
	
	protected int getHeightIndex(final int taskIdx) {
		return indexUnit;
	}

	@Override
	public void setSolver(final Solver solver) {
		super.setSolver(solver);
	}

	
	private final static IntDomainVar[] createIntVarArray(final IntDomainVar[] otherVars,final IntDomainVar uppBound) {
		if(uppBound == null) {
			throw new SolverException("no makepsan specified for a resource constraint");
		}
		IntDomainVar unit = uppBound.getSolver().createIntegerConstant("unit", 1);
		return ArrayUtils.append(otherVars, new IntDomainVar[]{uppBound, unit});
	}

	
	protected void updateMakespan(final int value) throws ContradictionException {
		if(indexUB >= 0) {
			getIntVar(indexUB).updateInf(value, cIndices[indexUB]);
		}
	}	


	public final BitFlags getFlags() {
		return flags;
	}


	@Override
	public void awake() throws ContradictionException {
		ensureTaskConsistency();
		super.awake();
	}

	private final void checkTask(final int varIdx) throws ContradictionException {
		if(varIdx < taskIntVarOffset) {updateCompulsoryPart(varIdx % getNbTasks() );}
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




	//*****************************************************************//
	//*******************  Resource  ********************************//
	//***************************************************************//


	@Override
	public String pretty() {
		return super.pretty();
	}


	@Override
	public String getRscName() {
		return name;
	}


	
	@Override
	public List<TaskVar> asList() {
		return Collections.unmodifiableList(Arrays.asList(taskvars));
	}


	@Override
	public Iterator<TaskVar> getTaskIterator() {
		return IteratorUtils.iterator(taskvars);
	}


	/**
	 * a simple task Wrapper for required task with unit height.
	 * 
	 */
	//TODO optimize index access
	public class RTask extends AbstractRTask {

				
		public RTask(final int taskidx) {
			super(taskidx);
		}

		@Override
		public TaskVar getTaskVar() {
			return taskvars[taskIdx];
		}

		@Override
		public boolean schedule(final int startingTime, final int duration)
				throws ContradictionException {
			if( taskvars[taskIdx].start().instantiate(startingTime, getCIndiceStart(taskIdx)) ||
					taskvars[taskIdx].duration().instantiate(duration, getCIndiceDuration(taskIdx))	) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean setDuration(final int duration) throws ContradictionException {
			if( taskvars[taskIdx].duration().instantiate(duration, getCIndiceDuration(taskIdx))	) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean setStartingTime(final int startingTime)
				throws ContradictionException {
			if( taskvars[taskIdx].start().instantiate(startingTime, getCIndiceStart(taskIdx))) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean updateECT(final int val) throws ContradictionException {
			if( taskvars[taskIdx].end().updateInf(val, getCIndiceEnd(taskIdx)) ) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean updateEST(final int val) throws ContradictionException {
			if( taskvars[taskIdx].start().updateInf(val, getCIndiceStart(taskIdx)) ) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean updateLCT(final int val) throws ContradictionException {
			if( taskvars[taskIdx].end().updateSup(val, getCIndiceEnd(taskIdx)) ) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean updateLST(final int val) throws ContradictionException {
			if( taskvars[taskIdx].start().updateSup(val, getCIndiceStart(taskIdx)) ) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean updateMaxDuration(final int val) throws ContradictionException {
			if( taskvars[taskIdx].duration().updateSup(val, getCIndiceDuration(taskIdx)) ) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		@Override
		public boolean updateMinDuration(final int val) throws ContradictionException {
			if( taskvars[taskIdx].duration().updateInf(val, getCIndiceDuration(taskIdx)) ) {
				updateCompulsoryPart(taskIdx);
				return true;
			}
			return false;
		}

		
		@Override
		public void fail() throws ContradictionException {
			AbstractResourceSConstraint.this.fail();
		}

		
		@Override
		public boolean assign() throws ContradictionException {
			final int idx = getUsageIndex(taskIdx);
			return vars[idx].instantiate(1, cIndices[idx]);
		}

		@Override
		public boolean isOptional() {
			return !vars[getUsageIndex(taskIdx)].isInstantiated();
		}

		@Override
		public boolean isRegular() {
			return vars[getUsageIndex(taskIdx)].isInstantiatedTo(1);
		}
		
		@Override
		public boolean isEliminated() {
			return vars[getUsageIndex(taskIdx)].isInstantiatedTo(0);
		}

		@Override
		public boolean remove() throws ContradictionException {
			final int idx = getUsageIndex(taskIdx);
			return vars[idx].instantiate(0, cIndices[idx]);
		}

		@Override
		public IntDomainVar getHeight() {
			return vars[getHeightIndex(taskIdx)];
		}

		@Override
		public boolean updateMaxHeight(final int val) throws ContradictionException {
			final int idx = getHeightIndex(taskIdx);
			return vars[idx].updateSup(val, cIndices[idx]);
		}

		@Override
		public boolean updateMinHeight(final int val) throws ContradictionException {
			final int idx = getHeightIndex(taskIdx);
			return vars[idx].updateInf(val, cIndices[idx]);
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
