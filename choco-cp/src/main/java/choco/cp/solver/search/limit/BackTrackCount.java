package choco.cp.solver.search.limit;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;

public final class BackTrackCount extends AbstractGlobalSearchLimit {
	
	public BackTrackCount(AbstractGlobalSearchStrategy theStrategy) {
		super(theStrategy, Integer.MAX_VALUE, Limit.BACKTRACK);
		limitMask = END_NODE;
	}
	

	@Override
	public final void newNode() throws ContradictionException {}


	@Override
	public void endNode() throws ContradictionException {
		nb++;
	}
}

