// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.SolveTest.java, last modified by Francois 2 d�c. 2003 23:49:19 */
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

public class SolveTest extends TestCase {
    private Logger logger = Logger.getLogger("choco.currentElement");
    private Problem pb;
    private IntDomainVar x;
    private IntDomainVar y;

    protected void setUp() {
        logger.fine("StoredInt Testing...");
        pb = new Problem();
        x = pb.makeBoundIntVar("X", 0, 5);
        y = pb.makeBoundIntVar("Y", 0, 1);
    }

    protected void tearDown() {
        x = null;
        y = null;
        pb = null;
    }

    /**
     * currentElement the solution count for an infeasible problem
     */
    public void test1() {
        logger.finer("test1");
        pb.post(pb.eq(x, 2));
        pb.post(pb.eq(x, 3));
        pb.solve(false);
        assertEquals(pb.getSolver().getNbSolutions(), 0);
        assertEquals(pb.isFeasible(), Boolean.FALSE);
    }

    /**
     * currentElement the solution count for instantiated problem
     */
    public void test2() {
        logger.finer("test2");
        pb.post(pb.eq(x, 2));
        pb.post(pb.eq(y, 1));
        pb.solve(true);
        assertEquals(pb.isFeasible(), Boolean.TRUE);
        assertEquals(pb.getSolver().getNbSolutions(), 1);
    }

    /**
     * currentElement the solution count for a simple one-variable problem
     */
    public void test3() {
        logger.finer("test3");
        pb.post(pb.eq(y, 1));
        pb.solve(true);
        assertEquals(pb.isFeasible(), Boolean.TRUE);
        assertEquals(pb.getSolver().getNbSolutions(), 6);
    }

    /**
     * currentElement the solution count for a simple two-variable problem
     */
    public void test4() {
        logger.finer("test4");
        pb.solve(true);
        assertEquals(pb.isFeasible(), Boolean.TRUE);
        assertEquals(pb.getSolver().getNbSolutions(), 12);
    }

    /**
     * currentElement the incremental solve.
     */
    public void test5() {
        logger.finer("test5");
        pb.solve();
        while (pb.nextSolution() == Boolean.TRUE) {
        }
        pb.printRuntimeSatistics();
        assertEquals(pb.isFeasible(), Boolean.TRUE);
        assertEquals(pb.getSolver().getNbSolutions(), 12);
        //Logger.getLogger("choco").getHandlers()[0].flush();
    }

    /**
     * currentElement redondqnt post
     */
    public void test6() {
        logger.finer("tesT6");
        Constraint c = pb.eq(y, 1);
        pb.worldPush();
        pb.post(c);
        try {
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        pb.worldPop();
        pb.post(c);
        Solver.flushLogs();
        //Logger.getLogger("choco").getHandlers()[0].flush();
    }

    public static void testBugTPetit150205() {
        int n = 3;
        Problem pb = new Problem();
        IntDomainVar[] vars = new IntDomainVar[n];
        for (int i = 0; i < n; i++) {
            vars[i] = pb.makeEnumIntVar("debut " + i + " :" + i, 1, n);
        }
        ArrayList list = new ArrayList();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Constraint c = pb.neq(vars[i], vars[j]);
                    list.add(c);
                    pb.post(c);
                }
            }
        }

        int FAILPB = 0; // try any value but 0 => no pb
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < list.size(); i++) {
                Constraint constraint = (Constraint) list.get(i);
                ((Propagator) constraint).constAwake(true);
            }

            pb.worldPush();

            Constraint ct = pb.eq(vars[0], 1);
            Constraint ct2 = pb.eq(vars[1], 1);
            if (j == FAILPB) { // no solution
                pb.post(ct);
                pb.post(ct2);
            }


            pb.solveAll();
            if (pb.getSolver().getNbSolutions() > 0) {
                //System.out.println("Nb Solution = " + pb.getSolver().getNbSolutions());
                for (int i = 0; i < n; i++) {
                    System.out.print(vars[i].getVal() + " ");
                }
                System.out.println();
            }
            if (FAILPB == j) assertEquals(pb.getSolver().getNbSolutions(), 0);
            if (FAILPB != j) assertEquals(pb.getSolver().getNbSolutions(), 6);
            pb.worldPopUntil(0);

            /*   if (j == FAILPB) {
             ct.delete();
             ct2.delete();
           } */
            //Logger.getLogger("choco").getHandlers()[0].flush();
        }

    }

    public void testNbNodes() {
        Problem pb = new Problem();
        IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 4);
        IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 3);
        IntDomainVar v3 = pb.makeEnumIntVar("v3", 1, 2);

        pb.post(pb.gt(v1, v2));
        pb.post(pb.gt(v2, v3));

        Solver.setVerbosity(Solver.SEARCH);
        pb.getSolver().setLoggingMaxDepth(10);
        pb.solve(false);
        System.out.println(pb.pretty());

        Solver s = pb.getSolver();
        int time = pb.getSolver().getSearchSolver().getTimeCount();
        int nds = pb.getSolver().getSearchSolver().getNodeCount();
        Solver.flushLogs();
        assertEquals(nds, 2);
        System.out.println(" time: " + time + " nodes: " + nds);
    }

    public static void main(String[] args) {
        Problem pb = new Problem();
        int n = 10;
        IntDomainVar[] bvars = pb.makeEnumIntVarArray("b", n, 0, 1);
        IntDomainVar charge = pb.makeBoundIntVar("charge", 20000, 100000);
        int[] coefs = new int[n];
        Random rand = new Random(100);
        int[] coef = new int[]{2000, 4000};
        for (int i = 0; i < coefs.length; i++) {
            coefs[i] = coef[rand.nextInt(2)];
        }
        //pb.post(pb.eq(pb.scalar(,)));
        Constraint knapsack = pb.geq(pb.scalar(coefs, bvars), charge);
        pb.post(knapsack);

        pb.worldPush();
        int initWorld = pb.getWorldIndex();
        int cpt = 0;
        int k = 5;

        while (cpt < 100) {
            pb.worldPush();

            //redemande le reveil initial des contraintes
            Iterator it = pb.getIntConstraintIterator();
            for (;it.hasNext();) {
                Propagator o = (Propagator) it.next();
                o.constAwake(true);
            }

            //instancie au hasard au plus k variable a 0
            try {
                for (int i = 0; i < k; i++) {
                    bvars[rand.nextInt(10)].setVal(0);
                }
                /*for (int i = 0; i < n; i++) {
                    bvars[i].setVal(0);
                }*/
                if(rand.nextBoolean())
                    charge.setVal(22000);
            } catch (ContradictionException e) {
                e.printStackTrace();
            }

            if (charge.isInstantiated()) {
               System.out.print("Charge FIXED : ");
               pb.solve(); 
            } else {
               pb.maximize(charge, true);
            }

            if (pb.isFeasible()) {
                do {
                    for (int i = 0; i < n; i++) {
                        System.out.print(coefs[i] + "*" + bvars[i].getVal() + " ");
                    }
                    System.out.println(" = " + charge.getVal());
                } while (pb.nextSolution() == Boolean.TRUE);
            } else System.out.println("no solution");

            //retour a un etat propre
            pb.worldPopUntil(initWorld);

            //verifie que tout est propre
            for (int i = 0; i < n; i++) {
                if (bvars[i].isInstantiated()) throw new Error("Error");
            }
            if (charge.isInstantiated()) throw new Error("Error");
            cpt++;
        }
    }
}

