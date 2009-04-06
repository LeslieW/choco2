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
package choco.cp.solver.constraints.global.multicostregular;

import static choco.Choco.DEBUG;
import choco.cp.solver.constraints.global.multicostregular.algo.PathFinder;
import choco.cp.solver.constraints.global.multicostregular.structure.Arc;
import choco.cp.solver.constraints.global.multicostregular.structure.LayeredGraph;
import choco.cp.solver.constraints.global.multicostregular.structure.Node;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.IntIterator;
import choco.kernel.common.util.UtilAlgo;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntHashSet;
import gnu.trove.TObjectIntHashMap;

import java.util.Iterator;


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
public class MultiCostRegular extends AbstractLargeIntSConstraint
{

    /**
     * Defines the rounding precision
     */
    public static final int PRECISION = 5; // MUST BE < 13 as java messes up the precisions starting from 10E-12 (34.0*0.05 == 1.70000000000005)

    /**
     * Defines the smallest used double
     */
    public static final double D_PREC = Math.pow(10.0,-PRECISION);

    /**
     * Maximum number of iteration during a bound computation
     */
    public static final int MAXBOUNDITER = 10;

    /**
     * Maximum number of non improving iteration while computing a bound
     */
    public static final int MAXNONIMPROVEITER = 15;

    /**
     * Constant coefficient of the lagrangian relaxation
     */
    public final static double U0 = 10.0;

    /**
     * Lagrangian multiplier decreasing factor
     */
    public final static double RO = 0.75;


    /**
     * Map to retrieve rapidly the index of a given variable.
     */
    public final TObjectIntHashMap<IntDomainVar> map;

    /**
     * The last computed Shortest Path
     */
    public Arc[] lastSp;

    /**
     * The last computed Longest Path
     */
    public Arc[] lastLp;


    /**
     * Decision variables
     */
    protected final IntDomainVar[] vs;

    /**
     * Cost variables
     */
    protected final IntDomainVar[] z;

    /**
     * Integral costs : c[i][j][k] is the cost over dimension k of x_i = j
     */
    protected final int[][][] costs;

    /**
     * The finite automaton which defines the regular language the variable sequence must belong
     */
    protected final Automaton pi;

    /**
     * Layered graph of the unfolded automaton
     */
    protected LayeredGraph graph;

    /**
     * Boolean array which record whether a bound has been modified by the propagator
     */
    protected final boolean[] modifiedBound;

    /**
     * Cost to be applied to the graph for a given relaxation
     */
    protected final double[][] newCosts;

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
    protected PathFinder slp;

    /**
     * Store the number of resources = z.length
     */
    protected final int nbR;

    /**
     * Store the last world in which propagation was called
     */
    protected int lastVisitedWorld;


    /**
     * Stack to store removed edges index, for delayed update
     */
    protected final IStateIntVector toRemove;


    /**
     * Buffer to check whether an arc needs to be removed.
     */
    protected final TIntHashSet removed = new TIntHashSet();


