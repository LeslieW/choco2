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
import choco.cp.solver.constraints.strong.SCVariable;
import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;
import java.util.Map;

public class MaxRPCConstraintLight extends AbstractMaxRPCConstraint {
    /**
     * Structure pour les résidus
     */
    private final int[][] last;

    private final int[] offset;

    /**
     * Implémentation de la contrainte utilisant des résidus pour les supports
     * PC construction de la liste des résidus PC à partir d'un tableau
     * prégénéré
     * 
     * @param intSConstraint
     * @param variablesMap
     */
    public MaxRPCConstraintLight(ISpecializedConstraint intSConstraint,
            Map<IntDomainVar, SCVariable> variablesMap) {
        super(intSConstraint, variablesMap);

        last = new int[2][];
        offset = new int[2];

        for (int i = 2; --i >= 0;) {
            offset[i] = scope[i].getOffset();
            last[i] = new int[1 + scope[i].getSVariable().getSup() - offset[i]];
            Arrays.fill(last[i], Integer.MAX_VALUE);
        }
    }

    public void compute3Cliques() {
        compute3Cliques(false);
    }

    private void setLast(int position, int value, int support) {
        last[position][value - offset[position]] = support;
    }

    /**
     * Contrôle la validité d'un résidu
     * 
     * @param position
     * @param value
     * @return
     */
    private boolean checkLast(int position, int value) {
        return scope[1 - position].getSVariable().canBeInstantiatedTo(
                last[position][value - offset[position]]);
    }

    /**
     * Propagation d'un arc (Contrainte, Variable)
     * 
     * @param constraint
     * @param position
     *            la position de la variable dans la contrainte
     * @throws ContradictionException
     */
    @Override
    public boolean revise(int position) throws ContradictionException {
        boolean revised = false;
        final DisposableIntIterator itr = scope[position].getSVariable()
                .getDomain().getIterator();
        try {
            while (itr.hasNext()) {
                final int a = itr.next();
                if (revise(position, a)) {
                    scope[position].removeVal(a);
                    revised = true;
                }
            }
        } finally {
            itr.dispose();
        }
        return revised;
    }

    private boolean revise(int position, int a) {
        if (checkLast(position, a)) {
            return false;
        }

        int b = firstSupport(position, a);

        while (b < Integer.MAX_VALUE) {
            if (pConsistent(position, a, b)) {
                setLast(position, a, b);
                setLast(1 - position, b, a);
                return false;
            }

            b = nextSupport(position, a, b);
        }
        return true;
    }

    protected boolean pConsistent(int position, int a, int b) {
        final Clique[] cliques = this.cliques;
        for (int c = cliques.length; --c >= 0;) {
            if (cliques[c].findPCSupport(position, a, b) >= Integer.MAX_VALUE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean revisePC(Clique clique, int position) {
        return false;
    }
}
