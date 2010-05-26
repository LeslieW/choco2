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
package choco.kernel.solver.constraints.real;

import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;

/* 
 * Created by IntelliJ IDEA.
 * User: FLABURTHE
 * Date: 19 ao�t 2005
 * Since : Choco 2.0.0
 *
 */
public abstract class AbstractBinRealIntSConstraint extends AbstractMixedSRealIntSConstraint {
    protected RealVar v0;
   
    protected IntDomainVar v1;
   
    public AbstractBinRealIntSConstraint(RealVar v0, IntDomainVar v1) {
        super(new Var[]{v0, v1});
        this.v0 = v0;
        this.v1 = v1;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // Variable management
    public RealVar getRealVar(int i) {
        if (i == 0) {
            return v0;
        }
        return null;
    }

    public int getRealVarNb() {
        return 1;
    }

    public IntDomainVar getIntVar(int i) {
        if (i == 0) {
            return v1;
        }
        return null;
    }

    public int getIntVarNb() {
        return 1;
    }

//    public void setVar(int i, Var v) {
//        if (i == 0) {
//            if (v instanceof RealVar) {
//                this.v0 = (RealVar) v;
//            } else {
//                throw new SolverException("BUG in CSP network management: wrong type of Var for setVar");
//            }
//        } else if (i == 1) {
//            if (v instanceof IntDomainVar) {
//                this.v1 = (IntDomainVar) v;
//            } else {
//                throw new SolverException("BUG in CSP network management: wrong type of Var for setVar");
//            }
//        } else {
//            throw new SolverException("BUG in CSP network management: too large index for setVar");
//        }
//    }


    public boolean isSatisfied(int[] tuple) {
        throw new UnsupportedOperationException(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
    }

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.MIXED;
    }
}
