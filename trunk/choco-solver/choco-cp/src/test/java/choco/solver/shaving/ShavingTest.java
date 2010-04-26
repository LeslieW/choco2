/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
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
package choco.solver.shaving;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 20 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class ShavingTest {

    @Test
    public void test2989765_1() {
        Model m = new CPModel();
        IntegerVariable[] pos = new IntegerVariable[4];
        for (int i = 0; i < pos.length; i++) {
            pos[i] = Choco.makeIntVar("VM" + i + "on ?", 0, 4);
            IntegerVariable [] bools = Choco.makeBooleanVarArray("VM" + i + "on ", 6);
            m.addConstraint(Choco.domainConstraint(pos[i], bools));
            m.addConstraint(Choco.neq(pos[i], 3));
            m.addConstraint(Choco.neq(pos[i], 4));
        }
        IntegerVariable nbNodes = Choco.makeIntVar("nbNodes", 3, 3, Options.V_OBJECTIVE);
        m.addConstraint(Choco.atMostNValue(pos,nbNodes));
        Solver s = new CPSolver();
        s.read(m);
        s.minimize(s.getVar(nbNodes), false);
    }

     @Test
    public void test2989765_2() {
        Model m = new CPModel();
        IntegerVariable[] pos = new IntegerVariable[4];
        for (int i = 0; i < pos.length; i++) {
            pos[i] = Choco.makeIntVar("VM" + i + "on ?", 0, 0);
            IntegerVariable [] bools = Choco.constantArray(new int[]{0,0,0,0,0,0});
            m.addConstraint(Choco.domainConstraint(pos[i], bools));
            m.addConstraint(Choco.neq(pos[i], 3));
            m.addConstraint(Choco.neq(pos[i], 4));
        }
        IntegerVariable nbNodes = Choco.makeIntVar("nbNodes", 3, 3, Options.V_OBJECTIVE);
        m.addConstraint(Choco.atMostNValue(pos,nbNodes));
        Solver s = new CPSolver();
        s.read(m);
        s.minimize(s.getVar(nbNodes), false);
    }

}
