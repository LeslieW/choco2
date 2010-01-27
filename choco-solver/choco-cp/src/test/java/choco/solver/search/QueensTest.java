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
package choco.solver.search;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valselector.MaxVal;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/* File choco.currentElement.search.QueensTest.java, last modified by flaburthe 12 janv. 2004 18:03:29 */

/**
 * A currentElement placing n-queens on a chessboard, so that no two attack each other
 */
public class QueensTest {
    public final static int NB_QUEENS_SOLUTION[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};
    public static final boolean LINKED = false;
    private final static Logger LOGGER = ChocoLogging.getTestLogger();
    public Model m;
    public Solver s1;
    public Solver s2;
    private IntegerVariable[] queens;

    @Before
    public void setUp() {
        LOGGER.fine("Queens Testing...");
        m = new CPModel();
        s1 = new CPSolver();
        s2 = new CPSolver();
    }

    @After
    public void tearDown() {
        m = null;
        s1 = s2 =null;
        queens = null;
    }

    private IntegerVariable createVar(String name, int min, int max) {
        if (LINKED) return makeIntVar(name, min, max, "cp:link");
        return makeIntVar(name, min, max);
    }

    public void model(int n){
        queens = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = createVar("Q" + i, 1, n);
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

  
    public void incrementalSolve(Solver solver, int n) {
    	solver.solve();
        do{
            Assert.assertTrue("Not a solution", solver.checkSolution());
        }while(Boolean.TRUE.equals(solver.nextSolution()));
        solver.printRuntimeStatistics();
        assertEquals(Boolean.valueOf( NB_QUEENS_SOLUTION[n] > 0), solver.isFeasible());
        assertEquals(NB_QUEENS_SOLUTION[n], solver.getNbSolutions());
    }
    
    public void solve(int n){
        s1.read(m);
        s1.setValIntIterator(new DecreasingDomain()); 
        incrementalSolve(s1, n);
        
        s2.read(m);
        s2.setValIntSelector(new MaxVal()); 
        incrementalSolve(s2, n);

        Assert.assertEquals(s1.getSolutionCount(), s2.getSolutionCount());
    }

    public void queen0(int n) {
        LOGGER.finer("n queens, binary model, n=" + n);
        model(n);
        solve(n);
    }

    @Test
    public void test0() {
        queen0(4);
    }

    @Test
    public void test1() {
        queen0(5);
    }

    @Test
    public void test2() {
        queen0(6);
    }

    @Test
    public void test3() {
        queen0(7);
    }

    @Test
    public void test4() {
        queen0(8);
    }

    @Test
    public void test5() {
        queen0(9);
    }

    @Test
    public void test6() {
        queen0(10);
    }

    @Test
    public void test7() {
        queen0(11);
    }

}