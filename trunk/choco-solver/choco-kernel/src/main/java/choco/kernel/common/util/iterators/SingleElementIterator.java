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
package choco.kernel.common.util.iterators;

public final class SingleElementIterator<E> extends DisposableIterator<E> {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {}

        private static SingleElementIterator instance = SingleElementIterator.build();

        private static void set(final SingleElementIterator iterator){
            instance = iterator;
        }
    }


	private E elem;

	private boolean hnext;

	private SingleElementIterator() {}

    private static SingleElementIterator build(){
        return new SingleElementIterator();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static <E> SingleElementIterator getIterator(final E element) {
        SingleElementIterator<E> it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(element);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final E anElement) {
        super.init();
        this.elem = anElement;
        hnext=true;
    }

    @Override
	public boolean hasNext() {
		return hnext;
	}

    @Override
	public E next() {
        hnext=false;
        return elem;
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



