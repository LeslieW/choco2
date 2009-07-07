package choco.cp.solver.constraints.global.multicostregular.structure;

import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.cp.solver.constraints.global.multicostregular.algo.PathFinder;

import java.util.Iterator;/* * * * * * * * * * * * * * * * * * * * * * * * *
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

public interface ILayeredGraph {
    
    
    IStateBitSet getInStack();

    boolean isInStack(int idx);

    void setInStack(int idx);

    void clearInStack(int idx);

    PathFinder getPF();

    void removeEdge(int outIndex, IStateIntVector hs) throws ContradictionException;

    Iterator<IArc> getOutEdgeIterator(INode n);

    Iterator<IArc> getInEdgeIterator(INode n);

    Iterator<IArc> getAllActiveEdgeIterator();

    INode[] getLayer(int i);

    int getNbLayers();

    INode getSource();

    INode getTink();
}
