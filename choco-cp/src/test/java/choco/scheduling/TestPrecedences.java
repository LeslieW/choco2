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
package choco.scheduling;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.PrecedenceWithDelta;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import samples.scheduling.pert.DeterministicPert;

import java.util.logging.Logger;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public class TestPrecedences {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	public CPModel m;

	public CPSolver s;

	

	public void solve(int nbSol,String label) {
		long seed= SchedUtilities.RANDOM.nextLong();
		int n = 3;
		CPSolver[] solvers= new CPSolver[n];
		for (int i = 0; i < solvers.length; i++) {
			solvers[i] = new CPSolver();
			solvers[i].setHorizon(Integer.MAX_VALUE-1);
		}
		int cpt =0;
		solvers[cpt].getScheduler().setPrecedenceNetwork(true);
		solvers[cpt].getScheduler().setIncrementalPert(false);
		cpt++;
		solvers[cpt].getScheduler().setPrecedenceNetwork(true);
		solvers[cpt].getScheduler().setIncrementalPert(true);
		
		for (CPSolver s : solvers) {
			s.read(m);
			//LOGGER.info(s.pretty());
			s.setRandomSelectors(seed);
		}
		SchedUtilities.compare(nbSol, SchedUtilities.CHECK_NODES, label, solvers);
	}

	@Test
	public void testProjectDates() {
		m=new CPModel();
		m.addVariable(Choco.makeTaskVar("alone",20, 5, "cp:bound"));
		solve(16,"project ");
	}



	@Test
	public void testCycle() {
		m=new CPModel();
		TaskVariable t1= makeTaskVar("t1", 20, 3);
		TaskVariable t2= makeTaskVar("t2", 20, 5);
		TaskVariable t3= makeTaskVar("t2", 20, 5);
		m.addConstraint(endsBeforeBegin(t1,t2));
		m.addConstraint(endsBeforeBegin(t2,t3));
		m.addConstraint(endsBeforeBegin(t3,t1));
		s=new CPSolver();
		s.read(m);
		s.solveAll();
		assertEquals(0, s.getNbSolutions());
		assertEquals(0, s.getNodeCount());
		
	}



	@Test
	public void testSBB() {
		m=new CPModel();
		TaskVariable t1= makeTaskVar("T1", 6, 3);
		TaskVariable t2= makeTaskVar("T2", 6, 5);
		m.addConstraint(startsBeforeBegin(t1, t2, -1));
		solve(5,"SBB");
	}


	@Test
	public void testSmall() {
		m=new CPModel();
		TaskVariable t1= makeTaskVar("T1", 20, 10);
		TaskVariable t2= makeTaskVar("T2", 20, 10);
		TaskVariable t3= makeTaskVar("T3", 20, 10);
		m.addConstraint(endsBeforeBegin(t1, t2));
		m.addConstraint(startsBeforeBegin(t3, t2,2));
		solve(9,"small ");
	}
	

	
	@Test
	public void testSmall2() {
		m=new CPModel();
		TaskVariable t1= makeTaskVar("T1", 20, 5);
		TaskVariable t2= makeTaskVar("T2", 20, 7);
		TaskVariable t3= makeTaskVar("T3", 20, 6);
		TaskVariable t4= makeTaskVar("T4", 20, 8);
		m.addConstraint(endsBeforeBegin(t1, t2));
		m.addConstraint(endsBeforeBegin(t1, t3));
		m.addConstraint(endsBeforeBegin(t2, t4));
		m.addConstraint(endsBeforeBegin(t1, t4));
		
		solve(10,"middle ");
	}


	/**
	 *  http://scienceblogs.com/goodmath/2007/09/critical_paths_scheduling_and.php
	 *  critical path lenght : 30 ; 19656 solutions
	 * @param horizon
	 * @return
	 */
	public void setPertExample(int horizon) {
		m=new CPModel();
		TaskVariable a= makeTaskVar("A",2,horizon,2);
		TaskVariable b= makeTaskVar("B",horizon, 2);
		TaskVariable c= makeTaskVar("C",3,horizon,3);
		TaskVariable d= makeTaskVar("D", horizon, 2);
		TaskVariable e= makeTaskVar("E", horizon, 4);
		TaskVariable f= makeTaskVar("F", horizon, 3);
		TaskVariable g= makeTaskVar("G", horizon, 5);
		TaskVariable h= makeTaskVar("H", horizon, 3);
		TaskVariable i= makeTaskVar("I", horizon, 6);
		m.addConstraint(startsAfterEnd(b,a));
		m.addConstraint(startsAfterEnd(b,c));

		m.addConstraints(startsAfterEnd(d,a,4));
		m.addConstraints(startsAfterEnd(d,b,5));

		m.addConstraint(startsAfterEnd(e,b));
		m.addConstraints(startsAfterEnd(f,c,2));
		m.addConstraint(startsAfterEnd(g,d));
		m.addConstraint(startsAfterEnd(h,g));

		m.addConstraints(startsAfterEnd(i,d,9));
		m.addConstraint(startsAfterEnd(i,e));
		m.addConstraint(startsAfterEnd(i,f));
	}

	@Test
	public void testPertExample() {
		setPertExample(29);
		solve(0,"pert example (unsat)");
		setPertExample(30);
		solve(19656,"pert example (sat)");
	}
	
	@Ignore
	@Test
	public void testLargePertExample() {
		setPertExample(32);
		solve(504726,"pert example (sat)");
	}


	@Test
	public void testIlogExample() {
		DeterministicPert example;
		example=new DeterministicPert(17);
		m=example.getModel();
		solve(0,"Ilog ex.");

		example=new DeterministicPert(18);
		m=example.getModel();
		solve(154,"Ilog ex.");

		example=new DeterministicPert(19);
		m=example.getModel();
		solve(1764,"Ilog ex.");

		example=new DeterministicPert(28);
		example.requireUnaryResource();
		m=example.getModel();
		solve(0,"Ilog ex.");
		
		example=new DeterministicPert(29);
		example.requireUnaryResource();
		m=example.getModel();
		solve(112,"Ilog ex.");
	}

	
	@Test
	public void testVariablePrecedence() {
		for (int seed = 0; seed < 2; seed++) {
			CPModel mod = new CPModel();
			IntegerVariable[] vars = makeIntVarArray("vs", 4, 0, 30);
			mod.addVariables(vars);
			//mod.addConstraint(leq(plus(vars[0],vars[1]),plus(vars[2],vars[3])));
			CPSolver cs = new CPSolver();
			cs.read(mod);
			IntDomainVar[] svars = cs.getVar(vars);
			cs.post(new PrecedenceWithDelta(svars[0], svars[1], svars[2], svars[3]));
			cs.setVarIntSelector(new RandomIntVarSelector(cs,seed));
			cs.setValIntSelector(new RandomIntValSelector(seed));
			cs.solveAll();
			LOGGER.info("" + cs.getNbSolutions() + " " + cs.getNodeCount() + " " + cs.getTimeCount());
			assertTrue(cs.getNbSolutions() == 471696);
		}
	}

	
}
