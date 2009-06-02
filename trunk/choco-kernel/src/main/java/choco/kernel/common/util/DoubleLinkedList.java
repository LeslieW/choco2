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

import choco.kernel.common.logging.ChocoLogging;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A double linked list with constant time access, addition and deletion in o(1)
 * The list is encoded by two tables of integers. Efficient implementation regarding
 * time but very poor for memory.
 */
public class DoubleLinkedList implements IntIterator {

	protected final static Logger LOGGER = ChocoLogging.getKernelLogger();

	/**
	 *   Successors table
	 */
	protected int[] nextT;

	/**
	 * Predecessors table
	 */
	protected int[] prevT;


	/**
	 *  Current number of elements
	 */
	protected int size = 0;

	/**
	 * util for iteration
	 */
	protected int currentT = 0;

	/**
	 * maximum size of the list
	 */
	protected int listSize;


	/**
	 *  build a list of maximum size "listSize"
	 * @param listSize
	 */
	public DoubleLinkedList(int listSize) {
		this.listSize = listSize;
		this.nextT = new int[listSize + 1];
		this.prevT = new int[listSize + 1];
		reset();
	}

	/**
	 *  Constructor (copy)
	 */
	public DoubleLinkedList(DoubleLinkedList origin) {
		this.nextT = new int[origin.nextT.length];
		this.prevT = new int[origin.prevT.length];
		System.arraycopy(origin.nextT, 0, this.nextT, 0, this.nextT.length);
		System.arraycopy(origin.prevT, 0, this.prevT, 0, this.prevT.length);
		this.size = origin.size;
		this.listSize = origin.listSize;
	}

	/**
	 * Add an element "val"
	 */
	public void addVal(int val) {
		if (size == 0) {
			nextT[listSize] = val;
			prevT[listSize] = val;
		} else {
			prevT[nextT[listSize]] = val;
			nextT[val] = nextT[listSize];
			nextT[listSize] = val;
		}

		size = size + 1;
	}


	/**
	 * Remove an element "val"
	 */
	public void removeVal(int val) {
		if (size == 1) {
			nextT[listSize] = -1;
			prevT[listSize] = -1;
		} else if (nextT[listSize] == val) {
			nextT[listSize] = nextT[val];
			prevT[nextT[val]] = -1;
		} else if (prevT[listSize] == val) {
			prevT[listSize] = prevT[val];
			nextT[prevT[val]] = -1;
		} else {
			nextT[prevT[val]] = nextT[val];
			prevT[nextT[val]] = prevT[val];
		}
		nextT[val] = -1;
		prevT[val] = -1;
		size--;
	}

	/**
	 *    Get current number of element
	 */
	public int getSize() {
		return size;
	}

	/**
	 * reset
	 */
	public void reset() {
		size = 0;
		Arrays.fill(nextT, -1);
		Arrays.fill(prevT, -1);
		this.restart();
	}

	/**
	 * Initialize the iterator
	 */
	public void restart() {
		currentT = listSize;
	}

	/**
	 * Set the iterator from val
	 * @param val
	 */
	public void restartFrom(int val) {
		currentT = val;
	}

	public boolean hasNext() {
		return ((currentT != -1) && (nextT[currentT] != -1));
	}

	public boolean hasNextTo(int val) {
		return ((currentT != -1) && (nextT[currentT] != val));
	}

	/**
	 * return the next element
	 */
	public int next() {
		currentT = nextT[currentT];
		return currentT;
	}

	/**
	 * return the current iterated element
	 */
	public int read() {
		return currentT;
	}

	public int getFirst() {
		return nextT[listSize];
	}

	public int getLast() {
		return prevT[listSize];
	}

	public boolean isIn(int val) {
		return nextT[listSize] == val || prevT[val] != -1;
	}

	/**
	 * Restrict the domain to the element val
	 * @param val
	 */
	public void restrict(int val) {
		reset();
		addVal(val);
	}

	public int[] toTable() {
		int[] tab = new int[getSize()];
		int cpt = 0;
		restart();
		while(hasNext()) {
			tab[cpt] = next();
			cpt++;
		}
		return tab;
	}

	/**
	 * remove the current iterated element
	 */
	public void remove() {
		int pred;
		if (currentT != nextT[listSize]) {
			pred = prevT[currentT];
		} else pred = listSize;
		removeVal(currentT);
		currentT = pred;
	}


	// Display the table
	public void AfficheTab() {
		if(LOGGER.isLoggable(Level.INFO)) {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < nextT.length; i++) {
				b.append(nextT[i]).append(" | ");
				b.append(prevT[i]).append('\n');
			}
			LOGGER.log(Level.INFO, "{0}\n ---", b);
		}
	}



	// Display the table
	@Override
	public String toString() {
		String n = "next    :";
		String p = "suivant :";
		for (int i = 0; i < nextT.length; i++) {
			n += nextT[i] + " ";
			p += prevT[i] + " ";
		}

		return ("first :" + nextT[listSize] + "| " + "last :" + prevT[listSize] + "| " + n + "| " + p);
	}

}