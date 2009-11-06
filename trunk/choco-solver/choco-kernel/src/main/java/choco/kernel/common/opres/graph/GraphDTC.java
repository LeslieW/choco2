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

import gnu.trove.TIntArrayList;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntProcedure;
import choco.kernel.common.IDotty;




/**
 *
 * opération sur les enfants : insertion, suppression, recherche
 * @author Arnaud Malapert
 *
 */
final class TreeNode {

	protected TreeNode father;

	public int index;

	protected int incomingIndex=0;

	protected TIntObjectHashMap<TreeNode> children = new TIntObjectHashMap<TreeNode>();

	/**
	 * @param index
	 */
	public TreeNode(final int index) {
		super();
		this.index = index;
	}

	@Override
	public String toString() {
		return String.valueOf(index);
	}

	public void removeChild(final int i) {
		TreeNode child = children.remove(i);
		child.father=null; //NOPMD
	}

	public void setRoot() {
		if(father!=null) {
			father.removeChild(this.index);
		}
	}

	public void addChild(final TreeNode child) {
		child.father=this;
		this.children.put(child.index, child);
	}

	public TreeNode[] copyChildren() {
		return children.getValues( new TreeNode[children.size()]);
	}

	public boolean isChild(final int i) {
		return children.containsKey(i);
	}


}


/**
 * @author Arnaud Malapert  </br></br>
 *
 *
<table>
<tr valign="top"><td align="left">
Daniele Frigioni, Tobias Miller, Umberto Nanni, and Christos&nbsp;D. Zaroliagis.
</td></tr>
<tr valign="top"><td align="left">
<b> An experimental study of dynamic algorithms for transitive closure.</b>
</td></tr>
<tr valign="top"><td align="left">
 <em>ACM Journal of Experimental Algorithms</em>, 6:9, 2001.
</td></tr>
</table>
 *
 *
 *
 */
public class GraphDTC implements IDotty {

	public final static int ADDED=0;

	public final static int CYCLE=1;

	public final static int TRANSITIVE=2;

	public final static int INTERNAL_ERROR=3;

	public final static int EXISTING=4;

	public final int n;

	protected int nbEdges = 0;

	protected boolean TransitiveArcAdded = true;

	protected final TreeNode[][] index;

	protected final TIntArrayList[] successors;

	protected final TIntArrayList[] predecessors;

	public GraphDTC(final int n) {
		super();
		this.n=n;
		successors = new TIntArrayList[n];
		predecessors = new TIntArrayList[n];
		for (int i = 0; i < n; i++) {
			successors[i] = new TIntArrayList();
			predecessors[i] = new TIntArrayList();
		}
		index=initIndex();
	}


	public final boolean isTransitiveArcAdded() {
		return TransitiveArcAdded;
	}


	public final void setTransitiveArcAdded(boolean transitiveArcAdded) {
		TransitiveArcAdded = transitiveArcAdded;
	}




	private final TreeNode[][] initIndex() {
		TreeNode[][] res=new TreeNode[this.n][this.n];
		for (int i = 0; i < n; i++) {
			res[i][i]=new TreeNode(i);
		}
		return res;
	}



	protected final  boolean isNotTransitive(final int i,final int j) {
		return index[i][j]==null;
	}



	public boolean isTransitive(final int i,final int j) {
		return index[i][j]!=null;
	}

	protected final void meld(final int i, final int j, final int u, final int v) {
		index[i][v]=new TreeNode(v);
		index[i][u].addChild(index[i][v]);
		if(index[j][v]!=null) {
			for (TreeNode w	: index[j][v].copyChildren()) {
				if(index[i][w.index]==null) {
					meld(i, j, v, w.index);
				}
			}
		}

	}



	public int add(final int i,final int j) {
		if(isNotTransitive(i,j)) {
			successors[i].add(j);
			predecessors[j].add(i);
			for (int u = 0; u < this.n; u++) {
				if(index[u][i]!=null && index[u][j]==null) {
					meld(u,j,i,j);
				}
			}
			nbEdges++;
			return ADDED;
		}else {
			if(successors[i].contains(j)) {
				return EXISTING;
			}else if( isTransitiveArcAdded()) {
				successors[i].add(j);
				predecessors[j].add(i);
				nbEdges++;
			}
			return TRANSITIVE;
		}
	}

	/**
	 * @see choco.kernel.common.IDotty#toDotty()
	 */
	@Override
	public String toDotty() {
		return toDotty(true);
	}

	protected String toDotty(boolean primalOrDual) {
		final TIntArrayList[] graph = primalOrDual ? successors : predecessors;
		DotProcedure proc = new DotProcedure();
		return proc.toDotty(graph);
	}

	final class DotProcedure implements TIntProcedure {

		public final StringBuilder buffer = new StringBuilder();

		public int origin = 0;

		protected String toDotty(TIntArrayList[] graph) {
			for (origin = 0; origin < graph.length; origin++) {
				graph[origin].forEach(this);
			}
			return new String(buffer);
		}

		@Override
		public boolean execute(int arg0) {
			buffer.append(origin).append("->").append(arg0).append(";\n");
			return true;
		}
	}

	public boolean isDisconnected(int i) {
		return !hasPredecessor(i) && !hasSuccessor(i);
	}

	public final boolean hasPredecessor(final int i) {
		return !predecessors[i].isEmpty();
	}

	public final boolean hasSuccessor(final int i) {
		return !successors[i].isEmpty();
	}

	public final int getNbPredecessors(final int i) {
		return predecessors[i].size();
	}

	public final TIntArrayList getPredecessors(final int i) {
		return predecessors[i];
	}

	public final int getNbSuccessors(final int i) {
		return successors[i].size();
	}

	public final TIntArrayList getSuccessors(final int i) {
		return successors[i];
	}

	

	public final boolean isEmpty() {
		return nbEdges == 0;
	}


	public final boolean[][] toTreeNodeMatrix() {
		boolean[][] r=new boolean[n][n];
		for (int i = 0; i < index.length; i++) {
			for (int j = 0; j < index[i].length; j++) {
				r[i][j]= index[i][j]!=null;
			}
		}
		return r;
	}


}


