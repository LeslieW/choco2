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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.common;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 17 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*
* Index of an object.
*
* BEWARE : IT IS NOT SIMILAR TO HASHCODE!!!
*
* Index can change from one execution to another one. HashCode should not!
*/
public interface IIndex {
    
    /**
     * Unique index 
     * (Different from hashCode, can change from one execution to another one) 
     * @return the indice of the objet
     */
    public long getIndice();

}
