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
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.kernel.common.util.iterators;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 18 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class MultipleIterators<E> extends DisposableIterator<E>{

    static MultipleIterators _iterator;

    public static <E>MultipleIterators getMultipleIterators(DisposableIterator<E>... its){
        if(_iterator == null){
            _iterator = new MultipleIterators<E>(its);
        }else{
            _iterator.init(its);
        }
        return _iterator;
    }


    DisposableIterator<E>[] its;
    int idx = 0;

    public MultipleIterators(DisposableIterator<E>[] its) {
        init(its);
    }

    public void init(DisposableIterator<E>[] its) {
        super.init();
        this.its = its;
        idx = 0;
    }

    /**
     * This method allows to declare that the iterator is not usefull anymoure. It
     * can be reused by another object.
     */
    @Override
    public void dispose() {
        super.dispose();
        for(DisposableIterator<E> it: its){
            it.dispose();
        }
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
        while(idx < its.length
                &&!its[idx].hasNext()){
            idx++;
        }
        return (idx < its.length && its[idx].hasNext());
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    @Override
    public E next() {
        return its[idx].next();
    }
}
