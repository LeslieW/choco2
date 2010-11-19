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
package choco.cp.solver.constraints.reified.leaves.arithm;

import choco.kernel.solver.constraints.reified.ArithmNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 14 sept. 2010
 */
public class DoubleNode extends INode implements ArithmNode {
    public DoubleNode(INode[] subt) {
        super(subt, NodeType.CUSTOM);
    }

    @Override
    public String pretty() {
        return "("+subtrees[0].pretty()+"*2)";
    }
    @Override
    public int eval(int[] tuple) {
        return ((ArithmNode) subtrees[0]).eval(tuple) * 2;
    }
}
