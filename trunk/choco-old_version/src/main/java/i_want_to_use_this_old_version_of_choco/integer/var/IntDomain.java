// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.var;

import i_want_to_use_this_old_version_of_choco.Entity;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * An interface for all domains of search variables
 */
public interface IntDomain extends Entity {

  /**
   * Retrieve an getIterator for traversing the sequence of values contained in the domain
   */

  public DisposableIntIterator getIterator();


  /**
   * Access the minimal value stored in the domain.
   */

  public int getInf();


  /**
   * Access the maximal value stored in the domain/
   */

  public int getSup();


  /**
   * Augment the minimal value stored in the domain.
   * returns the new lower bound (<i>x</i> or more, in case <i>x</i> was
   * not in the domain)
   */

  public int updateInf(int x);


  /**
   * Diminish the maximal value stored in the domain.
   * returns the new upper bound (<i>x</i> or more, in case <i>x</i> was
   * not in the domain).
   */

  public int updateSup(int x);


  /**
   * Testing whether an search value is contained within the domain.
   */

  public boolean contains(int x);


  /**
   * Removing a single value from the domain.
   */

  public boolean remove(int x);


  /**
   * Restricting the domain to a singleton
   */

  public void restrict(int x);


  /**
   * Access the total number of values stored in the domain.
   */

  public int getSize();


  /**
   * Accessing the smallest value stored in the domain and strictly greater
   * than <i>x</i>.
   * Does not require <i>x</i> to be in the domain.
   */

  public int getNextValue(int x);


  /**
   * Accessing the largest value stored in the domain and strictly smaller
   * than <i>x</i>.
   * Does not require <i>x</i> to be in the domain.
   */

  public int getPrevValue(int x);


  /**
   * Testing whether there are values in the domain that are strictly greater
   * than <i>x</i>.
   * Does not require <i>x</i> to be in the domain.
   */

  public boolean hasNextValue(int x);


  /**
   * Testing whether there are values in the domain that are strictly smaller
   * than <i>x</i>.
   * Does not require <i>x</i> to be in the domain.
   */

  public boolean hasPrevValue(int x);


  /**
   * Draws a value at random from the domain.
   */

  public int getRandomValue();

  /**
   * Returns an getIterator over the set of values that have been removed from the domain since the last propagation
   */
  public IntIterator getDeltaIterator();

  /**
   * The delta domain container is "frozen" (it can no longer accept new value removals)
   * so that this set of values can be iterated as such
   */
  public void freezeDeltaDomain();

  /**
   * after an iteration over the delta domain, the delta domain is reopened again.
   *
   * @return true iff the delta domain is reopened empty (no updates have been made to the domain
   *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
   *         were made to the domain, while the delta domain was frozen).
   */
  public boolean releaseDeltaDomain();

  /**
   * checks whether the delta domain has indeed been released (ie: chechks that no domain updates are pending)
   */
  public boolean getReleasedDeltaDomain();

  /**
   * cleans the data structure implementing the delta domain
   */
  public void clearDeltaDomain();

  public boolean isEnumerated();

  /**
   * Is it a 0/1 domain ?
   */
  public boolean isBoolean();
}
