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
/*
 * @(#)LinkedIntList.java	1.67 06/04/21
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public class LinkedIntList
        extends AbstractSequentialIntList
        implements IntList, IntDeque, Cloneable, java.io.Serializable
{
    private transient Entry header = new Entry(Integer.MAX_VALUE, null, null);
    private transient int size = 0;


    public LinkedIntList() {
        header.next = header.previous = header;
    }

    public LinkedIntList(IntCollection c) {
        this();
        addAll(c);
    }


    public int getFirst() {
        if (size==0)
            throw new NoSuchElementException();

        return header.next.element;
    }


    public int getLast()  {
        if (size==0)
            throw new NoSuchElementException();

        return header.previous.element;
    }


    public int removeFirst() {
        return remove(header.next);
    }


    public int removeLast() {
        return remove(header.previous);
    }

    public void addFirst(int e) {
        addBefore(e, header.next);
    }


    public void addLast(int e) {
        addBefore(e, header);
    }

    public boolean contains(int o) {
        return indexOf(o) != -1;
    }

    public int size() {
        return size;
    }

    public boolean add(int e) {
        addBefore(e, header);
        return true;
    }

    public boolean remove(int o) {

        for (Entry e = header.next; e != header; e = e.next) {
            if (o == e.element) {
                remove(e);
                return true;
            }
        }

        return false;
    }

    public boolean addAll(IntCollection c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, IntCollection c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: "+index+
                    ", Size: "+size);
        int[] a = c.toArray();
        int numNew = a.length;
        if (numNew==0)
            return false;
        modCount++;

        Entry successor = (index==size ? header : entry(index));
        Entry predecessor = successor.previous;
        for (int i=0; i<numNew; i++) {
            Entry e = new Entry(a[i], successor, predecessor);
            predecessor.next = e;
            predecessor = e;
        }
        successor.previous = predecessor;

        size += numNew;
        return true;
    }

    public void clear() {
        Entry e = header.next;
        while (e != header) {
            Entry next = e.next;
            e.next = e.previous = null;
            e.element = Integer.MAX_VALUE;
            e = next;
        }
        header.next = header.previous = header;
        size = 0;
        modCount++;
    }


    public int get(int index) {
        return entry(index).element;
    }

    public int set(int index, int element) {
        Entry e = entry(index);
        int oldVal = e.element;
        e.element = element;
        return oldVal;
    }


    public void add(int index, int element) {
        addBefore(element, (index==size ? header : entry(index)));
    }


    public int removeAtPosition(int index) {
        return remove(entry(index));
    }


    private Entry entry(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+
                    ", Size: "+size);
        Entry e = header;
        if (index < (size >> 1)) {
            for (int i = 0; i <= index; i++)
                e = e.next;
        } else {
            for (int i = size; i > index; i--)
                e = e.previous;
        }
        return e;
    }


    public int indexOf(int o) {
        int index = 0;

        for (Entry e = header.next; e != header; e = e.next) {
            if (o == (e.element))
                return index;
            index++;

        }
        return -1;
    }


    public int lastIndexOf(int o) {
        int index = size;

        for (Entry e = header.previous; e != header; e = e.previous) {
            index--;
            if (o == (e.element))
                return index;
        }

        return -1;
    }


    public int peek() {
        if (size==0)
            return Integer.MAX_VALUE;
        return getFirst();
    }


    public int element() {
        return getFirst();
    }


    public int poll() {
        if (size==0)
            return Integer.MAX_VALUE;
        return removeFirst();
    }

    public int remove() {
        return removeFirst();
    }


    public boolean offer(int e) {
        return add(e);
    }


    public boolean offerFirst(int e) {
        addFirst(e);
        return true;
    }


    public boolean offerLast(int e) {
        addLast(e);
        return true;
    }


    public int peekFirst() {
        if (size==0)
            return Integer.MAX_VALUE;
        return getFirst();
    }


    public int peekLast() {
        if (size==0)
            return Integer.MAX_VALUE;
        return getLast();
    }

    public int pollFirst() {
        if (size==0)
            return Integer.MAX_VALUE;
        return removeFirst();
    }


    public int pollLast() {
        if (size==0)
            return Integer.MAX_VALUE;
        return removeLast();
    }

    public void push(int e) {
        addFirst(e);
    }


    public int pop() {
        return removeFirst();
    }


    public boolean removeFirstOccurrence(int o) {
        return remove(o);
    }

    public boolean removeLastOccurrence(int o) {

        for (Entry e = header.previous; e != header; e = e.previous) {
            if (o == (e.element)) {
                remove(e);
                return true;
            }
        }

        return false;
    }

    public IntListIterator listIterator(int index) {
        return new IntListItr(index);
    }

    private class IntListItr implements IntListIterator {
        private Entry lastReturned = header;
        private Entry next;
        private int nextIndex;
        private int expectedModCount = modCount;

        IntListItr(int index) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: "+index+
                        ", Size: "+size);
            if (index < (size >> 1)) {
                next = header.next;
                for (nextIndex=0; nextIndex<index; nextIndex++)
                    next = next.next;
            } else {
                next = header;
                for (nextIndex=size; nextIndex>index; nextIndex--)
                    next = next.previous;
            }
        }

        public boolean hasNext() {
            return nextIndex != size;
        }

        public int next() {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }

        public boolean hasPrevious() {
            return nextIndex != 0;
        }

        public int previous() {
            if (nextIndex == 0)
                throw new NoSuchElementException();

            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex-1;
        }

        public void remove() {
            checkForComodification();
            Entry lastNext = lastReturned.next;
            try {
                LinkedIntList.this.remove(lastReturned);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException();
            }
            if (next==lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        public void set(int e) {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.element = e;
        }

        public void add(int e) {
            checkForComodification();
            lastReturned = header;
            addBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Entry {
        int element;
        Entry next;
        Entry previous;

        Entry(int element, Entry next, Entry previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

    private Entry addBefore(int e, Entry entry) {
        Entry newEntry = new Entry(e, entry, entry.previous);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        size++;
        modCount++;
        return newEntry;
    }

    private int remove(Entry e) {
        if (e == header)
            throw new NoSuchElementException();

        int result = e.element;
        e.previous.next = e.next;
        e.next.previous = e.previous;
        e.next = e.previous = null;
        e.element = Integer.MAX_VALUE;
        size--;
        modCount++;
        return result;
    }


    public IntIterator descendingIterator() {
        return new DescendingIterator();
    }

    /** Adapter to provide descending iterators via ListItr.previous */
    private class DescendingIterator implements IntIterator {
        final IntListItr itr = new IntListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public int next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }

    public Object clone() throws CloneNotSupportedException {
        LinkedIntList clone;//null;
        try {
            clone = (LinkedIntList) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }

        // Put clone into "virgin" state
        clone.header = new Entry(Integer.MAX_VALUE, null, null);
        clone.header.next = clone.header.previous = clone.header;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Entry e = header.next; e != header; e = e.next)
            clone.add(e.element);

        return clone;
    }


    public int[] toArray() {
        int[] result = new int[size];
        int i = 0;
        for (Entry e = header.next; e != header; e = e.next)
            result[i++] = e.element;
        return result;
    }


    public  int[] toArray(int[] a) {
        if (a.length < size)
            a = new int[size];
        int i = 0;
        int[] result = a;
        for (Entry e = header.next; e != header; e = e.next)
            result[i++] = e.element;

        if (a.length > size)
            a[size] = Integer.MAX_VALUE;

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;


    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Entry e = header.next; e != header; e = e.next)
            s.writeObject(e.element);
    }


    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Initialize header
        header = new Entry(Integer.MAX_VALUE, null, null);
        header.next = header.previous = header;

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++)
            addBefore((Integer)s.readObject(), header);
    }
}
