package choco.cp.solver.constraints.integer.bool.sum;

import java.util.Arrays;

import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class NeqBoolSum extends AbstractBoolSum {

	public NeqBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(environment, vars, bValue);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		super.awakeOnInst(idx);
		if (nbo.get() > bValue || nbz.get() > gap) {
			setEntailed();
		} else if (nbo.get() == bValue) {
			if(nbz.get() == gap - 1) putAllOne();
			else if( nbz.get() == gap) fail();
		} else if (nbz.get() == gap) {
			if(nbo.get() == bValue - 1) putAllZero();
			else if( nbo.get() == bValue) fail();
		}
	}


	@Override
	public boolean isSatisfied(int[] tuple) {
		return MathUtils.sum(tuple) != bValue;
	}

	@Override
	public Boolean isEntailed() {
		final int lb = computeLbFromScratch();
		final int ub = computeUbFromScratch();
		if (lb > bValue || ub < bValue) {
			return Boolean.TRUE;
		} else if (lb == ub && bValue == lb) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return new EqBoolSum(solver.getEnvironment(), Arrays.copyOf(vars, vars.length), bValue);
	}

}
