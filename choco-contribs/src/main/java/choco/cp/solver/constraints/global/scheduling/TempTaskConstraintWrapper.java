package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.listener.TaskPropagator;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class TempTaskConstraintWrapper extends AbstractSConstraint<TaskVar> implements TaskPropagator {

	protected final AbstractUseResourcesSConstraint constraint;
		
	public TempTaskConstraintWrapper(TaskVar task,
			AbstractUseResourcesSConstraint constraint) {
		super(new TaskVar[]{task});
		this.constraint = constraint;
	}

	@Override
	public boolean isConsistent() {
		return constraint.isConsistent();
	}

	@Override
	public void propagate() throws ContradictionException {}

	@Override
	public SConstraintType getConstraintType() {
		return null;
	}

	@Override
	public boolean isSatisfied() {
		return constraint.isSatisfied();
	}

	@Override
	public void awakeOnHypDomMod(int varIdx) throws ContradictionException {
		constraint.filterHypotheticalDomains();		
	}

	
}
