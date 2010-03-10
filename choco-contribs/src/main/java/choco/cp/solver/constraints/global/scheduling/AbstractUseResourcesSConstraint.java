package choco.cp.solver.constraints.global.scheduling;

import choco.Choco;
import choco.cp.solver.constraints.integer.bool.sum.BoolSumStructure;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public abstract class AbstractUseResourcesSConstraint extends AbstractTaskSConstraint {

	private final IRTask[] rtasks;

	protected final BoolSumStructure boolSumS;

	protected static final int BOOL_OFFSET = 3;

	private static final int TASK_IDX = 0;

	public AbstractUseResourcesSConstraint(IEnvironment environment, TaskVar taskvar, int k, IntDomainVar[] usages, IRTask[] rtasks) {
		super(new TaskVar[]{taskvar}, usages);
		this.rtasks = rtasks;
		this.boolSumS = new BoolSumStructure(environment, this, usages,k);
	}	


	@Override
	public int getFilteredEventMask(int idx) {
		//listen only usage inst. (ignore changes on the real domain of the task)
		return idx < BOOL_OFFSET ? 0 : IntVarEvent.INSTINTbitvector;
	}

	@Override
	public void propagate() throws ContradictionException {
		boolSumS.reset();
		for (int i = BOOL_OFFSET; i < vars.length; i++) {
			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
		filterHypotheticalDomains();
	}

	public void filterHypotheticalDomains() throws ContradictionException {
		if( boolSumS.nbo.get() < boolSumS.bValue) {
			final int k = boolSumS.bValue - boolSumS.nbo.get();
			if(k > 1) {
				filterEarliestStartingTime(k);
				filterLatestCompletionTime(k);
			}else {
				assert k == 1;
				filterEarliestStartingTime();
				filterLatestCompletionTime();
			}
		}
	}


	protected final void filterLatestCompletionTime() throws ContradictionException {
		int maxLctI = Choco.MIN_LOWER_BOUND;
		for (int i = 0; i < rtasks.length; i++) {
			if(rtasks[i].isOptional()) {
				final int lctI= rtasks[i].getHTask().getLCT(); 
				if(lctI > maxLctI) maxLctI = lctI;
				//if( lctI < taskvars[TASK_IDX].getLCT()) {
					//TODO optimize comparison
				//	if(lctI > maxLctI) maxLctI = lctI;
				//}else return; //no update
			}
		}
		if(maxLctI > Choco.MIN_LOWER_BOUND && maxLctI < taskvars[TASK_IDX].getLCT()) {
			vars[getEndIndex(TASK_IDX)].updateSup(maxLctI, this,false);
		}
	}

	protected final void filterLatestCompletionTime(int k) throws ContradictionException {
		filterLatestCompletionTime(); //TODO

	}

	private void filterEarliestStartingTime() throws ContradictionException {
		int minEstI = Choco.MAX_UPPER_BOUND;
		for (int i = 0; i < rtasks.length; i++) {
			if(rtasks[i].isOptional()) {
				final int estI= rtasks[i].getHTask().getEST(); 
				if(estI < minEstI) 
					minEstI = estI;
				//if( estI > taskvars[TASK_IDX].getEST()) {
					//TODO optimize comparison
				//	if(estI < minEstI) minEstI = estI;
				//}else return; //no update
			}
		}
		if(minEstI < Choco.MAX_UPPER_BOUND && minEstI > taskvars[TASK_IDX].getEST()) {
			vars[getStartIndex(TASK_IDX)].updateInf(minEstI, this,false);
		}
	}


	protected void filterEarliestStartingTime(int k) throws ContradictionException {
		filterEarliestStartingTime(); //TODO
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		assert idx >= BOOL_OFFSET;
		final int val = vars[idx].getVal();
		if (val == 0) boolSumS.addZero();
		else boolSumS.addOne();
	}

}
