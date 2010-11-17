/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
package choco.solver.search;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import static choco.Choco.*;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 6 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class ThreadSolverTest {

    private static class ThreadSolver extends Thread{

        public final CPSolver solver;
        /**
         * Allocates a new <code>Thread</code> object. This constructor has
         * the same effect as <code>Thread(null, null,</code>
         * <i>gname</i><code>)</code>, where <b><i>gname</i></b> is
         * a newly generated name. Automatically generated names are of the
         * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer.
         *
         * @see #Thread(ThreadGroup, Runnable, String)
         */
        private ThreadSolver(final CPModel model) {
            solver = new CPSolver();
            solver.read(model);
        }

        public CPSolver getSolver() {
            return solver;
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
            solver.solveAll();
        }
    }

    private CPModel m;
    private IntegerVariable[] queens;

    public void model(int n){
        m = new CPModel();
        queens = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = Choco.makeIntVar("Q" + i, 1, n);
        }
        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));
                m.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }
    }

    public static void main(String[] args){
        new ThreadSolverTest();
    }

    public ThreadSolverTest() {
//        ChocoLogging.setVerbosity(Verbosity.VERBOSE);
        ChocoLogging.setEveryXNodes(Integer.MAX_VALUE);
        int n = 14;
        model(n);
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        final ThreadSolver s1 = new ThreadSolver(m);
        final ThreadSolver s2 = new ThreadSolver(m);

        CPSolver ss1 = s1.getSolver();
        ss1.post(ss1.lt(ss1.getVar(queens[0]), (n/2+1)));

        CPSolver ss2 = s2.getSolver();
        ss2.post(ss2.geq(ss2.getVar(queens[0]), (n/2+1)));

        s1.start();
        s2.start();
    }

}
