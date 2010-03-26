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

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.regular.Regular;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.model.constraints.automaton.Transition;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/*
 *  ______
 * (__  __)
 *    ||
 *   /__\                  Choco manager
 *    \                    =============
 *    \                      Aug. 2008
 *    \                  Regular constraint
 *    \
 *    |
 */
/**
 * A manager to build new regular constraint
 */
public final class RegularManager extends IntConstraintManager {
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] vars, Object parameters, Set<String> options) {
        if (solver instanceof CPSolver) {
            IntDomainVar[] variables = solver.getVar((IntegerVariable[]) vars);
            if (parameters instanceof int[][]) {
                int[] coefs = ((int[][]) parameters)[0];
                int value = ((int[][]) parameters)[1][0];
                return knapsack(solver, vars, value, coefs);
            } else if (parameters instanceof Object[]) { // List of tuples, min and max lists
                Object[] params = (Object[]) parameters;
                return new Regular(new DFA((List<int[]>) params[0], (int[]) params[1], (int[]) params[2]), variables, solver.getEnvironment());
            } else if (parameters instanceof List) { // List of tuples
                return new Regular(new DFA((List<int[]>) parameters), variables, solver.getEnvironment());
            } else if (parameters instanceof DFA) { // Direct DFA
                return new Regular((DFA) parameters, variables, solver.getEnvironment());
            } else if (parameters instanceof String) { // Regexp
                return new Regular(new DFA((String) parameters, vars.length), variables, solver.getEnvironment());
            }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

    public int[] getFavoriteDomains(Set<String> options) {
        return new int[]{
                IntDomainVar.BITSET,
                IntDomainVar.LINKEDLIST,
                IntDomainVar.BINARYTREE};
    }


    private int nID;

    /**
     * @param bools Boolean variables
     * @param goal  Sum
     * @param A     An array Containing the coefficients
     * @return
     */
    public SConstraint knapsack(Solver s, IntegerVariable[] bools, int goal, int[] A) {
        int[] temp = new int[A.length + 1];
        System.arraycopy(A, 0, temp, 1, A.length);
        temp[0] = 0;
        A = temp;
        int U = goal;
        int L = goal;
        int[][] P = new int[A.length][U + 1];
        int[][] G = new int[A.length][U + 1];

        for (int i = 0; i < P.length; ++i)
            for (int j = 0; j < P[0].length; ++j)
                P[i][j] = G[i][j] = 0;

        P[0][0] = 1;

        for (int i = 1; i < P.length; ++i)
            for (int b = 0; b < P[0].length; ++b)
                if (P[i - 1][b] == 1) {
                    DisposableIntIterator it = bools[i - 1].getDomainIterator();
                    while (it.hasNext()) {
                        int x = it.next();
                        //for (int x = bools[i - 1].getLowB(); x <= bools[i - 1].getUppB(); ++x)
                        if (b + A[i] * x <= U)
                            P[i][b + A[i] * x] = 1;
                    }
                    it.dispose();
                }

        boolean sat = false;
        for (int i = U; i >= L; --i)
            sat |= (P[P.length - 1][U] == 1);

        G[G.length - 1][U] = 1;
        if (sat) {
            for (int i = G.length - 2; i >= 0; --i)
                for (int b = 0; b < G[0].length; b++)
                    if (G[i + 1][b] == 1) {
                        DisposableIntIterator it = bools[i].getDomainIterator();
                        while (it.hasNext()) {
                            int x = it.next();
                            //for (int x = bools[i].getLowB(); x <= bools[i].getUppB(); ++x)
                            if (b - A[i + 1] * x >= 0 && P[i][b - A[i + 1] * x] == 1)
                                G[i][b - A[i + 1] * x] = 1;
                        }
                        it.dispose();
                    }

            List<Transition> t = new LinkedList<Transition>();
            List<Integer> ints = new LinkedList<Integer>();
            nID = 0;
            int[][] labels = new int[G.length][G[0].length];
            for (int i = 0; i < labels.length; ++i)
                for (int j = 0; j < labels[0].length; ++j)
                    labels[i][j] = -1;
            generateTransitionList(0, 0, t, labels, A, G, bools);
            for (int i = 0; i <= (L - U); ++i) {
                ints.add(G.length + i - 1);
            }
            DFA dfa = new DFA(t, ints, ints.get(0));
            return new Regular(dfa, s.getVar(bools), s.getEnvironment());
        } else {
            return CPSolver.FALSE;// not satisfiable
        }
    }

    /**
     * Generates the list of transitions
     *
     * @param x      X-ord in grid G
     * @param y      Y-ord in grid G
     * @param t      List of transitions
     * @param labels Table containing all nodes previously labeled
     * @param A      Array of coeffieients
     * @param G      Table containg minimum solution paths
     * @param bools  list of variables (not always bools)
     */
    private void generateTransitionList(int x, int y, List<Transition> t,
                                        int[][] labels, int[] A, int[][] G, IntegerVariable[] bools) {
        if (x >= G.length - 1)
            return;
        int[] vars = bools[x].getValues();
        if (vars == null) {
            for (int var = bools[x].getLowB(); var <= bools[x].getUppB(); ++var)
                if (y + A[x + 1] * var < G[0].length && G[x + 1][y + A[x + 1] * var] == 1) {
                    if (labels[x][y] == -1)
                        labels[x][y] = nID++;
                    if (labels[x + 1][y + A[x + 1] * var] == -1)
                        labels[x + 1][y + A[x + 1] * var] = nID++;
                    t.add(new Transition(labels[x][y], var, labels[x + 1][y + A[x + 1] * var]));
                    generateTransitionList(x + 1, y + A[x + 1] * var, t, labels, A, G, bools);
                }
        } else {
            for (int var = 0; var < vars.length; ++var) {
                if (y + A[x + 1] * vars[var] < G[0].length && G[x + 1][y + A[x + 1] * vars[var]] == 1) {
                    if (labels[x][y] == -1)
                        labels[x][y] = nID++;
                    if (labels[x + 1][y + A[x + 1] * vars[var]] == -1)
                        labels[x + 1][y + A[x + 1] * vars[var]] = nID++;
                    t.add(new Transition(labels[x][y], vars[var],
                            labels[x + 1][y + A[x + 1] * vars[var]]));
                    generateTransitionList(x + 1, y + A[x + 1] * vars[var], t, labels, A, G, bools);
                }
            }
        }
    }

}

