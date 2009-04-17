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
package choco.cp.solver.constraints.global.tree.deduction;
import choco.kernel.memory.trailing.StoredBitSet;

import java.util.BitSet;
import java.util.logging.Level;


public class IncompGraphDeduction extends AbstractDeduction {
    
    public IncompGraphDeduction(Object[] params) {
        super(params);
    }

    /**
     * the main method that update the incomparability constraints according to the different parts of the tree constraint
     */
    public void updateIncompGraphWithDeductions() {
        update = false;
        compatible = true;
        updateIncFromInst();
        updateIncFromPrecs();
    }

    /**
     * update incomparability constraints according to the required arcs involved in the graph
     */
    private void updateIncFromInst() {
        StoredBitSet[] trueGraph = inputGraph.getSure().getGraph();
        for (int i = 0; i < nbVertices; i++) {
            for (int j = trueGraph[i].nextSetBit(0); j >= 0; j = trueGraph[i].nextSetBit(j + 1)) {
                StoredBitSet inc_i = incomp.getSuccessors(i);
                StoredBitSet A_j = inputGraph.getGlobal().getAncestors(j);
                if (i != j) {
                    StoredBitSet inc = substractBitSet(inc_i, A_j);
                    for (int k = inc.nextSetBit(0); k >= 0; k = inc.nextSetBit(k + 1)) {
                        if (j < k && !incomp.getSuccessors(j).get(k)) {
                            if (affiche)
                                LOGGER.log(Level.INFO, "11- updateIncFromInst(): on ajoute ({0}, {1})", new Object[]{j,k});
                            update = true;
                            incomp.addArc(j,k);
                        }
                        if (k < j && !incomp.getSuccessors(k).get(j)) {
                            if (affiche)
                                LOGGER.log(Level.INFO, "12- updateIncFromInst(): on ajoute ({0}, {1})", new Object[]{k, j});
                            update = true;
                            incomp.addArc(k,j);
                        }
                    }
                }
            }
        }
    }

    /**
     * update incomparability constraints according to the precedence constraints and the existing incomparability
     * constraints
     */
    private void updateIncFromPrecs() {
        for (int u = 0; u < nbVertices; u++) {
            BitSet A_u = precs.getAncestors(u);
            A_u.set(u,true);
            StoredBitSet inc_u = incomp.getSuccessors(u);
            if (inc_u.cardinality() > 0) {
                for (int u_a = A_u.nextSetBit(0); u_a >= 0; u_a = A_u.nextSetBit(u_a + 1)) {
                    for (int v = inc_u.nextSetBit(0); v >= 0; v = inc_u.nextSetBit(v + 1)) {
                        BitSet A_v = precs.getAncestors(v);
                        A_v.set(v,true);
                        for (int v_a = A_v.nextSetBit(0); v_a >= 0; v_a = A_v.nextSetBit(v_a + 1)) {
                            if (u_a < v_a && !incomp.getSuccessors(u_a).get(v_a)) {
                                if (affiche)
                                    LOGGER.log(Level.INFO, "1- updateIncFromRemoval(): on ajoute ({0}, {1})", new Object[]{u_a, v_a});
                                update = true;
                                incomp.addArc(u_a,v_a);
                            }
                            if (v_a < u_a && !incomp.getSuccessors(v_a).get(u_a)) {
                                if (affiche)
                                    LOGGER.log(Level.INFO, "2- updateIncFromRemoval(): on ajoute ({0}, {1})", new Object[]{v_a, u_a});
                                update = true;
                                incomp.addArc(v_a,u_a);
                            }
                        }
                        BitSet D_v = precs.getDescendants(v);
                        D_v.set(v,false);
                        for (int v_d = D_v.nextSetBit(0); v_d >= 0; v_d = D_v.nextSetBit(v_d + 1)) {
                            if (u_a != u && incomp.getSuccessors(v_d).get(u_a)) {
                                if (u < v_d && !incomp.getSuccessors(u).get(v_d)) {
                                    if (affiche)
                                        LOGGER.log(Level.INFO, "3- updateIncFromRemoval(): on ajoute ({0}, {1})", new Object[]{u, v_d});
                                    update = true;
                                    incomp.addArc(u,v_d);
                                }
                                if (v_d < u && !incomp.getSuccessors(v_d).get(u)) {
                                    if (affiche)
                                        LOGGER.log(Level.INFO, "4- updateIncFromRemoval(): on ajoute ({0}, {1})", new Object[]{v_d, u});
                                    update = true;
                                    incomp.addArc(v_d,u);
                                }
                                if (v < u_a && !incomp.getSuccessors(v).get(u_a)) {
                                    if (affiche)
                                        LOGGER.log(Level.INFO, "5- updateIncFromRemoval(): on ajoute ({0}, {1})", new Object[]{v, u_a});
                                    update = true;
                                    incomp.addArc(v,u_a);
                                }
                                if (u_a < v && !incomp.getSuccessors(u_a).get(v)) {
                                    if (affiche)
                                        LOGGER.log(Level.INFO, "6- updateIncFromRemoval(): on ajoute ({0}, {1})", new Object[]{u_a, v });
                                    update = true;
                                    incomp.addArc(u_a,v);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param a     a backtrackable bitset
     * @param b     a backtrackable bitset
     * @return   a backtrackable bitset which is the difference between a and b
     */
    private StoredBitSet substractBitSet(StoredBitSet a, StoredBitSet b) {
        StoredBitSet res = (StoredBitSet) a.clone();
        res.and(b);
        res.xor(a);
        return res;
    }
}
