/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  Â°(..)  |                           *
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
package choco.cp.solver.constraints.global.scheduling.trees.status;

import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;



/**
 * @author Arnaud Malapert</br> 
 * @since 10 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class ConsumptionStatus {

	protected long time;

	protected long consumption;

	public final long getTime() {
		return time;
	}

	public final void setTime(long time) {
		this.time = time;
	}

	public final long getConsumption() {
		return consumption;
	}

	public final void setConsumption(final long consumption) {
		this.consumption = consumption;
	}

	public void updateECT(final ConsumptionStatus left, final ConsumptionStatus right) {
		this.setTime(Math.max(right.getTime(), left.getTime() + right.getConsumption()));
		this.setConsumption(right.getConsumption() + left.getConsumption());
	}

	public void update(final TreeMode mode,ConsumptionStatus left, final ConsumptionStatus right ) {
		switch (mode) {
		case ECT: updateECT(left, right);break;
		default:
			throw new UnsupportedOperationException("unknown tree mode.");
		}
	}
}