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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.common.util.iterators;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class IntArrayIterator extends DisposableIntIterator{

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static IntArrayIterator instance = IntArrayIterator.build();

        private static void set(final IntArrayIterator iterator) {
            instance = iterator;
        }
    }

    private int[] elements;
    private int endIdx;
    private int curentIdx;

    private IntArrayIterator() {
    }

    private static IntArrayIterator build() {
        return new IntArrayIterator();
    }

    /**
     * Get iterator over {@code someElements} starting from {@code from} to {@code to} (included).
     * @param someElements array of int
     * @param from starting index
     * @param to ending index (excluded)
     * @return Disposable iterator
     */
    @SuppressWarnings({"unchecked"})
    public static synchronized IntArrayIterator getIterator(final int[] someElements, final int from, final int to) {
        IntArrayIterator it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(someElements, from, to);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int[] someElements, final int from, final int to) {
        super.init();
        this.elements = someElements;
        this.endIdx = to;
        curentIdx = from;
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
        return curentIdx < endIdx;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    @Override
    public int next() {
        return elements[curentIdx++];
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
