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

import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.ArithmNode;
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
public class VariableLeaf extends INode implements ArithmNode {

	public int idx;

	public IntDomainVar var;

	public IntegerVariable ivar;

	public VariableLeaf(IntDomainVar var) {
		super(NodeType.VARIABLELEAF);
		this.var = var;
	}

	public VariableLeaf(IntegerVariable ivar) {
		super(NodeType.VARIABLELEAF);
		this.ivar = ivar;
	}


    public int getIdx() {
        return idx;    
    }

    public void setSolverVar(IntDomainVar v) {
		var = v;
	}

	public int eval(int[] tuple) {
		return tuple[idx];
	}

	public void setIndexes(IntDomainVar[] vs) {
		for (int i = 0; i < vs.length; i++) {
			if (vs[i] == var) {
				idx = i;break;
			}
		}
	}

	public final IntDomainVar[] getScope(Solver s) {
		if (ivar != null) var = s.getVar(ivar);
		return new IntDomainVar[]{var};
	}

	public IntegerVariable[] getModelScope() {
		return new IntegerVariable[]{ivar};	
	}

	public boolean isDecompositionPossible() {
		return true;
	}

	public boolean isReified() {
		return false;
	}
	
	public IntDomainVar extractResult(Solver s) {
		return var;
	}

    public int getNbSubTrees() {
        return 0;
    }

    public boolean isAVariable() {
        return true;
    }

    public boolean isBoolean(){
        return var.hasBooleanDomain();
    }

    public boolean isAConstant() {
        return false;
    }

    public String pretty() {
        return var.pretty();
    }

    public int countNbVar() {
        return 1;
    }

    public boolean isALinearTerm() {
        return true;
    }
    
    public int[] computeLinearExpr(int scope) {
        int[] coeffs = new int[scope + 1];
        coeffs[idx] = 1;
        return coeffs;
    }

}
