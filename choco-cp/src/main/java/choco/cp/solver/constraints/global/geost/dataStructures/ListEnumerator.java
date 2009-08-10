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