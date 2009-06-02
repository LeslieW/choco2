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
 * Implements a constraint X !== Y + C, with X and Y two variables and C a constant.
 */
public class NotEqualXYC extends AbstractBinIntSConstraint {

    /**
     * The search constant of the constraint
     */
    protected final int cste;

    /**
     * Constructs the constraint with the specified variables and constant.
     *
     * @param x0 first IntDomainVar
     * @param x1 second IntDomainVar
     * @param c  The search constant used in the disequality.
     */

    public NotEqualXYC(IntDomainVar x0, IntDomainVar x1, int c) {
        super(x0, x1);
        this.cste = c;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
        // return 0x0B;
    }

 // /!\  Logging statements decrease performances
    /**
     * The one and only propagation method, using foward checking
     */

    public void propagate() throws ContradictionException {
        if (v0.isInstantiated()) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0}) != VAL({1}) - {2}", new Object[]{v1.toString(), v0.toString(),this.cste});}
            v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
        } else if (v1.isInstantiated()) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0}) != VAL({1}) + {2}", new Object[]{v0.toString(), v1.toString(), this.cste});}
            v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
        }
    }

    public void awakeOnInf(int idx) throws ContradictionException {
        if (v0.isInstantiated()) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0} != VAL({1}) - {2}", new Object[]{v1.toString(), v0.toString(), this.cste});}
            v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
        } else if (v1.isInstantiated()) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0} != VAL({1}) + {2}", new Object[]{v0.toString(), v1.toString(), this.cste});}
            v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
        }
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        if (v0.isInstantiated()) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0}) != VAL({1}) - {2}", new Object[]{v1.toString(), v0.toString(), this.cste});}
            v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
        } else if (v1.isInstantiated()) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0}) != VAL({1}) + {2}", new Object[]{v0.toString(), v1.toString(), this.cste});}
            v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
        }
    }

    public void awakeOnInst(int idx) throws ContradictionException {
        if (idx == 0) {
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0}) != VAL({1}) - {2}", new Object[]{v1.toString(), v0.toString(), this.cste});}
            v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
        } else {
            assert (idx == 1);
//            if (LOGGER.isLoggable(Level.FINEST))
//            {LOGGER.log(Level.FINEST, "VAL({0}) != VAL({1}) + {2}", new Object[]{v0.toString(), v1.toString(),this.cste});}
            v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
        }
    }

    /**
     * Checks if the listeners must be checked or must fail.
     */

    public Boolean isEntailed() {
        if ((v0.getSup() < v1.getInf() + this.cste) ||
                (v1.getSup() < v0.getInf() - this.cste))
            return Boolean.TRUE;
        else if ((v0.getInf() == v0.getSup()) &&
                (v1.getInf() == v1.getSup()) &&
                (v0.getInf() == v1.getInf() + this.cste))
            return Boolean.FALSE;
        else
            return null;
    }

    /**
     * Checks if the constraint is satisfied when the variables are instantiated.
     */

    public boolean isSatisfied(int[] tuple) {
        return (tuple[0] != tuple[1] + this.cste);
    }

    /**
     * tests if the constraint is consistent with respect to the current state of domains
     *
     * @return true iff the constraint is bound consistent (weaker than arc consistent)
     */
    public boolean isConsistent() {
        return ((v0.isInstantiated()) ?
                ((v1.hasEnumeratedDomain()) ?
                        (!v1.canBeInstantiatedTo(v0.getVal())) :
                        ((v1.getInf() != v0.getVal()) && (v1.getSup() != v0.getVal()))) :
                ((!v1.isInstantiated()) || ((v0.hasEnumeratedDomain()) ?
                        (!v0.canBeInstantiatedTo(v1.getVal())) :
                        ((v0.getInf() != v1.getVal()) && (v0.getSup() != v1.getVal())))));
    }

    public AbstractSConstraint opposite() {
//    return new EqualXYC(v0, v1, cste);
        Solver solver = getSolver();
        return (AbstractSConstraint) solver.eq(v0, solver.plus(v1, cste));
    }


    public String pretty() {
        StringBuffer sb = new StringBuffer();
        sb.append(v0.toString());
        sb.append(" != ");
        sb.append(v1.toString());
        sb.append(Arithm.pretty(this.cste));
        return sb.toString();
    }

}
