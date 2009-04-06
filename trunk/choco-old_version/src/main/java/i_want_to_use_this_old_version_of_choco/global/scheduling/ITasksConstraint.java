/**
 *
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling;

import i_want_to_use_this_old_version_of_choco.ContradictionException;



/**
 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
 *
 */
public interface ITasksConstraint {
	/**
	 * Update ECT.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	public boolean updateECT(final int i, final int val) throws ContradictionException;

	/**
	 * Update EST.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	public boolean updateEST(final int i,final int val) throws ContradictionException;

	/**
	 * Update ECT.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	public boolean updateLCT(final int i, final int val) throws ContradictionException;



	/**
	 * Update LST.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	public boolean updateLST(final int i, final int val) throws ContradictionException;

}
