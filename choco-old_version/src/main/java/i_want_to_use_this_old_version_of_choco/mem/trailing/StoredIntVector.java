// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem.trailing;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateIntVector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a backtrackable search vector.
 * <p/>
 * Cette classe permet de stocker facilment des entiers dans un tableau
 * backtrackable d'entiers.
 */
public final class StoredIntVector implements IStateIntVector {
  /**
   * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem");

  /**
   * Minimal capacity of a vector
   */
  public static final int MIN_CAPACITY = 8;

  /**
   * Contains the elements of the vector.
   */

  private int[] elementData;

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

  private final StoredIntVectorTrail trail;


  /**
   * Constructs a stored search vector with an initial size, and initial values.
   *
   * @param env          The current environment.
   * @param initialSize  The initial size.
   * @param initialValue The initial common value.
   */

  public StoredIntVector(EnvironmentTrailing env, int initialSize, int initialValue) {
    int initialCapacity = MIN_CAPACITY;
    int w = env.getWorldIndex();

    if (initialCapacity < initialSize)
      initialCapacity = initialSize;

    this.environment = env;
    this.elementData = new int[initialCapacity];
    this.worldStamps = new int[initialCapacity];
    for (int i = 0; i < initialSize; i++) {
      this.elementData[i] = initialValue;
      this.worldStamps[i] = w;
    }
    this.size = new StoredInt(env, initialSize);

    this.trail = (StoredIntVectorTrail) this.environment.getTrail(IEnvironment.INT_VECTOR_TRAIL);
  }


  public StoredIntVector(EnvironmentTrailing env, int[] entries) {
    int initialCapacity = MIN_CAPACITY;
    int w = env.getWorldIndex();
      int initialSize = entries.length;

    if (initialCapacity < initialSize)
      initialCapacity = initialSize;

    this.environment = env;
    this.elementData = new int[initialCapacity];
    this.worldStamps = new int[initialCapacity];
    for (int i = 0; i < initialSize; i++) {
      this.elementData[i] = entries[i]; // could be a System.arrayCopy but since the loop is needed...
      this.worldStamps[i] = w;
    }
    this.size = new StoredInt(env, initialSize);

    this.trail = (StoredIntVectorTrail) this.environment.getTrail(IEnvironment.INT_VECTOR_TRAIL);
  }

  /**
   * Constructs an empty stored search vector.
   *
   * @param env The current environment.
   */

  public StoredIntVector(EnvironmentTrailing env) {
    this(env, 0, 0);
  }


  /**
   * Returns the current size of the stored search vector.
   */

  public int size() {
    return size.get();
  }


  /**
   * Checks if the vector is empty.
   */

  public boolean isEmpty() {
    return (size.get() == 0);
  }

/*    public Object[] toArray() {
        // TODO : voir ci c'est utile
        return new Object[0];
    }*/


  /**
   * Checks if the capacity is great enough, else the capacity
   * is extended.
   *
   * @param minCapacity the necessary capacity.
   */

  public void ensureCapacity(int minCapacity) {
    int oldCapacity = elementData.length;
    if (minCapacity > oldCapacity) {
      int[] oldData = elementData;
      int[] oldStamps = worldStamps;
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity)
        newCapacity = minCapacity;
      elementData = new int[newCapacity];
      worldStamps = new int[newCapacity];
      System.arraycopy(oldData, 0, elementData, 0, size.get());
      System.arraycopy(oldStamps, 0, worldStamps, 0, size.get());
    }
  }


  /**
   * Adds a new search at the end of the vector.
   *
   * @param i The search to add.
   */

  public void add(int i) {
    int newsize = size.get() + 1;
    ensureCapacity(newsize);
    size.set(newsize);
    elementData[newsize - 1] = i;
    worldStamps[newsize - 1] = environment.getWorldIndex();
  }

  /**
   * removes the search at the end of the vector.
   * does nothing when called on an empty vector
   */

  public void removeLast() {
    int newsize = size.get() - 1;
    if (newsize >= 0)
      size.set(newsize);
  }

  /**
   * Returns the <code>index</code>th element of the vector.
   */

  public int get(int index) {
    if (index < size.get() && index >= 0) {
      return elementData[index];
    }
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
  }


  /**
   * Assigns a new value <code>val</code> to the element <code>index</code>.
   */

  public int set(int index, int val) {
    if (index < size.get() && index >= 0) {
      //<hca> je vire cet assert en cas de postCut il n est pas vrai ok ?  
      //assert(this.worldStamps[index] <= environment.getWorldIndex());
      int oldValue = elementData[index];
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

  int _set(int index, int val, int stamp) {
    int oldval = elementData[index];
    elementData[index] = val;
    worldStamps[index] = stamp;
    return oldval;
  }
}
