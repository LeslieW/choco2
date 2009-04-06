package i_want_to_use_this_old_version_of_choco.mem;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 12 juil. 2007
 * Time: 10:24:12
 */
public interface IStateLong {
    /**
   * Returns the current value.
     */

    long get();

    /**
   * Checks if a value is currently stored.
     */

    boolean isKnown();

    /**
   * Modifies the value and stores if needed the former value on the
     * trailing stack.
     */

    void set(long y);

    /**
   * modifying a StoredInt by an increment
     *
     * @param delta
     */
    void add(long delta);

    /**
   * Retrieving the environment
     */
    IEnvironment getEnvironment();
}
