package choco.kernel.memory;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: May 6, 2010
 * Time: 12:49:25 PM
 */
/**
 * Describes an search vector with states (describing some history of the data structure).
 */

public interface IStateLongVector
{

public final static Logger LOGGER = ChocoLogging.getEngineLogger();
  /**
   * Minimal capacity of a vector
   */
  public static final int MIN_CAPACITY = 8;

  /**
   * Returns the current size of the stored search vector.
   */

  public int size();

  /**
   * Checks if the vector is empty.
   */

  public boolean isEmpty();

  /**
   * Adds a new search at the end of the vector.
   *
   * @param i The search to add.
   */

  public void add(long i);

    public boolean contain(long val);

  /**
   * Removes an int.
   *
   * @param idx The search to remove.
   */

  public void remove(int idx);


  /**
   * removes the search at the end of the vector.
   * does nothing when called on an empty vector
   */

  public void removeLast();

  /**
   * Returns the <code>index</code>th element of the vector.
   */

  public long get(int index);

    /**
     * access an element without any bound check
     * @param index
     * @return
     */
    public long quickGet(int index);

  /**
   * Assigns a new value <code>val</code> to the element <code>index</code> and returns
   * the old value
   */

  public long set(int index, long val);

    /**
     * Assigns a new value val to the element indexth and return the old value without bound check
     * @param index the index where the value is modified
     * @param val the new value
     * @return the old value
     */
    public long quickSet(int index, long val);

  public DisposableIntIterator getIterator();

}
