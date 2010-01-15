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
package choco.model;

import choco.Choco;
import static choco.Choco.leq;
import static choco.Choco.scalar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 15 janv. 2010
 * Time: 17:46:30
 * <p/>
 * Test suite on model reading performance
 */
public class ModelReadingTest {

    @Test
    public void test1() {
        Model m = new CPModel();
        IntegerVariable[] vvs = Choco.makeBooleanVarArray("b", 20801);
        m.addVariables(vvs);

        int n = 500;
        IntegerVariable[] vars = Choco.makeBooleanVarArray("b", n);
        int[] coeffs = new int[n];
        Arrays.fill(coeffs, 12);
        IntegerVariable[] result = Choco.makeIntVarArray("result", n, 0, 2048);

        for (int i = 0; i < 100; i++) {
            m.addConstraint(leq(scalar(vars, coeffs), result[i]));
        }
        Solver s = new CPSolver();
        long ts = -System.currentTimeMillis();
        s.read(m);
        ts += System.currentTimeMillis();
        Assert.assertTrue(ts < 5000);

    }
}
