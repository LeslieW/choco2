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
package choco.cp.solver.constraints.strong;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.propagation.ConstraintEvent;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class Adapter extends AbstractIntSConstraint implements
        ISpecializedConstraint {

    private static final int[] tuple = new int[2];

    private final IntSConstraint sConstraint;

    public Adapter(IntSConstraint sConstraint) {
        super(ConstraintEvent.MEDIUM);
        this.sConstraint = sConstraint;
        setSolver(sConstraint.getSolver());
    }

    public int firstSupport(int position, int value) {
        tuple[position] = value;

        final DisposableIntIterator itr = getVar(1 - position).getDomain()
                .getIterator();

        while (itr.hasNext()) {
            tuple[1 - position] = itr.next();
            if (check(tuple)) {
                itr.dispose();
                return tuple[1 - position];
            }
        }

        itr.dispose();
        return Integer.MAX_VALUE;
    }

    public int nextSupport(int position, int value, int lastSupport) {
        tuple[position] = value;
        final IntDomainVar iterateOver = getVar(1 - position);
        tuple[1 - position] = iterateOver.getNextDomainValue(lastSupport);

        while (tuple[1 - position] < Integer.MAX_VALUE && !check(tuple)) {
            tuple[1 - position] = iterateOver
                    .getNextDomainValue(tuple[1 - position]);
        }
        return tuple[1 - position];
    }

    @Override
    public String toString() {
        return sConstraint.toString();
    }

    @Override
    public int getConstraintIdx(int idx) {
        return sConstraint.getConstraintIdx(idx);
    }

    @Override
    public int getNbVars() {
        return sConstraint.getNbVars();
    }

    @Override
    public void setConstraintIndex(int i, int idx) {
        sConstraint.setConstraintIndex(i, idx);
    }

    @Override
    public void setVar(int i, Var v) {
        sConstraint.setVar(i, v);
    }

    @Override
    public String pretty() {
        return sConstraint.pretty();
    }

    @Override
    public boolean isSatisfied() {
        return sConstraint.isSatisfied();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean check(int[] tuple) {
        return sConstraint.isSatisfied(tuple);
    }

    @Override
    public IntDomainVar getVar(int i) {
        return sConstraint.getIntVar(i);
    }

    @Override
    public boolean isCompletelyInstantiated() {
        return sConstraint.isCompletelyInstantiated();
    }

    @Override
    public boolean isConsistent() {
        return sConstraint.isConsistent();
    }

    @Override
    public void propagate() throws ContradictionException {
        sConstraint.propagate();
    }

    @Override
    public IntDomainVar getIntVar(int i) {
        return sConstraint.getIntVar(i);
    }
}