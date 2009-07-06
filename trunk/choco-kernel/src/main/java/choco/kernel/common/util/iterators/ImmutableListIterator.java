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

import java.util.ListIterator;

public class ImmutableListIterator<E> implements ListIterator<E> {

	private final static String MSG="can not modify the list";

	private final ListIterator<E> iter;

	/**
	 *
	 */
	public ImmutableListIterator(final ListIterator<E> iter) {
		super();
		this.iter=iter;
	}

	@Override
	public void add(final E e) {
		throw new UnsupportedOperationException(MSG);

	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return iter.hasPrevious();
	}

	@Override
	public E next() {
		return iter.next();
	}

	@Override
	public int nextIndex() {
		return iter.nextIndex();
	}

	@Override
	public E previous() {
		return iter.previous();
	}

	@Override
	public int previousIndex() {
		return iter.previousIndex();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(MSG);
	}
	@Override
	public void set(final E e) {
		throw new UnsupportedOperationException(MSG);

	}

}
