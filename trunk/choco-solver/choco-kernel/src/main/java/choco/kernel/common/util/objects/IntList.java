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

import choco.kernel.common.util.iterators.DisposableIntIterator;


/**
 * A list over integers
 */
public class IntList {
	protected int[] content;
	protected int size;
	protected int currentIdx = 0;

    public IntList(int size) {
		this.size = size;
		this.content = new int[size];
	}

	public IntList(int[] content, int size) {
		this.content = content;
		this.size = size;
	}

	public IntList copy() {
		int[] copContent = new int[content.length];
		System.arraycopy(content, 0, copContent, 0, copContent.length);
		return new IntList(copContent, size);
	}

	public int getFirst() {
		if (size > 0) return content[0];
		else throw new IllegalArgumentException("List is empty");
	}

	public int getSize() {
		return size;
	}

	public void reInit() {
		size = 0;
	}

	public void add(int v) {
		if (size == content.length)
			throw new IllegalArgumentException("" + size);
		else
			content[size++] = v;
	}


	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('{');
		DisposableIntIterator iter = iterator();
		if(iter.hasNext()) {
			b.append(iter.next());
			while(iter.hasNext()) {
				b.append(',').append(iter.next());
			}
		}
        iter.dispose();
		b.append('}');
		return new String(b);
	}


    protected IntListIterator _cachedIterator = null;

    public DisposableIntIterator iterator() {
        IntListIterator iter = _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIterator = new IntListIterator(this);
        return _cachedIterator;
    }


	protected static class IntListIterator extends DisposableIntIterator {
		int currentIdx = 0;
		int maxSize;

        IntList list;

		public IntListIterator(IntList list) {
            this.list = list;
			init();
		}

        @Override
        public void init() {
            super.init();
            currentIdx = 0;
        }

        public boolean hasNext() {
			return (currentIdx < list.size);
		}

		public int next() {
			return list.content[currentIdx++];
		}

		public void remove() {
			if (currentIdx != list.size) {
				currentIdx--; // back to last returned
				list.content[currentIdx] = list.content[list.size - 1]; // reinsert the last one where we remove one
			}
			list.size--;
		}

		/**
		 * Read the next element wihtout incrementing
		 */
		public int read() {
			return list.content[currentIdx];
		}


	}
}