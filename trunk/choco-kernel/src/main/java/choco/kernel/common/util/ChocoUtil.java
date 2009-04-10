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

import choco.IPretty;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerConstantVariable;

import gnu.trove.TIntArrayList;

import java.lang.reflect.Array;
import java.util.*;


/**
 * @author Arnaud Malapert
 *
 */
public final class ChocoUtil {

	private ChocoUtil() {}


	/**
	 * Pads out a string upto padlen with pad chars
	 * @param str string to be padded
	 * @param padlen length of pad (+ve = pad on right, -ve pad on left)
	 * @param pad character
	 */
	public static String pad(String str, int padlen, String pad) {
		String padding = new String();
		int len = Math.abs(padlen) - str.length();
		if (len < 1) {
			return str;
		}
		for (int i = 0; i < len; ++i) {
			padding = padding + pad;
		}
		return (padlen < 0 ? padding + str : str + padding);
	}

	//*****************************************************************//
	//*******************  Pretty  ********************************//
	//***************************************************************//
	public static String pretty(final IPretty[] elems, int begin, int end) {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("{ ");
		for (int i = begin; i < end; i++) {
			buffer.append(elems[i].pretty()).append(", ");	
		}
		buffer.deleteCharAt(buffer.length()-2);
		buffer.append("}");
		return new String(buffer);
	}

	public static String pretty(final IPretty... elems) {
		return pretty(elems, 0, elems.length);
	}

	public static String prettyOnePerLine(final Collection<? extends IPretty> elems) {
		return prettyOnePerLine(elems.iterator());
	}

	/**
	 * @param iter
	 */
	public static String prettyOnePerLine(Iterator<? extends IPretty> iter) {
		final StringBuilder buffer = new StringBuilder();
		while(iter.hasNext()) {
			buffer.append(iter.next().pretty()).append('\n');
		}
		return new String(buffer);
	}


	public static String pretty(final Collection<? extends IPretty> elems) {
		return pretty(elems.iterator());
	}

