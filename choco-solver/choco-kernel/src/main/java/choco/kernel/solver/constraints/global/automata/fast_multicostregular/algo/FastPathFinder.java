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
package choco.kernel.solver.constraints.global.automata.fast_multicostregular.algo;

import choco.kernel.common.Constants;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.global.automata.fast_multicostregular.structure.StoredDirectedMultiGraph;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 19, 2009
 * Time: 5:50:53 PM
 */
public class FastPathFinder {

    StoredDirectedMultiGraph graph;

    int[] sp;
    int[] lp;

    int nbLayer;
    int nbR;

    public FastPathFinder(StoredDirectedMultiGraph graph)
    {
        this.graph = graph;
        this.sp = new int[graph.layers.length-1];
        this.lp = new int[graph.layers.length-1];
        this.nbLayer= graph.layers.length-1;
        this.nbR =  this.graph.nbR-1;
        this.tmpU = new double[nbR];

    }

    private final double getCost(int e, int resource,double[] u, boolean lagrange, boolean max)
    {
        double cost;
        if (!lagrange)
            cost = graph.GArcs.originalCost[e][resource];
        else
        {
            double tmp = 0.0;
            for (int k = 1 ; k <= nbR ; k++)
            {
                tmp+= (u[k-1]) *graph.GArcs.originalCost[e][k];
            }
            if (max) tmp = -tmp;
            cost =  graph.GArcs.originalCost[e][0]+ tmp;
        }
        graph.GArcs.temporaryCost[e] = cost;
        return cost;
    }

    private double[] tmpU;
    private final  double[] simplifyLagrangian(double[] u)
    {
        for (int k = 1 ; k <= nbR ; k++)
            tmpU[k-1] = u[k-1]-u[k-1+nbR];
        return tmpU;
    }
    private static final boolean isAllZero(double[] u)
    {
        for (double d : u)
        {
            if (d != 0.0)
                return false;
        }
        return true;
    }



    public void resetNodeLongestPathValues() {

        Arrays.fill(graph.GNodes.lpfs, Double.NEGATIVE_INFINITY);
        Arrays.fill(graph.GNodes.lpft, Double.NEGATIVE_INFINITY);


    }

