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
package choco.kernel.common.opres.pack;

import java.util.BitSet;



/**
 * * This class implements lower bound for the 1D bin packing problem.
 * The LowerBound are DDFF proposed by F. Clautiaux in his phd thesis.
 * He proposed 3 functions f_0^k, f_1^k, f_2^k. f_0^k and f_2^k are maximal DDFF.
 * You can use composition of the functions but it is not yet implemented.
 * You can also compute the L^{DFF}_{1BP} lower bound for the 1BP with {@link LowerBoundFactory#computeL_DFF_1BP(int[], int)}.
 * the parameter k takes it values between 1<=k<=c/2.
 *
 * @author Arnaud Malapert
 */
public abstract class AbstractDDFF {

	/** The sizes. */
	protected final int[] sizes;

	/** backup sizes. */
	protected final int[] storedSizes;

	/** The capacity. */
	protected int capacity;


	/** The capacity. */
	protected final int storedCapacity;

	/** The selected. */
	protected BitSet selected;

	/** The stored selected. */
	private BitSet storedSelected;


	//
	/**
	 * The Constructor.
	 *
	 * @param sizes the sizes of the items
	 * @param capacity the capacity of the bin
	 */
	public AbstractDDFF(final int[] sizes, final int capacity) {
		this(sizes,capacity,null);
	}

	/**
	 * The Constructor.
	 *
	 * @param sizes the sizes of the items
	 * @param capacity the capacity of the bin
	 * @param selected a subset of selected items
	 */
	public AbstractDDFF(final int[] sizes, final int capacity,final BitSet selected) {
		super();
		this.storedSizes=sizes;
		this.storedCapacity=capacity;
		this.storedSelected=selected;
		this.sizes=new int[sizes.length];
		this.reset();
	}

	/**
	 * Reset.
	 */
	public void reset() {
		if(storedSelected==null) {selected=null;} //NOPMD
		else {selected=(BitSet) storedSelected.clone();}

	}

	/**
	 * Transform the size of an item according to a DDFF.
	 *
	 * @param i the index of the item
	 * @param k the parameter of the DDFF
	 *
	 * @return the new size
	 */
	private int transform(final int i,final int k) {
		sizes[i]=applyFunction(i, k);
		if(sizes[i]==0) {
			selected.set(i,false);
		}
		return sizes[i];
	}

	/**
	 * Apply the DDFF function to all selected items.
	 *
	 * @param k the parameter of the function
	 *
	 * @return the 1D Lower bound associated to this transformation
	 */
	public int applyFunction(final int k) {
		int sum=0;
		updateCapacity(k);
		if(selected==null) {
			selected=new BitSet(sizes.length);
			selected.set(0, sizes.length);
			for (int i = 0; i < sizes.length; i++) {
				sum+=transform(i, k);
			}
		}else {
			for(int i=selected.nextSetBit(0);i>=0;i=selected.nextSetBit(i+1)) {
				sum+=transform(i, k);
			}
		}
		return round(sum,capacity);
	}


	/**
	 * Update capacity according to the DDFF.
	 *
	 * @param k the parameter
	 */
	protected abstract void updateCapacity(int k);

	/**
	 * Apply the DDFF to an item.
	 *
	 * @param i the i-th item
	 * @param k the parameter of the function
	 *
	 * @return the transformed size of the item
	 */
	protected abstract int applyFunction(int i,int k);

	/**
	 * Checks if the parameter is is valid.
	 *
	 * @param k the parameter to test
	 * @param capacity the capacity
	 *
	 * @return true, if k is valid
	 */
	public final static boolean isValidParameter(final int k,final int capacity) {
		return k>=1 && k<=capacity/2;
	}

	/**
	 * Checks if the parameter is is valid.
	 *
	 * @param k the parameter to test
	 *
	 * @return true, if k is valid
	 */
	public final boolean isValidParameter(final int k) {
		return isValidParameter(k, storedCapacity);
	}


	/**
	 * add a paramter to a subset of selected parameters
	 *
	 * @param params the subset
	 * @param k the new parameter
	 *
	 * @return true, if successful
	 */
	protected boolean setParameter(final BitSet params,final int k) {
		boolean r=false;
		if(isValidParameter(k)) {
			params.set(k);
			r=true;
		}
		return r;
	}

