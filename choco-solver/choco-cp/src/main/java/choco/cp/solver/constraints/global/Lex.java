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
package choco.cp.solver.constraints.global;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBool;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Enforce a lexicographic ordering on two vectors of integer
 * variables x <_lex y with x = <x_0, ..., x_n>, and y = <y_0, ..., y_n>.
 * ref : Global Constraints for Lexicographic Orderings (Frisch and al)
 */
public class Lex extends AbstractLargeIntSConstraint {

	public int n;            // size of both vectors
	public IStateInt alpha;  // size of both vectors
	public IStateInt beta;
	public IStateBool entailed;
	public IntDomainVar[] x;
	public IntDomainVar[] y;

	public boolean strict = false;

	// two vectors of same size n vars = [.. v1 ..,.. v2 ..]
	public Lex(IntDomainVar[] vars, int n, boolean strict, IEnvironment environment) {
		super(vars);
		x = new IntDomainVar[n];
		y = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			x[i] = vars[i];
			y[i] = vars[i + n];
		}
		this.strict = strict;
		this.n = n;
		alpha = environment.makeInt(0);
		beta = environment.makeInt(0);
		entailed = environment.makeBool(false);
	}

    @Override
    public int getFilteredEventMask(int idx) {
        if(vars[idx].hasEnumeratedDomain()){
            return IntVarEvent.REMVALbitvector;
        }else{
            return IntVarEvent.INSTINTbitvector+IntVarEvent.BOUNDSbitvector;
        }
    }

    public boolean groundEq(IntDomainVar x1, IntDomainVar y1) {
		if (x1.isInstantiated() && y1.isInstantiated()) {
			return x1.getVal() == y1.getVal();
		}
		return false;
	}

	public boolean leq(IntDomainVar x1, IntDomainVar y1) {
		return x1.getSup() <= y1.getInf();
	}

	public boolean less(IntDomainVar x1, IntDomainVar y1) {
		return x1.getSup() < y1.getInf();
	}

	public boolean greater(IntDomainVar x1, IntDomainVar y1) {
		return x1.getInf() > y1.getSup();
	}

	public boolean checkLex(int i) {
		if (!strict) {
			if (i == n - 1) {
				return leq(x[i], y[i]);
			} else {
				return less(x[i], y[i]);
			}
		} else {
			return less(x[i], y[i]);
		}
	}

	public void ACleq(int i) throws ContradictionException {
		x[i].updateSup(y[i].getSup(), cIndices[i]);
		y[i].updateInf(x[i].getInf(), cIndices[i + n]);
	}

	public void ACless(int i) throws ContradictionException {
		x[i].updateSup(y[i].getSup() - 1, cIndices[i]);
		y[i].updateInf(x[i].getInf() + 1, cIndices[i + n]);
	}

	public void updateAlpha(int i) throws ContradictionException {
		if (i == beta.get()) {
			this.fail();
		}
		if (i == n) {
			entailed.set(true);
		} else {
			if (!groundEq(x[i], y[i])) {
				alpha.set(i);
				filter(i);
			} else {
				updateAlpha(i + 1);
			}
		}
	}

	public void updateBeta(int i) throws ContradictionException {
		if ((i + 1) == alpha.get()) {
			this.fail();
		}
		if (x[i].getInf() < y[i].getSup()) {
			beta.set(i + 1);
			if (x[i].getSup() >= y[i].getInf()) {
				filter(i);
			}
		} else if (x[i].getInf() == y[i].getSup()) {
			updateBeta(i - 1);
		}
	}

	public void initialize() throws ContradictionException {
		entailed.set(false);
		int i = 0;
		while (i < n && groundEq(x[i], y[i])) {
			i++;
		}
		if (i == n) {
			if (!strict) {
				entailed.set(true);
			} else {
				this.fail();
			}
		} else {
			alpha.set(i);
			if (checkLex(i)) {
				entailed.set(true);
			}
			beta.set(-1);
			while (i != n && x[i].getInf() <= y[i].getSup()) {
				if (x[i].getInf() == y[i].getSup()) {
					if (beta.get() == -1) {
						beta.set(i);
					}
				} else {
					beta.set(-1);
				}
				i++;
			}
			if (i == n) {
				if (!strict) {
					beta.set(Integer.MAX_VALUE);
				} else {
					beta.set(n);
				}
			} else if (beta.get() == -1) {
				beta.set(i);
			}
			if (alpha.get() >= beta.get()) {
				this.fail();
			}
			filter(alpha.get());
		}
	}

	public void filter(int i) throws ContradictionException {
		if (i < beta.get() && !entailed.get()) {                   //Part A
			if (i == alpha.get() && (i + 1 == beta.get())) {        //Part B
				ACless(i);
				if (checkLex(i)) {
					entailed.set(true);
				}
			} else if (i == alpha.get() && (i + 1 < beta.get())) {  //Part C
				ACleq(i);
				if (checkLex(i)) {
					entailed.set(true);
				} else if (groundEq(x[i], y[i])) {
					updateAlpha(i + 1);
				}
			} else if (alpha.get() < i && i < beta.get()) {         //Part D
				if (((i == beta.get() - 1) && x[i].getInf() == y[i].getSup()) || greater(x[i], y[i])) {
					updateBeta(i - 1);
				}
			}
		}
	}

	@Override
	public void awakeOnInf(int idx) throws ContradictionException {
		if (idx < n) {
			filter(idx);
		} else {
			filter(idx - n);
		}
	}

	@Override
	public void awakeOnSup(int idx) throws ContradictionException {
		if (idx < n) {
			filter(idx);
		} else {
			filter(idx - n);
		}
	}

	@Override
	public void awakeOnRem(int idx, int x) throws ContradictionException {
		if (idx < n) {
			filter(idx);
		} else {
			filter(idx - n);
		}
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx < n) {
			filter(idx);
		} else {
			filter(idx - n);
		}
	}

	@Override
	public void propagate() throws ContradictionException {
		filter(alpha.get());
	}

	@Override
	public void awake() throws ContradictionException {
		initialize();
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		for (int i = 0; i < x.length; i++) {
			int xi = tuple[i];
			int yi = tuple[i + n];
			if (xi < yi) {
				return true;
			}else if(xi>yi){
                return false;
            }//else xi == yi
		}
		if (strict) {
			return false;
		} else {
			return (tuple[n - 1] == tuple[n - 1 + n]);
		}
	}

	@Override
	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < x.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			IntDomainVar var = x[i];
			sb.append(var.pretty());
		}
		sb.append("} <");
		if (!strict) {
			sb.append("=");
		}
		sb.append("_lex {");
		for (int i = 0; i < y.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			IntDomainVar var = y[i];
			sb.append(var.pretty());
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not yet implemented on choco.cp.cpsolver.constraints.global.Lex");
	}
}