    /**
     * Constructs a multi-cost-regular constraint propagator
     * @param vars  decision variables
     * @param CR    cost variables
     * @param auto  finite automaton
     * @param costs assignment cost arrays
     */
    public MultiCostRegular(final IntDomainVar[] vars, final IntDomainVar[] CR, final Automaton auto, final int[][][] costs)
    {
        super(UtilAlgo.<IntDomainVar>append(vars,CR));
        this.vs = vars;
        this.costs = costs;
        this.z= CR;
        this.nbR = this.z.length-1;
        this.pi = auto;
        this.modifiedBound = new boolean[]{true,true};
        this.newCosts = new double[costs.length+1][];
        for (int i = 0; i < costs.length; i++) {
            this.newCosts[i] = new double[costs[i].length];
        }
        this.newCosts[costs.length] = new double[1];
        this.uUb = new double[2*nbR];
        this.uLb = new double[2*nbR];

        this.map = new TObjectIntHashMap<IntDomainVar>();
        for (int i = 0 ; i < vars.length ; i++)
        {
            this.map.put(vars[i],i);
        }
        this.toRemove = this.getSolver().getEnvironment().makeIntVector();

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
        Arc[] P;
        double coeff;
        double bk = RO;
        int nbNSig = 0;
        int nbNSig2 = 0;
        double bestVal = Double.POSITIVE_INFINITY;
        int nbIter = 0;
        do {
            nbIter++;
            coeff = 0.0;
            for (int i = 0 ; i < nbR ; i++)
            {
                coeff+= (uUb[i]* z[i+1].getSup());
                coeff-= (uUb[i+nbR]* z[i+1].getInf());
            }


            modif =false;
            this.updateCosts(uUb,true);

            boolean tmp = true;
            while (tmp)
            {
                slp.resetNodeLongestPathValues();// slp.resetNodeShortestPathValues();
                slp.computeLongestPath(toRemove,newCosts,z[0].getInf()-coeff);
                tmp = false;
                tmp = toRemove.size() > 0;
                 this.delayedGraphUpdate();


            }




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
                for (Arc e : P)
                {
                    int i = e.orig.getLayer();
                    int j = e.getLabel();
                    if (i < vs.length)
                        axu+= costs[i][j][l+1];
                }
                newLB = Math.max(uUb[l]- uk * (z[l+1].getSup()-axu),0);
                newLA = Math.max(uUb[l+nbR]- uk*(axu-z[l+1].getInf()),0);
                if (Math.abs(uUb[l] - newLB) >= D_PREC)
                {
                    uUb[l] = newLB;
                    modif = true;
                }
                if (Math.abs(uUb[l+nbR]-newLA) >= D_PREC)
                {
                    uUb[l+nbR] = newLA;
                    modif = true;
                }
            }
            k++;

        } while (modif && nbNSig2 < MAXNONIMPROVEITER && nbIter < MAXBOUNDITER);
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
        Arc[] P;
        double coeff;
        double bk = RO;
        double bestVal = Double.NEGATIVE_INFINITY;
        int nbNSig = 0;
        int nbNSig2 = 0;
        int nbIter = 0;
        do
        {
            coeff = 0.0;
            for (int i = 0 ; i < nbR ; i++)
            {
                coeff+= (uLb[i]* z[i+1].getSup());
                coeff-= (uLb[i+nbR]* z[i+1].getInf());
            }



            modif = false;
            this.updateCosts(uLb,false);



            boolean tmp = true;
            while (tmp)
            {
                slp.resetNodeShortestPathValues();
                slp.computeShortestPath(toRemove,newCosts,z[0].getSup()+coeff);
                tmp = false;
                tmp = toRemove.size() > 0;
                this.delayedGraphUpdate();
            }



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
                for (Arc e : P)
                {
                    int i = e.orig.getLayer();
                    int j = e.getLabel();
                    if (i < vs.length)
                        axu+= costs[i][j][l+1];
                }

                newLB = Math.max(uLb[l]+ uk * (axu-z[l+1].getSup()),0);
                newLA = Math.max(uLb[l+nbR]+uk*(z[l+1].getInf()-axu),0);
                if (Math.abs(uLb[l]-newLB) >= D_PREC)
                {
                    uLb[l] = newLB;
                    modif = true;
                }
                if (Math.abs(uLb[l+nbR]-newLA) >= D_PREC)
                {
                    uLb[l+nbR] = newLA;
                    modif = true;
                }


            }
            k++;
        } while(modif && nbNSig2 < MAXNONIMPROVEITER && nbIter < MAXBOUNDITER);
        this.lastSp =P;
    }


    /**
     * Performs cost based filtering w.r.t. each cost dimension.
     * @throws ContradictionException if a domain is emptied
     */
    protected void prefilter() throws ContradictionException {
        double[] u = new double[nbR+nbR];
        PathFinder p = this.graph.getPF();
        boolean b = true;
        while (b)
        {
            b = false;
            for (int i = 0 ; i <nbR+1 ; i++)
            {
                int bsup = z[i].getSup();
                int binf = z[i].getInf();
                updateCosts(u,i,false);
                p.resetNodeShortestandLongestPathValues();
                p.computeShortestAndLongestPath(toRemove,newCosts,binf,bsup);
                b |= toRemove.size() > 0;
                z[i].updateInf((int)Math.ceil(p.getShortestPathValue()),this.getConstraintIdx(vs.length));
                z[i].updateSup((int)Math.floor(p.getLongestPathValue()),this.getConstraintIdx(vs.length));
                if (b)
                {
                       this.delayedGraphUpdate();
                       break;
                }

            }
           // b = false;
        }
        this.delayedGraphUpdate();
    }


    /**
     * Filters w.r.t. a given lower bound.
     * @param realsp a given lower bound
     * @throws ContradictionException if the cost variable domain is emptied
     */
    protected void filterDown(final double realsp) throws ContradictionException {

        if (realsp - z[0].getSup() >= D_PREC)
        {
            this.fail();
        }
        if (realsp - z[0].getInf() >= D_PREC)
        {
            double mr = Math.round(realsp);
            double rsp = (realsp-mr <= D_PREC)? mr : realsp;
            z[0].updateInf((int) Math.ceil(rsp),this.getConstraintIdx(vars.length-1));
            modifiedBound[0] = true;
        }
    }

    /**
     * Filters w.r.t. a given upper bound.
     * @param reallp a given upper bound
     * @throws ContradictionException if the cost variable domain is emptied
     */
    protected void filterUp(final double reallp) throws ContradictionException {
        if (reallp - z[0].getInf() <= -D_PREC )
        {
            this.fail();
        }
        if (reallp - z[0].getSup() <= -D_PREC )
        {
            double mr = Math.round(reallp);
            double rsp = (reallp-mr <= D_PREC)? mr : reallp;
            z[0].updateSup((int) Math.floor(rsp),this.getConstraintIdx(vars.length -1));
            modifiedBound[1] = true;
        }
    }

    /**
     * updates the graph arc costs given lagrangian multipliers
     * @param u lagrangian multipliers
     * @param max are we computing an upper bound ?
     */
    protected  void updateCosts(final double[] u, final boolean max)
    {
        updateCosts(u,0,max);
    }

    /**
     * updates the graph arc costs given lagrangian multipliers
     * @param u lagrangian multipliers
     * @param resource cost variable index that will not be relaxed
     * @param max are we computing an upper bound ?
     */
    protected void updateCosts(final double[] u,final int resource, final boolean max)
    {
        for (int i = 0 ;i < costs.length ; i++)
        {
            for (int j = 0 ; j < costs[i].length ; j++)
            {
                double tmp = 0;
                for (int k = 1 ; k < costs[i][j].length; k++)
                {
                    tmp+= (u[k-1]-u[k-1+nbR])*costs[i][j][k];

                }
                if (max) tmp = -tmp;
                newCosts[i][j] = costs[i][j][resource]+ tmp;
            }
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
            for (int i = 0 ; i < vs.length ; i++)
            {
                cost+= costs[i][word[i]][k];
            }
            if (k == 0)
            {
                if (cost != z[0].getVal())
                {
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

    public int getFilteredEventMask(int idx) {
        return (idx < vs.length ? IntVarEvent.REMVALbitvector + IntVarEvent.INSTINTbitvector : IntVarEvent.INSTINTbitvector + IntVarEvent.INCINFbitvector + IntVarEvent.DECSUPbitvector);
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
            this.graph.removeEdge(n, toRemove);
        }
        //  this.prefilter();
    }






    public void awakeOnRemovals(final int idx, final IntIterator deltadomain) throws ContradictionException {
        removed.clear();
        // boolean modified = false;
        while (deltadomain.hasNext())
            removed.add(deltadomain.next());

        Node[] sn = this.graph.getLayer(idx);
        for (Node n : sn)
        {
            Iterator<Arc> it = this.graph.getOutEdgeIterator(n);
            while (it.hasNext())
            {
                Arc e = it.next();
                if (removed.contains(e.getLabel()) &&!graph.isInStack(e.getOutIndex())) {
                    graph.setInStack(e.getOutIndex());
                    toRemove.add(e.getOutIndex());

                    //if (this.lastLp == null || this.lastLp[e.orig.getLayer()].equals(e) || this.lastSp[e.orig.getLayer()].equals(e))
                    //  modified = true;
                }
            }
        }
        if (!toRemove.isEmpty())
        {
            this.constAwake(false);
        }
    }

    public void awakeOnRem(final int idx, final int val) throws ContradictionException {
        Node[] sn = this.graph.getLayer(idx);
        for (Node n : sn) {
            Iterator<Arc> it = this.graph.getOutEdgeIterator(n);
            while (it.hasNext()) {
                Arc e = it.next();
                if (e.getLabel() == val && !graph.isInStack(e.getOutIndex())) {
                    graph.setInStack(e.getOutIndex());
                    toRemove.add(e.getOutIndex());

                    //if (this.lastLp == null || this.lastLp[e.orig.getLayer()].equals(e) || this.lastSp[e.orig.getLayer()].equals(e))
                    //  modified = true;
                }
            }
        }
        if (!toRemove.isEmpty())
        {
            this.constAwake(false);
        }

    }
    public void awakeOnInst(final int idx) throws ContradictionException {
        //boolean modified = false;
        int val = vars[idx].getVal();
        if (idx >= vs.length)
        {
            this.constAwake(false);
        }
        else if (idx < vs.length)
        {
            Node[] sn = this.graph.getLayer(idx);
            for (Node n : sn)
            {
                Iterator<Arc> it = this.graph.getOutEdgeIterator(n);
                while (it.hasNext())
                {
                    Arc e = it.next();
                    if (e.getLabel() != val &&!graph.isInStack(e.getOutIndex())) {
                        graph.setInStack(e.getOutIndex());
                        toRemove.add(e.getOutIndex());

                        //if (this.lastLp == null || this.lastLp[e.orig.getLayer()].equals(e) || this.lastSp[e.orig.getLayer()].equals(e))
                        //  modified = true;
                    }
                }

            }


            if (!toRemove.isEmpty())
            {

                this.constAwake(false);
            }
        }
    }

    public void awakeOnBounds(final int idx) throws ContradictionException {
        this.awakeOnInf(idx);
        this.awakeOnSup(idx);
    }

    public void awakeOnInf(final int idx) throws ContradictionException {
        if (idx >= vs.length)
        {
            this.constAwake(false);
        }
        else if (idx < vs.length)
        {
            //boolean modified = false;
            int inf = vars[idx].getInf();

            Node[] sn = this.graph.getLayer(idx);
            int i = 0 ;
            while (i < sn.length) {
                Node n = sn[i];
                Iterator<Arc> it = this.graph.getOutEdgeIterator(n);
                while(it.hasNext())
                {
                    Arc e = it.next();
                    if (e.getLabel() < inf && !graph.isInStack(e.getOutIndex()))
                    {
                        graph.setInStack(e.getOutIndex());
                        toRemove.add(e.getOutIndex());
                        //if (this.lastLp == null || this.lastLp[e.orig.getLayer()].equals(e) || this.lastSp[e.orig.getLayer()].equals(e))
                        //  modified = true;
                    }
                }
                i++;
            }
            if (!toRemove.isEmpty())
            {
                this.constAwake(false);
            }

        }

    }
    public void awakeOnSup(final int idx) throws ContradictionException {
        if (idx >= vs.length)
        {
            this.constAwake(false);
        }
        else
        {
            //boolean modified = false;
            int sup = vars[idx].getSup();
            if (idx < vs.length)
            {
                Node[] sn = this.graph.getLayer(idx);
                for (Node n : sn) {
                    Iterator<Arc> it = this.graph.getOutEdgeIterator(n);
                    while (it.hasNext()) {
                        Arc e = it.next();
                        if (e.getLabel() > sup && !graph.isInStack(e.getOutIndex())) {
                            graph.setInStack(e.getOutIndex());
                            this.toRemove.add(e.getOutIndex());
                            //if (this.lastLp == null || this.lastLp[e.orig.getLayer()].equals(e) || this.lastSp[e.orig.getLayer()].equals(e))
                            //modified = true;
                        }
                    }
                }
            }

            if (!toRemove.isEmpty())
            {
                this.constAwake(false);
            }

        }

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



    public void awake() throws ContradictionException
    {
        this.graph = new LayeredGraph(vs,pi,this);
        this.slp = this.graph.getPF();
        if(DEBUG)
        {
            System.out.println("NB OF EDGES IN GRAPH : "+this.graph.getActiveOut().cardinality());
            int nbNode = 0 ;
            for (int i = 0 ; i <  this.graph.getNbLayers() ; i++)
                nbNode+=this.graph.getLayer(i).length;
            System.out.println("NB Of NODES IN GRAPH : "+nbNode);
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
        if (DEBUG && !this.check())
        {
            System.out.flush();
            System.err.println("ACCEPTED INSTANTIATION DOES NOT COMPLY WITH CHECKER");
            System.exit(1);
            this.fail();
        }
    }


}
