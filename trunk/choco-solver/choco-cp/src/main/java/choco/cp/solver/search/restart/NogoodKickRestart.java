package choco.cp.solver.search.restart;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.NogoodRecorder;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;

public class NogoodKickRestart extends AbstractKickRestart {

	protected final NogoodRecorder recorder;

	public NogoodKickRestart(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
		if (searchStrategy.solver instanceof CPSolver) {
			recorder = new NogoodRecorder( (CPSolver) searchStrategy.solver);
		}else {
			throw new SolverException("nogood recording is a CPSolver feature.");
		}
	}

	/**
	 * record nogood from each restart.
	 */
	@Override
	public void restoreRootNode(IntBranchingTrace ctx) {
		recorder.reset();
		do {
			recorder.handleTrace(ctx);
		} while ( (ctx = searchStrategy.popTrace() ) != null );
		searchStrategy.solver.worldPopUntil(searchStrategy.baseWorld + 1);
		recorder.generateNogoods();  
	}

}
