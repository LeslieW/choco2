package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;


/**
 * an abstract class which represents a node of the tree.
 */

public abstract class AbstractNode {

	/** The Constant DEFAULT_COLOR. */
	protected static final String DEFAULT_COLOR = "white";


	/** The task associated with this node. the task is virtual if the node is an internal node */
	public final int task;

	/** The father of this node. */
	protected InternalNode father=null;

	/** The informations associated to the node. */
	public final AbstractNodeInfo infos;

	/**
	 *  Instantiates a new node.
	 *
	 * @param task the task
	 * @param infos informations associated to the node
	 */
	public AbstractNode(final int task,AbstractNodeInfo infos) {
		this.task=task;
		this.infos=infos;
	}

	/**
	 * reset the node.
	 * @param tasks the set of tasks
	 * @param ectOrLst the type of tree
	 */
	public  abstract void reset(ITasksSet tasks, boolean ectOrLst);


	/**
	 * a function used for dotty drawing
	 *
	 */
	protected abstract String getDotStyle();
	/**
	 * Checks for father.
	 *
	 * @return true, if successful
	 */
	public final boolean hasFather() {
		return father!=null;
	}


	/**
	 * Sets the father.
	 *
	 * @param father the father to set
	 */
	public final void setFather(final InternalNode father) {
		this.father = father;
	}

/**
 * getter
 * @return the father of the node
 */
	public InternalNode getFather() {
		return father;
	}

	/**
	 * Gets the dot name.
	 *
	 * @return the dot name
	 */
	protected final String getDotName() {
		return "node"+task;
	}


	/**
	 * To dot string.
	 *
	 * @return this node in Dot format
	 */
	protected String toDotString() {
		final StringBuilder buf=new StringBuilder();
		buf.append(getDotName()).append("[shape=record,");
		buf.append("style=").append(getDotStyle()).append(",fillcolor=").append(this.dotColor());
		buf.append(",label=\"");
		buf.append(task).append('|');
		infos.toDotCells(buf);
		buf.append("\"]\n");
		return buf.toString();
	}

	/**
	 * Dot color.only for lambda subset.
	 *
	 * @return the color
	 */
	protected String dotColor() {
		return DEFAULT_COLOR;
	}


}

