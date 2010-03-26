/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
package choco.cp.solver.constraints.global.automata.fast_multicostregular;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.Constants;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.global.automata.fast_multicostregular.algo.FastPathFinder;
import choco.kernel.solver.constraints.global.automata.fast_multicostregular.structure.Arc;
import choco.kernel.solver.constraints.global.automata.fast_multicostregular.structure.Node;
import choco.kernel.solver.constraints.global.automata.fast_multicostregular.structure.StoredDirectedMultiGraph;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TObjectIntHashMap;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;


/**
 * Created by IntelliJ IDEA.
 * User: julien          S
 * Date: Jul 16, 2008
 * Time: 5:56:50 PM
 *
 * Multi-Cost-Regular is a propagator for the constraint ensuring that, given :
 * an automaton Pi;
 * a sequence of domain variables X;
 * a set of bound variables Z;
 * a assignment cost matrix for each bound variable C;
 *
 * The word formed by the sequence of assigned variables is accepted by Pi;
 * for each z^k in Z, sum_i(C_i(x_k)k) = z^k
 *
 * AC is NP hard for such a constraint.
 * The propagation is based on a Lagrangian Relaxation approach of the underlying
 * Resource constrained  shortest/longest path problems
 */
public final class FastMultiCostRegular extends AbstractLargeIntSConstraint
{


    /**
     * Maximum number of iteration during a bound computation
     */
    public static int MAXBOUNDITER = 10;

    /**
     * Maximum number of non improving iteration while computing a bound
     */
    public static int MAXNONIMPROVEITER = 15;

    /**
     * Constant coefficient of the lagrangian relaxation
     */
    public static double U0 = 10.0;

    /**
     * Lagrangian multiplier decreasing factor
     */
    public static double RO = 0.7;


    /**
     * Map to retrieve rapidly the index of a given variable.
     */
    public final TObjectIntHashMap<IntDomainVar> map;

    /**
     * The last computed Shortest Path
     */
    public int[] lastSp;

    /**
     * The last computed Longest Path
     */
    public int[] lastLp;


    /**
     * Decision variables
     */
    protected final IntDomainVar[] vs;

    /**
     * Cost variables
     */
    protected final IntDomainVar[] z;

    /**
     * Integral costs : c[i][j][k][s] is the cost over dimension k of x_i = j on state s
     */
    protected final int[][][][] costs;

    /**
     * The finite automaton which defines the regular language the variable sequence must belong
     */
    protected final FiniteAutomaton pi;

    /**
     * Layered graph of the unfolded automaton
     */
    protected StoredDirectedMultiGraph graph;

    /**
     * Boolean array which record whether a bound has been modified by the propagator
     */
    protected final boolean[] modifiedBound;

    /**
     * Cost to be applied to the graph for a given relaxation
     */
    // protected final double[][] newCosts;

    /**
     * Lagrangian multiplier container to compute an UB
     */
    protected final double[] uUb;

    /**
     * Lagrangian multiplier container to compute a LB
     */
    protected final double[] uLb;

    /**
     * Instance of the class containing all path finding algorithms
     * Also contains graph filtering algorithms
     */
    protected FastPathFinder slp;

    /**
     * Store the number of resources = z.length
     */
    protected final int nbR;


    /**
     * Stack to store removed edges index, for delayed update
     */
    protected final IStateIntVector toRemove;


    /**
     * Buffer to check whether an arc needs to be removed.
     */
    protected final TIntHashSet removed = new TIntHashSet();

    private final IEnvironment environment;

    /**
     * Constructs a multi-cost-regular constraint propagator
     * @param vars  decision variables
     * @param CR    cost variables
     * @param auto  finite automaton
     * @param costs assignment cost arrays
     * @param environment environment
     */
    public FastMultiCostRegular(final IntDomainVar[] vars, final IntDomainVar[] CR, final FiniteAutomaton auto, final int[][][] costs, IEnvironment environment)
    {
        this(vars,CR,auto,make4dim(costs,auto),environment);
    }

    private static int[][][][] make4dim(int[][][] costs, FiniteAutomaton auto) {
        int nbStates = auto.getNbStates();
        int[][][][] out = new int[costs.length][][][];
        for (int i = 0 ; i < costs.length ; i++)
        {
            out[i] = new int[costs[i].length][][];
            for (int j = 0 ; j < out[i].length ; j++)
            {
                out[i][j] = new int[costs[i][j].length][nbStates];
                for (int k = 0 ; k < out[i][j].length ; k++)
                {
                    for (int s = 0 ; s < nbStates ; s++)
                    {
                        out[i][j][k][s] = costs[i][j][k];
                    }
                }
            }

        }
        return out;

    }


