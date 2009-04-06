package choco.kernel.solver.variables.scheduling;

public abstract class AbstractRTask implements IRTask {
	
	
	protected final int taskIdx;

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
		final int h = getMaxHeight();
		return h * ( h>0 ? getTaskVar().getMaxDuration() : getTaskVar().getMinDuration());
	}

	@Override
	public final long getMinConsumption() {
		final int h = getMinHeight();
		return h * ( h>0 ? getTaskVar().getMinDuration() : getTaskVar().getMaxDuration());
	}

	@Override
	public final int getTaskIndex() {
		return taskIdx;
	}
	
	
}
