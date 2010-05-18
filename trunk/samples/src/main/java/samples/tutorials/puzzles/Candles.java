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
package samples.tutorials.puzzles;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.tutorials.PatternExample;

import static choco.Choco.*;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class Candles extends PatternExample {

    /**
     *
     * Lily and Marshall have seven children, all born on Thanksgiving
     * during six consective's years. Since the first anniversary of the elder,
     * Uncle Barney holds a party every Thanksgiving.
     * Today, the day of their anniversary, there are seven children, each
     * receives a beautiful cake decorated with so many candles he has years.
     * Uncle Barney finds he bought twice as many candles as two years ago.
     * How old are the children and how many candles did Barney buy?
     */

    IntegerVariable[] vars;

    @Override
    public void printDescription() {
        super.printDescription();

        LOGGER.info("Lily and Marshall have seven children, all born on Thanksgiving");
        LOGGER.info("during six consective years. Since the first anniversary of the elder,");
        LOGGER.info("Uncle Barney holds a party every Thanksgiving.");
        LOGGER.info("Today, the day of their birthday, there are seven children, each");
        LOGGER.info("receives a beautiful cake decorated with so many candles he has years.");
        LOGGER.info("Uncle Barney finds he bought twice as many candles as two years ago.");
        LOGGER.info("-> How old are the children and how many candles did Barney buy?");

    }

    @Override
    public void buildModel() {
        model = new CPModel();

        vars = makeIntVarArray("son#", 7, 0, 100);

        model.addConstraint(eq(vars[0], plus(vars[1],1)));
        model.addConstraint(eq(vars[1], plus(vars[2],1)));
        model.addConstraint(eq(vars[2], plus(vars[3],1)));
        model.addConstraint(eq(vars[3], plus(vars[4],1)));
        model.addConstraint(eq(vars[4], plus(vars[5],1)));
        model.addConstraint(eq(vars[5], plus(vars[6],1)));

        IntegerExpressionVariable twoyearsago = minus(sum(vars), 14);
        IntegerExpressionVariable now = sum(vars);

        model.addConstraint(eq(mult(twoyearsago,2), now));
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
    }

    @Override
    public void solve() {
        solver.solveAll();
    }

    @Override
    public void prettyOut() {
        int sum = 0;
        for(int i = 0; i < 7; i++){
            sum += solver.getVar(vars[i]).getVal();
            LOGGER.info("son#"+i+" : "+ solver.getVar(vars[i]).getVal()+" years old");
        }
        LOGGER.info("Number of candles : "+ sum + "\n");
    }
    
    public static void main(String[] args) {
        new Candles().execute();
    }
}
