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
package choco.cp.solver.constraints.reified.leaves;

import choco.cp.solver.CPSolver;
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
 * User: charles
 * Date: 22 mai 2008
 * Since : Choco 2.0.0
 *
 */
public final class ConstraintLeaf extends INode implements BoolNode {

    protected AbstractIntSConstraint c;
	protected AbstractIntSConstraint oppositec;
	protected int[] idxtuple;
	protected int[] tup;


    public ConstraintLeaf(SConstraint c) {
        super(NodeType.CONSTRAINTLEAF);
        this.c = (AbstractIntSConstraint) c;
	    idxtuple = new int[c.getNbVars()];
	    tup = new int[c.getNbVars()];
    }

	public ConstraintLeaf(SConstraint c, SConstraint oppositec) {
        super(NodeType.CONSTRAINTLEAF);
	    this.c = (AbstractIntSConstraint) c;
	    idxtuple = new int[c.getNbVars()];
	    tup = new int[c.getNbVars()];
        this.oppositec = (AbstractIntSConstraint) oppositec;
	}

    public boolean checkTuple(int[] tuple) {
        setTuple(tuple);
	    return c.isSatisfied(tup);
    }

  public void setTuple(int[] tuple) {
		for (int i = 0; i < tup.length; i++) {
			tup[i] = tuple[idxtuple[i]];
		}
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
		if (oppositec != null)
			s.post(((CPSolver)s).reifiedIntConstraint(v,c,oppositec));
		else s.post(((CPSolver)s).reifiedIntConstraint(v,c));
		return v;
	}

  public SConstraint extractConstraint(Solver s) {
    return c;
  }

  public boolean isReified() {
		return false;
	}

    public int getNbSubTrees() {
        return 0;
    }

    public boolean isDecompositionPossible() {
		return true;
	}

	public IntDomainVar[] getScope(Solver s) {
		IntDomainVar[] vars = new IntDomainVar[c.getNbVars()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = c.getVar(i);
		}
		return vars;
	}

	public void setIndexes(IntDomainVar[] vs) {
		idxtuple = new int[c.getNbVars()];
		for (int i = 0; i < c.getNbVars(); i++) {
			IntDomainVar v = c.getVar(i);
			for (int j = 0; j < vs.length; j++) {
				if (vs[j].equals(v)) {
			        idxtuple[i] = j;
					break;
				}
			}
		}
	}

    public String pretty() {
        return c.pretty();
    }

    public int countNbVar() {
        return c.getNbVars();
    }

}
