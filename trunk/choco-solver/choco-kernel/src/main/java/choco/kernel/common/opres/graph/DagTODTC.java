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
package choco.kernel.common.opres.graph;

import java.util.LinkedList;
import java.util.ListIterator;



/**
 *
 * @author Arnaud Malapert
 *<table>
<tr valign="top"><td align="left">
David&nbsp;J. Pearce and Paul H.&nbsp;J. Kelly.
</td></tr>
<tr valign="top"><td align="left">
<b> A dynamic topological sort algorithm for directed acyclic graphs.</b>
</td></tr>
<tr valign="top"><td align="left">
<em>ACM Journal of Experimental Algorithms</em>, 11, 2006.
</td></tr>
 */
public class DagTODTC extends DagDTC {

	/**
	 * @param n
	 */
	public DagTODTC(final int n) {
		super(n);
	}


	private void reorder(final ListIterator<Integer> nodes,final ListIterator<Integer> index) {
		while(index.hasNext()) {
			final int ind=index.next();
			final int n=nodes.next();
			order[ind]=n;
			orderIndex[n]=ind;
		}
	}


	@Override
	protected void fireTopologicalorder(int i, int j) {
		if(orderIndex[i]>orderIndex[j]) {
			final int lb=orderIndex[j];
			final int ub=orderIndex[i];
			final LinkedList<Integer> deltaF=new LinkedList<Integer>();
			final LinkedList<Integer> deltaB=new LinkedList<Integer>();
			final LinkedList<Integer> indexList=new LinkedList<Integer>();
			//Computing sets
			for (int k = lb; k <= ub; k++) {
				final int n=order[k];
				if(index[order[lb]][n]!=null) {
					deltaF.add(n);
					indexList.add(k);
				}else if(index[n][order[ub]]!=null) {
					deltaB.add(n);
					indexList.add(k);
				}
			}
			//reorder
			deltaB.addAll(deltaF);
			reorder(deltaB.listIterator(),indexList.listIterator());
		}
	}

}

