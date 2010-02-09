package choco.kernel.solver.search.measure;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;


/**
 * Measure counting the number of fails
 */
public class FailMeasure implements PropagationEngineListener {

	private final PropagationEngine propagationEngine;

	private int failCount = Integer.MIN_VALUE;

	public FailMeasure(PropagationEngine propagationEngine) {
		super();
		this.propagationEngine = propagationEngine;
	}


	/**
	 * Define action to do just before a addition.
	 */
	public final void safeAdd() {
		if ( ! propagationEngine.containsPropagationListener(this)) {
			propagationEngine.addPropagationEngineListener(this);
			failCount = 0;
		}
	}
	/**
	 * Define action to do just before a deletion.
	 */
	@Override
	public final void safeDelete() {
		propagationEngine.removePropagationEngineListener(this);
		failCount = Integer.MIN_VALUE;
	}

	public final void safeReset() {
		failCount = propagationEngine.containsPropagationListener(this) ? 0 : Integer.MIN_VALUE;
	}

	public final int getFailCount() {
		return failCount;
	}

	public void contradictionOccured(ContradictionException e) {
		if(!e.isSearchLimitCause()) {failCount++;}
	}
}

