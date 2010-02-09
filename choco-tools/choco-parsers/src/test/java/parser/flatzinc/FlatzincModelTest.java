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
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parser.flatzinc.ast.SolveGoal;
import parser.flatzinc.parser.FZNParser;
import parser.flatzinc.parser.TerminalParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

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
    public void before() {
        fzn = new FZNParser();
        s = new CPSolver();
//        s = new PreProcessCPSolver();
//        try {
//            File f = File.createTempFile("LOG", ".log");
//            System.out.println(f.getAbsolutePath());
//            ChocoLogging.setFileHandler(f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void test1() {
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 2: a::output_var;");
        TerminalParser.parse(fzn.CONSTRAINT, "constraint int_ne(a, 1);");
        SolveGoal sg = TerminalParser.parse(fzn.SOLVE_GOAL, "solve satisfy;");
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        Assert.assertEquals(1, s.getSolutionCount());
    }

    @Test
    public void test2() {
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
    public void test3() {
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
        tester("nsp_1_1.fzn", false, 0, new int[]{}, "");
    }


    @Test
    public void test50() throws URISyntaxException, ContradictionException {
        tester("roster_model_chicroster_dataset_1.fzn", true, 1,
                new int[]{
                        1, 1, 2, 3, 2, 2, 1,
                        1, 3, 4, 4, 3, 2, 1,
                        1, 3, 4, 4, 1, 1, 1,
                        2, 1, 1, 5, 4, 3, 1,
                        3, 4, 5, 5, 4, 4, 1}, "roster");
    }

    @Test
    public void test51() throws URISyntaxException, ContradictionException {
        tester("roster_model_chicroster_dataset_3.fzn", true, 1,
                new int[]{
                        1, 1, 1, 4, 3, 4, 1,
                        1, 1, 3, 1, 1, 1, 3,
                        1, 1, 1, 5, 3, 5, 1,
                        1, 1, 3, 1, 1, 1, 3,
                        1, 1, 5, 1, 1, 1, 3,
                        3, 4, 1, 1, 4, 1, 1,
                        3, 4, 1, 1, 4, 1, 1,
                        4, 4, 5, 1, 5, 1, 1,
                        5, 5, 5, 1, 1, 1, 5}, "roster");
    }

    @Test
    public void test6() throws URISyntaxException {
        tester("talent_scheduling_small.fzn", true, 1, new int[]{4, 1, 2, 3, 6, 5}, "s");
    }

    @Test
    public void test7() throws URISyntaxException, IOException {
        tester("talent_scheduling_01_small.fzn", true, 1,
                new int[]{4, 2, 1, 3, 5, 6}, "s");
    }

    @Test
    public void test8() throws URISyntaxException {
        tester("black-hole_1.fzn", true, 1,
                new int[]{1, 2, 14, 15, 16, 17, 18, 19, 20, 8, 9, 10,
                        11, 36, 22, 34, 33, 45, 31, 30, 3, 28, 29, 41,
                        27, 39, 40, 52, 12, 24, 38, 37, 23, 35, 47, 7,
                        6, 5, 4, 42, 43, 44, 32, 46, 21, 48, 49, 50,
                        25, 13, 51, 26}, "x");
    }

    @Test
    public void test9() throws URISyntaxException {
        tester("black-hole_17.fzn", false, 0, new int[]{}, "");
    }

    /******************************************************************************************************************/
    /**
     * **************************************************************************************************************
     */

    private void tester(String filename, boolean opt, int nbSol, int[] knownSolution, String varName) throws URISyntaxException {
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        fzn.loadInstance(new File(f + File.separator + filename));
        SolveGoal sg = fzn.parse();
        s.read(fzn.model);
        sg.defineGoal(s);
        s.launch();
        if (opt) {
            Assert.assertTrue(s.getSolutionCount() > 0);
            solutionChecker(s, knownSolution, varName);
        } else {
            Assert.assertEquals(nbSol, s.getSolutionCount());
            if (nbSol > 0) {
                solutionChecker(s, knownSolution, varName);
            }
        }
    }

    private void solutionChecker(CPSolver s, int[] knownSolution, String varName) {
        Iterator<IntegerVariable> it = fzn.model.getIntVarIterator();
        while (it.hasNext()) {
            IntegerVariable v = it.next();
            if (v.getName().startsWith(varName)) {
                int idx = Integer.parseInt(v.getName().substring(varName.length() + 1));
                Assert.assertTrue(v.getName() + "is not well instantiated!",
                        s.getVar(v).isInstantiatedTo(knownSolution[idx - 1]));
            }
        }
    }

}
