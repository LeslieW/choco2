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

public class ArrayIterator<E> extends DisposableIterator<E> {

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static ArrayIterator _quickIterator = null;

    @SuppressWarnings({"unchecked"})
    public static <E> ArrayIterator getIterator(final E[] elements, final int size) {
        ArrayIterator iter = _quickIterator;
        if (iter != null && iter.reusable) {
            iter.init(elements, size);
            return iter;
        }
        _quickIterator = new ArrayIterator(elements, size);
        return _quickIterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    E[] elements;
    int size = 0;
    int cursor = 0;

    private ArrayIterator(final E[] elements, final int size) {
        init(elements, size);
    }

    private void init(final E[] elements, final int size) {
        super.init();
        this.elements = elements;
        this.size = size;
        cursor = 0;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the getIterator has more elements.
     */
    @Override
    public boolean hasNext() {
        return cursor < size;
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
        return elements[cursor++];
    }
}
