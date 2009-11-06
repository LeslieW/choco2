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
package choco.memory;

import choco.cp.solver.CPSolver;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.copy.EnvironmentCopying;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static samples.seminar.ExDonaldGeraldRobert.*;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 13 oct. 2008
 * Time: 10:45:04
 * To change this template use File | Settings | File Templates.
 */
public class CopyTest {

    IEnvironment env;

    @Before
    public void before(){
        env = new EnvironmentCopying();
    }

    @After
    public void after(){
        env = null;
    }

    @Test
        public void donaldGeraldRobert(){
            Model m = modelIt1();
            Solver s = new CPSolver(env);
            Solver _s = new CPSolver();
            // Read the model
            s.read(m);
            _s.read(m);

            // Then solve it
            s.solve();
            _s.solve();

            // Print name value
            Assert.assertEquals("donald is not equal",_s.getVar(_donald).getVal(),s.getVar(_donald).getVal());
            Assert.assertEquals("gerald is not equal",_s.getVar(_gerald).getVal(),s.getVar(_gerald).getVal());
            Assert.assertEquals("robert is not equal",_s.getVar(_robert).getVal(),s.getVar(_robert).getVal());
        }



}
