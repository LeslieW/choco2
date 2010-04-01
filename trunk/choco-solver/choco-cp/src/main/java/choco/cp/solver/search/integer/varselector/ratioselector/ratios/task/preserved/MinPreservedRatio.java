/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved;

import java.util.Random;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.AbstractPrecedenceRatio;
import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * Must handle properly integer overflow.
 * @author Arnaud Malapert</br> 
 * @since 26 mars 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public class MinPreservedRatio extends AbstractPrecedenceRatio {

	public final static int NULL = -1;
	
	private final static int DIVISOR = 1 << 10;
		
	private int dividend;
	
	public MinPreservedRatio(IPrecedence precedence) {
		super(precedence);
	}

	@Override
	public int getDividend() {
		return dividend;
	}

	@Override
	public int getDivisor() {
		return DIVISOR;
	}


	public final int getBestVal(Random randomBreakTie) {
		return getBestVal(randomBreakTie, precedence.getOrigin(), precedence.getDestination());
	}

	public final int getBestVal(Random randomBreakTie, TaskVar t1, TaskVar t2) {
		final double leftM = TaskUtils.getPreserved(t1, t2);
		final double rightM = TaskUtils.getPreserved(t2, t1);
		if( isUp(leftM, rightM) ) return 1;
		else if(leftM == rightM) return randomBreakTie.nextBoolean() ? 1 : 0;
		else return 0;
	}

	protected boolean isUp(double leftM, double rightM) {
		return leftM <= rightM;
	}
	
	@Override
	public final boolean isActive() {
		if(precedence.getBoolVar().isInstantiated()) return false;
		else {
			final TaskVar t1 = precedence.getOrigin();
			final TaskVar t2 = precedence.getDestination();
			final double leftM = TaskUtils.getPreserved(t1 , t2);
			final double rightM = TaskUtils.getPreserved(t2, t1);
			if( isUp(leftM, rightM) ) {
				dividend=  (int) (leftM * DIVISOR) ;
			}else {
				dividend=  (int) (rightM * DIVISOR) ;
			}
			return true;
		}
	}
}