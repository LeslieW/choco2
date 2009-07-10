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
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class Candles extends PatternExample{

    /**
     * Marshall and Lily have 7 children borned on Thanksgiving of
     * 6 consecutives years.
     * Today, uncle Barney, like every year, prepare a birthday cake with candles.
     * This year, he has bought two more candles that last year.
     * How old are the children and how many candles did Barney buy?
     */

    IntegerVariable[] vars;

    @Override
    public void buildModel() {
        _m = new CPModel();

        vars = makeIntVarArray("son#", 7, 0, 100);

        _m.addConstraint(eq(vars[0], plus(vars[1],1)));
        _m.addConstraint(eq(vars[1], plus(vars[2],1)));
        _m.addConstraint(eq(vars[2], plus(vars[3],1)));
        _m.addConstraint(eq(vars[3], plus(vars[4],1)));
        _m.addConstraint(eq(vars[4], plus(vars[5],1)));
        _m.addConstraint(eq(vars[5], plus(vars[6],1)));

        IntegerExpressionVariable twoyearsago = minus(sum(vars), 14);
        IntegerExpressionVariable now = sum(vars);

        _m.addConstraint(eq(mult(twoyearsago,2), now));
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
        int sum = 0;
        for(int i = 0; i < 7; i++){
            sum += _s.getVar(vars[i]).getVal();
            LOGGER.info("son#"+i+" : "+_s.getVar(vars[i]).getVal()+" years old");
        }
        LOGGER.info("Number of candles : "+ sum);
    }
    
    public static void main(String[] args) {
        new Candles().execute();
    }
}
