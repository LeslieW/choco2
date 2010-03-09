package choco.cp.solver.constraints.integer.bool.sum;

import java.util.Arrays;

import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class LeqBoolSum extends AbstractBoolSum {

	public LeqBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(environment, vars, bValue);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		super.awakeOnInst(idx);
		boolSumS.awakeOnLeq();
	}

	@Override
	public void propagate() throws ContradictionException {
		if ( boolSumS.filterLeq() ) {
			super.propagate();
		} 
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		return MathUtils.sum(tuple) <= boolSumS.bValue;
	}

	@Override
	public Boolean isEntailed() {
		return boolSumS.isEntailedLeq();
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return new GeqBoolSum(solver.getEnvironment(), Arrays.copyOf(vars, vars.length), boolSumS.bValue + 1);
	}

}