    /**
     * Constructs a multi-cost-regular constraint propagator
     * @param vars  decision variables
     * @param CR    cost variables
     * @param auto  finite automaton
     * @param costs assignment cost arrays
     * @param environment environment
     */
    public FastMultiCostRegular(final IntDomainVar[] vars, final IntDomainVar[] CR, final FiniteAutomaton auto, final int[][][][] costs, IEnvironment environment)
    {
        super(ArrayUtils.<IntDomainVar>append(vars,CR));
        this.environment = environment;
        this.vs = vars;
        this.costs = costs;
        this.z= CR;
        this.nbR = this.z.length-1;
        this.pi = auto;
        this.modifiedBound = new boolean[]{true,true};
        /*  this.newCosts = new double[costs.length+1][];
    for (int i = 0; i < costs.length; i++) {
        this.newCosts[i] = new double[costs[i].length];
    }
    this.newCosts[costs.length] = new double[1]; */
        this.uUb = new double[2*nbR];
        this.uLb = new double[2*nbR];

        this.map = new TObjectIntHashMap<IntDomainVar>();
        for (int i = 0 ; i < vars.length ; i++)
        {
            this.map.put(vars[i],i);
        }
        this.toRemove = environment.makeIntVector();




    }

    public void initGraph()
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



        DirectedMultigraph<Node,Arc> graph;

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

        layer.get(0).add(pi.getInitialState());
        TIntHashSet nexts = new TIntHashSet();

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
                    nexts.clear();
                    pi.delta(k,j,nexts);
                    TIntIterator it = nexts.iterator();
                    for (;it.hasNext();)
                    {
                        int succ = it.next();
                        layer.get(i+1).add(succ);
                    }
                    if(!nexts.isEmpty())
                    {
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
            if (!pi.isFinal(k))
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
                        nexts.clear();
                        pi.delta(k,j,nexts);
                        if (nexts.size() > 1)
                            System.err.println("STOP");
                        boolean added = false;
                        for (TIntIterator it = nexts.iterator();it.hasNext();)
                        {
                            int qn = it.next();

                            if (layer.get(i+1).contains(qn))
                            {
                                added = true;
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


                                Arc arc = new Arc(a,b,j,aid++);
                                graph.addEdge(a,b,arc);
                                tmp.get(idx).add(arc);

                                mark.set(k);
                            }
                        }
                        if (!added)
                            qijIter.remove();
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
                    Arc a = new Arc(o,tink,0,aid++);
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
            this.graph = new StoredDirectedMultiGraph(environment, this,graph,intLayer,starts,offsets,totalSizes,costs);
    }


    /**
     * Performs a lagrangian relaxation to compute a new Upper bound of the underlying RCSPP problem
     * Each built subproblem is a longest path one can use to perform cost based filtering
     * @throws ContradictionException if a domain becomes empty
     */
    protected void updateUpperBound() throws ContradictionException
    {
        int k = 0;
        double uk;
        double lp;
        double axu;
        double newLB;
        double newLA;
        boolean modif;
        int[] P;
        double coeff;
        double bk = RO;
        int nbNSig = 0;
        int nbNSig2 = 0;
        double bestVal = Double.POSITIVE_INFINITY;
        // Arrays.fill(uUb,0.0);
        do {
            coeff = 0.0;
            for (int i = 0 ; i < nbR ; i++)
            {
                coeff+= (uUb[i]* z[i+1].getSup());
                coeff-= (uUb[i+nbR]* z[i+1].getInf());
            }


            modif =false;

            slp.computeLongestPath(toRemove,z[0].getInf()-coeff,uUb,true,true,0);

            lp = slp.getLongestPathValue();
            P = slp.getLongestPath();
            filterUp(lp+coeff);

            if (bestVal-(lp+coeff) < 1.0/2.0)
            {
                nbNSig++;
                nbNSig2++;
            }
            else
            {
                nbNSig = 0;
                nbNSig2 = 0;

            }
            if (nbNSig == 3)
            {
                bk*=0.8;
                nbNSig = 0;
            }
            if (lp+coeff < bestVal)
            {
                bestVal = lp+coeff;
            }

            uk = U0 *Math.pow(bk,k) ;

            for (int l= 0 ;  l < uUb.length/2 ; l++)
            {
                axu = 0.0;
                for (int e : P)
                {
                    int i = graph.GNodes.layers[graph.GArcs.origs[e]];//  e.getOrigin().getLayer();
                    //int j = graph.GArcs.values[e];//e.getLabel();
                    if (i < vs.length)
                        axu+= graph.GArcs.originalCost[e][l+1];//costs[i][j][l+1];
                }
                newLB = Math.max(uUb[l]- uk * (z[l+1].getSup()-axu),0);
                newLA = Math.max(uUb[l+nbR]- uk*(axu-z[l+1].getInf()),0);
                if (Math.abs(uUb[l] - newLB) >= Constants.MCR_DECIMAL_PREC)
                {
                    uUb[l] = newLB;
                    modif = true;
                }
                if (Math.abs(uUb[l+nbR]-newLA) >= Constants.MCR_DECIMAL_PREC)
                {
                    uUb[l+nbR] = newLA;
                    modif = true;
                }
            }
            k++;

        } while (modif && nbNSig2 < MAXNONIMPROVEITER && k < MAXBOUNDITER);
        this.lastLp = P;

    }


