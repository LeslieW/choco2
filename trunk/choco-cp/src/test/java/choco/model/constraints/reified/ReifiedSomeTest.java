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

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.model.managers.operators.SqrtManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import choco.cp.solver.constraints.integer.channeling.ReifiedLargeOr;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import static choco.kernel.model.constraints.ConstraintType.*;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.*;
import static org.junit.Assert.*;

import static java.text.MessageFormat.format;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 28 janv. 2008
 * Time: 09:39:16
 */
public class ReifiedSomeTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Model m;
    Solver s;
    IntegerVariable i1;
    IntegerVariable i2;
    RealVariable r1;
    RealVariable r2;
    SetVariable s1;
    SetVariable s2;

    @After
    public void tearDown() throws Exception {
        ChocoLogging.flushLogs();
        m = null;
        s = null;
        i1 = null;
        i2 = null;
        r1 = null;
        r2 = null;
        s1 = null;
        s2 = null;
    }

    @Before
    public void setUp() throws Exception {
        m = new CPModel();
        m.setPrecision(0.1);
        s = new CPSolver();
    }

    @Test
    public void test1() {
        LOGGER.info("ReifiedSomeTest.test1");
        i1 = makeIntVar("i1", 1, 2);
        m.addConstraint(ifOnlyIf(eq((i1), (1)), eq((i1), (2))));
        s.read(m);
        LOGGER.info(s.pretty());
        s.solve();
        assertTrue("Solution found : unexpected", !s.isFeasible());

    }

    @Test
    public void test1Decomp() {
        LOGGER.info("ReifiedSomeTest.test1Decomp");
        i1 = makeIntVar("i1", 1, 2);
        Constraint e = ifOnlyIf(eq((i1), (1)), eq((i1), (2)));
        //e.setDecomposeExp(true);
        m.addConstraint(e);
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", !s.isFeasible());

    }

    @Test
    public void test2() {
        LOGGER.info("ReifiedSomeTest.test2");
        i1 = makeIntVar("i1", 1, 3);
        m.addConstraint(implies(eq((i1), (1)), eq((i1), (2))));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());
        assertTrue("Value i1 unexpected", s.getVar(i1).getVal() == 2);
    }

    @Test
    public void test2Decomp() {
        LOGGER.info("ReifiedSomeTest.test2");
        i1 = makeIntVar("i1", 1, 3);
        m.setDefaultExpressionDecomposition(true);
        m.addConstraint(implies(eq((i1), (1)), eq((i1), (2))));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());
        assertEquals("Value i1 unexpected", 2, s.getVar(i1).getVal());
    }

    @Test
    @Ignore
    public void test3() {
        LOGGER.info("ReifiedSomeTest.test3");
        r1 = makeRealVar("r1", 1, 3);
        r2 = makeRealVar("r2", 1, 3);
        m.addConstraint(ifOnlyIf(geq(r1, 2.1), leq(r2, 2)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    @Ignore
    public void test4() {
        LOGGER.info("ReifiedSomeTest.test4");
        r1 = makeRealVar("r1", 1, 3);
        r2 = makeRealVar("r2", 1, 3);
        //m.addConstraint(implies(geq(r1, 2.1), leq(r2, 2)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    @Ignore
    public void test5() {
        LOGGER.info("ReifiedSomeTest.test5");
        s1 = makeSetVar("s1", 1, 3);
        s2 = makeSetVar("s2", 1, 3);
        //m.addConstraint(ifOnlyIf(member(s1, 1), member(s2, 1)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    @Ignore
    public void test6() {
        LOGGER.info("ReifiedSomeTest.test6");
        s1 = makeSetVar("s1", 1, 3);
        s2 = makeSetVar("s2", 1, 3);
        //m.addConstraint(implies(member(s1, 1), member(s2, 1)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());
    }

    @Test
    @Ignore
    public void test7() {
        LOGGER.info("ReifiedSomeTest.test7");
        i1 = makeIntVar("i1", 1, 3);
        m.addVariable("cp:bound", i1);
        r2 = makeRealVar("r2", 1, 3);
        //m.addConstraint(implies(geq(i1, 2), leq(r2, 2)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    @Ignore
    public void test8() {
        LOGGER.info("ReifiedSomeTest.test8");
        i1 = makeIntVar("i1", 1, 3);
        m.addVariable("cp:bound", i1);
        s2 = makeSetVar("s2", 1, 3);
        //m.addConstraint(implies(geq(i1, 2), member(s2, 1)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    @Ignore
    public void test9() {
        LOGGER.info("ReifiedSomeTest.test9");
        r1 = makeRealVar("r1", 1, 3);
        s2 = makeSetVar("s2", 1, 3);
        //m.addConstraint(implies(geq(r1, 2.1), member(s2, 1)));
        s.read(m);
        s.solve();
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    public void test10() {
        LOGGER.info("ReifiedSomeTest.test10");
        i1 = makeIntVar("i1", 1, 3);
        i2 = makeIntVar("i2", 1, 3);
        IntegerVariable V3 = makeIntVar("V3", 1, 3);

        Constraint c10 = eq((i1), (1));
        Constraint c11 = eq((i2), (1));
        Constraint c12 = eq((V3), (1));
        Constraint c1 = and(c10, c11, c12);

        Constraint c20 = eq((i1), (2));
        Constraint c21 = eq((i2), (2));
        Constraint c22 = eq((V3), (2));
        Constraint c2 = and(c20, c21, c22);

        Constraint c30 = eq((i1), (3));
        Constraint c31 = eq((i2), (3));
        Constraint c32 = eq((V3), (3));
        Constraint c3 = and(c30, c31, c32);

        m.addConstraint(or(c1, c2, c3));
        s.read(m);
        s.solve();
        LOGGER.info("i1 = " + s.getVar(i1).pretty());
        LOGGER.info("i2 = " + s.getVar(i2).pretty());
        LOGGER.info("i3 = " + s.getVar(V3).pretty());
        LOGGER.info(""+s.isFeasible());
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    public void test10Decomp() {
        LOGGER.info("ReifiedSomeTest.test10");
        i1 = makeIntVar("i1", 1, 3);
        i2 = makeIntVar("i2", 1, 3);
        IntegerVariable V3 = makeIntVar("V3", 1, 3);

        Constraint c10 = eq((i1), (1));
        Constraint c11 = eq((i2), (1));
        Constraint c12 = eq((V3), (1));
        Constraint c1 = and(c10, c11, c12);

        Constraint c20 = eq((i1), (2));
        Constraint c21 = eq((i2), (2));
        Constraint c22 = eq((V3), (2));
        Constraint c2 = and(c20, c21, c22);

        Constraint c30 = eq((i1), (3));
        Constraint c31 = eq((i2), (3));
        Constraint c32 = eq((V3), (3));
        Constraint c3 = and(c30, c31, c32);

        m.setDefaultExpressionDecomposition(true);
        m.addConstraint(or(c1, c2, c3));
        s.read(m);
        s.solve();
        LOGGER.info("i1 = " + s.getVar(i1).pretty());
        LOGGER.info("i2 = " + s.getVar(i2).pretty());
        LOGGER.info("i3 = " + s.getVar(V3).pretty());
        LOGGER.info(""+s.isFeasible());
        assertTrue("Solution found : unexpected", s.isFeasible());

    }

    @Test
    public void testPropagBibi() {
        IntegerVariable x = makeIntVar("x", 0, 20);
        m.addConstraint(or(lt((x), (10)), lt((x), (8))));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        assertEquals(s.getVar(x).getSup(), 9);
        LOGGER.info(s.pretty());
    }

    @Test
    public void testProsser() {
        IntegerVariable x = makeIntVar("x", 0, 2);
        IntegerVariable y = makeIntVar("y", 0, 2);
        IntegerVariable z = makeIntVar("z", 0, 2);
        IntegerVariable d = makeIntVar("d", 0, 1);
        Constraint Cxy = eq((x), (y));
        Constraint Cxz = eq((x), (z));
        Constraint Cyz = lt((y), (z));
        Constraint C1 = and(Cxy, and(Cxz, Cyz));
        Constraint C2 = eq((d), (0));
        Constraint C3 = ifOnlyIf(C1, C2);
        m.addConstraint(C3);
        s.read(m);
        s.solve(false);
        LOGGER.info(s.pretty());
    }

    @Test
    public void testProsserDecomp() {
        IntegerVariable x = makeIntVar("x", 0, 2);
        IntegerVariable y = makeIntVar("y", 0, 2);
        IntegerVariable z = makeIntVar("z", 0, 2);
        IntegerVariable d = makeIntVar("d", 0, 1);
        Constraint Cxy = eq((x), (y));
        Constraint Cxz = eq((x), (z));
        Constraint Cyz = lt((y), (z));
        Constraint C1 = and(Cxy, and(Cxz, Cyz));
        Constraint C2 = eq((d), (0));
        m.setDefaultExpressionDecomposition(true);
        Constraint C3 = ifOnlyIf(C1, C2);
        m.addConstraint(C3);
        s.read(m);
        s.solve(false);
        LOGGER.info(s.pretty());
    }

    @Test
    public void testBugMaurice1a() {
        int tMax = 10;
        IntegerVariable S = makeIntVar("S", 0, tMax);
        m.addVariable("cp:bound", S);
        int p = 4;
        IntegerVariable[] y = new IntegerVariable[11];
        for (int t = 0; t <= tMax; t++) {
            y[t] = makeIntVar("y_" + t, 0, 1);
            m.addVariable("cp:bound", y[t]);
            Constraint cst;
            cst = implies(eq((y[t]), (1)),
                    and(geq((t), (S)),
                            lt((t), plus((S), (p)))));
            m.addConstraint(cst);
            Constraint cst2 = implies(and(geq((t), (S)), lt((t), plus((S), (p)))),
                    eq((y[t]), (1)));
            m.addConstraint(cst2);
        }

        try {
            s.read(m);
            s.propagate();
            s.solve();
            int nbSolution = 0;
            do {
                nbSolution++;
                StringBuffer st = new StringBuffer();
                st.append(format("S = {0}   \t", s.getVar(S).getVal()));
                for (IntegerVariable intVar : y) {
                    st.append(format("{0} = {1}   ", intVar, s.getVar(intVar).getVal()));
                }
                LOGGER.info(st.toString());
            } while (s.nextSolution());
            assertEquals(11, nbSolution);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBugMaurice1aDecomp() {
        int tMax = 10;
        IntegerVariable S = makeIntVar("S", 0, tMax);
        m.addVariable("cp:bound", S);
        int p = 4;
        IntegerVariable[] y = new IntegerVariable[11];
        for (int t = 0; t <= tMax; t++) {
            y[t] = makeIntVar("y_" + t, 0, 1);
            m.addVariable("cp:bound", y[t]);
            Constraint cst;
            cst = implies(eq((y[t]), (1)),
                    and(geq((t), (S)),
                            lt((t), plus((S), (p)))));
            m.addConstraint(cst);
            Constraint cst2 = implies(and(geq((t), (S)), lt((t), plus((S), (p)))),
                    eq((y[t]), (1)));
            m.addConstraint(cst2);
            m.setDefaultExpressionDecomposition(true);
        }

        try {
            s.read(m);
            s.propagate();
            s.solve();
            int nbSolution = 0;
            do {
                nbSolution++;
                StringBuffer st = new StringBuffer();
                st.append(format("S = {0}   \t", s.getVar(S).getVal()));
                for (IntegerVariable intVar : y) {
                    st.append(format("{0} = {1}   ", intVar, s.getVar(intVar).getVal()));
                }
                LOGGER.info(st.toString());
            } while (s.nextSolution());
            assertEquals(11, nbSolution);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBugMaurice1b() {
        int tMax = 10;
        IntegerVariable S = makeIntVar("S", 0, tMax);
        m.addVariable("cp:bound", S);
        int p = 4;
        IntegerVariable[] y = new IntegerVariable[11];
        for (int t = 0; t <= tMax; t++) {
            y[t] = makeIntVar("y_" + t, 0, 1);
            m.addVariable("cp:bound", y[t]);
            Constraint cst;
            cst = implies(eq((y[t]), (1)),
                    and(geq((t), (S)),
                            lt((t), plus((S), (p)))));
            m.addConstraint(cst);
            Constraint cst2 = implies(and(geq((t), (S)), lt((t), plus((S), (p)))),
                    eq((y[t]), (1)));
            m.addConstraint(cst2);
        }
        s.read(m);
        try {
            s.propagate();
            s.solve();
            int nbSolution = 0;
            do {
                nbSolution++;
                StringBuffer st = new StringBuffer();
                st.append(format("S = {0}   \t", s.getVar(S).getVal()));
                for (IntegerVariable intVar : y) {
                    st.append(format("{0} = {1}   ", intVar, s.getVar(intVar).getVal()));
                }
                LOGGER.info(st.toString());
            } while (s.nextSolution());
            assertEquals(11, nbSolution);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBugMaurice1bDecomp() {
        int tMax = 10;
        IntegerVariable S = makeIntVar("S", 0, tMax);
        m.addVariable("cp:bound", S);
        int p = 4;
        IntegerVariable[] y = new IntegerVariable[11];
        for (int t = 0; t <= tMax; t++) {
            y[t] = makeIntVar("y_" + t, 0, 1);
            m.addVariable("cp:bound", y[t]);
            Constraint cst;
            cst = implies(eq((y[t]), (1)),
                    and(geq((t), (S)),
                            lt((t), plus((S), (p)))));
            m.addConstraint(cst);
            Constraint cst2 = implies(and(geq((t), (S)), lt((t), plus((S), (p)))),
                    eq((y[t]), (1)));
            m.addConstraint(cst2);
            m.setDefaultExpressionDecomposition(true);
        }
        s.read(m);
        try {
            s.propagate();
            s.solve();
            int nbSolution = 0;
            do {
                nbSolution++;
                StringBuffer st = new StringBuffer();
                st.append(format("S = {0}   \t", s.getVar(S).getVal()));
                for (IntegerVariable intVar : y) {
                    st.append(format("{0} = {1}   ", intVar, s.getVar(intVar).getVal()));
                }
                LOGGER.info(st.toString());
            } while (s.nextSolution());
            assertEquals(11, nbSolution);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBugMaurice1c() {
        int tMax = 10;
        IntegerVariable S = makeIntVar("S", 0, tMax);
        m.addVariable("cp:bound", S);
        int p = 4;
        IntegerVariable[] y = new IntegerVariable[11];
        for (int t = 0; t <= tMax; t++) {
            y[t] = makeIntVar("y_" + t, 0, 1);
            m.addVariable("cp:bound", y[t]);
            Constraint cst;
            cst = ifOnlyIf(eq((y[t]), (1)),
                    and(geq((t), (S)),
                            lt((t), plus((S), (p)))));
            m.addConstraint(cst);
        }
        s.read(m);
        try {
            s.propagate();
            s.solve();
            int nbSolution = 0;
            do {
                nbSolution++;
                StringBuffer st = new StringBuffer();
                st.append(format("S = {0}   \t", s.getVar(S).getVal()));
                for (IntegerVariable intVar : y) {
                    st.append(format("{0} = {1}   ", intVar, s.getVar(intVar).getVal()));
                }
                LOGGER.info(st.toString());
            } while (s.nextSolution());
            assertEquals(11, nbSolution);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBugMaurice1cDecomp() {
        int tMax = 10;
        IntegerVariable S = makeIntVar("S", 0, tMax);
        m.addVariable("cp:bound", S);
        int p = 4;
        IntegerVariable[] y = new IntegerVariable[11];
        for (int t = 0; t <= tMax; t++) {
            y[t] = makeIntVar("y_" + t, 0, 1);
            m.addVariable("cp:bound", y[t]);
            Constraint cst;
            cst = ifOnlyIf(eq((y[t]), (1)),
                    and(geq((t), (S)),
                            lt((t), plus((S), (p)))));
            m.addConstraint(cst);
            m.setDefaultExpressionDecomposition(true);
        }
        s.read(m);
        try {
            s.propagate();
            s.solve();
            int nbSolution = 0;
            do {
                nbSolution++;
                StringBuffer st = new StringBuffer();
                st.append(format("S = {0}   \t", s.getVar(S).getVal()));
                for (IntegerVariable intVar : y) {
                    st.append(format("{0} = {1}   ", intVar, s.getVar(intVar).getVal()));
                }
                LOGGER.info(st.toString());
            } while (s.nextSolution());
            assertEquals(11, nbSolution);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testBugFromentin1() {
        IntegerVariable v = makeIntVar("zou", 1, 10);
        m.addConstraint(and(Choco.TRUE, neq((v), (1))));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            fail();
        }
        assertTrue(!s.getVar(v).canBeInstantiatedTo(1));
    }

    @Test
    public void testBugFromentin2() {
        IntegerVariable v = makeIntVar("zou", 1, 10);
        m.addConstraint(and(Choco.FALSE, neq((v), (1))));
        s.read(m);
        try {
            s.propagate();
            assertTrue(false);
        } catch (ContradictionException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testBugFromentin2Decomp() {
        IntegerVariable v = makeIntVar("zou", 1, 10);
        m.setDefaultExpressionDecomposition(true);
        m.addConstraint(and(Choco.FALSE, neq((v), (1))));
        s.read(m);
        try {
            s.propagate();
            assertTrue(false);
        } catch (ContradictionException e) {
            assertTrue(true);
        }
    }

    @Test
    @Ignore
    public void testJmm3bool() {

        IntegerConstantVariable betaBlocker = constant("Beta-blocker", 1);
        IntegerConstantVariable copd = constant("Chronic Obstructive Lung Disease", 1);
        IntegerConstantVariable verapamil = constant("Verapamil", 0);

        HashMap<Constraint, String> cardioConstraints = new HashMap<Constraint, String>();

        /*ArrayList infeasTuple = new ArrayList();
                  infeasTuple.add(new int[]{1, 1});
                  Constraint cv4 = infeasPairAC(betaBlocker, copd, infeasTuple, 2001);
                  Constraint cv5 = infeasPairAC(betaBlocker, verapamil, infeasTuple, 2001);
                  */

        //No Beta Blocker combined with COPD
        Constraint cv4 = not(and(eq((betaBlocker), (1)), eq((copd), (1))));
        //No Beta Blocker combined with Verapamil

        Constraint cv5 = not(and(eq((betaBlocker), (1)), eq((verapamil), (1))));


        cardioConstraints.put(cv4, "Beta Blocker with Chronic Obscructive Lung Disease. (risk of bronchospasm)");
        cardioConstraints.put(cv5, "Beta blocker with verapamil. (risk of symptomatic heart block)");

        m.addConstraint(cv4);
        m.addConstraint(cv5);
        s.read(m);
        s.solve();

        if (s.getNbSolutions() > 0) {
            LOGGER.info("Prescription Acceptable");
        } else {
            LOGGER.info("Prescription Unacceptable");
            Iterator it = s.getIntConstraintIterator();
            while (it.hasNext()) {
                SConstraint c = (SConstraint) it.next();
                //LOGGER.info(""  + c);
                if (!c.isSatisfied()) {
                    LOGGER.info(format("Failed: {0}", cardioConstraints.get(c)));
                }
            }
        }
    }

    @Test
    public void testBooleanConstraints() {
        for (int seed = 0; seed < 100; seed++) {
            CPModel m = new CPModel();
            CPSolver s = new CPSolver();

            IntegerVariable b = makeIntVar("b", 0, 1);
            IntegerVariable[] tab = makeIntVarArray("t", 10, 0, 1);
            m.addVariables(tab);
            m.addVariable(b);
            s.read(m);
            IntDomainVar[] tabs = new IntDomainVar[tab.length];
            for (int i = 0; i < tabs.length; i++) {
                tabs[i] = s.getVar(tab[i]);
            }
            s.post(new ReifiedLargeOr(s.getVar(b), tabs));

            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed + 1));

            s.solveAll();
            LOGGER.info("" + s.getNbSolutions());
            assertTrue(s.getNbSolutions() == 1024);
        }

    }

    @Test
    public void testBugNormalized() {
        for (int seed = 0; seed < 10; seed++) {
            LOGGER.info("seed:" + seed);
            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable[] vars = new IntegerVariable[26];
            vars[0] = makeIntVar("v0", 31, 31);
            vars[1] = makeIntVar("v1", 60, 60);

            for (int i = 2; i < 26; i = i + 3) {
                vars[i] = makeIntVar("vA" + i, 0, 60);
                vars[i + 1] = makeIntVar("vB" + i, 0, 31);
                vars[i + 2] = makeIntVar("vC" + i, 0, 1);
            }

            // Predicat 1
            m.addConstraint(predicat1(vars[3], vars[4], 7, 5, vars[0]));
            m.addConstraint(predicat1(vars[6], vars[7], 14, 5, vars[0]));
            m.addConstraint(predicat1(vars[9], vars[10], 14, 8, vars[0]));
            m.addConstraint(predicat1(vars[12], vars[13], 4, 8, vars[0]));
            m.addConstraint(predicat1(vars[15], vars[16], 21, 13, vars[0]));
            m.addConstraint(predicat1(vars[18], vars[19], 7, 11, vars[0]));
            m.addConstraint(predicat1(vars[21], vars[22], 14, 11, vars[0]));
            m.addConstraint(predicat1(vars[24], vars[25], 14, 5, vars[0]));
            m.addConstraint(predicat1(vars[2], vars[4], 5, 7, vars[1]));
            m.addConstraint(predicat1(vars[5], vars[7], 5, 14, vars[1]));
            m.addConstraint(predicat1(vars[8], vars[10], 8, 14, vars[1]));
            m.addConstraint(predicat1(vars[11], vars[13], 8, 4, vars[1]));
            m.addConstraint(predicat1(vars[14], vars[16], 13, 21, vars[1]));
            m.addConstraint(predicat1(vars[17], vars[19], 11, 7, vars[1]));
            m.addConstraint(predicat1(vars[20], vars[22], 11, 14, vars[1]));
            m.addConstraint(predicat1(vars[23], vars[25], 5, 14, vars[1]));

            //Predicat 2
            m.addConstraint(predicat2(vars[11], vars[12], vars[13], 8, 4, vars[14], vars[15], vars[16], 13, 21));
            m.addConstraint(predicat2(vars[11], vars[12], vars[13], 8, 4, vars[17], vars[18], vars[19], 11, 7));
            m.addConstraint(predicat2(vars[11], vars[12], vars[13], 8, 4, vars[20], vars[21], vars[22], 11, 14));
            m.addConstraint(predicat2(vars[11], vars[12], vars[13], 8, 4, vars[23], vars[24], vars[25], 5, 14));
            m.addConstraint(predicat2(vars[14], vars[15], vars[16], 13, 21, vars[17], vars[18], vars[19], 11, 7));
            m.addConstraint(predicat2(vars[14], vars[15], vars[16], 13, 21, vars[20], vars[21], vars[22], 11, 14));
            m.addConstraint(predicat2(vars[14], vars[15], vars[16], 13, 21, vars[23], vars[24], vars[25], 5, 14));
            m.addConstraint(predicat2(vars[17], vars[18], vars[19], 11, 7, vars[20], vars[21], vars[22], 11, 14));
            m.addConstraint(predicat2(vars[17], vars[18], vars[19], 11, 7, vars[23], vars[24], vars[25], 5, 14));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[11], vars[12], vars[13], 8, 4));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[14], vars[15], vars[16], 13, 21));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[17], vars[18], vars[19], 11, 7));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[20], vars[21], vars[22], 11, 14));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[23], vars[24], vars[25], 5, 14));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[5], vars[6], vars[7], 5, 14));
            m.addConstraint(predicat2(vars[2], vars[3], vars[4], 5, 7, vars[8], vars[9], vars[10], 8, 14));
            m.addConstraint(predicat2(vars[20], vars[21], vars[22], 11, 14, vars[23], vars[24], vars[25], 5, 14));
            m.addConstraint(predicat2(vars[5], vars[6], vars[7], 5, 14, vars[11], vars[12], vars[13], 8, 4));
            m.addConstraint(predicat2(vars[5], vars[6], vars[7], 5, 14, vars[14], vars[15], vars[16], 13, 21));
            m.addConstraint(predicat2(vars[5], vars[6], vars[7], 5, 14, vars[17], vars[18], vars[19], 11, 7));
            m.addConstraint(predicat2(vars[5], vars[6], vars[7], 5, 14, vars[20], vars[21], vars[22], 11, 14));
            m.addConstraint(predicat2(vars[5], vars[6], vars[7], 5, 14, vars[23], vars[24], vars[25], 5, 14));
            m.addConstraint(predicat2(vars[5], vars[6], vars[7], 5, 14, vars[8], vars[9], vars[10], 8, 14));
            m.addConstraint(predicat2(vars[8], vars[9], vars[10], 8, 14, vars[11], vars[12], vars[13], 8, 4));
            m.addConstraint(predicat2(vars[8], vars[9], vars[10], 8, 14, vars[14], vars[15], vars[16], 13, 21));
            m.addConstraint(predicat2(vars[8], vars[9], vars[10], 8, 14, vars[17], vars[18], vars[19], 11, 7));
            m.addConstraint(predicat2(vars[8], vars[9], vars[10], 8, 14, vars[20], vars[21], vars[22], 11, 14));
            m.addConstraint(predicat2(vars[8], vars[9], vars[10], 8, 14, vars[23], vars[24], vars[25], 5, 14));


            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.setGeometricRestart(30, 1.1);
            s.solve();
            LOGGER.info(format("{0}", s.getNodeCount()));
            Assert.assertTrue("Solution incorrecte", s.checkSolution(false));
        }
    }


    private Constraint predicat1(IntegerVariable v1, IntegerVariable v2, int v3, int v4, IntegerVariable v5) {
        //e.setDecomposeExp(true);
        return leq(plus(v1, ifThenElse(eq((v2), (0)), constant(v3), constant(v4))), v5);
    }

    private Constraint predicat2(IntegerVariable v0, IntegerVariable v1, IntegerVariable v2, int v3, int v4, IntegerVariable v5, IntegerVariable v6, IntegerVariable v7, int v8, int v9) {
        //e.setDecomposeExp(true);
        return or(or(leq(plus((v0),
                ifThenElse(eq((v2), (0)), constant(v3), constant(v4))), (v5)), leq(plus((v5), ifThenElse(eq((v7), (0)), constant(v8), constant(v9))), (v0))), or(leq(plus((v1), ifThenElse(eq((v2), constant(0)), constant(v4), constant(v3))), (v6)), leq(plus((v6), ifThenElse(eq((v7), (0)), constant(v9), constant(v8))), (v1))));
    }

    @Test
    public void deterministicOnRCPSP() {
        for (int seed = 0; seed < 20; seed++) {
            LOGGER.info(format("seed:{0}", seed));
            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable[] vars = new IntegerVariable[20];
            int[] max = new int[]{12, 15, 15, 14, 14, 11, 15, 13, 14, 12, 15, 13, 13, 15, 12, 14, 13, 13, 11, 12};
            for (int i = 0; i < 20; i++) {
                vars[i] = makeIntVar("v" + i, 0, max[i]);
            }
            m.addConstraint(predicat3(vars[0], 4, vars[2]));
            m.addConstraint(predicat3(vars[1], 1, vars[18]));
            m.addConstraint(predicat3(vars[10], 1, vars[12]));
            m.addConstraint(predicat3(vars[11], 3, vars[15]));
            m.addConstraint(predicat3(vars[16], 3, vars[17]));
            m.addConstraint(predicat3(vars[2], 1, vars[10]));
            m.addConstraint(predicat3(vars[2], 1, vars[12]));
            m.addConstraint(predicat3(vars[3], 2, vars[6]));
            m.addConstraint(predicat3(vars[3], 2, vars[9]));
            m.addConstraint(predicat3(vars[4], 2, vars[14]));
            m.addConstraint(predicat3(vars[5], 5, vars[8]));
            m.addConstraint(predicat3(vars[7], 3, vars[17]));
            m.addConstraint(predicat3(vars[7], 3, vars[18]));
            m.addConstraint(predicat3(vars[7], 3, vars[18]));
            m.addConstraint(predicat3(vars[8], 2, vars[10]));

            int[] durations = new int[]{4, 1, 1, 2, 2, 5, 1, 3, 2, 4, 1, 3, 3, 1, 4, 2, 3, 3, 5, 4};
            int[] heights_1 = new int[]{0, 0, 1, 3, 3, 3, 1, 1, 0, 2, 1, 1, 1, 0, 2, 2, 2, 3, 2, 2};
            int[] heights_2 = new int[]{3, 1, 2, 3, 0, 2, 1, 2, 2, 0, 1, 0, 3, 0, 2, 3, 0, 3, 0, 2};
            int[] heights_3 = new int[]{1, 3, 2, 0, 1, 1, 1, 0, 0, 0, 3, 0, 2, 2, 0, 0, 2, 2, 1, 1};

            IntegerVariable[] ends = new IntegerVariable[20];
            IntegerVariable[] durs = new IntegerVariable[20];
            for (int i = 0; i < 20; i++) {
                ends[i] = makeIntVar("end", vars[i].getLowB() + durations[i], vars[i].getUppB() + durations[i]);
                durs[i] = constant("dur", durations[i]);
            }

            int capa = 6;
            final String option = SettingType.TASK_INTERVAL.getOptionName();
            final TaskVariable[] tasks = Choco.makeTaskVarArray("T", vars, ends, durs);
            m.addConstraint(cumulativeMax(tasks, heights_1, capa, option));
            m.addConstraint(cumulativeMax(tasks, heights_2, capa, option));
            m.addConstraint(cumulativeMax(tasks, heights_3, capa, option));

            s.read(m);

            s.setFirstSolution(true);
            s.attachGoal(new ImpactBasedBranching(s, s.getVar(vars)));
            s.generateSearchStrategy();
            s.launch();
            Assert.assertEquals("Nb sol incorrect", 1, s.getNbSolutions());
            Assert.assertTrue("Solution incorrect", s.checkSolution(false));
            //Assert.assertEquals("Nb nodes incorrect", 92, s.getSearchStrategy().getNodeCount());

        }
    }

    private Constraint predicat3(IntegerVariable v0, int v1, IntegerVariable v2) {
        return leq(plus((v0), (v1)), (v2));
    }

    @Test
    public void testSquareRoot() {
        IntegerVariable v = makeIntVar("v", 196, 196);
        IntegerVariable square = makeIntVar("square", 0, 20);
        IntegerExpressionVariable iev = new IntegerExpressionVariable("", SqrtManager.class, v);
        m.addConstraint(eq(square, iev));
        s.read(m);
        s.solveAll();
        assertEquals("square value", s.getVar(square).getVal(), 14);
        assertEquals("square nb sol", s.getNbSolutions(), 1);
    }

    @Test
    public void testSquareRoot2() {
        int xa = 5;
        int ya = 2;
        int xb = 8;
        int yb = 4;

        IntegerVariable x1 = makeIntVar("x1", xa, xa);
        IntegerVariable y1 = makeIntVar("x2", ya, ya);
        IntegerVariable x2 = makeIntVar("y1", xb, xb);
        IntegerVariable y2 = makeIntVar("y2", yb, yb);

        IntegerVariable dist = makeIntVar("dist", 0, 20);
        IntegerExpressionVariable iev = new IntegerExpressionVariable("", SqrtManager.class, plus(
                mult(
                        minus(x2, x1), minus(x2, x1)
                ),
                mult(
                        minus(y2, y1), minus(y2, y1)
                )
        ));
        m.addConstraint(eq(dist, iev));
        s.read(m);
        s.solveAll();
        // Math check
        double dist2 = Math.sqrt((xb - xa) * (xb - xa) + (yb - ya) * (yb - ya));
        int roundDist2 = (int) Math.round(dist2);
        LOGGER.info("Choco: " + s.getVar(dist).getVal());
        LOGGER.info("Math: " + roundDist2 + " (" + dist2 + ")");
        assertEquals("square value", s.getVar(dist).getVal(), roundDist2);
        assertEquals("square nb sol", s.getNbSolutions(), 1);
    }


    @Test
    public void testPower() {
        m = new CPModel();
        s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 10);
        IntegerVariable y = makeIntVar("y", 2, 4);
        IntegerVariable z = makeIntVar("z", 28, 80);
        m.addConstraint(eq(z, power(x, y)));
        s.read(m);
        s.solveAll();
        Assert.assertEquals("nb sol", 4, s.getNbSolutions());
    }

//    @Test
//    public void testReifyingDistance() {
//        for (int seed = 0; seed < 3; seed++) {
//            CPModel m = new CPModel();
//            CPSolver s = new CPSolver();
//            m.setDefaultExpressionDecomposition(true);
//            IntegerVariable v0 = makeIntVar("v0", 1, 4, "cp:bound");
//            IntegerVariable v1 = makeIntVar("v1", 5, 7, "cp:bound");
//            IntegerVariable v2 = makeIntVar("v2", -100, 100, "cp:bound");
//            m.addConstraint(not(distanceEQ(v0, v1, v2, 0)));
//            s.read(m);
//            s.solveAll();
//            LOGGER.info("" + s.getNbSolutions());
//            assertEquals(2400, s.getNbSolutions());
//        }
//    }

    @Test
    public void testBugITE1() {
        CPModel m = new CPModel();
        CPSolver s = new CPSolver();

        IntegerVariable x = makeIntVar("x", 1, 3);
        IntegerVariable y = makeIntVar("y", 1, 3);
        IntegerVariable z = makeIntVar("z", 1, 3);

        m.addConstraint(leq(z, ifThenElse(lt(x, y), minus(y, x), constant(1))));

        s.read(m);
        s.solveAll();
        Assert.assertNotNull("x is not kown in the Solver", s.getVar(x));
        Assert.assertNotNull("y is not kown in the Solver", s.getVar(x));
        Assert.assertNotNull("z is not kown in the Solver", s.getVar(x));
        Assert.assertEquals("nb of solutions", s.getNbSolutions(), 10);
    }

    @Test
    public void testBugITE2() {
        CPModel m = new CPModel();
        CPSolver s = new CPSolver();

        IntegerVariable x = makeIntVar("x", 1, 3);
        IntegerVariable y = makeIntVar("y", 1, 3);
        IntegerVariable z = makeIntVar("z", 1, 3);

        m.addConstraint(leq(z, ifThenElse(lt(x, y), constant(2), constant(1))));

        s.read(m);
        s.solveAll();
        Assert.assertNotNull("x is not kown in the Solver", s.getVar(x));
        Assert.assertNotNull("y is not kown in the Solver", s.getVar(x));
        Assert.assertNotNull("z is not kown in the Solver", s.getVar(x));
        Assert.assertEquals("nb of solutions", s.getNbSolutions(), 12);
    }


    @Test
    public void testBugReifiedOnScalar() {

        ConstraintType[] types = {EQ, NEQ, GEQ, LEQ, GT, LT};
        for (int seed = 0; seed < 20; seed++) {
            LOGGER.info(format("{0}", seed));
            CPModel m = new CPModel();
            CPSolver s = new CPSolver();

            int n = 6;

            IntegerVariable x = makeIntVar("x", 0, 1);
            IntegerVariable[] y = makeIntVarArray("y", n, 0, n);

            int[] coefs = new int[n];
            Random r = new Random();
            for (int i = 0; i < n; i++) {
                coefs[i] = r.nextInt(n);
            }

            Constraint c = new ComponentConstraint(EQ, types[r.nextInt(6)], new Variable[]{constant(n * n / 2), scalar(y, coefs)});
            m.addConstraints(reifiedIntConstraint(x, c));

            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solve();
            Assert.assertTrue("unsatisfied", s.checkSolution(false));
        }
    }

    @Test
    public void testBugReifiedOnScalar01() {

        ConstraintType[] types = {EQ, NEQ, GEQ, LEQ, GT, LT};
        for (int seed = 0; seed < 20; seed++) {
            CPModel m = new CPModel();
            CPSolver s = new CPSolver();

            int n = 6;

            IntegerVariable x = makeIntVar("x", 0, 1);
            IntegerVariable[] y = makeIntVarArray("y", n, 0, n);

            int[] coefs = new int[n];
            Random r = new Random();
            for (int i = 0; i < n; i++) {
                coefs[i] = r.nextInt(2);
            }

            Constraint c = new ComponentConstraint(EQ, types[r.nextInt(6)], new Variable[]{constant(n * n / 2), scalar(y, coefs)});
            m.addConstraints(reifiedIntConstraint(x, c));

            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solve();
            Assert.assertTrue("unsatisfied", s.checkSolution(false));
        }
    }

    @Test
    public void testBugReifiedOnSum() {

        ConstraintType[] types = {EQ, NEQ, GEQ, LEQ, GT, LT};
        for (int seed = 0; seed < 200; seed++) {
            CPModel m = new CPModel();
            CPSolver s = new CPSolver();

            int n = 6;

            IntegerVariable x = makeIntVar("x", 0, 1);
            IntegerVariable[] y = makeIntVarArray("y", n, 0, n);

            Random r = new Random(seed);
            Constraint c = new ComponentConstraint(EQ, types[r.nextInt(6)], new Variable[]{constant(n * n / 2), sum(y)});
            m.addConstraints(reifiedIntConstraint(x, c));

            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solve();
            Assert.assertTrue("unsatisfied", s.checkSolution(false));
        }
    }

    @Test
    public void testBugReifiedOnScalarNeg() {

        ConstraintType[] types = {EQ, NEQ, GEQ, LEQ, GT, LT};
        for (int seed = 0; seed < 200; seed++) {
            LOGGER.info(format("{0}", seed));
            CPModel m = new CPModel();
            CPSolver s = new CPSolver();

            int n = 6;

            IntegerVariable x = makeIntVar("x", 0, 1);
            IntegerVariable[] y = makeIntVarArray("y", n, 0, n);

            int[] coefs = new int[n];
            Random r = new Random();
            for (int i = 0; i < n; i++) {
                coefs[i] = n / 2 - r.nextInt(n);
            }

            Constraint c = new ComponentConstraint(EQ, types[r.nextInt(6)], new Variable[]{constant(n * n / 2), scalar(y, coefs)});
            m.addConstraints(reifiedIntConstraint(x, c));

            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.solve();
            Assert.assertTrue("unsatisfied", s.checkSolution(false));
        }
    }

    @Test
    public void AndTest() {
        Model m1 = new CPModel();
        Solver s1 = new CPSolver();

        Model m2 = new CPModel();
        Solver s2 = new CPSolver();

        IntegerVariable v1 = makeIntVar("v1", 1, 3);
        IntegerVariable v2 = makeIntVar("v2", 1, 3);
        IntegerVariable v3 = makeIntVar("v3", 1, 3);

        m1.setDefaultExpressionDecomposition(true);
        m1.addConstraint(and(gt(v1, v2), gt(v2, v3)));
        s1.read(m1);
        s1.solve();
        m2.addConstraints(gt(v1, v2), gt(v2, v3));
        s2.read(m2);
        s2.solve();

        Assert.assertEquals("v1", s1.getVar(v1).getVal(), s2.getVar(v1).getVal());
        Assert.assertEquals("v2", s1.getVar(v2).getVal(), s2.getVar(v2).getVal());
        Assert.assertEquals("v3", s1.getVar(v3).getVal(), s2.getVar(v3).getVal());
    }

    @Test
    public void LazovicCt1() {
        for (int seed = 0; seed < 3; seed++) {
            int n = 4;
            int numStates = 5;
            int numLabels = 3;
            Random rand = new Random(0);
            CPModel model = new CPModel();
            model.setDefaultExpressionDecomposition(true);

            IntegerVariable[] svars = new IntegerVariable[n];
            for (int i = 0; i < n; i++) {
                IntegerVariable sv = makeIntVar("..", 0, numStates, "cp:enum");
                model.addVariable("cp:decision", sv);
                svars[i] = sv;
            }

            // initial state variable
            model.addConstraint(eq(svars[0], rand.nextInt(numStates + 1)));

            // create transition variables
            IntegerVariable[] lvars = new IntegerVariable[n - 1];
            for (int i = 0; i < n - 1; i++) {
                IntegerVariable lv = makeIntVar("..", 0, numLabels, "cp:enum");
                model.addVariable("cp:decision", lv);
                lvars[i] = lv;
            }

            // if state si is sj, then lv can only be one of its transitions,
            // and following state according to that transition
            // it is true for each state variable, except of the last on
            for (int i = 0; i < n - 1; i++) {
                IntegerVariable sv = svars[i];
                IntegerVariable svx = svars[i + 1];
                IntegerVariable lv = lvars[i];
                for (int j = 0; j < numStates; j++) {
                    Constraint a = eq(sv, j);
                    Constraint[] b = new Constraint[rand.nextInt(20) + 1];
                    for (int k = 0; k < b.length; k++) {
                        int ln = rand.nextInt(numLabels + 1);
                        int sn = rand.nextInt(numStates + 1);
                        b[k] = and(eq(lv, ln), eq(svx, sn));
                    }
                    model.addConstraint(implies(a, or(b)));
                }
            }


            CPSolver solver = new CPSolver();
            solver.read(model);
            IntDomainVar[] decVars = new IntDomainVar[n + n - 1];
            System.arraycopy(solver.getVar(svars), 0, decVars, 0, n);
            System.arraycopy(solver.getVar(lvars), 0, decVars, n, n - 1);

            solver.setVarIntSelector(new RandomIntVarSelector(solver, decVars, seed));
            solver.setValIntSelector(new RandomIntValSelector());
            LOGGER.info("solve nbv: " + solver.getNbIntVars() + " nbc: " + solver.getNbIntConstraints());
            solver.solveAll();
            Assert.assertEquals(solver.getNbSolutions(), 1166);
            LOGGER.info(" " + solver.getNbSolutions() + " " + solver.getTimeCount() + " " + solver.getNodeCount());
        }
    }

    @Test
    public void LazovicCt2() {
        for (int seed = 0; seed < 10; seed++) {
            int n = 10;
            int numStates = 2;
            CPModel model = new CPModel();
            model.setDefaultExpressionDecomposition(true);

            IntegerVariable[] svars = new IntegerVariable[n];
            for (int i = 0; i < n; i++) {
                IntegerVariable sv = makeIntVar("..", 0, numStates, "cp:enum");
                model.addVariable("cp:decision", sv);
                svars[i] = sv;
            }

            Constraint[] b = new Constraint[n - 1];
            for (int i = 0; i < n - 1; i++) {
                b[i] = eq(svars[i], svars[n - 1]);
            }
            model.addConstraint(or(b));
            CPSolver solver = new CPSolver();
            solver.read(model);
            solver.setVarIntSelector(new RandomIntVarSelector(solver, seed));
            solver.setValIntSelector(new RandomIntValSelector());
            LOGGER.info("solve nbv: " + solver.getNbIntVars() + " nbc: " + solver.getNbIntConstraints());
            solver.solveAll();
            Assert.assertEquals(solver.getNbSolutions(), 57513);
            LOGGER.info(" " + solver.getNbSolutions() + " " + solver.getTimeCount());
        }
    }

    @Test
    public void LazovicCt3() {
        for (int seed = 0; seed < 10; seed++) {
            int n = 5;
            int numStates = 7;
            int bns = 3;
            Random rand = new Random(0);
            CPModel model = new CPModel();
            model.setDefaultExpressionDecomposition(true);

            IntegerVariable[] svars = new IntegerVariable[n];
            for (int i = 0; i < n; i++) {
                IntegerVariable sv = makeIntVar("..", 0, numStates, "cp:enum");
                model.addVariable(sv);
                svars[i] = sv;
            }

            Constraint[] failCon = new Constraint[n * bns];
            int z = 0;
            for (int i = 0; i < n; i++) {
                IntegerVariable sv = svars[i];
                for (int j = 0; j < bns; j++) {
                    failCon[z++] = eq(sv, rand.nextInt(numStates));
                }
            }
            model.addConstraint(or(failCon));

            CPSolver solver = new CPSolver();
            solver.read(model);
            solver.setVarIntSelector(new RandomIntVarSelector(solver, seed));
            solver.setValIntSelector(new RandomIntValSelector());

            solver.solveAll();
            Assert.assertEquals(solver.getNbSolutions(), 29018);
            LOGGER.info(" " + solver.getNbSolutions() + " " + solver.getTimeCount());
        }
    }

    @Test
    public void wendlmar1Test() {
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable v1 = makeIntVar("v1", -10, 10);
        IntegerVariable v2 = makeIntVar("v2", -10, 10);

        m.addConstraint(and(abs(v1, v2), eq(v1, abs(v2))));

        s.read(m);
        s.solveAll();
        Assert.assertEquals("nb solutions", 21, s.getNbSolutions());
    }


    @Test
    public void wendlmar2Test() {
        Operator[] op = {Operator.MIN, Operator.MAX};
        ConstraintType[] co = {ConstraintType.MIN, ConstraintType.MAX};

        for (int seed = 0; seed < 6; seed++) {
            Random r = new Random(seed);
            int type = r.nextInt(2);

            Model m = new CPModel();
            Solver s = new CPSolver();

            IntegerVariable v1 = makeIntVar("v1", -r.nextInt(10), r.nextInt(10));
            IntegerVariable v2 = makeIntVar("v2", -r.nextInt(10), r.nextInt(10));
            IntegerVariable v3 = makeIntVar("v3", -r.nextInt(10), r.nextInt(10));

            Constraint c1 = new ComponentConstraint(co[type], co[type].equals(ConstraintType.MIN), new IntegerVariable[]{v1, v2, v3});
            Constraint c2 = new ComponentConstraint(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{new IntegerExpressionVariable(null, op[type], v1, v2), v3});

            m.addConstraint(and(c1, c2));

            s.read(m);
            s.solveAll();
            Assert.assertTrue("nb solutions", s.getNbSolutions() > 0);
        }
    }

    @Test
    public void wendlmar3Test() {
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable v1 = makeIntVar("v1", 0, 10);
        IntegerVariable v2 = makeIntVar("v2", 0, 10);

        m.addConstraint(and(mod(v1, v2, 1), eq(v1, mod(v2, 1))));

        s.read(m);
        s.solveAll();
        LOGGER.info(s.pretty());
        Assert.assertEquals("nb solutions", 11, s.getNbSolutions());
    }

    @Test
    public void wendlmar4Test() {
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable v1 = makeIntVar("v1", 0, 10);
        IntegerVariable v2 = makeIntVar("v2", 0, 10);
        IntegerVariable v3 = makeIntVar("v3", 0, 10);

        m.addConstraint(and(times(v1, v2, v3), eq(v3, mult(v1, v2))));

        s.read(m);
        s.solveAll();
        LOGGER.info(s.pretty());
        Assert.assertEquals("nb solutions", 48, s.getNbSolutions());
    }

    @Test
    public void wendlmar5Test() {
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable v1 = makeIntVar("v1", 0, 10);
        IntegerVariable v2 = makeIntVar("v2", 1, 10);
        IntegerVariable v3 = makeIntVar("v3", 0, 10);

        m.addConstraint(and(intDiv(v1, v2, v3), eq(v3, div(v1, v2))));

        s.read(m);
        s.solveAll();
        LOGGER.info(s.pretty());
        Assert.assertEquals("nb solutions", 110, s.getNbSolutions());
    }

    @Test
    public void bugSavoureyTest() {
        /**
         * Bug on IfThenElseManager...
         */
        int n = 4;
        Model model = new CPModel();

        IntegerVariable[] x = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            x[i] = makeIntVar("x" + i, 0, n);
            model.addVariable(x[i]);
        }

        IntegerExpressionVariable y = min(x);

        IntegerExpressionVariable w = ifThenElse(leq(y, 5), constant(1), constant(0));
        Constraint t = ifThenElse(Choco.TRUE, Choco.TRUE, Choco.FALSE);

        model.addConstraint(t);
        model.addConstraint(Choco.eq(w, 1));

        Solver s = new CPSolver();
        s.read(model);
        s.solveAll();
        Assert.assertEquals("nb de solutions", 625, s.getNbSolutions());
    }


    @Test
    public void bugCedric1() {
        for (int seed = 0; seed < 10; seed++) {

            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable x = makeIntVar("x", 0, 1);
            IntegerVariable y = makeIntVar("y", 0, 1);
            IntegerVariable z = makeIntVar("z", 0, 1);
            IntegerVariable h = makeIntVar("h", 0, 4);
            //m.addConstraint(reifiedIntConstraint(x, eq(y, z)));
            m.addConstraint(ifThenElse(geq(h, 2), reifiedIntConstraint(x, eq(y, z)), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            //LOGGER.info(s.getNbSolutions() + " ");
            Assert.assertEquals("nb de solutions", 28, s.getNbSolutions());
            //LOGGER.info(s.getVar(h) + " " + s.getVar(x) + " " + s.getVar(y) + " " + s.getVar(z));

        }

    }

     @Test
    public void testIsEntailedMax() {
        for (int seed = 0; seed < 5; seed++) {

            int n = 4;
            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable h = makeIntVar("x", 0, 3);
            IntegerVariable[] tab = makeIntVarArray("x",n, 0, 3);
            IntegerVariable max = makeIntVar("max", -5, 5);

            m.addConstraint(ifThenElse(geq(h, 2), Choco.max(tab,max), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            LOGGER.info(s.getNbSolutions() + " ");
            Assert.assertEquals("nb de solutions", 6144, s.getNbSolutions());
            //LOGGER.info(s.getVar(h) + " " + s.getVar(x) + " " + s.getVar(y) + " " + s.getVar(z));

        }

    }

     @Test
    public void testIsEntailedMin() {
        for (int seed = 0; seed < 5; seed++) {

            int n = 4;
            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable h = makeIntVar("x", 0, 3);
            IntegerVariable[] tab = makeIntVarArray("x",n, 0, 3);
            IntegerVariable max = makeIntVar("max", -5, 5);

            m.addConstraint(ifThenElse(geq(h, 2), Choco.min(tab,max), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            LOGGER.info(s.getNbSolutions() + " " + s.getNodeCount());
            
            Assert.assertEquals("nb de solutions", 6144, s.getNbSolutions());
        }

    }

        @Test
    public void testIsEntailedOccuMin() {
        for (int seed = 0; seed < 5; seed++) {

            int n = 4;
            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable h = makeIntVar("x", 0, 3);
            IntegerVariable[] tab = makeIntVarArray("x",n, 0, 3);
            IntegerVariable max = makeIntVar("max", -5, 5);

            m.addConstraint(ifThenElse(geq(h, 2), Choco.occurrenceMin(2,max,tab), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            LOGGER.info(s.getNbSolutions() + " " + s.getNodeCount());

            Assert.assertEquals("nb de solutions", 9216, s.getNbSolutions());
        }

    }

         @Test
    public void testIsEntailedOccuEq() {
        for (int seed = 0; seed < 5; seed++) {

            int n = 4;
            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable h = makeIntVar("x", 0, 3);
            IntegerVariable[] tab = makeIntVarArray("x",n, 0, 3);
            IntegerVariable max = makeIntVar("max", -5, 5);

            m.addConstraint(ifThenElse(geq(h, 2), Choco.occurrence(3,max,tab), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            LOGGER.info(s.getNbSolutions() + " " + s.getNodeCount());

            Assert.assertEquals("nb de solutions", 6144, s.getNbSolutions());
        }

    }

         @Test
    public void testIsEntailedSameSignOp() {
        for (int seed = 0; seed < 5; seed++) {

            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable c1 = makeIntVar("x", 1, 2);
            IntegerVariable h1 = makeIntVar("x", -1, 1);
            IntegerVariable h2 = makeIntVar("x", -1, 1);


            m.addConstraint(ifThenElse(geq(c1, 2), Choco.sameSign(h1,h2), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            LOGGER.info(s.getNbSolutions() + " " + s.getNodeCount());

            Assert.assertEquals("nb de solutions", 16, s.getNbSolutions());
        }

    }

        @Test
    public void testIsEntailedSignOp() {
        for (int seed = 0; seed < 5; seed++) {

            CPModel m = new CPModel();
            m.setDefaultExpressionDecomposition(true);
            CPSolver s = new CPSolver();
            IntegerVariable c1 = makeIntVar("x", 1, 2);
            IntegerVariable h1 = makeIntVar("x", -1, 1);
            IntegerVariable h2 = makeIntVar("x", -1, 1);


            m.addConstraint(ifThenElse(geq(c1, 2), Choco.oppositeSign(h1,h2), Choco.TRUE));
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector());

            s.solveAll();
            LOGGER.info(s.getNbSolutions() + " " + s.getNodeCount());

            Assert.assertEquals("nb de solutions", 11, s.getNbSolutions());
        }

    }


    @Test
   public void testCEDRIC() {
      CPModel m = new CPModel();
      m.setDefaultExpressionDecomposition(true);
      IntegerVariable x = makeIntVar("x", 0, 1);
      IntegerVariable y = makeIntVar("y", 0, 1);
      IntegerVariable z = makeIntVar("z", 0, 1);
      IntegerVariable t = makeIntVar("t", 0, 1);
      m.addConstraint(reifiedIntConstraint(t, eq(x,0), eq(y,0)));
      m.addConstraint(reifiedIntConstraint(t,eq(z,1)));
      m.addConstraint(eq(x,0));
 
      CPSolver s = new CPSolver();
      s.read(m);
      s.solveAll();
        LOGGER.info("" + s.getNbSolutions());
      Assert.assertEquals("nb de solutions", 2, s.getNbSolutions());
  }

     @Test
   public void testCEDRIC2() {
      CPModel m = new CPModel();
      m.setDefaultExpressionDecomposition(true);
      IntegerVariable x = makeIntVar("x", 0, 1);
      IntegerVariable y = makeIntVar("y", 0, 1);
      IntegerVariable t = makeIntVar("t", 0, 1);
      m.addConstraint(ifThenElse(eq(x,1),eq(y,0),eq(t,0)));
      CPSolver s = new CPSolver();
      s.read(m);
      Assert.assertEquals("nb de vars", s.getNbIntVars(), 3);
  }

    @Test
   public void testCEDRIC3() {
      CPModel m = new CPModel();
      m.setDefaultExpressionDecomposition(true);
      IntegerVariable x = makeIntVar("x", 0, 1);
      IntegerVariable y = makeIntVar("y", 0, 1);
      IntegerVariable t = makeIntVar("t", 0, 1);
      m.addConstraint(ifThenElse(eq(1,x),eq(y,0),eq(t,0)));
      CPSolver s = new CPSolver();
      s.read(m);
      Assert.assertEquals("nb de vars", s.getNbIntVars(), 3);
  }

   @Test
   public void testCEDRIC4() {
      CPModel m = new CPModel();
      m.setDefaultExpressionDecomposition(true);
      IntegerVariable x = makeIntVar("x", 0, 1);
      IntegerVariable y = makeIntVar("y", 0, 1);
      IntegerVariable t = makeIntVar("t", 0, 1);
      m.addConstraint(ifThenElse(eq(0,x),eq(y,0),eq(t,0)));
      CPSolver s = new CPSolver();
      s.read(m);
      Assert.assertEquals("nb de solutions", s.getNbIntVars(), 4);
  }

    @Test
   public void testCEDRIC5() {
      CPModel m = new CPModel();
      m.setDefaultExpressionDecomposition(true);
      IntegerVariable x = makeIntVar("x", 0, 1);
      IntegerVariable y = makeIntVar("y", 0, 1);
      IntegerVariable t = makeIntVar("t", 0, 1);
      Constraint sc = ifThenElse(eq(0,x),eq(y,0),eq(t,0));

      m.addConstraint("cp:ac",sc);
      CPSolver s = new CPSolver();
      s.read(m);
      Assert.assertEquals("nb de solutions", s.getNbIntVars(), 4);
  }

    @Test
   public void testCEDRIC6() {
         for (int seed = 0; seed < 5; seed++) {
             CPModel m = new CPModel();
             m.setDefaultExpressionDecomposition(true);
             IntegerVariable x = makeIntVar("x", 0, 1);
             IntegerVariable y = makeIntVar("y", 0, 4);
             IntegerVariable z = makeIntVar("z", 0, 3);
             m.addConstraint(ifThenElse(eq(x, 1), nth(y, new int[]{1, 1, 2, 4}, z), Choco.TRUE));

             CPSolver s = new CPSolver();
             s.read(m);
             s.setVarIntSelector(new RandomIntVarSelector(s, seed));
             s.setValIntSelector(new RandomIntValSelector());

             s.solveAll();
             LOGGER.info("" + s.getNbSolutions());
             Assert.assertEquals("nb de solutions", 23, s.getNbSolutions()); 
         }
     }


}