    public void computeLongestPath(IStateIntVector removed, double lb, double[] u, boolean lagrange, boolean max, int resource) throws ContradictionException {

        boolean update;
        if (lagrange)
        {
            if (isAllZero(u))
            {
                u = null;
                lagrange = false;
                resource = 0;
            }else
            {
                u = simplifyLagrangian(u);
            }
        }

        graph.GNodes.lpfs[graph.sourceIndex] = 0.0;
        graph.GNodes.lpft[graph.tinIndex] = 0.0;

        for (int i = 0 ; i < nbLayer ; i++)
        {
            update = false;
            DisposableIntIterator origIter = graph.layers[i].getIterator();
            while (origIter.hasNext()) {
                int orig = origIter.next();
                StoredIndexedBipartiteSet bs = graph.GNodes.outArcs[orig];
                assert(!bs.isEmpty());
                DisposableIntIterator out = bs.getIterator();

                while (out.hasNext())
                {
                    int e = out.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.dests[e];//e.getDestination();
                        double newCost = graph.GNodes.lpfs[orig] + getCost(e,resource,u,lagrange,max);//cost[graph.GNodes.layers[orig]][graph.GArcs.values[e]];

                        if (graph.GNodes.lpfs[next] < newCost)
                        {
                            graph.GNodes.lpfs[next] = newCost;
                            graph.GNodes.prevLP[next] = e;
                            update =  true;
                        }
                    }

                }
                out.dispose();

            }
            origIter.dispose();
            if (!update) this.graph.constraint.fail();
        }
        for (int i = nbLayer ; i > 0 ; i--)
        {
            update = false;
            DisposableIntIterator destIter = graph.layers[i].getIterator();
            while(destIter.hasNext()){
                int dest = destIter.next();
                StoredIndexedBipartiteSet bs = graph.GNodes.inArcs[dest];
                assert(!bs.isEmpty());
                DisposableIntIterator in = bs.getIterator();

                while (in.hasNext())
                {
                    int e = in.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.origs[e];
                        double newCost = graph.GNodes.lpft[dest] +graph.GArcs.temporaryCost[e];//cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost + graph.GNodes.lpfs[next] -lb <= -Constants.MCR_DECIMAL_PREC)
                        {
                            graph.setInStack(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.lpft[next] < newCost)
                        {
                            graph.GNodes.lpft[next] = newCost;
                            graph.GNodes.nextLP[next] = e;
                            update = true;
                        }
                    }

                }
                in.dispose();

            }
            destIter.dispose();
            if (!update) this.graph.constraint.fail();
        }


    }

    public final double getLongestPathValue() {
        return graph.GNodes.lpft[graph.sourceIndex];
    }

    public int[] getLongestPath() {
        int i = 0;
        int current = this.graph.sourceIndex;
        do {
            int e = graph.GNodes.nextLP[current];//current.getSptt();
            sp[i++]= e;
            current = graph.GArcs.dests[e];//.getDestination();

        } while(graph.GNodes.nextLP[current] != Integer.MIN_VALUE);
        return sp;
    }

    public void resetNodeShortestPathValues() {

        Arrays.fill(graph.GNodes.spfs, Double.POSITIVE_INFINITY);
        Arrays.fill(graph.GNodes.spft, Double.POSITIVE_INFINITY);


    }

    public void computeShortestPath(IStateIntVector removed, double ub, double[] u, boolean lagrange, boolean max, int resource) throws ContradictionException {

        graph.GNodes.spfs[graph.sourceIndex] = 0.0;
        graph.GNodes.spft[graph.tinIndex] = 0.0;
        boolean update;
        if (lagrange)
        {
            if (isAllZero(u))
            {
                u = null;
                lagrange = false;
                resource = 0;
            }else
            {
                u = simplifyLagrangian(u);
            }
        }


        for (int i = 0 ; i < nbLayer ; i++)
        {
            update = false;
            DisposableIntIterator origIter = graph.layers[i].getIterator();
            while (origIter.hasNext()) {
                int orig = origIter.next();
                StoredIndexedBipartiteSet bs = graph.GNodes.outArcs[orig];
                assert(!bs.isEmpty());
                DisposableIntIterator out = bs.getIterator();

                while (out.hasNext())
                {
                    int e = out.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.dests[e];//.getDestination();
                        double newCost = graph.GNodes.spfs[orig] +  getCost(e,resource,u,lagrange,max);//cost[i][graph.GArcs.values[e]];
                        if (graph.GNodes.spfs[next] > newCost)
                        {
                            graph.GNodes.spfs[next] = newCost;
                            graph.GNodes.prevSP[next] = e;
                            update = true;

                        }
                    }
                }
                out.dispose();
            }
            origIter.dispose();
            if (!update)  this.graph.constraint.fail();
        }
        for (int i = nbLayer ; i > 0 ; i--)
        {
            update  = false;
            DisposableIntIterator destIter = graph.layers[i].getIterator();
            while(destIter.hasNext()){
                int dest = destIter.next();
                StoredIndexedBipartiteSet bs = graph.GNodes.inArcs[dest];
                assert(!bs.isEmpty());
                DisposableIntIterator in =bs.getIterator();//getInEdgeIterator(n);
                while (in.hasNext())
                {
                    int e = in.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.origs[e];//e.getOrigin()  ;
                        double newCost = graph.GNodes.spft[dest] + graph.GArcs.temporaryCost[e];
                        if (newCost + graph.GNodes.spfs[next] - ub >= Constants.MCR_DECIMAL_PREC)
                        {
                            graph.setInStack(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.spft[next] > newCost)
                        {
                            graph.GNodes.spft[next] = newCost;
                            graph.GNodes.nextSP[next] = e;
                            update = true;
                        }
                    }
                }
                in.dispose();


            }
            destIter.dispose();
            if (!update) this.graph.constraint.fail();
        }

    }

    public final double getShortestPathValue() {
        return graph.GNodes.spft[graph.sourceIndex];
    }

    public int[] getShortestPath() {
        int i = 0;
        int current = this.graph.sourceIndex;
        do {
            int e = graph.GNodes.nextSP[current];//current.getSptt();
            sp[i++]= e;
            current = graph.GArcs.dests[e];//.getDestination();

        } while(graph.GNodes.nextSP[current] != Integer.MIN_VALUE);
        return sp;
    }

    public void resetNodeShortestandLongestPathValues() {

        for (int i = 0 ; i < graph.GNodes.spfs.length ;i++)
        {
            graph.GNodes.spfs[i] = Double.POSITIVE_INFINITY;
            graph.GNodes.spft[i] = Double.POSITIVE_INFINITY;
            graph.GNodes.lpfs[i] = Double.NEGATIVE_INFINITY;
            graph.GNodes.lpft[i] = Double.NEGATIVE_INFINITY;
        }

    }

    public void computeShortestAndLongestPath(IStateIntVector removed, int lb, int ub,double[] u, boolean lagrange, boolean max, int resource) throws ContradictionException {

        graph.GNodes.spfs[graph.sourceIndex] = 0.0;
        graph.GNodes.spft[graph.tinIndex] = 0.0;
        graph.GNodes.lpfs[graph.sourceIndex] = 0.0;
        graph.GNodes.lpft[graph.tinIndex] = 0.0;
        boolean update;
        if (lagrange)
        {
            if (isAllZero(u))
            {
                u = null;
                lagrange = false;
                resource = 0;
            }else
            {
                u = simplifyLagrangian(u);
            }
        }

        for (int i = 0 ; i < nbLayer ; i++)
        {
            update = false;
            DisposableIntIterator origIter = graph.layers[i].getIterator();
            while (origIter.hasNext()) {
                int orig = origIter.next();
                StoredIndexedBipartiteSet bs = graph.GNodes.outArcs[orig];
                assert(!bs.isEmpty());
                DisposableIntIterator out = bs.getIterator();
                while (out.hasNext())
                {
                    int e = out.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.dests[e];//.getDestination();
                        double cost= getCost(e,resource,u,lagrange,max);
                        double newCost = graph.GNodes.spfs[orig] + cost;//cost[i][graph.GArcs.values[e]];
                        if (graph.GNodes.spfs[next] > newCost)
                        {
                            graph.GNodes.spfs[next] = newCost;
                            graph.GNodes.prevSP[next] = e;
                            update = true;

                        }
                        double newCost2 = graph.GNodes.lpfs[orig] +  cost;//cost[graph.GNodes.layers[n]][graph.GArcs.values[e]];

                        if (graph.GNodes.lpfs[next] < newCost2)
                        {
                            graph.GNodes.lpfs[next] = newCost2;
                            graph.GNodes.prevLP[next] = e;
                            update = true;
                        }
                    }
                }
                out.dispose();

            }
            origIter.dispose();
            if (!update) this.graph.constraint.fail();
        }
        for (int i = nbLayer ; i > 0 ; i--)
        {
            update = false;
            DisposableIntIterator destIter = graph.layers[i].getIterator();
            while(destIter.hasNext()){
                int dest = destIter.next();
                StoredIndexedBipartiteSet bs = graph.GNodes.inArcs[dest];
                assert(!bs.isEmpty());
                DisposableIntIterator in = bs.getIterator();//getInEdgeIterator(n);
                while (in.hasNext())
                {
                    int e = in.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.origs[e];//e.getOrigin()  ;
                        double cost= graph.GArcs.temporaryCost[e];

                        double newCost = graph.GNodes.spft[dest] +  cost;//cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost + graph.GNodes.spfs[next] - ub >= Constants.MCR_DECIMAL_PREC)
                        {
                            graph.getInStack().set(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.spft[next] > newCost)
                        {
                            graph.GNodes.spft[next] = newCost;
                            graph.GNodes.nextSP[next] = e;
                            update = true;
                        }

                        double newCost2 = graph.GNodes.lpft[dest] +  cost;//cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost2 + graph.GNodes.lpfs[next] -lb <= -Constants.MCR_DECIMAL_PREC)
                        {
                            graph.setInStack(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.lpft[next] < newCost2)
                        {
                            graph.GNodes.lpft[next] = newCost2;
                            graph.GNodes.nextLP[next] = e;
                            update =true;
                        }



                    }
                }
                in.dispose();

            }
            destIter.dispose();
            if (!update) this.graph.constraint.fail();
        }


    }


    /*  public int getMaxNbOccurence(ArrayList<int[]> subpattern, Automaton pi)
{
long time = System.currentTimeMillis();
int n = this.graph.GNodes.states.length;
int sum = 0;
int[][] mat = new int[subpattern.size()][n];

for (int i = 0 ; i < this.graph.offsets.length ; i++)
{
    for (int idx = subpattern.size()-1 ; idx >= 0 ; idx--)
    {
        int[] elem = subpattern.get(idx);

        for (int orig : graph.layers[i]) {
            StoredIndexedBipartiteSet bs = graph.GNodes.outArcs[orig];
            if (!bs.isEmpty())
            {
                DisposableIntIterator out = bs.getIterator();

                while (out.hasNext())
                {
                    int e = out.next();
                   int val = graph.GArcs.values[e];
                    if (contains(val,elem))
                    {
                        int dest = graph.GArcs.dests[e];
                        int add;
                        if (idx == 0)
                            add = 1+ mat[subpattern.size()-1][orig];
                        else
                        {
                            add = mat[idx-1][orig];
                            mat[idx-1][orig] = 0;
                        }
                        mat[idx][dest] = Math.max(mat[idx][dest],add);
                    }

                }
            }
        }
    }
}
for (int idx : this.graph.layers[this.graph.offsets.length])
    {
        int tmp = mat[subpattern.size()-1][idx];
        sum = Math.max(tmp,sum);
        //mat[subpattern.size()-1][idx] = 0;
    }
time = System.currentTimeMillis()-time;
System.out.println("TEMPS POUR NB OCCURENCE : "+time+"ms");

return sum;

}


public int getMinNbOccurence(ArrayList<int[]> subpattern, Automaton pi)
{
long time = System.currentTimeMillis();
int n = this.graph.GNodes.states.length;
int sum = Integer.MAX_VALUE;
int[][] mat = new int[subpattern.size()][n];

for (int i = 0 ; i < this.graph.offsets.length ; i++)
{
    for (int idx = subpattern.size()-1 ; idx >= 0 ; idx--)
    {
        int[] elem = subpattern.get(idx);

        for (int orig : graph.layers[i]) {
            StoredIndexedBipartiteSet bs = graph.GNodes.outArcs[orig];
            if (!bs.isEmpty())
            {
                DisposableIntIterator out = bs.getIterator();

                while (out.hasNext())
                {
                    int e = out.next();
                   int val = graph.GArcs.values[e];
                    if (contains(val,elem))
                    {
                        int dest = graph.GArcs.dests[e];
                        int add;
                        if (idx == 0)
                            add = 1+ mat[subpattern.size()-1][orig];
                        else
                        {
                            add = mat[idx-1][orig];
                            mat[idx-1][orig] = 0;
                        }
                        mat[idx][dest] = Math.min(mat[idx][dest],add);
                    }

                }
            }
        }
    }
}
for (int idx : this.graph.layers[this.graph.offsets.length])
    {
        int tmp = mat[subpattern.size()-1][idx];
        sum = Math.min(tmp,sum);
        //mat[subpattern.size()-1][idx] = 0;
    }
time = System.currentTimeMillis()-time;
System.out.println("TEMPS POUR NB OCCURENCE : "+time+"ms");

return sum;

}


private static boolean contains(int i, int[] tab)
{
for (int e : tab)
{
    if (i == e)
        return true;

}
return false;
}
    */

}