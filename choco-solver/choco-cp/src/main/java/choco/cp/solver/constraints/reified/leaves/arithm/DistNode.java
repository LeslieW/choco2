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
package choco.cp.solver.constraints.reified.leaves.arithm;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.Absolute;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.ArithmNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

import static java.lang.Math.*;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 5 août 2008
 * Since : Choco 2.0.0
 *
 */
public class DistNode extends INode implements ArithmNode {

	public DistNode(INode[] subt) {
		super(subt, NodeType.DIST);
	}

	public int eval(int[] tuple) {
		return abs(((ArithmNode) subtrees[0]).eval(tuple)-((ArithmNode) subtrees[1]).eval(tuple));
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = subtrees[1].extractResult(s);
		IntDomainVar v3;
        IntDomainVar v4;
        int a = v1.getInf() - v2.getInf();
		int b = v1.getInf() - v2.getSup();
		int c = v1.getSup() - v2.getInf();
		int d = v1.getSup() - v2.getSup();
		int lb = min(min(min(a,b),c),d);
		int ub = max(max(max(a,b),c),d);
        if(lb==0 && ub ==1){
            v3 = s.createBooleanVar(StringUtils.randomName());
            v4 = s.createBooleanVar(StringUtils.randomName());
        }else
		if (v1.hasEnumeratedDomain() && v2.hasEnumeratedDomain()) {
			v3 = s.createEnumIntVar(StringUtils.randomName(), lb, ub);
            v4 = s.createEnumIntVar(StringUtils.randomName(), lb, ub);
        } else {
			v3 = s.createBoundIntVar(StringUtils.randomName(), lb, ub);
            v4 = s.createBoundIntVar(StringUtils.randomName(), lb, ub);
        }
		s.post(s.eq(v3, ((CPSolver)s).minus(v1,v2)));
        s.post(new Absolute(v4, v3));
        return v4;
	}

    public String pretty() {
        return "(|"+subtrees[0].pretty()+"-"+subtrees[1].pretty()+"|)";
    }
}
