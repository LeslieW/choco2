package choco.cp.solver.constraints.global.automata.fast_regular;


import choco.cp.solver.constraints.global.automata.fast_regular.structure.Arc;
import choco.cp.solver.constraints.global.automata.fast_regular.structure.Node;
import choco.cp.solver.constraints.global.automata.fast_regular.structure.StoredDirectedMultiGraph;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.ContradictionException;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import org.jgrapht.graph.DirectedMultigraph;


import java.util.*;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntStack;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Oct 30, 2009
 * Time: 3:43:21 PM
 */

public class FastRegular extends AbstractLargeIntSConstraint {


    /**
     * Reference to the automaton representing the accepted language
     */
    Automaton auto;



    StoredDirectedMultiGraph graph;



    /**
     * Construct a new explained regular constraint
     * @param vars Variables that must form a word accepted by auto
     * @param auto An automaton forming a regular languauge
     */
    public FastRegular(IntDomainVar[] vars, Automaton auto) {
        super(vars);
        this.auto = auto;


        int aid = 0;
        int nid = 0;


        int[] offsets = new int[vars.length];
        int[] sizes = new int[vars.length];
        int[] starts = new int[vars.length];

        int totalSizes = 0;

        starts[0] = 0;
        for (int i = 0 ; i < vars.length ; i++)
        {
            offsets[i] = vars[i].getInf();
            sizes[i] = vars[i].getSup() - vars[i].getInf()+1;
            if (i > 0) starts[i] = sizes[i-1] + starts[i-1];
            totalSizes += sizes[i];
        }



        DirectedMultigraph<Node,Arc> graph;

        int n = vars.length;
        graph = new DirectedMultigraph<Node, Arc>(new Arc.ArcFacroty());
        ArrayList<HashSet<Arc>> tmp = new ArrayList<HashSet<Arc>>(totalSizes);
        for (int i = 0 ; i < totalSizes ;i++)
            tmp.add(new HashSet<Arc>());



        int i,j,k;
        DisposableIntIterator varIter;
        TIntIterator layerIter;
        TIntIterator qijIter;

        ArrayList<TIntHashSet> layer = new ArrayList<TIntHashSet>();
        TIntHashSet[] tmpQ = new TIntHashSet[totalSizes];
        // DLList[vars.length+1];

        for (i = 0 ; i <= n ; i++)
        {
            layer.add(new TIntHashSet());// = new DLList(nbNodes);
        }

        //forward pass, construct all paths described by the automaton for word of length nbVars.

        layer.get(0).add(auto.getStartingState());

        for (i = 0 ; i < n ; i++)
        {
            varIter = vars[i].getDomain().getIterator();
            while(varIter.hasNext())
            {
                j = varIter.next();
                layerIter = layer.get(i).iterator();//getIterator();
                while(layerIter.hasNext())
                {
                    k = layerIter.next();
                    int succ = auto.delta(k,j);
                    if (succ >= 0)
                    {
                        layer.get(i+1).add(succ);
                        //incrQ(i,j,);

                        int idx = starts[i]+j-offsets[i];
                        if (tmpQ[idx] == null)
                            tmpQ[idx] =  new TIntHashSet();

                        tmpQ[idx].add(k);


                    }
                }
            }
            varIter.dispose();
        }

        //removing reachable non accepting states

        layerIter = layer.get(n).iterator();
        while (layerIter.hasNext())
        {
            k = layerIter.next();
            if (!auto.isAccepting(k))
            {
                layerIter.remove();
            }

        }


        //backward pass, removing arcs that does not lead to an accepting state
        int nbNodes = auto.size();
        BitSet mark = new BitSet(nbNodes);

        Node[] in = new Node[auto.size()*(n+1)];

        for (i = n -1 ; i >=0 ; i--)
        {
            mark.clear(0,nbNodes);
            varIter = vars[i].getDomain().getIterator();
            while (varIter.hasNext())
            {
                j = varIter.next();
                int idx = starts[i]+j-offsets[i];
                TIntHashSet l = tmpQ[idx];
                if (l!= null)
                {
                    qijIter = l.iterator();
                    while (qijIter.hasNext())
                    {
                        k = qijIter.next();
                        int qn = auto.delta(k,j);
                        if (layer.get(i+1).contains(qn))
                        {
                            Node a = in[i*auto.size()+k];
                            if (a == null)
                            {
                                a = new Node(k,i,nid++);
                                in[i*auto.size()+k] = a;
                                graph.addVertex(a);
                            }



                            Node b = in[(i+1)*auto.size()+qn];
                            if (b == null)
                            {
                                b = new Node(qn,i+1,nid++);
                                in[(i+1)*auto.size()+qn] = b;
                                graph.addVertex(b);
                            }


                            Arc arc = new Arc(a,b,j,aid++);
                            graph.addEdge(a,b,arc);
                            tmp.get(idx).add(arc);

                            // addToOutarc(k,qn,j,i);
                            //  addToInarc(k,qn,j,i+1);
                            mark.set(k);
                        }
                        else
                            qijIter.remove();
                        //  decrQ(i,j);
                    }
                }
            }
            layerIter = layer.get(i).iterator();

            // If no more arcs go out of a given state in the layer, then we remove the state from that layer
            while (layerIter.hasNext())
                if(!mark.get(layerIter.next()))
                    layerIter.remove();
        }



        this.graph = new StoredDirectedMultiGraph(this,graph,starts,offsets,totalSizes);


    }


    TIntStack temp  = new TIntStack();

    public void awakeOnRem(int i, int j) throws ContradictionException {
        StoredIndexedBipartiteSet sup = graph.getSupport(i,j);

        if (sup != null)
        {

            DisposableIntIterator it = sup.getIterator();
            while (it.hasNext())
            {
                int arcId = it.next();
                temp.push(arcId);


            }
            it.dispose();

            while(temp.size() > 0)
            {
                int arcId = temp.pop();
                try{
                    graph.removeArc(arcId);
                } catch (ContradictionException e)
                {
                    temp.clear();
                    throw e;
                }
            }

        }
        System.out.print("");



    }



    public void propagate() throws ContradictionException {

    }

    public void awake() throws ContradictionException
    {
        for (int i  = 0 ; i < vars.length ; i++)
        {
            for (int j = vars[i].getInf() ; j <= vars[i].getSup() ; j = vars[i].getNextDomainValue(j))
            {
                StoredIndexedBipartiteSet sup = graph.getSupport(i,j);
                if (sup == null || sup.isEmpty())
                {
                    vars[i].removeVal(j,this.getConstraintIdx(i));
                }
            }
        }


    }

    public int getFilteredEventMask(int idx)
    {
        return IntVarEvent.REMVALbitvector;
    }







}