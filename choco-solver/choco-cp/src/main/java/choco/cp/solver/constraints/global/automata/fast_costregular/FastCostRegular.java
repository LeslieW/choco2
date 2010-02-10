package choco.cp.solver.constraints.global.automata.fast_costregular;

import choco.cp.solver.CPSolver;
import choco.kernel.solver.constraints.global.automata.fast_costregular.structure.Arc;
import choco.kernel.solver.constraints.global.automata.fast_costregular.structure.Node;
import choco.kernel.solver.constraints.global.automata.fast_costregular.structure.StoredValuedDirectedMultiGraph;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IStateBool;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntStack;
import gnu.trove.TIntArrayList;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Feb 4, 2010
 * Time: 1:03:04 PM
 */
public class FastCostRegular extends AbstractLargeIntSConstraint{


    IntDomainVar[] vs;
    IntDomainVar z;
    StoredValuedDirectedMultiGraph graph;
    TIntStack toRemove;
    IStateBool boundChange;
    int lastWorld = -1;


    public FastCostRegular(IntDomainVar[] vars, Automaton pi, double[][][] costs) {
        super(vars);


        this.vs = new IntDomainVar[vars.length-1];
        System.arraycopy(vars, 0, vs, 0, vs.length);
        this.z = vars[vars.length-1];
        this.toRemove = new TIntStack();
        this.boundChange = this.getSolver().getEnvironment().makeBool(false);
        initGraph(costs,pi);
    }
    public FastCostRegular(IntDomainVar[] vars, DirectedMultigraph<Node,Arc> graph,Node source)
    {
        super(vars);
        this.vs = new IntDomainVar[vars.length-1];
        System.arraycopy(vars, 0, vs, 0, vs.length);
        this.z = vars[vars.length-1];
        this.toRemove = new TIntStack();
        this.boundChange = this.getSolver().getEnvironment().makeBool(false);


        initGraph(graph,source);




    }

    public void initGraph(DirectedMultigraph<Node,Arc> graph, Node source)
    {

        int[] offsets = new int[vs.length];
        int[] sizes = new int[vs.length];
        int[] starts = new int[vs.length];

        int totalSizes = 0;

        starts[0] = 0;
        for (int i = 0 ; i < vs.length ; i++)
        {
            offsets[i] = vs[i].getInf();
            sizes[i] = vs[i].getSup() - vs[i].getInf()+1;
            if (i > 0) starts[i] = sizes[i-1] + starts[i-1];
            totalSizes += sizes[i];
        }


        TIntArrayList[] layers = new TIntArrayList[vs.length+1];
        for (int i = 0 ; i < layers.length ;i++)
        {
            layers[i] = new TIntArrayList();
        }
        Queue<Node> queue = new ArrayDeque<Node>();
        source.layer = 0;
        queue.add(source);

        int nid = 0;
        int aid = 0;
        while(!queue.isEmpty())
        {
            Node n = queue.remove();
            n.id  = nid++;
            layers[n.layer].add(n.id);
            Set<Arc> tmp = graph.outgoingEdgesOf(n);
            for (Arc a : tmp)
            {
                a.id = aid++;
                Node next = graph.getEdgeTarget(a);
                next.layer = n.layer+1;
                queue.add(next);
            }
        }
        int[][] lays = new int[layers.length][];
        for (int i = 0 ; i < lays.length ;i++)
        {
            lays[i] = layers[i].toNativeArray();
        }
        this.graph = new StoredValuedDirectedMultiGraph(this,graph,lays,starts,offsets,totalSizes);

        

    }


