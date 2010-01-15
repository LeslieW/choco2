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
package parser.flatzinc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parser.flatzinc.parser.FZNParser;
import parser.flatzinc.parser.TerminalParser;

import java.io.IOException;
import java.net.URISyntaxException;

import static parser.flatzinc.parser.FZNParser.*;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 13 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class FlatzincModelTest {

    @Before
    public void before(){
        init();
    }

    @Test
    public void test1(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "var 1 .. 2: a::output_var;");
        TerminalParser.parse(CONSTRAINT, "constraint int_ne(a, 1);");
        TerminalParser.parse(SOLVE_GOAL, "solve satisfy;");
        FZNParser.solver.launch();
        Assert.assertEquals(1, FZNParser.solver.getSolutionCount());
    }

    @Test
    public void test2(){
        String problem = "var 1 .. 2: a::output_var;\n" +
                "constraint int_ne(a, 1);\n" +
                "solve satisfy;";

        FZNParser.FLATZINC_MODEL(problem, true);
        Assert.assertEquals(1, FZNParser.solver.getSolutionCount());
    }

    @Test
    public void test3(){
        String problem = "array[1 .. 2] of var 1 .. \n" +
                "2: q;\n" +
                "constraint int_ne(q[1], q[2]);\n" +
                "solve satisfy;";

        FZNParser.FLATZINC_MODEL(problem, true);
        Assert.assertEquals(1, FZNParser.solver.getSolutionCount());
    }

    @Test
    public void testMain() throws IOException, URISyntaxException {
        String data = Mzn2fzn.class.getResource("/flatzinc").toURI().getPath();
        String[] args = new String[]{data};
        FznModel.main(args);
    }

}
