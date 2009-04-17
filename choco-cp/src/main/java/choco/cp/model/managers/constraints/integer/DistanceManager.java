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
package choco.cp.model.managers.constraints.integer;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.DistanceXYC;
import choco.cp.solver.constraints.integer.DistanceXYZ;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

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
 * A manager to build new all distance constraints
 */
public class DistanceManager extends IntConstraintManager {

    private static final int NEQ = 3;


    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {
            if (parameters instanceof Integer) {
                int type = (Integer) parameters;
                if (variables.length == 3) {
                    return new DistanceXYC(solver.getVar((IntegerVariable) variables[0]),
                            solver.getVar((IntegerVariable) variables[1]),
                            ((IntegerConstantVariable) variables[2]).getValue(), type);
                } else {
                    if (type != NEQ) {
                        return new DistanceXYZ(solver.getVar((IntegerVariable) variables[0]),
                                solver.getVar((IntegerVariable) variables[1]),
                                solver.getVar((IntegerVariable) variables[2]),
                                ((IntegerConstantVariable) variables[3]).getValue(),
                                type);
                    }
                }
            }
        }
        if (Choco.DEBUG) {
            LOGGER.severe("Could not found an implementation of distance !");
        }
        return null;
    }
}