    public void initGraph(double[][][] costs, Automaton pi)
    {
        int aid = 0;
        int nid = 0;


        int[] offsets = new int[vs.length];
        int[] sizes = new int[vs.length];
        int[] starts = new int[vs.length];

        int totalSizes = 0;

        starts[0] = 0;
        for (int i = 0 ; i < vs.length ; i++)
        {
            offsets[i] = vs[i].getInf();
            sizes[i] = vs[i].getSup() - vs[i].getInf()+1;
            if (i > 0) starts[i] = sizes[i-1] + starts[i-1];
            totalSizes += sizes[i];
        }



        DirectedMultigraph<Node, Arc> graph;

        int n = vs.length;
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

        layer.get(0).add(pi.getStartingState());

        for (i = 0 ; i < n ; i++)
        {
            varIter = vs[i].getDomain().getIterator();
            while(varIter.hasNext())
            {
                j = varIter.next();
                layerIter = layer.get(i).iterator();//getIterator();
                while(layerIter.hasNext())
                {
                    k = layerIter.next();
                    int succ = pi.delta(k,j);
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
            if (!pi.isAccepting(k))
            {
                layerIter.remove();
            }

        }


        //backward pass, removing arcs that does not lead to an accepting state
        int nbNodes = pi.size();
        BitSet mark = new BitSet(nbNodes);

        Node[] in = new Node[pi.size()*(n+1)];
        Node tink = new Node(pi.getNbStates()+1,n+1,nid++);
        graph.addVertex(tink);

        for (i = n -1 ; i >=0 ; i--)
        {
            mark.clear(0,nbNodes);
            varIter = vs[i].getDomain().getIterator();
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
                        int qn = pi.delta(k,j);
                        if (layer.get(i+1).contains(qn))
                        {
                            Node a = in[i*pi.size()+k];
                            if (a == null)
                            {
                                a = new Node(k,i,nid++);
                                in[i*pi.size()+k] = a;
                                graph.addVertex(a);
                            }



                            Node b = in[(i+1)*pi.size()+qn];
                            if (b == null)
                            {
                                b = new Node(qn,i+1,nid++);
                                in[(i+1)*pi.size()+qn] = b;
                                graph.addVertex(b);
                            }


                            Arc arc = new Arc(a,b,j,aid++,costs[i][j][a.state]);
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
            varIter.dispose();
            layerIter = layer.get(i).iterator();

            // If no more arcs go out of a given state in the layer, then we remove the state from that layer
            while (layerIter.hasNext())
                if(!mark.get(layerIter.next()))
                    layerIter.remove();
        }

        TIntHashSet th = new TIntHashSet();
        int[][] intLayer = new int[n+2][];
        for (k = 0 ; k < pi.getNbStates() ; k++)
        {
            Node o = in[n*pi.size()+k];
            {
                if (o != null)
                {
                    Arc a = new Arc(o,tink,0,aid++,0.0);
                    graph.addEdge(o,tink,a);
                }
            }
        }


        for (i = 0 ; i <= n ; i++)
        {
            th.clear();
            for (k = 0 ; k < pi.getNbStates() ; k++)
            {
                Node o = in[i*pi.size()+k];
                if (o != null)
                {
                    th.add(o.id);
                }
            }
            intLayer[i] = th.toArray();
        }
        intLayer[n+1] = new int[]{tink.id};


        if (intLayer[0].length > 0)
            this.graph = new StoredValuedDirectedMultiGraph(this,graph,intLayer,starts,offsets,totalSizes);
        graph = null;
    }



    public void awake() throws ContradictionException
    {
        double zinf = this.graph.GNodes.spft.get(this.graph.sourceIndex);
        double zsup = this.graph.GNodes.lpfs.get(this.graph.tinkIndex);

        z.updateInf((int)Math.ceil(zinf),this.getConstraintIdx(vs.length));
        z.updateSup((int)Math.floor(zsup),this.getConstraintIdx(vs.length));

        DisposableIntIterator it = this.graph.inGraph.getIterator();
        //for (int id = this.graph.inGraph.nextSetBit(0) ; id >=0 ; id = this.graph.inGraph.nextSetBit(id+1))  {
        while(it.hasNext())
        {
            int id = it.next();
            int orig = this.graph.GArcs.origs[id];
            int dest = this.graph.GArcs.dests[id];

            double acost = this.graph.GArcs.costs[id];

            double spfs = this.graph.GNodes.spfs.get(orig);
            double lpfs = this.graph.GNodes.lpfs.get(orig);

            double spft = this.graph.GNodes.spft.get(dest);
            double lpft = this.graph.GNodes.lpft.get(dest);


            if ((spfs + spft + acost > z.getSup() || lpfs + lpft + acost < z.getInf()) && !this.graph.isInStack(id))
            {
                this.graph.setInStack(id);
                this.toRemove.push(id);
            }
        }

        it.dispose();

        try
        {
            do
            {
                while (toRemove.size() > 0)
                {
                    int id = toRemove.pop();
                    // toRemove.removeLast();
                    this.graph.removeArc(id,toRemove);
                }
                while (this.graph.toUpdateLeft.size() > 0)
                {
                    this.graph.updateLeft(this.graph.toUpdateLeft.pop(),toRemove);
                }
                while(this.graph.toUpdateRight.size() > 0)
                {
                    this.graph.updateRight(this.graph.toUpdateRight.pop(),toRemove);
                }
            } while (toRemove.size() > 0);
        }
        catch (ContradictionException e)
        {
            toRemove.reset();
            this.graph.inStack.clear();
            this.graph.toUpdateLeft.reset();
            this.graph.toUpdateRight.reset();

            throw e;
        }






        /*for (int i  = 0 ; i < vs.length ; i++)
        {
            for (int j = vs[i].getInf() ; j <= vs[i].getSup() ; j = vs[i].getNextDomainValue(j))
            {
                StoredIndexedBipartiteSet sup = graph.getSupport(i,j);
                if (sup == null || sup.isEmpty())
                {
                    vs[i].removeVal(j,this.getConstraintIdx(i));
                }
            }
        }*/

    }

    protected void checkWorld()
    {
        int current = this.getSolver().getEnvironment().getWorldIndex();
        if (current < lastWorld)
        {
            this.toRemove.reset();
            this.graph.inStack.clear();
            this.graph.toUpdateLeft.reset();
            this.graph.toUpdateRight.reset();
        }
        lastWorld = current;
    }


    public void awakeOnRemovals(int idx, DisposableIntIterator it2) throws ContradictionException {
        checkWorld();
        boolean mod = false;
        while (it2.hasNext())
        {
            int val = it2.next();
            StoredIndexedBipartiteSet sup = graph.getSupport(idx,val);
            if (sup != null)
            {
                DisposableIntIterator it=  sup.getIterator();
                while (it.hasNext())
                {
                    int arcId  = it.next();
                    if (!graph.isInStack(arcId))
                    {
                        graph.setInStack(arcId);
                        toRemove.push(arcId);
                        mod = true;
                    }
                }
                it.dispose();
            }

        }
        it2.dispose();
        if (mod)
            this.constAwake(false);

    }

    public void awakeOnInf(int idx) throws ContradictionException {
        checkWorld();
        boundChange.set(true);
        this.constAwake(false);

    }

    public void awakeOnSup(int idx) throws ContradictionException {
        checkWorld();
        boundChange.set(true);
        this.constAwake(false);

    }


    public void awakeOnInst(int idx){
        System.err.println("CALLED INST");
    }
    public void awakeOnBounds(int idx){
        System.err.println("CALLED BOUNDS");
    }
    public void awakeOnRem(int idx, int val) {
        System.err.println("CALLED REM");
    }


    @Override
    public void propagate() throws ContradictionException {

        if (boundChange.get())
        {
            boundChange.set(false);
            DisposableIntIterator it = this.graph.inGraph.getIterator();
            //for (int id = this.graph.inGraph.nextSetBit(0) ; id >=0 ; id = this.graph.inGraph.nextSetBit(id+1))  {
            while(it.hasNext())
            {
                int id = it.next();
                int orig = this.graph.GArcs.origs[id];
                int dest = this.graph.GArcs.dests[id];

                double acost = this.graph.GArcs.costs[id];
                double lpfs = this.graph.GNodes.lpfs.get(orig);
                double lpft = this.graph.GNodes.lpft.get(dest);

                double spfs = this.graph.GNodes.spfs.get(orig);
                double spft = this.graph.GNodes.spft.get(dest);


                if ((lpfs + lpft + acost < z.getInf() || spfs + spft + acost > z.getSup()) && !this.graph.isInStack(id))
                {
                    this.graph.setInStack(id);
                    this.toRemove.push(id);
                }
            }
            it.dispose();

        }

        do
        {
            while (toRemove.size() > 0)
            {
                int id = toRemove.pop();
                // toRemove.removeLast();
                this.graph.removeArc(id,toRemove);
            }
            while (this.graph.toUpdateLeft.size() > 0)
            {
                this.graph.updateLeft(this.graph.toUpdateLeft.pop(),toRemove);
            }
            while(this.graph.toUpdateRight.size() > 0)
            {
                this.graph.updateRight(this.graph.toUpdateRight.pop(),toRemove);
            }
        } while (toRemove.size() > 0);


        double zinf = this.graph.GNodes.spft.get(this.graph.sourceIndex);
        double zsup = this.graph.GNodes.lpfs.get(this.graph.tinkIndex);

        z.updateInf((int)Math.ceil(zinf),this.getConstraintIdx(vs.length));
        z.updateSup((int)Math.floor(zsup),this.getConstraintIdx(vs.length));



    }

    public final int getFilteredEventMask(int idx) {
        return (idx < vs.length ? IntVarEvent.REMVALbitvector: IntVarEvent.BOUNDSbitvector);
    }

    public static void main(String[] args) throws ContradictionException {
        CPSolver s = new CPSolver();
        int n = 10;
        IntDomainVar[] v = new IntDomainVar[n+1];
        for (int i = 0 ; i < n ; i++)
        {
            v[i] = new IntDomainVarImpl(s,"x_"+i,IntDomainVar.BITSET,0,2);
        }
        v[n] = new IntDomainVarImpl(s,"z",IntDomainVar.BOUNDS,0,0);

        Automaton auto = new Automaton();
        int start = auto.addState();
        int end = auto.addState();
        auto.setStartingState(start);
        auto.setAcceptingState(start);
        auto.setAcceptingState(end);

        auto.addTransition(start,start,new int[]{0,1});
        auto.addTransition(start,end,2);

        auto.addTransition(end,start,2);
        auto.addTransition(end,start,new int[]{0,1});

        double[][][] costs = new double[n][3][2];
        for (int i = 0 ; i < costs.length ; i++)
        {
            costs[i][0][1] = 1.0;
            costs[i][1][1] = 1.0;
        }

        long t1 = System.currentTimeMillis();
        FastCostRegular cr = new FastCostRegular(v,auto,costs);
        System.out.println("TEMPS CREATION FCR : "+(System.currentTimeMillis()-t1)+"ms");
        cr.awake();

    }
}
