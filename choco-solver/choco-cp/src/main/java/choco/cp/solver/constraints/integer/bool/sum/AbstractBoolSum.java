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

	/**
	 * The number of variables instantiated to zero in the sum
	 */
	protected final IStateInt nbz;

	/**
	 * The number of variables instantiated to one in the sum
	 */
	protected final IStateInt nbo;

	protected final int gap;

	protected final int bValue;

	public AbstractBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(vars);
		this.bValue = bValue;
		this.gap = vars.length - bValue;
		nbz = environment.makeInt(0);
		nbo = environment.makeInt(0);
	}


	@Override
	public int getFilteredEventMask(int idx) {
		return IntVarEvent.INSTINTbitvector;
	}

	@Override
	public void propagate() throws ContradictionException {
		nbz.set(0);
		nbo.set(0);
		for (int i = 0; i < vars.length; i++) {
			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
	}

	public final void putAllZero() throws ContradictionException {
		for (int i = 0; i < vars.length; i++) {
			if (!vars[i].isInstantiated())
				vars[i].instantiate(0, cIndices[i]);
		}
	}

	public final void putAllOne() throws ContradictionException {
		for (int i = 0; i < vars.length; i++) {
			if (!vars[i].isInstantiated())
				vars[i].instantiate(1, cIndices[i]);
		}
	}


	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		final int val = vars[idx].getVal();
		if (val == 0) nbz.add(1);
		else nbo.add(1);
	}

	/**
	 * Computes an upper bound estimate of a linear combination of variables.
	 *
	 * @return the new upper bound value
	 */
	protected final int computeUbFromScratch() {
		int s = 0;
		for (int i = 0; i < vars.length; i++) {
			s += vars[i].getSup();
		}
		return s;
	}

	/**
	 * Computes a lower bound estimate of a linear combination of variables.
	 *
	 * @return the new lower bound value
	 */
	protected final int computeLbFromScratch() {
		int s = 0;
		for (int i = 0; i < vars.length; i++) {
			s += vars[i].getInf();
		}
		return s;
	}



}
