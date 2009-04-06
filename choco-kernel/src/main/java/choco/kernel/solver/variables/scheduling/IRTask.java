package choco.kernel.solver.variables.scheduling;

import choco.IPretty;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;


interface IAltRTask {

	boolean isOptional();

	boolean isRegular();
	
	boolean isEliminated();
	
	boolean assign() throws ContradictionException;
	
	boolean remove() throws ContradictionException;

}

interface IEnergyRTask {

	long getMinConsumption();

	long getMaxConsumption();

	//IntDomainVar getConsumption();

}


interface ICumulRTask extends IEnergyRTask {

	int getMinHeight();

	int getMaxHeight();

	IntDomainVar getHeight();

	boolean updateMaxHeight(int val) throws ContradictionException;

	boolean updateMinHeight(int val) throws ContradictionException;

}


public interface IRTask extends ICumulRTask,IAltRTask, IPretty {

	int getTaskIndex();

	TaskVar getTaskVar();
	
	/**
	 * Update the Earliest Completion Time (ECT).
	 *
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	boolean updateECT(final int val) throws ContradictionException;

	/**
	 * Update the Earliest Starting Time (EST).
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	boolean updateEST(final int val) throws ContradictionException;

	/**
	 * Update the Latest Completion Time (LCT).
	 *
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	boolean updateLCT(final int val) throws ContradictionException;

	/**
	 * Update the Latest Starting Time (LST).
	 *
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	boolean updateLST(final int val) throws ContradictionException;

	boolean updateMinDuration(final int val) throws ContradictionException;

	boolean updateMaxDuration(final int val) throws ContradictionException;

	boolean setDuration(final int duration) throws ContradictionException;

	boolean setStartingTime(final int startingTime) throws ContradictionException;

	boolean schedule(final int startingTime,final int duration) throws ContradictionException;
	
	void fail() throws ContradictionException;

	
}
