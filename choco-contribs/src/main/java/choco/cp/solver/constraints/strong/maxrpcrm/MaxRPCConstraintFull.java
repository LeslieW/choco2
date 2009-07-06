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
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;
import java.util.Map;

public class MaxRPCConstraintFull extends AbstractMaxRPCConstraint {

    // public static boolean use_reset = true;

    /**
     * Structure pour les résidus
     */
    private final int[][] last;

    private final int[] offset;

    private int[] currentPcRes;

    /**
     * Implémentation de la contrainte utilisant des résidus pour les supports
     * PC construction de la liste des résidus PC à partir d'un tableau
     * prégénéré
     * 
     * @param intSConstraint
     * @param variablesMap
     */
    public MaxRPCConstraintFull(ISpecializedConstraint intSConstraint,
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

    @Override
    public void compute3Cliques() {
        super.compute3Cliques(true);
        currentPcRes = new int[cliques.length];
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

    private int last(int position, int value) {
        return last[position][value - offset[position]];
    }

    /**
     * Propagation d'un arc (Contrainte, Variable, Valeur)
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
                if (revise(position, a, Integer.MAX_VALUE)) {
                    scope[position].removeVal(a);
                    revised = true;
                }
            }
        } finally {
            itr.dispose();
        }
        return revised;
    }

    private boolean revise(int position, int a, int notPC) {
        if (notPC >= Integer.MAX_VALUE && checkLast(position, a)) {
            return false;
        }

        final Clique[] cliques = this.cliques;
        final int[] currentPcRes = this.currentPcRes;
        final int otherPosition = 1 - position;

        int b = firstSupport(position, a);

        while (b < Integer.MAX_VALUE) {
            if (b != notPC
                    && pConsistent(position, a, b, currentPcRes, cliques)) {
                setLast(position, a, b);
                setLast(otherPosition, b, a);
                for (int c = cliques.length; --c >= 0;) {
                    cliques[c].setLast(position, a, currentPcRes[c]);
                    cliques[c].setLast(otherPosition, b, currentPcRes[c]);
                }
                return false;

            }
            b = nextSupport(position, a, b);
        }
        return true;
    }

    private static boolean pConsistent(int position, int a, int b,
            int[] currentPcRes, Clique[] cliques) {
        for (int c = cliques.length; --c >= 0;) {
            final int pcs = cliques[c].findPCSupport(position, a, b);
            if (pcs >= Integer.MAX_VALUE) {
                return false;
            }
            currentPcRes[c] = pcs;
        }
        return true;
    }

    @Override
    public boolean revisePC(Clique clique, int position)
            throws ContradictionException {
        boolean revised = false;

        final DisposableIntIterator itr = scope[position].getSVariable()
                .getDomain().getIterator();

        try {
            while (itr.hasNext()) {
                final int v = itr.next();
                if (revisePC(position, v, clique)) {
                    scope[position].removeVal(v);
                    revised = true;
                }
            }
        } finally {
            itr.dispose();
        }

        return revised;
    }

    private boolean revisePC(int position, int value, Clique clique) {
        final int last = last(position, value);
        if (last >= Integer.MAX_VALUE) {
            return false;
        }

        if (clique.checkLast(position, value)) {
            return false;
        }

        final int pcs = clique.findPCSupport(position, value, last);

        if (pcs >= Integer.MAX_VALUE) {
            return revise(position, value, last);
        }
        clique.setLast(position, value, pcs);
        return false;
    }
}
