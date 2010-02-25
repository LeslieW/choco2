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
	
	void fireRemoval();

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

/**
 * Update operations update the domain and ensure task consistency whereas set operations update the domain without checking. 
 * @author Arnaud Malapert</br> 
 * @since 4 sept. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public interface IRTask extends ICumulRTask,IAltRTask, IPretty {

	int getTaskIndex();

	TaskVar getTaskVar();
	
	ITask getHTask();

	void checkConsistency() throws ContradictionException;
	
	void updateCompulsoryPart() throws ContradictionException;

	void fail() throws ContradictionException;

	/**
	 * Update the Earliest Completion Time (ECT).
	 */
	boolean updateECT(final int val) throws ContradictionException;

	/**
	 * Update the Earliest Starting Time (EST).
	 */
	boolean updateEST(final int val) throws ContradictionException;

	/**
	 * Update the Latest Completion Time (LCT).
	 *
	 */
	boolean updateLCT(final int val) throws ContradictionException;

	/**
	 * Update the Latest Starting Time (LST).
	 */
	boolean updateLST(final int val) throws ContradictionException;

	/**
	 * The task can not start in the interval [a,b].
	 */
	boolean updateStartNotIn(final int a, final int b) throws ContradictionException;
	
	/**
	 * The task can not end in the interval [a,b].
	 */
	boolean updateEndNotIn(final int a, final int b) throws ContradictionException;

	boolean updateMinDuration(final int val) throws ContradictionException;

	boolean updateMaxDuration(final int val) throws ContradictionException;

	boolean updateDuration(final int duration) throws ContradictionException;

	boolean updateStartingTime(final int startingTime) throws ContradictionException;

	boolean updateEndingTime(final int endingTime) throws ContradictionException;


	/**
	 * Update the Earliest Completion Time (ECT).
	 */
	boolean setECT(final int val) throws ContradictionException;

	/**
	 * Update the Earliest Starting Time (EST).
	 */
	boolean setEST(final int val) throws ContradictionException;

	/**
	 * Update the Latest Completion Time (LCT).
	 *
	 */
	boolean setLCT(final int val) throws ContradictionException;

	/**
	 * Update the Latest Starting Time (LST).
	 */
	boolean setLST(final int val) throws ContradictionException;

	/**
	 * The task can not start in the interval [a,b].
	 */
	boolean setStartNotIn(final int a, final int b) throws ContradictionException;
	
	/**
	 * The task can not end in the interval [a,b].
	 */
	boolean setEndNotIn(final int a, final int b) throws ContradictionException;

	boolean setMinDuration(final int val) throws ContradictionException;

	boolean setMaxDuration(final int val) throws ContradictionException;

	boolean setDuration(final int duration) throws ContradictionException;

	boolean setStartingTime(final int startingTime) throws ContradictionException;

	boolean setEndingTime(final int endingTime) throws ContradictionException;

	/**
	 * Utility: A filtering algorithm can store a value to perform update operations (noargs) later.
	 */
	void storeValue(int val);
	
	int getStoredValue();
	
	/**
	 * Update using {@link IRTask#getStoredValue()}   .
	 */
	boolean updateECT() throws ContradictionException;
	
	/**
	 * Update using {@link IRTask#getStoredValue()}   .
	 */
	boolean updateEST() throws ContradictionException;
	
	/**
	 * Update using {@link IRTask#getStoredValue()}   .
	 */
	boolean updateLCT() throws ContradictionException;
	
	/**
	 * Update using {@link IRTask#getStoredValue()}   .
	 */
	boolean updateLST() throws ContradictionException;
}
