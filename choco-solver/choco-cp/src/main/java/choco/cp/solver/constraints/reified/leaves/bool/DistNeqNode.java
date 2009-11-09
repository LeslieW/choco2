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

import choco.cp.solver.constraints.integer.DistanceXYZ;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.ArithmNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

import static java.lang.Math.max;
import static java.lang.Math.min;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 23 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public class DistNeqNode extends AbstractBoolNode {


	public DistNeqNode(INode[] subt) {
		super(subt, NodeType.DISTNEQ);
	}

    /**
     * Extracts the sub constraint without reifying it !
     *
     * @param s solver
     * @return Sconstraint
     */
    public SConstraint extractConstraint(Solver s) {
        //TODO : add distanceNEQ constraint with 4 parameters
        IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = subtrees[1].extractResult(s);
        IntDomainVar v3 = subtrees[2].extractResult(s);
        IntDomainVar v4;
        int a = v1.getInf() - v2.getInf();
		int b = v1.getInf() - v2.getSup();
		int c = v1.getSup() - v2.getInf();
		int d = v1.getSup() - v2.getSup();
		int lb = min(min(min(a,b),c),d);
		int ub = max(max(max(a,b),c),d);
        if(lb==0 && ub == 1){
            v4 = s.createBooleanVar(StringUtils.randomName());
        }else
		if (v1.hasEnumeratedDomain() && v2.hasEnumeratedDomain()) {
            v4 = s.createEnumIntVar(StringUtils.randomName(), lb, ub);
        } else {
            v4 = s.createBoundIntVar(StringUtils.randomName(), lb, ub);
        }
        s.post(new DistanceXYZ(v1,v2,v4, 0, 0));
        return s.neq(v3, v4);
    }

    public boolean checkTuple(int[] tuple) {
		return Math.abs(((ArithmNode) subtrees[0]).eval(tuple)-((ArithmNode) subtrees[1]).eval(tuple))
                !=((ArithmNode) subtrees[2]).eval(tuple);
	}

	@Override
	public boolean isDecompositionPossible() {
		return false;
	}

	@Override
	public String pretty() {
        return "(|"+subtrees[0].pretty()+"-"+subtrees[1].pretty()+"|!="+subtrees[2].pretty()+")";
    }
}
