package choco.cp.solver.variables.integer;

/**
 * An event dedicated to boolean variables
 **/
public class BoolVarEvent extends IntVarEvent {

    public BoolVarEvent(IntDomainVarImpl var) {
        super(var);
    }


    @Override
    protected void freeze() {
        cause = null;
        eventType = EMPTYEVENT;
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
    @Override
    protected boolean release() {
        return true;
    }
}
