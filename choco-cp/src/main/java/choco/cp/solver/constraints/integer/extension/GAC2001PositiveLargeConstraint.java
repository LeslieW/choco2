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
package choco.cp.solver.constraints.integer.extension;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.extension.IterTuplesTable;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * GAC2001 in allowed tuples
 */
public class GAC2001PositiveLargeConstraint extends CspLargeSConstraint {

    /**
     * supports[i][j stores the index of the tuple that currently support
     * the variable-value pair (i,j)
     */
    protected IStateInt[][] supports;

	protected int[] blocks;

	protected int arity;

	protected int[] offsets;

	protected static final int NO_SUPPORT = -2;

	protected IterTuplesTable relation;

	protected int[][][] tab;

	// check if none of the tuple is trivially outside
	//the domains and if yes use a fast valid check
	//by avoiding checking the bounds
	protected ValidityChecker valcheck;

	public GAC2001PositiveLargeConstraint(IntDomainVar[] vs, IterTuplesTable relation) {
		super(vs, null);
		this.relation = relation;
		this.solver = vs[0].getSolver();
		this.arity = vs.length;
		this.blocks = new int[arity];
		this.offsets = new int[arity];
		this.tab = relation.getTableLists();
        this.supports = new IStateInt[arity][];

		for (int i = 0; i < arity; i++) {
			offsets[i] = vs[i].getInf();
            this.supports[i] = new IStateInt[vs[i].getSup() - vs[i].getInf() + 1];
            for (int j= 0; j < supports[i].length; j++) {
                this.supports[i][j] = solver.getEnvironment().makeInt(0);
            }

		}

		int[][] tt = relation.getTupleTable();
		boolean fastValidCheckAllowed = true;
		boolean fastBooleanValidCheckAllowed = true;

		// check if all tuples are within the range
		// of the domain and if so set up a faster validity checker
		// that avoids checking original bounds first		
		for (int i = 0; i < tt.length; i++) {
			for (int j = 0; j < tt[i].length; j++) {
				int lb = vs[j].getInf();
				int ub = vs[j].getSup();
				if (lb > tt[i][j] ||
						ub < tt[i][j]) {
					fastValidCheckAllowed = false;
				}
				if (lb < 0 || ub > 1) {
					fastBooleanValidCheckAllowed = false;
				}
			}
			if (!fastBooleanValidCheckAllowed &&
					!fastValidCheckAllowed) break;
		}
		if (fastBooleanValidCheckAllowed) {
			valcheck = new FastBooleanValidityChecker(arity,vars);
		} else if (fastValidCheckAllowed) {
			valcheck = new FastValidityChecker(arity,vars);
		} else valcheck = new ValidityChecker(arity,vars);
	}

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
    }

    /**
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    public int[] getFavoriteDomains() {
        return new int[]{IntDomainVar.BITSET,
                IntDomainVar.LINKEDLIST,
                IntDomainVar.BINARYTREE,
                IntDomainVar.BOUNDS,
        };
    }

    /**
	 * 	updates the support for all values in the domain of variable
	 * 	and remove unsupported values for variable
	 * @param indexVar
	 * @throws ContradictionException
	 */
	public void reviseVar(int indexVar) throws ContradictionException {
		IntIterator itv = vars[indexVar].getDomain().getIterator();
		while (itv.hasNext()) {
			int val = itv.next();
			int nva = val - relation.getRelationOffset(indexVar);
			int currentIdxSupport = getSupport(indexVar, val);
            currentIdxSupport = seekNextSupport(indexVar, nva, currentIdxSupport);
			if (currentIdxSupport == NO_SUPPORT) {
				vars[indexVar].removeVal(val, cIndices[indexVar]);
			} else {
				setSupport(indexVar, val, currentIdxSupport);
			}
		}
	}


	/**
	 * 	seek a new support for the pair variable-value : (indexVar, nva)
	 * start the iteration from the stored support (the last one)
	 */
	public int seekNextSupport(int indexVar, int nva, int start) {
		int currentIdxSupport;
		int[] currentSupport = null;
		for (int i = start; i < tab[indexVar][nva].length; i++) {
            currentIdxSupport = tab[indexVar][nva][i];
			currentSupport = relation.getTuple(currentIdxSupport);
			if (valcheck.isValid(currentSupport)) return i;
		}
		return NO_SUPPORT;
	}

	/**
	 * store the new support
	 * @param indexVar
	 * @param value
	 * @param idxSupport : the index of the support in the list of allowed tuples for
	          the pair variable-value (indexVar,value)
	 */
	public void setSupport(int indexVar, int value, int idxSupport) {
        supports[indexVar][value - offsets[indexVar]].set(idxSupport); //- offset already included in blocks
	}

	/**
	 * @param indexVar
	 * @param value
	 * @return the stored support for the pair (indexVar,value)
	 */
	public int getSupport(int indexVar, int value) {
		return supports[indexVar][value - offsets[indexVar]].get();
	}

	// REACTION TO EVENTS
	public void propagate() throws ContradictionException {
		for (int indexVar = 0; indexVar < arity; indexVar++)
			reviseVar(indexVar);
	}	

	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		filter(idx);
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		filter(idx);
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		filter(idx);
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		filter(idx);
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		filter(varIndex);
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		filter(idx);
	}

	public void awake() throws ContradictionException {
		propagate();
	}


	public void filter(int idx) throws ContradictionException {
		//sort variables regarding domain sizes to speedup the check !		
		valcheck.sortvars();
		for (int i = 0; i < arity; i++)
			if (idx != valcheck.position[i]) reviseVar(valcheck.position[i]);
	}


	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("GAC2001AllowedLarge({");
		for (int i = 0; i < vars.length; i++) {
			if (i > 0) sb.append(", ");
			IntDomainVar var = vars[i];
			sb.append(var.pretty());
		}
		sb.append("})");
		return sb.toString();
	}

    @Override
    //<hca> implementation not efficient at all because
    //this constraint never "check" tuples but iterate over them and check the domains.
    //this should only be called in the restore solution
    public boolean isSatisfied(int[] tuple) {
        int minListIdx = -1;
        int minSize = Integer.MAX_VALUE;
        for (int i = 0; i < tuple.length; i++) {
            if (tab[i][tuple[i] - offsets[i]].length < minSize) {
                minSize = tab[i][tuple[i] - offsets[i]].length;
                minListIdx = i;
            }
        }
        int currentIdxSupport;
        int[] currentSupport;
        int nva = tuple[minListIdx] - relation.getRelationOffset(minListIdx);
        for (int i = 0; i < tab[minListIdx][nva].length; i++) {
            currentIdxSupport = tab[minListIdx][nva][i];
            currentSupport = relation.getTuple(currentIdxSupport);
            boolean isValid = true;
            for (int j = 0; isValid && j < tuple.length; j++) {
                if (tuple[j] != currentSupport[j]) {
                    isValid = false;
                }
            }
            if (isValid) return true;
        }
        return false;
    }
}
