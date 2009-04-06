/**
 * created the 27 sept. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling.trees;
import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNodeInfo;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractThetaTree;

/**
 * The Theta tree is used in the rules NF/NL, detectable precedence and overload checking of the disjunctive constraints.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class DisjTreeT extends AbstractThetaTree {



	/**
	 * @param tasks
	 * @param ectOrLst
	 */
	public DisjTreeT(final ITasksSet tasks, boolean ectOrLst) {
		super(tasks, ectOrLst);
	}


	@Override
	protected AbstractNodeInfo createEmptyNodeInfo() {
		return new DisjNodeInfoT();
	}
	/**
	 * get the theta time of the root node
	 * @return the time for this type of tree
	 */
	public int getTime() {
		return root.infos.getIntValue(0);
	}

	/**
	 * get the theta consumption of the root node
	 * @return the consumption for this type of tree
	 */
	public int getConsumption() {
		return root.infos.getIntValue(1);

	}



}
