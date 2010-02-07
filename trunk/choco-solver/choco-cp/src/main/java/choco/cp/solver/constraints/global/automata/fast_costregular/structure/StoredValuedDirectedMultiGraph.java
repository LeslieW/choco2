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
package choco.cp.solver.constraints.global.automata.fast_costregular.structure;

import choco.cp.solver.constraints.global.automata.common.StoredIndexedBipartiteSetWithOffset;
import choco.kernel.memory.*;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntStack;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.BitSet;
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
public class StoredValuedDirectedMultiGraph {

    IntSConstraint constraint;

    int[] starts;
    int[] offsets;

    public int sourceIndex;
    public int tinkIndex;



    StoredIndexedBipartiteSetWithOffset[] supports;
    public int[][] layers;
    public BitSet inStack;
    public StoredIndexedBipartiteSet inGraph;


    public class Nodes
    {
        public int[] states;
        public int[] layers;
        public StoredIndexedBipartiteSetWithOffset[] outArcs;
        public StoredIndexedBipartiteSetWithOffset[] inArcs;

        public IStateIntVector nextSP;
        public IStateIntVector prevSP;
        public IStateIntVector nextLP;
        public IStateIntVector prevLP;

        public IStateDoubleVector spfs;
        public IStateDoubleVector spft;
        public IStateDoubleVector lpfs;
        public IStateDoubleVector lpft;

    }


    public class Arcs
    {
        public int[] values;
        public int[] dests;
        public int[] origs;
        public double[] costs;
    }



    public Nodes GNodes;
    public Arcs GArcs;






    public StoredValuedDirectedMultiGraph(IntSConstraint constraint, DirectedMultigraph<Node, Arc> graph, int[][] layers, int[] starts, int[] offsets, int supportLength)
    {
        this.constraint = constraint;
        this.starts = starts;
        this.offsets =offsets;
        this.layers = layers;
        this.sourceIndex = layers[0][0];
        this.tinkIndex = layers[layers.length-1][0];

        this.GNodes = new Nodes();
        this.GArcs = new Arcs();

        TIntHashSet[] sups = new TIntHashSet[supportLength];
        this.supports = new StoredIndexedBipartiteSetWithOffset[supportLength];


        Set<Arc> arcs = graph.edgeSet();

        this.inStack = new BitSet(arcs.size());//constraint.getSolver().getEnvironment().makeBitSet(arcs.size());

        GArcs.values = new int[arcs.size()];
        GArcs.dests = new int[arcs.size()];
        GArcs.origs = new int[arcs.size()];
        GArcs.costs = new double[arcs.size()];


        int[] inginit = new int[arcs.size()];
        int tmp = 0;
        for (Arc a : arcs)
        {
            inginit[tmp++] = a.id;
            GArcs.values[a.id] = a.value;
            GArcs.dests[a.id] = a.dest.id;
            GArcs.origs[a.id] = a.orig.id;
            GArcs.costs[a.id] = a.cost;

            if (a.orig.layer < starts.length)
            {
                int idx = starts[a.orig.layer]+a.value-offsets[a.orig.layer];
                if (sups[idx] == null)
                    sups[idx] = new TIntHashSet();
                sups[idx].add(a.id);
            }

        }

        this.inGraph = new StoredIndexedBipartiteSet(constraint.getSolver().getEnvironment(),inginit);
       // this.inGraph = constraint.getSolver().getEnvironment().makeBitSet(arcs.size());
//        this.inGraph.set(0,arcs.size());
       // System.out.println(this.inGraph.size());
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

        IEnvironment env = constraint.getSolver().getEnvironment();

        GNodes.prevLP = env.makeIntVector(nodes.size(),Integer.MIN_VALUE);
        GNodes.nextLP = env.makeIntVector(nodes.size(),Integer.MIN_VALUE);
        GNodes.prevSP = env.makeIntVector(nodes.size(),Integer.MIN_VALUE);
        GNodes.nextSP = env.makeIntVector(nodes.size(),Integer.MIN_VALUE);


        GNodes.lpfs = env.makeDoubleVector(nodes.size(),Double.NEGATIVE_INFINITY);
        GNodes.lpft = env.makeDoubleVector(nodes.size(),Double.NEGATIVE_INFINITY);
        GNodes.spfs = env.makeDoubleVector(nodes.size(),Double.POSITIVE_INFINITY);
        GNodes.spft = env.makeDoubleVector(nodes.size(),Double.POSITIVE_INFINITY);





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


        initPathInfo();

    }

