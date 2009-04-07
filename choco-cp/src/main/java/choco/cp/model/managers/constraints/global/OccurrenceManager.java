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
import choco.cp.solver.constraints.global.Occurrence;
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
 * A manager to build new all occurence constraints
 */
public class OccurrenceManager extends IntConstraintManager {

    private static final int NOR = 0;
    private static final int MIN = -1;
    private static final int MAX = 1;

    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
        if(solver instanceof CPSolver){
            if(parameters instanceof Integer){
                int type = (Integer)parameters;
                IntegerVariable[] vars = new IntegerVariable[variables.length-1];
                vars[vars.length-1] = (IntegerVariable)variables[1];
                System.arraycopy(variables, 2, vars, 0, vars.length-1);
                if(type == NOR){
                    return new Occurrence(solver.getVar(vars), ((IntegerConstantVariable)variables[0]).getValue(), true, true);
                }
                if(type == MIN){
                    return new Occurrence(solver.getVar(vars), ((IntegerConstantVariable)variables[0]).getValue(), true, false);
                }
                if(type == MAX){
                    return new Occurrence(solver.getVar(vars), ((IntegerConstantVariable)variables[0]).getValue(), false, true);
                }
            }
        }
        if(Choco.DEBUG){
            System.err.println("Could not found an implementation of occurence !");
        }
        return null;
    }
}