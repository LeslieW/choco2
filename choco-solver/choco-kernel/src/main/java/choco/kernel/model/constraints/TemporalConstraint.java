package choco.kernel.model.constraints;

import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

public class TemporalConstraint extends ComponentConstraint implements ITemporalRelation<TaskVariable, IntegerVariable> {


	private static final long serialVersionUID = 5410687647692627875L;

	public TemporalConstraint(ConstraintType constraintType,
			Object parameters, Variable[] variables) {
		super(constraintType, parameters, variables);
	}

	public final boolean isInPreprocess() {
		assert checkDomains();
		if (getParameters() instanceof Boolean) {
			return (Boolean) getParameters() 
			&& getForwardSetup().getLowB() >= 0
			&& getBackwardSetup().getLowB() >= 0;
		}
		return false;
	}

    public final boolean checkDomains() {
		return getDirection().isBoolean()
		&& getForwardSetup().isConstant()
		&& getBackwardSetup().isConstant();
	}

	/* (non-Javadoc)
	 * @see choco.kernel.model.constraints.ITemporalRelation#getOrigin()
	 */
	public final TaskVariable getOrigin() {
		return (TaskVariable) getVariable(0);
	}

	public final int getOHook() {
		return getOrigin().getHook();
	}

	public final IntegerVariable getForwardSetup() {
		return (IntegerVariable) getVariable(1);
	}


	/* (non-Javadoc)
	 * @see choco.kernel.model.constraints.ITemporalRelation#forwardSetup()
	 */
	public final int forwardSetup() {
		return getForwardSetup().getLowB();
	}
	
	public final void setForwardSetup(int val) {
		replaceByConstantAt(1, val);
	}

	/* (non-Javadoc)
	 * @see choco.kernel.model.constraints.ITemporalRelation#getDestination()
	 */
	public final TaskVariable getDestination() {
		return (TaskVariable) getVariable(2);
	}

	public final int getDHook() {
		return getDestination().getHook();
	}

	public final IntegerVariable getBackwardSetup() {
		return (IntegerVariable) getVariable(3);
	}

	/* (non-Javadoc)
	 * @see choco.kernel.model.constraints.ITemporalRelation#backwardSetup()
	 */
	public final int backwardSetup() {
		return getBackwardSetup().getLowB();
	}

	public final void setBackwardSetup(int val) {
		replaceByConstantAt(3, val);
	}

	/* (non-Javadoc)
	 * @see choco.kernel.model.constraints.ITemporalRelation#getDirection()
	 */
	public final IntegerVariable getDirection() {
		return (IntegerVariable) getVariable(4);
	}

	@Deprecated
	public final boolean isDirConstant() {
		return getDirection().isConstant();
	}
	
	@Deprecated
	public final int getDirVal() {
		return getDirection().getLowB();
	}

	
	/**
	 * check before that isFixed() = true
	 */
	@Override
	public final boolean isBackward() {
		return getDirection().canBeEqualTo(0);
	}

	@Override
	public final boolean IsFixed() {
		return getDirection().isConstant();
	}

	/**
	 * check before that isFixed() = true
	 */
	@Override
	public final boolean isForward() {
		return getDirection().canBeEqualTo(1);
	}

	@Override
	public String toString() {
		return pretty();
	}

	
}
