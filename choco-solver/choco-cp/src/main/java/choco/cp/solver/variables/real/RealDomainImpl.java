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
package choco.cp.solver.variables.real;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateDouble;
import choco.kernel.solver.ContradictionException;
import static choco.kernel.solver.ContradictionException.Type.DOMAIN;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.real.RealDomain;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealVar;

/**
 * An implmentation of real variable domains using two stored floats for storing bounds.
 */
public class RealDomainImpl implements RealDomain {

	//public double width_zero = 1.e-8;
	//public double reduction_factor = 0.99;

    final PropagationEngine propagationEngine;

	/**
	 * for the delta domain: current value of the inf (domain lower bound) when the bound started beeing propagated
	 * (just to check that it does not change during the propagation phase)
	 */
	protected double currentInfPropagated = Double.NEGATIVE_INFINITY;

	/**
	 * for the delta domain: current value of the sup (domain upper bound) when the bound started beeing propagated
	 * (just to check that it does not change during the propagation phase)
	 */
	protected double currentSupPropagated = Double.POSITIVE_INFINITY;

	protected IStateDouble inf;

	protected IStateDouble sup;

	protected RealVar variable;

	public RealDomainImpl(RealVar v, double a, double b) {
		variable = v;
        propagationEngine = v.getSolver().getPropagationEngine();
		final IEnvironment env = v.getSolver().getEnvironment();
		inf = env.makeFloat(a);
		sup = env.makeFloat(b);
	}

	@Override
	public String toString() {
		return "[" +this.getInf() +", "+this.getSup()+"]";
	}

	public String pretty() {
		return this.toString();
	}

	public double getInf() {
		return inf.get();
	}

	public double getSup() {
		return sup.get();
	}

	public void intersect(RealInterval interval) throws ContradictionException {
		intersect(interval, VarEvent.NOCAUSE);
	}

	public void intersect(RealInterval interval, int index) throws ContradictionException {
		if ((interval.getInf() > this.getSup()) || (interval.getSup() < this.getInf())) {
			propagationEngine.raiseContradiction(this, DOMAIN);
		}

		double old_width = this.getSup() - this.getInf();
		double new_width = Math.min(interval.getSup(), this.getSup()) -
		Math.max(interval.getInf(), this.getInf());
		boolean toAwake = (variable.getSolver().getPrecision() / 100. <= old_width)
		&& (new_width < old_width * variable.getSolver().getReduction());

		if (interval.getInf() > this.getInf()) {
			if (toAwake) propagationEngine.postUpdateInf(variable, index);
			inf.set(interval.getInf());
		}

		if (interval.getSup() < this.getSup()) {
			if (toAwake) propagationEngine.postUpdateSup(variable, index);
			sup.set(interval.getSup());
		}
	}

	public void clearDeltaDomain() {
		currentInfPropagated = Double.NEGATIVE_INFINITY;
		currentSupPropagated = Double.POSITIVE_INFINITY;
	}

	public boolean releaseDeltaDomain() {
		boolean noNewUpdate = ((getInf() == currentInfPropagated) && (getSup() == currentSupPropagated));
		currentInfPropagated = Double.NEGATIVE_INFINITY;
		currentSupPropagated = Double.POSITIVE_INFINITY;
		return noNewUpdate;
	}

	public void freezeDeltaDomain() {
		currentInfPropagated = getInf();
		currentSupPropagated = getSup();
	}

	public boolean getReleasedDeltaDomain() {
		return true;
	}

	public void silentlyAssign(RealInterval i) {
		inf.set(i.getInf());
		sup.set(i.getSup());
	}
}
