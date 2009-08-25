package choco.kernel.solver.constraints.global;

import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class MetaSConstraint implements SConstraint {

	private final static TaskVar[] EMPTY_TASK_ARRAY = new TaskVar[0];
	
	public final TaskVar[] tasks;

	public final SConstraint[] constraints;

	public Solver solver;

	public MetaSConstraint(SConstraint[] constraints) {
		this(constraints, null);
	}
	
	public MetaSConstraint(SConstraint[] constraints, TaskVar[] tasks) {
		if(constraints == null || constraints.length == 0) {
			throw new SolverException("Empty MetaConstraint !?");
		}
		this.constraints = constraints;
		this.tasks = tasks == null ? EMPTY_TASK_ARRAY : tasks ;
	}

	/**
	 * does not really add a listener as it is useless for propagation.
	 * it only records a list of constraints for a task variables.
	 * @param dynamicAddition
	 */
	public void addListener(final boolean dynamicAddition) {
		for (TaskVar t : tasks) {
			t.addConstraint(this, -1, dynamicAddition);
		}
	}
	
	
	@Override
	public final int getConstraintIdx(int idx) {
		return -1;
	}

	@Override
	public int getFineDegree(int idx) {
		return 0;
	}

	@Override
	public final int getNbVars() {
		return tasks.length;
	}

	@Override
	public final Solver getSolver() {
		return solver;
	}

	@Override
	public final Var getVar(int i) {
		return tasks[i];
	}

	public final Var getTask(int i) {
		return tasks[i];
	}

	public final int getNbSubConstraints() {
		return constraints.length;
	}

	public final SConstraint getSubConstraints(int i) {
		return constraints[i];
	}
	/**
	 * returns the same numbering in a constraint and its counterpart
	 * @param i the idx of a variable
	 * @return the same numbering in a constraint and its counterpart
	 */
	// defaut implementation: returns the same numbering in a constraint and its counterpart.
	@Override
	public int getVarIdxInOpposite(int i) {
		return i;
	}

	@Override
	public boolean isSatisfied() {
		for (SConstraint c : constraints) {
			if( ! c.isSatisfied() ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public AbstractSConstraint opposite() {
		 throw new UnsupportedOperationException("opposite is not supported");
	}

	@Override
	public final void setConstraintIndex(int i, int idx) {
		//throw new UnsupportedOperationException("index is useless");
	}

	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public final void setSolver(Solver solver) {
		this.solver = solver;
	}

	@Override
	public final void setVar(int i, Var v) {
		if (v instanceof TaskVar) {
			tasks[i] = (TaskVar) v;
		}
	}

	@Override
	public String pretty() {
		return "tasks:"+StringUtils.pretty(tasks)+" subconstraints:"+StringUtils.pretty(constraints);
	}

}
