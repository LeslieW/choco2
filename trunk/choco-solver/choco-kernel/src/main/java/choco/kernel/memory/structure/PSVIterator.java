package choco.kernel.memory.structure;

import static choco.kernel.common.Constant.STORED_OFFSET;
import choco.kernel.common.util.iterators.DisposableIterator;

import java.util.NoSuchElementException;

public final class PSVIterator<E> extends DisposableIterator<E> {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {}

        private static PSVIterator instance = PSVIterator.build();

        private static void set(final PSVIterator iterator) {
            instance = iterator;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <E> DisposableIterator getIterator(final PartiallyStoredVector vector) {
        PSVIterator<E> it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(vector);
        return it;

    }

    private int idx;

    private PartiallyStoredVector<E> vector;

    private PSVIterator() {}

    private static PSVIterator build() {
        return new PSVIterator();
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final PartiallyStoredVector<E> aVector) {
        super.init();
        this.vector = aVector;
        idx = -1;
    }

    public boolean hasNext() {
        if (idx < STORED_OFFSET) {
            return idx + 1 < vector.nStaticObjects || vector.nStoredObjects.get() > 0;
        } else {
            return idx + 1 < STORED_OFFSET + vector.nStoredObjects.get();
        }
    }

    public E next() {
        if (idx < STORED_OFFSET) {
            if (idx + 1 < vector.nStaticObjects) {
                idx++;
                while (vector.staticObjects[idx] == null && idx < vector.nStaticObjects) {
                    idx++;
                }
            } else if (vector.nStoredObjects.get() > 0) {
                idx = STORED_OFFSET;
            } else {
                throw new NoSuchElementException();
            }
        } else if (idx + 1 < STORED_OFFSET + vector.nStoredObjects.get()) {
            idx++;
        } else {
            throw new NoSuchElementException();
        }
        return vector.get(idx);
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