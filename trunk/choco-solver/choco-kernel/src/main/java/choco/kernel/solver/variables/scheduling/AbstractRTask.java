package choco.kernel.solver.variables.scheduling;

import choco.kernel.solver.ContradictionException;

public abstract class AbstractRTask implements IRTask {
	
	
	public final int taskIdx;

	public AbstractRTask(int taskIdx) {
		super();
		this.taskIdx = taskIdx;
	}
	
	
	
	@Override
	public final boolean updateDuration(int duration) throws ContradictionException {
		if( setDuration(duration)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}



	@Override
	public final boolean updateECT(int val) throws ContradictionException {
		if( setECT(val)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateEndingTime(int endingTime)
			throws ContradictionException {
		if( setEndingTime(endingTime)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateEndNotIn(int a, int b) throws ContradictionException {
		if( setEndNotIn(a, b)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateEST(int val) throws ContradictionException {
		if( setEST(val)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateLCT(int val) throws ContradictionException {
		if( setLCT(val)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateLST(int val) throws ContradictionException {
		if( setLST(val)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateMaxDuration(int val) throws ContradictionException {
		if( setMaxDuration(val)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateMinDuration(int val) throws ContradictionException {
		if( setMinDuration(val)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateStartingTime(int startingTime)
			throws ContradictionException {
		if( setStartingTime(startingTime)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
	}




	@Override
	public final boolean updateStartNotIn(int a, int b) throws ContradictionException {
		if( setStartNotIn(a, b)) {
			updateCompulsoryPart();
			return true;
		}
		return false;
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
