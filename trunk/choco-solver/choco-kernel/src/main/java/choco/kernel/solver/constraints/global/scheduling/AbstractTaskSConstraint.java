package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.listener.TaskPropagator;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public abstract class AbstractTaskSConstraint extends AbstractLargeIntSConstraint implements TaskPropagator {

	protected final TaskVar[] taskvars;
	
	protected final int startOffset;

	protected final int endOffset;

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

	
	public AbstractTaskSConstraint(final TaskVar[] taskvars, final IntDomainVar[] intvars, final IntDomainVar... otherVars) {
		super(makeIntVarArray(taskvars, intvars, otherVars));
		this.taskvars = taskvars;
		startOffset = getNbTasks();
		endOffset = 2 * startOffset;
		this.taskIntVarOffset = 3 * startOffset;
	}
	

	public final static TaskVar[] createTaskVarArray(Solver solver) {
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

	public final static IntDomainVar[] makeIntVarArray(final TaskVar[] taskvars, IntDomainVar[] intvars, IntDomainVar[] othervars) {
		final int n=taskvars.length;
		final int v1 = 2 * n;
		final int v2 = 3 * n;
		final int v3 =  v2 + (intvars == null ? 0 : intvars.length);
		final int v4 =  v3 + (othervars == null ? 0 : othervars.length);
		final IntDomainVar[] ivars= new IntDomainVar[v4];
		for (int i = 0; i < n; i++) {
			ivars[i]=taskvars[i].start();
			ivars[i + n]=taskvars[i].end();
			ivars[i + v1]=taskvars[i].duration();
		}
		for (int i =v2; i < v3; i++) {
			ivars[i] = intvars[i - v2];
		}
		for (int i =v3; i < v4; i++) {
			ivars[i] = othervars[i - v3];
		}
		return ivars;
	}

	protected final int getTaskIntVarOffset() {
		return taskIntVarOffset;
	}

	protected final int getStartIndex(final int tidx) {
		return tidx;
	}

	protected final int getEndIndex(final int tidx) {
		return startOffset + tidx;
	}

	protected final int getDurationIndex(final int tidx) {
		return endOffset+ tidx;
	}

	@Override
	public void addListener(final boolean dynamicAddition) {
		super.addListener(dynamicAddition);
		for (int i = 0; i < getNbTasks(); i++) {
			getTask(i).addConstraint(this, -1, dynamicAddition);
		}
	}


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

	protected final String pretty(String name) {
		StringBuilder b = new StringBuilder();
		b.append(name).append("( ");
		b.append(StringUtils.pretty(taskvars));
		if( vars.length > taskIntVarOffset) {
			b.append(", ");
			b.append(StringUtils.pretty(vars, taskIntVarOffset, vars.length));
		}
		b.append(" )");
		return new String(b);
	}
	@Override
	public String pretty() {
		return pretty(this.getClass().getSimpleName());
	}


	@Override
	public void awakeOnHypDomMod(int varIdx) throws ContradictionException {
		//throw new SolverException("not filtering on hypothetical domain on resources");
		
	}
	
	
}