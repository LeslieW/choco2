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
package choco.memory;

import choco.cp.solver.CPSolver;
import choco.kernel.memory.IStateBinaryTree;
import choco.kernel.memory.trailing.StoredBinaryTree;
import choco.kernel.solver.Solver;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 19 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class StoredBinaryTreeTest {

    @Test
    public void test0() {

        Solver s = new CPSolver();
        StoredBinaryTree t = (StoredBinaryTree) s.getEnvironment().makeBinaryTree(0,500);
        t.remove(15);
        t.remove(16);
        t.remove(17);
        t.remove(5);
        t.remove(10);
        t.remove(100);
        System.out.println(t);

        IStateBinaryTree.Node n = t.prevNode(0);
        System.out.println(n);
    }
}
