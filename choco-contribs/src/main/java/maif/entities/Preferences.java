/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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