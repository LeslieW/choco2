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
package choco.kernel.model;

import java.util.Set;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*
* Interface for options management
*/
public interface IOptions {

    /**
     * Add a single option to the pool of option.
     *
     *@param the option.
     */
    void addOption(String option);
    
    /**
     * Add several options to the pool of option.
     * @param options
     */
    void addOptions(String options);
    /**
     * Add an array of options to the pool of options
     * of the object
     * @param options array of options
     */
    void addOptions(String[] options);

    /**
     * Add a set of options to the pool of options
     * of the object
     * @param options set of options
     */
    void addOptions(Set<String> options);

    /**
     * Get the pool of unique options
     * @return set of options
     */
    Set<String> getOptions();
   
    /**
     * check if the option is activated
     * @return <code>true</code> if the set contains the option
     */
    boolean containsOption(String option);
}
