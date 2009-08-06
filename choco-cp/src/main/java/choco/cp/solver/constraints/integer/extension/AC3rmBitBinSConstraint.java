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

import choco.cp.solver.variables.integer.BitSetIntDomain;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.extension.CouplesBitSetTable;
import choco.kernel.solver.constraints.integer.extension.CspBinSConstraint;
import choco.kernel.solver.constraints.integer.extension.BinRelation;
import choco.kernel.solver.constraints.integer.extension.ConsistencyRelation;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Jul 29, 2008
 * Since : Choco 2.0.0
 *
 */
public class AC3rmBitBinSConstraint extends CspBinSConstraint {

	protected int offset0;
	protected int offset1;

	protected int minS0;    //value with minimum number of supports for v0
	protected int minS1;	//value with minimum number of supports for v1

	protected int initDomSize0;
	protected int initDomSize1;

	protected BitSetIntDomain v0Domain, v1Domain;

	public AC3rmBitBinSConstraint(IntDomainVar x0, IntDomainVar x1, CouplesBitSetTable relation) {
		super(x0, x1, relation);
		v0Domain = (BitSetIntDomain) v0.getDomain();
		v1Domain = (BitSetIntDomain) v1.getDomain();
	}

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
    }    

    public void fastInitNbSupports() {
		int[] initS1 = new int[v1.getSup() - v1.getInf() + 1];
		minS0 = Integer.MAX_VALUE;
		minS1 = Integer.MAX_VALUE;
		DisposableIntIterator itv0 = v0.getDomain().getIterator();
		while (itv0.hasNext()) {
			int val0 = itv0.next();
			int initS0 = 0;
			DisposableIntIterator itv1 = v1.getDomain().getIterator();
			while (itv1.hasNext()) {
				int val1 = itv1.next();
				if (relation.isConsistent(val0, val1)) {
					initS0++;
					initS1[val1 - offset1]++;
				}
			}
			if (initS0 < minS0) minS0 = initS0;
			itv1.dispose();
		}
		itv0.dispose();
		for (int i = 0; i < initS1.length; i++) {
			if (initS1[i] < minS1) minS1 = initS1[i];
		}
	}

	public Object clone() {
		return new AC3rmBitBinSConstraint(this.v0, this.v1, (CouplesBitSetTable) this.relation);
	}

    public AbstractSConstraint opposite() {
        return new AC3rmBitBinSConstraint(this.v0, this.v1, (CouplesBitSetTable) ((ConsistencyRelation) this.relation).getOpposite());        
    }

    // updates the support for all values in the domain of v1, and remove unsupported values for v1
	public void reviseV1() throws ContradictionException {
		int v0Size = v0Domain.getSize();
		if (minS1 <= (initDomSize0 - v0Size)) {
			DisposableIntIterator itv1 = v1Domain.getIterator();
			try {
				while (itv1.hasNext()) {
					int y = itv1.next();
					if (!((CouplesBitSetTable) relation).checkValue(1, y, v0Domain)) {
						v1.removeVal(y, cIdx1);
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
					if (!((CouplesBitSetTable) relation).checkValue(0, x, v1Domain)) {
						v0.removeVal(x, cIdx0);
					}
				}
			} finally {
				itv0.dispose();
			}
		}
	}


	public void init() {
		offset0 = v0.getInf();
		offset1 = v1.getInf();

		initDomSize0 = v0.getDomainSize();
		initDomSize1 = v1.getDomainSize();

		fastInitNbSupports();
	}

	public void awake() throws ContradictionException {
		init();
		DisposableIntIterator itv0 = v0Domain.getIterator();
        try{
            while (itv0.hasNext()) {
                int val0 = itv0.next();
                if (!((CouplesBitSetTable) relation).checkValue(0, val0, v1Domain)) {
                    v0.removeVal(val0, cIdx0);
                }
            }
        }finally {
            itv0.dispose();
        }
        itv0 = v1Domain.getIterator();
        try{
            while (itv0.hasNext()) {
                int val1 = itv0.next();
                if (!((CouplesBitSetTable) relation).checkValue(1, val1, v0Domain)) {
                    v1.removeVal(val1, cIdx1);
                }
            }
        }finally {
            itv0.dispose();
        }
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
						v1.removeVal(val, cIdx1);
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
						v0.removeVal(val, cIdx0);
					}
				}
			} finally {
				itv0.dispose();
			}
		}
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("AC3rmBitSet(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
				append(this.relation.getClass().getSimpleName()).append(")");
		return sb.toString();
	}
}

