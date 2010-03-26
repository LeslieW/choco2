package choco.cp.solver.constraints.integer.bool.sum;

import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;

public final class NeqBoolSum extends AbstractBoolSum {

	public NeqBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(environment, vars, bValue);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		super.awakeOnInst(idx);
		boolSumS.awakeOnNeq();
	}


	@Override
	public boolean isSatisfied(int[] tuple) {
		return MathUtils.sum(tuple) != boolSumS.bValue;
	}

	@Override
	public Boolean isEntailed() {
		return boolSumS.isEntailedNeq();
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return new EqBoolSum(solver.getEnvironment(), Arrays.copyOf(vars, vars.length), boolSumS.bValue);
	}
	
	@Override
	public String pretty() {
		return boolSumS.pretty("!=");
	}

}
