/* ************************************************
 *           _       _                            *
 *          |  �(..)  |                           *
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
package choco.kernel.solver.branch;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 ao�t 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*
* An abstract class for all implementations of large branching objets (objects controlling the tree search)
*
* This class is ensuring use of old branching strategies
* See AbstractLargeIntBranchingStrategy to upgrade your branching
*/
@Deprecated
public abstract class AbstractLargeIntBranching extends AbstractIntBranching{
}