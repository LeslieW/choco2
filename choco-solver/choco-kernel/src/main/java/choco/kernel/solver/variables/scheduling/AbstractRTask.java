package choco.kernel.solver.variables.scheduling;

import choco.kernel.common.util.tools.TaskUtils;

public abstract class AbstractRTask implements IRTask {
	
	
	public final int taskIdx;

	public AbstractRTask(int taskIdx) {
		super();
		this.taskIdx = taskIdx;
	}
	
	@Override
	public final int getMaxHeight() {
		return getHeight().getSup();
	}

	@Override
	public final int getMinHeight() {
		return getHeight().getInf();
	}

	@Override
	public final long getMaxConsumption() {
		return TaskUtils.getMaxConsumption(this);
	}

	@Override
	public final long getMinConsumption() {
		return TaskUtils.getMinConsumption(this);
	}

	@Override
	public final int getTaskIndex() {
		return taskIdx;
	}
	
	
}
