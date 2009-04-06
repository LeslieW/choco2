package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 14 janv. 2008
 * Time: 10:38:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLargeSetConstraint extends AbstractSetConstraint {

	/**
	 * The set variables representing the scope of the constraint.
	 */
	public SetVar[] vars;

	/**
	 * The index of the constraint among all listeners of its first variable.
	 */
	public int[] cIndices;

	/**
	 * constructor: allocates the data util for n variables
	 *
	 * @param n the number of variables involved in the constraint
	 * @deprecated use AbstractLargeIntConstraint(IntDomainVar[] vars) instead
	 */
	public AbstractLargeSetConstraint(int n) {
		vars = new SetVar[n];
		cIndices = new int[n];
	}

	public AbstractLargeSetConstraint(SetVar[] vars) {
		this.vars = new SetVar[vars.length];
		System.arraycopy(vars, 0, this.vars, 0, vars.length);
		cIndices = new int[vars.length];
	}


	/**
	 * Let v be the i-th var of c, records that c is the n-th constraint involving v.
	 */
	public void setConstraintIndex(int i, int val) {
		if (i >= 0 && i < vars.length) {
			cIndices[i] = val;
		} else throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
	}

	/**
	 * Returns the index of the constraint in the specified variable.
	 */
	public int getConstraintIdx(int idx) {
		if (idx >= 0 && idx < vars.length)
			return cIndices[idx];
		else
			return -1;
	}

	/**
	 * Checks if all the variables are instantiated.
	 */
	public boolean isCompletelyInstantiated() {
		for (SetVar var : vars) {
			if (!var.isInstantiated()) return false;
		}
		return true;
	}

	/**
	 * Returns the number of variables.
	 */
	public int getNbVars() {
		return vars.length;
	}

	/**
	 * Gets the specified variable.
	 */
	public Var getVar(int i) {
		if (i >= 0 && i < vars.length)
			return vars[i];
		else
			return null;
	}

	public void setVar(int i, Var v) {
		if (v instanceof SetVar) {
			if (i >= 0 && i < vars.length)
				vars[i] = (SetVar) v;
			else
				throw new Error("BUG in CSP network management: too large index for setVar");
		} else {
			throw new Error("BUG in CSP network management: wrong type of Var for setVar");
		}
	}

	/**
	 * Gets the <code>i</code>th search valued variable.
	 */
	public SetVar getSetVar(int i) {
		if (i >= 0 && i < vars.length)
			return vars[i];
		else
			return null;
	}

	public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
		int j = i;
		for (int k = 0; k < getNbVars(); k++) {
			j++;
			int cidx = root.connectVar(vars[k], j, dynamicAddition);
			setConstraintIndex(k, cidx);
		}
		return j;
	}

}
