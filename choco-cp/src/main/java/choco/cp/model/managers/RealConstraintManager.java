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

import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.real.RealVar;

import java.util.HashSet;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 8, 2008
 * Since : Choco 2.0.0
 *
 */
public abstract class RealConstraintManager extends ConstraintManager<RealVariable> {

    /**
     * @param options the set of options on the constraint (Typically the level of consistency)
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    public int[] getFavoriteDomains(HashSet<String> options) {
        return new int[]{RealVar.BOUNDS};
    }

    /**
     * Build a expression node
     *
     * @param solver
     * @param vars   variables
     * @return
     */
    public abstract RealExp makeRealExpression(Solver solver, RealExpressionVariable... vars);


    public final RealExp getRealExp(Solver s, RealExpressionVariable rev){
        if(rev.getVariableType() == VariableType.CONSTANT_DOUBLE){
            return (RealVar)s.getVar(rev);
        }else if(rev.getVariableType() == VariableType.REAL){
            return (RealVar)s.getVar(rev);
        }else if(rev.getVariableType() == VariableType.REAL_EXPRESSION){
            return ((RealConstraintManager)rev.getRcm()).makeRealExpression(s, rev.getVariables());
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
    public SConstraint[] makeConstraintAndOpposite(Solver solver, RealVariable[] variables, Object parameters, HashSet<String> options) {
        SConstraint c = makeConstraint(solver, variables, parameters, options);
        SConstraint opp = c.opposite();
        return new SConstraint[]{c, opp};
    }
}
