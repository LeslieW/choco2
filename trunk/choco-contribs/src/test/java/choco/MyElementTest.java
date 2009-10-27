/* ************************************************
 *           _       _                            *
 *          |  �(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco;

import static choco.Choco.makeIntVar;
import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.MyElementManager;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 14 oct. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class MyElementTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Model m;
    Solver s;

    @Before
    public void before(){
        m = new CPModel();
        s = new CPSolver();
    }

    @Test
    public void test1(){
        int[] values = new int[]{1, 2, 0, 4, 3};
		IntegerVariable index = makeIntVar("index", -3, 10);
		IntegerVariable var = makeIntVar("value", -20, 20);

        // actions normalement effectu�es dans Choco.java
        // debut
        IntegerVariable[] vars = new IntegerVariable[values.length+2];
		for (int i = 0; i < values.length; i++) {
			vars[i] = Choco.constant(values[i]);
		}
		vars[vars.length-2] = index;
		vars[vars.length-1] = var;

        Constraint element = new ComponentConstraint(MyElementManager.class, 0, vars);
        // fin

        m.addConstraint(element);
		s.read(m);
		s.solveAll();

		assertEquals(5, s.getNbSolutions());
    }
}
