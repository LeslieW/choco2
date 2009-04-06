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
package choco.model.constraints.set;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.*;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BasicConstraintsTest {
    private Logger logger = Logger.getLogger("choco.currentElement");
    private Model m;
    private Solver s;
    private SetVariable x;
    private SetVariable y;
    private SetVariable z;
    private IntegerVariable iv;
    private Constraint c1;
    private Constraint c2;
    private Constraint c3;
    private Constraint c4;

    @Before
    public void setUp() {
        logger.fine("EqualXC Testing...");
        m = new CPModel();
        s = new CPSolver();
        x = makeSetVar("X", 1, 5);
        y = makeSetVar("Y", 1, 5);
        z = makeSetVar("Z", 2, 3);
    }

    @After
    public void tearDown() {
        c1 = null;
        c2 = null;
        c3 = null;
        c4 = null;
        x = null;
        y = null;
        z = null;
        iv = null;
        m = null;
        s = null;
    }

    /**
     * Test MemberX - NotMemberX
     */
    @Test
    public void test1() {
        logger.finer("test1");
        c1 = member(x, 3);
        c2 = member(x, 5);
        c3 = notMember(x, 2);
        try {
            m.addConstraint(c1);
            m.addConstraint(c2);
            m.addConstraint(c3);
            s.read(m);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        assertTrue(s.getVar(x).isInDomainKernel(3));
        assertTrue(s.getVar(x).isInDomainKernel(5));
        assertTrue(!s.getVar(x).isInDomainKernel(2));
        System.out.println("[BasicConstraintTests,test1] x : " + x.pretty());
        logger.finest("domains OK after first propagate");
    }

    /**
     * Test MemberXY
     */
    @Test
    public void test2() {
        logger.finer("test2");
        iv = makeIntVar("iv", 1, 5);
        c1 = member(x, 3);
        c2 = member(x, 5);
        c3 = notMember(x, 2);
        c4 = member(x, iv);
        try {
            m.addConstraint(c4);
            m.addConstraint(c2);
            m.addConstraint(c3);
            m.addConstraint(c1);
            s.read(m);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        assertTrue(!s.getVar(iv).canBeInstantiatedTo(2));
        assertTrue(s.getVar(x).isInDomainKernel(3));
        assertTrue(s.getVar(x).isInDomainKernel(5));
        assertTrue(!s.getVar(x).isInDomainKernel(2));
        System.out.println("[BasicConstraintTests,test2] x : " + x.pretty());
        System.out.println("[BasicConstraintTests,test2] iv : " + iv.pretty());
        logger.finest("domains OK after first propagate");
        s.setFirstSolution(false);
        s.generateSearchStrategy();
        s.addGoal(new AssignSetVar(new MinDomSet(s), new MinEnv(s)));
        s.launch();

        assertEquals(12, s.getNbSolutions());
    }

    /**
     * Test NotMemberXY
     */
    @Test
    public void test3() {
        logger.finer("test3");
        iv = makeIntVar("iv", 1, 5);
        c1 = member(x, 3);
        c2 = member(x, 5);
        c3 = notMember(x, 2);
        c4 = notMember(x, iv);
        try {
            m.addConstraint(c2);
            m.addConstraint(c1);
            m.addConstraint(c3);
            m.addConstraint(c4);
            s.read(m);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        System.out.println("[BasicConstraintTests,test1] x : " + s.getVar(x).pretty());
        System.out.println("[BasicConstraintTests,test1] iv : " + s.getVar(iv).pretty());
        assertTrue(!s.getVar(iv).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(iv).canBeInstantiatedTo(5));
        assertTrue(s.getVar(x).isInDomainKernel(3));
        assertTrue(s.getVar(x).isInDomainKernel(5));
        assertTrue(!s.getVar(x).isInDomainKernel(2));
        System.out.println("[BasicConstraintTests,test3] x : " + s.getVar(x).pretty());
        System.out.println("[BasicConstraintTests,test3] iv : " + s.getVar(iv).pretty());
        logger.finest("domains OK after first propagate");
        s.setFirstSolution(false);
        s.generateSearchStrategy();
        s.addGoal(new AssignSetVar(new MinDomSet(s), new MinEnv(s)));
        s.launch();

        assertEquals(8, s.getNbSolutions());
    }

    /**
     * Test TestCardinality ==
     */
    @Test
    public void test4() {
        for (int i = 0; i < 20; i++) {
            m = new CPModel();
            s = new CPSolver();
            logger.finer("test4");
            x = makeSetVar("X", 1, 5);
            iv = makeIntVar("iv", 2, 3);
            c1 = member(x, 3);
            c2 = eqCard(x, iv);   // on teste l'�galit�
            try {
                m.addConstraint(c1);
                m.addConstraint(c2);
                s.read(m);
                s.propagate();
            } catch (ContradictionException e) {
                assertTrue(false);
            }
//        s.setFirstSolution(false);
//        s.generateSearchStrategy();
            s.setVarSetSelector(new RandomSetVarSelector(s, i));
            s.setValSetSelector(new RandomSetValSelector(i + 1));
//        s.launch();
            s.solve();
            do {
                System.out.print("x = " + s.getVar(x).pretty());
                System.out.println(", iv = " +
                        s.getVar(iv).pretty());
            } while (s.nextSolution());
            System.out.println("Nb solution: " + s.getNbSolutions());
            assertEquals(10, s.getNbSolutions());
        }
    }

    @Test
    public void simpleTests(){
        x = makeSetVar("x", 1, 2);
        iv = makeIntVar("iv", 1, 1);
        m.addVariable("cp:bound", iv);
        m.addConstraint(eqCard(x, iv));
        s.read(m);
        s.solve();
            do{
                System.out.println("x = " + s.getVar(x).pretty());
                System.out.println("iv = " + s.getVar(iv).pretty());
            }while(s.nextSolution());
        assertEquals(s.getNbSolutions(),2);

        m = new CPModel();
        s = new CPSolver();
        m.addConstraint(leqCard(x, iv));
        s.read(m);
        s.solve();
            do{
                System.out.println("x = " + s.getVar(x).pretty());
                System.out.println("iv = " + s.getVar(iv).pretty());
            }while(s.nextSolution());
        assertEquals(s.getNbSolutions(),3);

        m = new CPModel();
        s = new CPSolver();
        m.addConstraint(geqCard(x, iv));
        s.read(m);
        s.solve();
            do{
                System.out.println("x = " + s.getVar(x).pretty());
                System.out.println("iv = " + s.getVar(iv).pretty());
            }while(s.nextSolution());
        assertEquals(s.getNbSolutions(),3);


    }

    /**
     * Test TestCardinality <=
     */
    @Test
    public void test5() {
        for (int i = 0; i < 20; i++) {
            logger.finer("test5");
            m = new CPModel();
            s = new CPSolver();
            x = makeSetVar("X", 1, 3);
            iv = makeIntVar("iv", 2, 2);
            c1 = member(x, 3);
            c2 = leqCard(x, iv);   // on teste <=
            try {
                m.addConstraint(c1);
                m.addConstraint(c2);
                s.read(m);
                s.propagate();
            } catch (ContradictionException e) {
                assertTrue(false);
            }
            s.setVarSetSelector(new RandomSetVarSelector(s, i));
            s.setValSetSelector(new RandomSetValSelector(i + 1));
            s.solve();
            do{
                System.out.println("x = " + s.getVar(x).pretty());
                System.out.println("iv = " + s.getVar(iv).pretty());
            }while(s.nextSolution());

            assertEquals(3, s.getNbSolutions());
        }
    }

    /**
     * Test TestCardinality >=
     */
    @Test
    public void test6() {
        for (int i = 0; i < 20; i++) {
            logger.finer("test6");
            m = new CPModel();
            s = new CPSolver();
            x = makeSetVar("X", 1, 3);
            iv = makeIntVar("iv", 1, 2);
            c1 = member(x, 3);
            c2 = geqCard(x, iv);   // on teste =>
            try {
                m.addConstraint(c1);
                m.addConstraint(c2);
                s.read(m);
                s.propagate();
            } catch (ContradictionException e) {
                assertTrue(false);
            }
            s.setVarSetSelector(new RandomSetVarSelector(s, i));
            s.setValSetSelector(new RandomSetValSelector(i + 1));
            s.solve();
            do{

                System.out.println("x = " + s.getVar(x).pretty());
                System.out.println("iv = " + s.getVar(iv).pretty());
            }while(s.nextSolution());
            System.out.println("Nb solution: " + s.getNbSolutions());
            assertEquals(7, s.getNbSolutions());
        }
    }

    /**
     * Test TestDisjoint
     * The number of disjoint pair of set taken in a set of initial size n is :
     * sigma_{k = 0 -> k = n} (C_n^k * 2^(n - k))
     */
    @Test
    public void test7() {
        for (int i = 0; i < 20; i++) {
            logger.finer("test7");
            m = new CPModel();
            s = new CPSolver();
            x = makeSetVar("X", 1, 3);
            y = makeSetVar("Y", 1, 3);
            c1 = setDisjoint(x, y);
            try {
                m.addConstraint(c1);
                s.read(m);
                s.propagate();
            } catch (ContradictionException e) {
                assertTrue(false);
            }
            s.setVarSetSelector(new RandomSetVarSelector(s, i));
            s.setValSetSelector(new RandomSetValSelector(i + 1));
            s.solveAll();
            System.out.println("nbSol " + s.getNbSolutions());
            assertEquals(27, s.getNbSolutions());
        }
    }

    /**
     * Test Intersection
     */
    @Test
    public void test8() {
        for (int i = 0; i < 20; i++) {
        logger.finer("test8");
            m = new CPModel();
            s = new CPSolver();
        x = makeSetVar("X", 1, 3);
        y = makeSetVar("Y", 1, 3);
        z = makeSetVar("Z", 2, 3);
        c1 = setInter(x, y, z);
        //c2 = notmember(z,2);
        try {
            //m.addConstraint(c2);
            m.addConstraint(c1);
            s.read(m);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
       s.setVarSetSelector(new RandomSetVarSelector(s, i));
            s.setValSetSelector(new RandomSetValSelector(i + 1));
            s.solveAll();
        System.out.println("nbSol " + s.getNbSolutions());
        assertEquals(48, s.getNbSolutions());
        }
    }

    /**
	 * Test cardinality reasonnings
	 */
    @Test
    public void test9() {
		logger.finer("test9");
		m = new CPModel();
		x = makeSetVar("X", 1, 3);
		m.addConstraint(geqCard(x,2));
		m.addConstraint(eqCard(x,1));
		boolean contr = false;
        Solver s = new CPSolver();
        s.read(m);
        try {
			s.propagate();
		} catch (ContradictionException e) {
			contr = true;
		}
		assertTrue(contr);
	}

    @Test
    public void test10() {
		cardtest10(true);
	}

    @Test
    public void test10_2() {
		cardtest10(false);
	}

	public void cardtest10(boolean cardr) {
		logger.finer("test10");
		m = new CPModel();
		x = makeSetVar("X", 0, 5);
		y = makeSetVar("Y", 0, 5);
		z = makeSetVar("Z", 0, 5);
		m.addConstraint(setUnion(x,y,z));
		m.addConstraint(leqCard(x,2));
		m.addConstraint(leqCard(y,2));
		m.addConstraint(geqCard(z,5));
        s = new CPSolver();
        s.setCardReasoning(cardr);
        s.read(m);
        boolean contr = false;
		try {
			s.propagate();
		} catch (ContradictionException e) {
            System.out.println("The contradiction is seen only if cardr is set to true");
            contr = true;
		}
		assertTrue(cardr == contr);
	}

}