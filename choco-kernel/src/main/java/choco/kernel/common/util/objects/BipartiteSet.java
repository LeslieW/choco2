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
package choco.kernel.common.util.objects;

import choco.kernel.common.logging.ChocoLogging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a bipartite set.
 * <p/>
 * Cette classe est utilisee pour stocker les evenements de propagation
 * de contrainte : les elements de gauche sont a propages, les autres
 * ne doivent pas etre propages.
 */
public class BipartiteSet<E> {
  /**
   * Reference to an object for logging trace statements related to util (using the java.util.logging package)
   */

  protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

  /**
   * Contains all the objects in the two parts of the set.
   */

  ArrayList<E> objects;


  /**
   * The number of elements in the left part of the set.
   */

  int nbLeft = 0;


  /**
   * Maps the element objects to the corresponding index.
   */

  HashMap<E,Integer> indices = new HashMap<E,Integer>();


  /**
   * Constructs a new bipartite set. Initialized internal util.
   */

  public BipartiteSet() {
    this.objects = new ArrayList<E>();
    this.indices = new HashMap<E,Integer>();
  }


  /**
   * Swaps two elements in the list containing all the objects of the set.
   */

  private void swap(int idx1, int idx2) {
    if (idx1 != idx2) {
      E obj1 = objects.get(idx1);
      E obj2 = objects.get(idx2);
      this.objects.set(idx1, obj2);
      this.objects.set(idx2, obj1);
      this.indices.put(obj1, idx2);
      this.indices.put(obj2, idx1);
    }
  }


  /**
   * Moves the object the left part of the set if needed.
   */

  public void moveLeft(E object) {
    Integer idx = this.indices.get(object);
    if (idx == null) {
      if (LOGGER.isLoggable(Level.SEVERE))
        LOGGER.logp(Level.SEVERE, "BipartiteSet", "moveLeft", "bipartite set does not contain " + object);
    } else {
      int index = idx.intValue();
      if (index >= this.nbLeft) {
        swap(index, this.nbLeft++);
      }
    }
  }


  /**
   * Moves the object the right part of the set if needed.
   */

  public void moveRight(E object) {
    Integer idx = this.indices.get(object);
    if (idx == null) {
      if (LOGGER.isLoggable(Level.SEVERE))
        LOGGER.logp(Level.SEVERE, "BipartiteSet", "moveRight", "bipartite set does not contain " + object);
    } else {
      int index = idx.intValue();
      if (index < this.nbLeft) {
        swap(index, --this.nbLeft);
      }
    }
  }


  /**
   * Moves all the objects to the left part.
   */

  public void moveAllLeft() {
    this.nbLeft = this.objects.size();
  }


  /**
   * Moves all the objects to the right part.
   */

  public void moveAllRight() {
    this.nbLeft = 0;
  }


  /**
   * Adds an object to the right part of the set.
   */

  public void addRight(E object) {
    if (this.indices.get(object) != null) {
      if (LOGGER.isLoggable(Level.SEVERE))
        LOGGER.logp(Level.SEVERE, "BipartiteSet", "addRight", object + "already in the set bipartite set ");
    } else {
      objects.add(object);
      indices.put(object, objects.size() - 1);
    }
  }


  /**
   * Adds an object to the left part of the set.
   */

  public void addLeft(E object) {
    this.addRight(object);
    this.moveLeft(object);
  }


  /**
   * Checks if the object is in the left part of the set.
   */

  public boolean isLeft(E object) {
    Integer idx = indices.get(object);
    if (idx == null) {
      if (LOGGER.isLoggable(Level.SEVERE))
        LOGGER.logp(Level.SEVERE, "BipartiteSet", "isLeft", "bipartite set does not contain " + object);
      return false;
    } else {
      int index = idx.intValue();
      return (index < this.nbLeft);
    }
  }


  /**
   * Checks if the object is in the set.
   */

  public boolean isIn(E object) {
    return (this.indices.get(object) != null);
  }


  /**
   * Returns the number of elements in the left part.
   */

  public int getNbLeft() {
    return this.nbLeft;
  }


  /**
   * Returns the number of elements in the right part.
   */

  public int getNbRight() {
    return (this.objects.size() - this.nbLeft);
  }


  /**
   * Returns the number of objects in the set.
   */

  public int getNbObjects() {
    return this.objects.size();
  }


  /**
   * Move the last element in the left part to the right part.
   *
   * @return The moved element.
   */

  public E moveLastLeft() {
    // Autant eviter d'appeler la fonction de hachage pour popper le
    // dernier evenement !
    if (this.nbLeft > 0) {
      E ret = this.objects.get(--this.nbLeft);
      return ret;
    } else
      return null;
  }


  /**
   * Iterator without a valid remove method !
   * Warning : suppose the set is not modified suring iterating !
   */

  public Iterator<E> leftIterator() {
    return new LeftItr();
  }

  private class LeftItr implements Iterator<E> {
    int cursor = 0;

    public boolean hasNext() {
      return cursor != nbLeft;
    }

    public E next() {
      return objects.get(cursor++);
    }

    public void remove() {
    }
  }


  /**
   * Iterator without a valid remove method !
   * Warning : suppose the set is not modified suring iterating !
   */

  public Iterator<E> rightIterator() {
    return new RightItr();
  }

  private class RightItr implements Iterator<E> {
    int cursor = nbLeft;

    public boolean hasNext() {
      return cursor != objects.size();
    }

    public E next() {
      return objects.get(cursor++);
    }

    public void remove() {
    }
  }
}
