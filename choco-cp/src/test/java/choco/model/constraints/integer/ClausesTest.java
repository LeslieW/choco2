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
package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.bool.sat.ClauseStore;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.Solver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 16 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ClausesTest {


    @Test
    public void test0() {
        int nbsol = computeNbSol();
        for (int seed = 0; seed < 20; seed++) {
            CPModel mod = new CPModel();
            CPSolver s = new CPSolver();
            IntegerVariable[] vars = makeIntVarArray("b", 10, 0, 1, "cp:binary");
            mod.addVariables(vars);
            s.read(mod);
            IntDomainVar[] bvs = s.getVar(vars);
            ClauseStore store = new ClauseStore(s.getVar(vars));
            store.addClause(new IntDomainVar[]{bvs[0], bvs[3], bvs[4]}, new IntDomainVar[]{bvs[1], bvs[2], bvs[7]});
            store.addClause(new IntDomainVar[]{bvs[5], bvs[3]}, new IntDomainVar[]{bvs[1], bvs[5], bvs[4]});
            store.addClause(new IntDomainVar[]{bvs[8]}, new IntDomainVar[]{bvs[4]});
            store.addClause(new IntDomainVar[]{bvs[9], bvs[4]}, new IntDomainVar[]{bvs[6], bvs[8]});
            s.post(store);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solveAll();
            assertEquals(nbsol, s.getNbSolutions());
        }
    }

    @Test
    public void test1() {
        int nbsol = computeNbSol();
        for (int seed = 0; seed < 20; seed++) {
            CPModel mod = new CPModel();
            CPSolver s = new CPSolver();
            IntegerVariable[] vars = makeIntVarArray("b", 10, 0, 1, "cp:binary");
            mod.addConstraint(clause(new IntegerVariable[]{vars[0], vars[3], vars[4]}, new IntegerVariable[]{vars[1], vars[2], vars[7]}));
            mod.addConstraint(clause(new IntegerVariable[]{vars[5], vars[3]}, new IntegerVariable[]{vars[1], vars[4], vars[5]}));
            mod.addConstraint(clause(new IntegerVariable[]{vars[8]}, new IntegerVariable[]{vars[4]}));
            mod.addConstraint(clause(new IntegerVariable[]{vars[9], vars[4]}, new IntegerVariable[]{vars[6], vars[8]}));
            s.read(mod);

            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solveAll();
            assertEquals(nbsol, s.getNbSolutions());
        }
    }

    @Test
    public void testRestartClauses() {
        //for (int seed = 0; seed < 20; seed++) {
        CPModel mod = new CPModel();
        CPSolver s = new CPSolver();
        IntegerVariable[] vars = makeIntVarArray("b", 3, 0, 1, "cp:binary");
        mod.addConstraint(clause(new IntegerVariable[]{vars[0], vars[1], vars[2]}, new IntegerVariable[]{}));
        s.read(mod);
        CPSolver.setVerbosity(CPSolver.SEARCH);
        s.setGeometricRestart(1, 1.1);
        s.setVarIntSelector(new StaticVarOrder(s.getVar(vars)));
        s.solve();
        System.out.println(s.getVar(vars[0]) + " " + s.getVar(vars[1]) + " " + s.getVar(vars[2]));
        s.addNogood(new IntDomainVar[]{s.getVar(vars[0]), s.getVar(vars[1])}, new IntDomainVar[]{});
        s.nextSolution();
        System.out.println(s.getVar(vars[0]) + " " + s.getVar(vars[1]) + " " + s.getVar(vars[2]));

        assertTrue(s.getVar(vars[0]).getVal() == 1 || s.getVar(vars[1]).getVal() == 1);
        CPSolver.flushLogs();
        //}
    }


    @Test
    public void test2() {
        int nbsol = computeNbSol2();
        for (int seed = 0; seed < 20; seed++) {
            CPModel mod = new CPModel();
            CPSolver s = new CPSolver();
            IntegerVariable[] vars = makeIntVarArray("b", 10, 0, 1, "cp:binary");
            mod.addConstraint(clause(new IntegerVariable[]{vars[0], vars[0], vars[4]}, new IntegerVariable[]{vars[1], vars[2], vars[7]}));
            mod.addConstraint(clause(new IntegerVariable[]{vars[5], vars[3]}, new IntegerVariable[0]));
            mod.addConstraint(clause(new IntegerVariable[0], new IntegerVariable[]{vars[8]}));
            mod.addConstraint(clause(new IntegerVariable[]{vars[9], vars[6]}, new IntegerVariable[]{vars[4], vars[6], vars[4]}));
            mod.addConstraint(clause(new IntegerVariable[]{vars[4]}, new IntegerVariable[0]));

            s.read(mod);

            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solveAll();
            System.out.println("" + s.getNbSolutions());
            assertEquals(nbsol, s.getNbSolutions());
        }
    }

    private int computeNbSol2() {
        CPModel mod = new CPModel();
        CPSolver s = new CPSolver();
        IntegerVariable[] v = makeIntVarArray("b", 10, 0, 1, "cp:binary");
        mod.addVariables(v);
        mod.addConstraint(or(eq(v[0], 1), eq(v[0], 1), eq(v[4], 1), eq(v[1], 0), eq(v[2], 0), eq(v[7], 0)));
        mod.addConstraint(or(eq(v[5], 1), eq(v[3], 1)));
        mod.addConstraint(or(eq(v[8], 0)));
        mod.addConstraint(or(eq(v[4], 1)));
        mod.addConstraint(or(eq(v[9], 1), eq(v[6], 1), eq(v[4], 0), eq(v[6], 0), eq(v[4], 0)));
        s.read(mod);
        s.solveAll();
        return s.getNbSolutions();
    }

    private int computeNbSol() {
        CPModel mod = new CPModel();
        CPSolver s = new CPSolver();
        IntegerVariable[] v = makeIntVarArray("b", 10, 0, 1, "cp:binary");
        mod.addVariables(v);
        mod.addConstraint(or(eq(v[0], 1), eq(v[3], 1), eq(v[4], 1), eq(v[1], 0), eq(v[2], 0), eq(v[7], 0)));
        mod.addConstraint(or(eq(v[5], 1), eq(v[3], 1), eq(v[1], 0), eq(v[5], 0), eq(v[4], 0)));
        mod.addConstraint(or(eq(v[8], 1), eq(v[4], 0)));
        mod.addConstraint(or(eq(v[9], 1), eq(v[4], 1), eq(v[6], 0), eq(v[8], 0)));
        s.read(mod);
        s.solveAll();
        return s.getNbSolutions();
    }

    @Test
    public void testRandomSatFormula() {
        for (int seed1 = 0; seed1 < 20; seed1++) {
            Random rand = new Random(seed1);
            int nbvar = rand.nextInt(200) + 30;
            int nbct = rand.nextInt(300) + 10;
            //int nbvar = 2000;//rand.nextInt(200) + 250;
            //int nbct = 15000;//rand.nextInt(2000) + 500;

            for (int seed2 = 0; seed2 < 5; seed2++) {
                System.out.println("seed " + seed1);
                int nbnode = solveNBSOL(seed2, nbvar, nbct, false);
                int nbnode2 = solveNBSOL(seed2, nbvar, nbct, true);
                assertEquals(nbnode, nbnode2);
            }
        }
    }

    public int solveNBSOL(int seed, int nbvar, int nbc, boolean clause) {
        CPModel mod = new CPModel();
        CPSolver s = new CPSolver();

        Random rand = new Random(seed);
        IntegerVariable[] vars = makeIntVarArray("b", nbvar, 0, 1, "cp:binary");
        mod.addVariables(vars);

        for (int i = 0; i < nbc; i++) {
            int poss1 = rand.nextInt(5) + 1;
            int neg1 = rand.nextInt(5);
            IntegerVariable[] poslit = new IntegerVariable[poss1];
            IntegerVariable[] neglit = new IntegerVariable[neg1];
            for (int j = 0; j < poslit.length; j++) {
                poslit[j] = vars[rand.nextInt(nbvar)];
            }
            for (int j = 0; j < neglit.length; j++) {
                neglit[j] = vars[rand.nextInt(nbvar)];
            }
            if (clause) {
                mod.addConstraint(clause(poslit, neglit));
            } else {
                int cpt = 0;
                Constraint[] largeor = new Constraint[poss1 + neg1];
                for (int j = 0; j < poslit.length; j++) {
                    largeor[cpt] = eq(poslit[j], 1);
                    cpt ++;
                }
                for (int j = 0; j < neglit.length; j++) {
                    largeor[cpt] = eq(neglit[j], 0);
                    cpt ++;
                }
                mod.addConstraint(or(largeor));
            }
        }
        s.read(mod);

        s.setVarIntSelector(new RandomIntVarSelector(s, seed));
        s.setValIntSelector(new RandomIntValSelector(seed));
        s.solve();
        System.out.println(seed + " : T= " + s.getTimeCount() + " N= " + s.getNodeCount());
        return s.getNodeCount();

    }

    @Test
    public void testBooleanIssue() {
        CPModel mod = new CPModel();
        CPSolver s = new CPSolver();
        IntegerVariable[] v = makeIntVarArray("b", 3, 0, 1, "cp:binary");
        mod.addVariables(v); //bug without it !
        mod.addConstraint(or(eq(v[2], 1), eq(v[1], 1), eq(v[1], 1)));
        mod.addConstraint(or(eq(v[2], 1), eq(v[1], 0), eq(v[2], 0), eq(v[1], 0)));
        mod.addConstraint(or(eq(v[1], 1), eq(v[2], 1)));
        s.read(mod);
        s.solveAll();
        assertEquals(s.getNbSolutions(), 6);
    }
}
