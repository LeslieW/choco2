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
package choco.cp.solver.constraints.strong;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Collection;

public class SCVariable<MyConstraint extends SCConstraint> {

	private final IntDomainVar sVariable;

	private final int id;

	// private final Collection<Arc> arcs;

	private final Collection<MyConstraint> constraints;

	private final int offset;

	private int cid;

	public SCVariable(IntDomainVar sVariable, int id) {
		this.sVariable = sVariable;
		this.id = id;

		// arcs = new ArrayList<Arc>();
		constraints = new ArrayList<MyConstraint>();
		offset = sVariable.getInf();
	}

	public int getId() {
		return id;
	}

	public IntDomainVar getSVariable() {
		return sVariable;
	}

	public void addConstraint(MyConstraint constraint) {
		constraints.add(constraint);
	}

	public Collection<MyConstraint> getConstraints() {
		return constraints;
	}

	public String toString() {
		return "my" + sVariable;
	}

	public int getWDeg() {
		int wdeg = 0;
		for (SCConstraint c : constraints) {
			for (int i = c.getArity(); --i >= 0;) {
				final SCVariable<? extends SCConstraint> v = c.getVariable(i);
				if (v == this || v.sVariable.isInstantiated()) {
					continue;
				}
				wdeg += c.getWeight();
			}
		}
		return wdeg;
	}

	public int getDDeg() {
		int ddeg = 0;
		for (SCConstraint c : constraints) {
			if (!c.getSConstraint().getVar(0).isInstantiated() &&
					!c.getSConstraint().getVar(1).isInstantiated()){
				ddeg++;
			}
		}
		return ddeg;
	}

	public int getOffset() {
		return offset;
	}

	public void setCId(AbstractStrongConsistency<? extends SCVariable> asc) {
		cid = asc.cIndices[id];
//		for (DisposableIntIterator it = sVariable.getConstraintVector()
//				.getIndexIterator(); it.hasNext();) {
//			final int i = it.next();
//			if (sVariable.getConstraint(i) == asc) {
//				cid = i;
//				return;
//			}
//		}
//		throw new IllegalArgumentException();
	}

	public void removeVal(int value) throws ContradictionException {
		sVariable.remVal(value);
		//sVariable.removeVal(value, cid);
	}
}
