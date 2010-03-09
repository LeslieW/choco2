package choco.cp.solver.constraints.global.scheduling;

import java.util.Arrays;

import choco.cp.solver.constraints.integer.bool.sum.LeqBoolSum;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class UseResourcesGeq extends AbstractUseResourcesSConstraint {

	public UseResourcesGeq(IEnvironment environment, TaskVar taskvar, int k,
			IntDomainVar[] usages, IRTask[] rtasks) {
		super(environment, taskvar, k, usages, rtasks);
	}
	
	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		super.awakeOnInst(idx);
		boolSumS.awakeOnGeq();
		filterHypotheticalDomains(); //FIXME temporary implementation (waiting for task event amangement)
	}

	@Override
	public void propagate() throws ContradictionException {
		if( boolSumS.filterGeq() ) {
			super.propagate();
		}
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		return MathUtils.sumFrom(tuple, BOOL_OFFSET) >= boolSumS.bValue;
	}

	@Override
	public Boolean isEntailed() {
		return boolSumS.isEntailedGeq();
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return new LeqBoolSum(solver.getEnvironment(), Arrays.copyOf(boolSumS.getBoolVars(), boolSumS.getBoolVars().length), boolSumS.bValue-1);
	}
}
