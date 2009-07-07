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
package choco.cp.solver.constraints.global.multicostregular.structure.bitset;

import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.cp.solver.constraints.global.multicostregular.algo.PathFinder;
import choco.cp.solver.constraints.global.multicostregular.structure.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import gnu.trove.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jul 16, 2008
 * Time: 6:01:21 PM
 */
public class BLayeredGraph extends AbstractLayeredGraph {




    /**
     * Layer view of the layered graph
     */
    protected final BNode[][] layers;



    /**
     * List of all created arcs, sorted in a way that for a given
     * Node, all incomming arcs have a contigous position in the list
     */
    protected final BArc[] sortIn;

    /**
     * List of all created arcs, sorted in a way that for a given
     * Node, all outgoing arcs have a contigous position in the list
     */
    protected final BArc[] sortOut;

    /**
     * Restorable bitset that indicates whether a given incomming arc is active or not
     */
    protected final IStateBitSet activeIn;

    /**
     * Restorable bitset that indicates whether a given outgoing arc is active or not
     */
    protected final IStateBitSet activeOut;


    /**
     * Reference to the source node of the graph
     */
    protected final BNode source;

    /**
     * Reference to the dummy tink node of the graph
     */
    protected final BNode tink;




    /**
     * Build a new Layered graph given a sequence of variables vars, an finite automaton pi and a multicostregular constraint
     * @param vars the sequence of variables
     * @param pi a finite automaton
     * @param mcr the associated multicostregular constraint
     * @throws ContradictionException if a domain gets empty
     */
    public BLayeredGraph(final IntDomainVar[] vars, final Automaton pi, final IntSConstraint mcr) throws ContradictionException
    {
        super(vars,pi,mcr);
        ArrayList<BArc> sortIn = new ArrayList<BArc>();
        ArrayList<BArc> sortOut = new ArrayList<BArc>();
        /*this.Q = new ArrayList<IStateInt>();
        this.Q.ensureCapacity(biggestDomainSize *vars.length);
        for (int i  =0 ; i < biggestDomainSize *vars.length ;i++)
            this.Q.add(null);
          */




        this.activeIterator = new BitSetAllActiveArcIterator();

        ArrayList<TreeSet<BNode>>  layers = new ArrayList<TreeSet<BNode>>();
        int n = vars.length;
        ArrayList<TIntObjectHashMap<BNode>> arr = new ArrayList<TIntObjectHashMap<BNode>>(n);
        for (int i = 0 ; i <= n+1; i++)
        {
            arr.add(new TIntObjectHashMap<BNode>());
            layers.add(new TreeSet<BNode>());
        }

        this.layers = new BNode[n+2][];

        this.source = new BNode(0,pi.getStartingState());
        this.tink = new BNode(n+1,pi.getNbStates()+1);
        arr.get(0).put(pi.getStartingState(),this.source);
        arr.get(n+1).put(pi.getNbStates()+1,this.tink);
        TIntObjectIterator<BNode> layerIter;
        int j;

        for (int i = 0 ; i < n ; i++)
        {

            layerIter = arr.get(i).iterator();
            while (layerIter.hasNext())
            {
                layerIter.advance();
                BNode s = layerIter.value();

                j = vars[i].getInf();
                int sup = vars[i].getSup();
                while (j <= sup)
                {

                    int fol = pi.delta(s.getState(),j);
                    if (fol >= 0)
                    {
                        BNode t = arr.get(i+1).get(fol);
                        if (t == null)
                        {
                            t = new BNode(i+1,fol);
                            arr.get(i+1).put(fol,t);
                        }
                        incQ(i,j);

                    }
                    j = vars[i].getNextDomainValue(j);

                }
            }

        }


        TIntHashSet toRem = new TIntHashSet();
        for (Object orem : arr.get(n).getValues())
        {
            BNode rem = (BNode) orem;
            if (!pi.isAccepting(rem.getState()))
            {
                toRem.add(rem.getState());
            }
        }

        for (TIntIterator it = toRem.iterator(); it.hasNext();)
            arr.get(n).remove(it.next());

        layers.get(n+1).add(this.tink);
        layers.get(0).add(this.source);
        BitSet mark = new BitSet(pi.getNbStates());
        for (int i = n-1 ; i >=0 ; i--)
        {

            mark.clear(0,pi.getNbStates());
            layerIter = arr.get(i).iterator();
            while (layerIter.hasNext())
            {
                layerIter.advance();
                BNode s = layerIter.value();

                j = vars[i].getInf();
                int sup = vars[i].getSup();
                while (j <= sup)
                {

                    int fol = pi.delta(s.getState(),j);
                    if (fol >= 0)
                    {
                        BNode t = arr.get(i+1).get(fol);
                        if (t != null)
                        {
                            layers.get(i+1).add(t);
                            mark.set(s.getState());
                            BArc e = new BArc(s,t,j);
                            sortOut.add(e);
                            sortIn.add(e);



                        }
                        else
                        {
                            decQ(i,j);
                        }
                    }

                    j = vars[i].getNextDomainValue(j);

                }
            }
            TIntObjectIterator it = arr.get(i).iterator();
            while (it.hasNext())
            {
                it.advance();
                if(!mark.get(it.key()))
                    it.remove();
            }
        }

        layerIter = arr.get(n).iterator();
        while (layerIter.hasNext())
        {
            layerIter.advance();
            BNode end = layerIter.value();
            BArc e = new BArc(end,this.tink,0);
            sortOut.add(e);
            sortIn.add(e);
        }



        activeOut = vars[0].getSolver().getEnvironment().makeBitSet(sortOut.size());
        activeIn = vars[0].getSolver().getEnvironment().makeBitSet(sortOut.size());
        inStack = vars[0].getSolver().getEnvironment().makeBitSet(sortOut.size());


        for (int i = 0  ; i < sortOut.size() ; i++) {
            activeOut.set(i);
            activeIn.set(i);
        }

        //trier sortIn;

        Collections.sort(sortIn);

        Collections.sort(sortOut, IArc.outComparator);
        this.sortIn = sortIn.toArray(new BArc[sortIn.size()]);
        this.sortOut = sortOut.toArray(new BArc[sortOut.size()]);

        if (this.sortOut.length > 0)
        {

            BNode out = this.sortOut[0].orig;
            BNode in =this.sortIn[0].dest;

            // Creating the future iterators
            this.inIterator = new BitSetInArcIterator(in);
            this.outIterator  = new BitSetOutArcIterator(out);

            in.inOffset = 0;
            out.outOffset = 0;

            in.nbInArcs = 1;
            out.nbOutArcs = 1;

            BNode next;

            this.sortOut[0].outIndex = 0;
            this.sortIn[0].inIndex = 0;

            for (int i = 0 ; i < sortOut.size()-1 ; i++)
            {
                this.sortOut[i+1].outIndex = i+1;
                out = this.sortOut[i].orig;
                next= this.sortOut[i+1].orig;
                if (out == next)
                {
                    out.nbOutArcs++;
                }
                else
                {
                    next.outOffset = i+1;
                    next.nbOutArcs++;
                }

                this.sortIn[i+1].inIndex = i+1;
                in = this.sortIn[i].dest;
                next = this.sortIn[i+1].dest;
                if (in == next)
                {
                    in.nbInArcs++;
                }
                else
                {
                    next.inOffset = i+1;
                    next.nbInArcs++;
                }
            }


            int i = 0;
            for (TreeSet<BNode>  hn : layers)
            {
                BNode[] tmp = hn.toArray(new BNode[hn.size()]);
                this.layers[i++] = tmp;
            }
        }
        else
        {
            this.inIterator = null;
            this.outIterator = null;
        }

        // System.out.println("NBEDGES IN LAYERED GRAPH : "+sortOut.size());
        this.initialFilter();






    }



