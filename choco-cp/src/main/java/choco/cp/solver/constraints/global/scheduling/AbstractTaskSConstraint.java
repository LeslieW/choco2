package choco.cp.solver.constraints.global.scheduling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.ChocoUtil;
import choco.kernel.common.util.IntIterator;
import choco.kernel.common.util.UtilAlgo;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public abstract class AbstractTaskSConstraint extends AbstractLargeIntSConstraint {

	protected static final int EVENT_MASK = IntVarEvent.INSTINTbitvector + IntVarEvent.INCINFbitvector + IntVarEvent.DECSUPbitvector ;;
	protected final TaskVar[] taskvars;
	protected final int taskIntVarOffset;
	
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
		super(UtilAlgo.append(taskVarToIntVar(taskvars), otherVars));
		this.taskvars = taskvars;
		this.taskIntVarOffset = 3 *getNbTasks();
	}
	
	public static final TaskVar[] createTaskVarArray(Solver solver) {
		TaskVar[] tasks = new TaskVar[solver.getNbTaskVars()];
		for (int i = 0; i < solver.getNbTaskVars(); i++) {
			tasks[i] = solver.getTaskVar(i);
			if(tasks[i].getID() != i) {
				throw new SolverException("invalid task ID");
			}
		}
		return tasks;
	}
	
	public static final IntDomainVar[] taskVarToIntVar(final TaskVar[] taskvars) {
		final int n=taskvars.length;
		final IntDomainVar[] ivars=new IntDomainVar[3*n];
		for (int i = 0; i < n; i++) {
			ivars[i]=taskvars[i].start();
			ivars[i+n]=taskvars[i].end();
			ivars[i+2*n]=taskvars[i].duration();
		}
		return ivars;
	}

	protected boolean isStartEvent(int idx) {
		return idx < getNbTasks();
	}
	
	protected boolean isEndEvent(int idx) {
		return getNbTasks() <= idx && idx < 2*getNbTasks();
	}
	
	protected boolean isDurationEvent(int idx) {
		return 2*getNbTasks() <= idx && idx < taskIntVarOffset;
	}
	
	protected final int getTaskIntVarOffset() {
		return taskIntVarOffset;
	}

	protected TaskVar intVarIndexToTask(final int vidx) {
		return vidx < 3*getNbTasks() ? getTask(vidx % getNbTasks()) : null; 
	}

	protected final int getCIndiceStart(final int taskIdx) {
		return this.cIndices[taskIdx];
	}

	protected final int getCIndiceEnd(final int taskIdx) {
		return this.cIndices[getNbTasks() + taskIdx];
	}

	protected final int getCIndiceDuration(final int taskIdx) {
		return this.cIndices[2 * getNbTasks() + taskIdx];
	}

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

	public final void ensureTaskConsistency() throws ContradictionException {
		for (int i = 0; i < getNbTasks(); i++) {
			updateCompulsoryPart(i);
		}
	}

	public final void updateCompulsoryPart(final int taskIdx)
			throws ContradictionException {
				final TaskVar t= taskvars[taskIdx];
				final IntDomainVar s = t.start();
				final IntDomainVar e = t.end();
				final IntDomainVar d = t.duration();
				final int sidx =getCIndiceStart(taskIdx);
				final int eidx =getCIndiceEnd(taskIdx);
				final int didx =getCIndiceDuration(taskIdx);
				
				boolean fixPoint = true;
				while (fixPoint) {
					fixPoint = false;
					fixPoint |= s.updateInf(e.getInf() - d.getSup(), sidx);
					fixPoint |= s.updateSup(e.getSup() - d.getInf(), sidx);
					fixPoint |= e.updateInf(s.getInf() + d.getInf(), eidx);
					fixPoint |= e.updateSup(s.getSup() + d.getSup(), eidx);
					fixPoint |= d.updateInf(e.getInf() - s.getSup(), didx);
					fixPoint |= d.updateSup(e.getSup() - s.getInf(), didx);
				}
			}

	@Override
	public void awakeOnRemovals(final int idx, final IntIterator deltaDomain)
			throws ContradictionException {
				//nothing to do
			}

	@Override
	public void awakeOnRem(final int varIdx, final int val)
			throws ContradictionException {
				//nothing to do
			}

	public int getNbTasks() {
		return taskvars.length;
	}

	public TaskVar getTask(final int idx) {
		return taskvars[idx];
	}

	@Override
	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append(this.toString());
		b.append(" taskvars ");
		b.append(ChocoUtil.pretty(taskvars));
		if( vars.length > 3 * getNbTasks()) {
			b.append(" intvars");
			b.append(ChocoUtil.pretty(vars, 3 * getNbTasks(), vars.length));
		}
		return new String(b);
	}

}