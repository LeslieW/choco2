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


import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.extension.BinRelation;
import choco.kernel.solver.constraints.integer.extension.ConsistencyRelation;
import choco.kernel.solver.constraints.integer.extension.CspBinSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;

/**
 * The AC3rm algorithm. When a support is lost, we first check is the last one
 */
public final class AC3rmBinSConstraint extends CspBinSConstraint {

	protected int[] currentSupport0;
	protected int[] currentSupport1;

	protected int offset0;
	protected int offset1;

	protected int[] initS0; //initial number of supports of each value of x0
	protected int[] initS1; //initial number of supports of each value of x0
	protected int minS0;    //value with minimum number of supports for v0
	protected int minS1;	//value with minimum number of supports for v1

	protected int initDomSize0;
	protected int initDomSize1;

	protected AbstractIntDomain v0Domain, v1Domain;

	public AC3rmBinSConstraint(IntDomainVar x0, IntDomainVar x1, BinRelation relation) {
		super(x0, x1, relation);
		v0Domain = (AbstractIntDomain) v0.getDomain();
		v1Domain = (AbstractIntDomain) v1.getDomain();
	}

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINT_MASK + IntVarEvent.REMVAL_MASK;
        // return 0x0B;
    }

    public void fastInitNbSupports(int a, int b) {
		DisposableIntIterator itv0 = v0.getDomain().getIterator();
		int cpt1 = 0;
		while (itv0.hasNext()) {
			int val0 = itv0.next();
			cpt1++;
			DisposableIntIterator itv1 = v1.getDomain().getIterator();
			int cpt2 = 0;
			while (itv1.hasNext()) {
				cpt2++;
				int val1 = itv1.next();
				if (relation.isConsistent(val0, val1)) {
					initS0[val0 - offset0]++;
					initS1[val1 - offset1]++;
				}
				if (cpt2 >= a) break;
			}
			itv1.dispose();
			if (cpt1 >= b) break;
		}
		itv0.dispose();
		minS0 = Integer.MAX_VALUE;
		minS1 = Integer.MAX_VALUE;
		for (int i = 0; i < initS0.length; i++) {
			if (initS0[i] < minS0) minS0 = initS0[i];
		}
		for (int i = 0; i < initS1.length; i++) {
			if (initS1[i] < minS1) minS1 = initS1[i];
		}
	}

	public Object clone() {
		return new AC3rmBinSConstraint(this.v0, this.v1, this.relation);
	}

    public AbstractSConstraint opposite(Solver solver) {
        return new AC3rmBinSConstraint(this.v0, this.v1, (BinRelation) ((ConsistencyRelation) this.relation).getOpposite());        
    }

    public boolean testDeepakConditionV1(int y, int v0Size) {
		return initS1[y - offset1] <= (initDomSize0 - v0Size);
	}

	public boolean testDeepakConditionV0(int x, int v1Size) {
		return initS0[x - offset0] <= (initDomSize1 - v1Size);
	}

	public int getSupportV1(int y) {
		return currentSupport1[y - offset1];
	}

	public int getSupportV0(int x) {
		return currentSupport0[x - offset0];
	}

	// updates the support for all values in the domain of v1, and remove unsupported values for v1
	public void reviseV1() throws ContradictionException {
		int v0Size = v0Domain.getSize();
		if (minS1 <= (initDomSize0 - v0Size)) {
			DisposableIntIterator itv1 = v1Domain.getIterator();
			try {
				while (itv1.hasNext()) {
					int y = itv1.next();
					if (testDeepakConditionV1(y,v0Size)) { //initS1[y - offset1] <= (initDomSize0 - v0Size)) {
						if (!v0Domain.contains(getSupportV1(y)))
							updateSupportVal1(y);
					}
				}
			} finally {
				itv1.dispose();
			}
		}
	}

	// updates the support for all values in the domain of v0, and remove unsupported values for v0
	public void reviseV0() throws ContradictionException {
		int v1Size = v1Domain.getSize();
		if (minS0 <= (initDomSize1 - v1Size)) {
			DisposableIntIterator itv0 = v0Domain.getIterator();
			try {
				while (itv0.hasNext()) {
					int x = itv0.next();
					if (testDeepakConditionV0(x,v1Size)) { //initS0[x - offset0] <= (initDomSize1 - v1Size)) {
						if (!v1Domain.contains(getSupportV0(x)))
							updateSupportVal0(x);
					}
				}
			} finally {
				itv0.dispose();
			}
		}
	}

	public void storeSupportV0(int support,int x) {
		currentSupport0[x - offset0] = support;
		currentSupport1[support - offset1] = x;
	}

	public void storeSupportV1(int support,int y) {
		currentSupport1[y - offset1] = support;
		currentSupport0[support - offset0] = y;
	}


	protected void updateSupportVal0(int x) throws ContradictionException {
		boolean found = false;
		int support = 0;
        DisposableIntIterator itv1 = v1Domain.getIterator();
        while (!found && itv1.hasNext()) {
            support = itv1.next();
            if (relation.isConsistent(x, support)) found = true;
        }
        itv1.dispose();
		if (found) {
			storeSupportV0(support,x);
		} else {
			v0.removeVal(x, this, false);
		}
	}

	protected void updateSupportVal1(int y) throws ContradictionException {
		boolean found = false;
		int support = 0;
        DisposableIntIterator itv0 = v0Domain.getIterator();
        while (!found && itv0.hasNext()) {
            support = itv0.next();
            if (relation.isConsistent(support,y)) found = true;
        }
        itv0.dispose();

        if (found) {
			storeSupportV1(support,y);
		} else {
			v1.removeVal(y, this, false);
		}
	}

	public void init() {
		offset0 = v0.getInf();
		offset1 = v1.getInf();
		currentSupport0 = new int[v0.getSup() - v0.getInf() + 1];
		currentSupport1 = new int[v1.getSup() - v1.getInf() + 1];
		initS0 = new int[v0.getSup() - v0.getInf() + 1];
		initS1 = new int[v1.getSup() - v1.getInf() + 1];

		initDomSize0 = v0.getDomainSize();
		initDomSize1 = v1.getDomainSize();

		Arrays.fill(currentSupport0, -1);
		Arrays.fill(currentSupport1, -1);
		//double cardprod = v0.getDomainSize() * v1.getDomainSize();
		//if (cardprod <= 7000)
			fastInitNbSupports(Integer.MAX_VALUE,Integer.MAX_VALUE);
		//else fastInitNbSupports(80,80);
	}

	public void awake() throws ContradictionException {
		init();
		DisposableIntIterator itv0 = v0Domain.getIterator();
		int support = 0;
		boolean found = false;
        try{
            while (itv0.hasNext()) {
                DisposableIntIterator itv1 = v1Domain.getIterator();
                int val0 = itv0.next();
                while (itv1.hasNext()) {
                    int val1 = itv1.next();
                    if (relation.isConsistent(val0, val1)) {
                        support = val1;
                        found = true;
                        break;
                    }
                }
                itv1.dispose();
                if (!found) {
                    v0.removeVal(val0, this, false);
                } else {
                    storeSupportV0(support,val0);
                }
                found = false;
            }
        }finally {
            itv0.dispose();
        }
		found = false;
		DisposableIntIterator itv1 = v1Domain.getIterator();
        try{
		while (itv1.hasNext()) {
			itv0 = v0Domain.getIterator();
			int val1 = itv1.next();
			while (itv0.hasNext()) {
				int val0 = itv0.next();
				if (relation.isConsistent(val0, val1)) {
					support = val0;
					found = true;
					break;
				}
			}
			itv0.dispose();
			if (!found) {
				v1.removeVal(val1, this, false);
			} else {
				storeSupportV1(support,val1);
			}
			found = false;
		}
        }finally {
            itv1.dispose();
        }
		//propagate();
	}

	public void propagate() throws ContradictionException {
		reviseV0();
		reviseV1();
	}

	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		revise(idx);
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		revise(idx);
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		revise(idx);
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		revise(idx);
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		revise(varIndex);
	}

	public void revise(int idx) throws ContradictionException {
		if (idx == 0)
			reviseV1();
		else
			reviseV0();
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) {
			int value = v0.getVal();
			DisposableIntIterator itv1 = v1Domain.getIterator();
			try {
				while (itv1.hasNext()) {
					int val = itv1.next();
					if (!relation.isConsistent(value, val)) {
						v1.removeVal(val, this, false);
					}
				}
			} finally {
				itv1.dispose();
			}
		} else {
			int value = v1.getVal();
			DisposableIntIterator itv0 = v0Domain.getIterator();
			try {
				while (itv0.hasNext()) {
					int val = itv0.next();
					if (!relation.isConsistent(val, value)) {
						v0.removeVal(val, this, false);
					}
				}
			} finally {
				itv0.dispose();
			}
		}
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("AC3rm(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
				append(this.relation.getClass().getSimpleName()).append(")");
		return sb.toString();
	}
}

