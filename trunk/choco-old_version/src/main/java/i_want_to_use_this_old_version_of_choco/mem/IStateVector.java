package i_want_to_use_this_old_version_of_choco.mem;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 12 juil. 2007
 * Time: 10:25:47
 */
public interface IStateVector {
    /**
   * Minimal capacity of a vector
     */
    int MIN_CAPACITY = 8;

    /**
   * Returns the current size of the stored search vector.
     */

    int size();

    /**
   * Checks if the vector is empty.
     */

    boolean isEmpty();

    /**
   * Checks if the capacity is great enough, else the capacity
     * is extended.
     *
     * @param minCapacity the necessary capacity.
     */

    void ensureCapacity(int minCapacity);

    /**
   * Adds a new search at the end of the vector.
     *
     * @param i The search to add.
     */

    boolean add(Object i);

    /**
   * removes the search at the end of the vector.
     * does nothing when called on an empty vector
     */

    void removeLast();

    /**
   * Returns the <code>index</code>th element of the vector.
     */

    Object get(int index);

    /**
   * Assigns a new value <code>val</code> to the element <code>index</code>.
     */

    Object set(int index, Object val);
}
