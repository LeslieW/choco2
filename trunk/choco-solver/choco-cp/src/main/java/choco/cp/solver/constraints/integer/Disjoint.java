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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.integer;

import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractUnIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 22 févr. 2010
 * Since : Choco 2.1.1
 */
public final class Disjoint extends AbstractUnIntSConstraint {


    final int[] values;

    public Disjoint(IntDomainVar v0, int[] values) {
        super(v0);
        this.values = values;
    }

    /**
     * <i>Propagation:</i>
     * Propagating the constraint until local consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void propagate() throws ContradictionException {
        for (int val : values) {
            v0.removeVal(val, this, false);
        }
        this.setEntailed();
    }


    /**
     * Get the opposite constraint
     *
     * @return the opposite constraint  @param solver
     */
    @Override
    public AbstractSConstraint opposite(Solver solver) {
        return new Among(v0, values);
    }

    @Override
    public String pretty() {
        StringBuffer sb = new StringBuffer("DISJOINT(");
        sb.append(v0.pretty()).append(",{");
        StringUtils.pretty(values);
        sb.append("})");
        return sb.toString();
    }

    /**
     * TEMPORARY: if not overriden by the constraint, throws an error
     * to avoid bug using reified constraints in constraints
     * that have not been changed to fulfill this api yet !
     *
     * @param tuple
     * @return
     */
    @Override
    public boolean isSatisfied(int[] tuple) {
        for (int val : values) {
            if (tuple[0] == val) {
                return false;
            }
        }
        return true;
    }

    /**
     * Default implementation of the isSatisfied by
     * delegating to the isSatisfied(int[] tuple)
     *
     * @return
     */
    @Override
    public boolean isSatisfied() {
        for (int val : values) {
            if (v0.canBeInstantiatedTo(val)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates if the constraint is entailed, from now on will be always satisfied
     *
     * @return wether the constraint is entailed
     */
    @Override
    public Boolean isEntailed() {
        int nb = 0;
        for(int val : values){
            if(v0.canBeInstantiatedTo(val)){
                nb++;
            }
        }
        if(nb == 0)return true;
        else if(nb == v0.getDomainSize())return false;
        return null;
    }
}