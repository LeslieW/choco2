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
package choco.cp.model.managers.variables;

import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ExpressionManager;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;

/* User:    charles
 * Date:    21 août 2008
 */
public class IntegerExpressionManager implements ExpressionManager {


    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] variables) {
        if(solver instanceof CPSolver){
            if(variables.length == 1){
                IntegerExpressionVariable var = variables[0];
                return var.getExpressionManager().makeNode(solver, cstrs, var.getExpVariables());
            }
        }
        return null;
    }
}
