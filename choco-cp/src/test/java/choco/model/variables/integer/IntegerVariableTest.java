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
package choco.model.variables.integer;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.variables.integer.IntervalIntDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 13 juin 2008
 * Time: 12:51:54
 */
public class IntegerVariableTest {
    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    private static HashMap<Integer, String> typeList = new HashMap();

    static {
        typeList.put(0, "bound");
        typeList.put(1, "enum");
        typeList.put(2, "binTree");
        typeList.put(3, "linkedList");
    }

    @Test
    @Ignore
    public void nqueen() {
        LOGGER.info("===================NQUEEN=====================");
        //nbSol
        int[] sol = new int[13];
        sol[2] = 0;
        sol[4] = 2;
        sol[6] = 4;
        sol[8] = 92;
        sol[10] = 724;


        for (int n = 2; n < 11; n += 2) {
            LOGGER.info("=================== n = " + n + " =====================");
            for (int type = 0; type < 4; type++) {
                Model m = new CPModel();
                IntegerVariable[] queens = new IntegerVariable[n];
                IntegerVariable[] queensdual = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    queens[i] = createVar(type, "Q" + i, 1, n, m);
                    queensdual[i] = createVar(type, "QD" + i, 1, n, m);
                }
                nQueensNaifRed(m, type, n, queens, queensdual, sol[n]);
            }
        }
    }

    @Test
    @Ignore
    public void alldifferent() {
        LOGGER.info("===================ALLDIFFERENT=====================");
        //nbSol
        int[] sol = new int[]{0, 1, 2, 6, 24, 120, 720, 5040, 40320};
        for (int n = 1; n < 9; n += 1) {
            LOGGER.info("=================== n = " + n + " =====================");
            // BEAWRE: we do not test the linked list domain for this problem, too much time consumer
            for (int type = 0; type < 3; type++) {
                Model m = new CPModel();
                IntegerVariable[] vars = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    vars[i] = createVar(type, "v" + i, 1, n, m);
                }
                pbAllDifferent(m, type, sol[n], vars);
            }
        }
    }

    @Test
    @Ignore
    public void boundalldifferent() {
        LOGGER.info("===================BOUNDALLDIFFERENT=====================");

        for (int n = 1000; n < 2001; n += 1000) {
            LOGGER.info("=================== n = " + n + " =====================");
            // BEAWRE: we do not test the linked list domain for this problem, too much time consumer
            for (int type = 0; type < 3; type++) {
                Model m = new CPModel();
                IntegerVariable[] vars = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    vars[i] = createVar(type, "v" + i, 1, n, m);
                }
                pbBoundAllDifferent(type, vars);
            }
        }
    }

    @Test
//    @Ignore
    public void odd() {
        LOGGER.info("===================ODD=====================");

        for (int n = 10; n < 10001; n *= 10) {
            LOGGER.info("=================== n = " + n + " =====================");
            // BEAWRE: we do not test the linked list domain for this problem, too much time consumer
            for (int type = 0; type < 3; type++) {
                Model m = new CPModel();
                IntegerVariable v0 = createVar(type, "v0", 1, n, m);
                IntegerVariable v1 = createVar(type, "v1", 1, n, m);
                oddPb(m, type, v0, v1);
            }
        }
    }

//
//    @Test
//    public void magicsquare(){
//
//    }
//
//    @Test
//    public void donaldgeraldrobert(){
//
//    }


    private IntegerVariable createVar(int type, String name, int bi, int bs, Model m) {
        IntegerVariable iv = makeIntVar(name, bi, bs);
        switch (type) {
            case 0:
                m.addVariable("cp:bound", iv);
                break;
            case 1:
                m.addVariable("cp:enum", iv);
                break;
            case 2:
                m.addVariable("cp:btree", iv);
                break;
            case 3:
                m.addVariable("cp:link", iv);
                break;
            default:
                break;
        }
        return iv;
    }


    /**
     * ********PROBLEMES****************
     */


    public static void nQueensNaifRed(Model m, int type, int n, IntegerVariable[] queens, IntegerVariable[] queensdual, int nbSol) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
                m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queensdual[i], queensdual[j]));
                m.addConstraint(neq(queensdual[i], plus(queensdual[j], k)));  // diagonal constraints
                m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
            }
        }
        m.addConstraint(inverseChanneling(queens, queensdual));
        Solver s = new CPSolver();
        s.read(m);
        s.setVarIntSelector(new MinDomain(s, s.getVar(queens)));
        //CPSolver.setVerbosity(CPSolver.SOLUTION);
        int timeLimit = 10000;
        //s.setTimeLimit(timeLimit);
        s.solveAll();
        LOGGER.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeSatistics();
        Assert.assertEquals("nbSol incorrect", nbSol, s.getNbSolutions());
    }


    public static void pbAllDifferent(Model m, int type, int nbSol, IntegerVariable... vars) {
        Constraint c = allDifferent(vars);
        m.addConstraint(c);

        Solver s = new CPSolver();
        s.read(m);

        //int timeLimit = 10000;
        //s.setTimeLimit(timeLimit);
        s.solveAll();
        LOGGER.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeSatistics();
        Assert.assertEquals("nbSol incorrect", nbSol, s.getNbSolutions());

    }

    public static void pbBoundAllDifferent(int type, IntegerVariable... vars){
        Model m = new CPModel();
        Constraint c  = allDifferent(vars);
        m.addConstraint("cp:clique", c);

        Solver s = new CPSolver();
        s.read(m);

//        int timeLimit = 10000;
//        s.setTimeLimit(timeLimit);
        s.solve();
        LOGGER.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeSatistics();

    }

    public static void oddPb(Model m, int type, IntegerVariable v0, IntegerVariable v1) {
        m.addConstraint(eq(v0, plus(v1, 1)));

        m.addConstraint(new ComponentConstraint(IsOdd.IsOddManager.class, null, new IntegerVariable[]{v1}));
        m.addConstraint(new ComponentConstraint("choco.model.variables.integer.IsOdd$IsOddManager", null, new IntegerVariable[]{v0}));


        Solver s = new CPSolver();
        s.read(m);
        s.monitorBackTrackLimit(true);
        s.monitorFailLimit(true);
        s.solve();

        Assert.assertEquals(s.getNbSolutions(), 0);
        LOGGER.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeSatistics();

    }

    @Test
    public void testOnUndefinedVariable(){
        Model m = new CPModel();
        IntegerVariable v = Choco.makeIntVar("v", Integer.MIN_VALUE, Integer.MAX_VALUE);
        IntegerVariable w = Choco.makeIntVar("w");
        m.addVariables(v, w);
        Solver s = new CPSolver();
        try{
            s.read(m);
        }catch (OutOfMemoryError e){
            Assert.fail("OutOfMemoryError...");
        }
        Assert.assertEquals("w wrong type", IntervalIntDomain.class, s.getVar(w).getDomain().getClass());

    }

}
