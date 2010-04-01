package choco.cp.solver.search.task;

import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;



/**
 * @author Arnaud Malapert</br> 
 * @since 18 juin 2009 version 2.1.0</br>
 * @version 2.1.0</br>
 */
public final class StoredPrecedence implements IPrecedence {

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

	public final IntDomainVar getBoolVar() {
		return direction;
	}
	
	public final int getDomMesure() {
		return TaskUtils.getSlack(t1) + TaskUtils.getSlack(t2) + 2;
	}
	
	@Override
	public String toString() {
		return "("+t1.getName()+","+t2.getName()+")";
	}
	
}