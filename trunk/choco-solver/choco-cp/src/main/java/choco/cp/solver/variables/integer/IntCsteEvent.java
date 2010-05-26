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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.propagation.listener.IntPropagator;

@SuppressWarnings({"unchecked"})
public class IntCsteEvent<C extends AbstractSConstraint & IntPropagator> extends IntVarEvent<C> {

    public IntCsteEvent(IntDomainVarImpl var) {
        super(var);
        eventType = EMPTYEVENT;
    }

    /**
     * useful for debugging
     */
    public String toString() {
        return ("VarEvt(" + modifiedVar + ")[" + eventType + ':'
                + ((eventType & INCINF_MASK) != 0 ? "I" : "")
                + ((eventType & DECSUP_MASK) != 0 ? "S" : "")
                + ((eventType & REMVAL_MASK) != 0 ? "r" : "")
                + ((eventType & INSTINT_MASK) != 0 ? "X" : "")
                + ']');
    }

    @Override
    public int getPriority() {
        return modifiedVar.getPriority();
    }

    /**
     * Clears the var: delegates to the basic events.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * the event had been "frozen", (since the call to freeze), while it was handled by the propagation engine:
     * This meant that the meaning of the event could not be changed: it represented
     * a static set of value removals, during propagation.
     * Now, the event becomes "open" again: new value removals can be hosted, the delta domain can
     * accept that further values are removed.
     * In case value removals happened while the event was frozen, the release method returns false
     * (the event cannot be released, it must be handled once more). Otherwise (the standard behavior),
     * the method returns true
     */
    protected boolean release() {
        throw new UnsupportedOperationException();
    }

    protected void freeze() {
        throw new UnsupportedOperationException();
    }

    public boolean getReleased() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over the set of removed values
     *
     * @return an iterator over the set of values that have been removed from the domain
     */
    public DisposableIntIterator getEventIterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Propagates the event through calls to the propagation engine.
     *
     * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
     * @throws choco.kernel.solver.ContradictionException
     */
    public boolean propagateEvent() throws ContradictionException {
        throw new UnsupportedOperationException();
    }
}