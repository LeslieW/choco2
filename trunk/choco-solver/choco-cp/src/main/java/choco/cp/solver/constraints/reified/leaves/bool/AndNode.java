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

import choco.cp.solver.constraints.global.Occurrence;
import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
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
public class AndNode extends AbstractBoolNode{


	public AndNode(INode... subt) {
		super(subt, NodeType.AND);
	}

	public boolean checkTuple(int[] tuple) {
        for (INode subtree : subtrees) {
            if (!((BoolNode) subtree).checkTuple(tuple)) {
                return false;
            }
        }
		return true;
	}

	@Override
	public IntDomainVar extractResult(Solver s) {
		IntDomainVar[] vs = new IntDomainVar[subtrees.length];
		IntDomainVar sand = s.createBoundIntVar(StringUtils.randomName(),0,subtrees.length);
		IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
		for (int i = 0; i < vs.length; i++) {
			vs[i] = subtrees[i].extractResult(s);
		}
		s.post(s.eq(s.sum(vs),sand));
		s.post(new ReifiedIntSConstraint(v,(AbstractIntSConstraint)s.eq(sand,subtrees.length)));
		return v;
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
        vs[vs.length - 1] = new IntDomainVarImpl(s, StringUtils.randomName(), IntDomainVar.BOUNDS, subtrees.length, subtrees.length);
        return new Occurrence(vs, 1, true, true);
    }

    @Override
	public boolean isReified() {
		return true;
	}

	@Override
	public String pretty() {
        StringBuffer st = new StringBuffer("(");
        int i = 0;
        while (i < subtrees.length-1) {
            st.append(subtrees[i].pretty()).append(" and ");
            i++;
        }
        st.append(subtrees[i].pretty()).append(")");
        return st.toString();
    }
}
