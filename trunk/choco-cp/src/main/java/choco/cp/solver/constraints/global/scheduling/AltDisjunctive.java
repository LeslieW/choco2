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
import static choco.cp.solver.SettingType.OVERLOAD_CHECKING;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class AltDisjunctive extends Disjunctive {

	protected final int nbRequired;
	
	public AltDisjunctive(final String name, final TaskVar[] taskvars,final IntDomainVar[] usages, final IntDomainVar makespan) {
		super(name, taskvars, makespan, usages);
		nbRequired = computeNbRequired();
		rules = new AltDisjRules(rtasks);
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
	protected final boolean hasOverloadChecking() {
		return flags.contains(OVERLOAD_CHECKING);
	}

	@Override
	public void awakeOnInst(final int idx) throws ContradictionException {
		if( idx < getTaskIntVarOffset()) {
			//TaskVar event
			rtasks[idx % getNbTasks()].updateCompulsoryPart();
		} else if(vars[idx].isInstantiatedTo(0) && idx!=indexUB) {
			//removal, update data structure
			final int taskIdx = idx - getTaskIntVarOffset() + nbRequired;
			rules.remove(rtasks[taskIdx]);
		}
		this.constAwake(false);
	}

	@Override
	protected final boolean isRegular(int[] tuple, int tidx) {
		return tuple[ getUsageIndex(tidx, nbRequired)] == 1;
	}
	
	
	

}
