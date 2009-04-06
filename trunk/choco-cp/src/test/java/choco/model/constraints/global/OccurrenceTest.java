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
package choco.model.constraints.global;


import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class OccurrenceTest {
    private Logger logger = Logger.getLogger("choco.currentElement");
    private CPModel pb;
    private CPSolver s;
    private IntegerVariable x1, x2, x3, x4, x5, x6, x7, y1, x, y, n, m, xx;

    @Before
    public void setUp() {
        logger.fine("Occurrence Testing...");
        pb = new CPModel();
        x = makeIntVar("x", 0, 2);
        xx = makeIntVar("xx", 1, 1);
        y = makeIntVar("y", 0, 2);
        n = makeIntVar("n", 0, 5);
        m = makeIntVar("m", 0, 5);
        pb.addVariables("cp:bound", x, xx, y, n, m);
        x1 = makeIntVar("X1", 0, 10);
        x2 = makeIntVar("X2", 0, 10);
        x3 = makeIntVar("X3", 0, 10);
        x4 = makeIntVar("X4", 0, 10);
        x5 = makeIntVar("X5", 0, 10);
        x6 = makeIntVar("X6", 0, 10);
        x7 = makeIntVar("X7", 0, 10);
        y1 = makeIntVar("Y1", 0, 10);
        s = new CPSolver();
    }

    @After
    public void tearDown() {
        pb = null;
        s = null;
        x1 = x2 = x3 = x4 = x5 = x6 = x7 = y1 = x = y = n = m = xx = null;
    }

    /**
     * Simple currentElement: 5 equations on 4 variables: 1 single search solution that should be found by propagation
     */
    @Test
    public void test1() {
        logger.finer("test1");
        try {
            pb.addConstraint(occurrence(3, y1, new IntegerVariable[]{x1, x2, x3, x4, x5, x6, x7})); // OccurenceEq
            // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
            s.read(pb);
            s.getVar(x1).setVal(3);
            s.getVar(x2).setVal(3);
            s.getVar(x3).setVal(3);
            s.getVar(x4).remVal(3);
            s.getVar(x5).remVal(3);
            s.propagate();
            assertTrue(s.getVar(y1).getInf() >= 3);
            assertTrue(s.getVar(y1).getSup() <= 5);
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    @Test
    public void test2() {
        logger.finer("test2");
        try {
            pb.addConstraint(occurrence(3, y1, new IntegerVariable[]{x1, x2, x3}));
            pb.addConstraint(occurrence(4, y1, new IntegerVariable[]{x1, x5, x4, x6}));
            s.read(pb);
            s.getVar(x1).setVal(3);
            s.getVar(y1).setInf(3);
            s.propagate();
            assertTrue(s.getVar(x2).isInstantiatedTo(3));
            assertTrue(s.getVar(x3).isInstantiatedTo(3));
            assertTrue(s.getVar(x5).isInstantiatedTo(4));
            assertTrue(s.getVar(x4).isInstantiatedTo(4));
            assertTrue(s.getVar(x6).isInstantiatedTo(4));
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    @Test
    public void test3() {
        logger.finer("test3 : first old choco currentElement");
        try {
            pb.addConstraint(occurrence(1, n, new IntegerVariable[]{x, y}));
            pb.addConstraint(occurrence(2, m, new IntegerVariable[]{x, y}));
            // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
            s.read(pb);
            s.propagate();
            s.getVar(n).setVal(0);
            s.getVar(x).setSup(1);
            s.propagate();
            assertTrue(s.getVar(x).getVal() == 0);
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    @Test
    public void test4() {
        logger.finer("test3 : third old choco currentElement");
        try {
            pb.addConstraint(occurrence(1, n, new IntegerVariable[]{xx, m}));
            // pb.getPropagationEngine().getLogger().setVerbosity(choo.model.ILogger.TALK);
            s.read(pb);
            s.propagate();
            assertTrue(s.getVar(n).getInf() >= 1);
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testMagicSeries() {
        int n = 4;
        CPModel pb = new CPModel();
        IntegerVariable[] vs = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            vs[i] = makeIntVar("" + i, 0, n - 1);
        }
        for (int i = 0; i < n; i++) {
            pb.addConstraint(occurrence(i, vs[i], vs));
        }
        pb.addConstraint(eq(sum(vs), n));     // contrainte redondante 1
        int[] coeff2 = new int[n - 1];
        IntegerVariable[] vs2 = new IntegerVariable[n - 1];
        for (int i = 1; i < n; i++) {
            coeff2[i - 1] = i;
            vs2[i - 1] = vs[i];
        }
        pb.addConstraint(eq(scalar(coeff2, vs2), n)); // contrainte redondante 2
        s.read(pb);
        s.solve();
        do {
            for (int i = 0; i < vs.length; i++) {
                System.out.print(s.getVar(vs[i]).getVal() + " ");
            }
            System.out.println("");
        } while (s.nextSolution() == Boolean.TRUE);
        assertEquals(2, s.getNbSolutions());
    }

    @Test
    public void testRandomProblems() {
        for (int bigseed = 0; bigseed < 5; bigseed++) {
            int nbsol = 0, nbsol2 = 0;
            //nb solutions of the gac constraint
            int realNbSol = randomOcc(-1, bigseed,true,1,true);
            //nb solutions of occurrence + enum
            nbsol = randomOcc(realNbSol, bigseed, true, 3, false);
            //b solutions of occurrences + bound
            nbsol2 = randomOcc(realNbSol, bigseed, false, 3, false);
            System.out.println(nbsol + " " + nbsol2 + " " + realNbSol);
            assertEquals(nbsol, nbsol2);
            assertEquals(nbsol, realNbSol);            
        }
    }


    public int randomOcc(int nbsol, int seed, boolean enumvar, int nbtest, boolean gac) {
        for (int interseed = 0; interseed < nbtest; interseed++) {
            int nbOcc = 2;
            int nbVar = 9;
            int sizeDom = 4;
            int sizeOccurence = 4;

            CPModel mod = new CPModel();
            IntegerVariable[] vars;
            vars = makeIntVarArray("e", nbVar, 0, sizeDom);
            if (enumvar) {
                mod.addVariables("cp:enum", vars);
            } else {
                mod.addVariables("cp:bound", vars);
            }

            List<IntegerVariable> lvs = new LinkedList<IntegerVariable>();
            for (int i = 0; i < vars.length; i++) {
                lvs.add(vars[i]);
            }

            Random rand = new Random(seed);
            for (int i = 0; i < nbOcc; i++) {
                IntegerVariable[] vs = new IntegerVariable[sizeOccurence];
                for (int j = 0; j < sizeOccurence; j++) {
                    IntegerVariable iv = lvs.get(rand.nextInt(lvs.size()));
                    lvs.remove(iv);
                    vs[j] = iv;
                }
                IntegerVariable ivc = lvs.get(rand.nextInt(lvs.size()));
                int val = rand.nextInt(sizeDom);
                if (gac) {
                    mod.addConstraint(getTableForOccurence(vs, ivc, val, sizeDom));
                } else {
                    mod.addConstraint(occurrence(val, ivc, vs));
                }
            }
            mod.addConstraint(eq(plus(vars[0], vars[3]), vars[6]));

            CPSolver s = new CPSolver();
            s.read(mod);

            s.setValIntSelector(new RandomIntValSelector(interseed));
            s.setVarIntSelector(new RandomIntVarSelector(s, interseed + 10));

            s.solveAll();
            if (nbsol == -1) {
                nbsol = s.getNbSolutions();
                System.out.println("GAC NBSOL : " + s.getNbSolutions() + " " + s.getNodeCount() + " " + s.getTimeCount());
            } else {
                System.out.println(interseed + " NB solutions " + s.getNbSolutions() + " " + s.getNodeCount() + " " + s.getTimeCount());
                assertEquals(nbsol,s.getNbSolutions());
            }

        }
        return nbsol;
    }

    /**
     * generate a table to encode an occurrence constraint.
     * @param vs
     * @param ub
     * @param val
     * @return
     */
    public Constraint getTableForOccurence(IntegerVariable[] vs, IntegerVariable occ, int val, int ub) {
        CPModel mod = new CPModel();
        IntegerVariable[] vars;
        vars = makeIntVarArray("e", vs.length + 1, 0, ub);
        mod.addVariables("cp:enum", vars);
        CPSolver s = new CPSolver();
        s.read(mod);

        List<int[]> tuples = new LinkedList<int[]>();
        s.solve();
        do {
            int[] tuple = new int[vars.length];
            for (int i = 0; i < tuple.length; i++) {
                tuple[i] = s.getVar(vars[i]).getVal();
            }
            int checkocc = 0;
            for (int i = 0; i < (tuple.length - 1); i++) {
                if (tuple[i] == val) checkocc++;
            }
            if (checkocc == tuple[tuple.length - 1]) {
                tuples.add(tuple);
            }
        } while (s.nextSolution() == Boolean.TRUE);

        IntegerVariable[] newvs = new IntegerVariable[vs.length + 1];
        System.arraycopy(vs,0,newvs,0,vs.length);
        newvs[vs.length] = occ;
        return feasTupleAC("cp:ac32", tuples, newvs);
    }

    @Test
    public void occCedric1() {
        CPModel m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable x = makeIntVar("x", new int[]{1, 3, 4});
        IntegerVariable y = makeIntVar("y", new int[]{2, 3});
        IntegerVariable z = makeIntVar("z", 0, 3);
        IntegerVariable[] tab = new IntegerVariable[]{x, y};
        m.addConstraint(occurrence(1, z, tab));
        CPSolver s = new CPSolver();
        CPSolver.setVerbosity(CPSolver.SOLUTION);
        s.read(m);
        s.solveAll();
        assertEquals(6,s.getNbSolutions());

    }

}