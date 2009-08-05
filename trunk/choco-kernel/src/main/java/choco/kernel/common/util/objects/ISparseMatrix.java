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
package choco.kernel.common.util.objects;

import java.util.Iterator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 8 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public interface ISparseMatrix {

    /**
     * Add a new element in the matrix
     * @param i
     * @param j
     */
    public void add(int i, int j);


    /**
     * get the number of element contained in the matrix
     * @return the number of element
     */
    public int getNbElement();

    /**
     * Return an iterator over the values
     * @return an iterator
     */
    public Iterator<Long> iterator();

    /**
     * Prepare the matrix for correct iteration.
     */
    public void prepare();
}
