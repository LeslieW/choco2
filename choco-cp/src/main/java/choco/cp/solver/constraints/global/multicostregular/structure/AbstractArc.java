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
package choco.cp.solver.constraints.global.multicostregular.structure;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jun 8, 2009
 * Time: 3:14:11 PM
 */
public abstract class AbstractArc implements IArc{

    /**
     * Label carried by this Edge
     */
    protected final int j;





    /**
     * Construct a new Arc for a Layered graph
     * @param j Arc label
     */
    public AbstractArc( final int j)
    {
        this.j = j;
    }

    @Override
    public int getLabel() {
        return j;
    }






    public int compareTo(final IArc edge) {
        return (this.getDestination().getId() < edge.getDestination().getId()) ? -1 : (this.getDestination().getId() == edge.getDestination().getId()) ? 0 : 1;
    }

}