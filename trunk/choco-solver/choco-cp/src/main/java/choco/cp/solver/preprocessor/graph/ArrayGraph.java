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
// Petite API de Graphes pour tester BK73 
// TP 16/02/2007
// --------------------------------------

package choco.cp.solver.preprocessor.graph;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;

import java.util.BitSet;
import java.util.HashMap;

/**
 * A simple representation of a graph as both matrix/list of adjacency  to
 * perform clique detection among binary constraints
 */
public class ArrayGraph {

    //matrix of adjacency
    private BitSet[] mat;

    //lists of adjacency
    private int[][] adjmat;

    //number of nodes
    public int nbNode;

    //number of edges
    public int nbEdges = 0;

    //associate the constraint that gave rise to the edge
    public HashMap<Edge,Constraint> storeEdges;

    public ArrayGraph(int n) {
		mat = new BitSet[n]; // default is false
		adjmat = new int[n][];
		for (int i = 0; i < n; i++) {
			mat[i] = new BitSet(n);
		}
		nbNode = n;
        storeEdges = new HashMap<Edge,Constraint>();
    }

	public void addEdge(int i, int j) {
		if (!mat[i].get(j)) nbEdges++;
		if (!mat[j].get(i)) nbEdges++;
		mat[i].set(j);
		mat[j].set(i);
	}

	public void setNeighbours() {
		for (int i = 0; i < mat.length; i++) {
			adjmat[i] = new int[mat[i].cardinality()];
			int index = 0;
			for (int j = mat[i].nextSetBit(0); j >= 0; j = mat[i].nextSetBit(j + 1)) {
				adjmat[i][index] = j;
				index++;
			}
		}
	}

	public void remEdge(int i, int j) {
		if (mat[i].get(j)) nbEdges--;
		if (mat[j].get(i)) nbEdges--;
		mat[i].clear(j);
		mat[j].clear(i);
	}

	public boolean isIn(int i, int j) {
		return mat[i].get(j);
	}

	public int degree(int i) {
		return adjmat[i].length;
	}

	public int[] neighbours(int i) {
		return adjmat[i];
	}

	public int[] degrees() {
		int[] res = new int[mat.length];
		for (int i = 0; i < mat.length; i++) {
			res[i] = degree(i);
		}
		return res;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
        for (BitSet aMat : mat) {
            for (int j = 0; j < mat.length; j++) {
                if (aMat.get(j)) {
                    s.append("x ");
                } else {
                    s.append(". ");
                }
            }
            s.append("\n");
        }
		return s.toString();
	}

    public void storeEdge(Constraint c, int a, int b) {
        storeEdges.put(new Edge(a,b),c);
    }

    public void deleteConstraintEdge(CPModel mod, int a, int b) {
        Constraint c = storeEdges.get(new Edge(a,b));
        Constraint c2 = storeEdges.get(new Edge(b,a));
        if (c != null) mod.removeConstraint(c);
        else if (c2 != null) mod.removeConstraint(c2);
    }

    public static class Edge {
        int a;
        int b;

        public Edge(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int hashCode() {
            return a * 10000000 + b; 
        }

        public boolean equals(Object obj) {
            return  ((Edge) obj).a == a && ((Edge) obj).b == b; 
        }
    }

}

