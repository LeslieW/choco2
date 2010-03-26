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
package choco.kernel.memory.structure;

import static choco.kernel.common.Constant.STORED_OFFSET;
import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class PSVIndexIterator extends DisposableIntIterator {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    private static PSVIndexIterator _cachedPSVIndexIterator;

    public static DisposableIntIterator getIndexIterator(PartiallyStoredVector vector) {
        if (_cachedPSVIndexIterator != null && _cachedPSVIndexIterator.isReusable()) {
            _cachedPSVIndexIterator.init(vector);
            return _cachedPSVIndexIterator;
        } else {
            _cachedPSVIndexIterator = new PSVIndexIterator(vector);
            return _cachedPSVIndexIterator;
        }

    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    int idx;
    PartiallyStoredVector vector;

    private PSVIndexIterator(PartiallyStoredVector vector) {
        init(vector);
    }

    public void init(PartiallyStoredVector vector) {
        super.init();
        this.vector = vector;
        idx = -1;
    }

    public boolean hasNext() {
        if (idx < STORED_OFFSET) {
            return idx + 1 < vector.nStaticObjects || vector.nStoredObjects.get() > 0;
        } else return idx + 1 < STORED_OFFSET + vector.nStoredObjects.get();
    }

    public int next() {
        if (idx < STORED_OFFSET) {
            if (idx + 1 < vector.nStaticObjects) {
                idx++;
                while (vector.staticObjects[idx] == null && idx < vector.nStaticObjects) {
                    idx++;
                }
            } else if (vector.nStoredObjects.get() > 0) {
                idx = STORED_OFFSET;
            } else {
                throw new java.util.NoSuchElementException();
            }
        } else if (idx + 1 < STORED_OFFSET + vector.nStoredObjects.get()) {
            idx++;
        } else {
            throw new java.util.NoSuchElementException();
        }
        return idx;
    }
}