    public void initPathInfo()
    {
        int start = layers[0][0];
        int end = layers[layers.length-1][0];
        GNodes.spfs.set(start,0.0);
        GNodes.lpfs.set(start,0.0);
        GNodes.spft.set(end,0.0);
        GNodes.lpft.set(end,0.0);


        for (int i = 1 ; i < layers.length ; i++)
        {
            int[] layer = layers[i];
            for (int q : layer)
            {
                DisposableIntIterator it = GNodes.inArcs[q].getIterator();
                while (it.hasNext())
                {
                    int arc = it.next();
                    double acost = GArcs.costs[arc];
                    int orig = GArcs.origs[arc];
                    double otherS = GNodes.spfs.get(orig) +acost;
                    if (otherS < GNodes.spfs.get(q))
                    {
                        GNodes.spfs.set(q,otherS);
                        GNodes.prevSP.set(q,arc);
                    }

                    double otherL = GNodes.lpfs.get(orig) +acost;
                    if (otherL > GNodes.lpfs.get(q))
                    {
                        GNodes.lpfs.set(q,otherL);
                        GNodes.prevLP.set(q,arc);
                    }

                }
                it.dispose();

            }

        }

        for (int i = layers.length-2 ; i >=0 ; i--)
        {
            int[] layer = layers[i];
            for (int q : layer)
            {
                DisposableIntIterator it = GNodes.outArcs[q].getIterator();
                while (it.hasNext())
                {
                    int arc = it.next();
                    double acost = GArcs.costs[arc];
                    int dest = GArcs.dests[arc];
                    double otherS = GNodes.spft.get(dest) + acost;
                    if (otherS < GNodes.spft.get(q))
                    {
                        GNodes.spft.set(q,otherS);
                        GNodes.nextSP.set(q,arc);
                    }

                    double otherL = GNodes.lpft.get(dest) +acost;
                    if (otherL > GNodes.lpft.get(q))
                    {
                        GNodes.lpft.set(q,otherL);
                        GNodes.nextLP.set(q,arc);
                    }
                }
            }
        }

        //   System.out.println(GNodes.lpfs.get(end));
        //   System.out.println(GNodes.lpft.get(start));


    }

    public final StoredIndexedBipartiteSetWithOffset getSupport(int i, int j)
    {
        int idx = starts[i]+j-offsets[i];
        return supports[idx];


    }






    public void removeArc(int arcId, TIntStack toRemove) throws ContradictionException {
        inStack.clear(arcId);
        inGraph.remove(arcId);

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

        StoredIndexedBipartiteSetWithOffset out = GNodes.outArcs[orig];
        StoredIndexedBipartiteSetWithOffset in;

        out.remove(arcId);

        in = GNodes.inArcs[dest];
        in.remove(arcId);


        if (GNodes.nextSP.get(orig) == arcId)
        {
            updateSPFT(orig,toRemove);
        }
        if (GNodes.nextLP.get(orig) == arcId)
        {
            updateLPFT(orig,toRemove);
        }
        if (GNodes.prevSP.get(dest) == arcId)
        {
            updateSPFS(dest,toRemove);
        }
        if (GNodes.prevLP.get(dest) == arcId)
        {
            updateLPFS(dest,toRemove);
        }

    }


    public void updateSPFT(int nid, TIntStack toRemove)
    {


        double tempPval = Double.POSITIVE_INFINITY;
        int tempP = Integer.MIN_VALUE;
        DisposableIntIterator it = GNodes.outArcs[nid].getIterator();


        while(it.hasNext())
        {
            int arcId = it.next();
            int dest = GArcs.dests[arcId];
            double spft = GNodes.spft.get(dest) + GArcs.costs[arcId];
            if (tempPval > spft)
            {
                tempPval = spft;
                tempP = arcId;
            }

        }
        it.dispose();
        double old = GNodes.spft.set(nid,tempPval);
        GNodes.nextSP.set(nid,tempP);

        if (nid != sourceIndex && old != tempPval)
        {
            it = GNodes.inArcs[nid].getIterator();

            while(it.hasNext())
            {
                int arcId = it.next();
                int orig =  GArcs.origs[arcId];
                if (GNodes.nextSP.get(orig) == arcId)
                {
                    updateSPFT(orig,toRemove);
                }
                double spfs = GNodes.spfs.get(orig);
                double acost = GArcs.costs[arcId];
                if (!isInStack(arcId) && tempPval + spfs+acost > constraint.getIntVar(starts.length).getSup())
                {
                    setInStack(arcId);
                    toRemove.push(arcId);
                }
            }
            it.dispose();
        }

    }

