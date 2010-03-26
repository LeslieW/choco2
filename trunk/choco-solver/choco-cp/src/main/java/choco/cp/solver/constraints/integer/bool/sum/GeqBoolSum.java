package choco.cp.solver.constraints.integer.bool.sum;

import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;

public final class GeqBoolSum extends AbstractBoolSum {

	public GeqBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(environment, vars, bValue);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		super.awakeOnInst(idx);
		boolSumS.awakeOnGeq();
	}

	@Override
	public void propagate() throws ContradictionException {
		if( boolSumS.filterGeq()) {
			super.propagate();
		}
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		return MathUtils.sum(tuple) >= boolSumS.bValue;
	}

	@Override
	public Boolean isEntailed() {
		return boolSumS.isEntailedGeq();
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return new LeqBoolSum(solver.getEnvironment(), Arrays.copyOf(vars, vars.length), boolSumS.bValue-1);
	}
	
	@Override
	public String pretty() {
		return boolSumS.pretty(">=");
	}
	
}
