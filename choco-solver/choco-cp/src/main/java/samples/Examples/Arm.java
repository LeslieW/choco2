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
package samples.Examples;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.real.CyclicRealVarSelector;
import choco.cp.solver.search.real.RealIncreasingDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;

import java.util.logging.Logger;

import static choco.Choco.*;

/**
 * Samples in Elisa package.
 */
public class Arm extends PatternExample{

    protected final static Logger LOGGER = ChocoLogging.getSamplesLogger();

    RealVariable a, b, alpha, beta, x, y, jr, ir;


    @Override
    public void buildModel() {
        _m = new CPModel();

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
        _m.addConstraint(eq(ir, i));
        _m.addConstraint(eq(jr, j));

        RealExpressionVariable exp1 = minus(y, plus(mult(a, sin(alpha)), mult(b, sin(minus(alpha, beta)))));
        RealExpressionVariable exp2 = minus(x, plus(mult(a, cos(alpha)), mult(b, cos(minus(alpha, beta)))));
        _m.addConstraint(eq(exp1, 0.0));
        _m.addConstraint(eq(exp2, 0.0));

        _m.addConstraint(leq(mult(a, cos(alpha)), 10.0));
        _m.addConstraint(leq(mult(a, sin(alpha)), 8.0));

        RealExpressionVariable circle = plus(power(minus(x, 8), 2), power(minus(y, 4), 2));
        _m.addConstraint(leq(circle, 4.0));

        _m.addConstraint(eq(alpha, Math.PI / 6));
        RealConstantVariable v = new RealConstantVariable(1.99, 2.01);
        _m.addConstraint(eq(minus(a, mult(ir, v)), 0.0));
        _m.addConstraint(eq(minus(b, mult(jr, v)), 0.0));
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
        _s.read(_m);
        _s.setVarRealSelector(new CyclicRealVarSelector(_s));
        _s.setValRealIterator(new RealIncreasingDomain());
    }

    @Override
    public void solve() {
        _s.solve();
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
        LOGGER.info("a = "+ _s.getVar(a).getValue());
        LOGGER.info("b = "+ _s.getVar(b).getValue());
        LOGGER.info("alpha = "+ _s.getVar(alpha).getValue());
        LOGGER.info("beta = "+ _s.getVar(beta).getValue());
        LOGGER.info("x = "+ _s.getVar(x).getValue());
        LOGGER.info("y = "+ _s.getVar(y).getValue());
        LOGGER.info("ir = "+ _s.getVar(ir).getValue());
        LOGGER.info("jr = "+ _s.getVar(jr).getValue());
    }
    
	public static void main(String[] args) {
        new Arm().execute();
    }
}