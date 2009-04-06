// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.mem.trailing;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateVector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a backtrackable search vector.
 * <p/>
 * Cette classe permet de stocker facilment des entiers dans un tableau
 * backtrackable d'entiers.
 */
public final class StoredVector implements IStateVector {
  /**
   * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem");

    /**
   * Contains the elements of the vector.
   */

  private Object[] elementData;

  /**
   * Contains time stamps for all entries (the world index of the last update for each entry)
   */

  int[] worldStamps;

  /**
   * A backtrackable search with the size of the vector.
   */

  private StoredInt size;


  /**
   * The current environment.
   */

  private final EnvironmentTrailing environment;


  /**
   * The history of all the backtrackable search vectors.
   */

  private final StoredVectorTrail trail;


  /**
   * Constructs a stored search vector with an initial size, and initial values.
   *
   * @param env The current environment.
   */

  public StoredVector(EnvironmentTrailing env) {
    int initialCapacity = MIN_CAPACITY;
    int w = env.getWorldIndex();

    this.environment = env;
    this.elementData = new Object[initialCapacity];
    this.worldStamps = new int[initialCapacity];

    this.size = new StoredInt(env, 0);

    this.trail = (StoredVectorTrail) this.environment.getTrail(IEnvironment.VECTOR_TRAIL);
  }


  public StoredVector(int[] entries) {
    // TODO
    throw new UnsupportedOperationException();
  }


  public int size() {
    return size.get();
  }


  public boolean isEmpty() {
    return (size.get() == 0);
  }

/*    public Object[] toArray() {
        // TODO : voir ci c'est utile
        return new Object[0];
    }*/


  public void ensureCapacity(int minCapacity) {
    int oldCapacity = elementData.length;
    if (minCapacity > oldCapacity) {
      Object[] oldData = elementData;
      int[] oldStamps = worldStamps;
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity)
        newCapacity = minCapacity;
      elementData = new Object[newCapacity];
      worldStamps = new int[newCapacity];
      System.arraycopy(oldData, 0, elementData, 0, size.get());
      System.arraycopy(oldStamps, 0, worldStamps, 0, size.get());
    }
  }


  public boolean add(java.lang.Object i) {
    int newsize = size.get() + 1;
    ensureCapacity(newsize);
    size.set(newsize);
    elementData[newsize - 1] = i;
    worldStamps[newsize - 1] = environment.getWorldIndex();
    return true;
  }

  public void removeLast() {
    int newsize = size.get() - 1;
    if (newsize >= 0)
      size.set(newsize);
  }

  public Object get(int index) {
    if (index < size.get() && index >= 0) {
      return elementData[index];
    }
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
  }


  public Object set(int index, Object val) {
    if (index < size.get() && index >= 0) {
      assert(this.worldStamps[index] <= environment.getWorldIndex());
      Object oldValue = elementData[index];
      if (val != oldValue) {
        int oldStamp = this.worldStamps[index];
        if (logger.isLoggable(Level.FINEST))
          logger.finest("W:" + environment.getWorldIndex() + "@" + index + "ts:" + this.worldStamps[index]);
        if (oldStamp < environment.getWorldIndex()) {
          trail.savePreviousState(this, index, oldValue, oldStamp);
          worldStamps[index] = environment.getWorldIndex();
        }
        elementData[index] = val;
      }
      return oldValue;
    }
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
  }


  /**
   * Sets an element without storing the previous value.
   */

  Object _set(int index, Object val, int stamp) {
    Object oldval = elementData[index];
    elementData[index] = val;
    worldStamps[index] = stamp;
    return oldval;
  }
}
