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

import choco.kernel.common.util.IntIterator;

import java.util.ConcurrentModificationException;
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
public abstract class AbstractIntList extends AbstractIntCollection implements IntList {

    protected AbstractIntList() {
    }

    public boolean add(int e) {
        add(size(), e);
        return true;
    }


    abstract public int get(int index);


    public int set(int index, int element) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, int element) {
        throw new UnsupportedOperationException();
    }


    public int removeAtPosition(int index) {
        throw new UnsupportedOperationException();
    }



    public int indexOf(int o) {
        IntListIterator e = listIterator();
        while (e.hasNext())
            if (o == e.next())
                return e.previousIndex();

        return -1;
    }


    public int lastIndexOf(int o) {
        IntListIterator e = listIterator(size());

        while (e.hasPrevious())
            if (o== e.previous())
                return e.nextIndex();

        return -1;
    }

    public void clear() {
        removeRange(0, size());
    }


    public boolean addAll(int index, IntCollection c) {
        boolean modified = false;
        IntIterator e = c.iterator();
        while (e.hasNext()) {
            add(index++, e.next());
            modified = true;
        }
        return modified;
    }



    public IntIterator iterator() {
        return new Itr();
    }

    public IntListIterator listIterator() {
        return listIterator(0);
    }


    public IntListIterator listIterator(final int index) {
        if (index<0 || index>size())
            throw new IndexOutOfBoundsException("Index: "+index);

        return new IntListItr(index);
    }

    private class Itr implements IntIterator {
        int cursor = 0;
        int lastRet = -1;

        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size();
        }

        public int next() {
            checkForComodification();
            try {
                int next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1)
                throw new IllegalStateException();
            checkForComodification();

            try {
                AbstractIntList.this.removeAtPosition(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class IntListItr extends Itr implements IntListIterator {
        IntListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                int previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor-1;
        }

        public void set(int e) {
            if (lastRet == -1)
                throw new IllegalStateException();
            checkForComodification();

            try {
                AbstractIntList.this.set(lastRet, e);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(int e) {
            checkForComodification();

            try {
                AbstractIntList.this.add(cursor++, e);
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public IntList subList(int fromIndex, int toIndex) {
        return (this instanceof RandomAccess ?
                new RandomAccessSubIntList(this, fromIndex, toIndex) :
                new SubIntList(this, fromIndex, toIndex));
    }


    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof IntList))
            return false;

        IntListIterator e1 = listIterator();
        IntListIterator e2 = ((IntList) o).listIterator();
        while(e1.hasNext() && e2.hasNext()) {
            int o1 = e1.next();
            int o2 = e2.next();
            if (!(o1 == o2))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }


    public int hashCode() {
        int hashCode = 1;
        IntIterator i = iterator();
        while (i.hasNext()) {
            int obj = i.next();
            hashCode = 31*hashCode + obj;
        }
        return hashCode;
    }


    protected void removeRange(int fromIndex, int toIndex) {
        IntListIterator it = listIterator(fromIndex);
        for (int i=0, n=toIndex-fromIndex; i<n; i++) {
            it.next();
            it.remove();
        }
    }


    protected transient int modCount = 0;
}

@Deprecated // see trove4j librairy
class SubIntList extends AbstractIntList {
    private AbstractIntList l;
    private int offset;
    private int size;
    private int expectedModCount;

    SubIntList(AbstractIntList list, int fromIndex, int toIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
        l = list;
        offset = fromIndex;
        size = toIndex - fromIndex;
        expectedModCount = l.modCount;
    }

    public int set(int index, int element) {
        rangeCheck(index);
        checkForComodification();
        return l.set(index+offset, element);
    }

    public int get(int index) {
        rangeCheck(index);
        checkForComodification();
        return l.get(index+offset);
    }

    public int size() {
        checkForComodification();
        return size;
    }

    public void add(int index, int element) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException();
        checkForComodification();
        l.add(index+offset, element);
        expectedModCount = l.modCount;
        size++;
        modCount++;
    }

    public int removeAtPosition(int index) {
        rangeCheck(index);
        checkForComodification();
        int result = l.removeAtPosition(index+offset);
        expectedModCount = l.modCount;
        size--;
        modCount++;
        return result;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        checkForComodification();
        l.removeRange(fromIndex+offset, toIndex+offset);
        expectedModCount = l.modCount;
        size -= (toIndex-fromIndex);
        modCount++;
    }

    public boolean addAll(IntCollection c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, IntCollection c) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                    "Index: "+index+", Size: "+size);
        int cSize = c.size();
        if (cSize==0)
            return false;

        checkForComodification();
        l.addAll(offset+index, c);
        expectedModCount = l.modCount;
        size += cSize;
        modCount++;
        return true;
    }

    public IntIterator iterator() {
        return listIterator();
    }

    public IntListIterator listIterator(final int index) {
        checkForComodification();
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                    "Index: "+index+", Size: "+size);

        return new IntListIterator() {
            private IntListIterator i = l.listIterator(index+offset);

            public boolean hasNext() {
                return nextIndex() < size;
            }

            public int next() {
                if (hasNext())
                    return i.next();
                else
                    throw new NoSuchElementException();
            }

            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            public int previous() {
                if (hasPrevious())
                    return i.previous();
                else
                    throw new NoSuchElementException();
            }

            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            public void remove() {
                i.remove();
                expectedModCount = l.modCount;
                size--;
                modCount++;
            }

            public void set(int e) {
                i.set(e);
            }

            public void add(int e) {
                i.add(e);
                expectedModCount = l.modCount;
                size++;
                modCount++;
            }
        };
    }

    public IntList subList(int fromIndex, int toIndex) {
        return new SubIntList(this, fromIndex, toIndex);
    }

    private void rangeCheck(int index) {
        if (index<0 || index>=size)
            throw new IndexOutOfBoundsException("Index: "+index+
                    ",Size: "+size);
    }

    private void checkForComodification() {
        if (l.modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }
}

class RandomAccessSubIntList extends SubIntList implements RandomAccess {
    RandomAccessSubIntList(AbstractIntList list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public IntList subList(int fromIndex, int toIndex) {
        return new RandomAccessSubIntList(this, fromIndex, toIndex);
    }
}
