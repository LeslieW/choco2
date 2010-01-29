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
 * @author abadr
 * 
 */
public class ThetaOmegaStatus extends ThetaLambdaStatus {
	
	protected int tOTime; //Contains ECT(Theta,Omega) of subtree

	protected int tODuration; //Contains Total Duration of Theta-Omega subtree.

	protected Object respTOTime; //Optional activity in Omega responsible for highest ECT. 

	protected Object respTODuration; //Optional activity in Omega responsible for highest Duration.

	public int getTOTime() {
		return tOTime;
	}

	public void setTOTime(int time) {
		tOTime = time;
	}

	public int getTODuration() {
		return tODuration;
	}

	public void setTODuration(int duration) {
		tODuration = duration;
	}

	public Object getRespTOTime() {
		return respTOTime;
	}

	public void setRespTOTime(Object respTOTime) {
		this.respTOTime = respTOTime;
	}

	public Object getRespTODuration() {
		return respTODuration;
	}

	public void setRespTODuration(Object respTODuration) {
		this.respTODuration = respTODuration;
	}
	/**
	 * Updating the total duration for Theta-Omega in internal/root node
	 * @param left: left Theta-Omega subtree
	 * @param right: right Theta-Omega subtree
	 */
	protected void updateTODuration(ThetaOmegaStatus left, ThetaOmegaStatus right) {
		final int l=left.getTODuration() +right.getDuration(); //optional task in the left hand side of the tree
		final int r=left.getDuration()+right.getTODuration();//optional task in the right hand side of the tree
		if(l>=r) {
			setRespTODuration(left.getRespTODuration());
			setTODuration(l);
		}else {
			setRespTODuration(right.getRespTODuration());
			setTODuration(r);
		}
	}
	/**
	 * Updating Earliest Completion Time for Theta-Omega in internal/root node
	 * @param left: Left Theta-Omega Subtree
	 * @param right: Right Theta-Omega Subtree
	 */
	public void updateTOECT(ThetaOmegaStatus left, ThetaOmegaStatus right) {
		final int l=right.getTOTime();
		final int m=left.getTime()+right.getTODuration();
		final int r=left.getTOTime()+right.getDuration();
		if(l>=m && l>= r) {
			setRespTOTime(right.getRespTOTime());
			setTOTime(l);
		}else if(m>=r) {
			setRespTOTime(right.getRespTODuration());
			setTOTime(m);
		}else {
			setRespTOTime(left.getRespTOTime());
			setTOTime(r);
		}
	}
	
	public void updateTOLST(ThetaOmegaStatus left, ThetaOmegaStatus right) {
		final int l=left.getTOTime();
		final int m=right.getTime() - left.getTODuration();
		final int r=right.getTOTime() -left.getDuration();
		if(l<=m && l<= r) {
			setRespTOTime(left.getRespTOTime());
			setTOTime(l);
		}else if(m<=r) {
			setRespTOTime(left.getRespTODuration());
			setTOTime(m);
		}else {
			setRespTOTime(right.getRespTOTime());
			setTOTime(r);
		}
	}
	public void update(TreeMode mode,ThetaOmegaStatus left, ThetaOmegaStatus right ) {
		super.update(mode, left, right);
		switch (mode) {
		case ECT: updateTOECT(left, right);updateTODuration(left, right);break;
		case LST: updateTOLST(left, right);updateTODuration(left, right);break;
		default:
			throw new UnsupportedOperationException("unknown tree mode.");
		}
	}

}
