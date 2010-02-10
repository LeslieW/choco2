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
package choco.kernel.solver.constraints.global.automata.common;

import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.memory.structure.IndexedObject;
import choco.kernel.memory.IEnvironment;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 5, 2009
 * Time: 12:40:42 PM
 */
public class StoredIndexedBipartiteSetWithOffset extends StoredIndexedBipartiteSet
{

    int offset;

    public StoredIndexedBipartiteSetWithOffset(IEnvironment environment, int[] values) {
        super(environment, values);
    }

    public StoredIndexedBipartiteSetWithOffset(IEnvironment environment, IndexedObject[] values) {
        super(environment, values);
    }

    public StoredIndexedBipartiteSetWithOffset(IEnvironment environment, ArrayList<IndexedObject> values) {
        super(environment, values);
    }

    public StoredIndexedBipartiteSetWithOffset(IEnvironment environment, int nbValues) {
        super(environment, nbValues);
    }

    public void buildList(IEnvironment environment, int[] values) {
        this.list = values;
        int maxElt = 0;
        int minElt = Integer.MAX_VALUE;
        for (int value : values) {
            if (value > maxElt) maxElt = value;
            if (value < minElt) minElt = value;
        }
        this.offset = minElt;
        this.position = new int[maxElt-offset + 1];
        for (int i = 0; i < values.length; i++) {
            position[values[i]-offset] = i;
        }
        this.last = environment.makeInt(list.length - 1);

    }

    public boolean contain(int object) {
        return position[object-offset] <= last.get();
    }


     public void remove(int object) {
        if (contain(object)) {
            int idxToRem = position[object-offset];
            if (idxToRem == last.get()) {
                last.add(-1);
            } else {
                int temp = list[last.get()];
                list[last.get()] = object;
                list[idxToRem] = temp;
                position[object-offset] = last.get();
                position[temp-offset] = idxToRem;
                last.add(-1);
            }
        }
    }
}