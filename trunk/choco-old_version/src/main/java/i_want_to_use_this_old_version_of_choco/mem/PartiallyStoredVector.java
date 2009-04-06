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
 * By convention, objects with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And objects with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 */
public class PartiallyStoredVector {
  public static final int INITIAL_STATIC_CAPACITY = 8;
  public static final int INITIAL_STORED_CAPACITY = 8;
  public static final int STORED_OFFSET = 1000000;

  protected Object[] staticObjects;
  protected Object[] storedObjects;

  protected int nStaticObjects;
  protected IStateInt nStoredObjects;

  public PartiallyStoredVector(IEnvironment env) {
    staticObjects = new Object[INITIAL_STATIC_CAPACITY];
    storedObjects = new Object[INITIAL_STORED_CAPACITY];
    nStaticObjects = 0;
    nStoredObjects = env.makeInt(0);
  }

  public boolean contains(Object o) {
      for (int i = 0; i < nStaticObjects; i++) {
          if(staticObjects[i].equals(o))
              return true;
      }
      for (int i = 0; i < nStoredObjects.get(); i++) {
          if(storedObjects[i].equals(o))
              return true;
      }
      return false;
  }

  public int staticAdd(Object o) {
    ensureStaticCapacity(nStaticObjects + 1);
    staticObjects[nStaticObjects++] = o;
    return nStaticObjects - 1;
  }

  // TODO: maintain size ?
  protected void staticRemove(int idx) {
    staticObjects[idx] = null;
    if (idx == nStaticObjects - 1)
      nStaticObjects--;
  }

  public void remove(Object o) {
    for (int i = 0; i < staticObjects.length; i++) {
      Object staticObject = staticObjects[i];
      if (staticObject == o) {
        staticRemove(i);
        return;
      }
    }
    throw new Error("impossible to remove the object (a constraint ?) from the static part of the collection (cut manager ?)");
  }

  public void ensureStaticCapacity(int n) {
    if (n >= staticObjects.length) {
      int newSize = staticObjects.length;
      while (n >= newSize) {
        newSize = (3 * newSize) / 2;
      }
      Object[] newStaticObjects = new Object[newSize];
      System.arraycopy(staticObjects, 0, newStaticObjects, 0, staticObjects.length);
      this.staticObjects = newStaticObjects;
    }
  }

  public int add(Object o) {
    ensureStoredCapacity(nStoredObjects.get() + 1);
    storedObjects[nStoredObjects.get()] = o;
    nStoredObjects.add(1);
    return STORED_OFFSET + nStoredObjects.get() - 1;
  }

  public void ensureStoredCapacity(int n) {
    if (n >= storedObjects.length) {
      int newSize = storedObjects.length;
      while (n >= newSize) {
        newSize = (3 * newSize) / 2;
      }
      Object[] newStoredObjects = new Object[newSize];
      System.arraycopy(storedObjects, 0, newStoredObjects, 0, storedObjects.length);
      this.storedObjects = newStoredObjects;
    }
  }

  public Object get(int index) {
    if (index < STORED_OFFSET) {
      return staticObjects[index];
    } else {
      return storedObjects[index - STORED_OFFSET];
    }
  }

  public boolean isEmpty() {
    return ((nStaticObjects == 0) && (nStoredObjects.get() == 0));
  }

  public int size() {
    return (nStaticObjects + nStoredObjects.get());
  }

  public IntIterator getIndexIterator() {
    return new IntIterator() {
      int idx = -1;

      public boolean hasNext() {
        if (idx < STORED_OFFSET) {
          if (idx + 1 < nStaticObjects)
            return true;
          else if (nStoredObjects.get() > 0)
            return true;
          else
            return false;
        } else if (idx + 1 < STORED_OFFSET + nStoredObjects.get())
          return true;
        else
          return false;
      }

      public int next() {
        if (idx < STORED_OFFSET) {
          if (idx + 1 < nStaticObjects) {
			  idx++;
              while(staticObjects[idx] == null && idx < nStaticObjects)
				  idx++;
		  } else if (nStoredObjects.get() > 0)
            idx = STORED_OFFSET;
          else
            throw new java.util.NoSuchElementException();
        } else if (idx + 1 < STORED_OFFSET + nStoredObjects.get())
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

  public int getLastStaticIndex() {
    return nStaticObjects - 1;
  }

  public static int getFirstStaticIndex() {
    return 0;
  }
}
