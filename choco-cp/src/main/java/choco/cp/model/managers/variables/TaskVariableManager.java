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

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.scheduling.TaskVar;

/* User:    charles
 * Date:    21 août 2008
 */
public class TaskVariableManager implements VariableManager {


    /**
     * Build a set variable for the given solver
     * @param solver
     * @param var
     * @return a set variable
     */
    public Var makeVariable(Solver solver, Variable var) {
        if(solver instanceof CPSolver){
            TaskVariable tv = (TaskVariable)var;
            TaskVar stv = solver.createTaskVar(tv.getName(), solver.getVar(tv.start()),
                    solver.getVar(tv.end()), solver.getVar(tv.duration()));
            return stv;
        }
        if(Choco.DEBUG){
            LOGGER.severe("Count not found implementation for SetVariable !");
            System.exit(-1);
        }
        return null;
    }

    /**
     * Build a expression node
     *
     * @param solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] vars) {
        return null;
    }
}
