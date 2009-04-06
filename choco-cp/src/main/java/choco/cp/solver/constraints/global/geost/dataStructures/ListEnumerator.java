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
package choco.cp.solver.constraints.global.geost.dataStructures;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class ListEnumerator implements Enumeration {
  final LinkedList list;
  ListIterator cursor;

  ListEnumerator(choco.cp.solver.constraints.global.geost.dataStructures.LinkedList l) {
    list = l;
    cursor = list.head();
    cursor.next();
  }

  public boolean hasMoreElements() {
    return cursor.pos != list.head;
  }

  public Object nextElement() {
    synchronized (list) {
      if (cursor.pos != list.head) {
        Object object = cursor.pos.obj;
        cursor.next();
        return object;
      }
    }
    throw new NoSuchElementException("ListEnumerator");
  }
}