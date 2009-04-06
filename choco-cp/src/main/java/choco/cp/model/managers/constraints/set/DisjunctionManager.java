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
package choco.cp.model.managers.constraints.set;

import choco.Choco;
import choco.cp.model.managers.SetConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.Disjoint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 août 2008
 * Time: 13:09:07
 */
public class DisjunctionManager extends SetConstraintManager{

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {

        if(solver instanceof CPSolver){
            if(parameters == null){
                return new Disjoint(solver.getVar((SetVariable)variables[0]), solver.getVar((SetVariable)variables[1]));
            }
        }

        if(Choco.DEBUG){
            throw new RuntimeException("Could not found implementation for Disjunction !");
        }
        return null;
    }
}
