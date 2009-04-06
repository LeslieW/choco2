/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package samples.Examples;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class HowOldAmI extends PatternExample{

    /**
     * Easy simple problem defined by:
     * "Six years ago, my brother was two time my age.
     * In five years, we will have 40 years together.
     * How old am I?"
     * (sorry for the translation :) )
     */

    IntegerVariable me, him;

    @Override
    public void buildModel() {
        _m = new CPModel();
        me = makeIntVar("me", 0, 40);
        him = makeIntVar("him", 0, 40);

        _m.addConstraint(eq(mult(2, minus(me, 6)), minus(him, 6)));
        _m.addConstraint(eq(40, plus(plus(me, 5), plus(him,5))));
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
        _s.read(_m);
    }

    @Override
    public void solve() {
        _s.solveAll();
    }

    @Override
    public void prettyOut() {
        System.out.println("Me :"+_s.getVar(me).getVal()+" years old");
        System.out.println("Him :"+_s.getVar(him).getVal()+" years old");
    }

    public static void main(String[] args) {
        new HowOldAmI().execute(null);
    }
}
