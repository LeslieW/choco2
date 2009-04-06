/**
 *
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;



/**
 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
 *
 */
public class UnaryResourceConstraint extends AbstractLargeIntConstraint
		implements ITasksSet,ITasksConstraint {


	/** the processing time array of the tasks. */
	protected final int[] processingTimes;

	protected final int totalLoad;


	/**
	 * @param vars
	 * @param processingTimes
	 */
	public UnaryResourceConstraint(IntDomainVar[] vars, int[] processingTimes) {
		this(vars,processingTimes,null);
	}


	/**
	 * @param vars
	 * @param processingTimes
	 */
	protected UnaryResourceConstraint(IntDomainVar[] vars,int[] processingTimes,IntDomainVar objective) {
		super(concat(vars, objective));
		this.processingTimes = processingTimes;
		int tl=0;
		for (int p : this.processingTimes) {
			tl+=p;
		}
		totalLoad=tl;
	}


	public final static IntDomainVar[] concat(IntDomainVar[] begin,IntDomainVar last) {
		if(last==null) {return begin;}
		else {
		IntDomainVar[] vars=new IntDomainVar[begin.length+1];
		System.arraycopy(begin, 0, vars, 0, begin.length);
		vars[begin.length]=last;
		return vars;
		}
	}


	/**
	 * Gets the objective, if any.
	 *
	 * @return the objective or <code>null</code>
	 */
	protected final IntDomainVar getObjective() {
		return vars[this.getNbTasks()];
	}

	//****************************************************************//
	//************************ INTERFACE TASKS************************//
	//****************************************************************//

	/**
	 * @see choco.global.scheduling.ITasksSet#getECT(int)
	 */
	//@Override
	public final int getECT(final int i) {
		return vars[i].getInf()+processingTimes[i];
	}

	/**
	 * @see choco.global.scheduling.ITasksSet#getEST(int)
	 */
	@Override
	public final int getEST(final int i) {
		return vars[i].getInf();
	}

	/**
	 * @see choco.global.scheduling.ITasksSet#getLCT(int)
	 */
	@Override
	public final int getLCT(final int i) {
		return vars[i].getSup()+processingTimes[i];
	}

	/**
	 * @see choco.global.scheduling.ITasksSet#getLST(int)
	 */
	@Override
	public final int getLST(final int i) {
		return vars[i].getSup();
	}

	/**
	 * @see choco.global.scheduling.ITasksSet#getNbTasks()
	 */
	@Override
	public final int getNbTasks() {
		//TODO y a pas mieux ?
		return processingTimes.length;
	}

	/**
	 * @see choco.global.scheduling.ITasksSet#getProcessingTime(int)
	 */
	@Override
	public final int getProcessingTime(final int i) {
		return processingTimes[i];
	}


	/**
	 * @see choco.global.scheduling.ITasksSet#getConsumption(int)
	 */
	@Override
	public final long getConsumption(int i) {
		return getProcessingTime(i);
	}


	/**
	 * @see choco.global.scheduling.ITasksSet#getTotalLoad()
	 */
	@Override
	public final int getTotalLoad() {
		return totalLoad;
	}


	/**
	 * @see choco.global.scheduling.ITasksSet#getHeight(int)
	 */
	@Override
	public int getHeight(int i) {
		return 1;
	}


	//****************************************************************//
	//************************ INTERFACE UPDATE **********************//
	//****************************************************************//




	/**
	 * Update ECT.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	//@Override
	public final boolean updateECT(final int i, final int val) throws ContradictionException {
		return vars[i].updateInf(val-processingTimes[i],cIndices[i]);
	}

	/**
	 * Update EST.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	//@Override
	public final boolean updateEST(final int i,final int val) throws ContradictionException {
		return vars[i].updateInf(val,cIndices[i]);
	}

	/**
	 * Update ECT.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	//@Override
	public final boolean updateLCT(final int i, final int val) throws ContradictionException {
		return vars[i].updateSup(val-processingTimes[i],cIndices[i]);
	}


	/**
	 * Update LST.
	 *
	 * @param i the task's index
	 * @param val the new value
	 *
	 * @return <code>true</code> if the update changed the domain of the task
	 * @throws ContradictionException
	 */
	//@Override
	public final boolean updateLST(final int i, final int val) throws ContradictionException {
		return vars[i].updateSup(val,cIndices[i]);
	}


	//****************************************************************//
	//************************ UTILS *********************************//
	//****************************************************************//

	protected final void writeTasks(final StringBuilder buffer) {
		for (int i = 0; i < getNbTasks(); i++) {
			buffer.append(' ').append(vars[i].toString());
			buffer.append(",p=").append(processingTimes[i]).append(" ;");
		}
		buffer.deleteCharAt(buffer.length()-1);

	}

	/**
	 * @see choco.AbstractEntity#pretty()
	 */
	@Override
	public String pretty() {
		final StringBuilder buffer=new StringBuilder();
		buffer.append(this.getClass()).append('(');
		buffer.delete(0,6);
		writeTasks(buffer);
		buffer.append(')');
		return buffer.toString();
	}

	/**
	 * @see choco.Constraint#isSatisfied()
	 */
	//@Override
	@Override
	public boolean isSatisfied() {
		System.out.println("isSatisfied not yet implemented on "+this.getClass());
		return false;
	}

	/**
	 * @see choco.AbstractConstraint#isEntailed()
	 */
	@Override
	public Boolean isEntailed() {
		System.out.println("isEntailed not yet implemented on "+this.getClass());
		return false;
	}


	/**
	 * @see choco.global.scheduling.ITasksSet#getTotalConsumption()
	 */
	@Override
	public int getTotalConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

}
