/**
 * created the 4 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNode;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.InternalNode;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.Leaf;

/**
 * The information container used for {@link DisjTreeT}.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class DisjNodeInfoT extends AbstractNodeInfo {


	/**
	 * @param intValues
	 * @param longValues
	 */
	public DisjNodeInfoT(int[] intValues, long[] longValues) {
		super(intValues, longValues);
	}

	public DisjNodeInfoT() {
		super(new int[2],null);
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo#reset(ITasksSet, i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.Leaf, boolean)
	 */
	@Override
	public void reset(final ITasksSet tasks,final Leaf leaf, final boolean ectOrLst) {
		this.reset(0, true, ectOrLst);
	}

	@Override
	public void reset(final InternalNode node,final boolean ectOrLst) {
		this.reset(0, true, ectOrLst);
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo#toDotCells(java.lang.StringBuilder)
	 */
	@Override
	public void toDotCells(final StringBuilder buffer) {
		toDotCell(buffer, "theta", intValues[0], intValues[1]);
	}

	@Override
	public void update(final ITasksSet tasks,final Leaf leaf, final boolean ectOrLst) {
		intValues[0]= ectOrLst ? tasks.getEST(leaf.task)+tasks.getProcessingTime(leaf.task) : tasks.getLST(leaf.task);
		intValues[1]=tasks.getProcessingTime(leaf.task);
	}


	@Override
	public void update(final InternalNode node,final boolean ectOrLst) {
		if(ectOrLst) {updateECT(node.getLeftChild(), node.getRightChild());}
		else {updateLST(node.getLeftChild(), node.getRightChild());}
		updateIntSum(node, 1);
	}

	/**
	 * update the lastest starting time for an internal node
	 * @param left the left child
	 * @param right the right child
	 */
	private void updateLST(final AbstractNode left,final AbstractNode right) {
		intValues[0]=Math.min(left.infos.getIntValue(0),right.infos.getIntValue(0)-left.infos.getIntValue(1));
	}

	/**
	 * update the earliest completion time for an internal node
	 * @param left the left child
	 * @param right the right child
	 */
	private void updateECT(final AbstractNode left,final AbstractNode right) {
		intValues[0]=Math.max(right.infos.getIntValue(0),left.infos.getIntValue(0)+right.infos.getIntValue(1));
	}
}