	public static String pretty(final Iterator<? extends IPretty> iter) {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("{ ");
		if(iter.hasNext()) {
			while(iter.hasNext()) {
				buffer.append(iter.next().pretty()).append(", ");
			}
			buffer.deleteCharAt(buffer.length()-2);
		}
		buffer.append("}");
		return new String(buffer);
	}



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
		Iterator<E>[] iters= (Iterator<E>[]) Array.newInstance(Iterator.class, lists.length);
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
		Iterator<E>[] iters= (Iterator<E>[]) Array.newInstance(Iterator.class, arrays.length);
		for (int i = 0; i < arrays.length; i++) {
			iters[i]=iterator(arrays[i]);
		}
		return append(iters);
	}

	//****************************************************************//
	//********* Array *******************************************//
	//****************************************************************//

	public static int[] zeroToN(int n) {
		final int[] r=new int[n];
		for (int i = 0; i < n; i++) {
			r[i]= i;
		}
		return r;
	}

	public static int[] oneToN(int n) {
		final int[] r=new int[n];
		for (int i = 1; i <= n; i++) {
			r[i-1]= i;
		}
		return r;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] getColumn(final T[][] array, final int column) {
		if(array != null && array.length> 0 
				&&  column>=0 && array[0].length > column) {
			T[] res = (T[]) Array.newInstance(array[0][column].getClass(), array.length);
			for (int i = 0; i < array.length; i++) {
				res[i] = array[i][column];
			}
			return res;
		}
		return null;
	}

	public static <T> int length(final T[]... arrays) {
		int length=0;
		for (T[] array : arrays) {
			length+=array.length;
		}
		return length;
	}


	public static <T> T get(int index, final T[]... arrays) {
		int shift=index;
		for (T[] tab : arrays) {
			if(shift<tab.length) {return tab[shift];}
			else {shift-=tab.length;}
		}
		return null;
	}

	public static <T> T get(int index, final List<T>... arrays) {
		int shift=index;
		for (List<T> tab : arrays) {
			if(shift<tab.size()) {return tab.get(shift);}
			else {shift-=tab.size();}
		}
		return null;
	}

	/**
	 * Check the type of each variable and compute a int value
	 * @param v1 type of the first variable
	 * @param v2 type of he second variable
	 * @return a value corresponding to the whole type
	 *
	 * if the type is integer return 1 * position
	 * if the type is set return 2 * position
	 * if the type is real return 3 * position
	 *
	 * where position is 10 for v1 and 1 for v2
	 */
	public static int checkType(VariableType v1, VariableType v2){
		int t1 = 0;
		int t2 = 0;
		if(checkInteger(v1)){
			t1 = 1;
		}else if(checkSet(v1)){
			t1 = 2;
		}else if(checkReal(v1)){
			t1 = 3;
		}
		if(checkInteger(v2)){
			t2 = 1;
		}else if(checkSet(v2)){
			t2 = 2;
		}else if(checkReal(v2)){
			t2 = 3;
		}
		return 10*t1+t2;

	}
	
	/**
	 * create a new array which contains sorted distinct values;
	 * @param values
	 * @return
	 */
	public static int[] getNonRedundantSortedValues(int[] values) {
		return createNonRedundantSortedValues(new TIntArrayList(values));
	}

	/**
	 * create an array which contains sorted distinct values. do not modify the original list
	 */
	public static int[] getNonRedundantSortedValues(TIntArrayList values) {
		return createNonRedundantSortedValues( (TIntArrayList) values.clone());
	}
	
	protected static int[] createNonRedundantSortedValues(TIntArrayList values) {
		values.sort();
		int offset = 1;
		while(offset < values.size()) {
			while(values.get(offset -1) == values.get(offset)) {
				values.remove(offset);
				if(offset == values.size()) {break;}
			}
			offset++;
		}
		return values.toNativeArray();
	}

	public static <V> V[] getNonRedundantObjects(Class classe, V[] all) {
		V[] a = all.clone();
		Arrays.sort(a);
		//Remove duplicated one
		V[] c = (V[]) Array.newInstance(classe, a.length);
		int ind = 0;
		V previous = null;
		for (int i = 0; i < a.length; i++) {
			if (!a[i].equals(previous)) {
				previous = a[i];
				c[ind++] = previous;
			}
		}
		if(ind != a.length){
			a = (V[]) Array.newInstance(classe, ind);
			System.arraycopy(c, 0, a, 0, ind);
		}
		c = null;
		return a;
	}

	//****************************************************************//
	//********* TYPE *******************************************//
	//****************************************************************//

	public static boolean checkInteger(VariableType v){
		return v == VariableType.INTEGER || v == VariableType.CONSTANT_INTEGER;
	}

	public static boolean checkSet(VariableType v){
		return v == VariableType.SET || v == VariableType.CONSTANT_SET;
	}

	public static boolean checkReal(VariableType v){
		return v == VariableType.REAL || v == VariableType.CONSTANT_DOUBLE || v == VariableType.REAL_EXPRESSION;
	}

	//****************************************************************//
	//********* Sorting and permutation ******************************//
	//****************************************************************//

	public static IPermutation getIdentity() {
		return Identity.SINGLETON;
	}

	public static IPermutation getSortingPermuation(int[] criteria) {
		return getSortingPermuation(criteria, false);
	}

	public static IPermutation getSortingPermuation(int[] criteria,boolean reverse) {
		return new IntPermutation(criteria,reverse);
	}

	public static IPermutation getSortingPermuation(IntegerConstantVariable[] criteria,boolean reverse) {
		return new ConstantPermutation(criteria,reverse);
	}

	public static IntegerConstantVariable[] applyPermutation(IPermutation permutation, IntegerConstantVariable[] source) {
		if(permutation.isIdentity()) {return source;}
		else {
			IntegerConstantVariable[] dest = new IntegerConstantVariable[source.length];
			permutation.applyPermutation(source,dest);
			return dest;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Comparable<T>> T[] sort(Set<T> set) {
		final LinkedList<T> tmpl = new LinkedList<T>(set);
		if(tmpl.isEmpty()) {return null;}
		else {
			T[] tmpa = (T[]) Array.newInstance(tmpl.getFirst().getClass(),tmpl.size());
			tmpl.toArray(tmpa);
			Arrays.sort(tmpa);
			return tmpa;
		}
	}


}


abstract class AbstractImmutableIterator<E> implements Iterator<E> {

	@Override
	public void remove() {
		throw new UnsupportedOperationException("iterates over a immutable list");

	}
}


class ImmutableListIterator<E> implements ListIterator<E> {

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


class SingleElementIterator<E> extends AbstractImmutableIterator<E> {

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

class ArrayIterator<E> extends AbstractImmutableIterator<E> {

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



class AppendIterator<E> extends AbstractImmutableIterator<E> {

	private final Iterator<Iterator<? extends E>> master;

	private Iterator<? extends E> slave;

	public AppendIterator(final Iterator<? extends E>... iterators) {
		super();
		master=new ArrayIterator<Iterator<? extends E>>(iterators);
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
