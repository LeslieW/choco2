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
package choco.kernel.model.constraints;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

public abstract class ConstraintManager <V extends Variable> implements ExpressionManager{


    /**
     * Build a constraint for the given solver and "model variables"
     * @param solver solver to build constraint in
     * @param variables array of variables
     * @param parameters Object defining the paramaters
     * @param options set of options
     * @return One SConstraint
     */
    public abstract SConstraint makeConstraint(Solver solver, V[] variables, Object parameters, Set<String> options);

    /**
     * Build a constraint and its opposite for the given solver and "model variables"
     * @param solver solver to build constraint in
     * @param variables array of variables
     * @param parameters Object defining the paramaters
     * @param options set of options
     * @return array of 2 SConstraint object, the constraint and its opposite
     */
    public abstract SConstraint[] makeConstraintAndOpposite(Solver solver, V[] variables, Object parameters, Set<String> options);

    /**
     * @param options : the set of options on the constraint (Typically the level of consistency)
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    public abstract int[] getFavoriteDomains(Set<String> options);

    protected static int[] getACFavoriteIntDomains() {
        return new int[]{IntDomainVar.BITSET,
                IntDomainVar.LINKEDLIST,
                IntDomainVar.BIPARTITELIST,
                IntDomainVar.BINARYTREE,
                IntDomainVar.BOUNDS,
        };
    }

    protected static int[] getBCFavoriteIntDomains() {
        return new int[]{IntDomainVar.BOUNDS,
                IntDomainVar.BINARYTREE,
                IntDomainVar.BITSET,
                IntDomainVar.BIPARTITELIST,
                IntDomainVar.LINKEDLIST,
        };
    }

    protected static SConstraint fail() {
    	return fail("?");
    }

    protected static SConstraint fail(String cname) {
    	LOGGER.severe("Could not found an implementation of "+cname+".");
    	ChocoLogging.flushLogs();
    	return null;
    }


}
