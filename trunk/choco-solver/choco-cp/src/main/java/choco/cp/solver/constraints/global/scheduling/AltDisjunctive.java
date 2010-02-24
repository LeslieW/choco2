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
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class AltDisjunctive extends Disjunctive {

	protected final int nbRequired;
	
	public AltDisjunctive(final String name, final TaskVar[] taskvars, final IntDomainVar[] usages, final IntDomainVar makespan, Solver solver) {
		super(solver, name, taskvars, makespan, usages);
		nbRequired = computeNbRequired();
		final int n = getNbTasks();
		final IEnvironment env = solver.getEnvironment();
		//Aliaa plug-in the new Data Structure with hypothetical domain
		for (int i = nbRequired; i < n; i++) {
			rtasks[i] = new HRTask(i, env);
		}
		rules = new AltDisjRules(rtasks, env);
	}

	
	@Override
	public int getFilteredEventMask(int idx) {
		return idx < taskIntVarOffset ? EVENT_MASK : IntVarEvent.INSTINTbitvector;
	}

	@Override
	protected final int getUsageIndex(final int taskIdx) {
		return  getUsageIndex(taskIdx, computeNbRequired());
	}

	private final int computeNbRequired() {
		return 4 * taskvars.length + 2 - vars.length;
	}
	
	private final int getUsageIndex(int tidx, int nbRequired) {
		return  tidx < nbRequired ? indexUnit : taskIntVarOffset + tidx - nbRequired;
	}

	@Override
	protected void fireTaskRemoval(IRTask rtask) {
		rules.remove(rtask);
	}


	@Override
	public void awakeOnInst(final int idx) throws ContradictionException {
		if( ! checkTask(idx) && 
				idx!=indexUB && // => usage variable
				vars[idx].isInstantiatedTo(0)) {
			//removal, update data structure
			final int taskIdx = idx - getTaskIntVarOffset() + nbRequired;
			fireTaskRemoval(rtasks[taskIdx]);
		}
		this.constAwake(false);
	}

	@Override
	protected final boolean isRegular(int[] tuple, int tidx) {
		return tuple[ getUsageIndex(tidx, nbRequired)] == 1;
	}
	
	
	

}
