/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.reified.leaves.bool;

import choco.cp.solver.constraints.global.Occurrence;
import choco.cp.solver.constraints.integer.channeling.ReifiedLargeOr;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.BoolNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 18 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class NorNode extends AbstractBoolNode{

    public NorNode(INode... subt) {
		super(subt, NodeType.NOR);
	}

	public boolean checkTuple(int[] tuple) {
        for (INode subtree : subtrees) {
            if (((BoolNode) subtree).checkTuple(tuple)) {
                return false;
            }
        }
		return true;
	}

	@Override
	public IntDomainVar extractResult(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
        }
        if (vs.length > 1) {
            IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
            IntDomainVar notv = s.createBooleanVar(StringUtils.randomName());
            IntDomainVar[] vars = ArrayUtils.append(new IntDomainVar[]{v}, vs);
            s.post(new ReifiedLargeOr(vars, s.getEnvironment()));
            s.post(s.neq(v,notv));
            return notv;
        } else {
            IntDomainVar notv = s.createBooleanVar(StringUtils.randomName());
            s.post(s.neq(vs[0],notv));
            return notv;
        }
	}

    /**
     * Extracts the sub constraint without reifying it !
     *
     * @param s solver
     * @return the equivalent constraint
     */
    public SConstraint extractConstraint(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length+1];
		for (int i = 0; i < vs.length-1; i++) {
			vs[i] = subtrees[i].extractResult(s);
		}
        vs[vs.length - 1] = new IntDomainVarImpl(s, StringUtils.randomName(), IntDomainVar.ONE_VALUE,
                subtrees.length, subtrees.length);
        return new Occurrence(vs, 0, true, true, s.getEnvironment());
    }

    @Override
	public boolean isReified() {
		return true;
	}

	@Override
	public String pretty() {
        StringBuilder st = new StringBuilder("(");
        int i = 0;
        while (i < subtrees.length-1) {
            st.append(subtrees[i].pretty()).append(" and ");
            i++;
        }
        st.append(subtrees[i].pretty()).append(')');
        return st.toString();
    }
}
