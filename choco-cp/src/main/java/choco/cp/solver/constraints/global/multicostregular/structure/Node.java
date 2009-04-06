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
 * Date: Jul 17, 2008
 * Time: 10:33:01 AM
 * Class to represent a Node in the layered graph of the Multi-Cost-Regular propagator
 */
public class Node implements Comparable{

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
     *  where can the outgoing arcs be found in the layeredgraph bitset
     */
    protected int outOffset;

    /**
     * The number of outgoing arcs
     */
    protected int nbOutArcs;

    /**
     * where can the incomming arcs be found in the layeredgraph bitset
     */
    protected int inOffset;

    /**
     * The number of incomming arcs
     */
    protected int nbInArcs;

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
    protected Arc spts;

    /**
     * next arc that belongs to the shortest path to the tink
     */
    protected Arc sptt;

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
    protected Arc lpts;

    /**
     * next arc that belongs to the longest path to the tink
     */
    protected Arc lptt;


    /**
     * Create a new Node Instance
     * @param layer The layer the node will be put in
     * @param state The associated automaton state
     */
    public Node(final int layer, final int state)
    {
        this.layer = layer;
        this.state = state;
        this.id = Node.ID++;
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

    public final int getOutOffset() {
        return outOffset;
    }

    public final void setOutOffset(int outOffset) {
        this.outOffset = outOffset;
    }

    public final int getNbOutArcs() {
        return nbOutArcs;
    }

    public final void setNbOutArcs(int nbOutArcs) {
        this.nbOutArcs = nbOutArcs;
    }

    public final int getInOffset() {
        return inOffset;
    }

    public final void setInOffset(int inOffset) {
        this.inOffset = inOffset;
    }

    public final int getNbInArcs() {
        return nbInArcs;
    }

    public final void setNbInArcs(int nbInArcs) {
        this.nbInArcs = nbInArcs;
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

    public final Arc getSpts() {
        return spts;
    }

    public final void setSpts(Arc spts) {
        this.spts = spts;
    }

    public final Arc getSptt() {
        return sptt;
    }

    public final void setSptt(Arc sptt) {
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

    public final Arc getLpts() {
        return lpts;
    }

    public final void setLpts(Arc lpts) {
        this.lpts = lpts;
    }

    public final Arc getLptt() {
        return lptt;
    }

    public final void setLptt(Arc lptt) {
        this.lptt = lptt;
    }

    public boolean equals(Object o)
    {
        if (o instanceof Node)
        {
            Node n = (Node) o;
            return (n.layer == this.layer && n.state == this.state);
        }
        return false;
    }


    public int compareTo(Object o)
    {
        Node n = (Node) o;
        return this.id < n.id ? -1 : this.id == n.id ? 0 : 1;
    }


}
