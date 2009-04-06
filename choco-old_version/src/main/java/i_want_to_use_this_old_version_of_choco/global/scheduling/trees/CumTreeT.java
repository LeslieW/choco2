/**
 * created the 5 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractThetaTree;

/**
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class CumTreeT extends AbstractThetaTree {

	private final int capacity;



	/**
	 * @param ectOrLst
	 */
	public CumTreeT(ITasksSet tasks,boolean ectOrLst, int capacity) {
		super(tasks, ectOrLst);
		this.capacity=capacity;
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree#createEmptyNodeInfo()
	 */
	@Override
	protected AbstractNodeInfo createEmptyNodeInfo() {
		final CumNodeInfoT infos=new CumNodeInfoT();
		infos.setCapacity(capacity);
		return infos;
	}

	public final long getEnergy() {
		return root.infos.getLongValue(0);
	}

}
