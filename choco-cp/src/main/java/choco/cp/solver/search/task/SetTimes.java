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
package choco.cp.solver.search.task;


import choco.IPretty;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractLargeIntBranching;
import choco.kernel.solver.search.task.RandomizedTaskSelector;
import choco.kernel.solver.search.task.TaskSelector;
import choco.kernel.solver.search.task.TaskVarSelector;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.*;
import java.util.logging.Level;
/**
 * @author Arnaud Malapert</br> 
 * @since 25 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
class SetTimesNode implements IPretty {

	public TaskVar lastSelected;

	public final LinkedHashSet<TaskVar> selectables;

	public SetTimesNode(final LinkedHashSet<TaskVar> selectables) {
		super();
		this.selectables=selectables;
	}

	public void removeLastSelected() {
		selectables.remove(lastSelected);
		lastSelected = null;
	}

	
	public final void setLastSelected(TaskVar lastSelected) {
		this.lastSelected = lastSelected;
	}

	public TaskVar getLastSelected() {
		return lastSelected;
	}

	@Override
	public String pretty() {
		return lastSelected.pretty();
	}

	@Override
	public String toString() {
		return getLastSelected().start().pretty();
	}

}

//class SetTimesNode implements IPretty {
//
//	public int lastSelected;
//
//	public final List<TaskVar> selectables;
//
//	public final static int NONE=-1;
//
//	public SetTimesNode(final List<TaskVar> selectables) {
//		super();
//		lastSelected=NONE;
//		this.selectables=selectables;
//	}
//
//	public TaskVar removeLastSelected() {
//		return selectables.remove(lastSelected);
//	}
//
//	public TaskVar getLastSelected() {
//		return selectables.get(lastSelected);
//	}
//
//	@Override
//	public String pretty() {
//		return selectables.get(lastSelected).pretty();
//	}
//
//	@Override
//	public String toString() {
//		return getLastSelected().start().pretty();
//	}
//
//
//
//
//}



/**
 * A search designed to minimize makespan. The search is not complete, so don't try a solveAll.
 *
 * @author Arnaud Malapert
 */
public class SetTimes extends AbstractLargeIntBranching {


	/** The flags wich indicates the last starting time of non selectable task. */
	protected final IStateInt[] flags;

	/** The subset of selectable tasks. */
	protected final StoredBitSet select;

	/** The subset of non selectable tasks. */
	protected final StoredBitSet nselect;



	/** The tasks list. */
	protected final List<TaskVar> tasksL;

	/** map of index*/
	protected final Map<TaskVar, Integer> taskIndexM;
	//TODO try to avoid boxing (use int instead)
	
	/** select a task. */
	protected final TaskVarSelector selector;
	
	
	public SetTimes(final Solver solver, final List<TaskVar> tasks, final Comparator<ITask> comparator, final boolean randomized) {
		this(solver, tasks, randomized ? new RandomizedTaskSelector(comparator) : new TaskSelector(comparator));
	}

	/**
	 * The Constructor.
	 *
	 * @param selector the heuristic
	 * @param tasks the selectables
	 */
	public SetTimes(final Solver solver, final List<TaskVar> tasks, final TaskVarSelector selector) {
		super();
		this.tasksL=new ArrayList<TaskVar>(tasks);
		taskIndexM=new HashMap<TaskVar, Integer>(tasksL.size());
		this.selector=selector;
		final IEnvironment env=solver.getEnvironment();
		select=(StoredBitSet) env.makeBitSet(tasks.size());
		select.set(0, this.tasksL.size());
		nselect=(StoredBitSet) env.makeBitSet(tasks.size());
		flags=new IStateInt[this.tasksL.size()];
		for (int i = 0; i < flags.length; i++) {
			flags[i]=env.makeInt();
			taskIndexM.put(this.tasksL.get(i), i);
		}
		LOG_DECISION_MSG=new String[]{"=="};
	}

	@Override
	public String getDecisionLogMsg(final int branchIndex) {
		return LOG_DECISION_MSG[0];
	}

	/**
	 * Finished branching.
	 *
	 * @return true, if there is no more selectable tasks for this node.
	 *
	 * @see choco.kernel.solver.branch.IntBranching#finishedBranching(java.lang.Object, int)
	 */
	@Override
	public boolean finishedBranching(final Object x, final int i) {
		final SetTimesNode n= (SetTimesNode) x;
		return n.selectables.isEmpty();
	}

	/**
	 * Gets the first branch.
	 *
	 * @param x the x
	 *
	 * @return the first branch : 1
	 *
	 * @see choco.kernel.solver.branch.IntBranching#getFirstBranch(java.lang.Object)
	 */
	@Override
	public int getFirstBranch(final Object x) {
		return 1;
	}

	/**
	 * Gets the next branch.
	 *
	 * @param x the x
	 * @param i the i
	 *
	 * @return i+1
	 *
	 * @see choco.kernel.solver.branch.IntBranching#getNextBranch(java.lang.Object, int)
	 */
	@Override
	public int getNextBranch(final Object x, final int i) {
		return i+1;
	}


	/**
	 * Select branching object.
	 * The branching object is a set of tasks. At each branch, it selects a task with the heuristic and it assigns its earliest starting time.
	 *
	 *	Handle transition between selectable and not selectable tasks.
	 *  @return the Current branching node
	 *
	 */
	@Override
	public Object selectBranchingObject() throws ContradictionException {
		//CPSolver.flushLogs();
		//update non selectable
		for (int i = nselect.nextSetBit(0); i >= 0; i = nselect.nextSetBit(i + 1)) {
			if(tasksL.get(i).getEST()!=flags[i].get()) {
				select.set(i);
				nselect.clear(i);
			}
		}
		//create selectable list
		final LinkedHashSet<TaskVar> l=new LinkedHashSet<TaskVar>(select.cardinality());
		for (int i = select.nextSetBit(0); i >= 0; i = select.nextSetBit(i + 1)) {
			if(tasksL.get(i).isScheduled()) {
				select.clear(i);
			}else {
				l.add(tasksL.get(i));
			}
		}
		return l.isEmpty() ? null : new SetTimesNode(l);
	}


	/**
	 * select a remaining task with the heuristic and assign its earliest starting time.
	 *
	 * @param x the x
	 * @param i the i
	 *
	 * @throws ContradictionException the contradiction exception
	 *
	 * @see choco.kernel.solver.branch.AbstractIntBranching#goDownBranch(java.lang.Object, int)
	 */
	@Override
	public void goDownBranch(final Object x, final int i) throws ContradictionException {
		final SetTimesNode n= (SetTimesNode) x;
		final TaskVar s = selector.selectTaskVar(n.selectables);
		n.setLastSelected(s);
		logDownBranch(s,i);
		s.start().setVal(s.getEST());
	}

	/**
	 * set the task not selectable.
	 *
	 */
	@Override
	public void goUpBranch(final Object x, final int i) throws ContradictionException {
		//enlever la dernière valeur
		final SetTimesNode n=(SetTimesNode) x;
		final TaskVar t = n.getLastSelected();
		n.removeLastSelected();
		logUpBranch(t,i);
		final int idx=this.taskIndexM.get(t);
		nselect.set(idx);
		flags[idx].set(t.getEST());
	}

	
	
	@Override
	protected String getLogMessage() {
		return getLogMessageWithBranch();
	}

	@Override
	protected Object getValueLogParameter(Object x, int branch) {
		return ((TaskVar) x).getEST();
	}

}
