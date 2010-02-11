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

import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.constraints.reified.BoolNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractBoolNode extends INode implements BoolNode {
    public AbstractBoolNode(INode[] subt, NodeType type) {
        super(subt, type);
    }

    @Override
    public IntDomainVar extractResult(Solver s) {
        IntDomainVar var = s.createBooleanVar(StringUtils.randomName());
        s.post(new ReifiedIntSConstraint(var, (AbstractIntSConstraint) extractConstraint(s), s));
        return var;
    }

  /**
   * Extracts the sub constraint without reifying it !
   *
   * @param s solver
   * @return SConstraint
   */
  public abstract SConstraint extractConstraint(Solver s);
}
