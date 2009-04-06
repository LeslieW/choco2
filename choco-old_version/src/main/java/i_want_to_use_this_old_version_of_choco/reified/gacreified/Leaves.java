package i_want_to_use_this_old_version_of_choco.reified.gacreified;

import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.RealConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetConstraint;

/**
 * Represent the leaves of the OR/AND tree as a set of Propagators
 * that can check a tuple using isSatisfied(int[] tuple)
 **/
public abstract class Leaves extends Predicat {

	/**
	 * The constraints constituting the leaves of the tree
	 */
	protected Propagator[] leaves;

	/**
	 * this table provide for each leave
	 * the indexes of their variables in the
	 * scope of the predicat
	 */
	protected int[][] scopes;

	/**
	 * Temporary data structure to store the projection
	 * of the tuple to be checked and avoid reallocating those tables
	 * again and again when checking the tuples.
	 */
	protected int[][] temptuples; // allocate memory for the tuples

	public Leaves(Propagator p) {
		pb = p.getProblem();
		leaves = new Propagator[1];
		leaves[0] = p;
		catchErrorsRealSets();
	}

	public Leaves(Propagator p1, Propagator p2) {
		pb = p1.getProblem();
		leaves = new Propagator[2];
		leaves[0] = p1;
		leaves[1] = p2;
		catchErrorsRealSets();
	}

	public Leaves(Propagator[] ps) {
		pb = ps[0].getProblem();
		leaves = ps;
		catchErrorsRealSets();
	}

	public void catchErrorsRealSets() {
		for (int i = 0; i < leaves.length; i++) {
			if (leaves[i] instanceof SetConstraint ||
				leaves[i] instanceof RealConstraint) {
				throw new Error("Reified constraint can not be posted on Set or Real Constraints yet");
			}
		}
	}


	public int[] getTupleForLeave(int j, int[] tuple) {
		for (int i = 0; i < temptuples[j].length; i++) {
			temptuples[j][i] = tuple[scopes[j][i]];
		}
		return temptuples[j];
	}

	/**
	 * Initialize the scopes and temptuples matrices
	 * @param vs
	 */
	protected void setIndexes(IntDomainVar[] vs) {
		scopes = new int[leaves.length][];
		temptuples = new int[leaves.length][];

		for (int i = 0; i < leaves.length; i++) {
			scopes[i] = new int[leaves[i].getNbVars()];
			temptuples[i] = new int[leaves[i].getNbVars()];

			for (int j = 0; j < leaves[i].getNbVars(); j++) {
				IntDomainVar v = (IntDomainVar) leaves[i].getVar(j);
				int idx = findIndex(vs, v); // return the index of v in vs
				assert (idx != -1);
				scopes[i][j] = idx;
			}
		}
	}

	/**
	 * return the index of v in tab
	 *
	 * @param tab
	 * @param v
	 * @return
	 */
	public int findIndex(IntDomainVar[] tab, IntDomainVar v) {
		for (int i = 0; i < tab.length; i++) {
			if (v == tab[i]) return i;
		}
		return -1;
	}

	protected IntDomainVar[] getScope() {
		if (leaves.length >= 2) {
			IntDomainVar[] vars = union(getScopeOfLeave(0), getScopeOfLeave(1));
			for (int i = 2; i < leaves.length; i++) {
				vars = union(vars, getScopeOfLeave(i));
			}
			return vars;
		} else return getScopeOfLeave(0);
	}	

	public IntDomainVar[] getScopeOfLeave(int k) {
		IntDomainVar[] lscope = new IntDomainVar[leaves[k].getNbVars()];
		for (int i = 0; i < lscope.length; i++) {
			lscope[i] = (IntDomainVar) leaves[k].getVar(i);
		}
		return lscope;
	}

}
