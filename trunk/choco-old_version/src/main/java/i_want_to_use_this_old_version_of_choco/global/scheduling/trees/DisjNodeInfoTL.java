/**
 * created the 4 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNode;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.InternalNode;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.Leaf;

/**
 * The information container used for {@link DisjTreeTL}.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class DisjNodeInfoTL extends DisjNodeInfoT {

	public DisjNodeInfoTL() {
		super(new int[6],null);
	}



	/**
	 * @see DisjNodeInfoT#reset(InternalNode, boolean)
	 */
	@Override
	public void reset(final InternalNode node,final boolean ectOrLst) {
		super.update(node, ectOrLst);
		copyThetaInLambda();
		setResponsibleTask(-1);
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjNodeInfoT#reset(ITasksSet, i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.Leaf, boolean)
	 */
	@Override
	public void reset(final ITasksSet tasks,final Leaf leaf, final boolean ectOrLst) {
		super.update(tasks, leaf, ectOrLst);
		copyThetaInLambda();
		setResponsibleTask(-1);
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjNodeInfoT#toDotCells(java.lang.StringBuilder)
	 */
	@Override
	public void toDotCells(final StringBuilder buffer) {
		super.toDotCells(buffer);
		buffer.append('|');
		toDotCell(buffer, "lambda", intValues[2], intValues[3]);
		buffer.append('|');
		toDotCell(buffer, "resp.", intValues[4], intValues[5]);
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjNodeInfoT#update(i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.InternalNode, boolean)
	 */
	@Override
	public void update(final InternalNode node,final boolean ectOrLst) {
		super.update(node, ectOrLst);
		if(ectOrLst) {updateGrayECT(node.getLeftChild(), node.getRightChild());}
		else {updateGrayLST(node.getLeftChild(), node.getRightChild());}
		updateGrayConsumption(node.getLeftChild(), node.getRightChild());
	}



	/**
	 * update the lambda consumption for an internal node
	 * @param left the left child of the internal node
	 * @param right the right child of the internal node
	 */
	private void updateGrayConsumption(final AbstractNode left,final AbstractNode right) {
		final int l=left.infos.getIntValue(3)+right.infos.getIntValue(1);
		final int r=left.infos.getIntValue(1)+right.infos.getIntValue(3);
		if(l>=r) {
			update(3, l, left.infos.getIntValue(5));
		}else {
			update(3, r, right.infos.getIntValue(5));
		}
	}

	/**
	 * méthod used to update a field and the responsible task for this field
	 * @param index the field index
	 * @param value the new value of the field
	 * @param task the responsible task for the field
	 */
	private final void update(final int index,final int value,final int task) {
		intValues[index]=value;
		intValues[index+2]=task;
	}
	private final void updateGrayECT(final int value,final int task) {
		this.update(2, value, task);
	}
	/**
	 * update the lambda ECT for an internal node
	 * @param left the left child of the internal node
	 * @param right the right child of the internal node
	 */
	private void updateGrayECT(final AbstractNode left,final AbstractNode right) {
		final int l=right.infos.getIntValue(2);
		final int m=left.infos.getIntValue(0)+right.infos.getIntValue(3);
		final int r=left.infos.getIntValue(2)+right.infos.getIntValue(1);
		if(l>=m && l>= r) {
			updateGrayECT(l,right.infos.getIntValue(4));
		}else if(m>=r) {
			updateGrayECT(m,right.infos.getIntValue(5));
		}else {
			updateGrayECT(r,left.infos.getIntValue(4));
		}
	}

	/**
	 * update the lambda LST for an internal node
	 * @param left the left child of the internal node
	 * @param right the right child of the internal node
	 */
	private void updateGrayLST(final AbstractNode left,final AbstractNode right) {
		final int l=left.infos.getIntValue(2);
		final int m=right.infos.getIntValue(0)-left.infos.getIntValue(3);
		final int r=right.infos.getIntValue(2)-left.infos.getIntValue(1);
		if(l<=m && l<= r) {
			updateGrayECT(l,left.infos.getIntValue(4));
		}else if(m<=r) {
			updateGrayECT(m, left.infos.getIntValue(5));
		}else {
			updateGrayECT(r,right.infos.getIntValue(4));
		}
	}


	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjNodeInfoT#update(ITasksSet, i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.Leaf, boolean)
	 */
	@Override
	public void update(final ITasksSet tasks,final Leaf leaf, final boolean ectOrLst) {
		if(leaf.getColor()==Leaf.WHITE) {
			this.reset(0, true, ectOrLst);
			setResponsibleTask(leaf.task);
		}if(leaf.getColor()==Leaf.GRAY) {
			this.reset(2,true,ectOrLst);
			setResponsibleTask(-1);
		}
	}



}
