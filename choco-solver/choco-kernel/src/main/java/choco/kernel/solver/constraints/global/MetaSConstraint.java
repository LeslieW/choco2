package choco.kernel.solver.constraints.global;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class MetaSConstraint implements SConstraint, IResource<TaskVar> {

	private final static TaskVar[] EMPTY_TASK_ARRAY = new TaskVar[0];

	private final static IntDomainVar[] EMPTY_INTVAR_ARRAY = new IntDomainVar[0];

	
	public final IntDomainVar[] vars;

	public final TaskVar[] tasks;

	public final SConstraint[] constraints;

	public Solver solver;

	protected String name;


	public MetaSConstraint(String name, SConstraint[] constraints, TaskVar[] tasks, IntDomainVar[] vars) {
		this(constraints, tasks, vars);
		this.name = name;
	}
	
	public MetaSConstraint(SConstraint[] constraints, TaskVar[] tasks, IntDomainVar[] vars) {
		if(constraints == null || constraints.length == 0) {
			throw new SolverException("Empty MetaConstraint !?");
		}
		this.constraints = constraints;
		this.vars = vars == null ? EMPTY_INTVAR_ARRAY : vars;
		this.tasks = tasks == null ? EMPTY_TASK_ARRAY : tasks ;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * does not really add a listener as it is useless for propagation.
	 * it only records a list of constraints for a task variables.
	 * @param dynamicAddition
	 */
	public void addListener(final boolean dynamicAddition) {
		//		for (TaskVar t : tasks) {
		//			t.addConstraint(this, -1, dynamicAddition);
		//		}
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
		return vars.length + tasks.length;
	}

	@Override
	public final Solver getSolver() {
		return solver;
	}

	@Override
	public final Var getVar(int i) {
		return i < tasks.length ? tasks[i] : vars[i];
	}

	public final TaskVar getTask(int i) {
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
		throw new UnsupportedOperationException("index is useless");
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
		throw new UnsupportedOperationException("cant change the scope of a meta constraint.");
	}

	@Override
	public String pretty() {
		return "intvars"+StringUtils.pretty(vars)+"\ntasks"+StringUtils.pretty(tasks)+"\nsubconstraints"+StringUtils.pretty(constraints);
	}

	@Override
	public List<TaskVar> asList() {
		return Arrays.asList(tasks);
	}

	@Override
	public int getNbTasks() {
		return tasks.length;
	}

	@Override
	public String getRscName() {
		return name;
	}

	@Override
	public IRTask getRTask(int idx) {
		return null;
	}

	@Override
	public Iterator<TaskVar> getTaskIterator() {
		return IteratorUtils.iterator(tasks);
	}


}