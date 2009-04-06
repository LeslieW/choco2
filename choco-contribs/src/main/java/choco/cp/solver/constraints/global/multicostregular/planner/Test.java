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
package choco.cp.solver.constraints.global.multicostregular.planner;

import choco.kernel.common.util.IntIterator;
import choco.kernel.common.util.intutil.HashIntSet;
import choco.kernel.model.constraints.Constraint;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import dk.brics.automaton.RegExp;

import static choco.Choco.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 6, 2009
 * Time: 10:25:28 AM
 */
public class Test {

    public static void main(String[] args) {

        CPModel m = new CPModel();
        CPSolver s = new CPSolver();

        Constraint c = eq(makeIntVar("x",0,10),makeIntVar("y",0,10));

        System.out.println(c);





    }





}