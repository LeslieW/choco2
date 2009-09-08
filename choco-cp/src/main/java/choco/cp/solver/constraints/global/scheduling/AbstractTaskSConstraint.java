package choco.cp.solver.constraints.global.scheduling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public abstract class AbstractTaskSConstraint extends AbstractLargeIntSConstraint {

	protected static final int EVENT_MASK = IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;

	protected final TaskVar[] taskvars;

	protected final int startOffset;

	protected final int endOffset;

	public final int taskIntVarOffset;

	/**
	 * 
	 * Create a task constraint.
	 * @param taskvars the tasks using the resources
	 * @param otherVars other integer variables of the constraint
	 */
	public AbstractTaskSConstraint(final TaskVar task1, TaskVar task2, final IntDomainVar... otherVars) {
		this(new TaskVar[]{task1,task2}, otherVars);
	}

	/**
	 * 
	 * Create a task constraint.
	 * @param taskvars the tasks using the resources
	 * @param otherVars other integer variables of the constraint
	 */
	public AbstractTaskSConstraint(final TaskVar[] taskvars, final IntDomainVar... otherVars) {
		super(makeIntVarArray(taskvars, otherVars));
		this.taskvars = taskvars;
		startOffset = getNbTasks();
		endOffset = 2 * startOffset;
		this.taskIntVarOffset = 3 * startOffset;
	}

	public static final TaskVar[] createTaskVarArray(Solver solver) {
		final int n = solver.getNbTaskVars();
		TaskVar[] tasks = new TaskVar[n];
		for (int i = 0; i < n; i++) {
			tasks[i] = solver.getTaskVar(i);
			if(tasks[i].getID() != i) {
				throw new SolverException("invalid task ID");
			}
		}
		return tasks;
	}

	public static final IntDomainVar[] makeIntVarArray(final TaskVar[] taskvars, IntDomainVar[] othervars) {
		final int n=taskvars.length;
		final int v1 = 2 * n;
		final int v2 = 3 * n;
		final int v3 =  v2 + (othervars == null ? 0 : othervars.length);
		final IntDomainVar[] ivars=new IntDomainVar[v3];
		for (int i = 0; i < n; i++) {
			ivars[i]=taskvars[i].start();
			ivars[i + n]=taskvars[i].end();
			ivars[i + v1]=taskvars[i].duration();
		}
		for (int i =v2; i < v3; i++) {
			ivars[i] = othervars[i - v2];
		}
		return ivars;
	}

	//
	//	protected boolean isStartEvent(int idx) {
	//		return idx < startOffset;
	//	}
	//	
	//	protected boolean isEndEvent(int idx) {
	//		return startOffset <= idx && idx < endOffset;
	//	}
	//	
	//	protected boolean isDurationEvent(int idx) {
	//		return endOffset <= idx && idx < taskIntVarOffset;
	//	}

	protected final int getTaskIntVarOffset() {
		return taskIntVarOffset;
	}

	protected final TaskVar intVarIndexToTask(final int vidx) {
		return vidx < taskIntVarOffset ? getTask(vidx % getNbTasks()) : null; 
	}

	protected final int getStartIndex(final int tidx) {
		return tidx;
	}

	protected final int getEndIndex(final int tidx) {
		return startOffset + tidx;
	}

	protected final int getDurationIndex(final int tidx) {
		return startOffset + tidx;
	}

	//	protected final int getCIndiceStart(final int taskIdx) {
	//		return this.cIndices[taskIdx];
	//	}
	//
	//	protected final int getCIndiceEnd(final int taskIdx) {
	//		return this.cIndices[startOffset + taskIdx];
	//	}
	//
	//	protected final int getCIndiceDuration(final int taskIdx) {
	//		return this.cIndices[endOffset + taskIdx];
	//	}

	@Override
	public int getFilteredEventMask(final int idx) {
		return EVENT_MASK;
	}

	@Override
	public void addListener(final boolean dynamicAddition) {
		super.addListener(dynamicAddition);
		for (int i = 0; i < getNbTasks(); i++) {
			getTask(i).addConstraint(this, -1, dynamicAddition);
		}
	}

	//	public final void ensureTaskConsistency() throws ContradictionException {
	//		final int n = getNbTasks();
	//		for (int i = 0; i < n; i++) {
	//			updateCompulsoryPart(i);
	//		}
	//	}

	//	public final void updateCompulsoryPart(final int taskIdx)
	//			throws ContradictionException {
	//				final TaskVar t= taskvars[taskIdx];
	//				final IntDomainVar s = t.start();
	//				final IntDomainVar e = t.end();
	//				final IntDomainVar d = t.duration();
	//				final int sidx =getCIndiceStart(taskIdx);
	//				final int eidx =getCIndiceEnd(taskIdx);
	//				final int didx =getCIndiceDuration(taskIdx);
	//				
	//				boolean fixPoint = true;
	//				while (fixPoint) {
	//					fixPoint = false;
	//					fixPoint |= s.updateInf(e.getInf() - d.getSup(), sidx);
	//					fixPoint |= s.updateSup(e.getSup() - d.getInf(), sidx);
	//					fixPoint |= e.updateInf(s.getInf() + d.getInf(), eidx);
	//					fixPoint |= e.updateSup(s.getSup() + d.getSup(), eidx);
	//					fixPoint |= d.updateInf(e.getInf() - s.getSup(), didx);
	//					fixPoint |= d.updateSup(e.getSup() - s.getInf(), didx);
	//				}
	//			}

	@Override
	public void awakeOnRemovals(final int idx, final DisposableIntIterator deltaDomain)
	throws ContradictionException {
		//nothing to do
	}

	@Override
	public void awakeOnRem(final int varIdx, final int val)
	throws ContradictionException {
		//nothing to do
	}

	public final int getNbTasks() {
		return taskvars.length;
	}

	public final TaskVar getTask(final int idx) {
		return taskvars[idx];
	}

	@Override
	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append(this.toString());
		b.append("tasks");
		b.append(StringUtils.pretty(taskvars));
		if( vars.length > taskIntVarOffset) {
			b.append("\nintvars");
			b.append(StringUtils.pretty(vars, taskIntVarOffset, vars.length));
		}
		return new String(b);
	}

}