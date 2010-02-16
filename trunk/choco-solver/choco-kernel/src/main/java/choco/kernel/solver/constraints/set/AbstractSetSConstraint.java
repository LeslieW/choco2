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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.listener.SetPropagator;
import choco.kernel.solver.variables.set.SetVar;


// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractSetSConstraint extends AbstractSConstraint<SetVar> implements SetPropagator {

    /**
     * Constraucts a constraint with the priority 0.
     */
    protected AbstractSetSConstraint(SetVar[] vars) {
        super(vars);
    }

    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnInst(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnEnvRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
        if (deltaDomain != null) {
            try {
                for (; deltaDomain.hasNext();) {
                    int val = deltaDomain.next();
                    awakeOnEnv(idx, val);
                }
            } finally {
                deltaDomain.dispose();
            }
        } else {
            throw new SolverException("deltaDomain should not be null in awakeOnEnvRemovals");
        }
    }

    public void awakeOnkerAdditions(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
        if (deltaDomain != null) {
            try {
                for (; deltaDomain.hasNext();) {
                    int val = deltaDomain.next();
                    awakeOnKer(idx, val);
                }
            } finally {
                deltaDomain.dispose();
            }
        } else {
            throw new SolverException("deltaDomain should not be null in awakeOnKerAdditions");
        }
    }

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.SET;
    }
}
