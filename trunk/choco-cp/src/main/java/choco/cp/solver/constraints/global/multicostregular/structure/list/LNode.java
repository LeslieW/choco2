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
package choco.cp.solver.constraints.global.multicostregular.structure.list;

import choco.cp.solver.constraints.global.multicostregular.structure.AbstractNode;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.IndexedObject;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jun 8, 2009
 * Time: 2:45:56 PM
 */
public class LNode extends AbstractNode {


    ArrayList<LArc> tempOut;
    ArrayList<LArc> tempIn;

    StoredIndexedBipartiteSet outArcs;
    StoredIndexedBipartiteSet inArcs;

    int nbOutArcs;
    int nbInArcs;

    public LNode(final int layer, final int state) {
        super(layer, state);
        this.nbOutArcs =0;
        this.nbInArcs = 0;
        this.tempOut = new ArrayList<LArc>();
        this.tempIn = new ArrayList<LArc>();
        
    }

    public void addOutArc(LArc arc)
    {
        arc.setOutIdx(nbOutArcs++);
        tempOut.add(arc);
    }
     public void addInArc(LArc arc)
    {
        arc.setInIdx(nbInArcs++);
        tempIn.add(arc);
    }

    public void makeDataStructure(IEnvironment env)
    {
        for (LArc a : tempOut)
            a.setOutArc();
        IndexedObject[] tmp = tempOut.toArray(new IndexedObject[tempOut.size()]);
        outArcs = new StoredIndexedBipartiteSet(env,tmp);
        tempOut = null;

        for (LArc a : tempIn)
            a.setInArc();
        tmp = tempIn.toArray(new IndexedObject[tempIn.size()]);
        inArcs = new StoredIndexedBipartiteSet(env,tmp);
        tempIn = null;
    }

    public final StoredIndexedBipartiteSet getOutArcs()
    {
        return outArcs;
    }

    public final StoredIndexedBipartiteSet getInArcs()
    {
        return inArcs;
    }
    

}