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

import java.util.Iterator;

public class AppendIterator<E> extends DisposableIterator<E> {

	private final Iterator<Iterator<? extends E>> master;

	private Iterator<? extends E> slave;

	@SuppressWarnings({"unchecked"})
    public AppendIterator(final Iterator<? extends E>... iterators) {
		super();
		master=ArrayIterator.getIterator(iterators);
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if(slave!=null && slave.hasNext()) {return true;}
		else {
			while(master.hasNext()) {
				slave=master.next();
				if(slave.hasNext()) {return true;}
			}
		}
		return false;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		if(slave==null ||
				( ! slave.hasNext() ) ) {
			while(master.hasNext()) {
				slave=master.next();
				if(slave.hasNext()) {break;}
			}
		}
		return slave.next();
	}
}
