/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.shaker;

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.limit.Limit;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.shaker.tools.search.IntBranchingFactory;
import org.junit.*;
import samples.Examples.PatternExample;
import samples.Examples.Queen;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class StrategyTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    CPSolver s;
    PatternExample pe;
    static Random random;
    boolean print=false;

    @Before
    public void before(){
        s = new CPSolver();
        loadModel();
    }

    private void loadModel() {
        pe = new Queen();
        pe.setUp(4);
		pe.buildModel();
		pe.buildSolver();
		pe._s.solveAll();
    }

    @After
    public void after(){
        s = null;
        pe=  null;
    }


    @Test
    @Ignore
    public void testStrategy() {
        LOGGER.setLevel(Level.FINEST);
        for(int i = 0; i < 100; i++){
            LOGGER.info("seed:"+i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe._m);
            s.setTimeLimit(1000);


            IntBranchingFactory bf = new IntBranchingFactory();
            List<IntDomainVar> vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars.toArray(new IntDomainVar[vars.size()])));
            checker();
        }

    }


    private void checker() {

        s.solveAll();
        Assert.assertFalse("encountered time limit", s.getLimitCount(Limit.TIME)>=s.getTimeCount());
        Assert.assertEquals("feasibility incoherence", pe._s.isFeasible(), s.isFeasible());
        Assert.assertEquals("nb sol incoherence", pe._s.getNbSolutions(), s.getNbSolutions());

    }

}