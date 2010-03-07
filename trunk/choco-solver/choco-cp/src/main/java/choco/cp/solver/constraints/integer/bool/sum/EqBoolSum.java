package choco.cp.solver.constraints.integer.bool.sum;

import java.util.Arrays;

import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class EqBoolSum extends AbstractBoolSum {

	public EqBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(environment, vars, bValue);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		super.awakeOnInst(idx);
		if (nbo.get() > bValue || nbz.get() > gap) {
			fail();
		} else if (nbo.get() == bValue) {
			putAllZero();
		} else if (nbz.get() == gap) {
			putAllOne();
		}
	}

	@Override
	public void propagate() throws ContradictionException {
		if(bValue == 0) putAllZero();
		else if(bValue == vars.length) putAllOne();
		else super.propagate();
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		return MathUtils.sum(tuple) == bValue;
	}

	@Override
	public Boolean isEntailed() {
		final int lb = computeLbFromScratch();
		final int ub = computeUbFromScratch();
		if (lb > bValue || ub < bValue) {
			return Boolean.FALSE;
		} else if (lb == ub && bValue == lb) {
			return Boolean.TRUE;
		} else {
			return null;
		}
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return new NeqBoolSum(solver.getEnvironment(), Arrays.copyOf(vars, vars.length), bValue);
	}

}
