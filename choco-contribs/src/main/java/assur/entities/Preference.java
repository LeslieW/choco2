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
package assur.entities;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 21, 2009
 * Time: 2:13:04 PM
 */
public class Preference{

    public Date date;
    public int shift;

    public Preference(Date date, int shift)
    {
        this.date = date; this.shift = shift;
    }

    public boolean equals(Object o)
    {
        return o instanceof Preference && ((Preference) o).date.equals(date) && ((Preference) o).shift == shift;
    }

    public int hashCode()
    {
        return this.date.hashCode()^shift;
    }

}