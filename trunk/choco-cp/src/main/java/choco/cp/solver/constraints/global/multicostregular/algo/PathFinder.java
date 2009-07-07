package choco.cp.solver.constraints.global.multicostregular.algo;

import choco.cp.solver.constraints.global.multicostregular.structure.ILayeredGraph;
import choco.cp.solver.constraints.global.multicostregular.structure.IArc;
import choco.cp.solver.constraints.global.multicostregular.structure.INode;
import choco.cp.solver.constraints.global.multicostregular.MultiCostRegular;
import choco.kernel.memory.IStateIntVector;

import java.util.Iterator;



/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jul 22, 2008
 * Time: 12:10:26 PM
 *
 * Class That embeds algorithms to compute shortest and longest path
 * in a given Layered graph.
 *
 */
public class PathFinder {

    /**
     * the graph in which 
     */
    final ILayeredGraph graph;

    /**
     * Array to store the last computed shortest part
     */
    final IArc[] sp;

    /**
     * Array to store the last computed longest path
     */
    final IArc[] lp;


    /**
     * Constructs a PathFinder instance
     * @param graph the LayeredGraph on which computation will be made
     */
    public PathFinder(final ILayeredGraph graph)
    {
        this.graph = graph;
        this.sp = new IArc[this.graph.getNbLayers()-1];
        this.lp = new IArc[this.graph.getNbLayers()-1];
    }


    /**
     * return the last computed shortest path
     * ATTENTION : does not compute the actual shortest path
     * @return an array of Arcs that forms the SP
     */
    public IArc[] getShortestPath()
    {
        int i = 0;
        INode current = this.graph.getSource();
        do {
            IArc e = current.getSptt();
            sp[i++]= (e);
            current = e.getDestination();

        } while(current.getSptt() != null);
        return sp;
    }

    /**
     * return the last computed longest path
     * ATTENTION : does not compute the actual longest path
     * @return an array of Arcs that forms the LP
     */
    public IArc[] getLongestPath()
    {
        int i = 0;
        INode current = this.graph.getSource();
        do {
            IArc e = current.getLptt();
            lp[i++] = e;
            current = e.getDestination();

        } while(current.getLptt() != null);
        return lp;
    }


    /**
     * Retrieve the last shortest path value computed
     * @return the shortest path value
     *  */
    public final double getShortestPathValue()
    {
        return this.graph.getTink().getSpfs();
    }

    /**
     * Retrieve the last longest path value computed
     * @return the longest path value
     *  */
    public final double getLongestPathValue()
    {
        return this.graph.getTink().getLpfs();
    }


    /**
     * Reset the value on nodes in order to prepare the shortest path algorithm
     */
    public void resetNodeShortestPathValues()
    {
        for (int i = 0 ; i < this.graph.getNbLayers() ; i++)
        {
            for (INode node : this.graph.getLayer(i)) node.resetShortestPathValues();
        }
    }

    /**
     * Reset the value on nodes in order to prepare the longest path algorithm
     */
    public  void resetNodeLongestPathValues()
    {
        for (int i = 0 ; i < this.graph.getNbLayers() ; i++)
        {
            for (INode node : this.graph.getLayer(i)) node.resetLongestPathValues();
        }
    }

      /**
     * Reset the value on nodes in order to prepare the longest and shortest path algorithms
     */
    public void resetNodeShortestandLongestPathValues()
    {
        for (int i = 0 ; i < this.graph.getNbLayers() ; i++)
        {
            for (INode node : this.graph.getLayer(i)) {
                node.resetLongestPathValues();
                node.resetShortestPathValues();
            }
        }
    }

