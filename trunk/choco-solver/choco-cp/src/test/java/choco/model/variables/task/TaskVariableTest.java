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
package choco.model.variables.task;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.scheduling.TaskVar;
import org.junit.Assert;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class TaskVariableTest {

    @Test
    public void test0(){
        Model m = new CPModel();
        TaskVariable t = Choco.makeTaskVar("task1", 10, 5);
        m.addVariable(t);

        Solver solver = new CPSolver();
        solver.read(m);
        Assert.assertNotNull("t is null", solver.getVar(t));
        Assert.assertTrue("t not well created", solver.getVar(t) instanceof TaskVar);
    }

}
