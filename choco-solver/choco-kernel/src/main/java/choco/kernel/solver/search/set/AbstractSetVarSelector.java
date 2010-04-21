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
package choco.kernel.solver.search.set;

import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractSetVarSelector extends AbstractSearchHeuristic implements VarSelector<SetVar> {

    /**
     * a specific array of SetVars from which the object seeks the one with smallest domain
     */
    protected final SetVar[] vars;


    public AbstractSetVarSelector(Solver solver) {
        this(solver, VariableUtils.getSetVars(solver));
    }


    public AbstractSetVarSelector(Solver solver, SetVar[] vars) {
        super(solver);
        this.vars = vars;
    }

    public SetVar selectVar() {
        int min = Integer.MAX_VALUE;
        SetVar v0 = null;
        final int n = vars.length;
        for (int i = 0; i < n; i++) {
            SetVar v = vars[i];
            if (!v.isInstantiated()) {
                int domSize = getHeuristic(v);
                if (domSize < min) {
                    min = domSize;
                    v0 = v;
                }
            }
        }
        return v0;
    }

    /**
     * Get decision vars
     *
     * @return decision vars
     */
    public SetVar[] getVars() {
        return vars;
    }

    /**
     * Set decision vars
     *
     * @return decision vars
     */
    @Deprecated
    public void setVars(SetVar[] vars) {
        throw new UnsupportedOperationException("setVars is final");
    }

    public abstract int getHeuristic(SetVar v);

}
