/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
package choco.model.constraints.reified;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 17 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class ReifiedSetSomeTest {

    @Test
    public void test_jussien1() {
        int NV = 2;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }
        IntegerVariable bool = Choco.makeBooleanVar("b");

        m.addConstraint(Choco.reifiedConstraint(bool, eq(vars[0], vars[1]), neq(vars[0], vars[1])));

        s.read(m);
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }

    @Test
    public void test_jussien2() {
        int NV = 2;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }
        IntegerVariable bool = Choco.makeBooleanVar("b");

        m.addConstraint(Choco.reifiedConstraint(bool, isIncluded(vars[0], vars[1]), isNotIncluded(vars[0], vars[1])));

        s.read(m);
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }

    @Test
    public void test_jussien3() {
        int NV = 3;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }

        m.addConstraint(Choco.implies(eq(vars[0], vars[1]), neq(vars[1], vars[2])));

        s.read(m);
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }

    @Test
    public void test_jussien3ref() {
        int NV = 3;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }
        IntegerVariable[] bool = Choco.makeBooleanVarArray("b", NV);

//        m.addConstraint(Choco.implies(eq(vars[0], vars[1]), neq(vars[1], vars[2])));
        m.addConstraint(reifiedConstraint(bool[0], eq(vars[0], vars[1]), neq(vars[0], vars[1])));
        m.addConstraint(reifiedConstraint(bool[1], neq(vars[1], vars[2]), eq(vars[1], vars[2])));
        m.addConstraint(implies(eq(bool[0],1), eq(bool[1],1)));


        s.read(m);
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }
}
