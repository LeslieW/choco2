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
package choco.cp.solver.constraints.global.multicostregular.structure.list;

import choco.cp.solver.constraints.global.multicostregular.structure.*;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.model.constraints.automaton.FA.Automaton;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.BitSet;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jun 9, 2009
 * Time: 2:05:38 PM
 */


public class LLayeredGraph extends AbstractLayeredGraph {

    public LNode source;

    public LNode tink;

    protected LNode[][] layers;

    protected LNode[] nodes;

    protected LArc[] arcs;


    public LLayeredGraph(final IntDomainVar[] vars, final Automaton pi, final IntSConstraint mcr) throws ContradictionException {
        super(vars, pi, mcr);

        int id = 0;
        ArrayList<LNode> nodelist = new ArrayList<LNode>();
        ArrayList<LArc> arclist = new ArrayList<LArc>();


        ArrayList<TreeSet<LNode>>  layers = new ArrayList<TreeSet<LNode>>();
        int n = vars.length;
        ArrayList<TIntObjectHashMap<LNode>> arr = new ArrayList<TIntObjectHashMap<LNode>>(n);
        for (int i = 0 ; i <= n+1; i++)
        {
            arr.add(new TIntObjectHashMap<LNode>());
            layers.add(new TreeSet<LNode>());
        }

        this.layers = new LNode[n+2][];

        this.source = new LNode(0,pi.getStartingState());
        nodelist.add(this.source);
        this.tink = new LNode(n+1,pi.getNbStates()+1);
        nodelist.add(this.tink);
        arr.get(0).put(pi.getStartingState(),this.source);
        arr.get(n+1).put(pi.getNbStates()+1,this.tink);
        TIntObjectIterator<LNode> layerIter;
        int j;

        for (int i = 0 ; i < n ; i++)
        {

            layerIter = arr.get(i).iterator();
            while (layerIter.hasNext())
            {
                layerIter.advance();
                LNode s = layerIter.value();

                j = vars[i].getInf();
                int sup = vars[i].getSup();
                while (j <= sup)
                {

                    int fol = pi.delta(s.getState(),j);
                    if (fol >= 0)
                    {
                        LNode t = arr.get(i+1).get(fol);
                        if (t == null)
                        {
                            t = new LNode(i+1,fol);
                            arr.get(i+1).put(fol,t);
                            nodelist.add(t);
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
            LNode rem = (LNode) orem;
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
                LNode s = layerIter.value();

                j = vars[i].getInf();
                int sup = vars[i].getSup();
                while (j <= sup)
                {

                    int fol = pi.delta(s.getState(),j);
                    if (fol >= 0)
                    {
                        LNode t = arr.get(i+1).get(fol);
                        if (t != null)
                        {
                            layers.get(i+1).add(t);
                            mark.set(s.getState());
                            LArc e = new LArc(s,t,j,id++);
                            arclist.add(e);
                            s.addOutArc(e);
                            t.addInArc(e);
                            //  sortOut.add(e);
                            //  sortIn.add(e);


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
            LNode end = layerIter.value();
            LArc e = new LArc(end,this.tink,0,id++);
            arclist.add(e);
            end.addOutArc(e);
            this.tink.addInArc(e);
            //  sortOut.add(e);
            //  sortIn.add(e);
        }

        this.arcs = arclist.toArray(new LArc[arclist.size()]);
        this.nodes = nodelist.toArray(new LNode[nodelist.size()]);
        this.inStack = vars[0].getSolver().getEnvironment().makeBitSet(arclist.size());




        int i = 0;
        for (TreeSet<LNode>  hn : layers)
        {
            LNode[] tmp = hn.toArray(new LNode[hn.size()]);
            this.layers[i++] = tmp;
            for (LNode node: tmp)
            {
                node.makeDataStructure(vars[0].getSolver().getEnvironment());
            }
        }

        this.inIterator = new ListInArcIterator(this.arcs[0].dest);
        this.outIterator  = new ListOutArcIterator(this.arcs[0].orig);
        this.activeIterator = new ListAllActiveArcIterator();


        this.initialFilter();
        










    }


    @Override
    public final void removeEdge(int outIndex, IStateIntVector hs) throws ContradictionException {
        removeEdge(arcs[outIndex],hs);
    }

    protected void removeEdge(final LArc e, final IStateIntVector hs) throws ContradictionException {
        inStack.clear(e.getInStackIdx());
        e.setOutArc();        
        if (e.orig.getOutArcs().contain(e))
        {
            e.setOutArc();
            e.orig.getOutArcs().remove(e);
            e.setInArc();
            e.dest.getInArcs().remove(e);
            decQ(e.orig.getLayer(),e.getLabel());
            LNode or = e.orig;
            LNode de = e.dest;
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



    @Override
    public Iterator<IArc> getOutEdgeIterator(INode n) {
        this.outIterator.reset(n);
        return this.outIterator;
    }

    public class ListOutArcIterator implements IOutArcIterator
    {
        StoredIndexedBipartiteSet.BipartiteSetIterator it;


        public ListOutArcIterator(LNode n)
        {
            this.it = n.getOutArcs().getObjectIterator();
        }

        @Override
        public final boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public final IArc next() {
            return (LArc)it.nextObject();
        }

        @Override
        public final void remove() {
            it.remove();
        }

        @Override
        public final void reset(INode n) {
            it.dispose();
            it = ((LNode)n).getOutArcs().getObjectIterator();
        }
    }

    @Override
    public Iterator<IArc> getInEdgeIterator(INode n) {
        this.inIterator.reset(n);
        return this.inIterator;
    }

    public class ListInArcIterator implements IInArcIterator
    {
        StoredIndexedBipartiteSet.BipartiteSetIterator it;


        public ListInArcIterator(LNode n)
        {
            this.it = n.getInArcs().getObjectIterator();
        }

        @Override
        public final boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public final IArc next() {
            return (LArc)it.nextObject();
        }

        @Override
        public final void remove() {
            it.remove();
        }

        @Override
        public final void reset(INode n) {
            it.dispose();
            it = ((LNode)n).getInArcs().getObjectIterator();
        }
    }

    @Override
    public final Iterator<IArc> getAllActiveEdgeIterator() {
        this.activeIterator.reset();
        return this.activeIterator;
    }

    private class ListAllActiveArcIterator implements IAllActiveArcIterator {

        int layer;
        int node;
        StoredIndexedBipartiteSet.BipartiteSetIterator it;


        public ListAllActiveArcIterator()
        {
            this.layer = 0;
            this.node = 0;
            it = layers[0][0].getOutArcs().getObjectIterator();
        }

        @Override
        public boolean hasNext() {
            if (it.hasNext())
                return true;
            else
            {
                boolean found = false;
                while (this.layer < nbLayers -1 && !found)
                {
                    if (this.node < layers[this.layer].length -1)
                    {
                        this.node++;
                        it = layers[this.layer][this.node].getOutArcs().getObjectIterator();
                        if (it.hasNext())
                            found = true;
                        else it.dispose();

                    }
                    else
                    {
                        this.node = 0;
                        this.layer++;
                    }
                }

                return found;
            }
        }

        @Override
        public final IArc next() {
            return (LArc)it.nextObject();
        }

        @Override
        public final void remove() {
            it.remove();
        }

        @Override
        public void reset() {
            this.layer = 0;
            this.node = 0;
            it = layers[0][0].getOutArcs().getObjectIterator();
        }
    }

    @Override
    public final INode[] getLayer(int i) {
        return this.layers[i];
    }


    @Override
    public final INode getSource() {
        return source;
    }

    @Override
    public final INode getTink() {
        return tink;
    }



}