    public void updateLPFT(int nid, TIntStack toRemove)
    {


        double tempPval = Double.NEGATIVE_INFINITY;
        int tempP = Integer.MIN_VALUE;
        DisposableIntIterator it = GNodes.outArcs[nid].getIterator();


        while(it.hasNext())
        {
            int arcId = it.next();
            int dest = GArcs.dests[arcId];
            double lpft = GNodes.lpft.get(dest) + GArcs.costs[arcId];
            if (tempPval < lpft)
            {
                tempPval = lpft;
                tempP = arcId;
            }

        }
        it.dispose();
        double old = GNodes.lpft.set(nid,tempPval);
        GNodes.nextLP.set(nid,tempP);

        if (nid != sourceIndex && old != tempPval)
        {
            it = GNodes.inArcs[nid].getIterator();

            while(it.hasNext())
            {
                int arcId = it.next();
                int orig =  GArcs.origs[arcId];
                if (GNodes.nextLP.get(orig) == arcId)
                {
                    updateLPFT(orig,toRemove);
                }
                double lpfs = GNodes.lpfs.get(orig);
                double acost = GArcs.costs[arcId];
                if (!isInStack(arcId) && tempPval + lpfs+acost < constraint.getIntVar(starts.length).getInf())
                {
                    setInStack(arcId);
                    toRemove.push(arcId);
                }
            }
            it.dispose();
        }

    }

    public void updateSPFS(int nid, TIntStack toRemove)
    {


        double tempPval = Double.POSITIVE_INFINITY;
        int tempP = Integer.MIN_VALUE;
        DisposableIntIterator it = GNodes.inArcs[nid].getIterator();


        while(it.hasNext())
        {
            int arcId = it.next();
            int orig = GArcs.origs[arcId];
            double spfs = GNodes.spfs.get(orig) + GArcs.costs[arcId];
            if (tempPval > spfs)
            {
                tempPval = spfs;
                tempP = arcId;
            }

        }
        it.dispose();
        double old = GNodes.spfs.set(nid,tempPval);
        GNodes.prevSP.set(nid,tempP);

        if (nid != tinkIndex && old != tempPval)
        {
            it = GNodes.outArcs[nid].getIterator();

            while(it.hasNext())
            {
                int arcId = it.next();
                int dest =  GArcs.dests[arcId];
                if (GNodes.prevSP.get(dest) == arcId)
                {
                    updateSPFS(dest,toRemove);
                }
                double spft = GNodes.spft.get(dest);
                double acost = GArcs.costs[arcId];
                if (!isInStack(arcId) && tempPval + spft+acost > constraint.getIntVar(starts.length).getSup())
                {
                    setInStack(arcId);
                    toRemove.push(arcId);
                }
            }
            it.dispose();
        }

    }

    public void updateLPFS(int nid, TIntStack toRemove)
    {


        double tempPval = Double.NEGATIVE_INFINITY;
        int tempP = Integer.MIN_VALUE;
        DisposableIntIterator it = GNodes.inArcs[nid].getIterator();


        while(it.hasNext())
        {
            int arcId = it.next();
            int orig = GArcs.origs[arcId];
            double lpfs = GNodes.lpfs.get(orig) + GArcs.costs[arcId];
            if (tempPval < lpfs)
            {
                tempPval = lpfs;
                tempP = arcId;
            }

        }
        it.dispose();
        double old = GNodes.lpfs.set(nid,tempPval);
        GNodes.prevLP.set(nid,tempP);


        if (nid != tinkIndex && old != tempPval)
        {
            it = GNodes.outArcs[nid].getIterator();

            while(it.hasNext())
            {
                int arcId = it.next();
                int dest =  GArcs.dests[arcId];
                if (GNodes.prevLP.get(dest) == arcId)
                {
                    updateLPFS(dest,toRemove);
                }
                double lpft = GNodes.lpft.get(dest);
                double acost = GArcs.costs[arcId];
                if (!isInStack(arcId) && tempPval + lpft+acost < constraint.getIntVar(starts.length).getInf())
                {
                    setInStack(arcId);
                    toRemove.push(arcId);
                }
            }
            it.dispose();
        }

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