    /**
     * Performs a lagrangian relaxation to compute a new Lower bound of the underlying RCSPP problem
     * Each built subproblem is a shortest path one can use to perform cost based filtering
     * @throws ContradictionException if a domain becomes empty
     */
    protected void updateLowerBound() throws ContradictionException {


        int k = 0;
        boolean modif;
        double sp;
        double uk;
        double axu;
        double newLB;
        double newLA;
        int[] P;
        double coeff;
        double bk = RO;
        double bestVal = Double.NEGATIVE_INFINITY;
        int nbNSig = 0;
        int nbNSig2 = 0;
        //  Arrays.fill(uLb,0.0);
        do
        {
            coeff = 0.0;
            for (int i = 0 ; i < nbR ; i++)
            {
                coeff+= (uLb[i]* z[i+1].getSup());
                coeff-= (uLb[i+nbR]* z[i+1].getInf());
            }

            modif = false;

            slp.computeShortestPath(toRemove,z[0].getSup()+coeff,uLb,true,false,0);


            sp = slp.getShortestPathValue();
            P = slp.getShortestPath();
            filterDown(sp-coeff);


            if ((sp-coeff) - bestVal < 1.0/2.0)
            {
                nbNSig++;
                nbNSig2++;
            }
            else
            {
                nbNSig = 0;
                nbNSig2 = 0;
            }
            if (nbNSig == 3)
            {
                bk*=0.8;
                nbNSig = 0;
            }
            if (sp-coeff > bestVal)
            {
                bestVal = sp-coeff;
            }



            uk = U0 *Math.pow(bk,k) ;

            for (int l = 0 ;  l < uLb.length/2 ; l++)
            {

                axu = 0.0;
                for (int e : P)
                {
                    int i = graph.GNodes.layers[graph.GArcs.origs[e]];
                    if (i < vs.length)
                        axu+= graph.GArcs.originalCost[e][l+1];
                }

                newLB = Math.max(uLb[l]+ uk * (axu-z[l+1].getSup()),0);
                newLA = Math.max(uLb[l+nbR]+uk*(z[l+1].getInf()-axu),0);
                if (Math.abs(uLb[l]-newLB) >= Constants.MCR_DECIMAL_PREC)
                {
                    uLb[l] = newLB;
                    modif = true;
                }
                if (Math.abs(uLb[l+nbR]-newLA) >= Constants.MCR_DECIMAL_PREC)
                {
                    uLb[l+nbR] = newLA;
                    modif = true;
                }


            }
            k++;
        } while(modif && nbNSig2 < MAXNONIMPROVEITER && k < MAXBOUNDITER);
        this.lastSp =P;
    }


