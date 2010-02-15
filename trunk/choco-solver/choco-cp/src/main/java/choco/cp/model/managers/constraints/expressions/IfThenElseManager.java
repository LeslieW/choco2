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

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.arithm.IfThenElseNode;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.ExpressionManager;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;

/*
 * User:    charles
 * Date:    21 août 2008
 */
public class IfThenElseManager implements ExpressionManager {

    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param cstrs  constraints (can be null)
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        if(solver instanceof CPSolver){
            CPSolver s = (CPSolver)solver;
            INode[] nt = new INode[3];
            if(cstrs[0].getConstraintType().equals(ConstraintType.IFTHENELSE)){
                MetaConstraint mc = (MetaConstraint)cstrs[0];
                for (int i = 0; i < mc.getConstraints().length; i++) {
                    Constraint c = mc.getConstraints()[i];
                    IntegerExpressionVariable[] ev = new IntegerExpressionVariable[c.getNbVars()];
                    for(int j = 0; j < c.getNbVars(); j++){
                        ev[j]  = (IntegerExpressionVariable)c.getVariables()[j];
                    }
                    nt[i] = c.getExpressionManager().makeNode(solver, new Constraint[]{c}, ev);
                }
                return new IfThenElseNode(nt);
            }else /*if(cstrs[0] instanceof ComponentConstraint)*/{
                IntegerExpressionVariable[] ev = new IntegerExpressionVariable[cstrs[0].getNbVars()];
                for(int j = 0; j < cstrs[0].getNbVars(); j++){
                    ev[j]  = (IntegerExpressionVariable)cstrs[0].getVariables()[j];
                }
                nt[0] = cstrs[0].getExpressionManager().makeNode(solver, new Constraint[]{cstrs[0]}, ev);
                nt[1] = vars[0].getExpressionManager().makeNode(s, vars[0].getConstraints(), vars[0].getVariables());
                nt[2] = vars[1].getExpressionManager().makeNode(s, vars[1].getConstraints(), vars[1].getVariables());
                return new IfThenElseNode(nt);
            }
        }
        throw new ModelException("Could not found a node manager in " + this.getClass() + " !");
    }
}
