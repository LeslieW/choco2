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


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint Sigma (ai Xi) <=/>=/= C,
 * with Xi variables, ai and C constants.
 */
public final class IntLinComb2 extends AbstractIntLinComb {

	
	public IntLinComb2(IntDomainVar[] lvars, int[] coeffs, int nbPosVars,
			int cste) {
		super(lvars, coeffs, nbPosVars, cste);
	}

	@Override
	protected int getInfNV(final int i, final int mylb) {
		return MathUtils.divCeil(mylb, -coeffs[i]) + vars[i].getSup();
	}

	@Override
	protected int getSupNV(final int i, final int myub) {
		return MathUtils.divFloor(myub, -coeffs[i]) + vars[i].getInf();
	}

	@Override
	protected int getInfPV(final int i, final int myub) {
		return MathUtils.divCeil(-myub, coeffs[i]) + vars[i].getSup();
	}

	@Override
	protected int getSupPV(final int i, final int mylb) {
		return MathUtils.divFloor(-mylb, coeffs[i]) + vars[i].getInf();
	}

	//*****************************************************************//
	//*******************  BOUNDING  *********************************//
	//***************************************************************//


	@Override
	public int computeLowerBound() {
		int lb = cste;
		for (int i = 0; i < nbPosVars; i++) {
			lb += coeffs[i] * vars[i].getInf();
		}
		for (int i = nbPosVars; i < vars.length; i++) {
			lb += coeffs[i] * vars[i].getSup();
		}
		return lb;
	}


	@Override
	public int computeUpperBound() {
		int ub = cste;
		for (int i = 0; i < nbPosVars; i++) {
			ub += coeffs[i] * vars[i].getSup();
		}
		for (int i = nbPosVars; i < vars.length; i++) {
			ub += coeffs[i] * vars[i].getInf();
		}
		return ub;
	}

}

