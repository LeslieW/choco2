package i_want_to_use_this_old_version_of_choco.mem.trailing;
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

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateIntInterval;

public class StoredIntInterval implements IStateIntInterval{

    private EnvironmentTrailing environment;

    private int inf;

    private int sup;

    int worldStamp;

    private final StoredIntIntervalTrail trail;

  /**
   * Constructs a stored search with initial values.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   *
   * @param env environnement to associate with
   * @param inf the initial lower bound
   * @param sup the initial upper bound
   */

  public StoredIntInterval(EnvironmentTrailing env, int inf, int sup) {
    environment = env;
    this.inf = inf;
    this.sup = sup;
    worldStamp = env.getWorldIndex();
    trail = (StoredIntIntervalTrail) this.environment.getTrail(IEnvironment.INT_INTERVAL_TRAIL);
  }

    /**
     * Returns the actual lower bound
     * @return the actual lower bound
     */
    public int getInf() {
        return inf;
    }

    /**
     * Sets the lower bound to y
     * @param y the new lower bound
     */
    public void setInf(int y) {
    if (y != inf) {
      if (this.worldStamp < environment.getWorldIndex()) {
        trail.savePreviousState(this, inf, sup, worldStamp);
        worldStamp = environment.getWorldIndex();
      }
      inf = y;
    }
    }

    /**
     * Adds the value delta to the actual lower bound
     * @param delta the value to add to the lower bound
     */
    public void addInf(int delta) {
        setInf(getInf() + delta);
    }

    /**
     * Returns the actual upper bound
     * @return the actual upper bound
     */
    public int getSup() {
        return sup;
    }

    /**
     * Sets the upper bound to y
     * @param y the new upper bound
     */
    public void setSup(int y) {
    if (y != sup) {
      if (this.worldStamp < environment.getWorldIndex()) {
        trail.savePreviousState(this, inf, sup, worldStamp);
        worldStamp = environment.getWorldIndex();
      }
      sup = y;
    }
    }

    /**
     * Adds the value of delta to the actual upper bound
     * @param delta the value to add to the upper bound
     */
    public void addSup(int delta) {
        setSup(getSup() + delta);
    }

    /**
     * Returns the size of the interval
     * @return the size of the interval
     */
    public int getSize() {
        return sup-inf+1;
    }

    /**
     * Returns the environnement
     * @return the environnement
     */
    public IEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Sets to y the lower bound and record the index of the current world
     * @param y the new lower bound
     * @param wstamp the current world
     */
    public void _setInf(int y, int wstamp){
        inf = y;
        worldStamp = wstamp;
        
    }

    /**
     * Sets to y the upper bound and record the index of the current world
     * @param y the new upper bound
     * @param wstamp the current world
     */
    public void _setSup(int y, int wstamp){
        sup = y;
        worldStamp = wstamp;
    }

  /**
   *  pretty printing
   * @return pretty print
   */
  public String toString() {
      return String.valueOf("["+inf+","+sup+"]");
  }
    /**
     * Checks wether the stored interval contains x
     * @param x the value to check
     * @return wether x is contained
     */
    public boolean contains(int x){
        return x <= sup && x >= inf;
    }

}
