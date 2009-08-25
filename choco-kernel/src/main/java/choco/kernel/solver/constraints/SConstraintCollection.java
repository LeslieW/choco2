/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver.constraints;

import java.util.Collection;
import java.util.Iterator;

import choco.kernel.solver.propagation.Propagator;

/**
 * An interface for handling collections (sets/sequences) of constraints.
 * This is useful for representing explanations, states, paths in a search tree, and so on.
 */
public interface SConstraintCollection {
  /**
   * Merges an explain with the current one.
   *
   * @param collection The collection of constraints that must be added to this
   */
  void merge(SConstraintCollection collection);

  /**
   * Clones the collection as a new one.
   */
  SConstraintCollection copy();

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
  boolean containsAll(SConstraintCollection collec);
  
  /**
   * get an iterator over the collection of constraint.
   */
  Iterator<Propagator> iterator();
}
