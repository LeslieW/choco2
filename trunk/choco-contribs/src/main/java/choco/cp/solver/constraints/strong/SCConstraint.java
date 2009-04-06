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
package choco.cp.solver.constraints.strong;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Map;

/**
 * This class provides a skeletal implementation of the MyConstraint interface,
 * to minimize the effort required to implement this interface.
 * 
 * @author vion
 */
public class SCConstraint {
	/**
	 * Enclosed Choco Solver constraint
	 */
	private final ISpecializedConstraint sConstraint;

	/**
	 * Scope of the constraint
	 */
	protected final SCVariable[] scope;

	private int weight = 1;

	/**
	 * @param sConstraint
	 *            Contrainte encapsulée
	 * @param pool
	 *            Map de contraintes entre IntDomainVar et MyVariable pour faire
	 *            la correspondance dans MyConstraint
	 */
	public SCConstraint(ISpecializedConstraint sConstraint,
			Map<IntDomainVar, SCVariable> pool) {
		this.sConstraint = sConstraint;

		scope = new SCVariable[2];

		for (int i = 2; --i >= 0;) {
			scope[i] = pool.get(sConstraint.getVar(i));
			scope[i].addConstraint(this);
		}
	}

	public final boolean check(int[] tuple) {
		return sConstraint.check(tuple);
	}

	public int firstSupport(int position, int value) {
		return sConstraint.firstSupport(position, value);
	}

	public int nextSupport(int position, int value, int lastSupport) {
		return sConstraint.nextSupport(position, value, lastSupport);
	}

	public SCVariable<? extends SCConstraint> getVariable(int position) {
		return scope[position];
	}

	public int getArity() {
		return 2;
	}

	@Override
	public String toString() {
		return "sc" + sConstraint;
	}

	public int getWeight() {
		return weight;
	}

	public void increaseWeight() {
		weight++;

	}

	public SConstraint getSConstraint() {
		return sConstraint;
	}
}