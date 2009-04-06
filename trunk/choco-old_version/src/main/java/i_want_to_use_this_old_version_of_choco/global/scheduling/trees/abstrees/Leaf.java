package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;


/**
 * The Class Leaf. A leaf represents a task to schedule.
 *
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 */
public class Leaf extends AbstractNode{

	public static final int WHITE = 0;

	public static final int GRAY = 1;

	public static final int BLACK= 2;

	/**
	 * the color of the node
	 */
	private int nodeColor;
	/**
	 * The Constructor.
	 *
	 * @param task the task
	 */
	public Leaf(final int task, final AbstractNodeInfo infos) {
		super(task,infos);
	}


	@Override
	public void reset(final ITasksSet tasks, final boolean ectOrLst) {
		infos.reset(tasks, this, ectOrLst);
	}

	/**
	 * @return the nodeColor
	 */
	public final int getColor() {
		return nodeColor;
	}

	/**
	 * @param nodeColor the nodeColor to set
	 */
	public final void setColor(final int nodeColor) {
		this.nodeColor = nodeColor;
	}


	/**
	 * @see AbstractNode#getDotStyle()
	 */
	@Override
	protected String getDotStyle() {
		return "filled";
	}


	/**
	 * @see AbstractNode#dotColor()
	 */
	@Override
	protected String dotColor() {
		switch (nodeColor) {
		case WHITE:return DEFAULT_COLOR;
		case GRAY:return "lightgray";
		case BLACK:return "black";
		default:
			return "";
		}
	}



}
