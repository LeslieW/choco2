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
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package samples.tutorials.trunk;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.real.CyclicRealVarSelector;
import choco.cp.solver.search.real.RealIncreasingDomain;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import samples.tutorials.PatternExample;

import static choco.Choco.*;

/**
 * Samples in Elisa package.
 */
public class Arm extends PatternExample {

    RealVariable a, b, alpha, beta, x, y, jr, ir;


    @Override
    public void buildModel() {
        model = new CPModel();

        a = makeRealVar("a", 2.0, 8.0);
        b = makeRealVar("b", 2.0, 8.0);
        alpha = makeRealVar("alpha", 0.0, Math.PI);
        beta = makeRealVar("beta", 0.0, Math.PI);
        x = makeRealVar("x", 0.0, 10.0);
        y = makeRealVar("y", 0.0, 8.0);

        IntegerVariable i = makeIntVar("i", 1, 4);
        IntegerVariable j = makeIntVar("j", 1, 4);
        ir = makeRealVar("i'", 1.0, 4.0);
        jr = makeRealVar("j'", 1.0, 4.0);
        model.addConstraint(eq(ir, i));
        model.addConstraint(eq(jr, j));

        RealExpressionVariable exp1 = minus(y, plus(mult(a, sin(alpha)), mult(b, sin(minus(alpha, beta)))));
        RealExpressionVariable exp2 = minus(x, plus(mult(a, cos(alpha)), mult(b, cos(minus(alpha, beta)))));
        model.addConstraint(eq(exp1, 0.0));
        model.addConstraint(eq(exp2, 0.0));

        model.addConstraint(leq(mult(a, cos(alpha)), 10.0));
        model.addConstraint(leq(mult(a, sin(alpha)), 8.0));

        RealExpressionVariable circle = plus(power(minus(x, 8), 2), power(minus(y, 4), 2));
        model.addConstraint(leq(circle, 4.0));

        model.addConstraint(eq(alpha, Math.PI / 6));
        RealConstantVariable v = new RealConstantVariable(1.99, 2.01);
        model.addConstraint(eq(minus(a, mult(ir, v)), 0.0));
        model.addConstraint(eq(minus(b, mult(jr, v)), 0.0));
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.setVarRealSelector(new CyclicRealVarSelector(solver));
        solver.setValRealIterator(new RealIncreasingDomain());
    }

    @Override
    public void solve() {
        solver.solve();
    }

    @Override
    public void prettyOut() {
        LOGGER.info("ARM problem is:");
        LOGGER.info(" y - ( a * sin(alpha) + b * sin(alpha - beta) ) = 0.0");
        LOGGER.info(" x - ( a * cos(alpha) + b * cos(alpha - beta) ) = 0.0");
        LOGGER.info(" a * cos(alpha) <= 10.0");
        LOGGER.info(" b * sin(alpha) <= 8.0");
        LOGGER.info(" (x - 8)² + (y - 4)² <= 4.0");
        LOGGER.info(" alpha = PI/6");
        LOGGER.info(" a - (ir * 2) = 0");
        LOGGER.info(" b - (jr * 2) = 0");
        LOGGER.info("\nWhere:");
        LOGGER.info("a = "+ solver.getVar(a).getValue());
        LOGGER.info("b = "+ solver.getVar(b).getValue());
        LOGGER.info("alpha = "+ solver.getVar(alpha).getValue());
        LOGGER.info("beta = "+ solver.getVar(beta).getValue());
        LOGGER.info("x = "+ solver.getVar(x).getValue());
        LOGGER.info("y = "+ solver.getVar(y).getValue());
        LOGGER.info("ir = "+ solver.getVar(ir).getValue());
        LOGGER.info("jr = "+ solver.getVar(jr).getValue());
    }
    
	public static void main(String[] args) {
        new Arm().execute();
    }
}