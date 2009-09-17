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
package choco.cp.model.managers.constraints.global;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.regular.Regular;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.model.constraints.automaton.Transition;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.*;

/*
 *  ______
 * (__  __)
 *    ||
 *   /__\                  Choco manager
 *    \                    =============
 *    \                      Aug. 2008
 *    \            All alldifferent constraints
 *    \
 *    |
 */
/**
 * A manager to build new all stretchpath constraints
 */
public class StretchPathManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {

            if (parameters instanceof List) {
                List<int[]> stretchParameters = (List<int[]>)parameters;

                IntDomainVar[] vars = solver.getVar((IntegerVariable[]) variables);

                IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
                System.arraycopy(vars, 0, tmpVars, 0, vars.length);

                ArrayList<Integer> alphabet = new ArrayList<Integer>();
                for (int i = 0; i < vars.length; i++) {
                    DisposableIntIterator it = tmpVars[i].getDomain().getIterator();
                    for ( ; it.hasNext();) {
                        int val = it.next();
                        if (!alphabet.contains(val)) {
                            alphabet.add(val);
                        }
                    }
                    it.dispose();
                }

                int nbStates = 1;
                Hashtable<Integer, Integer> tab = new Hashtable<Integer, Integer>();
                List<Transition> t = new LinkedList<Transition>();
                List<Integer> fs = new LinkedList<Integer>();
                fs.add(0);

                for (int[] vals : stretchParameters) {
                    int valState = nbStates++;
                    tab.put(vals[0], valState);
                    t.add(new Transition(0, vals[0], valState));
                    if (vals[1] == 1) {
                        fs.add(valState);
                    }
                }

                for (Integer val : alphabet) {
                    if (!tab.containsKey(val)) {
                        t.add(new Transition(0, val, 0));
                    }
                }

                for (int[] vals : stretchParameters) {
                    int lastState = tab.get(vals[0]);
                    for (int j = 2; j <= vals[2]; j++) {
                        int newState = nbStates++;
                        t.add(new Transition(lastState, vals[0], newState));
                        if ((j > vals[1])) {
                            for (int i1 = 0; i1 < alphabet.size(); i1++) {
                                Object anAlphabet = alphabet.get(i1);
                                int val = (Integer) anAlphabet;
                                if ((vals[0] != val)) {
                                    if (tab.containsKey(val)) {
                                        int dest = tab.get(val);
                                        t.add(new Transition(lastState, val, dest));
                                    } else {
                                        t.add(new Transition(lastState, val, 0));
                                    }
                                }
                            }
                        }

                        if (j >= vals[1]) {
                            fs.add(newState);
                        }
                        lastState = newState;
                    }

                    for (Integer val : alphabet) {
                        if (vals[0] != val) {
                            if (tab.containsKey(val)) {
                                int dest = tab.get(val);
                                t.add(new Transition(lastState, val, dest));
                            } else {
                                t.add(new Transition(lastState, val, 0));
                            }
                        }
                    }
                }


                DFA auto = new DFA(t, fs, vars.length);

                return new Regular(auto, tmpVars);
            }
        }
        if (Choco.DEBUG) {
            LOGGER.severe("Could not found an implementation of stretchPath !");
        }
        return null;
    }
}
