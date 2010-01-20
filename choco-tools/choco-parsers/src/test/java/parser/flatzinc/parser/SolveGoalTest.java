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

import org.junit.Before;
import org.junit.Test;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 13 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class SolveGoalTest {

    FZNParser fzn;
    @Before
    public void before(){
        fzn = new FZNParser();
    }

    @Test
    public void testSatisfy(){
        TerminalParser.parse(fzn.SOLVE_GOAL, "solve satisfy;");
    }

    @Test
    public void testMaximize(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 10: a::output_var;");
        TerminalParser.parse(fzn.SOLVE_GOAL, "solve maximize a;");
    }

    @Test
    public void testMinimize(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 10: a::output_var;");
        TerminalParser.parse(fzn.SOLVE_GOAL, "solve minimize a;");
    }

    @Test
    public void testSatisfy2(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 10: a::output_var;");
        TerminalParser.parse(fzn.SOLVE_GOAL, "solve ::int_search([a],input_order,indomain_min, complete) satisfy;");
    }


    @Test
    public void testSatisfy3(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "array[1 .. 55] of var 1 .. 161: restdays;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "array[1 .. 161] of var 0 .. 3: restseq;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 10: objective::output_var;");
        TerminalParser.parse(fzn.SOLVE_GOAL, "solve\n" +
                "  ::seq_search(\n" +
                "    [ int_search(restdays, input_order, indomain_min, complete),\n" +
                "      int_search(flat2, input_order, indomain_min, complete) ])\n" +
                "  minimize objective;");
    }

}
