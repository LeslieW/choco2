package common;/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
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

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.kernel.solver.Configuration;
import org.junit.Assert;
import org.junit.Test;
import samples.tutorials.MinimumEdgeDeletion;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class SearchTest {

    private int capa = 0;

    class PoolSwitcher extends MinimumEdgeDeletion {

        @Override
        public void buildSolver() {
            _s = new CPSolver();
            _s.read(_m);
            Configuration configuration = _s.getConfiguration();
            configuration.putBoolean(Configuration.STOP_AT_FIRST_SOLUTION, false);
            configuration.putBoolean(Configuration.MAXIMIZE, false);
            configuration.putInt(Configuration.SOLUTION_POOL_CAPACITY, capa);
            _s.setValIntSelector(new MinVal());
            //_s.generateSearchStrategy();

        }

        @Override
        public void execute() {
            super.execute();
            Assert.assertEquals(Math.min(capa, _s.getNbSolutions()), _s.getSearchStrategy().getSolutionPool().size());
        }

    }

    @Test
    public void testSolutionPool() {
        //ChocoLogging.setVerbosity(Verbosity.SEARCH);
        PoolSwitcher pl = new PoolSwitcher();
        for (capa = 0; capa < 7; capa++) {
            pl.execute();
        }
        capa = Integer.MAX_VALUE;
        pl.execute();
    }

}
