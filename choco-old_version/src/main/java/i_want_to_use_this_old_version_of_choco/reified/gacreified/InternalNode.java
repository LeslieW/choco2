package i_want_to_use_this_old_version_of_choco.reified.gacreified;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 8 avr. 2008
 * Time: 14:10:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class InternalNode extends Predicat {

	/**
	 * reference to the right branch of the tree stating the predicat
	 */
	protected Predicat[] subtrees;

	/**
	 * reference to the left branch of the tree stating the predicat
	 */

	public InternalNode(Predicat[] subt) {
		pb = subt[0].getProblem();		
		subtrees = subt;
	}

	/**
	 * Compute the set of variable involved in this predicat
	 * @return
	 */
	protected IntDomainVar[] getScope() {
		IntDomainVar[] vars = union(subtrees[0].getScope(),subtrees[1].getScope());
		for (int i = 2; i < subtrees.length; i++) {
			vars = union(vars, subtrees[i].getScope());
		}
		return vars;
	}

	/**
	 * set the indexes of each variables in the leaves of the tree
	 * @param vs
	 */
	protected void setIndexes(IntDomainVar[] vs) {
		for (int i = 0; i < subtrees.length; i++) {
			subtrees[i].setIndexes(vs);
		}
	}

}
