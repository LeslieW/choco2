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
package samples.tutorials.seminar;

import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;

import static choco.Choco.*;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 28 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class ExKnapSack {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

    static IntegerVariable obj1;
    static IntegerVariable obj2;
    static IntegerVariable obj3;
    static IntegerVariable c;


    public static Model postKnapsacPB() {
        Model m = new CPModel();

        obj1 = makeIntVar("obj1", 0, 17);
        obj2 = makeIntVar("obj2", 0, 17);
        obj3 = makeIntVar("obj3", 0, 17);
        c = makeIntVar("cost", 0, 500);
        m.addVariable(Options.V_BOUND, c);

        int capacity = 34;

        int[] volumes = new int[]{7, 5, 2};
        int[] energy = new int[]{6, 4, 3};

        m.addConstraint(leq(scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3}), capacity));
        m.addConstraint(eq(scalar(energy, new IntegerVariable[]{obj1, obj2, obj3}), c));

        return m;
    }

    public static void knapsacSAT() {
        Model m = postKnapsacPB();

        Solver s =  new CPSolver();
        s.read(m);
        s.attachGoal(new AssignVar(new StaticVarOrder(s, s.getVar(new IntegerVariable[]{obj1, obj2, obj3})), new IncreasingDomain()));
//        ChocoLogging.toSearch();
        s.maximize(s.getVar(c), false);

        LOGGER.info("obj1: " + s.getVar(obj1).getVal());
        LOGGER.info("obj2: " + s.getVar(obj2).getVal());
        LOGGER.info("obj3: " + s.getVar(obj3).getVal());
        LOGGER.info("cost: " + s.getVar(c).getVal());
    }

    public static void knapsacOPT() {
        Model m = postKnapsacPB();

        Solver s = new CPSolver();
        s.read(m);

        s.setValIntIterator(new DecreasingDomain());

        s.maximize(s.getVar(c), true);

        LOGGER.info("obj1: " + s.getVar(obj1).getVal());
        LOGGER.info("obj2: " + s.getVar(obj2).getVal());
        LOGGER.info("obj3: " + s.getVar(obj3).getVal());
        LOGGER.info("cost: " + s.getVar(c).getVal());
    }


    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        knapsacSAT();
        long t2 = System.currentTimeMillis();
        LOGGER.info("time : "+ (t2-t1));
        LOGGER.info("");
        LOGGER.info("=============================================");
        LOGGER.info("");
        long t3 = System.currentTimeMillis();
//        knapsacOPT();
        long t4 = System.currentTimeMillis();
        LOGGER.info("time : "+ (t4-t3));
        ChocoLogging.flushLogs();
    }

}
