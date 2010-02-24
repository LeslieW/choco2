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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.model.variables.real;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 24 févr. 2010
 * Since : Choco 2.1.1
 */
public class RealVarTest {

    @Test
    public void testWayne99(){
        Model m = new CPModel();
		RealVariable r = Choco.makeRealVar("test_r",0,1.0);

		m.addConstraint(Choco.leq(r,0.4));

		Solver s = new CPSolver();
		s.read(m);
		s.maximize(s.getVar(r), false);

		System.out.println("test_r: " + s.getVar(r).getValue());
    }
}
