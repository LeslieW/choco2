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
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.extension.BinRelation;
import choco.kernel.solver.constraints.integer.extension.ConsistencyRelation;
import choco.kernel.solver.constraints.integer.extension.CspBinSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class AC3BinSConstraint extends CspBinSConstraint {

	public AC3BinSConstraint(IntDomainVar x0, IntDomainVar x1, BinRelation rela) {//int[][] consistencyMatrice) {
		super(x0, x1, rela);
	}

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
    }
    

    public Object clone() {
		return new AC3BinSConstraint(this.v0, this.v1, this.relation);
	}

    // updates the support for all values in the domain of v1, and remove unsupported values for v1
	public void reviseV1() throws ContradictionException {
		int nbs = 0;
		DisposableIntIterator itv1 = v1.getDomain().getIterator();
		while (itv1.hasNext()) {
			DisposableIntIterator itv0 = v0.getDomain().getIterator();
			int val1 = itv1.next();
			while (itv0.hasNext()) {
				int val0 = itv0.next();
				if (relation.isConsistent(val0, val1)) {
					nbs += 1;
					break;
				}
			}
			itv0.dispose();
			if (nbs == 0) v1.removeVal(val1, cIdx1);
			nbs = 0;
		}
		itv1.dispose();
	}

	// updates the support for all values in the domain of v0, and remove unsupported values for v0
	public void reviseV0() throws ContradictionException {
		int nbs = 0;
		DisposableIntIterator itv0 = v0.getDomain().getIterator();
		while (itv0.hasNext()) {
			DisposableIntIterator itv1 = v1.getDomain().getIterator();
			int val0 = itv0.next();
			while (itv1.hasNext()) {
				int val1 = itv1.next();
				if (relation.isConsistent(val0, val1)) {
					nbs += 1;
					break;
				}
			}
			itv1.dispose();
			if (nbs == 0) v0.removeVal(val0, cIdx0);
			nbs = 0;
		}
		itv0.dispose();
	}

	// standard filtering algorithm initializing all support counts
	public void propagate() throws ContradictionException {
		reviseV0();
		reviseV1();
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		if (idx == 0) {
			reviseV1();
		} else
			reviseV0();
	}
	//propagate();

	/**
	 * Propagation when a minimal bound of a variable was modified.
	 *
	 * @param idx The index of the variable.
	 * @throws choco.kernel.solver.ContradictionException
	 *
	 */
	// Note: these methods could be improved by considering for each value, the minimal and maximal support considered into the nbEdges
	public void awakeOnInf(int idx) throws ContradictionException {
		if (idx == 0) {
			reviseV1();
		} else
			reviseV0();
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		if (idx == 0) {
			reviseV1();
		} else
			reviseV0();
	}


	/**
	 * Propagation when a variable is instantiated.
	 *
	 * @param idx The index of the variable.
	 * @throws choco.kernel.solver.ContradictionException
	 *
	 */

	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) {
			reviseV1();
		} else
			reviseV0();
	}


	public AbstractSConstraint opposite(Solver solver) {
		BinRelation rela2 = (BinRelation) ((ConsistencyRelation) relation).getOpposite();
		AbstractSConstraint ct = new AC3BinSConstraint(v0, v1, rela2);
		return ct;
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("AC3(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
				append(this.relation.getClass().getSimpleName()).append(")");
		return sb.toString();
	}
}
