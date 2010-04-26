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

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 27 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class ExDonaldGeraldRobert {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

    public static IntegerVariable _donald = null;
    public static IntegerVariable _gerald = null;
    public static IntegerVariable _robert = null;

    public static IntegerVariable _d;
    public static IntegerVariable _o;
    public static IntegerVariable _n;
    public static IntegerVariable _a;
    public static IntegerVariable _l;
    public static IntegerVariable _g;
    public static IntegerVariable _e;
    public static IntegerVariable _r;
    public static IntegerVariable _b;
    public static IntegerVariable _t;

    public static Model modelIt1() {

        // Build model
        Model model = new CPModel();

        // Declare every letter as a variable
        _d = makeIntVar("d", 0, 9);
        _o = makeIntVar("o", 0, 9);
        _n = makeIntVar("n", 0, 9);
        _a = makeIntVar("a", 0, 9);
        _l = makeIntVar("l", 0, 9);
        _g = makeIntVar("g", 0, 9);
        _e = makeIntVar("e", 0, 9);
        _r = makeIntVar("r", 0, 9);
        _b = makeIntVar("b", 0, 9);
        _t = makeIntVar("t", 0, 9);

        // Declare every name as a variable
        _donald = makeIntVar("donald", 0, 1000000);
        _gerald = makeIntVar("gerald", 0, 1000000);
        _robert = makeIntVar("robert", 0, 1000000);
        model.addVariables(Options.V_BOUND, _donald, _gerald, _robert);

        // Array of coefficients
        int[] coeff = new int[]{100000, 10000, 1000, 100, 10, 1};

        // Declare every combination of letter as an integer expression
        IntegerExpressionVariable donaldLetters = scalar(new IntegerVariable[]{_d, _o, _n, _a, _l, _d}, coeff);
        IntegerExpressionVariable geraldLetters = scalar(new IntegerVariable[]{_g, _e, _r, _a, _l, _d}, coeff);
        IntegerExpressionVariable robertLetters = scalar(new IntegerVariable[]{_r, _o, _b, _e, _r, _t}, coeff);

        // Add equality between name and letters combination
        model.addConstraint(eq(donaldLetters, _donald));
        model.addConstraint(eq(geraldLetters, _gerald));
        model.addConstraint(eq(robertLetters, _robert));
        // Add constraint name sum
        model.addConstraint(eq(plus(_donald, _gerald), _robert));
        // Add constraint of all different letters.
        model.addConstraint(allDifferent(_d, _o, _n, _a, _l, _g, _e, _r, _b, _t));
        return model;
    }


    public static void solveIt1(Model model){
        // Build a solver
        Solver s = new CPSolver();
        // Read the model
        s.read(model);

        // Then solve it
        s.solve();

        // Print name value
        LOGGER.info("donald = " + s.getVar(_donald).getVal());
        LOGGER.info("gerald = " + s.getVar(_gerald).getVal());
        LOGGER.info("robert = " + s.getVar(_robert).getVal());
    }

    public static Model modelIt2() {

        // Build model
        Model model = new CPModel();

        // Declare every letter as a variable
        _d = makeIntVar("d", 0, 9);
        _o = makeIntVar("o", 0, 9);
        _n = makeIntVar("n", 0, 9);
        _a = makeIntVar("a", 0, 9);
        _l = makeIntVar("l", 0, 9);
        _g = makeIntVar("g", 0, 9);
        _e = makeIntVar("e", 0, 9);
        _r = makeIntVar("r", 0, 9);
        _b = makeIntVar("b", 0, 9);
        _t = makeIntVar("t", 0, 9);
        IntegerVariable r1 = makeIntVar("r1", 0, 1);
        IntegerVariable r2 = makeIntVar("r2", 0, 1);
        IntegerVariable r3 = makeIntVar("r3", 0, 1);
        IntegerVariable r4 = makeIntVar("r4", 0, 1);
        IntegerVariable r5 = makeIntVar("r5", 0, 1);

        // Add equality between letters
        model.addConstraint(eq(plus(_d,_d), plus(_t,mult(10,r1))));
        model.addConstraint(eq(plus(r1,plus(_l,_l)), plus(_r,mult(10,r2))));
        model.addConstraint(eq(plus(r2,plus(_a,_a)), plus(_e,mult(10,r3))));
        model.addConstraint(eq(plus(r3,plus(_n,_r)), plus(_b,mult(10,r4))));
        model.addConstraint(eq(plus(r4,plus(_o,_e)), plus(_o,mult(10,r5)))); // rewrite in scalar with a null coefficient => Bug !
//        model.addConstraint(eq(plus(r4,       e ),        mult(10,r5)));  // OK
        model.addConstraint(eq(plus(r5,plus(_d,_g)), _r));

//      model.addConstraint(eq(d, 5));   // if you add a clue ... propagation is enougth

        // Add constraint of all different letters.
        model.addConstraint(allDifferent(_d, _o, _n, _a, _l, _g, _e, _r, _b, _t));
        return model;
    }

    public static void solveIt2(Model model){
        // Build a solver
        Solver s = new CPSolver();
        // Read the model
        s.read(model);

        try {
            s.propagate();
        } catch (ContradictionException excep) {
            LOGGER.info("No solution");
        }

        // Then solve it
        s.solve();

        // Print name value
        LOGGER.info("donald = " + s.getVar(_d).getVal()+s.getVar(_o).getVal()+s.getVar(_n).getVal()+s.getVar(_a).getVal()+s.getVar(_l).getVal()+s.getVar(_d).getVal());
        LOGGER.info("gerald = " + s.getVar(_g).getVal()+s.getVar(_e).getVal()+s.getVar(_r).getVal()+s.getVar(_a).getVal()+s.getVar(_l).getVal()+s.getVar(_d).getVal());
        LOGGER.info("robert = " + s.getVar(_r).getVal()+s.getVar(_o).getVal()+s.getVar(_b).getVal()+s.getVar(_e).getVal()+s.getVar(_r).getVal()+s.getVar(_t).getVal());
        LOGGER.info(s.getNbSolutions()+" solution(s)");
        s.printRuntimeStatistics();
    }


    public static void main(String[] args) {
        LOGGER.info("Model 1 :");
        LOGGER.info("=========");
        solveIt1(modelIt1());
        LOGGER.info("\nModel 2 :");
        LOGGER.info("=========");
        solveIt2(modelIt2());
    }
}
