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
package choco.cp.solver.search;

import choco.kernel.solver.SolverException;

 /**
 * fast time limit computation inspired from: http://dow.ngra.de/2008/10/27/when-systemcurrenttimemillis-is-too-slow/.
 * cant use the heartbeat counter because it lascks of precision.
 * @author Arnaud Malapert</br> 
 * @since 23 juil. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class TimeCacheThread extends Thread {

	public final static int CHOCO_MS_TIME_PRECISION = 100;
		
	public static volatile long currentTimeMillis = System.currentTimeMillis();


	private TimeCacheThread() {
		super();
		setDaemon(true);
	}


	static {
		new TimeCacheThread().start();
	}

	@Override
	public void run() {
		while(true) {
			currentTimeMillis = System.currentTimeMillis();
			try {
				Thread.sleep(CHOCO_MS_TIME_PRECISION);
			} catch (InterruptedException e) {
				throw new SolverException("Time Limit Thread was interrupted");
			}
		}
	}


}