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
package choco.model.constraints.real;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.real.exp.RealCos;
import choco.cp.solver.constraints.real.exp.RealMinus;
import choco.cp.solver.constraints.real.exp.RealSin;
import choco.cp.solver.search.real.AssignInterval;
import choco.cp.solver.search.real.CyclicRealVarSelector;
import choco.cp.solver.search.real.RealIncreasingDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.real.RealVar;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.logging.Logger;

public class TrigoTest{

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    @Test
    public void test1() {
        CPSolver s = new CPSolver();
        RealVar alpha = s.createRealVal("alpha", -Math.PI, Math.PI);

        RealExp exp = new RealMinus(s,
                new RealCos(s, alpha),
                new RealSin(s, alpha));
        SConstraint c = s.makeEquation(exp, s.cst(0.0));
        LOGGER.info("c = " + c.pretty());
        s.post(s.makeEquation(exp, s.cst(0.0)));

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
        assertTrue(Math.abs(Math.cos(alpha.getInf()) - Math.sin(alpha.getInf())) < 1e-8);
    }

    @Test
    public void test1bis() {
        CPModel m = new CPModel();

        RealVariable alpha = makeRealVar("alpha", -Math.PI, Math.PI);
        Constraint exp = eq(cos(alpha), sin(alpha));
        m.addConstraint(exp);


        CPSolver s = new CPSolver();
        s.read(m);
        LOGGER.info("eq = " + s.getCstr(exp).pretty());

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
        assertTrue(Math.abs(Math.cos(s.getVar(alpha).getInf()) - Math.sin(s.getVar(alpha).getInf())) < 1e-8);
    }

    @Test
    public void test2() {
        CPSolver s = new CPSolver();
        RealVar alpha = s.createRealVal("alpha", -5.5 * Math.PI, -1.5 * Math.PI);
        RealExp exp = new RealCos(s, alpha);
        s.post(s.makeEquation(exp, s.cst(1.0)));

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
    }


    @Test
    public void test2bis() {
        CPModel m = new CPModel();

        RealVariable alpha = makeRealVar("alpha", -5.5 * Math.PI, -1.5 * Math.PI);
        m.addVariable(alpha);
        m.addConstraint(eq(cos(alpha), 1));


        CPSolver s = new CPSolver();
        s.read(m);

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
    }
}
