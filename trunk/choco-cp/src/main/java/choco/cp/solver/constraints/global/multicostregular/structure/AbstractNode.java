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


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jun 8, 2009
 * Time: 2:46:33 PM
 */
public class AbstractNode implements INode{

    /**
     * static field to give an unique id to every created instance
     */
    private static int ID = 0;

    /**
     * unique id of the node
     */
    protected final int id;

    /**
     * Layer of the graph in which the node is
     */
    protected final int layer;

    /**
     * The state index this node represents
     */
    protected final int state;

    /**
     * Shortest path value from this node to the source
     */
    protected double spfs = Double.POSITIVE_INFINITY;

    /**
     * Shortest path value from this node to the tink
     */
    protected double spft = Double.POSITIVE_INFINITY;

    /**
     * previous arc that belongs to the shortest path to the source
     */
    protected IArc spts;

    /**
     * next arc that belongs to the shortest path to the tink
     */
    protected IArc sptt;

    /**
     * Longest path value from this node to the source
     */
    protected double lpfs = Double.NEGATIVE_INFINITY;

    /**
     * Longest path value from this node to the tink
     */
    protected double lpft = Double.NEGATIVE_INFINITY;

    /**
     * previous arc that belongs to the longest path to the source
     */
    protected IArc lpts;

    /**
     * next arc that belongs to the longest path to the tink
     */
    protected IArc lptt;




    public AbstractNode(final int layer, final int state)
    {
        this.layer = layer;
        this.state = state;
        this.id = ID++;
    }



    /**
     * Set the shortest paths to the source and to the tink to be infinted
     * This allow the shortest path algorithm to work
     */
    public final void resetShortestPathValues()
    {
        this.spfs = Double.POSITIVE_INFINITY;
        this.spft = Double.POSITIVE_INFINITY;

    }

    /**
     * Set the longest paths to the source and to the tink to be infinted
     * This allow the longest path algorithm to work
     */
    public final void resetLongestPathValues()
    {
        this.lpfs = Double.NEGATIVE_INFINITY;
        this.lpft = Double.NEGATIVE_INFINITY;
    }


    /**
     * Accessor to the layer this node belongs
     * @return the index of the layer
     */
    public final int getLayer()
    {
        return this.layer;
    }

    public final int getId() {
        return id;
    }

    public final int getState() {
        return state;
    }

     public final double getSpfs() {
        return spfs;
    }

    public final void setSpfs(double spfs) {
        this.spfs = spfs;
    }

    public final double getSpft() {
        return spft;
    }

    public final void setSpft(double spft) {
        this.spft = spft;
    }

    public final IArc getSpts() {
        return spts;
    }

    public final void setSpts(IArc spts) {
        this.spts = spts;
    }

    public final IArc getSptt() {
        return sptt;
    }

    public final void setSptt(IArc sptt) {
        this.sptt = sptt;
    }

    public final double getLpfs() {
        return lpfs;
    }

    public final void setLpfs(double lpfs) {
        this.lpfs = lpfs;
    }

    public final double getLpft() {
        return lpft;
    }

    public final void setLpft(double lpft) {
        this.lpft = lpft;
    }

    public final IArc getLpts() {
        return lpts;
    }

    public final void setLpts(IArc lpts) {
        this.lpts = lpts;
    }

    public final IArc getLptt() {
        return lptt;
    }

    public final void setLptt(IArc lptt) {
        this.lptt = lptt;
    }

    public boolean equals(Object o)
    {
        if (o instanceof INode)
        {
            INode n = (INode) o;
            return (n.getLayer() == this.layer && n.getState() == this.state);
        }
        return false;
    }


    public int compareTo(Object o)
    {
        INode n = (INode) o;
        return this.id < n.getId() ? -1 : this.id == n.getId() ? 0 : 1;
    }
}