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
package choco.cp.solver.constraints.global.fast_regular.structure;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntStack;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.Set;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.ContradictionException;
import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 4, 2009
 * Time: 1:07:19 PM
 */
public class StoredDirectedMultiGraph {

    IntSConstraint constraint;

    int[] starts;
    int[] offsets;



    StoredIndexedBipartiteSetWithAccess[] supports;




    class Nodes
    {
        int[] states;
        int[] layers;
        StoredIndexedBipartiteSetWithAccess[] outArcs;
        StoredIndexedBipartiteSetWithAccess[] inArcs;



    }


    public class Arcs
    {
        int[] values;
        int[] dests;
        int[] origs;
    }



    public Nodes GNodes;
    public Arcs GArcs;






    public StoredDirectedMultiGraph(IntSConstraint constraint,DirectedMultigraph<Node, Arc> graph, int[] starts, int[] offsets, int supportLength)
    {
        this.constraint = constraint;
        this.starts = starts;
        this.offsets =offsets;

        this.GNodes = new Nodes();
        this.GArcs = new Arcs();

        TIntHashSet[] sups = new TIntHashSet[supportLength];
        this.supports = new StoredIndexedBipartiteSetWithAccess[supportLength];


        Set<Arc> arcs = graph.edgeSet();

        GArcs.values = new int[arcs.size()];
        GArcs.dests = new int[arcs.size()];
        GArcs.origs = new int[arcs.size()];

        for (Arc a : arcs)
        {
            GArcs.values[a.id] = a.value;
            GArcs.dests[a.id] = a.dest.id;
            GArcs.origs[a.id] = a.orig.id;

            int idx = starts[a.orig.layer]+a.value-offsets[a.orig.layer];
            if (sups[idx] == null)
                sups[idx] = new TIntHashSet();
            sups[idx].add(a.id);


        }

        for (int i =0 ;i < sups.length ;i++)
        {
            if (sups[i] != null)
                supports[i] = new StoredIndexedBipartiteSetWithAccess(this.constraint.getSolver().getEnvironment(),sups[i].toArray());
        }

        Set<Node> nodes = graph.vertexSet();
        GNodes.outArcs = new StoredIndexedBipartiteSetWithAccess[nodes.size()];
        GNodes.inArcs = new StoredIndexedBipartiteSetWithAccess[nodes.size()];
        GNodes.layers = new int[nodes.size()];
        GNodes.states = new int[nodes.size()];


        for (Node n : nodes)
        {
            GNodes.layers[n.id] = n.layer;
            GNodes.states[n.id] = n.state;
            int i;
            Set<Arc> outarc = graph.outgoingEdgesOf(n);
            if (!outarc.isEmpty())
            {
                int[] out = new int[outarc.size()];
                i = 0;
                for (Arc a : outarc)
                {
                    out[i++] = a.id;
                }
                GNodes.outArcs[n.id] = new StoredIndexedBipartiteSetWithAccess(this.constraint.getSolver().getEnvironment(),out);
            }

            Set<Arc> inarc = graph.incomingEdgesOf(n);
            if (!inarc.isEmpty())
            {
                int[] in = new int[inarc.size()];
                i = 0;
                for (Arc a : inarc)
                {
                    in[i++] = a.id;
                }
                GNodes.inArcs[n.id] = new StoredIndexedBipartiteSetWithAccess(this.constraint.getSolver().getEnvironment(),in);
            }
        }



    }

    public final StoredIndexedBipartiteSetWithAccess getSupport(int i, int j)
    {
        int idx = starts[i]+j-offsets[i];
        return supports[idx];


    }






    TIntStack stack = new TIntStack();

    public void removeArc(int arcId) throws ContradictionException {
        int orig = GArcs.origs[arcId];
        int dest = GArcs.dests[arcId];

        int layer = GNodes.layers[orig];
        int value = GArcs.values[arcId];

        StoredIndexedBipartiteSetWithAccess support = getSupport(layer,value);
        support.remove(arcId);

        if (support.isEmpty())
        {
            IntDomainVar var = this.constraint.getIntVar(layer);
            try
            {
                var.removeVal(value,this.constraint.getConstraintIdx(layer));
            }   catch (ContradictionException ce)
            {
                stack.clear();
                throw ce;
            }
        }

        DisposableIntIterator it;
        StoredIndexedBipartiteSetWithAccess out = GNodes.outArcs[orig];
        StoredIndexedBipartiteSetWithAccess in;

        out.remove(arcId);


        if (GNodes.layers[orig] > 0 && out.isEmpty())
        {
            in = GNodes.inArcs[orig];
            it = in.getIterator();
            while(it.hasNext())
            {
                int id = it.next();
                stack.push(id);
            }
            it.dispose();
        }

        in = GNodes.inArcs[dest];
        in.remove(arcId);



        if (GNodes.layers[dest] < this.constraint.getNbVars() && in.isEmpty())
        {
            out = GNodes.outArcs[dest];
            it = out.getIterator();
            while (it.hasNext())
            {
                int id = it.next();
                stack.push(id);
            }
            it.dispose();

        }


        while(!(stack.size() ==0))
        {
            removeArc(stack.pop());
        }

    }


}