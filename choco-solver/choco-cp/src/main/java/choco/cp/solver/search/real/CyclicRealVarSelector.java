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
package choco.cp.solver.search.real;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.real.AbstractRealVarSelector;
import choco.kernel.solver.search.real.RealVarSelector;
import choco.kernel.solver.variables.real.RealVar;

/**
 * A cyclic variable selector : since a dichotomy algorithm is used, cyclic assiging is nedded for instantiate
 * a real interval variable.
 */
public class CyclicRealVarSelector extends AbstractRealVarSelector implements RealVarSelector {
    protected int current;

    //protected double precision = 1.e-6;

    public CyclicRealVarSelector(Solver solver, final RealVar[] vars) {
        super(solver, vars);
        current = -1;
    }

    public CyclicRealVarSelector(Solver solver) {
        super(solver);
        current = -1;
    }

    public RealVar selectRealVar() {
        int nbvars = vars.length;
        if (nbvars == 0) return null;
        int start = current == -1 ? nbvars - 1 : current;
        int n = (current + 1) % nbvars;
        while (n != start && vars[n].isInstantiated()) {
            n = (n + 1) % nbvars;
        }
        if (vars[n].isInstantiated()) return null;
        current = n;
        return vars[n];
    }
}
