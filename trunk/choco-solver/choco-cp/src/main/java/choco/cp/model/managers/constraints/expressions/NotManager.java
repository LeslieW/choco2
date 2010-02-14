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
package choco.cp.model.managers.constraints.expressions;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.bool.*;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;

import java.util.Set;

/*
 * User:    charles
 * Date:    22 août 2008
 */
public class NotManager extends IntConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
        if(solver instanceof CPSolver){
            Constraint[] constraints = (Constraint[])((Object[])parameters)[1];
            if(constraints.length == 1){
                Constraint c = constraints[0];
                boolean decomp = false;
                if (c.getOptions().contains("cp:decomp")) {
                    decomp = true;
                }
                SConstraint[] ct = ((CPSolver)solver).makeSConstraintAndOpposite(c, decomp);
                return ct[1];
            }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param cstrs  constraints (can be null)
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] vars) {
        MetaConstraint mc = (MetaConstraint)cstrs[0];
        Constraint cons = mc.getConstraints()[0];
        if(cons instanceof ComponentConstraint){
            return detectSimpleNode(solver, cons);
        }else{
            INode[] nt = new INode[mc.getConstraints().length];
            for (int i = 0; i < mc.getConstraints().length; i++) {
                Constraint c = mc.getConstraints()[i];
                IntegerExpressionVariable[] ev = new IntegerExpressionVariable[c.getNbVars()];
                for(int j = 0; j < c.getNbVars(); j++){
                    ev[j]  = (IntegerExpressionVariable)c.getVariables()[j];
                }
                nt[i] = c.getExpressionManager().makeNode(solver, new Constraint[]{c}, ev);
            }
            return new NotNode(nt);
        }
    }

    /**
     * Detect simple unary constraint that can be replaced by efficient one
     * @param solver
     * @param cons
     * @return
     */
    private INode detectSimpleNode(Solver solver, Constraint cons) {
        ComponentConstraint cc = (ComponentConstraint) cons;
        ConstraintType type = cc.getConstraintType();

        switch (type) {
            case EQ:
                return new NeqNode(buildVariableNodes(solver, cc));
            case NEQ:
                return new EqNode(buildVariableNodes(solver, cc));
            case GEQ:
                return new LtNode(buildVariableNodes(solver, cc));
            case LEQ:
                return new GtNode(buildVariableNodes(solver, cc));
            case GT:
                return new LeqNode(buildVariableNodes(solver, cc));
            case LT:
                return new GeqNode(buildVariableNodes(solver, cc));
            case SIGNOP:
                if (cc.getParameters() == Boolean.FALSE) {
                    return new SameSignNode(buildVariableNodes(solver, cc));
                }
                if (cc.getParameters() == Boolean.TRUE) {
                    return new OppSignNode(buildVariableNodes(solver, cc));
                }
            default:
                INode[] nt = new INode[1];
                IntegerExpressionVariable[] ev = new IntegerExpressionVariable[cons.getNbVars()];
                for (int j = 0; j < cons.getNbVars(); j++) {
                    ev[j] = (IntegerExpressionVariable) cons.getVariables()[j];
                }
                nt[0] = cons.getExpressionManager().makeNode(solver, new Constraint[]{cons}, ev);
                return new NotNode(nt);
        }
    }

    /**
     * Build variable nodes for Component constraint
     * @param solver the solver
     * @param cc the component constraint
     * @return array of variable leaves
     */
    private static INode[] buildVariableNodes(Solver solver, ComponentConstraint cc){
        INode[] nt = new INode[cc.getVariables().length];
        for (int i = 0; i < cc.getVariables().length; i++) {
            IntegerExpressionVariable v = (IntegerExpressionVariable) cc.getVariable(i);
            nt[i] = v.getExpressionManager().makeNode(solver, v.getConstraints(), v.getExpVariables());
        }
        return nt;
    }

}
