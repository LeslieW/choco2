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
package choco.kernel.memory.structure.iterators;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.StoredBipartiteVarSet;
import choco.kernel.solver.variables.Var;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class SBVSIterator1<E extends Var> extends DisposableIterator<E> {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static SBVSIterator1 instance = SBVSIterator1.build();

        private static void set(final SBVSIterator1 iterator) {
            instance = iterator;
        }
    }

    private int i = -1;
    private StoredBipartiteVarSet storedBipartiteVarSet;
    private E[] elements;
    private IStateInt last;
    private int nlast;

    private SBVSIterator1() {}

    private static SBVSIterator1 build(){
        return new SBVSIterator1();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static <E extends Var> SBVSIterator1 getIterator(final StoredBipartiteVarSet aStoredBipartiteVarSet,
                                                final E[] someElements, final IStateInt last) {
        SBVSIterator1 it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(aStoredBipartiteVarSet, someElements, last);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final StoredBipartiteVarSet aStoredBipartiteVarSet, final E[] someElements, final IStateInt aLast) {
        super.init();
        this.storedBipartiteVarSet = aStoredBipartiteVarSet;
        this.elements = someElements;
        this.last = aLast;
        this.nlast = aLast.get();
        i = -1;
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
        i++;
        while (i < nlast && elements[i].isInstantiated()) {
            storedBipartiteVarSet.swap(i);
            nlast = last.get();
        }
        return i < nlast;
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
        return elements[i];
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
