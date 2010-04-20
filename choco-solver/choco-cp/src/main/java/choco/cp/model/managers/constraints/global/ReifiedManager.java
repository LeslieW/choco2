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
import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.cp.solver.constraints.set.ReifiedIntSetSConstraint;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;

import java.text.MessageFormat;
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
            if(constraints.length == 1){
                Constraint c = constraints[0];
                boolean decomp = false;
                if (c.getOptions().contains(Options.E_DECOMP)) {
                    decomp = true;
                }
                SConstraint[] ct = ((CPSolver)solver).makeSConstraintAndOpposite(c, decomp);
                switch (switcher(ct[0], ct[1])) {
                    case INT:
                        return new ReifiedIntSConstraint(solver.getVar((IntegerVariable)variables[0]), (AbstractIntSConstraint)ct[0], (AbstractIntSConstraint)ct[1]);
                    case OTHER:
                        return new ReifiedIntSetSConstraint(solver.getVar((IntegerVariable)variables[0]), (AbstractSConstraint)ct[0], (AbstractSConstraint)ct[1]);
                    default:
                        throw new UnsupportedOperationException(MessageFormat.format("{0} or {1} can not be reified", ct[0].pretty(), ct[1].pretty()));
                }
            }else{
                Constraint c = constraints[0];
                Constraint oppc = constraints[1];
                boolean decomp = false;
                if (c.getOptions().contains(Options.E_DECOMP)) {
                    decomp = true;
                }
                SConstraint ct = ((CPSolver)solver).makeSConstraint(c, decomp);
                SConstraint oppct = ((CPSolver)solver).makeSConstraint(oppc, decomp);
                switch (switcher(ct, oppct)) {
                    case INT:
                        return new ReifiedIntSConstraint(solver.getVar((IntegerVariable)variables[0]), (AbstractIntSConstraint)ct, (AbstractIntSConstraint)oppct);
                    case OTHER:
                        return new ReifiedIntSetSConstraint(solver.getVar((IntegerVariable)variables[0]), (AbstractSConstraint)ct, (AbstractSConstraint)oppct);
                    default:
                        throw new UnsupportedOperationException(MessageFormat.format("{0} or {1} can not be reified", ct.pretty(), oppct.pretty()));
                }
            }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

    private enum Reif {
        INT, REAL, OTHER
    }

    private static Reif switcher(SConstraint cons, SConstraint oppcons){
        SConstraintType c_int = cons.getConstraintType();
        SConstraintType oc_int = oppcons.getConstraintType();
        if(c_int.canBeReified() && oc_int.canBeReified()){
            if(c_int.equals(SConstraintType.INTEGER)
                    && c_int.equals(SConstraintType.INTEGER)  ){
                return Reif.INT;
            }
            return Reif.OTHER;
        }
        return Reif.REAL;
    }
}
