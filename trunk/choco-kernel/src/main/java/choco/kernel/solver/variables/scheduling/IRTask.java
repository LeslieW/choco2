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

/**
 * Update operations update the domain and ensure task consistency whereas set operations only update the domain. 
 * @author Arnaud Malapert</br> 
 * @since 4 sept. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public interface IRTask extends ICumulRTask,IAltRTask, IPretty {

	int getTaskIndex();

	TaskVar getTaskVar();

	void updateCompulsoryPart() throws ContradictionException;

	boolean schedule(final int startingTime,final int duration) throws ContradictionException;

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


}
