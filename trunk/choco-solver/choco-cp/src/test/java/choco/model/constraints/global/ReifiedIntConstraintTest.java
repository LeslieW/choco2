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
import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 16 mai 2008
 * Time: 10:40:09
 */
public class ReifiedIntConstraintTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Model m;

    Solver s;

    @Before
    public void before(){
       m = new CPModel();
        s = new CPSolver();
    }

    @After
    public void after(){
        m = null;
        s = null;
    }

	@Test
	public void testSimpleBooleanReification() {
		for (int seed = 0; seed < 20; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable b = makeIntVar("b", 0, 1);
			IntegerVariable y = makeIntVar("y", 1, 10);
			IntegerVariable z = makeIntVar("z", 1, 10);
            m.addVariables("cp:bound", b, y, z);

            //m.addVariable(b, y, z);
			s.read(m);

			s.post(new ReifiedIntSConstraint(s.getVar(b), (AbstractIntSConstraint) s.lt(s.getVar(y), s.getVar(z)), s));

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			s.solveAll();
			LOGGER.info(""+s.getNbSolutions());
			assertEquals(s.getNbSolutions(),100);
		}
	}

	@Test
	public void testSimpleBooleanReification2() {
		for (int seed = 0; seed < 20; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable b = makeIntVar("b", 0, 1);
			IntegerVariable y = makeIntVar("y", 1, 10);
			IntegerVariable z = makeIntVar("z", 1, 10);
            m.addVariables("cp:bound", b, y, z);

            m.addVariables(b, y, z);
			s.read(m);

			s.post(new ReifiedIntSConstraint(s.getVar(b), (AbstractIntSConstraint) s.eq(s.getVar(y), s.getVar(z)), s));

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			s.solve();
			do {
				LOGGER.info(s.getVar(b) + " " + s.getVar(y) + " " + s.getVar(z));
			} while(s.nextSolution() == Boolean.TRUE);
			LOGGER.info(""+s.getNbSolutions());
			assertEquals(s.getNbSolutions(),100);
		}
	}
	

    @Test
    public void test1(){
        IntegerVariable binary = makeIntVar("bin", 0,1);
        IntegerVariable a = makeIntVar("a", 0, 10);
        IntegerVariable b = makeIntVar("b", 0, 10);

        m.addConstraint(reifiedConstraint(binary, leq(a, b)));
        m.addConstraint(lt(b,binary));
        s.read(m);
        s.solveAll();
        assertEquals(s.getNbSolutions(),1);
    }

    @Test
    public void test2(){
        IntegerVariable binary = makeIntVar("bin", 0,1);
        int a = 0;
        IntegerVariable b = makeIntVar("b", 0, 10);

        m.addConstraint(reifiedConstraint(binary, leq(a, b)));
        m.addConstraint(lt(b,binary));
        s.read(m);
        s.solveAll();
        assertEquals(s.getNbSolutions(),1);
    }

    @Test
    public void test3(){
        IntegerVariable binary = makeIntVar("bin", 0,1);
        IntegerVariable a = makeIntVar("a", 0, 10);
        IntegerVariable b = makeIntVar("b", 0, 0);

        m.addConstraint(reifiedConstraint(binary, leq(a, b)));
        m.addConstraint(lt(b,binary));
        s.read(m);
        s.solveAll();

        Model m2 = new CPModel();
        Solver s2 = new CPSolver();

        m2.addConstraint(reifiedConstraint(binary, leq(a, 0)));
        m2.addConstraint(lt(b,binary));
        s2.read(m2);
        s2.solveAll();

        assertEquals(s.getNbSolutions(),s2.getNbSolutions());
    }
}
