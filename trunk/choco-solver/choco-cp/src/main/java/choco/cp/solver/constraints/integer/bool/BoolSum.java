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
package choco.cp.solver.constraints.integer.bool;

import choco.cp.solver.constraints.integer.IntLinComb;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A special case of sums over boolean variables only
 */
public class BoolSum extends AbstractLargeIntSConstraint {

    /**
     * The number of variables instantiated to zero in the sum
     */
    protected IStateInt nbz;

    /**
     * The number of variables instantiated to one in the sum
     */
    protected IStateInt nbo;


//    protected StoredIndexedBipartiteSet noninst;


    protected int op;

    protected int gap;

    private int coeff;

    public BoolSum(IntDomainVar[] vars, int coef, int op, IEnvironment environment) {
        super(vars);
        this.coeff = coef;
        this.op = op;
        this.gap = vars.length - coeff;
        nbz = environment.makeInt(0);
        nbo = environment.makeInt(0);
//        int[] lvar = new int[vars.length];
//        for (int i = 0; i < lvar.length; i++) {
//            lvar[i] = i;
//        }
//        IEnvironment env = vars[0].getSolver().getEnvironment();
//        noninst = (StoredIndexedBipartiteSet) env.makeBipartiteSet(lvar);
    }


    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    public void propagate() throws ContradictionException {
        if (coeff == 0 && (op == IntLinComb.EQ || op == IntLinComb.LEQ)) {
            for (int i = 0; i < vars.length; i++)
                vars[i].instantiate(0, cIndices[i]);
        } else if (coeff == vars.length && (op == IntLinComb.EQ || op == IntLinComb.GEQ)) {
            for (int i = 0; i < vars.length; i++)
                vars[i].instantiate(1, cIndices[i]);
        } else {
            nbz.set(0);
            nbo.set(0);
            for (int i = 0; i < vars.length; i++) {
                if (vars[i].isInstantiated()) {
                    awakeOnInst(i);
                }
            }
        }
    }

    public void putAllZero() throws ContradictionException {
        for (int i = 0; i < vars.length; i++) {
            if (!vars[i].isInstantiated())
                vars[i].instantiate(0, cIndices[i]);
        }
//        DisposableIntIterator it = noninst.getIterator();
//        while (it.hasNext()) {
//            int idx = it.next();
//            vars[idx].instantiate(0, cIndices[idx]);
//        }
//        it.dispose();
    }

    public void putAllOne() throws ContradictionException {
        for (int i = 0; i < vars.length; i++) {
            if (!vars[i].isInstantiated())
                vars[i].instantiate(1, cIndices[i]);
        }
//        DisposableIntIterator it = noninst.getIterator();
//        while (it.hasNext()) {
//            int idx = it.next();
//            vars[idx].instantiate(1, cIndices[idx]);
//        }
//        it.dispose();
    }


    public void awakeOnInst(int idx) throws ContradictionException {
        int val = vars[idx].getVal();
        if (val == 0) {
            nbz.add(1);
        } else {
            nbo.add(1);
        }
        //noninst.remove(idx);
        if (op == IntLinComb.GEQ) {
            if (nbo.get() >= coeff) {
                setEntailed();
            } else if (nbz.get() > gap) {
                fail();
            } else if (nbz.get() == gap) {
                putAllOne();
            }
        } else if (op == IntLinComb.LEQ) {
            if (nbz.get() >= gap) {
                setEntailed();
            } else if (nbo.get() > coeff) {
                fail();
            } else if (nbo.get() == coeff) {
                putAllZero();
            }
        } else if (op == IntLinComb.EQ) {
            if (nbo.get() > coeff || nbz.get() > gap)
                fail();
            if (nbo.get() == coeff) {
                putAllZero();
            } else if (nbz.get() == gap) {
                putAllOne();
            }
        } else if (op == IntLinComb.NEQ) {
            throw new SolverException("Neq operator not ready for BoolIntLinComb");
        }
    }

    public boolean isSatisfied(int[] tuple) {
        int s = 0;
        for (int i = 0; i < tuple.length; i++) {
            s += tuple[i];
        }
        if (op == IntLinComb.GEQ) {
            return s >= coeff;
        } else if (op == IntLinComb.LEQ) {
            return s <= coeff;
        } else if (op == IntLinComb.EQ) {
            return s == coeff;
        } else if (op == IntLinComb.NEQ) {
            return s != coeff;
        } else {
            throw new SolverException("operator unknown for BoolIntLinComb");
        }
    }

    /**
     * Computes the opposite of this constraint.
     *
     * @return a constraint with the opposite semantic  @param solver
     */
    @Override
    public AbstractSConstraint opposite(Solver solver) {
        IntDomainVar[] bvs = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, bvs, 0, vars.length);
        if (op == IntLinComb.EQ) {
            return new BoolSum(bvs, coeff, IntLinComb.NEQ, solver.getEnvironment());
        } else if (op == IntLinComb.NEQ) {
            return new BoolSum(bvs, coeff, IntLinComb.EQ, solver.getEnvironment());
        } else if (op == IntLinComb.GEQ) {
            return new BoolSum(bvs, coeff - 1, IntLinComb.LEQ, solver.getEnvironment());
        } else if (op == IntLinComb.LEQ) {
            return new BoolSum(bvs, coeff + 1, IntLinComb.GEQ, solver.getEnvironment());
        } else {
            throw new SolverException("operator unknown for BoolIntLinComb");
        }
    }

    /**
     * Computes an upper bound estimate of a linear combination of variables.
     *
     * @return the new upper bound value
     */
    protected int computeUbFromScratch() {
        int s = 0;
        for (int i = 0; i < vars.length; i++) {
            s += vars[i].getSup();
        }
        return s;
    }

    /**
     * Computes a lower bound estimate of a linear combination of variables.
     *
     * @return the new lower bound value
     */
    protected int computeLbFromScratch() {
        int s = 0;
        for (int i = 0; i < vars.length; i++) {
            s += vars[i].getInf();
        }
        return s;
    }


    /**
     * Checks if the constraint is entailed.
     *
     * @return Boolean.TRUE if the constraint is satisfied, Boolean.FALSE if it
     *         is violated, and null if the filtering algorithm cannot infer yet.
     */
    @Override
    public Boolean isEntailed() {
        if (op == IntLinComb.EQ) {
            int lb = computeLbFromScratch();
            int ub = computeUbFromScratch();
            if (lb > coeff || ub < coeff) {
                return Boolean.FALSE;
            } else if (lb == ub && coeff == lb) {
                return Boolean.TRUE;
            } else {
                return null;
            }
        } else if (op == IntLinComb.GEQ) {
            if (computeLbFromScratch() >= coeff) {
                return Boolean.TRUE;
            } else if (computeUbFromScratch() < coeff) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        } else if (op == IntLinComb.LEQ) {
            if (computeUbFromScratch() <= coeff) {
                return Boolean.TRUE;
            } else if (computeLbFromScratch() > coeff) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        } else {
            throw new SolverException("NEQ not managed by boolIntLinComb");
        }
    }


}
