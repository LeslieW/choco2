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
package choco.cp.solver.constraints.global.multicostregular.structure;

import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.cp.solver.constraints.global.multicostregular.algo.PathFinder;

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
public class LayeredGraph {


    /**
     * Reusable iterator over all active arcs
     */
    protected final AllActiveArcIterator activeIterator;

    /**
     * Reusable iterator over incomming arcs of a given node
     */
    protected final InArcIterator inIterator;

    /**
     * Reusable iterator over outgoing arcs of a given node
     */
    protected final OutArcIterator outIterator;

    /**
     * Layer view of the layered graph
     */
    protected final Node[][] layers;

    /**
     * Number of layers in the graph (== layers.size())
     */
    protected final int nbLayers;

    /**
     * List of all created arcs, sorted in a way that for a given
     * Node, all incomming arcs have a contigous position in the list
     */
    protected final Arc[] sortIn;

    /**
     * List of all created arcs, sorted in a way that for a given
     * Node, all outgoing arcs have a contigous position in the list
     */
    protected final Arc[] sortOut;

    /**
     * Restorable bitset that indicates whether a given incomming arc is active or not
     */
    protected final IStateBitSet activeIn;

    /**
     * Restorable bitset that indicates whether a given outgoing arc is active or not
     */
    protected final IStateBitSet activeOut;

    /**
     * Restorable bitset that indicates wheter an arc is in the to be removed stack or not
     */
    protected final IStateBitSet inStack;

    /**
     * Variables sequence used to build this layered graph
     */
    protected final IntDomainVar[] vars;


    /**
     * Support for every variable-value pairs if Qij <= 0, then xi != j
     */
    protected final IStateIntVector Q;

    /**
     * The biggest domain size of the variables vars
     */
    protected int biggestDomainSize;


    /**
     * Reference to the source node of the graph
     */
    protected final Node source;

    /**
     * Reference to the dummy tink node of the graph
     */
    protected final Node tink;

    /**
     * Instance of the class containing algorithm to find path in this graph
     */
    protected final PathFinder pathFinder;

    /**
     * Reference to the multicostregular constraint this layered graph is associated with
     */
    protected final IntSConstraint mcr;


