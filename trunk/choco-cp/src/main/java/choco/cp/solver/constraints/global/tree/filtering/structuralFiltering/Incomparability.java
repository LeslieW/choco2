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
package choco.cp.solver.constraints.global.tree.filtering.structuralFiltering;



import choco.cp.solver.constraints.global.tree.filtering.AbstractPropagator;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.common.util.DisposableIntIterator;

import java.util.BitSet;

public class Incomparability extends AbstractPropagator {

    public Incomparability(Object[] params) {
        super(params);
    }

    public String getTypePropag() {
        return "Incomp propagation";
    }

    public boolean feasibility() {
        StoredBitSet sources = precs.getSrcNodes();
        for (int w = sources.nextSetBit(0); w >= 0; w = sources.nextSetBit(w + 1)) {
            BitSet D_w = precs.getDescendants(w);
            for (int u = D_w.nextSetBit(0); u >= 0; u = D_w.nextSetBit(u + 1)) {
                StoredBitSet I_u = nodes[u].getIncomparableNodes();
                for (int v = I_u.nextSetBit(0); v >= 0; v = I_u.nextSetBit(v + 1)) {
                    if (D_w.get(v)) {
                        if (affiche)
                            System.out.println("Violation incomp : inc(" + u + "," + v + ") VS desc_" + w + " = " + D_w.toString());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p> two filtering rules are applied for the incomparability constraints: </p>
     *
     * <blockquote> 1- remove infeasible potential roots when the number of trees is fixed to 1. </blockquote>
     * <blockquote> 2- for each potential arc (u,v) in the graph, if there exists a node u_d in the mandatory
     * descendants of u and a node a_v in the mandatory ancestors of v such that u_d and a_v are incomparable then,
     * the arc (u,v) is infeasible according to the interaction between precedence and incomparability constraints.
     * </blockquote> 
     * @throws choco.kernel.solver.ContradictionException
     */
    public void filter() throws ContradictionException {
        filterAccordingToNtree();
        for (int u = 0; u < nbVertices; u++) {
            if (!nodes[u].getSuccessors().isInstantiated()) {
                IntDomain dom = nodes[u].getSuccessors().getDomain();
                DisposableIntIterator iter = dom.getIterator();
                while (iter.hasNext()) {
                    int v = iter.next();
                    if (u != v) filteringAccordingToPrecsAndIncs(u, v);
                }
            }
        }
    }

    private void filterAccordingToNtree() {
        if (tree.getNtree().isInstantiatedTo(1)) {
            for (int i = 0; i < nbVertices; i++) {
                if (nodes[i].getSuccessors().canBeInstantiatedTo(i)) {
                    if (nodes[i].getIncomparableNodes().cardinality() != 0) {
                        if (affiche)
                            System.out.println("1- filterAccordingToNtree(): l'arc (" + i + "," + i + ") est impossible");
                        int[] arc = {i, i};
                        propagateStruct.addRemoval(arc);
                    } else {
                        for (int j = 0; j < nbVertices; j++) {
                            if (nodes[j].getIncomparableNodes().get(i)) {
                                if (affiche)
                                    System.out.println("2- filterAccordingToNtree(): l'arc (" + i + "," + i + ") est impossible");
                                int[] arc = {i, i};
                                propagateStruct.addRemoval(arc);
                            }
                        }
                    }
                }
            }
        }
    }

    private void filteringAccordingToPrecsAndIncs(int u, int v) {
        int[] arc = {u, v};
        BitSet A_u = precs.getAncestors(u);
        A_u.set(u,true);
        BitSet D_v = precs.getDescendants(v);
        D_v.set(v,true);
        BitSet A_v = precs.getAncestors(v);
        A_v.set(v,true);
        BitSet Dr_u = precs.getDescendants(u);
        Dr_u.set(u,false);
        for (int v_a = A_v.nextSetBit(0); v_a >= 0; v_a = A_v.nextSetBit(v_a + 1)) {
            for (int u_d = Dr_u.nextSetBit(0); u_d >= 0; u_d = Dr_u.nextSetBit(u_d + 1)) {
                if (v_a < u_d && nodes[v_a].getIncomparableNodes().get(u_d)) {
                    if (affiche)
                        System.out.println("1- filteringAccordingToPrecsAndIncs(): l'arc (" + u + "," + v + ") est impossible");
                    propagateStruct.addRemoval(arc);
                    return;
                }
                if (u_d < v_a && nodes[u_d].getIncomparableNodes().get(v_a)) {
                    if (affiche)
                        System.out.println("2- filteringAccordingToPrecsAndIncs(): l'arc (" + u + "," + v + ") est impossible");
                    propagateStruct.addRemoval(arc);
                    return;
                }
            }
        }
        for (int u_a = A_u.nextSetBit(0); u_a >= 0; u_a = A_u.nextSetBit(u_a + 1)) {
            for (int v_d = D_v.nextSetBit(0); v_d >= 0; v_d = D_v.nextSetBit(v_d + 1)) {
                if (u_a < v_d && nodes[u_a].getIncomparableNodes().get(v_d)) {
                    if (affiche)
                        System.out.println("3- filteringAccordingToPrecsAndIncs(): l'arc (" + u + "," + v + ") est impossible");
                    propagateStruct.addRemoval(arc);
                    return;
                }
                if (v_d < u_a && nodes[v_d].getIncomparableNodes().get(u_a)) {
                    if (affiche)
                        System.out.println("4- filteringAccordingToPrecsAndIncs(): l'arc (" + u + "," + v + ") est impossible");
                    propagateStruct.addRemoval(arc);
                    return;
                }
            }
        }
    }

}
