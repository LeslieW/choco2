// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import java.util.Collection;

/**
 * An interface for handling collections (sets/sequences) of constraints.
 * This is useful for representing explanations, states, paths in a search tree, and so on.
 */
public interface ConstraintCollection {
  /**
   * Merges an explain with the current one.
   *
   * @param collection The collection of constraints that must be added to this
   */
  void merge(ConstraintCollection collection);

  /**
   * Clones the collection as a new one.
   */
  ConstraintCollection copy();

  /**
   * Adds a new constraint in the explain.
   *
   * @param constraint The constraint that should be added to the explain.
   *                   It must be a <code>PalmConstraint</code>.
   */

  void add(Propagator constraint);

  /**
   * Deletes a constraint from the explain.
   *
   * @param constraint The constraint that must be removed.
   */

  void delete(Propagator constraint);

  /**
   * Adds several constraints at a time
   *
   * @param collection The set of constraints
   */

  void addAll(Collection collection);

  /**
   * Checks if the explain is empty (that is wether the size of the set is null).
   */

  boolean isEmpty();

  /**
   * return the size of the bitSet
   */

  int size();

  /**
   * Deletes all indirect constraints.
   */

  void clear();

  /**
   * currentElement if a constraint is in the collection
   */
  boolean contains(Propagator ct);

  /**
   * currentElement inclusion
   */
  boolean containsAll(ConstraintCollection collec);
}
