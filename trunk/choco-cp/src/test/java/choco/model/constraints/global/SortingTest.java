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
package choco.model.constraints.global;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.SortingSConstraint;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 4 juin 2007
 * Time: 16:45:46
 * To change this template use File | Settings | File Templates.
 */
public class SortingTest extends TestCase {

    public static void testSorting() {
        CPModel m = new CPModel();
        IntegerVariable[] x = {
                makeIntVar("x0", 1, 16),
                makeIntVar("x1", 5, 10),
                makeIntVar("x2", 7, 9),
                makeIntVar("x3", 12, 15),
                makeIntVar("x4", 1, 13)
        };
        IntegerVariable[] y = {
                makeIntVar("y0", 2, 3),
                makeIntVar("y1", 6, 7),
                makeIntVar("y2", 8, 11),
                makeIntVar("y3", 13, 16),
                makeIntVar("y4", 14, 18)
        };
        Constraint c = sorting(x, y);
        m.addConstraint(c);
        CPSolver s = new CPSolver();
        s.read(m);
        try {
            ((SortingSConstraint)s.getCstr(c)).boundConsistency();
        }
        catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public static void testSorting2() {
        for (int seed = 0; seed < 10; seed++) {
            CPModel m = new CPModel();
            int n = 4;
            IntegerVariable[] x = makeIntVarArray("x", n, 0, 6);
            IntegerVariable[] y = makeIntVarArray("y", n, 0, 6);
            Constraint c = sorting(x, y);
            m.addConstraint(c);
            m.addConstraint(allDifferent(x));
            CPSolver s = new CPSolver();
            s.read(m);
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.setVarIntSelector(new RandomIntVarSelector(s, seed + 2));
            s.solveAll();
            System.out.println("Sorting nb solutions " + s.getNbSolutions());
            assertEquals(840, s.getNbSolutions());
        }

    }

}
