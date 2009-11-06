/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.common.util.iterators;

public class ArrayIterator<E> extends AbstractImmutableIterator<E> {

	protected final E[] array;

	private int index;

	public ArrayIterator(final E[] array) {
		this(array,0);
	}
	public ArrayIterator(final E[] array,final int index) {
		super();
		this.array=array;
		this.index = index;
	}

	@Override
	public boolean hasNext() {
		return index<array.length;
	}
	@Override
	public E next() {
		return array[index++];
	}
}
