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
package choco.solver;

import static java.text.MessageFormat.format;

import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import static choco.Choco.*;
import static org.junit.Assert.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 16 déc. 2008
 * Since : Choco 2.0.1
 * Update : Choco 2.0.1
 */
public class SolverTest {

	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	@Test
	@Ignore
	public void testCharge1(){
		int cpt;
		int newcpt;
		int[] nbVar = new int[]{10, 100, 1000, 100000, 1000000};
		for(int i = 1; i < nbVar.length; i++) {
			Runtime.getRuntime().gc();
			long t = System.currentTimeMillis();
			int n = nbVar[i];
			int b = 100;
			Solver solver = new CPSolver();
			IntDomainVar[] v = new IntDomainVar[n];
			for(int k = 0; k < n; k++){
				v[k] = solver.createBoundIntVar("v_"+k, 1, b);
			}
			newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
			//                if (cpt != 0) {
			//                    assertTrue(newcpt <= cpt + 10000);
			//                }
			cpt = newcpt;
			StringBuffer st = new StringBuffer();
			st.append(format("|{0} |", StringUtils.pad("" + n, -9, " ")));
			st.append(format("|{0} |", StringUtils.pad("" + (System.currentTimeMillis() - t), -5, " ")));
			st.append(format("|{0} |", StringUtils.pad("" + cpt, -10, " ")));
			LOGGER.info(st.toString());
		}
	}


	@Test
	@Ignore
	public void testCharge2(){
		int cpt = 0;
		int newcpt;
		int[] nbCstr = new int[]{10, 100, 1000, 100000, 1000000};
		for(int i = 1; i < nbCstr.length; i++) {
			Runtime.getRuntime().gc();
			long t = System.currentTimeMillis();
			int n = nbCstr[i];
			Solver solver = new CPSolver();
			IntDomainVar v  = solver.createBoundIntVar("v", 1 ,10);
			for(int k = 0; k < n; k++){
				solver.post(solver.eq(v, 5));
			}
			newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
			//                if (cpt != 0) {
			//                    assertTrue(newcpt <= cpt + 10000);
			//                }
			cpt = newcpt;
			StringBuffer st = new StringBuffer();
			st.append(format("|{0} |", StringUtils.pad("" + n, -9, " ")));
			st.append(format("|{0} |", StringUtils.pad("" + (System.currentTimeMillis() - t), -5, " ")));
			st.append(format("|{0} |", StringUtils.pad("" + cpt, -10, " ")));
			LOGGER.info(st.toString());
		}
	}

	Model model = new CPModel(); 
	
	CPSolver solver = new CPSolver();  

	private void checkAndSolve(Boolean res) {
		solver.read(model);
		assertEquals("check nb Constraint after read()", model.getNbConstraints(), solver.getNbIntConstraints());
		assertEquals("check nb variables after read()", model.getNbIntVars(), solver.getNbIntVars());
		assertEquals(res, solver.solve()); 
	}

	@Test 
	public void testSolveMultipleModels() { 
		//ChocoLogging.setVerbosity(Verbosity.VERBOSE);

		IntegerVariable i = makeBooleanVar("i"); 
		IntegerVariable j = makeBooleanVar("j"); 
		IntegerVariable k = makeIntVar("k", new int[]{0, 1, 2}); 
		IntegerVariable l = makeIntVar("l", new int[]{0, 1, 2}); 
		IntegerVariable z = makeBooleanVar("z"); 

		model.addVariables(i, j, k, z); 

		Constraint e1 = gt(i, j); 
		Constraint r1 = gt(z, j); 
		Constraint r2 = gt(i, z); 

		Constraint r3 = leq(k, j); 
		Constraint r4 = leq(l, j); 
		Constraint r5 = neq(k, j); 

		model.addConstraints(e1, r1, r2); 
		assertEquals("nb Constraint", 3, model.getNbConstraints());
		checkAndSolve(Boolean.FALSE);

		model.removeConstraint(r1); 
		model.removeConstraint(r2); 
		assertEquals("nb Constraint", 1, model.getNbConstraints());
		model.addConstraints(r2,r3,r4); 
		assertEquals("nb Constraint", 4, model.getNbConstraints());
		checkAndSolve(Boolean.TRUE);

		model.removeConstraint(r2); 
		model.removeConstraint(r3); 
		model.removeConstraint(r4); 
		assertEquals("nb Constraint", 1, model.getNbConstraints());
		model.addConstraints(r3,r4,r5); 
		assertEquals("nb Constraint", 4, model.getNbConstraints());
		checkAndSolve(Boolean.FALSE); 

	} 

}
