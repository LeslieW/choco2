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
package choco.kernel.common.opres.ssp;

import choco.kernel.solver.SolverException;

import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.ListIterator;




/**
 * @author Arnaud Malapert
 *
 */
public class BellmanWithLists extends AbstractSubsetSumSolver {



	private int[] setX;

	private final LinkedList<Integer> reachables=new LinkedList<Integer>();

	public BellmanWithLists(int[] sizes, int capacity) {
		super(sizes, capacity);
	}

	@Override
	public void reset() {
		super.reset();
		reachables.clear();
		Arrays.fill(setX, NONE);
	}


	@Override
	public void setCapacity(Long capacity) {
		if(!capacity.equals(this.capacity)) {
			setX=new int[Long.valueOf(capacity).intValue()+1];
			Arrays.fill(setX, NONE);
		}
		super.setCapacity(capacity);
	}

	@Override
	public String getName() {
		return "Bellman with lists";
	}

	@Override
	public long run() {
		reachables.add(0);
		for (int item = 0; item < sizes.length; item++) {
			handleItem(item);
			if(setX[capacity.intValue()]!=NONE) {
				break;
			}
		}
		objective=reachables.getLast();
		return objective;
	}


	@Override
	public BitSet getSolution() {
		int cpt= Long.valueOf(objective).intValue();
		BitSet solution=new BitSet(sizes.length);
		while(cpt>0) {
			solution.set(cpt);
			cpt-=sizes[setX[cpt]];
		}
		if(cpt!=0) {throw new SolverException("internal error of "+getName());}
		return solution;
	}




	public final BitSet getCoveredSet() {
		BitSet res=new BitSet(capacity.intValue()+1);
		for (Integer r : reachables) {
			res.set(r);
		}
		return res;
	}

	public void handleItem(final int item) {
		LinkedList<Integer> tmp=new LinkedList<Integer>();
		ListIterator<Integer> old=reachables.listIterator();
		while(old.hasNext()) {
			final Integer current=old.next();
			//adding new reachables
			old.previous();
			while( !tmp.isEmpty() && tmp.getFirst()<current) {
				old.add(tmp.removeFirst());
			}
			old.next();
			//complete list
			final Integer value=current+sizes[item];
			if(value<=capacity && setX[value]==NONE) {
				tmp.add(value);
				setX[value]=item;
			}
		}
		reachables.addAll(tmp);
	}


}
