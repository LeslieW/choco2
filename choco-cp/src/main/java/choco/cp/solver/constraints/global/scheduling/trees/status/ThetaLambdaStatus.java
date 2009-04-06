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
public class ThetaLambdaStatus extends ThetaStatus {

	protected int grayTime;

	protected int grayDuration;

	protected Object respGrayTime;

	protected Object respGrayDuration;



	public final int getGrayTime() {
		return grayTime;
	}

	public final void setGrayTime(int grayTime) {
		this.grayTime = grayTime;
	}

	public final int getGrayDuration() {
		return grayDuration;
	}

	public final void setGrayDuration(int grayDuration) {
		this.grayDuration = grayDuration;
	}

	public final Object getRespGrayTime() {
		return respGrayTime;
	}

	public final Object getRespGrayDuration() {
		return respGrayDuration;
	}

	public final void setRespGrayTime(Object respGrayTime) {
		this.respGrayTime = respGrayTime;
	}

	public final void setRespGrayDuration(Object respGrayDuration) {
		this.respGrayDuration = respGrayDuration;
	}

	protected void updateGrayDuration(ThetaLambdaStatus left, ThetaLambdaStatus right) {
		final int l=left.getGrayDuration() +right.getDuration();
		final int r=left.getDuration()+right.getGrayDuration();
		if(l>=r) {
			setRespGrayDuration(left.getRespGrayDuration());
			setGrayDuration(l);
		}else {
			setRespGrayDuration(right.getRespGrayDuration());
			setGrayDuration(r);
		}
	}

	public void updateGrayECT(ThetaLambdaStatus left, ThetaLambdaStatus right) {
		final int l=right.getGrayTime();
		final int m=left.getTime()+right.getGrayDuration();
		final int r=left.getGrayTime()+right.getDuration();
		if(l>=m && l>= r) {
			setRespGrayTime(right.getRespGrayTime());
			setGrayTime(l);
		}else if(m>=r) {
			setRespGrayTime(right.getRespGrayDuration());
			setGrayTime(m);
		}else {
			setRespGrayTime(left.getRespGrayTime());
			setGrayTime(r);
		}
	}

	public void updateGrayLST(ThetaLambdaStatus left, ThetaLambdaStatus right) {
		final int l=left.getGrayTime();
		final int m=right.getTime() - left.getGrayDuration();
		final int r=right.getGrayTime() -left.getDuration();
		if(l<=m && l<= r) {
			setRespGrayTime(left.getRespGrayTime());
			setGrayTime(l);
		}else if(m<=r) {
			setRespGrayTime(left.getRespGrayDuration());
			setGrayTime(m);
		}else {
			setRespGrayTime(right.getRespGrayTime());
			setGrayTime(r);
		}
	}

	public void update(TreeMode mode,ThetaLambdaStatus left, ThetaLambdaStatus right ) {
		super.update(mode, left, right);
		switch (mode) {
		case ECT: updateGrayECT(left, right);updateGrayDuration(left, right);break;
		case LST: updateGrayLST(left, right);updateGrayDuration(left, right);break;
		default:
			throw new UnsupportedOperationException("unknown tree mode.");
		}
	}

}