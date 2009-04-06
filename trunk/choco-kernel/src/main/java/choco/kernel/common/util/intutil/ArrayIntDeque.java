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
 * @(#)ArrayIntDeque.java	1.6 06/04/21
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
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
public class ArrayIntDeque extends AbstractIntCollection
        implements IntDeque, Cloneable, Serializable
{

    private transient int[] elements;

    private transient int head;


    private transient int tail;


    private static final int MIN_INITIAL_CAPACITY = 8;

    private void allocateElements(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>>  1);
            initialCapacity |= (initialCapacity >>>  2);
            initialCapacity |= (initialCapacity >>>  4);
            initialCapacity |= (initialCapacity >>>  8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0)   // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
        }
        elements = new int[initialCapacity];
        Arrays.fill(elements,Integer.MAX_VALUE);
    }

    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("Sorry, deque too big");
        int[] a = new int[newCapacity];
        Arrays.fill(a,Integer.MAX_VALUE);
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }


    private int[] copyElements(int[] a) {
        if (head < tail) {
            System.arraycopy(elements, head, a, 0, size());
        } else if (head > tail) {
            int headPortionLen = elements.length - head;
            System.arraycopy(elements, head, a, 0, headPortionLen);
            System.arraycopy(elements, 0, a, headPortionLen, tail);
        }
        return a;
    }


    public ArrayIntDeque() {
        elements = new int[16];
        Arrays.fill(elements,Integer.MAX_VALUE);
    }


    public ArrayIntDeque(int numElements) {
        allocateElements(numElements);
    }


    public ArrayIntDeque(IntCollection c) {
        allocateElements(c.size());
        addAll(c);
    }


    public void addFirst(int e) {
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail)
            doubleCapacity();
    }


    public void addLast(int e) {
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }

    public boolean offerFirst(int e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(int e) {
        addLast(e);
        return true;
    }


    public int removeFirst() {
        int x = pollFirst();
        if (x == Integer.MAX_VALUE)
            throw new NoSuchElementException();
        return x;
    }


    public int removeLast() {
        int x = pollLast();
        if (x == Integer.MAX_VALUE)
            throw new NoSuchElementException();
        return x;
    }

    public int pollFirst() {
        int h = head;
        int result = elements[h]; // Element is null if deque empty
        if (result == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        elements[h] = Integer.MAX_VALUE;     // Must null out slot
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    public int pollLast() {
        int t = (tail - 1) & (elements.length - 1);
        int result = elements[t];
        if (result == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        elements[t] = Integer.MAX_VALUE;
        tail = t;
        return result;
    }


    public int getFirst() {
        int x = elements[head];
        if (x == Integer.MAX_VALUE)
            throw new NoSuchElementException();
        return x;
    }


    public int getLast() {
        int x = elements[(tail - 1) & (elements.length - 1)];
        if (x == Integer.MAX_VALUE)
            throw new NoSuchElementException();
        return x;
    }

    public int peekFirst() {
        return elements[head]; // elements[head] is null if deque empty
    }

    public int peekLast() {
        return elements[(tail - 1) & (elements.length - 1)];
    }


    public boolean removeFirstOccurrence(int o) {
        if (o == Integer.MAX_VALUE)
            return false;
        int mask = elements.length - 1;
        int i = head;
        int x;
        while ( (x = elements[i]) != Integer.MAX_VALUE) {
            if (o == x) {
                delete(i);
                return true;
            }
            i = (i + 1) & mask;
        }
        return false;
    }


    public boolean removeLastOccurrence(int o) {
        if (o == Integer.MAX_VALUE)
            return false;
        int mask = elements.length - 1;
        int i = (tail - 1) & mask;
        int x;
        while ( (x = elements[i]) != Integer.MAX_VALUE) {
            if (o == (x)) {
                delete(i);
                return true;
            }
            i = (i - 1) & mask;
        }
        return false;
    }


    public boolean add(int e) {
        addLast(e);
        return true;
    }


    public boolean offer(int e) {
        return offerLast(e);
    }


    public int remove() {
        return removeFirst();
    }

    public int poll() {
        return pollFirst();
    }

    public int element() {
        return getFirst();
    }

    public int peek() {
        return peekFirst();
    }

    public void push(int e) {
        addFirst(e);
    }

    public int pop() {
        return removeFirst();
    }

    private void checkInvariants() {
        assert elements[tail] == Integer.MAX_VALUE;
        assert head == tail ? elements[head] == Integer.MAX_VALUE :
                (elements[head] != Integer.MAX_VALUE &&
                        elements[(tail - 1) & (elements.length - 1)] != Integer.MAX_VALUE);
        assert elements[(head - 1) & (elements.length - 1)] == Integer.MAX_VALUE;
    }


    private boolean delete(int i) {
        checkInvariants();
        final int[] elements = this.elements;
        final int mask = elements.length - 1;
        final int h = head;
        final int t = tail;
        final int front = (i - h) & mask;
        final int back  = (t - i) & mask;

        // Invariant: head <= i < tail mod circularity
        if (front >= ((t - h) & mask))
            throw new ConcurrentModificationException();

        // Optimize for least element motion
        if (front < back) {
            if (h <= i) {
                System.arraycopy(elements, h, elements, h + 1, front);
            } else { // Wrap around
                System.arraycopy(elements, 0, elements, 1, i);
                elements[0] = elements[mask];
                System.arraycopy(elements, h, elements, h + 1, mask - h);
            }
            elements[h] = Integer.MAX_VALUE;
            head = (h + 1) & mask;
            return false;
        } else {
            if (i < t) { // Copy the null tail as well
                System.arraycopy(elements, i + 1, elements, i, back);
                tail = t - 1;
            } else { // Wrap around
                System.arraycopy(elements, i + 1, elements, i, mask - i);
                elements[mask] = elements[0];
                System.arraycopy(elements, 1, elements, 0, t);
                tail = (t - 1) & mask;
            }
            return true;
        }
    }


    public int size() {
        return (tail - head) & (elements.length - 1);
    }


    public boolean isEmpty() {
        return head == tail;
    }

    public IntIterator iterator() {
        return new DeqIterator();
    }

    public IntIterator descendingIterator() {
        return new DescendingIterator();
    }

    private class DeqIterator implements IntIterator {

        private int cursor = head;

        private int fence = tail;

        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public int next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            int result = elements[cursor];
            // This check doesn't catch all possible comodifications,
            // but does catch the ones that corrupt traversal
            if (tail != fence || result == Integer.MAX_VALUE)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (delete(lastRet)) { // if left-shifted, undo increment in next()
                cursor = (cursor - 1) & (elements.length - 1);
                fence = tail;
            }
            lastRet = -1;
        }
    }

    private class DescendingIterator implements IntIterator {
        private int cursor = tail;
        private int fence = head;
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public int next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            cursor = (cursor - 1) & (elements.length - 1);
            int result = elements[cursor];
            if (head != fence || result == Integer.MAX_VALUE)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (!delete(lastRet)) {
                cursor = (cursor + 1) & (elements.length - 1);
                fence = head;
            }
            lastRet = -1;
        }
    }

    public boolean contains(int o) {
        if (o == Integer.MAX_VALUE)
            return false;
        int mask = elements.length - 1;
        int i = head;
        int x;
        while ( (x = elements[i]) != Integer.MAX_VALUE) {
            if (o == x)
                return true;
            i = (i + 1) & mask;
        }
        return false;
    }

    public boolean remove(int o) {
        return removeFirstOccurrence(o);
    }

    public void clear() {
        int h = head;
        int t = tail;
        if (h != t) { // clear all cells
            head = tail = 0;
            int i = h;
            int mask = elements.length - 1;
            do {
                elements[i] = Integer.MAX_VALUE;
                i = (i + 1) & mask;
            } while (i != t);
        }
    }

    public int[] toArray() {
        return copyElements(new int[size()]);
    }


    public int[] toArray(int[] a) {
        int size = size();
        if (a.length < size)
            a = new int[size];
        copyElements(a);
        if (a.length > size)
            a[size] = Integer.MAX_VALUE;
        return a;
    }

    public ArrayIntDeque clone() {
        try {
            ArrayIntDeque result = (ArrayIntDeque) super.clone();
            result.elements = Arrays.copyOf(elements, elements.length);
            return result;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    private static final long serialVersionUID = 2340985798034038923L;


    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size());

        // Write out elements in order.
        int mask = elements.length - 1;
        for (int i = head; i != tail; i = (i + 1) & mask)
            s.writeObject(elements[i]);
    }

    /**
     * Deserialize this deque.
     */
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        // Read in size and allocate array
        int size = s.readInt();
        allocateElements(size);
        head = 0;
        tail = size;

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            elements[i] = (Integer)s.readObject();
    }
}
