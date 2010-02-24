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
import choco.kernel.solver.propagation.listener.IntPropagator;
import choco.kernel.solver.propagation.listener.SetPropagator;
import choco.kernel.solver.variables.Var;

//**************************************************
//*                   J-CHOCO                      *
//*   Copyright (C) F. Laburthe, 1999-2003         *
//**************************************************
//*  an open-source Constraint Programming Kernel  *
//*     for Research and Education                 *
//**************************************************

/**
 * A class for mixed set and int constraint. It refers to both interface
 * SetConstraint and IntConstraint and implements a default behaviour
 * for all events awakeOn...
 */
public abstract class AbstractMixedSetIntSConstraint extends AbstractSConstraint<Var> implements SetPropagator, IntPropagator{

    /**
     * Constructs a constraint with the specified priority.
     *
     * @param priority The wished priority.
     */
    protected AbstractMixedSetIntSConstraint(Var[] vars) {
        super(vars);
    }


    /**
	 * The default implementation of propagation when a variable has been modified
	 * consists in iterating all values that have been removed (the delta domain)
	 * and propagate them one after another, incrementally.
	 *
	 * @param idx
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		if (deltaDomain != null) {
            try{
			for (; deltaDomain.hasNext();) {
				int val = deltaDomain.next();
				awakeOnRem(idx, val);
			}
            }finally {
                deltaDomain.dispose();
            }
		}
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		awakeOnInf(varIndex);
		awakeOnSup(varIndex);
	}


	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		propagate();
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		propagate();
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		propagate();
	}

    /**
     * Default propagation on one value removal: propagation on domain revision.
     */

    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        this.constAwake(false);
    }

    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     */

    public void awakeOnInf(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     */

    public void awakeOnSup(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnEnvRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		if (deltaDomain != null) {
            try{
			for (; deltaDomain.hasNext();) {
				int val = deltaDomain.next();
				awakeOnEnv(idx, val);
			}
            }finally {
                deltaDomain.dispose();
            }
		} else {
			throw new SolverException("deltaDomain should not be null in awakeOnEnvRemovals");
		}
	}

	public void awakeOnkerAdditions(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		if (deltaDomain != null) {
            try{
			for (; deltaDomain.hasNext();) {
				int val = deltaDomain.next();
				awakeOnKer(idx, val);
			}
            }finally {
                deltaDomain.dispose();
            }
		} else {
			throw new SolverException("deltaDomain should not be null in awakeOnKerAdditions");
		}
	}

	public boolean isCompletelyInstantiated() {
		int n = getNbVars();
		for (int i = 0; i < n; i++) {
			if (!(getVar(i).isInstantiated())) {
				return false;
			}
		}
		return true;
	}

	public boolean isSatisfied(int[] tuple) {
		throw new UnsupportedOperationException(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.INT_SET;
    }

    /**
     * tests if the constraint is consistent with respect to the current state of domains
     *
     * @return wether the constraint is consistent
     */
    @Override
    public boolean isConsistent() {
        return (isEntailed() == Boolean.TRUE);
    }
}
