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
package choco.cp.solver.constraints.global.fast_regular.structure;

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
public class StoredIndexedBipartiteSetWithAccess extends StoredIndexedBipartiteSet
{
    public StoredIndexedBipartiteSetWithAccess(IEnvironment environment, int[] values) {
        super(environment, values);
    }

    public StoredIndexedBipartiteSetWithAccess(IEnvironment environment, IndexedObject[] values) {
        super(environment, values);
    }

    public StoredIndexedBipartiteSetWithAccess(IEnvironment environment, ArrayList<IndexedObject> values) {
        super(environment, values);
    }

    public StoredIndexedBipartiteSetWithAccess(IEnvironment environment, int nbValues) {
        super(environment, nbValues);
    }


    public int[] getAllValues()
    {
      return this.list; 
    }
}