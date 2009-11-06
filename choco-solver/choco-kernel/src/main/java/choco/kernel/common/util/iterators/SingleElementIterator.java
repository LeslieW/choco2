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
package choco.kernel.common.util.iterators;

import java.util.NoSuchElementException;


public class SingleElementIterator<E> extends AbstractImmutableIterator<E> {

	protected final E elem;

	protected boolean hnext=true;

	public SingleElementIterator(final E elem) {
		super();
		this.elem = elem;
	}

    @Override
	public boolean hasNext() {
		return hnext;
	}

	@Override
	public E next() {
		if(hasNext()) {
			hnext=false;
			return elem;
		} else {throw new NoSuchElementException("single object iterator");}
	}


}