    /**
     * Build a new Layered graph given a sequence of variables vars, an finite automaton pi and a multicostregular constraint
     * @param vars the sequence of variables
     * @param pi a finite automaton
     * @param mcr the associated multicostregular constraint
     * @throws ContradictionException if a domain gets empty
     */
    public LayeredGraph(final IntDomainVar[] vars, final Automaton pi, final IntSConstraint mcr) throws ContradictionException
    {
        this.vars =vars;
        this.biggestDomainSize = Integer.MIN_VALUE;
        this.mcr = mcr;
        for (IntDomainVar v : vars)
        {
            int sz = v.getSup()+1;
            if (biggestDomainSize < sz)
                biggestDomainSize =sz;
        }

        ArrayList<Arc> sortIn = new ArrayList<Arc>();
        ArrayList<Arc> sortOut = new ArrayList<Arc>();
        /*this.Q = new ArrayList<IStateInt>();
        this.Q.ensureCapacity(biggestDomainSize *vars.length);
        for (int i  =0 ; i < biggestDomainSize *vars.length ;i++)
            this.Q.add(null);
          */
        this.Q = vars[0].getSolver().getEnvironment().makeIntVector();
        for (int i  =0 ; i < biggestDomainSize *vars.length ;i++)
            this.Q.add(0);



        this.activeIterator = new AllActiveArcIterator();

        ArrayList<TreeSet<Node>>  layers = new ArrayList<TreeSet<Node>>();
        int n = vars.length;
        ArrayList<TIntObjectHashMap<Node>> arr = new ArrayList<TIntObjectHashMap<Node>>(n);
        for (int i = 0 ; i <= n+1; i++)
        {
            arr.add(new TIntObjectHashMap<Node>());
            layers.add(new TreeSet<Node>());
        }
        this.nbLayers = n+2;
        this.layers = new Node[n+2][];

        this.source = new Node(0,pi.getStartingState());
        this.tink = new Node(n+1,Integer.MAX_VALUE);
        arr.get(0).put(pi.getStartingState(),this.source);
        arr.get(n+1).put(Integer.MAX_VALUE,this.tink);
        TIntObjectIterator<Node> layerIter;
        int j;

        for (int i = 0 ; i < n ; i++)
        {

            layerIter = arr.get(i).iterator();
            while (layerIter.hasNext())
            {
                layerIter.advance();
                Node s = layerIter.value();

                j = vars[i].getInf();
                int sup = vars[i].getSup();
                while (j <= sup)
                {

                    int fol = pi.delta(s.state,j);
                    if (fol >= 0)
                    {
                        Node t = arr.get(i+1).get(fol);
                        if (t == null)
                        {
                            t = new Node(i+1,fol);
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
            Node rem = (Node) orem;
            if (!pi.isAccepting(rem.state))
            {
                toRem.add(rem.state);
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
                Node s = layerIter.value();

                j = vars[i].getInf();
                int sup = vars[i].getSup();
                while (j <= sup)
                {

                    int fol = pi.delta(s.state,j);
                    if (fol >= 0)
                    {
                        Node t = arr.get(i+1).get(fol);
                        if (t != null)
                        {
                            layers.get(i+1).add(t);
                            mark.set(s.state);
                            Arc e = new Arc(s,t,j);
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
            Node end = layerIter.value();
            Arc e = new Arc(end,this.tink,0);
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

        Collections.sort(sortOut,Arc.outComparator);
        this.sortIn = sortIn.toArray(new Arc[sortIn.size()]);
        this.sortOut = sortOut.toArray(new Arc[sortOut.size()]);

        if (this.sortOut.length > 0)
        {

            Node out = this.sortOut[0].orig;
            Node in =this.sortIn[0].dest;

            // Creating the future iterators
            this.inIterator = new InArcIterator(in);
            this.outIterator  = new OutArcIterator(out);

            in.inOffset = 0;
            out.outOffset = 0;

            in.nbInArcs = 1;
            out.nbOutArcs = 1;

            Node next;

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
            for (TreeSet<Node>  hn : layers)
            {
                Node[] tmp = hn.toArray(new Node[hn.size()]);
                this.layers[i++] = tmp;
            }
        }
        else
        {
            this.inIterator = null;
            this.outIterator = null;
        }

        System.out.println("NBEDGES IN LAYERED GRAPH : "+sortOut.size());
        this.initialFilter();

        this.pathFinder = new PathFinder(this);

        



    }

    /**
     * Performs initial pruning if some arcs are already inconsistent
     * @throws ContradictionException  if a domain gets empty
     */
    protected void initialFilter() throws ContradictionException {
        for (int idx = 0 ; idx < this.Q.size() ; idx++)
        {
            int i = idx/ biggestDomainSize;
            int j = idx% biggestDomainSize;
            int tmp = this.Q.get(idx);
            if (tmp == Integer.MIN_VALUE || tmp == 0)
            {
                vars[i].removeVal(j,this.mcr.getConstraintIdx(i));
            }
        }
    }

    /**
     * Returns the restorable int counting the number of support for variable i value j
     * create one if not already done
     * @param i the index of the variable
     * @param j the value
     * @return the considered  IStateInt
     */
    protected  int getQ(final int i, final int j)
    {
        int idx  = i* biggestDomainSize +j;
        return this.Q.get(idx);
    }

    /**
     * set the number of support for the variable-value pair (x_i,j) to be a given value
     * @param i the index of the variable
     * @param j the value
     * @param val the new value for the number of support
     */
    protected  void setQ(final int i, final int j,final int val)
    {
        int idx  = i* biggestDomainSize +j;
        this.Q.set(idx,val);
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

    /**
     * Getter to the bitset of active outArc.
     * @return an instance of a storable bitset
     */
    public final IStateBitSet getActiveOut() {
        return activeOut;
    }

    /**
     * remove 1 from Qij
     * @param i the index of a variable
     * @param j the considered value
     * @throws ContradictionException if a domain get empty
     */
    protected void decQ(final int i, final int j) throws ContradictionException {
        if (i < vars.length)
        {
            int tmp = getQ(i,j) -1;
            setQ(i,j,tmp);
            if (tmp <= 0)
                vars[i].removeVal(j,this.mcr.getConstraintIdx(i));
        }

    }

    /**
     * add 1 to Qij
     * @param i the index of a variable
     * @param j the considered value
     */
    protected  void incQ(final int i, final int j)
    {
        setQ(i,j,getQ(i,j)+1);
    }


    /**
     * Accessor to the pathfinder of this graph
     * @return a pathfinder instance
     */
    public final PathFinder getPF()
    {
        return pathFinder;
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
    protected void removeEdge(final Arc e, final IStateIntVector hs) throws ContradictionException {
        inStack.clear(e.outIndex);

        if (activeOut.get(e.outIndex))
        {

            activeIn.clear(e.inIndex);
            activeOut.clear(e.outIndex);
            decQ(e.orig.layer,e.j);
            Node or = e.orig;
            Node de = e.dest;
            Iterator<Arc> outIt = getOutEdgeIterator(or);
            Iterator<Arc> inIt;
            if (!outIt.hasNext())
            {
                inIt =getInEdgeIterator(or);
                while (inIt.hasNext())
                {
                    Arc rm = inIt.next();

                    if (!inStack.get(rm.outIndex)) {
                        inStack.set(rm.outIndex);
                        hs.add(rm.outIndex);
                    }

                }
            }
            inIt =getInEdgeIterator(de);
            if (!inIt.hasNext())
            {
                outIt = getOutEdgeIterator(de);
                while(outIt.hasNext())
                {
                    Arc rm = outIt.next();

                    if (!inStack.get(rm.outIndex))
                    {
                        inStack.set(rm.outIndex);
                        hs.add(rm.outIndex);
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
    public Iterator<Arc> getOutEdgeIterator(final Node n)
    {
        this.outIterator.n = n;
        this.outIterator.lastReturned  = Integer.MIN_VALUE;
        this.outIterator.currentBit = activeOut.nextSetBit(n.outOffset);
        return this.outIterator;
    }

    /**
     * Inner class to define an iterator over outgoing arcs
     */
    protected class OutArcIterator implements Iterator<Arc> {

        public int currentBit;
        public int lastReturned = Integer.MIN_VALUE;
        public Node n;

        public OutArcIterator(final Node n)
        {
            this.n = n;
            this.currentBit = activeOut.nextSetBit(n.outOffset);
        }


        public final boolean hasNext() {
            return currentBit >= 0 && currentBit <= n.outOffset +n.nbOutArcs -1;
        }

        public Arc next() {
            Arc out = sortOut[currentBit];
            lastReturned = currentBit;
            currentBit = activeOut.nextSetBit(currentBit+1);
            return out;

        }

        public void remove() {
            activeOut.clear(lastReturned);
            Arc e = sortOut[lastReturned];
            activeIn.clear(e.inIndex);
        }
    }

    /**
     * return an iterator over the incomming arcs of a given node n
     * @param n the given node
     * @return an iterator over Arcs
     */
    public Iterator<Arc> getInEdgeIterator(final Node n)
    {
        this.inIterator.n = n;
        this.inIterator.lastReturned = Integer.MIN_VALUE;
        this.inIterator.currentBit = activeIn.nextSetBit(n.inOffset);
        return this.inIterator;
    }



    /**
     * Inner class to define an iterator over incomming arcs
     */
    public class InArcIterator implements Iterator<Arc> {

        public int currentBit;
        public int lastReturned = Integer.MIN_VALUE;
        public Node n;

        public InArcIterator(final Node n)
        {
            this.n = n;
            this.currentBit = activeIn.nextSetBit(n.inOffset);
        }


        public final boolean hasNext() {
            return currentBit >= 0 && currentBit <= n.inOffset +n.nbInArcs -1;
        }

        public  Arc next() {
            Arc out = sortIn[currentBit];
            lastReturned = currentBit;
            currentBit = activeIn.nextSetBit(currentBit+1);
            return out;

        }

        public void remove() {
            activeIn.clear(lastReturned);
            Arc e = sortIn[lastReturned];
            activeOut.clear(e.outIndex);
        }
    }

    /**
     * Inner class to define an iterator over all arcs in the graph
     */
    public class AllActiveArcIterator implements Iterator<Arc>
    {

        int current;

        public AllActiveArcIterator()
        {

            current = -1   ;


        }

        public boolean hasNext() {
            return activeOut.nextSetBit(current+1) >= 0;
        }

        public Arc next() {
            current = activeOut.nextSetBit(current+1);
            return sortOut[current];
        }

        public void remove() {
            Arc e = sortOut[current];
            activeOut.clear(e.outIndex);
            activeIn.clear(e.inIndex);
        }
    }

    /**
     * return an iterator over all arcs in the graph
     * @return an iterator over Arcs
     */
    public final Iterator<Arc> getAllActiveEdgeIterator()
    {
        this.activeIterator.current = -1;
        return this.activeIterator;
    }


    /**
     * Accessor to layers of the graph
     * @param i the index of the wanted layer
     * @return an array of node corresponding to the ith layer
     */
    public final Node[] getLayer(final int i)
    {
        return this.layers[i];
    }

    /**
     * Accessor to the number of layers
     * @return the number of layers
     */
    public final int getNbLayers()
    {
        return this.nbLayers;
    }


    /**
     * Accessor to the source
     * @return the source
     */
    public final Node getSource()
    {
        return this.source;
    }

    /**
     * Accessor to the tink
     * @return the tink
     */
    public final Node getTink()
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
            Node[] tmp = this.getLayer(i);
            for (Node n : tmp)
            {
                Iterator<Arc> it = this.getOutEdgeIterator(n);
                while (it.hasNext())
                {
                    Arc e = it.next();
                    sb.append(n.id).append(" -> ").append(e.dest.id).append(" [label = \"{").append(e.j).append("}\"];\n");

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
