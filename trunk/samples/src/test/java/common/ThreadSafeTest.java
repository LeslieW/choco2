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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package common;

import choco.kernel.common.logging.ChocoLogging;
import junit.framework.Assert;
import org.junit.Test;
import samples.tutorials.CycloHexan;
import samples.tutorials.PatternExample;
import samples.tutorials.Queen;
import samples.tutorials.SteinerSystem;

import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 12 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class ThreadSafeTest {

    private static final Logger LOGGER  = ChocoLogging.getTestLogger();

    private static class ThreadProblem extends Thread {
        private final PatternExample toRun;

        public ThreadProblem(final PatternExample example) {
            this.toRun = example;
        }

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p/>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see #start()
         * @see #stop()
         * @see #Thread(ThreadGroup, Runnable, String)
         */
        @Override
        public void run() {
		    toRun.execute();
            Assert.assertTrue(toRun._s.isFeasible());
        }
    }

    public ThreadSafeTest() throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            new ThreadProblem(new Queen()).start();
            Thread.sleep(2);
            new ThreadProblem(new SteinerSystem()).start();
            Thread.sleep(1);
            new ThreadProblem(new CycloHexan()).start();
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        ChocoLogging.setVerbosity(Verbosity.VERBOSE);
        new ThreadSafeTest();
    }

    @Test
    public void test(){
        // for test to run
        Assert.assertTrue(true);
    }

}
