/**
 * created the 4 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;

/**
 * The purpose of a theta-tree is to quickly recompute ECT_{theta} when an activity is inserted or removed from the set Theta.
 * A theta-tree is a balanced binary tree. Activities from the set theta are represented by leaf nodes.
 * Internal nodes of the tree are used to hold some precomputed values.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public abstract class AbstractThetaTree extends AbstractTree {


	public AbstractThetaTree(ITasksSet tasks,boolean ectOrLst) {
		super(tasks,ectOrLst);
	}


	/**
	 * insert a task in theta subset
	 * @param task the task to insert
	 * @return <code>true</code> if successful
	 */

	public final boolean insert(final int task) {
		final Leaf leaf = getLeaf(task);
		if(leaf.getColor()== Leaf.GRAY) {
			leaf.infos.update(tasks,leaf, this.ectOrLst);
			leaf.setColor(Leaf.WHITE);
			if(leaf.hasFather()) {leaf.getFather().update(this.ectOrLst);}
			return true;
		}
		return false;
	}


	/**
	 * remove a task of theta subset
	 * @param task the task to remove
	 * @return <code>true</code> if successful
	 */
	public boolean remove(final int task) {
		final Leaf leaf = getLeaf(task);
		if(leaf.getColor()== Leaf.WHITE) {
			leaf.infos.reset(tasks, leaf, ectOrLst);
			leaf.setColor(Leaf.GRAY);
			if(leaf.hasFather()) {leaf.getFather().update(this.ectOrLst);}
			return true;
		}
		return false;
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree#resetColor()
	 */
	@Override
	protected int resetColor() {
		return Leaf.GRAY;

	}

}