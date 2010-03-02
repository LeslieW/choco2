/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.memory.structure;

import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Iterator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 21 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public abstract class APartiallyStoredCstrList<C extends SConstraint> {

    protected final PartiallyStoredVector<C> elements;

    protected final PartiallyStoredIntVector indices;

    protected APartiallyStoredCstrList(IEnvironment env) {
        elements = env.makePartiallyStoredVector();
        indices = env.makePartiallyStoredIntVector();
    }

    /**
     * Retrieve the C element i
     * @param i index of the constraint
     * @return the ith constraint
     */
    public final C getConstraint(final int i) {
		return elements.get(i);
	}

    /**
	 * Returns the index of the constraint.
	 * @param constraintIndex the index of the constraint
	 * @return the index
	 */
	public final int getConstraintIndex(final int constraintIndex) {
		return indices.get(constraintIndex);
	}

    /**
	 * Returns the number of constraints
	 * @return the number of constraints
	 */
	public final int getNbConstraints() {
		return elements.size();
	}

    /**
	 * Access the data structure storing constraints
	 * @return the backtrackable structure containing the constraints
	 */
	public final PartiallyStoredVector<C> getConstraintVector() {
		return elements;
	}

    /**
	 * Access the data structure storing indices associated to constraints .
	 * @return the indices associated to this variable in each constraint
	 */
	public final PartiallyStoredIntVector getIndexVector() {
		return indices;
	}

    /**
	 * Removes (permanently) a constraint from the list of constraints
	 * connected to the variable.
	 * @param c the constraint that should be removed from the list this variable
	 * maintains.
     * @return index of the deleted constraint
	 */
	public int eraseConstraint(final SConstraint c) {
		int idx = elements.remove(c);
		indices.remove(idx);
        return idx;
	}

    /**
	 * Adds a new constraints on the stack of constraints
	 * the addition can be dynamic (undone upon backtracking) or not.
	 * @param c the constraint to add
	 * @param varIdx the variable index accrding to the added constraint
	 * @param dynamicAddition states if the addition is definitic (cut) or
	 * subject to backtracking (standard constraint)
	 * @return the index affected to the constraint according to this variable
	 */
	@SuppressWarnings({"unchecked"})
    public int addConstraint(final SConstraint c, final int varIdx, final boolean dynamicAddition) {
		int constraintIdx;
		if (dynamicAddition) {
			constraintIdx = elements.add((C)c);
			indices.add(varIdx);
		} else {
			constraintIdx = elements.staticAdd((C)c);
			indices.staticAdd(varIdx);
		}
		return constraintIdx;
	}

    /**
	 * This methods should be used if one want to access the different constraints stored.
	 *
	 * Indeed, since indices are not always
	 * consecutive, it is the only simple way to achieve this.
	 *
	 * Warning ! this iterator should not be used to remove elements.
	 * The <code>remove</code> method throws an
	 * <code>UnsupportedOperationException</code>.
	 *
	 * @return an iterator over all constraints involving this variable
	 */
	public final Iterator<SConstraint> getConstraintsIterator() {
		return elements.getIterator();
	}
}
