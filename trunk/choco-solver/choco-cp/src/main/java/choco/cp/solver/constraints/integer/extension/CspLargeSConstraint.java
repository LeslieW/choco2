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
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.constraints.integer.extension.ConsistencyRelation;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class CspLargeSConstraint extends AbstractLargeIntSConstraint {

	protected LargeRelation relation;

	protected int[] currentTuple;

	public CspLargeSConstraint(IntDomainVar[] vs, LargeRelation relation) {
		super(vs);
		this.relation = relation;
		this.currentTuple = new int[vs.length];
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		CspLargeSConstraint newc = (CspLargeSConstraint) super.clone();
		newc.currentTuple = new int[this.currentTuple.length];
		System.arraycopy(this.currentTuple, 0, newc.currentTuple, 0, this.currentTuple.length);
		return newc;
	}

	public LargeRelation getRelation() {
		return relation;
	}

     public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
    }

    @Override
	public void propagate() throws ContradictionException {
		boolean stop = false;
		int nbUnassigned = 0;
		int index = -1, i = 0;
		while (!stop && i < vars.length) {
			if (!vars[i].isInstantiated()) {
				nbUnassigned++;
				index = i;
			} else {
				currentTuple[i] = vars[i].getVal();
			}
			if (nbUnassigned > 1) {
				stop = true;
			}
			i++;
		}
		if (!stop) {
			if (nbUnassigned == 1) {
				DisposableIntIterator it = vars[index].getDomain().getIterator();
				try{
                while (it.hasNext()) {
					currentTuple[index] = it.next();
					if (!relation.isConsistent(currentTuple)) {
						vars[index].removeVal(currentTuple[index], cIndices[index]);
					}
				}
                }finally {
                    it.dispose();
                }
			} else {
				if (!relation.isConsistent(currentTuple)) {
					this.fail();
				}
			}
		}
	}

	@Override
	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		this.constAwake(false);
	}

	@Override
	public void awakeOnBounds(int varIndex) throws ContradictionException {
		this.constAwake(false);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		this.constAwake(false);
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
        return relation.isConsistent(tuple);
	}

	@Override
	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("CSPLarge({");
		for (int i = 0; i < vars.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			IntDomainVar var = vars[i];
			sb.append(var + ", ");
		}
		sb.append("})");
		return sb.toString();
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		LargeRelation rela2 = (LargeRelation) ((ConsistencyRelation) relation).getOpposite();
		AbstractSConstraint ct = new CspLargeSConstraint(vars, rela2);
		return ct;
	}

	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not yet implemented in CspLargeConstraint");
	}

}
