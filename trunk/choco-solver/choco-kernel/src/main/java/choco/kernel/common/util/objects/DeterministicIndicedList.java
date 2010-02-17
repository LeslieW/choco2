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
package choco.kernel.common.util.objects;

import choco.kernel.common.IIndex;
import gnu.trove.TLongIntHashMap;

import static java.lang.reflect.Array.newInstance;
import java.util.Arrays;
import java.util.Iterator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 juin 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*
* Structure created to recorded data of the model.
* It includes an array of indiced objects,
* a hashmap of indices.
*
* It allows deterministic iteration.
*/
public class DeterministicIndicedList<O extends IIndex>{

    protected Class clazz;

    /**
     * Speed hash set of constraint indices
     */
    protected TLongIntHashMap indices;

    /**
     * All the object
     */
    private O[] objects;

    /**
     * indice of the last object
     */
    protected int last;

    /**
     * Constructor
     * @param clazz the super Class of include object
     * @param initialSize initial size of the structure
     */
    public DeterministicIndicedList(Class clazz, int initialSize) {
        this.clazz = clazz;
        indices = new TLongIntHashMap(initialSize);
        objects = (O[]) newInstance(clazz, initialSize);
        last = 0;
    }

    /**
     * Constructor
     * @param clazz the super Class of include object
     */
    public DeterministicIndicedList(Class clazz) {
        this(clazz, 32);
    }

    public void clear(){
        indices.clear();
        Arrays.fill(objects, null);
        objects = null;
    }

    /**
     * Add object to the structure
     * @param object
     */
    public void add(O object){
        if(!indices.containsKey(object.getIndex())){
            ensureCapacity();
            objects[last] = object;
            indices.put(object.getIndex(), last++);
        }
    }

    /**
     * Ensure that the array has a correct size
     */
    private void ensureCapacity(){
        if(last >= objects.length){
            // treat the case where intial value = 1
            int cindT = objects.length * 3/2+1;
            O[] objectsT = (O[]) newInstance(clazz, cindT);
            System.arraycopy(objects, 0, objectsT, 0, last);
            objects = objectsT;
        }
    }


    /**
     * Remove object from the structure
     * We just swap the last object and the removed object 
     * @param object to remove
     */
    public int remove(O object){
        if(indices.containsKey(object.getIndex())){
            int ind = indices.get(object.getIndex());
            indices.remove(object.getIndex());
            if(last > 0 && ind < last-1){
                objects[ind] = objects[last-1];
                indices.adjustValue(objects[ind].getIndex(), -last+ind+1);
            }
            objects[--last] = null;
            return ind;
        }
        return -1;
    }

    /**
     * Indicates wether the structure contains the object
     * @param object
     * @return
     */
    public boolean contains(O object){
        return indices.containsKey(object.getIndex());
    }

    /**
     * Get the number of objects contained
     * @return
     */
    public int size(){
        return last;
    }

    /**
     * Get the object in position i
     * @param i position of the object
     * @return the ith object
     */
    public O get(int i){
        return objects[i];
    }

    /**
     * Get the position of the object
     * @param object required
     * @return its position
     */
    public int get(O object){
        return indices.get(object.getIndex());
    }


    public O getLast(){
        if(last>0){
            return objects[last-1];
        }else{
            return null;
        }
    }

    /**
     * Iterator over objects
     * @return
     */
    public Iterator<O> iterator(){
        return new Iterator<O>(){
            int current = 0;

            /**
             * Returns <tt>true</tt> if the iteration has more elements. (In other
             * words, returns <tt>true</tt> if <tt>next</tt> would return an element
             * rather than throwing an exception.)
             *
             * @return <tt>true</tt> if the iterator has more elements.
             */
            @Override
            public boolean hasNext() {
                return current < last;
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration.
             * @throws java.util.NoSuchElementException
             *          iteration has no more elements.
             */
            @Override
            public O next() {
                return objects[current++];
            }

            /**
             * Removes from the underlying collection the last element returned by the
             * iterator (optional operation).  This method can be called only once per
             * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
             * the underlying collection is modified while the iteration is in
             * progress in any way other than by calling this method.
             *
             * @throws UnsupportedOperationException if the <tt>remove</tt>
             *                                       operation is not supported by this Iterator.
             * @throws IllegalStateException         if the <tt>next</tt> method has not
             *                                       yet been called, or the <tt>remove</tt> method has already
             *                                       been called after the last call to the <tt>next</tt>
             *                                       method.
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("operation is not supported by this Iterator");
            }
        };
    }
}