    /**
     * Performs cost based filtering w.r.t. each cost dimension.
     * @throws ContradictionException if a domain is emptied
     */
    protected void prefilter() throws ContradictionException {
        FastPathFinder p = this.graph.getPathFinder();
        p.computeShortestAndLongestPath(toRemove,z);
      /*  for (int i = 0 ; i <nbR+1 ; i++)
        {
            int bsup = z[i].getSup();
            int binf = z[i].getInf();
            p.resetNodeShortestandLongestPathValues();
            p.computeShortestAndLongestPath(toRemove,binf,bsup,null,false,false,i);
            z[i].updateInf((int)Math.ceil(p.getShortestPathValue()), this, false);
            z[i].updateSup((int)Math.floor(p.getLongestPathValue()), this, false);
        }                         */
        this.delayedGraphUpdate();
    }


    /**
     * Filters w.r.t. a given lower bound.
     * @param realsp a given lower bound
     * @throws ContradictionException if the cost variable domain is emptied
     */
    protected void filterDown(final double realsp) throws ContradictionException {

        if (realsp - z[0].getSup() >= Constants.MCR_DECIMAL_PREC)
        {
            this.fail();
        }
        if (realsp - z[0].getInf() >= Constants.MCR_DECIMAL_PREC)
        {
            double mr = Math.round(realsp);
            double rsp = (realsp-mr <= Constants.MCR_DECIMAL_PREC)? mr : realsp;
            z[0].updateInf((int) Math.ceil(rsp), this, false);
            modifiedBound[0] = true;
        }
    }

    /**
     * Filters w.r.t. a given upper bound.
     * @param reallp a given upper bound
     * @throws ContradictionException if the cost variable domain is emptied
     */
    protected void filterUp(final double reallp) throws ContradictionException {
        if (reallp - z[0].getInf() <= -Constants.MCR_DECIMAL_PREC )
        {
            this.fail();
        }
        if (reallp - z[0].getSup() <= -Constants.MCR_DECIMAL_PREC )
        {
            double mr = Math.round(reallp);
            double rsp = (reallp-mr <= Constants.MCR_DECIMAL_PREC)? mr : reallp;
            z[0].updateSup((int) Math.floor(rsp), this, false);
            modifiedBound[1] = true;
        }
    }



    public boolean isSatisfied()
    {

        for (IntDomainVar var : this.vars) {
            if (!var.isInstantiated())
                return false;
        }
        return check();

    }

    public boolean isSatisfied(int[] word)
    {
        int first[] = new int[vs.length];
        System.arraycopy(word,0,first,0,first.length);

        return check(first);
    }

    public boolean check(int[] word)
    {
        if (!pi.run(word))
        {
            System.err.println("Word is not accepted by the automaton");
            System.err.print("{"+word[0]);
            for (int i = 1 ; i < word.length ;i++)
                System.err.print(","+word[i]);
            System.err.println("}");

            return false;
        }
        for (int k = 0 ; k < costs[0][0].length ; k++)
        {
            int cost = 0;
            TIntHashSet init = new TIntHashSet();
            init.add(this.pi.getInitialState());
            for (int i = 0 ; i < vs.length ; i++)
            {
                TIntHashSet next = new TIntHashSet();
                for(TIntIterator it = init.iterator() ; it.hasNext();)
                {
                    int source = it.next();
                    cost+= costs[i][word[i]][k][source];
                    this.pi.delta(source,word[i],next);
                    if (next.size() > 1)
                        System.err.println("STOP");
                }
                init = next;

            }
            if (k == 0)
            {
                if (cost != z[0].getVal())
                {
                    for (int i : word)
                        System.err.print(i+" ");
                    System.err.println("");
                    System.err.println("cost: "+cost+" != z:"+z[0].getVal());
                    return false;
                }
            }
            else
            {
                if (cost != z[k].getVal())
                {
                    System.err.println("cost: "+cost+" != z["+k+"] :"+z[k].getVal());
                    return false;
                }
            }

        }
        return true;

    }

    /**
     * Necessary condition : checks whether the constraint is violted or not
     * @return true if the constraint is not violated
     */
    public boolean check()
    {
        int[] word = new int[vs.length] ;
        for (int i = 0; i < vs.length ; i++)
        {
            if (!vs[i].isInstantiated())
                return true;
            word[i] = vs[i].getVal();
        }
        for (IntDomainVar aZ : z) {
            if (!aZ.isInstantiated()) return true;
        }
        return check(word);
    }

    public int getFilteredEventMask(int idx) {
        return (idx < vs.length ? IntVarEvent.REMVALbitvector : IntVarEvent.INSTINTbitvector + IntVarEvent.INCINFbitvector + IntVarEvent.DECSUPbitvector);
    }