    /**
     * Getter to the bitset of active outArc.
     * @return an instance of a storable bitset
     */
    public final IStateBitSet getActiveOut() {
        return activeOut;
    }



    /**
     * Removes the edge located at outIndex in the sortOut list
     * Adds any impacted arcs to the to be removed vector hs
     * @param outIndex the index of the arc in sortOut list
     * @param hs the to be removed vector
     * @throws ContradictionException if a domain gets empty
     */
    public void removeEdge(final int outIndex, final IStateIntVector hs) throws ContradictionException {
        removeEdge(sortOut[outIndex],hs);
    }

    /**
     * Removes the given edge
     * Adds any impacted arcs to the to be removed vector hs
     * @param e the arc to remove
     * @param hs the to be removed vector
     * @throws ContradictionException if a domain gets empty
     */
    protected void removeEdge(final BArc e, final IStateIntVector hs) throws ContradictionException {
        inStack.clear(e.outIndex);

        if (activeOut.get(e.outIndex))
        {

            activeIn.clear(e.inIndex);
            activeOut.clear(e.outIndex);
            decQ(e.orig.getLayer(),e.getLabel());
            BNode or = e.orig;
            BNode de = e.dest;
            Iterator<IArc> outIt = getOutEdgeIterator(or);
            Iterator<IArc> inIt;
            if (!outIt.hasNext())
            {
                inIt =getInEdgeIterator(or);
                while (inIt.hasNext())
                {
                    IArc rm = inIt.next();

                    if (!inStack.get(rm.getInStackIdx())) {
                        inStack.set(rm.getInStackIdx());
                        hs.add(rm.getInStackIdx());
                    }

                }
            }
            inIt =getInEdgeIterator(de);
            if (!inIt.hasNext())
            {
                outIt = getOutEdgeIterator(de);
                while(outIt.hasNext())
                {
                    IArc rm = outIt.next();

                    if (!inStack.get(rm.getInStackIdx()))
                    {
                        inStack.set(rm.getInStackIdx());
                        hs.add(rm.getInStackIdx());
                    }
                }
            }
        }
        else
        {
            System.err.println("SHOULD NOT BE HERE ARC IS BEING REMOVED TWICE");
        }

    }


