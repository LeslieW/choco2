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

import choco.cp.solver.CPSolver;
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
public class NotNode extends AbstractBoolNode implements BoolNode {

	public NotNode(INode[] subt) {
		super(subt, NodeType.NOT);
	}

	public boolean checkTuple(int[] tuple) {
		return !((BoolNode) subtrees[0]).checkTuple(tuple);
	}

	@Override
	public IntDomainVar extractResult(Solver s) {
		IntDomainVar vs = subtrees[0].extractResult(s);
		IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
		s.post(s.neq(v,vs));
		return v;
	}

  public SConstraint extractConstraint(Solver s) {
    IntDomainVar vs = subtrees[0].extractResult(s);
    return s.eq(((CPSolver)s).makeConstantIntVar(0), vs);
  }

  @Override
	public String pretty() {
        return "(!"+subtrees[0].pretty()+")";
    }
}
