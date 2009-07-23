package choco.cp.solver.search.limit;

import choco.kernel.solver.SolverException;

 /**
 * fast time limit computation inspired from: http://dow.ngra.de/2008/10/27/when-systemcurrenttimemillis-is-too-slow/.
 * @author Arnaud Malapert</br> 
 * @since 23 juil. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class TimeCacheThread extends Thread {

	public final static int CHOCO_MS_TIME_PRECISION = 100;
	
	public static volatile int elapsedTimeMillis = 0;
	
	public static volatile long startTimeMillis = System.currentTimeMillis();
	
	public static volatile long currentTimeMillis = System.currentTimeMillis();


	private TimeCacheThread() {
		super();
		setDaemon(true);
	}


	static {
		new TimeCacheThread().start();
	}

	public final static void reset() {
		startTimeMillis = currentTimeMillis;
		elapsedTimeMillis = 0;
	}

	@Override
	public void run() {
		while(true) {
			currentTimeMillis = System.currentTimeMillis();
			elapsedTimeMillis = (int) (currentTimeMillis - startTimeMillis);
			try {
				Thread.sleep(CHOCO_MS_TIME_PRECISION);
			} catch (InterruptedException e) {
				throw new SolverException("Time Limit Thread was interrupted");
			}
		}
	}


}