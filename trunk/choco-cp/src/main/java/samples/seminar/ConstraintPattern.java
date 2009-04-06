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
package samples.seminar;

import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class ConstraintPattern extends AbstractLargeIntSConstraint {

    public ConstraintPattern(IntDomainVar[] vars) {
        super(vars);
    }


    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    public String pretty() {
        return null;
    }


    /**
     * check wether the tuple satisfy the constraint
     * @param tuple value
     * @return true if satisfy
     */
    public boolean isSatisfied(int[] tuple) {
        return false;
    }


    /**
     * <i>Propagation:</i>
     * Propagating the constraint until local consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */

    public void propagate() throws ContradictionException {
        //Elementary method to implement
    }

    
    /**
     * <i>Propagation:</i>
     * Propagating the constraint for the very first time until local
     * consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */

    public void awake() throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on instantiation: full constraint re-propagation.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnInst(int varIdx) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */

    public void awakeOnInf(int varIdx) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnSup(int varIdx) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on one value removal: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnBounds(int varIdx) throws ContradictionException {
         //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on one value removal: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */

    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }

    /**
     * Default propagation on one value removal: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @param deltaDomain iterator over remove values.
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


}
