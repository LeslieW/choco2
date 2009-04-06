/**
 * created the 4 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;

/**
 * This class complete the Theta-tree structure to apply edge finding rules.
 *  all applicable activities i will be also included in the tree, but as gray nodes. A gray node represents an activity i which is not really in the set Theta.
 * However, we are curious what would happen with ECT_{Theta} if we are allowed to include <b>one</b> of the gray activities into the set Theta.
 * It uses additionnal fields to compute gray values.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public  abstract class AbstractThetaLambdaTree extends AbstractTree {



	/**
	 * @param tasks
	 * @param ectOrLst
	 */
	public AbstractThetaLambdaTree(ITasksSet tasks, boolean ectOrLst) {
		super(tasks, ectOrLst);
	}


	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree#resetColor()
	 */
	@Override
	protected int resetColor() {
		return Leaf.WHITE;

	}

	/**
	 * remove the task of theta subset and insert it in lambda subset
	 * @param task the task to remove
	 * @return <code>true</code> if successfull
	 */
	public final boolean rmThetaAndInsertLambda(final int task) {
		final Leaf leaf=this.getLeaf(task);
		if(leaf.getColor()== Leaf.WHITE) {
			leaf.infos.update(tasks,leaf, this.ectOrLst);
			leaf.setColor(Leaf.GRAY);
			if(leaf.hasFather()) {leaf.getFather().update(this.ectOrLst);}
			return true;
		}
		return false;
	}

	/**
	 * remove the task of lambda subset. you must have already called {@link AbstractThetaLambdaTree#rmThetaAndInsertLambda(int)} on the task.
	 * @param task the task to remove
	 * @return <code>true</code> if successfull
	 */
	public final boolean rmLambda(final int task) {
		final Leaf leaf=this.getLeaf(task);
		if(leaf.getColor()== Leaf.GRAY) {
			leaf.infos.update(tasks,leaf, this.ectOrLst);
			leaf.setColor(Leaf.BLACK);
			if(leaf.hasFather()) {leaf.getFather().update(this.ectOrLst);}
			return true;
		}
		return false;
	}
}
