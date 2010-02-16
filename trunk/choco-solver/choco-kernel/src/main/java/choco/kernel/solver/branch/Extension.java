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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver.branch;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 16 févr. 2010
 * Since : Choco 2.1.1
 *
 * Extension for Constraint and Variable.
 * Usefull for dynamic branching like {@link DomOverWDegBranching}
 */
public final class Extension {
    protected int nb = 0;

    public final void set(int val){
        nb = val;
    }

    public final int get(){
        return nb;
    }

    public final void add(int val){
        nb +=val;
    }
}
