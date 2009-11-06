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
package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import static choco.model.constraints.integer.MaxTest.testAll;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import static java.text.MessageFormat.format;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 30 janv. 2007
 * Time: 10:49:34
 * To change this template use File | Settings | File Templates.
 */
public class MinTest{

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    private CPModel m;
    private CPSolver s;

    @Before
    public void before(){
        m = new CPModel();
        s = new CPSolver();
    }

    @After
    public void after(){
        m = null;
        s = null;
    }

    @Test
    public void test1() {
        for (int i = 0; i <= 10; i++) {
            m = new CPModel();
            s = new CPSolver();
            IntegerVariable x = makeIntVar("x", 1, 5);
            IntegerVariable y = makeIntVar("y", 1, 5);
            IntegerVariable z = makeIntVar("z", 1, 5);
            IntegerVariable w = makeIntVar("w", 1, 5);
            m.addConstraint(min(new IntegerVariable[]{x, y, z},w));
            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i + 1));
            s.read(m);
            s.solve();
            do {
                /*LOGGER.info("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
            } while (s.nextSolution() == Boolean.TRUE);
            LOGGER.info("" + s.getSearchStrategy().getNodeCount());
            assertEquals(125, s.getNbSolutions());
            //LOGGER.info("Nb solution : " + s.getNbSolutions());
        }
    }

    @Test
    public void test2() {
        for (int i = 0; i <= 10; i++) {
            m = new CPModel();
            s = new CPSolver();
            IntegerVariable x = makeIntVar("x", 1, 5);
            IntegerVariable y = makeIntVar("y", 1, 5);
            IntegerVariable z = makeIntVar("z", 1, 5);
            m.addVariables("cp:bound", x, y, z);
            IntegerVariable w = makeIntVar("w", 1, 5);
            m.addConstraint(min(new IntegerVariable[]{x, y, z},w));
            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i + 1));
            s.read(m);
            s.solve();
            do {
                //LOGGER.info("" + x.getVal() + "=max(" + y.getVal() + "," +
                //�    z.getVal()+")");
            } while (s.nextSolution() == Boolean.TRUE);
            LOGGER.info("" + s.getSearchStrategy().getNodeCount());
            assertEquals(125, s.getNbSolutions());
            //LOGGER.info("Nb solution : " + s.getNbSolutions());
        }
    }

    @Test
    public void testPropagMinTern1() {

        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 4, 5);
        IntegerVariable min = makeIntVar("min", 1, 5);
        m.addConstraint(min(z, y, min));
        s.read(m);
        try {
            s.getVar(min).remVal(3);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        LOGGER.info(format("min {0}", s.getVar(min).getDomain().pretty()));
        LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
        LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
        LOGGER.info(format("{0}", !s.getVar(y).canBeInstantiatedTo(3)));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(3));
    }

    @Test
    public void testPropagMinTern2() {

        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 1, 5);
        IntegerVariable min = makeIntVar("min", 1, 5);
        m.addConstraint(min(z, y, min));
        s.read(m);
        try {
            s.getVar(y).remVal(3);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        LOGGER.info(format("min {0}", s.getVar(min).getDomain().pretty()));
        LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
        LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
        LOGGER.info(format("{0}", s.getVar(z).canBeInstantiatedTo(3) && s.getVar(min).canBeInstantiatedTo(3)));
        assertTrue(s.getVar(z).canBeInstantiatedTo(3) && s.getVar(min).canBeInstantiatedTo(3));
    }

    @Test
    public void testPropagMinTern3() {
        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 1, 5);
        IntegerVariable min = makeIntVar("min", 1, 5);
        m.addConstraint(min(z, y, min));
        s.read(m);
        try {
            s.getVar(min).remVal(3);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        LOGGER.info(format("min {0}", s.getVar(min).getDomain().pretty()));
        LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
        LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
        LOGGER.info(format("{0}", s.getVar(y).canBeInstantiatedTo(3) && s.getVar(z).canBeInstantiatedTo(3)));
        assertTrue(s.getVar(y).canBeInstantiatedTo(3) && s.getVar(z).canBeInstantiatedTo(3));
    }

    @Test
    public void testPropagMinTern4() {
        IntegerVariable y = makeIntVar("y", 1, 3);
        IntegerVariable z = makeIntVar("z", 4, 6);
        IntegerVariable min = makeIntVar("min", 1, 6);
        m.addConstraint(min(z, y, min));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        LOGGER.info(format("min {0}", s.getVar(min).getDomain().pretty()));
        LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
        LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
        LOGGER.info(format("{0}", s.getVar(min).getDomain().getSize() == 3));
        assertTrue(s.getVar(min).getDomain().getSize() == 3);
    }


    @Test
    public void testRandom() {

        for (int i = 0; i < 10; i++) {

            m = new CPModel();
            s = new CPSolver();
            IntegerVariable varA = makeIntVar("varA", 0, 3);
            IntegerVariable varB = makeIntVar("varB", 0, 3);
            IntegerVariable varC = makeIntVar("varC", 0, 3);
            m.addConstraint(min(varA, varB, varC));
            s.read(m);

            //-----Now get solutions
            s.setFirstSolution(true);
            s.generateSearchStrategy();
            s.setValIntSelector(new RandomIntValSelector(100 + i));
            s.setVarIntSelector(new RandomIntVarSelector(s, 101 + i));

            //LOGGER.info("Choco Solutions");
            int nbSolution = 0;
            if (s.solve() == Boolean.TRUE) {
                do {
                    //LOGGER.info("Min(" + ((IntegerVariable) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntegerVariable) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntegerVariable) chocoCSP.getIntVar(2)).getVal());
                    nbSolution++;
                } while (s.nextSolution() == Boolean.TRUE);
            }
            assertEquals(nbSolution, 16);
        }
    }

    @Test
    public void testConstant(){
        for (int i = 0; i < 10; i++) {

            m = new CPModel();
            s= new CPSolver();


            IntegerVariable x = makeIntVar("x", 0, 3);
            IntegerVariable y = makeIntVar("y", 0, 3);
            m.addConstraint(eq(y, min(x, 1)));

            //-----Now get solutions

            s.read(m);
            s.setFirstSolution(true);
            s.generateSearchStrategy();
            s.setValIntSelector(new RandomIntValSelector(100 + i));
            s.setVarIntSelector(new RandomIntVarSelector(s, i));


            //LOGGER.info("Choco Solutions");
            int nbSolution = 0;
            if (s.solve() == Boolean.TRUE) {
                do {
                    //LOGGER.info("Max(" + ((IntegerVariable) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntegerVariable) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntegerVariable) chocoCSP.getIntVar(2)).getVal());
                    nbSolution++;
                } while (s.nextSolution() == Boolean.TRUE);
            }

            assertEquals(nbSolution, 4);
        }
    }
    @Test
	public void testSet1() {
		testAll(true,true);
	}

	@Test
	public void testSet2() {
		testAll(true,false);
	}

    @Test
    public void testOneVarMin() {
        IntegerVariable[] vars = makeIntVarArray("vars", 1, 3, 5);
        IntegerVariable min = makeIntVar("min", 1, 6);
        m.addConstraint(eq(min, min(vars)));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        LOGGER.info("min " + s.getVar(min).getDomain().pretty());
        assertTrue(s.getVar(min).getDomain().getSize() == 3);
    }



}