	/**
	 * Sets the parameters depending of the i-th item.
	 *
	 * @param params the subset of parameters
	 * @param i the index of the item
	 */
	protected abstract void setParameters(BitSet params,int i);

	/**
	 * Select all valid and useful parameters for this DDFF.
	 *
	 * @return the set of parameters
	 */
	public BitSet selectParameters() {
		final BitSet params=new BitSet(capacity/2+1);
		params.set(1);
		if(selected==null) {
			for (int i = 0; i < sizes.length; i++) {
				setParameters(params,i);
			}
		}else {
			for(int i=selected.nextSetBit(0);i>=0;i=selected.nextSetBit(i+1)) {
				setParameters(params,i);
			}
		}
		return params;
	}


	/**
	 * Gets the sum of the item's sizes.
	 *
	 * @return the sum
	 */
	public long getSum() {
		int sum=0;
		if(selected==null) {
			for (int i = 0; i < sizes.length; i++) {
				sum+=sizes[i];
			}
		}else {
			for(int i=selected.nextSetBit(0);i>=0;i=selected.nextSetBit(i+1)) {
				sum+=sizes[i];
			}
		}
		return sum;
	}

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public final int getCapacity() {
		return capacity;
	}

	/**
	 * Gets the sizes.
	 *
	 * @return the sizes
	 */
	public final int[] getSizes() {
		return sizes;
	}


	/**
	 * Gets the selected items.
	 *
	 * @return the selected
	 */
	public final BitSet getSelected() {
		return selected;
	}



	/**
	 * Sets the stored selected.
	 *
	 * @param storedSelected the new stored selected
	 */
	public final void setStoredSelected(final BitSet storedSelected) {
		this.storedSelected = storedSelected;
		selected=(BitSet) storedSelected.clone();
	}

	/**
	 * Sets the selected.
	 *
	 * @param selected the new selected
	 */
	public final void setSelected(final BitSet selected) {
		this.selected = selected;
	}

	/**
	 * Compute the lower bound (continue) for the current sizes and capacity.
	 *
	 * @return the int
	 */
	public int computeL0() {
		return round(getSum(), capacity);
	}

	/**
	 * Rounding (ceil) method.
	 *
	 * @param d the quantity to divide
	 * @param div the divisor
	 *
	 * @return the rounding result
	 */
	protected static int round(final double d,final double div) {
		return (int) Math.ceil(d/div);
	}


	private int[] extract() {
		int[] s=new int[selected.cardinality()];
		int cpt=0;
		for (int i = selected.nextSetBit(0); i>=0 ;i=selected.nextSetBit(i+1)) {
			s[cpt]=storedSizes[i];
			cpt++;
		}
		return s;
	}
	/**
	 * compute an upper bound of the number of bins using 1BP heuristics.
	 *
	 * @return an upper bound for the 1BP
	 */
	public final int  computeUB() {
		int[] s;
		int mode;
		if(selected==null) {
			mode= AbstractHeurisic1BP.COPY_AND_SORT;
			s=storedSizes;
		}else {
			mode= AbstractHeurisic1BP.SORT;
			s=extract();
		}
		final AbstractHeurisic1BP h1= new BestFit1BP(s,storedCapacity,mode);
		final AbstractHeurisic1BP h2=new FirstFit1BP(h1.sizes,h1.capacity,AbstractHeurisic1BP.INCREASING);
		return Math.min(h1.computeUB(),h2.computeUB());
	}


	protected final static void write(final StringBuilder buffer,final String lab1,final int[] t,final String lab2,final int c) {
		buffer.append(lab2).append('=').append(c).append(' ');
		buffer.append(lab1).append("={");
		for (int i = 0; i < t.length; i++) {
			buffer.append(t[i]).append(',');
		}
		buffer.setCharAt(buffer.length()-1, '}');
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder buffer=new StringBuilder();
		write(buffer, "s",sizes,"c",capacity);
		return buffer.toString();
	}


}

