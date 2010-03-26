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
public final class ConstantLeaf extends INode implements ArithmNode {

	public int cste;

    /**
     * To avoid useless array creation
     */
    private static IntDomainVar[] emptyArrayIS = new IntDomainVar[0];

    private static IntegerVariable[] emptyArrayIM = new IntegerVariable[0];

    public ConstantLeaf(int cste) {
		super(NodeType.CONSTANTLEAF);
		this.cste = cste;
	}

	public int eval(int[] tuple) {
		return cste;
	}

	public void setIndexes(IntDomainVar[] vs) {
		//nothing to do here as there is no variable
	}

	public final IntDomainVar[] getScope(Solver s) {
		return emptyArrayIS;
	}

	public IntegerVariable[] getModelScope() {
		return emptyArrayIM;
	}


	public boolean isDecompositionPossible() {
		return true;
	}

	public boolean isReified() {
		return false;
	}

	public IntDomainVar extractResult(Solver s) {
		return ((CPSolver)s).makeConstantIntVar(cste);
	}

    public String pretty() {
        return ""+cste;
    }

    public int getConstant() {
        return cste;
    }

    public boolean isCsteEqualTo(int a) {
		return cste == a;
	}

    public boolean isAConstant() {
        return true;
    }

    public boolean isALinearTerm() {
        return true;
    }

    public int countNbVar() {
        return 0;
    }

    public int getNbSubTrees() {
        return 0;
    }

    public int[] computeLinearExpr(int scope) {
        int[] coeffs = new int[scope + 1];
        coeffs[scope] = cste;
        return coeffs;
    }

}
