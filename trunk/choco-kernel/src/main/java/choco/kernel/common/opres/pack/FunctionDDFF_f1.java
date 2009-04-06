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

import java.awt.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.ListIterator;


/**
 * the function f_1 as defined in the phd thesis of F. Clautiaux.
 * @author Arnaud Malapert
 *
 */

public class FunctionDDFF_f1 extends AbstractDDFF {

	/**
	 * the list which represents the solution of the knapsack problem
	 */
	private final ArrayList<Point> mcXJ;

	public FunctionDDFF_f1(final int[] sizes, final int capacity, final BitSet selected) {
		super(sizes, capacity, selected);
		mcXJ=new ArrayList<Point>();
	}


	public FunctionDDFF_f1(final int[] sizes,final int capacity) {
		this(sizes, capacity,null);
	}


	@Override
	public void reset() {
		super.reset();
		if(mcXJ!=null) {this.mcXJ.clear();}
	}



	@Override
	public int applyFunction(final int k) {
		solveKnapsack(extractSubset(k),storedCapacity);
		return super.applyFunction(k);
	}

	@Override
	protected int applyFunction(final int i,final int k) {
		int v;
		if(storedSizes[i]>storedCapacity/2) {
			v=capacity-search(mcXJ, this.storedCapacity-storedSizes[i]);
		}else if(storedSizes[i]>=k) {v=1;}
		else {v=0;}
		return v;
	}

	@Override
	protected void setParameters(final BitSet params, final int i) {
		setParameter(params, storedSizes[i]+1);

	}

	@Override
	protected void updateCapacity(final int k) {
		this.capacity=mcXJ.get(mcXJ.size()-1).y;
	}

	/**
	 * add an item to the subsset used for the KP.
	 * @param subset subset
	 * @param i the index of the item
	 * @param k the parameter of the function
	 */
	private void addItem(final ArrayList<Integer> subset,final int i,final int k) {
		if(k<=storedSizes[i] && storedSizes[i]<=storedCapacity/2) {subset.add(storedSizes[i]);}
	}

	/**
	 * Extract a subset of object. Used in {@link choco.kernel.common.opres.pack.FunctionDDFF_f1#applyFunction(int)}
	 *
	 * @param k the parameter
	 *
	 * @return a subset of the items
	 */
	private ArrayList<Integer> extractSubset(final int k) {
		final ArrayList<Integer> subset=new ArrayList<Integer>();
		if(selected==null) {
			for (int i = 0; i < sizes.length; i++) {addItem(subset, i, k);}
		}else {
			for(int i=selected.nextSetBit(0);i>=0;i=selected.nextSetBit(i+1)) {addItem(subset, i, k);}
		}
		Collections.sort(subset);
		return subset;
	}

	/**
	 * Solve  knapsack problems where you must maximize the number of items in the bin. Linear if items are sorted.
	 *
	 * @param subset the subset used in the problem
	 *
	 */
	private final void solveKnapsack(final ArrayList<Integer> subset,final int capacity) {
		final ListIterator<Integer> iter=subset.listIterator();
		int sum=0;
		mcXJ.add(new Point());
		while (iter.hasNext()) {
			sum+=iter.next();
			if(sum>capacity) {break;}
			else {mcXJ.add(new Point(sum,iter.previousIndex()+1));}
		}
	}

	/**
	 * Dichotomic search for a solution for a knapsack of capacity value.
	 *
	 * @param list the list returned by {@link choco.kernel.common.opres.pack.FunctionDDFF_f1#solveKnapsack(java.util.ArrayList, int)}
	 * @param value the value the capacity
	 * @param bounds the bounds the bounds used in dichotomic search
	 *
	 * @return the the maximum number of items
	 */
	private static int  search(final ArrayList<Point> list,final int value,final Point bounds) {
		if(bounds.x==bounds.y-1) {
			return list.get(bounds.x).y;
		}else {
			final int mean=(bounds.x+bounds.y+1)/2;
			if(list.get(mean).x<=value) {
				bounds.x=mean;
			}else {
				bounds.y=mean;
			}
			return search(list, value,bounds);
		}
	}




	/**
	 * Dichotomic search for a solution for a knapsack of capacity value.
	 *
	 * @param list the list returned by {@link choco.kernel.common.opres.pack.FunctionDDFF_f1#solveKnapsack(java.util.ArrayList, int)}
	 * @param value the value the capacity
	 *
	 * @return the the maximum number of items
	 */
	public static int search(final ArrayList<Point> list,final int value) {
		return search(list, value, new Point(0,list.size()));
	}



}

