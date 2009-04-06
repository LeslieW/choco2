package i_want_to_use_this_old_version_of_choco.mem;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 12 juil. 2007
 * Time: 10:18:42
 */
public interface IStateFloat {
    /**
   * Returns the current value.
     */

    double get();

    /**
   * Checks if a value is currently stored.
     */

    boolean isKnown();

    /**
   * Modifies the value and stores if needed the former value on the
     * trailing stack.
     */

    void set(double y);

    /**
   * modifying a StoredInt by an increment
     *
     * @param delta
     */
    void add(double delta);

    /**
   * Retrieving the environment
     */
    IEnvironment getEnvironment();
}