    /**
     * return an iterator over the outgoing arcs of a given node n
     * @param n the given node
     * @return an iterator over Arcs
     */
    public Iterator<IArc> getOutEdgeIterator(final INode n)
    {
        this.outIterator.reset(n);

        return this.outIterator;
    }

    /**
     * Inner class to define an iterator over outgoing arcs
     */
    protected class BitSetOutArcIterator implements IOutArcIterator {

        public int currentBit;
        public int lastReturned = Integer.MIN_VALUE;
        public BNode n;

        public BitSetOutArcIterator(final BNode n)
        {
            this.n = n;
            this.currentBit = activeOut.nextSetBit(n.outOffset);
        }


        public final boolean hasNext() {
            return currentBit >= 0 && currentBit <= n.outOffset +n.nbOutArcs -1;
        }

        public IArc next() {
            IArc out = sortOut[currentBit];
            lastReturned = currentBit;
            currentBit = activeOut.nextSetBit(currentBit+1);
            return out;

        }

        public void remove() {
            activeOut.clear(lastReturned);
            BArc e = sortOut[lastReturned];
            activeIn.clear(e.inIndex);
        }

        public void reset(INode n)
        {
            this.n = (BNode)n;
            this.lastReturned  = Integer.MIN_VALUE;
            this.currentBit = activeOut.nextSetBit(this.n.outOffset);
        }

    }

    /**
     * return an iterator over the incomming arcs of a given node n
     * @param n the given node
     * @return an iterator over Arcs
     */
    public Iterator<IArc> getInEdgeIterator(final INode n)
    {
        this.inIterator.reset(n);
        return this.inIterator;
    }



