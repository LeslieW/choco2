// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.util;

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
public class BipartiteSet {
  /**
   * Reference to an object for logging trace statements related to util (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("choco");

  /**
   * Contains all the objects in the two parts of the set.
   */

  ArrayList objects;


  /**
   * The number of elements in the left part of the set.
   */

  int nbLeft = 0;


  /**
   * Maps the element objects to the corresponding index.
   */

  HashMap indices = new HashMap();


  /**
   * Constructs a new bipartite set. Initialized internal util.
   */

  public BipartiteSet() {
    this.objects = new ArrayList();
    this.indices = new HashMap();
  }


  /**
   * Swaps two elements in the list containing all the objects of the set.
   */

  private void swap(int idx1, int idx2) {
    if (idx1 != idx2) {
      Object obj1 = objects.get(idx1);
      Object obj2 = objects.get(idx2);
      this.objects.set(idx1, obj2);
      this.objects.set(idx2, obj1);
      this.indices.put(obj1, new Integer(idx2));
      this.indices.put(obj2, new Integer(idx1));
    }
  }


  /**
   * Moves the object the left part of the set if needed.
   */

  public void moveLeft(Object object) {
    Object idx = this.indices.get(object);
    if (idx == null) {
      if (logger.isLoggable(Level.SEVERE))
        logger.logp(Level.SEVERE, "BipartiteSet", "moveLeft", "bipartite set does not contain " + object);
    } else {
      int index = ((Integer) idx).intValue();
      if (index >= this.nbLeft) {
        swap(index, this.nbLeft++);
      }
    }
  }


  /**
   * Moves the object the right part of the set if needed.
   */

  public void moveRight(Object object) {
    Object idx = this.indices.get(object);
    if (idx == null) {
      if (logger.isLoggable(Level.SEVERE))
        logger.logp(Level.SEVERE, "BipartiteSet", "moveRight", "bipartite set does not contain " + object);
    } else {
      int index = ((Integer) idx).intValue();
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

  public void addRight(Object object) {
    if (this.indices.get(object) != null) {
      if (logger.isLoggable(Level.SEVERE))
        logger.logp(Level.SEVERE, "BipartiteSet", "addRight", object + "already in the set bipartite set ");
    } else {
      objects.add(object);
      indices.put(object, new Integer(objects.size() - 1));
    }
  }


  /**
   * Adds an object to the left part of the set.
   */

  public void addLeft(Object object) {
    this.addRight(object);
    this.moveLeft(object);
  }


  /**
   * Checks if the object is in the left part of the set.
   */

  public boolean isLeft(Object object) {
    Object idx = indices.get(object);
    if (idx == null) {
      if (logger.isLoggable(Level.SEVERE))
        logger.logp(Level.SEVERE, "BipartiteSet", "isLeft", "bipartite set does not contain " + object);
      return false;
    } else {
      int index = ((Integer) idx).intValue();
      return (index < this.nbLeft);
    }
  }


  /**
   * Checks if the object is in the set.
   */

  public boolean isIn(Object object) {
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

  public Object moveLastLeft() {
    // Autant eviter d'appeler la fonction de hachage pour popper le
    // dernier evenement !
    if (this.nbLeft > 0) {
      Object ret = this.objects.get(--this.nbLeft);
      return ret;
    } else
      return null;
  }


  /**
   * Iterator without a valid remove method !
   * Warning : suppose the set is not modified suring iterating !
   */

  public Iterator leftIterator() {
    return new LeftItr();
  }

  private class LeftItr implements Iterator {
    int cursor = 0;

    public boolean hasNext() {
      return cursor != nbLeft;
    }

    public Object next() {
      return objects.get(cursor++);
    }

    public void remove() {
    }
  }


  /**
   * Iterator without a valid remove method !
   * Warning : suppose the set is not modified suring iterating !
   */

  public Iterator rightIterator() {
    return new RightItr();
  }

  private class RightItr implements Iterator {
    int cursor = nbLeft;

    public boolean hasNext() {
      return cursor != objects.size();
    }

    public Object next() {
      return objects.get(cursor++);
    }

    public void remove() {
    }
  }
}
