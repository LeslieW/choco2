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

public abstract class AbstractBinSetSConstraint extends AbstractLargeSetSConstraint {

    /**
     * The variables of the constraint.
     */

    public final SetVar v0, v1;

    /**
     * The index of the constraint among all listeners of its variables.
     */

    public int cIdx0, cIdx1;

    protected AbstractBinSetSConstraint(SetVar v0, SetVar v1) {
        super(new SetVar[]{v0, v1});
        this.v0 = v0;
        this.v1 = v1;
    }

    /**
     * Let v be the i-th var of c, records that c is the n-th constraint involving v.
     */
    @Override
    public void setConstraintIndex(int i, int val) {
        super.setConstraintIndex(i, val);
        switch (i) {
            case 0:
                cIdx0 = val;
                break;
            case 1:
                cIdx1 = val;
                break;
        }
    }


    /**
     * Returns the index of this constraint for the specified variables.
     */
    @Override
    public int getConstraintIdx(int i) {
        switch (i) {
            case 0:
                return cIdx0;
            case 1:
                return cIdx1;
            default:
                return -1;
        }
    }


    /**
     * Checks if all the variables are instantiated.
     */

    @Override
    public boolean isCompletelyInstantiated() {
        return (v0.isInstantiated() && v1.isInstantiated());
    }


    /**
     * Returns the number of varibles.
     */
    @Override
    public int getNbVars() {
        return 2;
    }

    /**
     * Returns the <code>i</code>th variable.
     */
    @Override
    public SetVar getVar(int i) {
        switch (i) {
            case 0:
                return v0;
            case 1:
                return v1;
            default:
                return null;
        }
    }
}
