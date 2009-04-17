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
package choco.model.constraints.set;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 janv. 2008
 * Time: 17:38:34
 */
public class SetUnionTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();
	Model m;
	Solver s;

	@After
	public void tearDown() throws Exception {
		LOGGER.info(s.pretty());
		s = null;
		m = null;
	}

	@Before
	public void setUp() throws Exception {
		m = new CPModel();
		s = new CPSolver();
	}

	@Test
	public void test() {
		for (int seed = 0; seed < 20; seed++) {
			m = new CPModel();
			s = new CPSolver();
			SetVariable v1 = makeSetVar("v1", 4, 6);
			SetVariable v2 = makeSetVar("v2", 3, 5);
			SetVariable v3 = makeSetVar("v3", 1, 6);
			SetVariable v4 = makeSetVar("v4", 4, 4);

			m.addConstraint(setDisjoint(v1, v4));
			m.addConstraint(eqCard(v4, 1));
			Constraint c1 = setUnion(v1, v2, v3);
			m.addConstraint(c1);
			s.read(m);
			s.setVarSetSelector(new RandomSetVarSelector(s, seed));
			s.setValSetSelector(new RandomSetValSelector(seed + 1));
			s.solveAll();
			assertEquals(32, s.getNbSolutions());
		}

	}

    @Test
    public void bugNoContradiction1() {

        Model m = new CPModel();
        SetVariable s1 = makeSetVar("s1", 1, 3);
        SetVariable x = makeSetVar("x", 1, 3);
        SetVariable y = makeSetVar("y", 1, 3);

        m.addConstraint(setUnion(x, y, s1));
        m.addConstraint(setDisjoint(x, y));
        m.addConstraint(geqCard(x, 1));

        Solver s = new CPSolver();
        s.read(m);

        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        s.solveAll();
        Assert.assertEquals("nb of solutions", 19, s.getNbSolutions());
    }

    @Test
    public void bugNoContradiction2() {
        Model m = new CPModel();
        SetVariable s1 = makeSetVar("s1", 1, 10);
        m.addConstraint(geqCard(s1, 1));
        m.addConstraint(eqCard(s1, 0));
        Solver s = new CPSolver();
        s.read(m);

        try {
            s.propagate();
            Assert.fail();
        } catch (ContradictionException e) {
            //OK    
        }
    }


}
