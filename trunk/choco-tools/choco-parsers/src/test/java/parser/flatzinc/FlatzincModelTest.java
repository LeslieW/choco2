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

import choco.cp.solver.CPSolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parser.flatzinc.ast.SolveGoal;
import parser.flatzinc.parser.FZNParser;
import parser.flatzinc.parser.TerminalParser;

import java.io.File;
import java.net.URISyntaxException;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 13 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class FlatzincModelTest {

    FZNParser fzn;
    CPSolver s;

    @Before
    public void before(){
        fzn = new FZNParser();
        s = new CPSolver();
    }

    @Test
    public void test1(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 2: a::output_var;");
        TerminalParser.parse(fzn.CONSTRAINT, "constraint int_ne(a, 1);");
        SolveGoal sg = TerminalParser.parse(fzn.SOLVE_GOAL, "solve satisfy;");
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

    @Test
    public void test2(){
        fzn.instance = "var 1 .. 2: a::output_var;\n" +
                "constraint int_ne(a, 1);\n" +
                "solve satisfy;";
        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

    @Test
    public void test3(){
        fzn.instance = "array[1 .. 2] of var 1 .. \n" +
                "2: q;\n" +
                "constraint int_ne(q[1], q[2]);\n" +
                "solve satisfy;";

        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }


    @Test
    public void test4() throws URISyntaxException {
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        fzn.loadInstance(new File(f+File.separator+"nsp_1_1.fzn"));

        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

    @Test
    public void test5() throws URISyntaxException {
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        fzn.loadInstance(new File(f+File.separator+"roster_model_chicroster_dataset_1.fzn"));

        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

    @Test
    public void test6() throws URISyntaxException {
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        fzn.loadInstance(new File(f+File.separator+"talent_scheduling_small.fzn"));

        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

    @Test
    public void test7() throws URISyntaxException {
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        fzn.loadInstance(new File(f+File.separator+"talent_scheduling_01_small.fzn"));

        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

}
