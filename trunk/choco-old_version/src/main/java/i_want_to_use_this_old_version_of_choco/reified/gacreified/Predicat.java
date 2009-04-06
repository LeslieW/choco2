package i_want_to_use_this_old_version_of_choco.reified.gacreified;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.TuplesTest;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * A class to represent a reified predicat of constraints as a tree
 * where the internal nodes are AND or OR combinations and the leaves
 * are made of propagators or their negation
 */
public abstract class Predicat extends TuplesTest implements Constraint {

	/**
	 * A reference to the problem to which this predicat belong
	 */
	protected AbstractProblem pb;

	/**
	 * The scope of the predicat
	 */
	protected IntDomainVar[] vars;

	/**
	 * compute the scope of this predicat as
	 * the union of the scopes of all the leaves
	 * and set the indexes of each variable of each leave
	 * regarding its position in the scope "vars"
	 * This is called once when posting the propagator
	 */
	public void setScope() {
	   if(vars == null) {
		   vars = getScope();
		   setIndexes(vars);
	   }
    }

	public IntDomainVar[] getVars() {
		return vars;
	}

	public int getNbVars() {
		return vars.length;
	}

	public Var getVar(int i) {
		return vars[i];
	}

	public void setVar(int i, Var v) {
		vars[i] = (IntDomainVar) v;
	}

	public AbstractProblem getProblem() {
		return pb;
	}

	public void setProblem(AbstractProblem problem) {
		this.pb = problem;
	}

	/**
	 * Compute the set of variable involved in this predicat
	 * @return
	 */
	protected abstract IntDomainVar[] getScope();

	
	/**
	 * set the indexes of each variables in the leaves of the tree
	 * @param vs
	 */	
	protected abstract void setIndexes(IntDomainVar[] vs);

	/**
	 * retutn the negation of this predicat
	 * @return
	 */
	public abstract Predicat getOpposite();
	
	/**
	 * Return a table being the union of the two tables given in argument
	 * @param t1
	 * @param t2
	 * @return
	 */
	public IntDomainVar[] union(IntDomainVar[] t1, IntDomainVar[] t2) {
		HashSet<IntDomainVar> unionset = new HashSet<IntDomainVar>();
		for (int i = 0; i < t1.length; i++) {
			if (!unionset.contains(t1[i]))
				unionset.add(t1[i]);
		}
		for (int i = 0; i < t2.length; i++) {
			if (!unionset.contains(t2[i]))
				unionset.add(t2[i]);
		}
		IntDomainVar[] uniontab = new IntDomainVar[unionset.size()];
		unionset.toArray(uniontab);
		return uniontab;
	}

	public boolean isSatisfied() {
		return false;
	}


	public AbstractConstraint opposite() {
		throw new Error("opposite should not be called on a predicat");
	}

	//todo
	public Object clone() throws CloneNotSupportedException {
		return null;
	}

	//todo hum isn'it NP-Complete to tell ? equivalence between two SAT formulas for exemple
	public boolean isEquivalentTo(Constraint compareTo) {
		return false;
	}


	public int getVarIdxInOpposite(int i) {
		throw new Error("getVarIdxInOpposite should not be called on a predicat");
	}

	public void setConstraintIndex(int i, int idx) {
		throw new Error("setConstraintIdx should not be called on a predicat");
	}

	public int getConstraintIdx(int idx) {
		throw new Error("getConstraintIdx should not be called on a predicat");
	}

    public int[] copy(int[] tab) {
		int[] tab2 = new int[tab.length];
		System.arraycopy(tab,0,tab2,0,tab.length);
		return tab2;
	}

	/**
	 * Generate the list of tuples corresponding to this predicat
	 * @return
	 */
	public List<int[]> getTuples() {
		setScope();
		LinkedList<int[]> ltuples = new LinkedList<int[]>();
		int size = vars.length;
		int[] currentSupport = new int[size];
		for (int i = 0; i < size; i++) {
			currentSupport[i] = vars[i].getInf();
		}
		if (isConsistent(currentSupport)) {
			ltuples.add(copy(currentSupport));
		}
		int k = 0;
		while (k < vars.length) {
			if (!vars[k].getDomain().hasNextValue(currentSupport[k])) {
				currentSupport[k] = vars[k].getInf();
				k++;
			} else {
				currentSupport[k] = vars[k].getDomain().getNextValue(currentSupport[k]);
				if (isConsistent(currentSupport)) {
					ltuples.add(copy(currentSupport));
				}
				k = 0;
			}
		}
		return ltuples;
	}

}
