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
package parser.flatzinc.parser;

import choco.kernel.model.constraints.ComponentConstraint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static parser.flatzinc.parser.FZNParser.*;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class ConstraintTest {

    @Before
    public void before(){
        init();
    }

    @Test
    public void testIntNe(){
        TerminalParser.parse(PAR_VAR_DECL, "var 1 .. 10: a::output_var;");
        TerminalParser.parse(PAR_VAR_DECL, "var 1 .. 10: b::output_var;");
        TerminalParser.parse(CONSTRAINT, "constraint int_ne(a, b);");
        Assert.assertEquals(1, model.getNbConstraints());
        Assert.assertTrue(ComponentConstraint.class.isInstance(model.getConstraint(0)));
        solver.read(model);
        solver.solveAll();
        Assert.assertEquals(90, solver.getSolutionCount());
    }

    @Test
    public void testIntLinNe(){
        TerminalParser.parse(PAR_VAR_DECL, "var 1 .. 26: a::output_var;");
        TerminalParser.parse(PAR_VAR_DECL, "var 1 .. 26: b::output_var;");
        TerminalParser.parse(CONSTRAINT, "constraint int_lin_eq([ 1, -1 ], [ a, b ], -1);");
        Assert.assertEquals(1, model.getNbConstraints());
        Assert.assertTrue(ComponentConstraint.class.isInstance(model.getConstraint(0)));
        solver.read(model);
        solver.solveAll();
        Assert.assertEquals(25, solver.getSolutionCount());
    }

    @Test
    public void testIntLinNe2(){
        TerminalParser.parse(PAR_VAR_DECL, "array[1 .. 2] of var 1 .. 2: q;");
        TerminalParser.parse(CONSTRAINT, "constraint int_lin_eq([ 1, -1 ], [ q[1], q[2] ], -1);");
        Assert.assertEquals(1, model.getNbConstraints());
        Assert.assertTrue(ComponentConstraint.class.isInstance(model.getConstraint(0)));
        solver.read(model);
        solver.solveAll();
        Assert.assertEquals(1, solver.getSolutionCount());
    }
    
}
