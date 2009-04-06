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
package choco.kernel.solver.constraints.integer.extension;

import choco.kernel.common.util.OpenBitSet;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.variables.integer.IBitSetIntDomain;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Jul 29, 2008
 * Since : Choco 2.0.0
 *
 */
public class CouplesBitSetTable extends ConsistencyRelation implements BinRelation, ExtensionalBinRelation {

	/**
	 * table[0][i] gives the supports of value i of variable 0
	 * table[1][i] gives the supports of value i of variable 1
	 */
	protected OpenBitSet[][] table;

	/**
	 * first value of x
	 */
	protected int[] offsets;

	protected int[] ns;

	public CouplesBitSetTable() {
	}

	public CouplesBitSetTable(boolean feas, int offset1, int offset2, int n1, int n2) {
		this.offsets = new int[]{offset1, offset2};
		this.table = new OpenBitSet[2][];
		this.table[0] = new OpenBitSet[n1];
		this.ns = new int[]{n1,n2};
		for (int i = 0; i < n1; i++) {
			table[0][i] = new OpenBitSet(n2);
			if (!feas) table[0][i].set(0,n2); 
		}
		this.table[1] = new OpenBitSet[n2];
		for (int i = 0; i < n2; i++) {
			table[1][i] = new OpenBitSet(n1);
			if (!feas) table[1][i].set(0,n1);
		}

		this.feasible = feas;
	}

	/**
	 * compute the opposite relation by "reusing" the table of consistency
	 *
	 * @return the opposite relation
	 */
	public ConsistencyRelation getOpposite() {
		CouplesBitSetTable t = new CouplesBitSetTable();
		t.feasible = !feasible;
		t.table = new OpenBitSet[2][];
		this.table[0] = new OpenBitSet[ns[0]];
		this.table[1] = new OpenBitSet[ns[1]];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < table[i].length; j++) {
				t.table[i][j] = (OpenBitSet) table[i][j].clone();
				t.table[i][j].flip(0,ns[i]);
			}
		}
		t.ns = ns;
		t.offsets = offsets;
		return t;
	}

	public void setCouple(int x, int y) {
		if (feasible) {
			table[0][x - offsets[0]].set(y - offsets[1]);
			table[1][y - offsets[1]].set(x - offsets[0]);
		} else {
			table[0][x - offsets[0]].clear(y - offsets[1]);
			table[1][y - offsets[1]].clear(x - offsets[0]);
		}
	}

	public void setCoupleWithoutOffset(int x, int y) {
		table[0][x].set(y);
		table[1][y].set(x);
	}

	public boolean isConsistent(int x, int y) {
		return table[0][x - offsets[0]].get(y - offsets[1]);
	}

	public boolean checkCouple(int x, int y) {
		return table[0][x - offsets[0]].get(y - offsets[1]);
	}


	public OpenBitSet getSupport(int var, int val) {
		assert (var == 0 || var == 1);
		return table[var][val - offsets[var]];
	}

	/**
	 * check is there exist a support for value val of variable var
	 * within the domain of v
	 * @param var
	 * @param val
	 * @param v
	 * @return
	 */
	public boolean checkValue(int var, int val, IBitSetIntDomain v) {
	   return ((StoredBitSet) v.getContent()).intersects(table[var][val - offsets[var]]);
	}
}
