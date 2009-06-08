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

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.DomOverWDegBranching;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 17 mai 2008
 * Time: 12:36:46
 */
public class ImpactTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    @Test
    public void test1() {
//	public static void main(String[] args) {
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] vs1 = Choco.makeIntVarArray("v1", 10, 2, 10);
        m.addVariables("cp:enum", vs1);

        m.addVariables(vs1);
        for (int i = 0; i < vs1.length; i++) {
            for (int j = i + 1; j < vs1.length; j++) {
                m.addConstraint(Choco.neq(vs1[i], vs1[j]));
            }
        }
        s.read(m);
        ImpactBasedBranching ibb = new ImpactBasedBranching(s);
        ibb.getImpactStrategy().initImpacts(100);
        s.attachGoal(ibb);
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.printRuntimeSatistics();
        s.launch();
    }

    @Test
    public void test2() {
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] vs1 = Choco.makeIntVarArray("v1", 10, 2, 10);

        m.addVariables(vs1);
        for (int i = 0; i < vs1.length; i++) {
            for (int j = i + 1; j < vs1.length; j++) {
                m.addConstraint(Choco.neq(vs1[i], vs1[j]));
            }
        }
        s.read(m);

        ImpactBasedBranching ibb = new ImpactBasedBranching(s);
        ibb.getImpactStrategy().initImpacts(100);
        s.attachGoal(ibb);
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.printRuntimeSatistics();
        s.launch();
    }

    @Test
    public void testMagicSquare() {
        testMagicSquare(9);
    }

    public void testMagicSquare(int n) {
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] vars = Choco.makeIntVarArray("C", n * n, 1, n * n);
        m.addVariables("cp:enum", vars);

        IntegerVariable sum = Choco.makeIntVar("sum", 1, n * n * (n * n + 1) / 2);
        m.addVariable("cp:bc", sum);

        m.addConstraint("cp:bc", Choco.allDifferent(vars));
        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(Choco.eq(Choco.sum(col), sum));
            m.addConstraint(Choco.eq(Choco.sum(row), sum));
        }

        m.addConstraint(Choco.eq(sum, n * (n * n + 1) / 2));

        CPSolver s = new CPSolver();
        s.read(m);

        ImpactBasedBranching ibb = new ImpactBasedBranching(s);
        ibb.getImpactStrategy().initImpacts(1000000);

        s.setCpuTimeLimit(60000);
        s.setTimeLimit(65000);
        s.monitorBackTrackLimit(true);
        s.setGeometricRestart(14, 1.5d);
        s.generateSearchStrategy();

        s.attachGoal(ibb);
        s.setFirstSolution(true);
        s.launch();

        LOGGER.info(n + " Nb noeuds = " + s.getNodeCount());
        LOGGER.info(n + " Temps = " + s.getTimeCount());

        for (int i = 0; i < n; i++) {
            StringBuffer st = new StringBuffer();
            for (int j = 0; j < n; j++) {
                st.append(MessageFormat.format("{0} ", ((IntDomainVar) s.getIntVar(i * n + j)).getVal()));
            }
            LOGGER.info(st.toString());
        }

        assertTrue(s.getNodeCount() < 5000);
    }

    @Test
    public void testMagicSquareRestartDwdeg() {
        int n = 5;
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] vars = Choco.makeIntVarArray("C", n * n, 1, n * n);
        m.addVariables("cp:enum", vars);

        IntegerVariable sum = Choco.makeIntVar("sum", 1, n * n * (n * n + 1) / 2);
        m.addVariable("cp:bc", sum);

        m.addConstraint("cp:bc", Choco.allDifferent(vars));
        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(Choco.eq(Choco.sum(col), sum));
            m.addConstraint(Choco.eq(Choco.sum(row), sum));
        }

        m.addConstraint(Choco.eq(sum, n * (n * n + 1) / 2));

        CPSolver s = new CPSolver();
        s.read(m);

        s.setGeometricRestart(14, 1.5d);
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.attachGoal(new DomOverWDegBranching(s, new IncreasingDomain()));
        s.launch();
        LOGGER.info(n + " Nb noeuds = " + s.getNodeCount());
        LOGGER.info(n + " Nb backs = " + s.getBackTrackCount());
        LOGGER.info(n + " Temps = " + s.getTimeCount());

        for (int i = 0; i < n; i++) {
            StringBuffer st = new StringBuffer();
            for (int j = 0; j < n; j++) {
                st.append(MessageFormat.format("{0} ", ((IntDomainVar) s.getIntVar(i * n + j)).getVal()));
            }
            LOGGER.info(st.toString());
        }
        LOGGER.info("" + s.getNodeCount());
        assertTrue(s.getNodeCount() < 5000);
    }

    public static void main(String[] args) {
        int n = 7;
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] vars = Choco.makeIntVarArray("C", n * n, 1, n * n);
        m.addVariables("cp:enum", vars);

        IntegerVariable sum = Choco.makeIntVar("sum", 1, n * n * (n * n + 1) / 2);
        m.addVariable("cp:clique", sum);

        m.addConstraint("cp:bc", Choco.allDifferent(vars));
        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(Choco.eq(Choco.sum(col), sum));
            m.addConstraint(Choco.eq(Choco.sum(row), sum));
        }

        m.addConstraint(Choco.eq(sum, n * (n * n + 1) / 2));

        CPSolver s = new CPSolver();
        s.read(m);
        s.monitorBackTrackLimit(true);
        s.solve();
        System.out.println(n + " Nb noeuds = " + s.getNodeCount());
        System.out.println(n + " Temps = " + s.getTimeCount());
        System.out.println(n + " back = " + s.getBackTrackCount());

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(((IntDomainVar) s.getIntVar(i * n + j)).getVal() + " ");
            }
            System.out.println("");
        }
    }

}
