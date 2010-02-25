package choco.kernel.solver.variables.scheduling;

import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.solver.ContradictionException;

public abstract class AbstractRTask implements IRTask {

	protected int value;

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

	@Override
	public final int getStoredValue() {
		return value;
	}

	@Override
	public void storeValue(int val) {
		this.value=val;

	}

	@Override
	public final boolean updateECT() throws ContradictionException {
		return updateECT(value);
	}

	@Override
	public final boolean updateEST() throws ContradictionException {
		return updateEST(value);
	}

	@Override
	public final boolean updateLCT() throws ContradictionException {
		return updateLCT(value);
	}

	@Override
	public final boolean updateLST() throws ContradictionException {
		return updateLST(value);
	}




}
