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
package maif.entities;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 20, 2009
 * Time: 6:06:44 PM
 */
public class Preferences implements Iterable<Preference>,Set<Preference> {


    HashSet<Preference> preferences;



    public Preferences(){
        this.preferences = new HashSet<Preference>();
    }

    void addPreference(Date d, int shift)
    {
        this.preferences.add(new Preference(d,shift));
    }

    void addPreference(Preference p)
    {
        this.preferences.add(p);
    }


    @Override
    public int size() {
        return preferences.size();
    }

    @Override
    public boolean isEmpty() {
        return preferences.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return preferences.contains(o);
    }

    @Override
    public Iterator<Preference> iterator() {
        return this.preferences.iterator();
    }

    @Override
    public Object[] toArray() {
        return preferences.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return preferences.toArray(ts);
    }

    @Override
    public boolean add(Preference preference) {
        return preferences.add(preference);
    }

    @Override
    public boolean remove(Object o) {
        return preferences.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return preferences.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends Preference> preferences) {
        return this.preferences.addAll(preferences);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return preferences.retainAll(objects);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return preferences.removeAll(objects);
    }

    @Override
    public void clear() {
        preferences.clear();
    }
}