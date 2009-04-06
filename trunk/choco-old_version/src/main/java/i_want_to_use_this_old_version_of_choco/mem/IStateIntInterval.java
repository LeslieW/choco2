package i_want_to_use_this_old_version_of_choco.mem;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/
public interface IStateIntInterval {

    /**
     * Returns the current lower bound according to the current world
     * @return The current lower bound of the storable variable.
     */
    int getInf();

    /**
     * Modifies the inf value and stores if needed the former value on the
     * trailing stack.
     * @param y the new value of the inf variable.
     */
    void setInf(int y);

  /**
   * Modifying a StoredIntInterval inf by an increment.
   * @param delta the inf value to add to the current value.
   */
  void addInf(int delta);

    /**
     * Returns the current upper bound according to the current world
     * @return The current upper bound of the storable variable.
     */
    int getSup();

    /**
     * Modifies the sup value and stores if needed the former value on the
     * trailing stack.
     * @param y the new value of the sup variable.
     */
    void setSup(int y);

    /**
   * Modifying a StoredIntInterval sup by an increment.
   * @param delta the sup value to add to the current value.
   */
    void addSup(int delta);


    /**
     * Returns size of the interval
     * @return size of the interval
     */
    int getSize();

  /**
   * Retrieving the environment.
   * @return the environment associated to this variable (the object
   * responsible to manage worlds and storable variables).
   */
    IEnvironment getEnvironment();

    /**
     * Checks wether the stored interval contains x
     * @param x the value to check
     * @return wether x is contained
     */
    boolean contains(int x);

}
