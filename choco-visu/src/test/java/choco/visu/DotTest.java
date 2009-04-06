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
package choco.visu;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.util.UtilAlgo;
import choco.kernel.model.Model;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.visu.components.panels.VarChocoPanel;
import static choco.visu.components.papplets.ChocoPApplet.DOTTYTREESEARCH;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 25 sept. 2008
 * Time: 18:23:06
 * To change this template use File | Settings | File Templates.
 */
public class DotTest {

    @Before
    public void checkEnvironment(){
        GraphicsEnvironment ge = GraphicsEnvironment
        .getLocalGraphicsEnvironment();
        if(ge.isHeadlessInstance()){
            System.exit(0);
        }
    }

	public static String createDotFileName(String prefix) {
		try {
			String filename = File.createTempFile(prefix,".dot").getAbsolutePath();
			System.out.println("generated filename : "+filename);
			return filename;
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;

	}

    @Test
    public void nQueensNaifRedSolve() {
        int n = 6;
        Model m = new CPModel();

        IntegerVariable[] queens = new IntegerVariable[n];
        IntegerVariable[] queensdual = new IntegerVariable[n];

        for (int i = 0; i < n; i++) {
            queens[i] = makeIntVar("Q" + i, 1, n);
            queensdual[i] = makeIntVar("QD" + i, 1, n);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
                m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queensdual[i], queensdual[j]));
                m.addConstraint(neq(queensdual[i], plus(queensdual[j], k)));  // diagonal constraints
                m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
            }
        }
        m.addConstraint(inverseChanneling(queens, queensdual));

        String url = createDotFileName("choco");
        Solver s = new CPSolver();
            s.read(m);

    //    s.setVarIntSelector(new MinDomain(s,s.getVar(queens)));
        s.attachGoal(new AssignVar(new MinDomain(s,s.getVar(queens)),new IncreasingDomain()));

        CPSolver.setVerbosity(CPSolver.SOLUTION);
        s.setLoggingMaxDepth(50);
        int timeLimit = 60000;
        s.setTimeLimit(timeLimit);

        Visu v = Visu.createVisu(220, 200);
        Variable[] vars = UtilAlgo.append(queens, queensdual);
        v.addPanel(new VarChocoPanel("Dotty", vars, DOTTYTREESEARCH, new Object[]{url, 100, null, null, null}));

        // Solve the model
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();


        CPSolver.flushLogs();
        v.kill();
    }


    @Test
    public void nQueensNaifRedSolveAll() {
        int n = 6;
        Model m = new CPModel();

        IntegerVariable[] queens = new IntegerVariable[n];
        IntegerVariable[] queensdual = new IntegerVariable[n];

        for (int i = 0; i < n; i++) {
            queens[i] = makeIntVar("Q" + i, 1, n);
            queensdual[i] = makeIntVar("QD" + i, 1, n);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
                m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queensdual[i], queensdual[j]));
                m.addConstraint(neq(queensdual[i], plus(queensdual[j], k)));  // diagonal constraints
                m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
            }
        }
        m.addConstraint(inverseChanneling(queens, queensdual));

        String url = createDotFileName("choco");
        Solver s = new CPSolver();
            s.read(m);

    //    s.setVarIntSelector(new MinDomain(s,s.getVar(queens)));
        s.attachGoal(new AssignVar(new MinDomain(s,s.getVar(queens)),new IncreasingDomain()));

        CPSolver.setVerbosity(CPSolver.SOLUTION);
        s.setLoggingMaxDepth(50);
        int timeLimit = 60000;
        //s.setTimeLimit(timeLimit);
        Visu v = Visu.createVisu(220, 200);
        Variable[] vars = UtilAlgo.append(queens, queensdual);
        v.addPanel(new VarChocoPanel("Dotty", vars, DOTTYTREESEARCH, new Object[]{url, 100, null, null, Boolean.TRUE}));

        // Solve the model
        s.setFirstSolution(false);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
        CPSolver.flushLogs();
        v.kill();
    }

    @Test
    public void knapSack() {
        IntegerVariable obj1;
        IntegerVariable obj2;
        IntegerVariable obj3;
        IntegerVariable c;

        Model m = new CPModel();

        obj1 = makeIntVar("obj1", 0, 5);
        obj2 = makeIntVar("obj2", 0, 7);
        obj3 = makeIntVar("obj3", 0, 10);
        c = makeIntVar("cost", 1, 1000000);
        m.addVariable("cp:bound", c);

        int capacity = 34;

        int[] volumes = new int[]{7, 5, 3};
        int[] energy = new int[]{6, 4, 2};

        m.addConstraint(leq(scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3}), capacity));
        m.addConstraint(eq(scalar(energy, new IntegerVariable[]{obj1, obj2, obj3}), c));

        String url = createDotFileName("choco");
        Solver s = new CPSolver();
        s.read(m);

        //s.setValIntIterator(new DecreasingDomain());
        Visu v = Visu.createVisu(220, 200);;
        Variable[] vars = new Variable[]{obj1, obj2, obj3};
        v.addPanel(new VarChocoPanel("Dotty", vars, DOTTYTREESEARCH, new Object[]{url, 100, s.getVar(c), Boolean.TRUE, Boolean.TRUE}));

        // Solve the model
        s.setDoMaximize(true);
        s.setObjective(s.getVar(c));
        s.setRestart(true);
        s.setFirstSolution(false);

        s.generateSearchStrategy();
        s.visualize(v);

        s.launch();
        CPSolver.flushLogs();
        System.out.println("obj1: " + s.getVar(obj1).getVal());
        System.out.println("obj2: " + s.getVar(obj2).getVal());
        System.out.println("obj3: " + s.getVar(obj3).getVal());
        System.out.println("cost: " + s.getVar(c).getVal());
        v.kill();
    }

}
