package choco.cp.solver.search.limit;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.Limit;

public final class NodeCount extends AbstractGlobalSearchLimit {

	
	public NodeCount(AbstractGlobalSearchStrategy theStrategy) {
		super(theStrategy, Integer.MAX_VALUE,Limit.NODE);
		limitMask = NEW_NODE;
	}

	@Override
	public void newNode() throws ContradictionException {
		nb++;
	}
	
	@Override
	public final void endNode() throws ContradictionException {}

		
}



