/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
*                                                *
*     Choco is a java library for constraint     *
*     satisfaction problems (CSP), constraint    *
*     programming (CP) and explanation-based     *
*     constraint solving (e-CP). It is built     *
*     on a event-based propagation mechanism     *
*     with backtrackable structures.             *
*                                                *
*     Choco is an open-source software,          *
*     distributed under a BSD licence            *
*     and hosted by sourceforge.net              *
*                                                *
*     + website : http://choco.emn.fr            *
*     + support : choco@emn.fr                   *
*                                                *
*     Copyright (C) F. Laburthe,                 *
*                   N. Jussien    1999-2010      *
**************************************************/
package choco.cp.model.managers.operators;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.arithm.DoubleNode;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ExpressionManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 14 sept. 2010
 */
public class DoubleManager implements ExpressionManager {
    @Override
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        if(solver instanceof CPSolver){
            CPSolver s = (CPSolver)solver;
            if(vars.length == 1){
                INode[] nodes = new INode[vars.length];
                for(int v = 0; v < vars.length; v++){
                    nodes[v] = vars[v].getExpressionManager().makeNode(s, vars[v].getConstraints(), vars[v].getVariables());
                }
                return new DoubleNode(nodes);
            }
        }
        throw new ModelException("Could not found a node manager in " + this.getClass() + " !");
    }
}
