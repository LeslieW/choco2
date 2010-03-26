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
package choco.cp.solver.constraints.reified.leaves.bool;

import choco.cp.solver.constraints.integer.bool.BinXor;
import choco.cp.solver.constraints.integer.channeling.ReifiedBinXor;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.BoolNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 23 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public final class XorNode extends AbstractBoolNode implements BoolNode {

    public XorNode(INode... subt) {
        super(subt, NodeType.XOR);
    }

    public boolean checkTuple(int[] tuple) {
        return tuple[0] == Math.abs(tuple[1]-1);
    }

    @Override
    public IntDomainVar extractResult(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
        }
        if (vs.length == 1) {
            IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
            s.post(new ReifiedBinXor(v, vs[1], vs[2]));
            return v;
        } else {
            return vs[0];
        }
    }

    public SConstraint extractConstraint(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
        }
        if (subtrees.length == 2) {
            return new BinXor(vs[0],vs[1]);
        }
        return null;
    }

    @Override
    public boolean isReified() {
        return true;
    }


    @Override
    public String pretty() {
        StringBuffer st = new StringBuffer("(");
        int i = 0;
        while (i < subtrees.length - 1) {
            st.append(subtrees[i].pretty()).append(" xor ");
            i++;
        }
        st.append(subtrees[i].pretty()).append(")");
        return st.toString();
    }

}