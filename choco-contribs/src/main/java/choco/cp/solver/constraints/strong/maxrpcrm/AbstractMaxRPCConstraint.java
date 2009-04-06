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
package choco.cp.solver.constraints.strong.maxrpcrm;

import choco.cp.solver.constraints.strong.ISpecializedConstraint;
import choco.cp.solver.constraints.strong.SCConstraint;
import choco.cp.solver.constraints.strong.SCVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMaxRPCConstraint extends SCConstraint {
    /**
     * Toutes les 3-cliques où apparaissent cette contrainte (à initialiser avec
     * compute3Cliques une fois que toutes les contraintes sont créées et liées
     * aux variables)
     */
    protected Clique[] cliques;

    protected final MaxRPCVariable[] scope;

    public AbstractMaxRPCConstraint(ISpecializedConstraint sConstraint,
            Map<IntDomainVar, SCVariable> pool) {
        super(sConstraint, pool);

        this.scope = new MaxRPCVariable[super.scope.length];
        for (int i = scope.length; --i >= 0;) {
            this.scope[i] = (MaxRPCVariable) super.scope[i];
        }
    }

    public abstract void compute3Cliques();

    protected void compute3Cliques(boolean useSupports) {
        final List<Clique> cliques = new ArrayList<Clique>();
        for (AbstractMaxRPCConstraint c1 : scope[0].getConstraints()) {
            if (c1 == this) {
                continue;
            }
            final int alt1 = (c1.getVariable(0) == scope[0] ? 1 : 0);
            final MaxRPCVariable altVariable = c1.getVariable(alt1);
            for (AbstractMaxRPCConstraint c2 : scope[1].getConstraints()) {
                final int alt2 = (c2.getVariable(0) == scope[1] ? 1 : 0);
                if (altVariable == c2.getVariable(alt2)) {
                    cliques.add(new Clique(this, (AbstractMaxRPCConstraint) c1,
                            alt1, (AbstractMaxRPCConstraint) c2, alt2,
                            useSupports));
                }
            }
        }
        this.cliques = cliques.toArray(new Clique[cliques.size()]);
    }


    public int getNbCliques() {
        return cliques.length;
    }

    public MaxRPCVariable getVariable(int position) {
        return scope[position];
    }

    public abstract boolean revise(int position) throws ContradictionException;

    public abstract boolean revisePC(Clique clique, int position)
            throws ContradictionException;

}