    /**
     * Updates the graphs w.r.t. the caught event during event-based propagation
     * @throws ContradictionException if removing an edge causes a domain to be emptied
     */
    protected void delayedGraphUpdate() throws ContradictionException {

        while (toRemove.size() > 0)
        {
            int n = toRemove.get(toRemove.size()-1);
            toRemove.removeLast();
            this.graph.removeArc(n, toRemove);
        }
        //  this.prefilter();
    }






    /**
     * Iteratively compute upper and lower bound for the underlying RCSPP
     * @throws ContradictionException if a domain gets empty
     */
    public void computeSharpBounds() throws ContradictionException
    {
        while (modifiedBound[0] || modifiedBound[1])
        {
            if (modifiedBound[1])
            {
                modifiedBound[1] = false;
                updateLowerBound();
            }
            if (modifiedBound[0])
            {
                modifiedBound[0] = false;
                updateUpperBound();
            }
            this.delayedGraphUpdate();
            this.prefilter();


        }
    }


    private boolean remContains(int e)
    {
        for (int i = 0 ; i < toRemove.size() ; i++)
            if (toRemove.get(i) == e)
                return true;
        return false;
    }


    public void awakeOnRem(final int idx, final int val) throws ContradictionException {
        StoredIndexedBipartiteSet support = this.graph.getSupport(idx,val);
        if (support != null)
        {
            DisposableIntIterator it = support.getIterator();
            while (it.hasNext())
            {
                int e = it.next();
                assert(graph.isInStack(e)==remContains(e));
                if (!graph.isInStack(e))
                {
                    graph.setInStack(e);
                    toRemove.add(e);
                }
            }
            it.dispose();
            if (!toRemove.isEmpty())
            {
                this.constAwake(false);
            }

        }

    }

    public final void awakeOnInst(final int idx)
    {
        this.constAwake(false);
    }

    public final void awakeOnSup(final int idx)
    {
        this.constAwake(false);
    }

    public final void awakeOnInf(final int idx)
    {
        this.constAwake(false);
    }




    public void awake() throws ContradictionException
    {

        initGraph();
        if (this.graph == null)
            this.fail();

        this.slp = this.graph.getPathFinder();

        /*    ArrayList<int[]> pattern = new ArrayList<int[]>();
   pattern.add(new int[]{0});
 //  pattern.add(new int[]{0});
   pattern.add(new int[]{1});

   System.out.println("NB OCCURENCE MAX : "+this.slp.getMaxNbOccurence(pattern,pi));
   System.out.println("NB OCCURENCE MIN : "+this.slp.getMinNbOccurence(pattern,pi)); */


        for (int i  = 0 ; i < vs.length ; i++)
        {
            for (int j = vs[i].getInf() ; j <= vs[i].getSup() ; j = vs[i].getNextDomainValue(j))
            {
                StoredIndexedBipartiteSet sup = graph.getSupport(i,j);
                if (sup == null || sup.isEmpty())
                {
                    vs[i].removeVal(j, this, false);
                }
            }
        }
        prefilter();
        propagate();

    }

    public void propagate() throws ContradictionException
    {
        this.delayedGraphUpdate();
        this.modifiedBound[0] = true;
        this.modifiedBound[1] = true;
        this.computeSharpBounds();
        if (toRemove.size() > 0)
            System.out.println("PB");


        assert(check());
        assert(isGraphConsistent());
    }

    public boolean isGraphConsistent()
    {
        boolean ret = true;
        for (int i = 0 ; i < vs.length ; i++)
        {
            DisposableIntIterator iter = this.graph.layers[i].getIterator();
            while (iter.hasNext())
            {
                int n = iter.next();
                DisposableIntIterator it = this.graph.GNodes.outArcs[n].getIterator();
                while(it.hasNext())
                {
                    int arc = it.next();
                    int val = this.graph.GArcs.values[arc];
                    if (!vars[i].canBeInstantiatedTo(val))
                    {
                        System.err.println("Arc "+arc+" from node "+n+" to node"+this.graph.GArcs.dests[arc]+" with value "+val+" in layer "+i+" should not be here");
                        return false;
                    }
                }
            }
            iter.dispose();
        }
        return ret;
    }





    public final StoredDirectedMultiGraph getGraph()
    {
        return graph;
    }





}
