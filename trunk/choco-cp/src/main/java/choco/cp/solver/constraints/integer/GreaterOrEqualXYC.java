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


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.Arithm;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Level;

/**
 * Implements a constraint X > Y + C, with X and Y two variables and C a constant.
 */
public class GreaterOrEqualXYC extends AbstractBinIntSConstraint {

    /**
     * The search constant of the constraint
     */
    protected final int cste;

    /**
     * Constructs the constraint with the specified variables and constant.
     *
     * @param x0 Should be greater than <code>x0+c</code>.
     * @param x1 Should be less than <code>x0-c</code>.
     * @param c  The search constant used in the inequality.
     */

    public GreaterOrEqualXYC(IntDomainVar x0, IntDomainVar x1, int c) {
        super(x0, x1);
        this.cste = c;
    }


    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
        // return 0x0B;
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * The propagation on constraint awake events.
     *
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public void propagate() throws ContradictionException {
        this.awakeOnInf(1);
        this.awakeOnSup(0);
    }

    /**
     * Propagation when a minimal bound of a variable was modified.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public void awakeOnInf(int idx) throws ContradictionException {

        if (idx == 1) {
            if (logger.isLoggable(Level.FINEST))
                logger.finest("INF(" + v0.toString() + ") >= INF(" + v1.toString() + ") + " + this.cste);
            v0.updateInf(v1.getInf() + this.cste, this.cIdx0);
        } else if (v0.getInf() >= v1.getSup() + this.cste)
            this.setEntailed();
    }


    /**
     * Propagation when a maximal bound of a variable was modified.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx == 0) {
            if (logger.isLoggable(Level.FINEST))
                logger.finest("SUP(" + v1.toString() + ") <= SUP(" + v0.toString() + ") - " + this.cste);
            v1.updateSup(v0.getSup() - this.cste, this.cIdx1);
        } else if (v0.getInf() >= v1.getSup() + this.cste)
            this.setEntailed();
    }


    /**
     * Propagation when a variable is instantiated.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public void awakeOnInst(int idx) throws ContradictionException {
        if (idx == 0) {
            if (logger.isLoggable(Level.FINEST))
                logger.finest("SUP(" + v1.toString() + ") <= SUP(" + v0.toString() + ") - " + this.cste);
            v1.updateSup(v0.getSup() - this.cste, this.cIdx1);
        } else if (idx == 1) {
            if (logger.isLoggable(Level.FINEST))
                logger.finest("INF(" + v0.toString() + ") >= INF(" + v1.toString() + ") + " + this.cste);
            v0.updateInf(v1.getInf() + this.cste, this.cIdx0);
        }
        if (v0.getInf() >= v1.getSup() + this.cste)
            this.setEntailed();
    }


    /**
     * Propagation when a value <code>x</code> of variable is removed.
     * <p/>
     * Not implemented yet.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        ;
    }


    /**
     * Checks if the listeners must be checked or must fail.
     */

    public Boolean isEntailed() {
        if (v0.getSup() < v1.getInf() + this.cste)
            return Boolean.FALSE;
        else if (v0.getInf() >= v1.getSup() + this.cste)
            return Boolean.TRUE;
        return null;
    }


    /**
     * Checks if the constraint is satisfied when the variables are instantiated.
     *
     * @return true if the constraint is satisfied
     */

    public boolean isSatisfied(int[] tuple) {
        return tuple[0] >= tuple[1] + this.cste;
    }

    /**
     * tests if the constraint is consistent with respect to the current state of domains
     *
     * @return true iff the constraint is bound consistent (weaker than arc consistent)
     */
    public boolean isConsistent() {
        return ((v0.getInf() >= v1.getInf() + this.cste) && (v1.getSup() <= v0.getSup() - this.cste));
    }

    public AbstractSConstraint opposite() {
        Solver solver = getSolver();
        return (AbstractSConstraint) solver.lt(v0, solver.plus(v1, cste));
//    return new GreaterOrEqualXYC(v1, v0, 1 - cste);
    }

    public int getVarIdxInOpposite(int i) {
        if (i == 0)
            return 1;
        else if (i == 1)
            return 0;
        else
            return -1;
    }


    public String pretty() {
        StringBuffer sb = new StringBuffer();
        sb.append(v0.toString());
        sb.append(" >= ");
        sb.append(v1.toString());
        sb.append(Arithm.pretty(this.cste));
        return sb.toString();
    }

}
