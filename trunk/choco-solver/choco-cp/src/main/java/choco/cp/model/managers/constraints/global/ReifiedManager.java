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

import choco.Options;
import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.ReifiedFactory;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 août 2008
 * Time: 11:39:38
 */
public final class ReifiedManager extends MixedConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, Set<String> options) {
        if(solver instanceof CPSolver){
            Constraint[] constraints = (Constraint[])((Object[])parameters)[1];
            SConstraint[] cons = new SConstraint[2];
            if(constraints.length == 1){
                Constraint c = constraints[0];
                boolean decomp = false;
                if (c.getOptions().contains(Options.E_DECOMP)) {
                    decomp = true;
                }
                cons = ((CPSolver)solver).makeSConstraintAndOpposite(c, decomp);
            }else{
                Constraint c = constraints[0];
                Constraint oppc = constraints[1];
                boolean decomp = false;
                if (c.getOptions().contains(Options.E_DECOMP)) {
                    decomp = true;
                }
                cons[0] = ((CPSolver)solver).makeSConstraint(c, decomp);
                cons[1] = ((CPSolver)solver).makeSConstraint(oppc, decomp);
            }
            return ReifiedFactory.builder(solver.getVar((IntegerVariable)variables[0]), cons[0], cons[1], solver);
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

}
