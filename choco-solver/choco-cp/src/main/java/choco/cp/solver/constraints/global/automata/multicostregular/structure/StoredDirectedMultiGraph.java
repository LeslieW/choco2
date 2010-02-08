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
package choco.cp.solver.constraints.global.automata.multicostregular.structure;

import choco.cp.solver.constraints.global.automata.common.StoredIndexedBipartiteSetWithOffset;
import gnu.trove.TIntHashSet;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.Set;
import java.util.Arrays;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.ContradictionException;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.IStateBitSet;
import choco.cp.solver.constraints.global.automata.multicostregular.algo.FastPathFinder;

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

    public int sourceIndex;
    public int tinIndex;



    StoredIndexedBipartiteSetWithOffset[] supports;
    public int[][] layers;
    FastPathFinder pf;
    private IStateBitSet inStack;


    public class Nodes
    {
        public int[] states;
        public int[] layers;
        public StoredIndexedBipartiteSetWithOffset[] outArcs;
        public StoredIndexedBipartiteSetWithOffset[] inArcs;

        public int[] nextSP;
        public int[] prevSP;
        public int[] nextLP;
        public int[] prevLP;

        public double[] spfs;
        public double[] spft;
        public double[] lpfs;
        public double[] lpft;

    }


    public class Arcs
    {
        public int[] values;
        public int[] dests;
        public int[] origs;
    }



    public Nodes GNodes;
    public Arcs GArcs;






    public StoredDirectedMultiGraph(IntSConstraint constraint,DirectedMultigraph<Node, Arc> graph, int[][] layers, int[] starts, int[] offsets, int supportLength)
    {
        this.constraint = constraint;
        this.starts = starts;
        this.offsets =offsets;
        this.layers = layers;
        this.sourceIndex = layers[0][0];
        this.tinIndex = layers[layers.length-1][0];

        this.GNodes = new Nodes();
        this.GArcs = new Arcs();

        TIntHashSet[] sups = new TIntHashSet[supportLength];
        this.supports = new StoredIndexedBipartiteSetWithOffset[supportLength];


        Set<Arc> arcs = graph.edgeSet();

        this.inStack = constraint.getSolver().getEnvironment().makeBitSet(arcs.size());

        GArcs.values = new int[arcs.size()];
        GArcs.dests = new int[arcs.size()];
        GArcs.origs = new int[arcs.size()];


        for (Arc a : arcs)
        {

            GArcs.values[a.id] = a.value;
            GArcs.dests[a.id] = a.dest.id;
            GArcs.origs[a.id] = a.orig.id;

            if (a.orig.layer < starts.length)
            {
                int idx = starts[a.orig.layer]+a.value-offsets[a.orig.layer];
                if (sups[idx] == null)
                    sups[idx] = new TIntHashSet();
                sups[idx].add(a.id);
            }

        }

        for (int i =0 ;i < sups.length ;i++)
        {
            if (sups[i] != null)
                supports[i] = new StoredIndexedBipartiteSetWithOffset(this.constraint.getSolver().getEnvironment(),sups[i].toArray());
        }

        Set<Node> nodes = graph.vertexSet();
        GNodes.outArcs = new StoredIndexedBipartiteSetWithOffset[nodes.size()];
        GNodes.inArcs = new StoredIndexedBipartiteSetWithOffset[nodes.size()];
        GNodes.layers = new int[nodes.size()];
        GNodes.states = new int[nodes.size()];

        GNodes.prevLP = new int[nodes.size()];
        Arrays.fill(GNodes.prevLP,Integer.MIN_VALUE);
        GNodes.nextLP = new int[nodes.size()];
        Arrays.fill(GNodes.nextLP,Integer.MIN_VALUE);
        GNodes.prevSP = new int[nodes.size()];
        Arrays.fill(GNodes.prevSP,Integer.MIN_VALUE);
        GNodes.nextSP = new int[nodes.size()];
        Arrays.fill(GNodes.nextSP,Integer.MIN_VALUE);


        GNodes.lpfs = new double[nodes.size()];
        GNodes.lpft = new double[nodes.size()];
        GNodes.spfs = new double[nodes.size()];
        GNodes.spft = new double[nodes.size()];




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
                GNodes.outArcs[n.id] = new StoredIndexedBipartiteSetWithOffset(this.constraint.getSolver().getEnvironment(),out);
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
                GNodes.inArcs[n.id] = new StoredIndexedBipartiteSetWithOffset(this.constraint.getSolver().getEnvironment(),in);
            }
        }


        this.pf = new FastPathFinder(this);

    }

    public final StoredIndexedBipartiteSetWithOffset getSupport(int i, int j)
    {
        int idx = starts[i]+j-offsets[i];
        return supports[idx];


    }

    public final FastPathFinder getPathFinder()
    {
        return pf;
    }







    public void removeArc(int arcId, IStateIntVector toRemove) throws ContradictionException {
        inStack.clear(arcId);

        int orig = GArcs.origs[arcId];
        int dest = GArcs.dests[arcId];

        int layer = GNodes.layers[orig];
        int value = GArcs.values[arcId];

        if (layer < starts.length)
        {
            StoredIndexedBipartiteSetWithOffset support = getSupport(layer,value);
            support.remove(arcId);

            if (support.isEmpty())
            {
                IntDomainVar var = this.constraint.getIntVar(layer);
                var.removeVal(value,this.constraint.getConstraintIdx(layer));
            }
        }

        DisposableIntIterator it;
        StoredIndexedBipartiteSetWithOffset out = GNodes.outArcs[orig];
        StoredIndexedBipartiteSetWithOffset in;

        out.remove(arcId);


        if (GNodes.layers[orig] > 0 && out.isEmpty())
        {
            in = GNodes.inArcs[orig];
            it = in.getIterator();
            while(it.hasNext())
            {
                int id = it.next();
                if(!isInStack(id))
                {
                    setInStack(id);
                    toRemove.add(id);
                }
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
                if (!isInStack(id))
                {
                    setInStack(id);
                    toRemove.add(id);
                }
            }
            it.dispose();

        }

    }


    /**
     * Getter to the is arc in to be removed stack bitSet
     * @return an instance of a storable bitset
     */
    public final IStateBitSet getInStack()
    {
        return inStack;
    }

    /**
     *  Getter, the idx th bit of the inStack bitSet
     * @param idx the index of the arc
     * @return true if a given arc is to be deleted
     */
    public final boolean isInStack(int idx)
    {
        return inStack.get(idx);
    }

    /**
     * Set the idx th bit of the to be removed bitset
     * @param idx the index of the bit
     */
    public final void setInStack(int idx)
    {
        inStack.set(idx);
    }

    /**
     * Clear the idx th bit of the to be removed bitset
     * @param idx the index of the bit
     */
    public final void clearInStack(int idx)
    {
        inStack.clear(idx);
    }

}