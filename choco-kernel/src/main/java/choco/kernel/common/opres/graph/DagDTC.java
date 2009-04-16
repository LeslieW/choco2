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

import choco.kernel.common.util.ChocoUtil;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntProcedure;




/**
 * @author Arnaud Malapert
 *
 */
public class DagDTC extends GraphDTC {

	protected final int[] orderIndex;

	protected int[] order;

	private TopoAlgoStruct topStruct;

	/**
	 * @param n
	 */
	public DagDTC(final int n) {
		super(n);
		order = ChocoUtil.zeroToN(n);
		orderIndex = ChocoUtil.zeroToN(n);
	}


	@Override
	public int add(int i, int j) {
		if(isNotCyclic(i, j)) {
			final int val = super.add(i,j);
			if(val == ADDED) {fireTopologicalorder(i, j);}
			return val;
		}
		else {return CYCLE;}

	}

	protected void fireTopologicalorder(int i, int j) {
		computeTopologicalOrder();
	}

	public boolean remove(int i, int j) {
		boolean changes=false;
		if(removeEdges(i, j)) {
			for (int u = 0; u < n; u++) {
				if(index[u][i]!=null && index[u][i].isChild(j)) {
					changes |= hook(u,j);
				}
			}

		}
		return changes;
	}

	private final boolean removeEdges(int i, int j) {
		int idx = successors[i].lastIndexOf(j);
		if(idx != -1) {
			successors[i].remove(idx);
			idx = predecessors[j].lastIndexOf(i);
			predecessors[j].remove(idx);
			return true;
		}
		return false;
	}

	private final boolean hasNextNode(final TreeNode node) {
		if(node.incomingIndex<predecessors[node.index].size()) {return true;}
		else {
			node.incomingIndex=predecessors[node.index].size() ;
			return false;
		}
	}

	private final int nextNode(final TreeNode node) {
		node.incomingIndex++;
		return predecessors[node.index].get(node.incomingIndex-1);

	}
	private final boolean hook(final int i, final int j) {
		final TreeNode tij=index[i][j];
		while( hasNextNode(tij)) {
			final int x= nextNode(tij);
			if(index[i][x]!=null) {
				//transforming tree
				tij.father.removeChild(j);
				index[i][x].addChild(index[i][j]);
				return false;
			}
		}
		//j is no more in the transitive closure
		index[i][j].setRoot();
		index[i][j]=null; //NOPMD
		//we have to copy the children list because we will continue to modify it
		TreeNode[] children = tij.copyChildren();
		for (TreeNode u : children) {
			hook(i, u.index);
		}
		return true;
	}

	private final boolean isNotCyclic(final int i,final int j) {
		return index[j][i]==null;
	}

	public final boolean isCyclic(final int i,final int j) {
		return index[j][i]!=null;
	}


	protected final void computeTopologicalOrder() {
		if(topStruct == null) {topStruct = new TopoAlgoStruct();}
		topStruct.reset();
		int cpt=0;
		while(! topStruct.free.isEmpty()) {
			//set node
			order[cpt]= topStruct.free.remove(0);
			orderIndex[order[cpt]] = cpt;
			successors[order[cpt]].forEach(topStruct);
			cpt++;
		}
	}

	public final int[] getTopologicalOrderIndex() {
		return orderIndex;
	}

	public final int[] getTopologicalOrder() {
		return order;
	}

	protected class TopoAlgoStruct implements TIntProcedure {


		public final int nbPredecessors[];

		public final TIntArrayList free;

		public TopoAlgoStruct() {
			super();
			nbPredecessors = new int[n];
			free = new TIntArrayList();
		}


		void reset() {
			free.clear();
			for (int i = 0; i < n; i++) {
				if( predecessors[i].size() == 0) {
					free.add(i);
				}else {
					nbPredecessors[i] = predecessors[i].size();
				}
			}
		}


		@Override
		public boolean execute(int arg0) {
			nbPredecessors[arg0]--;
			if(nbPredecessors[arg0] == 0) {free.add(arg0);}
			return true;
		}

	}

}