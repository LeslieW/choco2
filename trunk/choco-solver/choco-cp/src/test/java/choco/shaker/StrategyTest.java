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
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valselector.MidVal;
import choco.cp.solver.search.integer.varselector.DomOverWDeg;
import choco.cp.solver.search.integer.varselector.MaxValueDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.shaker.tools.search.IntBranchingFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import samples.Examples.GolombRuler;
import samples.Examples.MagicSquare;
import samples.Examples.PatternExample;
import samples.Examples.Queen;

import java.util.List;
import java.util.Random;
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
    }

    private void createModel(){
        pe.buildModel();
        pe._s = new CPSolver();
		pe._s.read(pe._m);
        pe._s.solveAll();
    }

    private void loadQueenModel() {
        pe = new Queen();
        pe.setUp(8);
        createModel();
    }

    private void loadMagicSquareModel() {
        pe = new MagicSquare();
        pe.setUp(3);
        createModel();
    }

    private void loadGolombRulerModel() {
        pe = new GolombRuler();
        pe.setUp(new Object[]{6,18, true});
        createModel();
    }

    @After
    public void after(){
        s = null;
        pe=  null;
    }


    @Test
    public void testStrategyQ() {
        loadQueenModel();
        for(int i = 0; i < 100; i++){
            LOGGER.info("seed:"+i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe._m);
            IntBranchingFactory bf = new IntBranchingFactory();
            List<IntDomainVar> vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars.toArray(new IntDomainVar[vars.size()])));
            checker();
        }

    }

    @Test
    public void testStrategyMS() {
        loadMagicSquareModel();
        for(int i = 0; i < 100; i++){
            LOGGER.info("seed:"+i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe._m);
            IntBranchingFactory bf = new IntBranchingFactory();
            List<IntDomainVar> vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars.toArray(new IntDomainVar[vars.size()])));
            checker();
        }

    }

    @Test
    public void testStrategyGR() {
        loadGolombRulerModel();
        for(int i = 0; i < 100; i++){
            LOGGER.info("seed:"+i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe._m);
            IntBranchingFactory bf = new IntBranchingFactory();
            List<IntDomainVar> vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars.toArray(new IntDomainVar[vars.size()])));
            checker();
        }

    }

    private void checker() {
    	s.solveAll(); //checkSolution enbaled by assertion
//        s.solve();
//        if(Boolean.TRUE.equals(s.isFeasible())){
//            do{
//                Assert.assertTrue(s.checkSolution());
//            }while(s.nextSolution());
//        }
        Assert.assertEquals("feasibility incoherence", pe._s.isFeasible(), s.isFeasible());
        Assert.assertEquals("nb sol incoherence", pe._s.getNbSolutions(), s.getNbSolutions());

    }

    @Test
    public void testStrategy1() {
        loadQueenModel();
        s = new CPSolver();
        s.read(pe._m);
        s.attachGoal(new AssignVar(new DomOverWDeg(s), new MidVal()));
        checker();
    }

    @Test
    public void testStrategy2() {
        loadGolombRulerModel();
        s = new CPSolver();
        s.read(pe._m);
        s.attachGoal(new AssignVar(new MaxValueDomain(s), new DecreasingDomain()));
        checker();
    }

}