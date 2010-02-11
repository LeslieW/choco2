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

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBool;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractCumulativeSConstraint extends AbstractResourceSConstraint implements ICumulativeResource<TaskVar> {


	/** indicates if all heights are instantiated (lazy computation) */
	private final IStateBool fixedHeights;

	/** indicates if all heights are positive or nil (lazy computation) */
	private final IStateBool positiveHeights;

	/** indicates if at least one regular tasks has a negative height (lazy computation) */
	private final IStateBool regularWithNegativeHeight;


	protected final int indexConsumption;

	protected final int indexCapacity;

	public AbstractCumulativeSConstraint(Solver solver, final String name, final TaskVar[] taskvars, final IntDomainVar[] heights,
                                         final IntDomainVar consumption, final IntDomainVar capacity,
                                         final IntDomainVar uppBound, final IntDomainVar... otherVars) {
		super(solver, name, taskvars, uppBound, ArrayUtils.append(heights,new IntDomainVar[]{consumption, capacity},otherVars));
		if(taskvars.length != heights.length) {
			throw new SolverException("tasks and heights array have different length.");
		}
		indexConsumption = taskIntVarOffset + heights.length;
		indexCapacity = indexConsumption + 1;
		IEnvironment env = solver.getEnvironment();
		fixedHeights = env.makeBool(false);
		positiveHeights = env.makeBool(false);
		regularWithNegativeHeight = env.makeBool(false);


	}
	
	@Override
	protected final int getHeightIndex(final int taskIdx) {
		return taskIntVarOffset + taskIdx;
	}

	public boolean updateMinCapacity(final int val) throws ContradictionException {
		return vars[indexCapacity].updateInf(val, cIndices[indexCapacity]);
	}

	public boolean updateMaxCapacity(final int val) throws ContradictionException {
		return vars[indexCapacity].updateSup(val, cIndices[indexCapacity]);
	}

	public boolean updateMinConsumption(final int val) throws ContradictionException {
		return vars[indexConsumption].updateInf(val, cIndices[indexConsumption]);
	}
	public boolean updateMaxConsumption(final int val) throws ContradictionException {
		return vars[indexConsumption].updateSup(val, cIndices[indexConsumption]);
	}


	@Override
	public final IntDomainVar getCapacity() {
		return getIntVar(indexCapacity);
	}

	@Override
	public final int getMaxCapacity() {
		return getCapacity().getSup();
	}


	@Override
	public final int getMinCapacity() {
		return getCapacity().getInf();
	}

	@Override
	public final IntDomainVar getConsumption() {
		return vars[indexConsumption];
	}

	@Override
	public final int getMaxConsumption() {
		return getConsumption().getSup();
	}

	@Override
	public final int getMinConsumption() {
		return getConsumption().getInf();
	}

	public final IntDomainVar getHeight(final int idx) {
		return getIntVar(getTaskIntVarOffset() + idx);
	}

	@Override
	public final boolean isInstantiatedHeights() {
		if( ! fixedHeights.get()) {
			for (int i = 0; i < getNbTasks(); i++) {
				final IRTask rtask = getRTask(i);
				if( !rtask.isEliminated() && !rtask.getHeight().isInstantiated()) {
					return false;
				}
			}
			fixedHeights.set(true);
		}
		return true;
	}



	@Override
	public final boolean hasOnlyPosisiveHeights() {
		if(! positiveHeights.get()) {
			if(regularWithNegativeHeight.get()) {return false;}
			else {
				for (int i = 0; i < getNbTasks(); i++) {
					final IRTask rtask = getRTask(i);
					if( !rtask.isEliminated() && rtask.getMinHeight() < 0) {
						if(rtask.isRegular()) {regularWithNegativeHeight.set(true);}
						return false;
					}
				}
				positiveHeights.set(true);
			}
		}
		return true;
	}
}
