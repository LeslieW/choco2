package choco.kernel.memory.structure.iterators;

import static choco.kernel.common.Constant.STORED_OFFSET;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IStateInt;

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
    public static <E> DisposableIterator getIterator(final int theNStaticObjects, final E[] theStaticObjects,
                    final IStateInt theNStoredObjects,final E[] theStoredObjects) {
        PSVIterator<E> it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(theNStaticObjects, theStaticObjects, theNStoredObjects, theStoredObjects);
        return it;

    }

    private int nStaticObjects;

    private int nStoredObjects;

    private E[] staticObjects;

    private E[] storedObjects;

    private int idx;

    private PSVIterator() {}

    private static PSVIterator build() {
        return new PSVIterator();
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int theNStaticObjects, final E[] theStaticObjects,
                    final IStateInt theNStoredObjects,final E[] theStoredObjects) {
        super.init();
        idx = -1;
        this.nStaticObjects = theNStaticObjects;
        this.staticObjects = theStaticObjects;
        this.nStoredObjects = theNStoredObjects.get();
        this.storedObjects =  theStoredObjects;
    }

    public boolean hasNext() {
        if (idx < STORED_OFFSET) {
            return idx + 1 < nStaticObjects || nStoredObjects > 0;
        } else {
            return idx + 1 < STORED_OFFSET + nStoredObjects;
        }
    }

    public E next() {
        if (idx < STORED_OFFSET) {
            if (idx + 1 < nStaticObjects) {
                idx++;
                while (staticObjects[idx] == null && idx < nStaticObjects) {
                    idx++;
                }
                return staticObjects[idx];
            } else if (nStoredObjects > 0) {
                idx = STORED_OFFSET;
                return storedObjects[0];
            } else {
                throw new NoSuchElementException();
            }
        } else if (idx + 1 < STORED_OFFSET + nStoredObjects) {
            return storedObjects[++idx - STORED_OFFSET];
        } else {
            throw new NoSuchElementException();
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