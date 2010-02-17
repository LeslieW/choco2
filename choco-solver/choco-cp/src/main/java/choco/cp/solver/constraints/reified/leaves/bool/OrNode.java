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

import choco.cp.solver.constraints.integer.bool.BinOr;
import choco.cp.solver.constraints.integer.bool.LargeOr;
import choco.cp.solver.constraints.integer.channeling.ReifiedLargeOr;
import choco.kernel.common.util.tools.ArrayUtils;
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
public class OrNode extends AbstractBoolNode implements BoolNode {

    public OrNode(INode... subt) {
        super(subt, NodeType.OR);
    }

    public boolean checkTuple(int[] tuple) {
        for (INode subtree : subtrees) {
            if (((BoolNode) subtree).checkTuple(tuple)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IntDomainVar extractResult(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
        }
        if (vs.length > 1) {
            IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
            IntDomainVar[] vars = ArrayUtils.append(new IntDomainVar[]{v}, vs);
            s.post(new ReifiedLargeOr(vars, s.getEnvironment()));
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
            return new BinOr(vs[0],vs[1]);
        } else {
            return new LargeOr(vs);
        }
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
            st.append(subtrees[i].pretty()).append(" or ");
            i++;
        }
        st.append(subtrees[i].pretty()).append(")");
        return st.toString();
    }

}
