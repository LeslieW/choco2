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

import choco.Choco;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.ArithmNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 5 août 2008
 * Since : Choco 2.0.0
 *
 */
public class ScalarNode extends INode implements ArithmNode {
    protected int[] coeffs;

    public ScalarNode(INode[] subt) {
        super(subt, NodeType.SCALAR);
        coeffs = new int[subt.length];
        Arrays.fill(coeffs, 1);
    }

    public ScalarNode(INode[] subt, int[] coeffs) {
        super(subt, NodeType.SCALAR);
        this.coeffs = coeffs;
        if (Choco.DEBUG) {
            if (subt.length != coeffs.length) {
                LOGGER.severe("There should be as many coeffs than variables in a sum/scalar !");
                System.exit(-1);
            }
        }
    }

    public int eval(int[] tuple) {
        int sum = 0;
        int i = 0;
        for (INode t : subtrees) {
            sum += coeffs[i++] * ((ArithmNode) t).eval(tuple);
        }
        return sum;
    }

    public IntDomainVar extractResult(Solver s) {
        IntDomainVar[] vars = new IntDomainVar[subtrees.length];
        int lb = 0, ub = 0;
        for (int i = 0; i < vars.length; i++) {
            vars[i] = subtrees[i].extractResult(s);
            lb += coeffs[i] >= 0 ? vars[i].getInf() * coeffs[i] : vars[i].getSup() * coeffs[i];
            ub += coeffs[i] >= 0 ? vars[i].getSup() * coeffs[i] : vars[i].getInf() * coeffs[i];
        }
        IntDomainVar sum = s.createBoundIntVar("iScalar", lb, ub);
        s.post(s.eq(sum, s.scalar(coeffs, vars)));
        return sum;
    }

    public String pretty() {
        StringBuffer st = new StringBuffer("(");
        int i = 0;
        for (INode t : subtrees) {
            st.append(t.pretty());
            i++;
            if (i < subtrees.length) {
                st.append("+");
            }
        }
        st.append(")");
        return st.toString();
    }

    public boolean isALinearTerm() {
       for (int i = 0; i < subtrees.length; i++) {
           if (!subtrees[i].isALinearTerm()) return false;
       }
        return true;
    }

    public int[] computeLinearExpr(int scope) {
        int[] cToRet = new int[scope + 1];
        for (int i = 0; i < coeffs.length; i++) {
            int[] c = subtrees[i].computeLinearExpr(scope);
            for (int j = 0; j < c.length; j++) {
                //cToRet[j] = (c[j] != 0) ? coeffs[i] * c[j] : cToRet[j];
                cToRet[j] += coeffs[i] * c[j];
            }
        }
        return cToRet;
    }

}

