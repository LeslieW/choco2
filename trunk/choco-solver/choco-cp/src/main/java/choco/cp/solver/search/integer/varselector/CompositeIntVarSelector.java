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
package choco.cp.solver.search.integer.varselector;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.ConstraintSelector;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.search.integer.HeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class that composes two heuristics for selecting a variable:
 * a first heuristic is appled for selecting a constraint.
 * from that constraint a second heuristic is applied for selecting the variable
 */
public class CompositeIntVarSelector extends AbstractIntVarSelector implements VarSelector {
    protected ConstraintSelector cs;
    protected HeuristicIntVarSelector cvs;

    public CompositeIntVarSelector(ConstraintSelector cs, HeuristicIntVarSelector cvs) {
        this.cs = cs;
        this.cvs = cvs;
    }

    public IntDomainVar selectIntVar() throws ContradictionException {
        SConstraint c = cs.getConstraint();
        if (c == null) return null;
        else return cvs.getMinVar((IntSConstraint) c);
    }

    public ConstraintSelector getCs() {
        return cs;
    }

    public HeuristicIntVarSelector getCvs() {
        return cvs;
    }
}
