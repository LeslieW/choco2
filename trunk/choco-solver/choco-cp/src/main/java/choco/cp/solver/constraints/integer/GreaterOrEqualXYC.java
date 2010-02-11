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
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint X >= Y + C, with X and Y two variables and C a constant.
 */
public final class GreaterOrEqualXYC extends AbstractBinIntSConstraint {

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


    @Override
	public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
        // return 0x0B;
    }

    
    private final void updateInfV0() throws ContradictionException {
    	 v0.updateInf(v1.getInf() + this.cste, this.cIdx0);
    }
    
    private final void updateSupV1() throws ContradictionException {
    	  v1.updateSup(v0.getSup() - this.cste, this.cIdx1);
    }
    /**
     * The propagation on constraint awake events.
     *
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public void propagate() throws ContradictionException {
        updateInfV0();
        updateSupV1();
    }

 
    /**
     * Propagation when a minimal bound of a variable was modified.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    @Override
	public void awakeOnInf(int idx) throws ContradictionException {
    	if (idx == 1) updateInfV0();
        else if (v0.getInf() >= v1.getSup() + this.cste) setEntailed();
    }


    /**
     * Propagation when a maximal bound of a variable was modified.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    @Override
	public void awakeOnSup(int idx) throws ContradictionException {
        if (idx == 0) updateSupV1();
        else if (v0.getInf() >= v1.getSup() + this.cste) setEntailed();
    }


    /**
     * Propagation when a variable is instantiated.
     *
     * @param idx The index of the variable.
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    @Override
	public void awakeOnInst(int idx) throws ContradictionException {
        if (idx == 0) updateSupV1();
        else updateInfV0();
        if (v0.getInf() >= v1.getSup() + this.cste)
            this.setEntailed();
    }

    /**
     * Checks if the listeners must be checked or must fail.
     */

    @Override
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

    @Override
	public boolean isSatisfied(int[] tuple) {
        return tuple[0] >= tuple[1] + this.cste;
    }

    /**
     * tests if the constraint is consistent with respect to the current state of domains
     *
     * @return true iff the constraint is bound consistent (weaker than arc consistent)
     */
    @Override
	public boolean isConsistent() {
        return ((v0.getInf() >= v1.getInf() + this.cste) && (v1.getSup() <= v0.getSup() - this.cste));
    }

    @Override
	public AbstractSConstraint opposite(Solver solver) {
        return (AbstractSConstraint) solver.lt(v0, solver.plus(v1, cste));
    }

    @Override
	public int getVarIdxInOpposite(int i) {
        if (i == 0)
            return 1;
        else if (i == 1)
            return 0;
        else
            return -1;
    }


    @Override
	public String pretty() {
        StringBuffer sb = new StringBuffer();
        sb.append(v0).append(" >= ");
        sb.append(v1).append(StringUtils.pretty(this.cste));
        return sb.toString();
    }

}
