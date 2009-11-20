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
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.model.variables.integer;

import static choco.Choco.makeIntVar;
import static choco.Choco.neq;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class LinkedListTest {

    @Test
     public void test_patakm1() {
        Model m = new CPModel();
        IntegerVariable[] arr = new IntegerVariable[10];
        int[] values = {-20000000, 20000000};
        for (int i = 0; i < arr.length; i++) {
            arr[i] = makeIntVar("", values, "cp:link");
            m.addConstraint(neq(arr[i], 1));
        }

        Solver s = new CPSolver();
        s.read(m);
        s.solve();
        System.out.println("number of solutions: " + s.getNbSolutions());
    }
}
