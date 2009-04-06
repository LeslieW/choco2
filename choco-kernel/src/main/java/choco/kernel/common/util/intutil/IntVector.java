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
package choco.kernel.common.util.intutil;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public class IntVector
        extends AbstractIntList
        implements IntList, RandomAccess, Cloneable, java.io.Serializable
{

    protected int[] elementData;

    protected int elementCount;
    protected int capacityIncrement;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -2767605614048989439L;
    public IntVector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        this.elementData = new int[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

    public IntVector(int initialCapacity) {
        this(initialCapacity, 0);
    }

    public IntVector() {
        this(10);
    }
    public IntVector(IntCollection c) {
        elementData = c.toArray();
        elementCount = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != int[].class)
            elementData = Arrays.copyOf(elementData, elementCount);
    }

    public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    public synchronized void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (elementCount < oldCapacity) {
            elementData = Arrays.copyOf(elementData, elementCount);
        }
    }

    public synchronized void ensureCapacity(int minCapacity) {
        modCount++;
        ensureCapacityHelper(minCapacity);
    }

    private void ensureCapacityHelper(int minCapacity) {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            int[] oldData = elementData;
            int newCapacity = (capacityIncrement > 0) ?
                    (oldCapacity + capacityIncrement) : (oldCapacity * 2);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    public synchronized void setSize(int newSize) {
        modCount++;
        if (newSize > elementCount) {
            ensureCapacityHelper(newSize);
        }
        elementCount = newSize;
    }

    public synchronized int capacity() {
        return elementData.length;
    }

    public synchronized int size() {
        return elementCount;
    }

    public synchronized boolean isEmpty() {
        return elementCount == 0;
    }

    public IntEnumeration elements() {
        return new IntEnumeration() {
            int count = 0;

            public boolean hasMoreElements() {
                return count < elementCount;
            }

            public int nextElement() {
                synchronized (IntVector.this) {
                    if (count < elementCount) {
                        return elementData[count++];
                    }
                }
                throw new NoSuchElementException("IntVector IntEnumeration");
            }
        };
    }

    public boolean contains(int o) {
        return indexOf(o, 0) >= 0;
    }

    public int indexOf(int o) {
        return indexOf(o, 0);
    }

    public synchronized int indexOf(int o, int index) {
        for (int i = index ; i < elementCount ; i++)
            if (o == (elementData[i]))
                return i;

        return -1;
    }


    public synchronized int lastIndexOf(int o) {
        return lastIndexOf(o, elementCount-1);
    }


    public synchronized int lastIndexOf(int o, int index) {
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);


        for (int i = index; i >= 0; i--)
            if (o == elementData[i])
                return i;

        return -1;
    }


    public synchronized int elementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        }

        return elementData[index];
    }


    public synchronized int firstElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return elementData[0];
    }


    public synchronized int lastElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return elementData[elementCount - 1];
    }


    public synchronized void setElementAt(int obj, int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                    elementCount);
        }
        elementData[index] = obj;
    }


    public synchronized void removeElementAt(int index) {
        modCount++;
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                    elementCount);
        }
        else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        elementCount--;
    }


    public synchronized void insertElementAt(int obj, int index) {
        modCount++;
        if (index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index
                    + " > " + elementCount);
        }
        ensureCapacityHelper(elementCount + 1);
        System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
        elementData[index] = obj;
        elementCount++;
    }

    public synchronized void addElement(int obj) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = obj;
    }


    public synchronized boolean removeElement(int obj) {
        modCount++;
        int i = indexOf(obj);
        if (i >= 0) {
            removeElementAt(i);
            return true;
        }
        return false;
    }


    public synchronized void removeAllElements() {
        modCount++;
        // Let gc do its work

        elementCount = 0;
    }


    public synchronized Object clone() {
        try {
            IntVector v = (IntVector) super.clone();
            v.elementData = Arrays.copyOf(elementData, elementCount);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }


    public synchronized int[] toArray() {
        return Arrays.copyOf(elementData, elementCount);
    }


    public synchronized int[] toArray(int[] a) {
        if (a.length < elementCount)
            return Arrays.copyOf(elementData, elementCount);

        System.arraycopy(elementData, 0, a, 0, elementCount);



        return a;
    }


    public synchronized int get(int index) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        return elementData[index];
    }


    public synchronized int set(int index, int element) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        int oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }


    public synchronized boolean add(int e) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = e;
        return true;
    }


    public boolean remove(int o) {
        return removeElement(o);
    }


    public void add(int index, int element) {
        insertElementAt(element, index);
    }


    public synchronized int removeAtPosition(int index) {
        modCount++;
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        int oldValue = elementData[index];

        int numMoved = elementCount - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);

        return oldValue;
    }


    public void clear() {
        removeAllElements();
    }


    public synchronized boolean containsAll(IntCollection c) {
        return super.containsAll(c);
    }

    public synchronized boolean addAll(IntCollection c) {
        modCount++;
        int[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);
        System.arraycopy(a, 0, elementData, elementCount, numNew);
        elementCount += numNew;
        return numNew != 0;
    }


    public synchronized boolean removeAll(IntCollection c) {
        return super.removeAll(c);
    }


    public synchronized boolean retainAll(IntCollection c)  {
        return super.retainAll(c);
    }


    public synchronized boolean addAll(int index, IntCollection c) {
        modCount++;
        if (index < 0 || index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        int[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);

        int numMoved = elementCount - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        elementCount += numNew;
        return numNew != 0;
    }

    public synchronized boolean equals(Object o) {
        return super.equals(o);
    }


    public synchronized int hashCode() {
        return super.hashCode();
    }


    public synchronized String toString() {
        return super.toString();
    }


    protected synchronized void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = elementCount - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                numMoved);

        // Let gc do its work
        int newElementCount = elementCount - (toIndex-fromIndex);

    }

    private synchronized void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException
    {
        s.defaultWriteObject();
    }
}
