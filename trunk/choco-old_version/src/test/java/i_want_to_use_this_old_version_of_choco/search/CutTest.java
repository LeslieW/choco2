package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class CutTest extends TestCase {
    private Logger logger = Logger.getLogger("choco.currentElement");
    private Problem pb;
    private IntDomainVar v1, v2;

    protected void setUp() {
        logger.fine("StoredInt Testing...");
        pb = new Problem();
        v1 = pb.makeEnumIntVar("v1", 0, 1);
        v2 = pb.makeEnumIntVar("v2", 0, 1);
    }

    protected void tearDown() {
        v1 = null;
        v2 = null;
        pb = null;
    }

    /**
     * one cut that sets the problem to the first solution -> no more solutions afterwards
     */
    public void test1() {
        logger.finer("test1");
        pb.solve();
        int valv1 = v1.getVal();
        int valv2 = v2.getVal();
        pb.postCut(pb.eq(v1, valv1));
        pb.postCut(pb.eq(v2, valv2));
        Boolean nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
    }

    /**
     * one cut that forces each variable to be different from their value in the first solution
     * -> only one other solution (flipping all variables)
     */
    public void test2() {
        logger.finer("test1");
        pb.solve();
        int valv1 = v1.getVal();
        int valv2 = v2.getVal();
        pb.postCut(pb.neq(v1, valv1));
        pb.postCut(pb.neq(v2, valv2));
        Boolean nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        assertEquals(v1.getVal(), 1 - valv1);
        assertEquals(v2.getVal(), 1 - valv2);
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
    }

    /**
     * one cut that sets the first variable to its value in the first solution
     * -> only one more solutions afterwards
     */
    public void test3() {
        logger.finer("test3");
        pb.solve();
        int valv1 = v1.getVal();
        pb.postCut(pb.eq(v1, valv1));
        Boolean nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        assertEquals(valv1, v1.getVal());
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
    }

    /**
     * one cut that sets the first variable to its value in the first solution
     * -> only one more solutions afterwards
     */
    public void test4() {
        logger.finer("test4");
        pb.solve();
        int valv2 = v2.getVal();
        pb.postCut(pb.eq(v2, valv2));
        Boolean nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        assertEquals(valv2, v2.getVal());
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
    }

    public void test5() {
        logger.finer("test5");
        IntDomainVar v3 = pb.makeEnumIntVar("v3", 0, 1);
        pb.solve();  // first solution 0,0,0
        pb.postCut(pb.leq(pb.plus(v1, pb.plus(v2, v3)), 1));
        // now three more solutions
        Boolean nxt = null;
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
    }

    public void test6() {
        logger.finer("test6");
        IntDomainVar v3 = pb.makeEnumIntVar("v3", 0, 1);
        pb.solve();  // first solution 0,0,0
        pb.postCut(pb.geq(pb.plus(v1, pb.plus(v2, v3)), 2));
        // now three more solutions
        Boolean nxt = null;
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.FALSE);
    }

    public void test7() {
        logger.finer("test7");
        IntDomainVar v3 = pb.makeEnumIntVar("v3", 0, 1);
        pb.solve();  // first solution 0,0,0
        pb.postCut(pb.eq(pb.plus(v1, pb.plus(v2, v3)), 2));
        // now three more solutions
        Boolean nxt = null;
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(nxt, Boolean.FALSE);
    }

    public void test8() {
        logger.finer("test8");
        IntDomainVar v3 = pb.makeEnumIntVar("v3", 0, 1);
        IntDomainVar v4 = pb.makeEnumIntVar("v4", 0, 1);
        IntDomainVar v5 = pb.makeEnumIntVar("v5", 0, 1);
        IntDomainVar v6 = pb.makeEnumIntVar("v6", 0, 1);
        pb.post(pb.geq(pb.sum(new IntDomainVar[]{v1, v2, v3, v4, v5, v6}), 5));

        Boolean found;
        found = pb.solve();
        System.out.println(pb.pretty());
        assertEquals(found, Boolean.TRUE);

        pb.postCut(pb.eq(v1, 1));
        pb.postCut(pb.leq(pb.sum(new IntDomainVar[]{v1, v2, v3, v4, v5, v6}), 5));
        for (int i = 0; i < 5; i++) {
            found = pb.nextSolution();
            System.out.println(pb.pretty());
            assertEquals(found, Boolean.TRUE);
        }

        found = pb.nextSolution();
        System.out.println(pb.pretty());
        assertEquals(found, Boolean.FALSE);
    }

    public void test9() {
        Problem pb;
        IntDomainVar x;
        IntDomainVar y;
        pb = new Problem();
        x = pb.makeEnumIntVar("x", 0, 1);
        y = pb.makeEnumIntVar("y", 0, 1);

        Constraint ct = pb.neq(x, y);
        pb.postCut(ct);
        pb.eraseConstraint(ct);

        pb.solveAll();

        System.out.println("Nombre de solutions : " + pb.getSolver().getNbSolutions());
        assertEquals(pb.getSolver().getNbSolutions(),4); 
    }

    public void test9bis() {
        Problem pb;
        IntDomainVar x;
        IntDomainVar y;
        pb = new Problem();
        x = pb.makeEnumIntVar("x", 0, 1);
        y = pb.makeEnumIntVar("y", 0, 1);

        Constraint ct = pb.neq(x, y);
        pb.postCut(ct);
        ((AbstractConstraint) ct).delete();

        pb.solveAll();

        System.out.println("Nombre de solutions : " + pb.getSolver().getNbSolutions());
        assertEquals(pb.getSolver().getNbSolutions(),4);
    }

    public void test10() {
        Problem pb;
        IntDomainVar x;
        IntDomainVar y;
        pb = new Problem();
        x = pb.makeEnumIntVar("x", 0, 1);
        y = pb.makeEnumIntVar("y", 0, 1);
        int baseworld = pb.getEnvironment().getWorldIndex();
        pb.worldPush();

        pb.solveAll();
        System.out.println("Nombre de solutions : " + pb.getSolver().getNbSolutions());
        assertEquals(pb.getSolver().getNbSolutions(),4);
        pb.worldPopUntil(baseworld);
        pb.solveAll();
        System.out.println("Nombre de solutions : " + pb.getSolver().getNbSolutions());
        assertEquals(pb.getSolver().getNbSolutions(),4);
    }
}