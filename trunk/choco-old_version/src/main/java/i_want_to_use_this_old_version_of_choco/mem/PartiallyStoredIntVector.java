// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * A class implementing a vector with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, integers with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And integers with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 */
public class PartiallyStoredIntVector {
  public static final int INITIAL_STATIC_CAPACITY = 8;
  public static final int INITIAL_STORED_CAPACITY = 8;
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
    int newSize = staticInts.length;
    while (n >= newSize) {
      newSize = (3 * newSize) / 2;
    }

    int[] newStaticObjects = new int[newSize];
    System.arraycopy(staticInts, 0, newStaticObjects, 0, staticInts.length);
    this.staticInts = newStaticObjects;
  }

  public int add(int o) {
    ensureStoredCapacity(nStoredInts.get() + 1);
    storedInts[nStoredInts.get()] = o;
    nStoredInts.add(1);
    return STORED_OFFSET + nStoredInts.get() - 1;
  }

  public void ensureStoredCapacity(int n) {
    int newSize = storedInts.length;
    while (n >= newSize) {
      newSize = (3 * newSize) / 2;
    }

    int[] newStoredObjects = new int[newSize];
    System.arraycopy(storedInts, 0, newStoredObjects, 0, storedInts.length);
    this.storedInts = newStoredObjects;
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

  public IntIterator getIndexIterator() {
    return new IntIterator() {
      int idx = -1;

      public boolean hasNext() {
        if (idx < STORED_OFFSET) {
          if (idx + 1 < nStaticInts)
            return true;
          else if (nStoredInts.get() > 0)
            return true;
          else
            return false;
        } else if (idx + 1 < STORED_OFFSET + nStoredInts.get())
          return true;
        else
          return false;
      }

      public int next() {
        if (idx < STORED_OFFSET) {
          if (idx + 1 < nStaticInts)
            idx++;
          else if (nStoredInts.get() > 0)
            idx = STORED_OFFSET;
          else
            throw new java.util.NoSuchElementException();
        } else if (idx + 1 < STORED_OFFSET + nStoredInts.get())
          idx++;
        else
          throw new java.util.NoSuchElementException();
        return idx;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
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
