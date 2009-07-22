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

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 20, 2009
 * Time: 6:02:00 PM
 */
public class Day implements Comparable<Day>{


    int year;
    int month;
    int day;

    public Day(int day, int month, int year)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public boolean equals(Object o)
    {
        return o instanceof Day && this.compareTo((Day) o) == 0;
    }

    @Override
    public int compareTo(Day day) {
        int comp;
        if (day.year < this.year) comp = 1;
        else if (day.year > this.year) comp = -1;
        else
        {
            if (day.month < this.month) comp = 1;
            else if (day.month > this.month) comp =  -1;
            else
            {
                if (day.day < this.day) comp = 1;
                else if (day.day > this.day) comp = -1;
                else comp = 0;
            }
        }
        return comp;
    }
}