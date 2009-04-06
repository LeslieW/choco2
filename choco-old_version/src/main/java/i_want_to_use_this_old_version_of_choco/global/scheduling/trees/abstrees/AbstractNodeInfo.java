package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;



/**
 * The Class AbstractNodeInfo is used as a information container for each Node.
 *
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 */
public abstract class AbstractNodeInfo {

	/** The int values. */
	protected final int[] intValues;

	/** The long values. */
	protected final long[] longValues;


	/**
	 * The Constructor.
	 *
	 * @param intValues the int array
	 * @param longValues the long array
	 */
	public AbstractNodeInfo(final int[] intValues, final long[] longValues) {
		super();
		this.intValues = intValues;
		this.longValues = longValues;
	}

	/**
	 * Reset.
	 * @param tasks TODO
	 * @param leaf the leaf
	 * @param ectOrLst the ECT or LST status
	 */
	public abstract void reset(ITasksSet tasks, Leaf leaf, boolean ectOrLst);

	/**
	 * Update.
	 * @param tasks TODO
	 * @param leaf the leaf
	 * @param ectOrLst the ECT or LST status
	 */
	public abstract void update(ITasksSet tasks,Leaf leaf, boolean ectOrLst);


	/**
	 * Reset.
	 *
	 * @param node the node
	 * @param ectOrLst the ECT or LST status
	 */
	public abstract void reset(InternalNode node,boolean ectOrLst);

	/**
	 * Update.
	 *
	 * @param node the node
	 * @param ectOrLst the ECT or LST status
	 */
	public abstract void update(InternalNode node,boolean ectOrLst);

	/**
	 * To dot cells.write the object int graphviz cell(s).
	 *
	 * @param buffer the output buffer
	 */
	public abstract void toDotCells(StringBuilder buffer);

	/**
	 * Gets the value of {@link AbstractNodeInfo#intValues}.
	 *
	 * @param index the index
	 *
	 * @return the value
	 */
	public int getIntValue(final int index) {
		return intValues[index];
	}

	/**
	 * Gets the value of {@link AbstractNodeInfo#longValues}.
	 *
	 * @param index the index
	 *
	 * @return the value
	 */
	public long getLongValue(final int index) {
		return longValues[index];
	}


	/**
	 * Update field in array.set the sum of the two child fields
	 *
	 * @param node the node
	 * @param index the index in the array
	 */
	protected void updateIntSum(final InternalNode node,final int index) {
		this.intValues[index]=node.getLeftChild().infos.getIntValue(index)+node.getRightChild().infos.getIntValue(index);
	}




	/**
	 * Update field in array.set the sum of the two child fields
	 *
	 * @param node the node
	 * @param index the index in the array
	 */
	protected void updateLongSum(final InternalNode node,final int index) {
		this.longValues[index]=node.getLeftChild().infos.getLongValue(index)+node.getRightChild().infos.getLongValue(index);
	}

	/**
	 * Sets the responsible task.
	 *
	 * @param task the new responsible task
	 */
	protected void setResponsibleTask(final int task) {
		intValues[4]=task;
		intValues[5]=task;
	}

	protected void copyThetaInLambda() {
		System.arraycopy(intValues, 0, intValues, 2, 2);
	}


	/**
	 * reset two value of an array. the start field is set to Inf and the start+1 to 0
	 * @param start the first index to reset
	 * @param intOrLong if <code>true</code> {@link AbstractNodeInfo#intValues} else {@link AbstractNodeInfo#longValues}
	 * @param minOrMax if <code>true</code> -Inf else +Inf
	 */
	protected void reset(final int start,final boolean intOrLong,final boolean minOrMax) {
		if(intOrLong) {
			intValues[start]=minOrMax ? Integer.MIN_VALUE :Integer.MAX_VALUE;
			intValues[start+1]=0;
		}else {
			longValues[start]=minOrMax ? Long.MIN_VALUE :Long.MAX_VALUE;
			longValues[start+1]=0;
		}
	}

	/**
	 * used to write a cell with three fields in the Dot buffer.
	 *
	 * @param buffer the output buffer
	 * @param name the name of this nodeInfo
	 * @param time a time value
	 * @param sum field SUM=
	 */
	public static final void toDotCell(final StringBuilder buffer,final String name,final long time,final long sum) {
		buffer.append('{').append(name).append("\\nTIME=").append(time);
		buffer.append("\\nSUM=").append(sum).append('}');
	}
}
