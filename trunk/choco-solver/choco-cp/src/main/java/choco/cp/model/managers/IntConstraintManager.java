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
package choco.cp.model.managers;

import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import choco.cp.solver.constraints.reified.leaves.ConstraintLeaf;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;

import java.util.Set;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 8, 2008
 * Since : Choco 2.0.0
 *
 */
public abstract class IntConstraintManager extends ConstraintManager<IntegerVariable> {


    /**
     * @param options : the set of options on the constraint (Typically the level of consistency)
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    public int[] getFavoriteDomains(Set<String> options) {
        return getACFavoriteIntDomains();
    }


    public static void readSetting(Set<String> source, Set<SettingType> dest, SettingType setting) {
		if(source.contains(setting.getOptionName()) ) {dest.add(setting);}
	}

    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param cstrs  constraints (can be null)
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        return new ConstraintLeaf(((CPSolver)solver).makeSConstraint(cstrs[0]), null);
    }

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
    public SConstraint[] makeConstraintAndOpposite(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
        SConstraint c = makeConstraint(solver, variables, parameters, options);
        SConstraint opp = c.opposite(solver);
        return new SConstraint[]{c, opp};
    }
}