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
package choco.model.constraints.reified;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 24 janv. 2008
 * Time: 16:21:59
 */
public class BinaryConjunctionTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();
    private CPModel m;
    private Solver s;
    private IntegerVariable x;

    @After
    public void tearDown() throws Exception {
        x = null;
        s = null;
        m = null;
    }

    @Before
    public void setUp() throws Exception {
        LOGGER.fine("choco.currentElement.reified.BinaryDisjunctionTest Testing...");
        m = new CPModel();
        s = new CPSolver();

    }

    @Test
    public void testBound(){
        LOGGER.info("BinaryConjunctionTest.testEnum");
        x = makeIntVar("X", 1, 10);
        m.addVariable("cp:bound", x);
        test();
    }

    @Test
    public void testEnum(){
        LOGGER.info("BinaryConjunctionTest.testBound");
        x = makeIntVar("X", 1, 10);
        test();
    }

     @Test
    public void testBoundDecomp(){
        LOGGER.info("BinaryConjunctionTest.testEnum");
        x = makeIntVar("X", 1, 10);
         m.addVariable("cp:bound", x);
         m.setDefaultExpressionDecomposition(true);
        test();
    }

    @Test
    public void testEnumDecomp(){
        LOGGER.info("BinaryConjunctionTest.testBound");
        x = makeIntVar("X", 1, 10);
        m.setDefaultExpressionDecomposition(true);
        test();
    }

    public void test() {
        //Constraint and = and(c0, c1);
	    Constraint and = and(geq((x),(3)),leq((x),(9)));
        LOGGER.info(and.pretty());
        m.addConstraint(and);
        s.read(m);
        LOGGER.info(s.getCstr(and).pretty());
	    //s.post(and);

        try {
            s.propagate();
        } catch (ContradictionException e) {
            LOGGER.severe("BinaryDisjunctionTest() : Test1#propagate() " + e.getMessage());
            fail();
        }
        s.solve();
        StringBuffer st = new StringBuffer();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertTrue("value of x not excepted", s.getVar(x).getVal() > 2 && s.getVar(x).getVal() < 10);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 7);
        LOGGER.info("OK");

    }
}