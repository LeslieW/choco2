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
package choco.kernel.common.util.tools;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import choco.kernel.common.util.iterators.AppendIterator;
import choco.kernel.common.util.iterators.ArrayIterator;
import choco.kernel.common.util.iterators.ImmutableListIterator;
import choco.kernel.common.util.iterators.SingleElementIterator;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 3 juil. 2009
 * Since : Choco 2.1.0
 * Update : Choco 2.1.0
 */
public class IteratorUtils {
	//****************************************************************//
	//********* Iterator *******************************************//
	//****************************************************************//
	public static <E> ListIterator<E> setImmutableIterator(final ListIterator<E> iter) {
		return new ImmutableListIterator<E>(iter);
	}

	public static <E> ListIterator<E> getImmutableIterator(final List<E> list) {
		return setImmutableIterator(list.listIterator());
	}

	public static <E> Iterator<E> iterator(final E elem) {
		return new SingleElementIterator<E>(elem);
	}

	public static <E> Iterator<E> iterator(final E[] array) {
		return new ArrayIterator<E>(array);
	}

	public static <E> Iterator<E> append(final Iterator<E>... iters) {
		return new AppendIterator<E>(iters);
	}

	@SuppressWarnings("unchecked")
	public static <E> Iterator<E> iterator(final List<E>... lists) {
		Iterator<E>[] iters= (Iterator<E>[]) java.lang.reflect.Array.newInstance(Iterator.class, lists.length);
		for (int i = 0; i < lists.length; i++) {
			iters[i]= getImmutableIterator(lists[i]);
		}
		return append(iters);
	}

	public static Iterator<Variable> variableIterator(final Iterator<? extends Variable>... iters) {
		return new AppendIterator<Variable>(iters);
	}

	public static <E> Iterator<E> appendAndCast(final Iterator<? extends E>... iters) {
		return new AppendIterator<E>(iters);
	}

	@SuppressWarnings("unchecked")
	public static <E> Iterator<E> append(final E[]... arrays) {
		Iterator<E>[] iters= (Iterator<E>[]) java.lang.reflect.Array.newInstance(Iterator.class, arrays.length);
		for (int i = 0; i < arrays.length; i++) {
			iters[i]= iterator(arrays[i]);
		}
		return append(iters);
	}

	public static Iterator<Constraint> iterator(final Model m, final Collection<Constraint> constraints) {
		return new Iterator<Constraint>(){
			Constraint c;
			final Iterator<Constraint> it = constraints.iterator();
			public boolean hasNext() {
				while(true){
					if(it == null){
						return false;
					}else
						if(it.hasNext()){
							c = it.next();
							if(Boolean.TRUE.equals(m.contains(c))){
								return true;
							}
						}else{
							return false;
						}
				}
			}

			@Override
			public Constraint next() {
				return c;
			}


			@Override
			public void remove() {
				it.remove();
			}
		};
	}
}
