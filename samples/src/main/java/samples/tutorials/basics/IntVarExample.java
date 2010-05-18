package samples.tutorials.basics;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.tutorials.PatternExample;

import static choco.Choco.*;

/**
 * Created by IntelliJ IDEA.
 * User: njussien
 * Date: 27 avr. 2010
 * Time: 14:45:58
 * To change this template use File | Settings | File Templates.
 */




public class IntVarExample extends PatternExample {

    IntegerVariable me, him;
    /*
         * Easy simple problem defined by:
         * "Six years ago, my brother was two time my age.
         * In five years, we will have 40 years together.
         * How old am I?"
         * (sorry for the translation :) )
         */

    @Override
    public void printDescription() {
        super.printDescription();
        LOGGER.info("Six years ago, my brother was twice my age.");
        LOGGER.info("In five years, our ages will add up to 40");
        LOGGER.info("How old am I ?");
    }

    @Override
    public void buildModel() {
        model = new CPModel();
        me = makeIntVar("me", 0, 40);
        him = makeIntVar("him", 0, 40);

        model.addConstraint(eq(mult(2, minus(me, 6)), minus(him, 6)));
        model.addConstraint(eq(40, plus(plus(me, 5), plus(him,5))));
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
        LOGGER.info("\nMe :"+ solver.getVar(me).getVal()+" years old");
        LOGGER.info("Him :"+ solver.getVar(him).getVal()+" years old\n");
    }

    public static void main(String[] args) {
        new IntVarExample().execute();
    }
}
