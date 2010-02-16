/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver.constraints.set;

import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractUnSetSConstraint extends AbstractLargeSetSConstraint {

    /**
     * The unique variable of the constraint.
     */

    public SetVar v0;


    /**
     * The index of the constraint among all listeners of its first (and unique)
     * variable.
     */

    public int cIdx0;

    public AbstractUnSetSConstraint(SetVar v0) {
        super(new SetVar[]{v0});
        this.v0 = v0;
    }

    /**
     * Returns the number of variables: 1 for an unIntConstraint.
     */

    public int getNbVars() {
        return (1);
    }


    /**
     * Returns the variable number <code>i</code>. Here, <code>i</code>
     * should be 0.
     */

    public SetVar getVar(int i) {
        if (i == 0) {
            return v0;
        } else {
            return null;
        }
    }


    /**
     * Returns the index of this listeners in the variable <code>idx</code>.
     *
     * @param idx Index of the variable.
     */

    public int getConstraintIdx(int idx) {
        if (idx == 0) {
            return cIdx0;
        } else {
            return -1;
        }
    }


    /**
     * Let v be the i-th var of c, records that c is the n-th constraint involving v.
     */

    public void setConstraintIndex(int i, int val) {
        super.setConstraintIndex(i, val);
        if (i == 0) {
            cIdx0 = val;
        }
    }


    /**
     * Checks if all the variables of the constraint are instantiated.
     */

    @Override
    public boolean isCompletelyInstantiated() {
        return v0.isInstantiated();
    }

}
