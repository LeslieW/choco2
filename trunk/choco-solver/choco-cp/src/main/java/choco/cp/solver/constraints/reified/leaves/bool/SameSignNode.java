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

import choco.cp.solver.constraints.integer.SignOp;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.ArithmNode;
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
public class SameSignNode extends AbstractBoolNode implements BoolNode {


	public SameSignNode(INode[] subt) {
		super(subt, NodeType.SAMESIGN);
	}

	public boolean checkTuple(int[] tuple) {
		int val1 = ((ArithmNode) subtrees[0]).eval(tuple);
        int val2 = ((ArithmNode) subtrees[1]).eval(tuple);
        if (val1 == 0 || val2 == 0) return true;
        boolean b1 = (val1 > 0);
		boolean b2 = (val2 > 0);
		return (b1==b2);
	}

  public SConstraint extractConstraint(Solver s) {
		IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = subtrees[1].extractResult(s);
		return new SignOp(v1,v2, true);
  }


  @Override
	public String pretty() {
		return "(("+subtrees[0].pretty()+">0)=("+subtrees[1].pretty()+">0))";
	}
}