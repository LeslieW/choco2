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


package choco.cp.solver.constraints.integer;

import choco.cp.solver.constraints.integer.intlincomb.*;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint Sigma (ai Xi) <=/>=/= C,
 * with Xi variables, ai and C constants.
 */
public final class IntLinComb extends AbstractLargeIntSConstraint {
    /**
     * Constant, to be assigned to <code>op</code>,
     * representing linear equalities.
     */
    public static final int EQ = 0;

    /**
     * Constant, to be assigned to <code>op</code>,
     * representing linear inequalities.
     */
    public static final int GEQ = 1;

    /**
     * Constant, to be assigned to <code>op</code>,
     * representing linear disequalities.
     */
    public static final int NEQ = 2;

    /**
     * Constant, to be assigned to <code>op</code>,
     * representing linear inequalities.
     * Only used vby BoolIntLinComb
     */
    public static final int LEQ = 3;

    /**
     * Field representing the number of variables
     * with positive coeffficients in the linear combination.
     */
    protected final int nbPosVars;

    /**
     * Filter based on the operator
     */
    protected final IntLinCombOp intlincomb;

    /**
     * Constructs the constraint with the specified variables and constant.
     * Use the Model.createIntLinComb API instead of this constructor.
     * This constructor assumes that there are no null coefficient
     * and that the positive coefficients come before the negative ones.
     *
     * @param lvars       the variables of the constraint
     * @param lcoeffs     the constant coefficients
     * @param nbPositive  number of positive coefficients
     * @param c           the constant value of the constraint (the value the linear
     *                    expression must equal)
     * @param linOperator the operator to use (equality, inequality...)
     */
    public IntLinComb(final IntDomainVar[] lvars, final int[] lcoeffs,
                      final int nbPositive, final int c, final int linOperator) {
        // create the appropriate data structure
        super(lvars);
        this.nbPosVars = nbPositive;
        switch (linOperator) {
            case EQ:
                intlincomb = new IntLinCombEQ(lcoeffs, nbPositive, c, lvars, this);
                break;
            case NEQ:
                intlincomb = new IntLinCombNEQ(lcoeffs, nbPositive, c, lvars, this);
                break;
            case GEQ:
                intlincomb = new IntLinCombGEQ(lcoeffs, nbPositive, c, lvars, this);
                break;
            case LEQ:
                intlincomb = new IntLinCombLEQ(lcoeffs, nbPositive, c, lvars, this);
                break;
            default:
                intlincomb = null;
        }
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
    }

    /**
     * Launchs the filtering algorithm.
     *
     * @throws ContradictionException if a domain empties or a contradiction is
     *                                infered
     */
    public void propagate() throws ContradictionException {
        intlincomb.filter(true, 2);
    }


    /**
     * Propagation whenever the lower bound of a variable is modified.
     *
     * @param idx the index of the modified variable
     * @throws ContradictionException if a domain empties or a contradiction is
     *                                infered
     */
    public void awakeOnInf(final int idx) throws ContradictionException {
        if (idx < nbPosVars) {
            intlincomb.filter(true, 1);
        } else {
            intlincomb.filter(false, 1);
        }
    }

    /**
     * Propagation whenever the upper bound of a variable is modified.
     *
     * @param idx the index of the modified variable
     * @throws ContradictionException if a domain empties or a contradiction is
     *                                infered
     */
    public void awakeOnSup(final int idx) throws ContradictionException {
        if (idx < nbPosVars) {
            intlincomb.filter(false, 1);
        } else {
            intlincomb.filter(true, 1);
        }
    }

    /**
     * Propagation whenever a variable is instantiated.
     *
     * @param idx the index of the modified variable
     * @throws ContradictionException if a domain empties or a contradiction is
     *                                infered
     */
    public void awakeOnInst(final int idx) throws ContradictionException {
        propagate();
    }

    /**
     * Propagation whenever a value is removed from the variable domain.
     *
     * @param idx the index of the modified variable
     * @param x   the removed value
     * @throws ContradictionException if a domain empties or a contradiction is
     *                                infered
     */
    public void awakeOnRem(final int idx, final int x)
            throws ContradictionException {
    }

    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
    }


    /**
     * Checks if the constraint is satisfied when all variables are instantiated.
     *
     * @return true if the constraint is satisfied
     */
	public boolean isSatisfied(int[] tuple) {
		return intlincomb.isSatisfied(tuple);
	}

    /**
     * Get the opposite constraint
     *
     * @return the opposite constraint  @param solver
     */
    @Override
    public AbstractSConstraint opposite(final Solver solver) {
        return intlincomb.opposite(solver);
    }

    @Override
    public String pretty() {
        return intlincomb.pretty();
    }
}
