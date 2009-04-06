/**
 * created the 27 sept. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractThetaLambdaTree;

/**
 * The Theta tree is used for Edge Finding (EF).
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class DisjTreeTL extends AbstractThetaLambdaTree{




	/**
	 * @param tasks
	 * @param ectOrLst
	 */
	public DisjTreeTL(ITasksSet tasks, boolean ectOrLst) {
		super(tasks, ectOrLst);
	}

	@Override
	protected AbstractNodeInfo createEmptyNodeInfo() {
		return new DisjNodeInfoTL();
	}

	/**
	 * get the theta time of the root node
	 * @return the time for this type of tree
	 */
	public int getTime() {
		return root.infos.getIntValue(0);
	}

	/**
	 * get the lambda time of the root node
	 * @return the time for this type of tree
	 */

	public int getGrayTime() {
		return root.infos.getIntValue(2);
	}
	/**
	 * get responsible task for {@link DisjTreeTL#getGrayTime()}
	 * @return the responsible task
	 */

	public final int getGrayResponsibleTask() {
		return root.infos.getIntValue(4);
	}






}
