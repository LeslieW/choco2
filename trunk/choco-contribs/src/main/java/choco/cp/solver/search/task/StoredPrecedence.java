package choco.cp.solver.search.task;

import choco.IPretty;
import choco.kernel.model.constraints.ITemporalRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;



/**
 * @author Arnaud Malapert</br> 
 * @since 18 juin 2009 version 2.1.0</br>
 * @version 2.1.0</br>
 */
public final class StoredPrecedence implements IPretty, ITemporalRelation<TaskVar<?>, IntDomainVar> {

	private final ITask t1;

	private final ITask t2;

	public final IntDomainVar direction;

	public StoredPrecedence(ITask t1, ITask t2, IntDomainVar direction) {
		super();
		this.t1 = t1;
		this.t2 = t2;
		this.direction = direction;
	}

	public final TaskVar getOrigin() {
		return (TaskVar) t1;
	}

	public final TaskVar getDestination() {
		return (TaskVar) t2;
	}

	public final IntDomainVar getDirectionVar() {
		return direction;
	}
	
	@Override
	public String toString() {
		return "("+t1.getName()+","+t2.getName()+")";
	}

	@Override
	public String pretty() {
		return toString();
	}


	@Override
	public boolean isBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean IsFixed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IntDomainVar getDirection() {
		return direction;
	}

	@Override
	public int backwardSetup() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forwardSetup() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	
	
}