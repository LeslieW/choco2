/* ************************************************
 *           _       _                            *
 *          |  �(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.cp.model.managers;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.ConstraintLeaf;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;

import java.util.List;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* A constraint manager for mixed variable type managers (like EqManager).
* It can deal with different types of variable.
*/
public abstract class MixedConstraintManager  extends ConstraintManager<Variable> {

    /**
     * Build a constraint and its opposite for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters
     * @param options
     * @return array of 2 SConstraint object, the constraint and its opposite
     */
    @Override
    public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, List<String> options) {
        SConstraint c = makeConstraint(solver, variables, parameters, options);
        SConstraint opp = c.opposite(solver);
        return new SConstraint[]{c, opp};
    }

    /**
     * @param options : the set of options on the constraint (Typically the level of consistency)
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    @Override
    public int[] getFavoriteDomains(List<String> options) {
        return ConstraintManager.getACFavoriteIntDomains();
    }

    /**
     * Build a expression node
     *
     * @param solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return
     */
    @Override
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        return new ConstraintLeaf(((CPSolver)solver).makeSConstraint(cstrs[0]), null);
    }
}
