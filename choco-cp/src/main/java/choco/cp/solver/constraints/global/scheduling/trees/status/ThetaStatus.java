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
public class ThetaStatus {

	protected int time;

	protected int duration;


	public final int getTime() {
		return time;
	}


	public final void setTime(int time) {
		this.time = time;
	}

	public final int getDuration() {
		return duration;
	}


	public final void setDuration(int duration) {
		this.duration = duration;
	}

	protected void updateDuration(ThetaStatus lcs, ThetaStatus rcs) {
		setDuration(lcs.getDuration() + rcs.getDuration());
	}

	public void updateECT(ThetaStatus lcs, ThetaStatus rcs) {
		setTime( Math.max(rcs.getTime() ,lcs.getTime()+rcs.getDuration()));
		updateDuration(lcs, rcs);
	}

	public void updateLST(ThetaStatus lcs, ThetaStatus rcs) {
		setTime( Math.min(lcs.getTime(),rcs.getTime() - lcs.getDuration()));
		updateDuration(lcs, rcs);
	}

	public void update(TreeMode mode,ThetaStatus left, ThetaStatus right ) {
		switch (mode) {
		case ECT: updateECT(left, right);updateDuration(left, right);break;
		case LST: updateLST(left, right);updateDuration(left, right);break;
		default:
			throw new UnsupportedOperationException("unknown tree mode.");
		}
	}
}