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
package parser.absconparseur.components;


import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.solver.constraints.integer.extension.BinRelation;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import parser.absconparseur.InstanceTokens;
import parser.absconparseur.Toolkit;
import parser.chocogen.XmlClause;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PRelation {

	private String name;

	private int arity;

	private int nbTuples;

	private String semantics;

	private int[][] tuples;

	private List<int[]> ltuples;

    private int index;

    /**
	 * weights = null if semantics is different from InstanceTokens.SOFT
	 */
	private int[] weights;

	/**
	 * defaultCost = Integer.MAX_VALUE if defaultCost is infinity
	 */
	private int defaultCost;
	
	/**
	 * The max of all weights values and deafultCost. It is 1 for an ordinary relation.
	 */
	private int maximalCost;

	/**
	 * Choco relation representing this PRelation if it is binary
	 */
	protected BinRelation brel;

	/**
	 * Choco relation representing this PRelation if it is nrary
	 */
	protected LargeRelation lrel;


    /**
     * Representing this extentional constraint by a set of
     * clauses
     */
    protected List<XmlClause> satencoding;

    /**
	 * DFA to represent the table
	 * @return
	 */
	protected DFA dfa;

	protected boolean eqInTuples;

	protected boolean neqInTuples;

	public String getName() {
		return name;
	}

	public int getArity() {
		return arity;
	}

	public int getNbTuples() {
		return nbTuples;
	}

	public String getSemantics() {
		return semantics;
	}

	public int[][] getTuples() {
		return tuples;
	}

	public BinRelation getBrel() {
		return brel;
	}

	public void setBrel(BinRelation brel) {
		this.brel = brel;
	}

	public LargeRelation getLrel() {
		return lrel;
	}

	public void setLrel(LargeRelation lrel) {
		this.lrel = lrel;
	}

	public DFA getDfa() {
		return dfa;
	}

	public void setDfa(DFA dfa) {
		this.dfa = dfa;
	}

    public void setClauseEncoding(List<XmlClause> encoding) {
        satencoding = encoding;
    }

    public List<XmlClause> getSatEncoding() {
        return satencoding;
    }

    public boolean isNeqInTuples() {
		return neqInTuples;
	}

	public void setNeqInTuples(boolean neqInTuples) {
		this.neqInTuples = neqInTuples;
	}

	public boolean isEqInTuples() {
		return eqInTuples;
	}

	public void setEqInTuples(boolean eqInTuples) {
		this.eqInTuples = eqInTuples;
	}

	public boolean checkEqInCouples() {
		for (Iterator<int[]> it = ltuples.iterator(); it.hasNext();) {
			int[] t = it.next();
			if (t[0] != t[1]) {
				return false;
			}
		}
		return true;
	}

	public boolean checkNeqInCouples() {
		for (Iterator<int[]> it = ltuples.iterator(); it.hasNext();) {
			int[] t = it.next();
			if (t[0] == t[1]) {
				return false;
			}
		}
		return true;
	}

	public List<int[]> getListTuples() {
		if (ltuples != null) return ltuples;
		else {
			ltuples = new LinkedList<int[]>();
			for (int i = 0; i < tuples.length; i++) {
				ltuples.add(tuples[i]);
			}
			tuples = null;
		}
		return ltuples;
	}

	public void eraseListTuple() {
		ltuples = null;
	}

	public int[] getWeights() {
		return weights;
	}

	public int getDefaultCost() {
		return defaultCost;
	}

	public int getMaximalCost() {
		return maximalCost;
	}
	
	public PRelation(String name, int arity, int nbTuples, String semantics, int[][] tuples, int[] weights, int defaultCost) {
		this.name = name;
		this.arity = arity;
		this.nbTuples = nbTuples;
		this.semantics = semantics;
		this.tuples = tuples;
		this.weights = weights;
		this.defaultCost = defaultCost;
		if (weights == null)
			maximalCost=1;
		else {
			maximalCost=defaultCost;
			for (int w : weights)
				if (w > maximalCost)
					maximalCost=w;
		}
        this.index = Integer.parseInt(name.substring(1,name.length()));
    }

	public PRelation(String name, int arity, int nbTuples, String semantics, int[][] tuples) {
		this(name, arity, nbTuples, semantics, tuples, null, semantics.equals(InstanceTokens.SUPPORTS) ? 1 : 0);
	}

	public int computeCostOf(int[] tuple) {
		int position = Arrays.binarySearch(tuples, tuple, Toolkit.lexicographicComparator);
		if (semantics.equals(InstanceTokens.SOFT))
			return position >= 0 ? weights[position] : defaultCost;
		if (semantics.equals(InstanceTokens.SUPPORTS))
			return position >= 0 ? 0 : 1;
		return position >= 0 ? 1 : 0;
	}

	public String toString() {
		int displayLimit = 5;
		String s = "  relation " + name + " with arity=" + arity + ", semantics=" + semantics + ", nbTuples=" + nbTuples + ", defaultCost=" + defaultCost + " : ";
		for (int i = 0; i < Math.min(nbTuples, displayLimit); i++) {
			s += "(";
			for (int j = 0; j < arity; j++)
				s += (tuples[i][j] + (j < arity - 1 ? "," : ""));
			s += ") ";
			if (weights != null)
				s += " with cost=" + weights[i] + ", ";
		}
		return s + (nbTuples > displayLimit ? "..." : "");
	}

	public boolean isSimilarTo(int arity, int nbTuples, String semantics, int[][] tuples) {
		if (semantics.equals(InstanceTokens.SOFT))
			throw new IllegalArgumentException();
		if (this.arity != arity || this.nbTuples != nbTuples)
			return false;
		if (!this.semantics.equals(semantics))
			return false;
		for (int i = 0; i < tuples.length; i++)
			for (int j = 0; j < tuples[i].length; j++)
				if (this.tuples[i][j] != tuples[i][j])
					return false;
		return true;
	}

	public String getStringListOfTuples() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < tuples.length; i++) {
			for (int j = 0; j < tuples[i].length; j++) {
				s.append(tuples[i][j]);
				if (j != tuples[i].length - 1)
					s.append(' ');
			}
			if (i != tuples.length - 1)
				s.append('|');
		}
		return s.toString();
	}

    public int hashCode() {
        return index;
    }
}