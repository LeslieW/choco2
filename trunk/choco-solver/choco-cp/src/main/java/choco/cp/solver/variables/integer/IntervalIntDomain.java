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
package choco.cp.solver.variables.integer;

import choco.cp.common.util.iterators.IntervalIntDomainIterator;
import choco.cp.solver.variables.delta.IntervalDeltaDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.OneValueIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.PropagationEngine;

import java.util.Random;

public class IntervalIntDomain extends AbstractIntDomain {
    private static final int eventBitMask = IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector;
    /**
     * A random generator for random value from the domain
     */

    protected static Random random = new Random(System.currentTimeMillis());

    /**
     * The backtrackable minimal value of the variable.
     */

    protected final IStateInt inf;

    /**
     * The backtrackable maximal value of the variable.
     */

    protected final IStateInt sup;

    public IntervalIntDomain(final IntDomainVarImpl v, final int a, final int b, final IEnvironment environment, final PropagationEngine propagationEngine) {
        super(propagationEngine);
        variable = v;
        inf = environment.makeInt(a);
        sup = environment.makeInt(b);
        deltaDom = new IntervalDeltaDomain(this, a,b);

    }

    public boolean contains(final int x) {
        return ((x >= getInf()) && (x <= getSup()));
    }

    public int getNextValue(final int x) {
        if (x < getInf()) {
            return getInf();
        } else if (x < getSup()) {
            return x + 1;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public int getPrevValue(final int x) {
        if (x > getSup()) {
            return getSup();
        } else if (x > getInf()) {
            return x - 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public int getRandomValue() {
        final int inf = getInf();
        final int rand = random.nextInt(this.getSize());
        return inf + rand;
    }

    public final int getSize() {
        return getSup() - getInf() + 1;
    }

    public boolean hasNextValue(final int x) {
        return (x < getSup());
    }

    public boolean hasPrevValue(final int x) {
        return (x > getInf());
    }

    protected DisposableIntIterator _iterator = null;

    public DisposableIntIterator getIterator(){
        if(getSize() == 1) return OneValueIterator.getIterator(getInf());
        return IntervalIntDomainIterator.getIterator(this);
      }

    public boolean remove(final int x) {
        return false;
    }

    public final int getSup() {
        return sup.get();
    }

    public final int getInf() {
        return inf.get();
    }

    public void restrict(final int x) {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
        deltaDom.remove(x);
      }
        inf.set(x);
        sup.set(x);
    }

    public int updateInf(final int x) {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
        deltaDom.remove(x);
      }
        inf.set(x);
        return x;
    }

    public int updateSup(final int x) {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
        deltaDom.remove(x);
      }
        sup.set(x);
        return x;
    }

    protected boolean _removeVal(final int x, final SConstraint cause) throws ContradictionException {
        final int infv = getInf();
        final int supv = getSup();
        if (x == infv) {
            _updateInf(x + 1, cause);
            if (getInf() == supv) _instantiate(supv, cause);
            return true;
        } else if (x == supv) {
            _updateSup(x - 1, cause);
            if (getSup() == infv) _instantiate(infv, cause);
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnumerated() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public String pretty() {
        final StringBuilder ret = new StringBuilder();
        ret.append('[').append(this.getInf()).append(" .. ").append(this.getSup()).append(']');
        ret.append(deltaDom.pretty());
        return ret.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////// DELTA DOMAIN /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void freezeDeltaDomain() {
        if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
            deltaDom.freeze();
        }
    }

    /**
     * release the delta domain
     *
     * @return wether it was a new update
     */
    @Override
    public boolean releaseDeltaDomain() {
        return (variable.getEvent().getPropagatedEvents() & eventBitMask) == 0 || deltaDom.release();
    }

    /**
     * checks whether the delta domain has indeed been released (ie: chechks that no domain updates are pending)
     */
    @Override
    public boolean getReleasedDeltaDomain() {
        return (variable.getEvent().getPropagatedEvents() & eventBitMask) == 0 || deltaDom.isReleased();
    }
}
