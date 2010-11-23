package choco.cp.common.util.preprocessor.detector.scheduling;

import java.util.ArrayList;
import java.util.List;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;


public final class DisjFromCumulModelDetector extends AbstractRscDetector {


	private List<TaskVariable> tasks;
	private List<IntegerVariable> usages;

	public DisjFromCumulModelDetector(CPModel model) {
		super(model, null);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.CUMULATIVE;
	}

	@Override
	protected void setUp() {
		super.setUp();
		tasks = new ArrayList<TaskVariable>();
		usages = new ArrayList<IntegerVariable>();
	}


	@Override
	protected void tearDown() {
		super.tearDown();
		tasks = null;
		usages = null;
	}

	private void addTask(PPResource rsc, int idx) {
		tasks.add(rsc.getTask(idx));
		if(idx >= rsc.getParameters().getNbRegularTasks()) {
			usages.add(rsc.getUsage(idx));
		}
	}

	private TaskVariable[] tasks() {
		return tasks.toArray(new TaskVariable[tasks.size()]);	
	}

	private IntegerVariable[] usages() {
		return usages.toArray(new IntegerVariable[usages.size()]);	
	}


	@Override
	protected void apply(PPResource ppr) {
		tasks.clear();usages.clear();
		final int capa = ppr.getMaxCapa();
		//if the capacity is even, then we can add at most one task t1 such that h1 == limit.
		//Indeed, for all other task t2, h1 + h2 >= limit + (limit+1) > 2*limit = capa. 
		boolean addExtraTask = capa % 2 == 0;
		final int limit = capa/2;
		final int n = ppr.getParameters().getNbTasks();
		for (int i = 0; i < n; i++) {
			final int h = ppr.getMinHeight(i);
			if( h > limit) {
				addTask(ppr, i);
			} else if ( addExtraTask && h == limit) {
				addTask(ppr, i);
				addExtraTask = false;
			}else if( h < 0) {
				//the height is negative (producer tasks): cancel disjunction.
				tasks.clear();usages.clear();
				return;
			}
		}
		final int nc = tasks.size();
		if( nc > 2) {
			if( usages.isEmpty()) add(Choco.disjunctive(tasks(),Options.C_NO_DETECTION));
			else add(Choco.disjunctive(tasks(), usages(), Options.C_NO_DETECTION));
			if( nc == ppr.getParameters().getNbTasks()) {
				delete(ppr.getConstraint());
			}	
		}
	}
}
