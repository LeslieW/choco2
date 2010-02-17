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

package choco.cp.solver.constraints;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class ConstantSConstraint extends AbstractIntSConstraint{
    private boolean satisfied;

    public ConstantSConstraint(boolean value) {
        super(ConstraintEvent.HIGH, new IntDomainVar[]{});
        satisfied = value;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getNbVars() {
        return 0;
    }

    public IntDomainVar getVar(int i) {
        return null;
    }

    @Override
    public boolean isSatisfied(int[] tuple) {
        return satisfied;
    }

    public void setVar(int i, IntDomainVar v) {
        throw new SolverException("BUG in CSP network management: too large index for setVar");
    }

    @Override
    public boolean isCompletelyInstantiated() {
        return true;
    }

    @Override
    public boolean isSatisfied() {
        return satisfied;
    }

    public void propagate() throws ContradictionException {
        if (!satisfied) {
            fail();
        }
    }

    @Override
    public boolean isConsistent() {
        return satisfied;
    }

    public void setConstraintIndex(int i, int idx) {
        throw new UnsupportedOperationException();
    }

    public int getConstraintIdx(int idx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean isEntailed() {
        return satisfied ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public AbstractSConstraint opposite(Solver solver) {
        return new ConstantSConstraint(!satisfied);
    }


    @Override
    public String pretty() {
        return "Constant constraint: " + isSatisfied();
    }


}