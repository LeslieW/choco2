/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
package choco.cp.solver.constraints.reified;

import choco.cp.solver.constraints.integer.channeling.ReifiedBinXor;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 20 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class ReifiedFactory {

    private ReifiedFactory() {
    }

    /**
     * Following the type of constraints, create a reified constraint.
     *
     * @param bool    boolean variable for reification
     * @param cons    a constraint
     * @param solver    the solver
     * @return a SConstraint
     * @throws UnsupportedOperationException when an expression constraint is in the scope.
     */
    public static SConstraint builder(IntDomainVar bool, SConstraint cons, Solver s) {
        return builder(bool, cons, cons.opposite(s), s);
    }


    /**
     * Following the type of constraints, create a reified constraint.
     *
     * @param bool    boolean variable for reification
     * @param cons    a constraint
     * @param oppcons the opposite constraint of {@code cons}
     * @param solver
     * @return a SConstraint
     * @throws UnsupportedOperationException when an expression constraint is in the scope.
     */
    public static SConstraint builder(IntDomainVar bool, SConstraint cons, SConstraint oppcons, final Solver solver) {
        SConstraintType c_int = cons.getConstraintType();
        SConstraintType oc_int = oppcons.getConstraintType();
//        if (!c_int.canBeReified() || !oc_int.canBeReified()) {
//            throw new UnsupportedOperationException(MessageFormat.format("{0} or {1} can not be reified", cons.pretty(),
//                    oppcons.pretty()));
//        }
        SConstraintType globalType = merge(c_int, oc_int);
        switch (globalType) {
            case INTEGER:
                return new ReifiedIntSConstraint(bool, (AbstractIntSConstraint) cons, (AbstractIntSConstraint) oppcons);
            case EXPRESSION:
                ExpressionSConstraint ec = (ExpressionSConstraint)cons;
                ExpressionSConstraint oec = (ExpressionSConstraint)cons;
                IntDomainVar vec = ec.expr.extractResult(solver);
                IntDomainVar voec = oec.expr.extractResult(solver);
                return new ReifiedBinXor(bool, vec, voec);
            default:
                return new ReifiedAllSConstraint(bool, (AbstractSConstraint) cons, (AbstractSConstraint) oppcons);

        }
    }

    /**
     * Scan and return the merged {@link choco.kernel.solver.constraints.SConstraintType} of the two constraints
     * {@code c_int} and {@code oc_int}.
     *
     * @param c_int  a constraint
     * @param oc_int another constraint
     * @return the type of the both constraint
     */
    private static SConstraintType merge(final SConstraintType c_int, final SConstraintType oc_int) {
        if (c_int.equals(oc_int)) {
            return c_int;
        } else {
            if (SConstraintType.EXPRESSION.equals(c_int)
                    || SConstraintType.EXPRESSION.equals(oc_int)) {
                return SConstraintType.EXPRESSION;
            } else {
                return SConstraintType.MIXED;
            }
        }
    }

}
