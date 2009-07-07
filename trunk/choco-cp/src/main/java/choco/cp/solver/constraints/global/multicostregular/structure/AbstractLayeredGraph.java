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

import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.ContradictionException;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.cp.solver.constraints.global.multicostregular.algo.PathFinder;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jun 8, 2009
 * Time: 2:12:40 PM
 */
public abstract class AbstractLayeredGraph implements ILayeredGraph
{
    /**
     * Reusable iterator over all active arcs
     */
    protected  IAllActiveArcIterator activeIterator;

    /**
     * Reusable iterator over incomming arcs of a given node
     */
    protected  IInArcIterator inIterator;

    /**
     * Reusable iterator over outgoing arcs of a given node
     */
    protected  IOutArcIterator outIterator;

    /**
     * Instance of the class containing algorithm to find path in this graph
     */
    protected final PathFinder pathFinder;

    /**
     * Reference to the multicostregular constraint this layered graph is associated with
     */
    protected final IntSConstraint mcr;


    /**
     * Restorable bitset that indicates wheter an arc is in the to be removed stack or not
     */
    protected IStateBitSet inStack;

    /**
     * Variables sequence used to build this layered graph
     */
    protected final IntDomainVar[] vars;


    /**
     * Support for every variable-value pairs if Qij <= 0, then xi != j
     */
    protected final IStateIntVector Q;

    /**
     * The biggest domain size of the variables vars
     */
    protected int biggestDomainSize;

    /**
     * Number of layers in the graph (== layers.size())
     */
    protected final int nbLayers;

    public AbstractLayeredGraph(final IntDomainVar[] vars, final Automaton pi, final IntSConstraint mcr) throws ContradictionException
    {
        this.vars =vars;
        this.biggestDomainSize = Integer.MIN_VALUE;
        this.mcr = mcr;
        for (IntDomainVar v : vars)
        {
            int sz = v.getSup()+1;
            if (biggestDomainSize < sz)
                biggestDomainSize =sz;
        }

        this.Q = vars[0].getSolver().getEnvironment().makeIntVector();
        for (int i  =0 ; i < biggestDomainSize *vars.length ;i++)
            this.Q.add(0);

        this.nbLayers = vars.length+2;

        this.pathFinder = new PathFinder(this);




    }

    /**
     * Performs initial pruning if some arcs are already inconsistent
     * @throws ContradictionException  if a domain gets empty
     */
    protected void initialFilter() throws ContradictionException {
        for (int idx = 0 ; idx < this.Q.size() ; idx++)
        {
            int i = idx/ biggestDomainSize;
            int j = idx% biggestDomainSize;
            int tmp = this.Q.get(idx);
            if (tmp == Integer.MIN_VALUE || tmp == 0)
            {
                vars[i].removeVal(j,this.mcr.getConstraintIdx(i));
            }
        }
    }

    /**
     * Returns the restorable int counting the number of support for variable i value j
     * create one if not already done
     * @param i the index of the variable
     * @param j the value
     * @return the considered  IStateInt
     */
    protected  int getQ(final int i, final int j)
    {
        int idx  = i* biggestDomainSize +j;
        return this.Q.get(idx);
    }

    /**
     * set the number of support for the variable-value pair (x_i,j) to be a given value
     * @param i the index of the variable
     * @param j the value
     * @param val the new value for the number of support
     */
    protected  void setQ(final int i, final int j,final int val)
    {
        int idx  = i* biggestDomainSize +j;
        this.Q.set(idx,val);
    }

    /**
     * Getter to the is arc in to be removed stack bitSet
     * @return an instance of a storable bitset
     */
    public final IStateBitSet getInStack()
    {
        return inStack;
    }

    /**
     *  Getter, the idx th bit of the inStack bitSet
     * @param idx the index of the arc
     * @return true if a given arc is to be deleted
     */
    public final boolean isInStack(int idx)
    {
        return inStack.get(idx);
    }

    /**
     * Set the idx th bit of the to be removed bitset
     * @param idx the index of the bit
     */
    public final void setInStack(int idx)
    {
        inStack.set(idx);
    }

    /**
     * Clear the idx th bit of the to be removed bitset
     * @param idx the index of the bit
     */
    public final void clearInStack(int idx)
    {
        inStack.clear(idx);
    }

    /**
     * remove 1 from Qij
     * @param i the index of a variable
     * @param j the considered value
     * @throws ContradictionException if a domain get empty
     */
    protected void decQ(final int i, final int j) throws ContradictionException {
        if (i < vars.length)
        {
            int tmp = getQ(i,j) -1;
            setQ(i,j,tmp);
            if (tmp <= 0)
                vars[i].removeVal(j,this.mcr.getConstraintIdx(i));
        }

    }

    /**
     * add 1 to Qij
     * @param i the index of a variable
     * @param j the considered value
     */
    protected  void incQ(final int i, final int j)
    {
        setQ(i,j,getQ(i,j)+1);
    }


    public final int getNbLayers()
    {
        return this.nbLayers;
    }



    /**
     * Accessor to the pathfinder of this graph
     * @return a pathfinder instance
     */
    public final PathFinder getPF()
    {
        return pathFinder;
    }
}