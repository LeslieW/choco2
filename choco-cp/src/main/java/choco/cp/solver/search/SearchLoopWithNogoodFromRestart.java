
package choco.cp.solver.search;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.restart.RestartStrategy;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;


/**
 * A search loop with nogood recording from restarts. </br>
 * Lecoutre, C.; Sais, L.; Tabary, S. & Vidal, <br>
 * Nogood Recording from Restarts </br>
 * IJCAI 2007 Proceedings of the 20th International Joint Conference on Artificial Intelligence, Hyderabad, India, January 6-12, 2007, 2007, 131-136

 * @author Arnaud Malapert</br> 
 * @since 22 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class SearchLoopWithNogoodFromRestart extends SearchLoopWithRestart {


	protected final NogoodRecorder recorder;

	public SearchLoopWithNogoodFromRestart(
			final AbstractGlobalSearchStrategy searchStrategy,
			final RestartStrategy restartStrategy) {
		super(searchStrategy, restartStrategy);
		recorder = new NogoodRecorder((CPSolver) searchStrategy.getSolver());
		super.setRestartMoveMask(AbstractGlobalSearchStrategy.OPEN_NODE);

		//searchStrategy.setLoggingMaxDepth(1000);

	}


	@Override
	public void setRestartMoveMask(int restartMask) {
		throw new UnsupportedOperationException("setup the move mask is not allowed when nogood recording is on");
	}




	@Override
	protected void restoreRootNode(IntBranchingTrace ctx) {
		recorder.reset();
		recorder.handleTrace(ctx);
		while ( (ctx = searchStrategy.popTrace() ) != null ) {
			recorder.handleTrace(ctx);
		}
		searchStrategy.solver.worldPopUntil(searchStrategy.baseWorld + 1);
		recorder.generateNogoods();  //succeed
	}


}


