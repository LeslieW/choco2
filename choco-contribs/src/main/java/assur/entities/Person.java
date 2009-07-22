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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 20, 2009
 * Time: 5:57:19 PM
 */
public class Person {

    String name;
    HashSet<Date> hollydays;
    Preferences preferences;
    private int index;

    public Person(String name)
    {
        this.name = name;
        this.preferences = new Preferences();
        this.hollydays = new HashSet<Date>();
    }


    public void addHollyday(Date d)
    {
        this.hollydays.add(d);
    }

    public HashSet<Date> getHollydays()
    {
        return this.hollydays;
    }


    public String getName() {
        return name;
    }

    public int getId()
    {
        return this.name.hashCode();
    }

    public Preference addPreference(Date d, int shift)
    {
        Preference p = new Preference(d,shift);
        this.preferences.addPreference(p);
        return p;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getIndex()
    {
        return this.index;
    }
}