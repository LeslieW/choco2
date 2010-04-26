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
package common;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import org.junit.Test;
import samples.tutorials.scheduling.pert.DeterministicPert;
import scheduling.SchedUtilities;

import java.util.Random;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class TestPrecedences {

    public CPModel m;

    public CPSolver s;

    final static Random RANDOM = new Random();

    public void solve(int nbSol) {
        for (int i = 0; i < 5; i++) {
            long seed = RANDOM.nextLong();
            s = new CPSolver();
            s.read(m);
            s.setRandomSelectors(seed);
            SchedUtilities.solveRandom(s, nbSol, -1, "Pert");
        }
    }

    @Test
    public void testIlogExample1() {
        DeterministicPert ex = new DeterministicPert(17);
        m = ex.getModel();
        solve(0);
    }

    @Test
    public void testIlogExample2() {
        DeterministicPert ex = new DeterministicPert(17);

        ex = new DeterministicPert(18);
        m = ex.getModel();
        solve(154);

    }

    @Test
    public void testIlogExample3() {
        DeterministicPert ex = new DeterministicPert(17);

        ex = new DeterministicPert(19);
        m = ex.getModel();
        solve(1764);

    }

    @Test
    public void testIlogExample4() {
        DeterministicPert ex = new DeterministicPert(17);

        ex = new DeterministicPert(28);
        ex.requireUnaryResource();
        m = ex.getModel();
        solve(0);

    }

    @Test
    public void testIlogExample5() {
        DeterministicPert ex = new DeterministicPert(17);
        ex = new DeterministicPert(29);
        ex.requireUnaryResource();
        m = ex.getModel();
        solve(112);
    }
}