    /**
     * Inner class to define an iterator over incomming arcs
     */
    public class BitSetInArcIterator implements IInArcIterator {

        public int currentBit;
        public int lastReturned = Integer.MIN_VALUE;
        public BNode n;

        public BitSetInArcIterator(final BNode n)
        {
            this.n = n;
            this.currentBit = activeIn.nextSetBit(n.inOffset);
        }


        public final boolean hasNext() {
            return currentBit >= 0 && currentBit <= n.inOffset +n.nbInArcs -1;
        }

        public IArc next() {
            IArc out = sortIn[currentBit];
            lastReturned = currentBit;
            currentBit = activeIn.nextSetBit(currentBit+1);
            return out;

        }

        public void remove() {
            activeIn.clear(lastReturned);
            BArc e = sortIn[lastReturned];
            activeOut.clear(e.outIndex);
        }

        public void reset(INode n)
        {
            this.n = (BNode)n;
            this.lastReturned = Integer.MIN_VALUE;
            this.currentBit = activeIn.nextSetBit(this.n.inOffset);
        }
    }

    /**
     * Inner class to define an iterator over all arcs in the graph
     */
    public class BitSetAllActiveArcIterator implements IAllActiveArcIterator {

        int current;

        public BitSetAllActiveArcIterator()
        {

            current = -1   ;


        }

        public boolean hasNext() {
            return activeOut.nextSetBit(current+1) >= 0;
        }

        public IArc next() {
            current = activeOut.nextSetBit(current+1);
            return sortOut[current];
        }

        public void remove() {
            BArc e = sortOut[current];
            activeOut.clear(e.outIndex);
            activeIn.clear(e.inIndex);
        }
        public void reset()
        {
            this.current = -1;
        }
    }

    /**
     * return an iterator over all arcs in the graph
     * @return an iterator over Arcs
     */
    public final Iterator<IArc> getAllActiveEdgeIterator()
    {
        this.activeIterator.reset();
        return this.activeIterator;
    }


    /**
     * Accessor to layers of the graph
     * @param i the index of the wanted layer
     * @return an array of node corresponding to the ith layer
     */
    public final BNode[] getLayer(final int i)
    {
        return this.layers[i];
    }



    /**
     * Accessor to the source
     * @return the source
     */
    public final INode getSource()
    {
        return this.source;
    }

    /**
     * Accessor to the tink
     * @return the tink
     */
    public final INode getTink()
    {
        return this.tink;
    }


    /*  public boolean isTupleInGraph(int[] tuple)
{

    Node n = this.getSource();
    Iterator<Arc> it = getOutEdgeIterator(n);
    for (int val : tuple)
    {
        boolean cond = false;
        while (it.hasNext() && !cond)
        {
            Arc e = it.next();
            if (e.j == val)
            {
                it = getOutEdgeIterator(e.dest);
                cond = true;
            }
        }
        if (!cond)
            return false;
    }
    return true;
}    */

    /**
     * write the graph to a file in the dot format
     * @param nb index of the graph
     */
    public void toDotty(int nb)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("digraph G { \nrankdir=LR; \nnode [width=0.3 height=0.3 shape = circle];\n");
        for (int i = 0 ; i < this.nbLayers ; i++)
        {
            BNode[] tmp = this.getLayer(i);
            for (BNode n : tmp)
            {
                Iterator<IArc> it = this.getOutEdgeIterator(n);
                while (it.hasNext())
                {
                    IArc e = it.next();
                    sb.append(n.getId()).append(" -> ").append(e.getDestination().getId()).append(" [label = \"{").append(e.getLabel()).append("}\"];\n");

                }
            }

        }
        sb.append("1 -> 999 [label = \"").append(this.mcr.getIntVar(this.mcr.getNbVars() - 1).getInf()).append("-").append(this.mcr.getIntVar(this.mcr.getNbVars() - 1).getSup()).append("\"];\n");
        sb.append("}");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("lg_58_"+nb+".dot")));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
