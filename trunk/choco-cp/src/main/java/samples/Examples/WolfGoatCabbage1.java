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
package samples.Examples;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;

/*
* Created by IntelliJ IDEA.
* User: charles
* Date: 9 juin 2008
* Since : Choco 2.0.0
*
*/
public class WolfGoatCabbage1 extends PatternExample {

    protected final static Logger LOGGER = ChocoLogging.getSamplesLogger();

    static int numberOfStates = 8;
    static WGC_State[] states;

    @Override
    public void setUp(Object paramaters) {
        if (paramaters instanceof Integer) {
            numberOfStates = (Integer) paramaters;
        }
    }


    @Override
    public void buildModel() {
        _m = new CPModel();
        // Create an array of numberOfStates states. The start
        // state and the end state are fixed. The intermediate
        // states are unknown as yet.
        states = new WGC_State[numberOfStates];
        for (int j = 0; j < numberOfStates; j++) {
            // Create state[j]. Pass the index of the final state.
            states[j] = new WGC_State(j, numberOfStates - 1);
            // The State method oneStepTo() returns the constraint
            // that requires the argument state to be a valid
            // successor of itself.
            if (j > 0) _m.addConstraint(states[j - 1].oneStepTo(states[j]));
        }
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
        _s.read(_m);
    }

    @Override
    public void solve() {
        _s.solve();
    }

    @Override
    public void prettyOut() {
        printStateTransitions();
    }

    private static void printStateTransitions() {
        StringBuffer st = new StringBuffer();
        st.append("\n\n"+(_s.isFeasible() ? "S" : "Not s") + "olved in " + states.length + " states: ");
        for (int j = 0; j < states.length; j++) {
            if (j > 0) {
                if (states[j - 1].isInstantiated(_s)
                        || states[j].isInstantiated(_s)
                        || !states[j - 1].canTransitionTo(states[j]))
                    st.append(" -/-> ");
                else st.append(" --> ");
            }
            st.append(states[j].toString(_s));
        }
        LOGGER.info(st.toString());
    }

    private static class WGC_State {
        int farmer;
        IntegerVariable wolf, goat, cabbage;

        public WGC_State(int index, int lastState) {
            // The farmer alternates between left and right.
            farmer = index % 2;
            wolf = createWGC_IntVar(index, lastState, "wolf");
            goat = createWGC_IntVar(index, lastState, "goat");
            cabbage = createWGC_IntVar(index, lastState, "cabbage");
        }

        private void addElt(StringBuffer left, StringBuffer right, char c, int pos) {
            (pos == 0 ? left : right).append(c);
        }

        public boolean canTransitionTo(WGC_State next) {
            return
                    farmer != next.farmer &&
                            (
                                    (wolf == next.wolf && goat == next.goat && cabbage == next.cabbage) ||
                                            (wolf != next.wolf && goat == next.goat && cabbage == next.cabbage) ||
                                            (wolf == next.wolf && goat != next.goat && cabbage == next.cabbage) ||
                                            (wolf == next.wolf && goat == next.goat && cabbage != next.cabbage)
                            );
        }

        /**
         * Create an IntVar for the wolf, goat, or cabbage.
         */
        private IntegerVariable createWGC_IntVar(int index, int lastState,
                                                 String name) {
            // Must end with all elements on the right bank (1).
            // Must start with all elements on the left bank (0).
            return makeIntVar(name + "[" + index + "]",
                    index == lastState ? 1 : 0, index == 0 ? 0 : 1);
        }

        public boolean isInstantiated(Solver s) {
            return s.getVar(wolf).isInstantiated() && s.getVar(goat).isInstantiated() && s.getVar(cabbage).isInstantiated();
        }

        /**
         * A state is valid if the farmer is with either (a) the goat or
         * (b) both the wolf and cabbage.
         *
         * @return the constraint
         */
        public Constraint isValid() {
            return or(
                    eq((goat), (farmer)),
                    and(
                            eq((wolf), (farmer)),
                            eq((cabbage), (farmer))
                    )
            );
        }

        /**
         * This constraint says that the next state is a valid trasition from this
         * one.  A transition is valid if the next state is valid and if at most
         * one of the wolf, goat, and cabbage has changed.  We don't have to
         * check to be sure the farmer has changed. That was set up when the
         * states were defined.  In any case, that would be a test that
         * would be made at the Java level since farmer is an instance
         * variable and not an IntDomainVar.
         *
         * @param next the next state
         * @return the constraint
         */
        public Constraint oneStepTo(WGC_State next) {
            return and(
                    next.isValid(),
                    or(
                            // None of wgc has changed.
                            and(eq((wolf), (next.wolf)),
                                    eq((goat), (next.goat)),
                                    eq((cabbage), (next.cabbage))),
                            // Only the wolf has changed.
                            and(neq((wolf), (next.wolf)),
                                    eq((goat), (next.goat)),
                                    eq((cabbage), (next.cabbage))),
                            // Only the goat has changed.
                            and(eq((wolf), (next.wolf)),
                                    neq((goat), (next.goat)),
                                    eq((cabbage), (next.cabbage))),
                            // Only the cabbage has changed.
                            and(eq((wolf), (next.wolf)),
                                    eq((goat), (next.goat)),
                                    neq((cabbage), (next.cabbage)))));
        }

        public String toString(Solver s) {
            StringBuffer left = new StringBuffer();
            StringBuffer right = new StringBuffer();
            if (!(s.getVar(wolf).isInstantiated() && s.getVar(goat).isInstantiated() && s.getVar(cabbage).isInstantiated()))
                return "?";
            addElt(left, right, 'f', farmer);
            addElt(left, right, 'w', s.getVar(wolf).getVal());
            addElt(left, right, 'g', s.getVar(goat).getVal());
            addElt(left, right, 'c', s.getVar(cabbage).getVal());
            return "[" + left + "|" + right + "]";
        }

    }

     public static void main(String[] args) {
        // Since we start on the left bank and end on the right
        // there must be an even number of states. Let i be
        // the number of states, including the start and end states.
        for (int i = 2; i <= 32; i += 2) {
            new WolfGoatCabbage1().execute(i);
        }
    }


}
