/**
 * created the 5 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.InternalNode;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.Leaf;



/**
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class CumNodeInfoT extends AbstractNodeInfo {

	/**
	 *
	 */
	public CumNodeInfoT() {
		super(new int[1],new long[2]);
	}

	public void setCapacity(final int capacity) {
		intValues[0]=capacity;
	}
	@Override
	public void reset(final ITasksSet tasks,final Leaf leaf, final boolean ectOrLst) {
		this.reset(0, false, ectOrLst);
	}

	@Override
	public void reset(final InternalNode node,final boolean ectOrLst) {
		this.reset(0, false, ectOrLst);

	}

	@Override
	public void toDotCells(final StringBuilder buffer) {
		toDotCell(buffer, "theta", longValues[0],longValues[1]);

	}

	@Override
	public void update(final ITasksSet tasks,final Leaf leaf, final boolean ectOrLst) {
		this.longValues[0]=intValues[0]*tasks.getEST(leaf.task)+tasks.getConsumption(leaf.task);
		this.longValues[1]=tasks.getConsumption(leaf.task);
	}


	@Override
	public void update(final InternalNode node,final boolean ectOrLst) {
		if(ectOrLst) {updateCECT(node);}
		else {updateCLST(node);}
		updateLongSum(node, 1);

	}
	/**
	 * update Time for ECT tree.
	 * @param node the node to update
	 */
	private void updateCECT(final InternalNode node) {
		final AbstractNodeInfo left=node.getLeftChild().infos;
		final AbstractNodeInfo right=node.getRightChild().infos;
		this.longValues[0]=Math.max(right.getLongValue(0), left.getLongValue(0)+right.getLongValue(1));
	}

	/**
	 * update Time for LST tree.
	 * @param node the node to update
	 */
	private void updateCLST(final InternalNode node) {
		//TODO arbre symetrique pour la cumulative
		System.err.println("fail : not yet implemented");
		System.exit(1);
	}
}
