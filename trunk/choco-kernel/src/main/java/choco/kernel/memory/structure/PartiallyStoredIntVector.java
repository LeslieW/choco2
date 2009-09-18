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
package choco.kernel.memory.structure;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;


/**
 * A class implementing a vector with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, integers with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And integers with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 */
public class PartiallyStoredIntVector {
  static final int INITIAL_STATIC_CAPACITY = 16;
  static final int INITIAL_STORED_CAPACITY = 16;
  public static final int STORED_OFFSET = 1000000;

  protected int[] staticInts;
  protected int[] storedInts;

  protected int nStaticInts;
  protected IStateInt nStoredInts;

  public PartiallyStoredIntVector(IEnvironment env) {
    staticInts = new int[INITIAL_STATIC_CAPACITY];
    storedInts = new int[INITIAL_STORED_CAPACITY];
    nStaticInts = 0;
    nStoredInts = env.makeInt(0);
  }

  public int staticAdd(int o) {
    ensureStaticCapacity(nStaticInts + 1);
    staticInts[nStaticInts++] = o;
    return nStaticInts - 1;
  }

  public void ensureStaticCapacity(int n) {
      if (n > staticInts.length) {
          int newSize = staticInts.length;
          while (n >= newSize) {
              newSize = (3 * newSize) / 2;
          }
          int[] newStaticObjects = new int[newSize];
          System.arraycopy(staticInts, 0, newStaticObjects, 0, staticInts.length);
          this.staticInts = newStaticObjects;
      }
  }

  public int add(int o) {
    ensureStoredCapacity(nStoredInts.get() + 1);
    storedInts[nStoredInts.get()] = o;
    nStoredInts.add(1);
    return STORED_OFFSET + nStoredInts.get() - 1;
  }

  public void remove(int o) {
    staticInts[o] = staticInts[nStaticInts];
    staticInts[nStaticInts] = 0;
    nStaticInts--;
  }

  public void ensureStoredCapacity(int n) {
      if (n > storedInts.length) {
          int newSize = storedInts.length;
          while (n >= newSize) {
              newSize = (3 * newSize) / 2;
          }
          int[] newStoredObjects = new int[newSize];
          System.arraycopy(storedInts, 0, newStoredObjects, 0, storedInts.length);
          this.storedInts = newStoredObjects;
      }
  }

  public int get(int index) {
    if (index < STORED_OFFSET) {
      return staticInts[index];
    } else {
      return storedInts[index - STORED_OFFSET];
    }
  }

  public boolean isEmpty() {
    return ((nStaticInts == 0) && (nStoredInts.get() == 0));
  }

  public int size() {
    return (nStaticInts + nStoredInts.get());
  }

//  public IntIterator getIndexIterator() {
//    return new IntIterator() {
//      int idx = -1;
//
//      public boolean hasNext() {
//        if (idx < STORED_OFFSET) {
//          if (idx + 1 < nStaticInts)
//            return true;
//          else if (nStoredInts.get() > 0)
//            return true;
//          else
//            return false;
//        } else if (idx + 1 < STORED_OFFSET + nStoredInts.get())
//          return true;
//        else
//          return false;
//      }
//
//      public int next() {
//        if (idx < STORED_OFFSET) {
//          if (idx + 1 < nStaticInts)
//            idx++;
//          else if (nStoredInts.get() > 0)
//            idx = STORED_OFFSET;
//          else
//            throw new java.util.NoSuchElementException();
//        } else if (idx + 1 < STORED_OFFSET + nStoredInts.get())
//          idx++;
//        else
//          throw new java.util.NoSuchElementException();
//        return idx;
//      }
//
//      public void remove() {
//        throw new UnsupportedOperationException();
//      }
//    };
//  }
//

    protected DisposableIntIterator _cachedIndexIterator = null;

    public DisposableIntIterator getIndexIterator() {
        IndexIterator iter = (IndexIterator) _cachedIndexIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIndexIterator = new IndexIterator(this);
        return _cachedIndexIterator;
    }


    protected static class IndexIterator extends DisposableIntIterator {
        PartiallyStoredIntVector vector;
        int idx = -1;
      boolean stats;
        boolean storeds;

        public IndexIterator(PartiallyStoredIntVector vector) {
            this.vector = vector;
            init();
        }

        public void init() {
            super.init();
            idx = -1;
            stats = (vector.nStaticInts> 0);
            storeds = (vector.nStoredInts.get() > 0);
        }

      public boolean hasNext() {
          if(idx == -1){
              return stats || storeds;
          }else{
              return ((stats && idx < vector.nStaticInts-1)
                      || (idx == vector.nStaticInts-1 && storeds)
                  ||( storeds && STORED_OFFSET <= idx && idx < STORED_OFFSET + vector.nStoredInts.get()-1));
          }
      }

      public int next() {
          idx++;
          if(idx==vector.nStaticInts){
              idx = STORED_OFFSET;
          }
          return idx;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
  }

  public static boolean isStaticIndex(int idx) {
    return idx < STORED_OFFSET;
  }

  public static int getSmallIndex(int idx) {
    if (idx < STORED_OFFSET)
      return idx;
    else
      return idx - STORED_OFFSET;
  }

  public static int getGlobalIndex(int idx, boolean isStatic) {
    if (isStatic)
      return idx;
    else
      return idx + STORED_OFFSET;
  }

}
