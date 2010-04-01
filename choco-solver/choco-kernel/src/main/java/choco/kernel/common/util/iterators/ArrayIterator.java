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

public final class ArrayIterator<E> extends DisposableIterator<E> {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static ArrayIterator instance = ArrayIterator.build();

        private static void set(final ArrayIterator iterator) {
            instance = iterator;
        }
    }

    private E[] elements;

    private int size;

    private int cursor;

    private ArrayIterator() {
    }

    private static ArrayIterator build() {
        return new ArrayIterator();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static <E> ArrayIterator <E> getIterator(final E[] elements, final int size) {
        ArrayIterator<E> it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(elements, size);
        return it;
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static <E> ArrayIterator <E> getIterator(final E[] elements) {
        ArrayIterator<E> it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(elements, elements.length);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    private void init(final E[] someElements, final int aSize) {
        super.init();
        this.elements = someElements;
        this.size = aSize;
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

    /**
     * This method allows to declare that the iterator is not used anymoure. It
     * can be reused by another object.
     */
    @Override
    public void dispose() {
        super.dispose();
        Holder.set(this);
    }
}
