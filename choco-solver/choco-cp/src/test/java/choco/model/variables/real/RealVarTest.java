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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.model.variables.real;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 24 févr. 2010
 * Since : Choco 2.1.1
 */
public class RealVarTest {

    @Test
    public void testWayne99() {
        final Model m = new CPModel();
        final RealVariable r = makeRealVar("test_r", 0, 1.0);

        m.addConstraint(leq(r, 0.4));

        final Solver s = new CPSolver();
        s.read(m);
        s.maximize(s.getVar(r), false);
    }

    @Test
    public void testDemeter() {
        // Build the model
        final Model m = new CPModel();
        m.setPrecision(0.1);
        // Creation of an array of variables
        final double[] prices = new double[2];
        prices[0] = 2.0;
        prices[1] = 5.0;

        // For each variable, we define its name and the boundaries of its domain.
        final RealVariable pizza = makeRealVar("pizza", 0, 1000);
        final RealVariable sandwich = makeRealVar("sandwich", 0, 1000);
        final RealVariable obj = makeRealVar("obj", 0, 1000);

        final RealExpressionVariable profitPizza = mult(prices[0], pizza);
        final RealExpressionVariable profitSandwich = mult(prices[1], sandwich);
        final RealExpressionVariable sumProfit = plus(profitPizza, profitSandwich);

        // Define constraints
        final Constraint c1 = leq(pizza, 4);
        m.addConstraint(c1);
        final Constraint c2 = leq(sandwich, 3);
        m.addConstraint(c2);
        final RealExpressionVariable sum = plus(pizza, sandwich);
        final Constraint c3 = leq(sum, 6);
        m.addConstraint(c3);
        final Constraint c4 = geq(pizza, 0);
        m.addConstraint(c4);
        final Constraint c5 = geq(sandwich, 0);
        m.addConstraint(c5);
        final Constraint c6 = eq(obj, sumProfit);
        m.addConstraint(c6);


        final Solver s = new CPSolver();

        s.read(m);

        s.maximize(s.getVar(obj), true);
    }

    @Test
    public void testSciss() {
        final Model m = new CPModel();
        final RealVariable s1 = makeRealVar( "start1", 0.0, 4.0 );
        final RealVariable s2 = makeRealVar( "stop1", 0.0, 60.0 );
        m.setPrecision(0.1);

        m.addConstraint(geq( s2, plus( 2.0, s1 )));

        final Solver s = new CPSolver();
        s.read(m);
        s.solve();
    }
}