    /**
     * Compute the shortest paths from the source to any other nodes and from the tink to any others.
     * If an arch becomes inconsistent w.r.t. the given upper bound, it is added to the to be removed vector
     * @param removed the vector that store the arcs that needs to be removed
     * @param cost the cost matrix of arcs
     * @param ub the upper bound of the shortest path
     */
    public void computeShortestPath(final IStateIntVector removed, final double[][][] cost, final double ub)
    {


        graph.getSource().setSpfs(0.0);
        graph.getTink().setSpft(0.0);

        for (int i = 0 ; i < cost.length ; i++)
        {
            for (INode n : graph.getLayer(i))
            {
                Iterator<IArc> out = graph.getOutEdgeIterator(n);
                while (out.hasNext())
                {
                    IArc e = out.next();
                    if (!graph.isInStack(e.getInStackIdx()))
                    {
                        INode next =e.getDestination();
                        double newCost = n.getSpfs() + cost[n.getLayer()][e.getLabel()][n.getState()];
                        if (next.getSpfs() > newCost)
                        {
                            next.setSpfs(newCost);
                            next.setSpts(e);
                        }
                    }
                }
            }
        }
        for (int i = cost.length ; i > 0 ; i--)
        {
            for (INode n : graph.getLayer(i))
            {
                Iterator<IArc> in = graph.getInEdgeIterator(n);
                while (in.hasNext())
                {
                    IArc e = in.next();
                    if (!graph.isInStack(e.getInStackIdx()))
                    {
                        INode next =e.getOrigin()  ;
                        double newCost = n.getSpft() + cost[next.getLayer()][e.getLabel()][next.getState()];

                        if (newCost + next.getSpfs() - ub >= MultiCostRegular.D_PREC)
                        {
                            graph.getInStack().set(e.getInStackIdx());
                            removed.add(e.getInStackIdx());
                        }
                        else if (next.getSpft() > newCost)
                        {
                            next.setSpft(newCost);
                            next.setSptt(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Compute the longest paths from the source to any other nodes and from the tink to any others.
     * If an arch becomes inconsistent w.r.t. the given lower bound, it is added to the to be removed vector
     * @param removed the vector that store the arcs that needs to be removed
     * @param cost the cost matrix of arcs
     * @param lb the lower bound of the longest path
     */
    public void computeLongestPath(final IStateIntVector removed, final double[][][] cost, final double lb)
    {


        graph.getSource().setLpfs(0.0);
        graph.getTink().setLpft(0.0);

        for (int i = 0 ; i < cost.length ; i++)
        {
            for (INode n : graph.getLayer(i))
            {
                Iterator<IArc> out = graph.getOutEdgeIterator(n);
                while (out.hasNext())
                {
                    IArc e = out.next();
                    if (!graph.isInStack(e.getInStackIdx()))
                    {
                        INode next =e.getDestination();

                        double newCost = n.getLpfs() + cost[n.getLayer()][e.getLabel()][n.getState()];
                        if (next.getLpfs() < newCost)
                        {
                            next.setLpfs(newCost);
                            next.setLpts(e);
                        }
                    }
                }
            }
        }
        for (int i = cost.length ; i > 0 ; i--)
        {
            for (INode n : graph.getLayer(i))
            {
                Iterator<IArc> in = graph.getInEdgeIterator(n);
                while (in.hasNext())
                {
                    IArc e = in.next();
                    if (!graph.isInStack(e.getInStackIdx()))
                    {
                        INode next =e.getOrigin();
                        double newCost = n.getLpft() + cost[next.getLayer()][e.getLabel()][next.getState()];
                        if (newCost + next.getLpfs() -lb <= -MultiCostRegular.D_PREC)
                        {
                            graph.getInStack().set(e.getInStackIdx());
                            removed.add(e.getInStackIdx());
                        }
                        else if (next.getLpft() < newCost)
                        {
                            next.setLpft(newCost);
                            next.setLptt(e);
                        }
                    }
                }
            }
        }

    }

    /**
     * Compute the shortest and longest paths from the source to any other nodes and from the tink to any others.
     * If an arch becomes inconsistent w.r.t. the given upper/lower bound, it is added to the to be removed vector
     * @param removed the vector that store the arcs that needs to be removed
     * @param cost the cost matrix of arcs
     * @param lb the lower bound of the longest path
     * @param ub the upper bound of the shortest path
     *
     */
    public void computeShortestAndLongestPath(final IStateIntVector removed,final double[][][] cost, final double lb,final double ub)
    {



        graph.getSource().setLpfs(0.0);
        graph.getTink().setLpft(0.0);
        graph.getSource().setSpfs(0.0);
        graph.getTink().setSpft(0.0);

        for (int i = 0 ; i < cost.length ; i++)
        {
            for (INode n : graph.getLayer(i))
            {
                Iterator<IArc> out = graph.getOutEdgeIterator(n);
                while (out.hasNext())
                {
                    IArc e = out.next();
                    if (!graph.isInStack(e.getInStackIdx()))
                    {
                        INode next =e.getDestination();
                        double newCost = n.getSpfs() + cost[n.getLayer()][e.getLabel()][n.getState()];
                        if (next.getSpfs() > newCost)
                        {
                            next.setSpfs(newCost);
                            next.setSpts(e);
                        }

                        newCost = n.getLpfs() + cost[n.getLayer()][e.getLabel()][n.getState()];
                        if (next.getLpfs() < newCost)
                        {
                            next.setLpfs(newCost);
                            next.setLpts(e);
                        }
                    }
                }
            }
        }
        for (int i = cost.length ; i > 0 ; i--)
        {
            for (INode n : graph.getLayer(i))
            {
                Iterator<IArc> in = graph.getInEdgeIterator(n);
                while (in.hasNext())
                {
                    IArc e = in.next();
                    if (!graph.isInStack(e.getInStackIdx()))
                    {
                        INode next =e.getOrigin();
                        double newCost = n.getLpft() + cost[next.getLayer()][e.getLabel()][next.getState()];
                        double newCost2 = n.getSpft() + cost[next.getLayer()][e.getLabel()][next.getState()];

                        if (newCost + next.getLpfs() -lb <= -MultiCostRegular.D_PREC
                                || newCost2 + next.getSpfs() - ub >= MultiCostRegular.D_PREC)
                        {

                            graph.getInStack().set(e.getInStackIdx());
                            removed.add(e.getInStackIdx());
                        }
                        else
                        {
                            if (next.getLpft() < newCost)
                            {
                                next.setLpft(newCost);
                                next.setLptt(e);
                            }
                            if (next.getSpft() > newCost2)
                            {
                                next.setSpft(newCost2);
                                next.setSptt(e);
                            }
                        }
                    }
                }
            }
        }
    }



}
