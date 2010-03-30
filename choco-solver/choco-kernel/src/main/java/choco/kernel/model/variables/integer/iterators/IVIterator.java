/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
package choco.kernel.model.variables.integer.iterators;

import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 29 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public final class IVIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static IVIterator instance = IVIterator.build();

        private static void set(final IVIterator iterator) {
            instance = iterator;
        }
    }

    private int upp;
    private int value;
    private int[] values;

    private IVIterator() {
    }

    private static IVIterator build() {
        return new IVIterator();
    }

    @SuppressWarnings({"unchecked"})
    public static synchronized IVIterator getIterator(final int theLow, final int theUpp, final int[] theValues) {
        IVIterator it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(theLow, theUpp, theValues);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int theLow, final int theUpp, final int[] theValues) {
        super.init();
        this.upp = theUpp;
        this.values = theValues;
        if(values != null){
            value = 0;
        }else{
            value = theLow;
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
        if (values == null) {
            return value <= upp;
        } else {
            return value < values.length;
        }
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
        if (values == null){
            return value++;
        }else{
            return values[value++];
        }
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