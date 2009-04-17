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
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.TaskComparators;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TestTask {


	private List<SimpleTask> tasksL;

	public final static List<SimpleTask> getExample() {
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

	private void testSort(int[] order) {
		for (int i = 0; i < order.length; i++) {
			assertEquals("sort : ",order[i],tasksL.get(i).getID());
		}
	}

	@Test
	public void testTasksComparator() {
		System.out.println(tasksL);
		Collections.sort(tasksL,TaskComparators.makeEarliestStartingTimeCmp());
		testSort(new int[] {0,2,3,1,4});
		Collections.sort(tasksL,TaskComparators.makeEarliestCompletionTimeCmp());
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
		choco.kernel.model.variables.scheduling.TaskVariable t1 = Choco.makeTaskVar("T1", 20, 5, "cp:bound");
		choco.kernel.model.variables.scheduling.TaskVariable t2 = Choco.makeTaskVar("T2", 20, 8, "cp:bound", "cp:no_decision");
		choco.kernel.model.variables.scheduling.TaskVariable t3 = Choco.makeTaskVar("T3", 25, 8, "cp:enum");
		m.addVariables(t1,t2, t3);
		CPSolver solver =new CPSolver();
		solver.read(m);
		System.out.println(solver.pretty());
		assertEquals(4, solver.getIntDecisionVars().size());
		assertEquals(3, solver.getNbTaskVars());
		assertEquals(2, solver.getTaskDecisionVars().size());
		assertTrue(solver.getVar(t3).start().getDomain().isEnumerated());
	}
	


}