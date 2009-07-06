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
package samples.Examples;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;


/**
 * <b>The famous SEND + MORE = MONEY problem.</b></br>
 * The Send More Money Problem consists in finding distinct digits for the letters D, E, M, N, O, R, S, Y
 * such that S and M are different from zero (no leading zeros) and the equation SEND + MORE = MONEY is satisfied.
 *
 * @author Arnaud Malapert</br>
 * @version 2.0.1</br>
 * @since 3 déc. 2008 version 2.0.1</br>
 */
public class SendMoreMoney extends PatternExample {


    IntegerVariable S, E, N, D, M, O, R, Y;
    IntegerVariable[] SEND, MORE, MONEY;

    @Override
    public void buildModel() {
        _m = new CPModel();
        S = makeIntVar("S", 0, 9);
        E = makeIntVar("E", 0, 9);
        N = makeIntVar("N", 0, 9);
        D = makeIntVar("D", 0, 9);
        M = makeIntVar("M", 0, 9);
        O = makeIntVar("0", 0, 9);
        R = makeIntVar("R", 0, 9);
        Y = makeIntVar("Y", 0, 9);
        _m.addConstraints(neq(S, 0), neq(M, 0));
        _m.addConstraint(allDifferent(S, E, N, D, M, O, R, Y));
        SEND = new IntegerVariable[]{S, E, N, D};
        MORE = new IntegerVariable[]{M, O, R, E};
        MONEY = new IntegerVariable[]{M, O, N, E, Y};
        _m.addConstraints(
                eq(plus(scalar(new int[]{1000, 100, 10, 1}, SEND),
                        scalar(new int[]{1000, 100, 10, 1}, MORE)),
                        scalar(new int[]{10000, 1000, 100, 10, 1}, MONEY))
        );
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
        _s.read(_m);
    }

    @Override
    public void solve() {
        try {
            LOGGER.info("PROPAGATION");
            _s.propagate();
            this.prettyOut();
        } catch (ContradictionException ignored) {
        }
        LOGGER.info("RESOLUTION");
        _s.solve();
    }

    @Override
    public void prettyOut() {
        LOGGER.info(StringUtils.pretty(_s.getVar(SEND)));
        LOGGER.info(" + " + StringUtils.pretty(_s.getVar(SEND)));
        LOGGER.info(" = " + StringUtils.pretty(_s.getVar(SEND)));
    }

    public static void main(String[] args) {
        new SendMoreMoney().execute(null);
    }

}