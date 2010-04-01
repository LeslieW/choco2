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
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.comparator.TaskComparators;
import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;


public class TestTask {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	private List<SimpleTask> tasksL;

	public static List<SimpleTask> getExample() {
		List<SimpleTask> tasksL=new ArrayList<SimpleTask>();
		tasksL.add(new SimpleTask(0,23,5));
		tasksL.add(new SimpleTask(4,26,6));
		tasksL.add(new SimpleTask(2,16,4));
		tasksL.add(new SimpleTask(3,14,10));
		tasksL.add(new SimpleTask(6,16,7));
		return tasksL;
	}
	
	@Before
	public void initialize() {
		this.tasksL=getExample();
	}

	private final void testSort(int[] order) {
		for (int i = 0; i < order.length; i++) {
			assertEquals("sort : ",order[i],tasksL.get(i).getID());
		}
	}

	@Test
	public void testTasksComparator() {
		LOGGER.info(""+tasksL);
		Collections.sort(tasksL,TaskComparators.makeEarliestStartingTimeCmp());
		testSort(new int[] {0,2,3,1,4});
		Collections.sort(tasksL, TaskComparators.makeEarliestCompletionTimeCmp());
		testSort(new int[] {0,2,1,3,4});
		Collections.sort(tasksL,TaskComparators.makeLatestStartingTimeCmp());
		testSort(new int[] {3,2,4,0,1});
		Collections.sort(tasksL,TaskComparators.makeLatestCompletionTimeCmp());
		testSort(new int[] {2,4,3,0,1});
		Collections.sort(tasksL,TaskComparators.makeMinDurationCmp());
		testSort(new int[] {2,0,1,4,3});
	}
	
	@Test
	public void testTaskVariable() {
		CPModel m = new CPModel();
		choco.kernel.model.variables.scheduling.TaskVariable t1 = Choco.makeTaskVar("T1", 20, 5, CPOptions.V_BOUND);
		choco.kernel.model.variables.scheduling.TaskVariable t2 = Choco.makeTaskVar("T2", 20, 8, CPOptions.V_BOUND, CPOptions.V_NO_DECISION);
		choco.kernel.model.variables.scheduling.TaskVariable t3 = Choco.makeTaskVar("T3", 25, 8, CPOptions.V_ENUM);
		m.addVariables(t1,t2, t3);
		CPSolver solver =new CPSolver();
		solver.read(m);
		LOGGER.info(solver.pretty());
		assertEquals(4, solver.getIntDecisionVars().size());
		assertEquals(3, solver.getNbTaskVars());
		assertEquals(2, solver.getTaskDecisionVars().size());
		assertTrue(solver.getVar(t3).start().getDomain().isEnumerated());
	}
	

	@Test
	public void testPreserved() {
		final Solver s = new CPSolver();
		final IntDomainVar x = s.createBoundIntVar("x", 1, 6);
		final IntDomainVar y = s.createBoundIntVar("y", 3, 7);
		Assert.assertEquals(30, TaskUtils.getA(x, y));
		Assert.assertEquals(56, TaskUtils.getB(x, y));
		Assert.assertEquals(6, TaskUtils.getCmin(x, y));
		Assert.assertEquals(2, TaskUtils.getCmax(x, y));
		Assert.assertEquals( 24.0/30, TaskUtils.getPreserved(x, y));
	}


}
