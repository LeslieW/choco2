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
import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 août 2008
 * Time: 11:39:38
 */
public class ReifiedManager extends IntConstraintManager {

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
            Constraint[] constraints = (Constraint[])((Object[])parameters)[1];
            if(constraints.length == 1){
                Constraint c = constraints[0];
                boolean decomp = false;
                if (c.getOptions().contains("cp:decomp")) {
                    decomp = true;
                }
                SConstraint[] ct = ((CPSolver)solver).makeSConstraintAndOpposite(c, decomp);
                if(!(ct[0] instanceof AbstractIntSConstraint && ct[1] instanceof AbstractIntSConstraint)){
                    throw new UnsupportedOperationException("reifiedIntConstraint cannot be used without IntConstraint");
                }
                return new ReifiedIntSConstraint(solver.getVar((IntegerVariable)variables[0]), (AbstractIntSConstraint)ct[0], (AbstractIntSConstraint)ct[1]);
            }else{
                Constraint c = constraints[0];
                Constraint oppc = constraints[1];
                boolean decomp = false;
                if (c.getOptions().contains("cp:decomp")) {
                    decomp = true;
                }
                SConstraint ct = ((CPSolver)solver).makeSConstraint(c, decomp);
                SConstraint oppct = ((CPSolver)solver).makeSConstraint(oppc, decomp);
                if(!((ct instanceof AbstractIntSConstraint) && (oppct instanceof AbstractIntSConstraint))){
                    throw new UnsupportedOperationException("reifiedIntConstraint cannot be used without IntConstraint");
                }
                return new ReifiedIntSConstraint(solver.getVar((IntegerVariable)variables[0]), (AbstractIntSConstraint)ct, (AbstractIntSConstraint)oppct);
            }
        }
        if (Choco.DEBUG) {
            throw new RuntimeException("Could not found an implementation of reified !");
        }
        return null;
    }
}
