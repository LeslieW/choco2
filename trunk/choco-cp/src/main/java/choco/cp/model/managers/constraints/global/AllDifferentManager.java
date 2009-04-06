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
import choco.cp.solver.constraints.global.BoundAllDiff;
import choco.cp.solver.constraints.global.matching.AllDifferent;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

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
 * A manager to build new all different constraints (and more... soon)
 */
public class AllDifferentManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, Variable[] vars, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {
            IntDomainVar[] variables = solver.getVar((IntegerVariable[]) vars);
            if (options.contains("cp:ac"))
                return new AllDifferent(variables);
            if (options.contains("cp:bc"))
                return new BoundAllDiff(variables, true);
            if (options.contains("cp:clique"))
                return new BoundAllDiff(variables, false);

            return defaultDetection(variables);
        }
        if (Choco.DEBUG) {
            System.err.println("Could not found an implementation of alldifferent !");
        }
        return null;
    }

    public int[] getFavoriteDomains(HashSet<String> options) {
        if (options.contains("cp:bc")) {
            return getBCFavoriteIntDomains();
        } else {
            return getACFavoriteIntDomains();
        }
    }

    /**
     * make a choice if the user didn't specify the type of consistency desired
     * @param vars
     * @return
     */
    public SConstraint defaultDetection(IntDomainVar[] vars) {
            int maxdszise = 0;
            int nbnoninstvar = 0;
            boolean holes = false;
            boolean boundOnly = true;
            for (int i = 0; i < vars.length; i++) {
                boundOnly &= !vars[i].hasEnumeratedDomain();
                int span = vars[i].getSup() - vars[i].getInf() + 1;
                if (vars[i].getDomainSize() > maxdszise) {
                    maxdszise = vars[i].getDomainSize();
                }
                if (vars[i].getDomainSize() > 1) nbnoninstvar++;
                holes |= 0.7 * span > vars[i].getDomainSize();
            }

            if (vars.length <= 3) {//very small cliques
                return new BoundAllDiff(vars, false);
            } else if (boundOnly) {
                return new BoundAllDiff(vars,true);
            } else if (holes || (maxdszise <= 30 &&
                      (vars.length <= 10 ||
                      (nbnoninstvar < vars.length && nbnoninstvar < 20)))) {
                //clique containing relatively small domains (less than 30) and
                //instantiated variables (so less than 20 real variables)
                return new AllDifferent(vars);
            }
            //return new AllDifferent(vars);
            return new BoundAllDiff(vars,true);
    }
}
