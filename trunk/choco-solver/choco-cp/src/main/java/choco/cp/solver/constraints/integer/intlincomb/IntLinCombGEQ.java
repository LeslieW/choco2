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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.integer.intlincomb;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 11 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class IntLinCombGEQ extends IntLinCombOp {


    public IntLinCombGEQ(final int[] coeffs, final int nbPosVars, final int cste, final IntDomainVar[] vars, final AbstractSConstraint constraint) {
        super(coeffs, nbPosVars, cste, vars, constraint);
    }

    /**
     * Checks if the constraint is entailed.
     *
     * @return Boolean.TRUE if the constraint is satisfied, Boolean.FALSE if it
     *         is violated, and null if the filtering algorithm cannot infer yet.
     */
    public Boolean isEntailed() {
        if (coeffPolicy.computeUpperBound() < 0) {
            return Boolean.FALSE;
        } else if (coeffPolicy.computeLowerBound() >= 0) {
            return Boolean.TRUE;
        } else {
            return null;
        }
    }

    /**
     * Checks if the constraint is satisfied when all variables are instantiated.
     *
     * @return true if the constraint is satisfied
     */
    public boolean isSatisfied(int[] tuple) {
       return (compute(tuple) >= 0);
    }

    /**
     * Checks a new lower bound.
     *
     * @return true if filtering has been infered
     * @throws choco.kernel.solver.ContradictionException
     *          if a domain empties or a contradiction is
     *          infered
     */
    public boolean filterOnImprovedLowerBound()
            throws ContradictionException {
        return false;
    }

    /**
     * Checks a new upper bound.
     *
     * @return true if filtering has been infered
     * @throws ContradictionException if a domain empties or a contradiction is
     *                                infered
     */
    public boolean filterOnImprovedUpperBound()
            throws ContradictionException {
        int myub = coeffPolicy.computeUpperBound();
        return propagateNewUpperBound(myub);
    }

    /**
     * Tests if the constraint is consistent
     * with respect to the current state of domains.
     *
     * @return true iff the constraint is bound consistent
     *         (weaker than arc consistent)
     */
    public boolean isConsistent() {
        //    int ub = computeUpperBound();
        //    int lb = computeLowerBound();
        //    int nbVars = getNbVars();
        return hasConsistentUpperBound();
    }

    /**
     * Computes the opposite of this constraint.
     *
     * @return a constraint with the opposite semantic  @param solver
     */
    public AbstractSConstraint opposite(Solver solver) {
        IntExp term = solver.scalar(coeffs, vars);
        return (AbstractSConstraint) solver.lt(term, -cste);
    }


    @Override
    protected String getOperator() {
        return " >= ";
    }
}