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
package choco.cp.solver.constraints.integer.bool.sum;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A special case of sums over boolean variables only
 */
public class AbstractBoolSum extends AbstractLargeIntSConstraint {

	protected final BoolSumStructure boolSumS;

	public AbstractBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(vars);
		this.boolSumS = new BoolSumStructure(environment, this, vars, bValue);
	}


	@Override
	public int getFilteredEventMask(int idx) {
		return IntVarEvent.INSTINTbitvector;
	}

	@Override
	public void propagate() throws ContradictionException {
		boolSumS.reset();
		for (int i = 0; i < vars.length; i++) {
			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
	}

	

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		final int val = vars[idx].getVal();
		if (val == 0) boolSumS.addZero();
		else boolSumS.addOne();
	}


}
