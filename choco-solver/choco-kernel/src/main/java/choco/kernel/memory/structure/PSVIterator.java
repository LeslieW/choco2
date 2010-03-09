package choco.kernel.memory.structure;

import static choco.kernel.common.Constant.STORED_OFFSET;
import choco.kernel.common.util.iterators.DisposableIterator;

import java.util.NoSuchElementException;

public class PSVIterator<E> extends DisposableIterator<E> {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    private static PSVIterator _cachedPSVIterator;

    @SuppressWarnings({"unchecked"})
    public static DisposableIterator getIterator(PartiallyStoredVector vector) {
        if (_cachedPSVIterator != null && _cachedPSVIterator.reusable) {
            _cachedPSVIterator.init(vector);
            return _cachedPSVIterator;
        } else {
            _cachedPSVIterator = new PSVIterator(vector);
            return _cachedPSVIterator;
        }

    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    int idx;
    PartiallyStoredVector<E> vector;

    private PSVIterator(PartiallyStoredVector<E> vector) {
        init(vector);
    }

    public void init(PartiallyStoredVector<E> vector) {
        super.init();
        this.vector = vector;
        idx = -1;
    }

    public boolean hasNext() {
        if (idx < STORED_OFFSET) {
            return idx + 1 < vector.nStaticObjects || vector.nStoredObjects.get() > 0;
        } else return idx + 1 < STORED_OFFSET + vector.nStoredObjects.get();
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
}