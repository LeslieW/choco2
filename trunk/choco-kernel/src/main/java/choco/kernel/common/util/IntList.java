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
package choco.kernel.common.util;


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

	public IntIterator iterator() {
		return new IntListIterator();
	}



	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('{');
		IntIterator iter = iterator(); 
		if(iter.hasNext()) {
			b.append(iter.next());
			while(iter.hasNext()) {
				b.append(',').append(iter.next());
			}
		}
		b.append('}');
		return new String(b);
	}



	private class IntListIterator implements choco.kernel.common.util.IntIterator {
		int currentIdx = 0;
		int maxSize;

		public IntListIterator() {
			currentIdx = 0;
		}

		public boolean hasNext() {
			return (currentIdx < size);
		}

		public int next() {
			return content[currentIdx++];
		}

		public void remove() {
			if (currentIdx != size) {
				currentIdx--; // back to last returned
				content[currentIdx] = content[size - 1]; // reinsert the last one where we remove one
			}
			size--;
		}

		/**
		 * Read the next element wihtout incrementing
		 */
		public int read() {
			return content[currentIdx];
		}


	}
}