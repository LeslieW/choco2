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
import choco.kernel.model.variables.integer.IntegerConstantVariable;
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
 * A manager to build new all distance constraints
 */
public class DistanceManager extends IntConstraintManager {

    private static final int NEQ = 3;


    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {
            if (parameters instanceof Integer) {
                int type = (Integer) parameters;
                if (variables.length == 3) {
                    return new DistanceXYC(solver.getVar(variables[0]),
                            solver.getVar(variables[1]),
                            ((IntegerConstantVariable) variables[2]).getValue(), type);
                } else {
                    if (type != NEQ) {
                        return new DistanceXYZ(solver.getVar(variables[0]),
                                solver.getVar(variables[1]),
                                solver.getVar(variables[2]),
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
    public SConstraint[] makeConstraintAndOpposite(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
        SConstraint[] cs = new SConstraint[2];
        if (solver instanceof CPSolver) {
            if (parameters instanceof Integer) {
                int type = (Integer) parameters;
                if (variables.length == 3) {
                    return super.makeConstraintAndOpposite(solver, variables, parameters, options);
                } else {
                    IntDomainVar Y;
                    final IntDomainVar X = solver.getVar(variables[2]);
                    // Introduces a intermediary variable
                    if(X.hasBooleanDomain()){
                        Y = solver.createBooleanVar("Y_opp");
                    }else if(X.hasEnumeratedDomain()){
                        Y = solver.createEnumIntVar("Y_opp", X.getInf(), X.getSup());
                    }else{
                        Y = solver.createBoundIntVar("Y_opp", X.getInf(), X.getSup());
                    }
                    if (type != NEQ) {
                        solver.post(new DistanceXYZ(solver.getVar(variables[0]),
                                solver.getVar(variables[1]),
                                Y,
                                ((IntegerConstantVariable) variables[3]).getValue(),
                                type));
                    }
                    cs[0] = solver.eq(Y, X);
                    cs[1] = solver.neq(Y, X);
                }
                return cs;
            }
        }
        if (Choco.DEBUG) {
            LOGGER.severe("Could not found an implementation of distance !");
        }
        return null;
    }
}
