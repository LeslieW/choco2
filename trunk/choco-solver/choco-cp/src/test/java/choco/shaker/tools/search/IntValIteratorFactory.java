/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.shaker.tools.search;

import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.kernel.solver.search.ValIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 5 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class IntValIteratorFactory {

    public ArrayList<V> scope = new ArrayList<V>();

    public enum V {
        DECDOM, INCDOM
    }

    /**
     * Define a specific scope of value iterator tuple to pick up in
     * @param vs the scope of value iterator
     */
    public void scopes(V... vs){
        scope.clear();
        scope.addAll(Arrays.asList(vs));
    }

    /**
     * Select randomly (among scope if defined)
     * and return a value iterator type
     * @param r random
     * @return type of value iterator
     */
    public V any(Random r) {
        if(scope.size()>0){
            return scope.get(r.nextInt(scope.size()));
        }
        V[] values = V.values();
        return values[r.nextInt(values.length)];
    }


    /**
     * Get one value iterator among all
     * @param r random
     * @return value iterator
     */
    public ValIterator make(Random r){
        return make(any(r), r);
    }

    /**
     * Create and return the corresponding value iterator
     * @param v the type of value iterator
     * @param r random
     * @return value iterator
     */
    public ValIterator make(V v, Random r) {
        // Select a new val iterator
        ValIterator vi = null;

        switch (v) {
            case DECDOM:
                vi = new DecreasingDomain();
            break;
            case INCDOM:
                vi = new IncreasingDomain();
            break;

        }
        return vi;
    }
}