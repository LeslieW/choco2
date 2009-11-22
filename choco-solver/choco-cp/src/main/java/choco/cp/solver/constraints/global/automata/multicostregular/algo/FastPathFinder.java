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
package choco.cp.solver.constraints.global.automata.multicostregular.algo;

import choco.kernel.memory.IStateIntVector;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.cp.solver.constraints.global.automata.multicostregular.structure.StoredDirectedMultiGraph;
import choco.cp.solver.constraints.global.automata.multicostregular.FastMultiCostRegular;

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

    public FastPathFinder(StoredDirectedMultiGraph graph)
    {
        this.graph = graph;
        this.sp = new int[graph.layers.length-1];
        this.lp = new int[graph.layers.length-1];

    }



    public void resetNodeLongestPathValues() {

        Arrays.fill(graph.GNodes.lpfs, Double.NEGATIVE_INFINITY);
        Arrays.fill(graph.GNodes.lpft, Double.NEGATIVE_INFINITY);


    }

    public void computeLongestPath(IStateIntVector removed, double[][] cost, double lb) {


        graph.GNodes.lpfs[graph.sourceIndex] = 0.0;
        graph.GNodes.lpft[graph.tinIndex] = 0.0;

        for (int i = 0 ; i < cost.length ; i++)
        {
            for (int orig : graph.layers[i]) {
                DisposableIntIterator out = graph.GNodes.outArcs[orig].getIterator();

                while (out.hasNext())
                {
                    int e = out.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.dests[e];//e.getDestination();
                        double newCost = graph.GNodes.lpfs[orig] + cost[graph.GNodes.layers[orig]][graph.GArcs.values[e]];

                        if (graph.GNodes.lpfs[next] < newCost)
                        {
                            graph.GNodes.lpfs[next] = newCost;
                            graph.GNodes.prevLP[next] = e;
                        }
                    }

                }
                out.dispose();
            }
        }
        for (int i = cost.length ; i > 0 ; i--)
        {
            for (int dest : graph.layers[i]){
                DisposableIntIterator in = graph.GNodes.inArcs[dest].getIterator();

                while (in.hasNext())
                {
                    int e = in.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.origs[e];
                        double newCost = graph.GNodes.lpft[dest] + cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost + graph.GNodes.lpfs[next] -lb <= -FastMultiCostRegular.D_PREC)
                        {
                            graph.getInStack().set(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.lpft[next] < newCost)
                        {
                            graph.GNodes.lpft[next] = newCost;
                            graph.GNodes.nextLP[next] = e;
                        }
                    }

                }
            }
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

    public void computeShortestPath(IStateIntVector removed, double[][] cost, double ub)
    {

        graph.GNodes.spfs[graph.sourceIndex] = 0.0;
        graph.GNodes.spft[graph.tinIndex] = 0.0;


        for (int i = 0 ; i < cost.length ; i++)
        {
            for (int n : graph.layers[i])
            {
                DisposableIntIterator out = graph.GNodes.outArcs[n].getIterator();
                while (out.hasNext())
                {
                    int e = out.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.dests[e];//.getDestination();
                        double newCost = graph.GNodes.spfs[n] + cost[i][graph.GArcs.values[e]];
                        if (graph.GNodes.spfs[next] > newCost)
                        {
                            graph.GNodes.spfs[next] = newCost;
                            graph.GNodes.prevSP[next] = e;

                        }
                    }
                }
                out.dispose();
            }
        }
        for (int i = cost.length ; i > 0 ; i--)
        {
            for (int n : graph.layers[i])
            {
                DisposableIntIterator in = graph.GNodes.inArcs[n].getIterator();//getInEdgeIterator(n);
                while (in.hasNext())
                {
                    int e = in.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.origs[e];//e.getOrigin()  ;
                        double newCost = graph.GNodes.spft[n] + cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost + graph.GNodes.spfs[next] - ub >= FastMultiCostRegular.D_PREC)
                        {
                            graph.getInStack().set(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.spft[next] > newCost)
                        {
                            graph.GNodes.spft[next] = newCost;
                            graph.GNodes.nextSP[next] = e;
                        }
                    }
                }
            }
        }

    }

    public double getShortestPathValue() {
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

    public void computeShortestAndLongestPath(IStateIntVector removed, double[][] cost, int lb, int ub) {

        graph.GNodes.spfs[graph.sourceIndex] = 0.0;
        graph.GNodes.spft[graph.tinIndex] = 0.0;
        graph.GNodes.lpfs[graph.sourceIndex] = 0.0;
        graph.GNodes.lpft[graph.tinIndex] = 0.0;


        for (int i = 0 ; i < cost.length ; i++)
        {
            for (int n : graph.layers[i])
            {
                DisposableIntIterator out = graph.GNodes.outArcs[n].getIterator();
                while (out.hasNext())
                {
                    int e = out.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.dests[e];//.getDestination();
                        double newCost = graph.GNodes.spfs[n] + cost[i][graph.GArcs.values[e]];
                        if (graph.GNodes.spfs[next] > newCost)
                        {
                            graph.GNodes.spfs[next] = newCost;
                            graph.GNodes.prevSP[next] = e;

                        }
                        double newCost2 = graph.GNodes.lpfs[n] + cost[graph.GNodes.layers[n]][graph.GArcs.values[e]];

                        if (graph.GNodes.lpfs[next] < newCost2)
                        {
                            graph.GNodes.lpfs[next] = newCost2;
                            graph.GNodes.prevLP[next] = e;
                        }




                    }
                }
                out.dispose();
            }
        }
        for (int i = cost.length ; i > 0 ; i--)
        {
            for (int n : graph.layers[i])
            {
                DisposableIntIterator in = graph.GNodes.inArcs[n].getIterator();//getInEdgeIterator(n);
                while (in.hasNext())
                {
                    int e = in.next();
                    if (!graph.isInStack(e))
                    {
                        int next = graph.GArcs.origs[e];//e.getOrigin()  ;
                        double newCost = graph.GNodes.spft[n] + cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost + graph.GNodes.spfs[next] - ub >= FastMultiCostRegular.D_PREC)
                        {
                            graph.getInStack().set(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.spft[next] > newCost)
                        {
                            graph.GNodes.spft[next] = newCost;
                            graph.GNodes.nextSP[next] = e;
                        }

                        double newCost2 = graph.GNodes.lpft[n] + cost[graph.GNodes.layers[next]][graph.GArcs.values[e]];
                        if (newCost2 + graph.GNodes.lpfs[next] -lb <= -FastMultiCostRegular.D_PREC)
                        {
                            graph.getInStack().set(e);
                            removed.add(e);
                        }
                        else if (graph.GNodes.lpft[next] < newCost2)
                        {
                            graph.GNodes.lpft[next] = newCost2;
                            graph.GNodes.nextLP[next] = e;
                        }



                    }
                }
            }
        }


    }
}