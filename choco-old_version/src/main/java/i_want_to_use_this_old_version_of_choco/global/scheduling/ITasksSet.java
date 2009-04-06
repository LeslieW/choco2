/**
 *
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling;


/**
 * The Interface ITasksSet.
 *
 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
 */
public interface ITasksSet {


	/**
	 * Gets the Earliest Starting Time (EST).
	 *
	 * @param i the task's index
	 *
	 * @return the EST
	 */
	public int getEST(final int i);

	/**
	 * Gets the Earliest Completion Time (ECT).
	 *
	 * @param i the task's index
	 *
	 * @return the ECT
	 */
	public int getECT(final int i);

	/**
	 * Gets the Latest Starting Time (LST).
	 *
	 * @param i the task's index
	 *
	 * @return the LST
	 */
	public int getLST(final int i);

	/**
	 * Gets the Latest Completion Time (LCT).
	 *
	 * @param i the task's index
	 *
	 * @return the LCT
	 */
	public int getLCT(final int i);

	/**
	 * Gets the number of tasks.
	 *
	 * @return the number of tasks on this resource
	 */
	public int getNbTasks();

	/**
	 * Gets the processing times.
	 *
	 * @param i the task's index
	 *
	 * @return the processing time of the task
	 */
	public int getProcessingTime(final int i);

	/**
	 * Gets the processing times.
	 *
	 * @param i the task's index
	 *
	 * @return the processing time of the task
	 */
	public int getHeight(final int i);


	/**
	 * get the consumption of the tasks. The results depends of the type of resource.
	 * @param i the task's index
	 * @return the consumpiton of the task
	 */
	public long getConsumption(final int i);

	/**
	 * Get the sum of all processing times
	 * @return the total load of the set
	 */
	public int getTotalLoad();

	/**
	 * Get the sum of all processing times
	 * @return the total load of the set
	 */
	public int getTotalConsumption();

}
