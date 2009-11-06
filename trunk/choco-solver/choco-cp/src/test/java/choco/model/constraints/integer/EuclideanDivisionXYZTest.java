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
package choco.model.constraints.integer;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 9 déc. 2008
 * Since : Choco 2.0.1
 * Update : Choco 2.0.1
 */
public class EuclideanDivisionXYZTest {

    @Test
    public void test1() {
        int[] size = new int[]{2, 3, 5, 10, 20};
        for (int siz = 0; siz < size.length; siz++) {
            int max = size[siz];

            for (int seed = 0; seed < 20; seed++) {
                Random r = new Random(seed);

                int x1 = r.nextInt(max) - r.nextInt(max);
                int y1 = x1 + r.nextInt(max);
                int x2 = r.nextInt(max) - r.nextInt(max);
                int y2 = x2 + r.nextInt(max);
                int x3 = r.nextInt(max) - r.nextInt(max);
                int y3 = x3 + r.nextInt(max);

                //REFERENCE

                Model _m = new CPModel();

                IntegerVariable _x = Choco.makeIntVar("x", x1, y1, "cp:bound");
                IntegerVariable _y = Choco.makeIntVar("y", x2, y2, "cp:bound");
                IntegerVariable _z = Choco.makeIntVar("z", x3, y3, "cp:bound");

                _m.addConstraint(Choco.eq(_z, Choco.div(_x, _y)));
                Solver _s = new CPSolver();
                _s.read(_m);
                _s.solveAll();

                //CONSTRAINT
                Model m = new CPModel();
                m.addConstraint(Choco.intDiv(_x, _y, _z));
                Solver s = new CPSolver();
                s.read(m);
                s.solve();
                if (s.isFeasible()) {
                    do {
                        try{
                        Assert.assertEquals("("+max+"-"+seed+") Value test", s.getVar(_z).getVal(), s.getVar(_x).getVal() / s.getVar(_y).getVal());
                        }catch (ArithmeticException a){
                            Assert.fail("("+max+"-"+seed+") Division by 0");
                        }
                    } while (s.nextSolution());
                }
                Assert.assertEquals("("+max+"-"+seed+") Nb solution test",_s.getNbSolutions(), s.getNbSolutions());
            }
        }
    }

}
