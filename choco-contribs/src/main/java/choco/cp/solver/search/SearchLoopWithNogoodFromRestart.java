
package choco.cp.solver.search;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.SearchLoopWithRestart;
import choco.cp.solver.search.restart.RestartStrategy;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.variables.integer.IntDomainVar;




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


	protected final NoGoodRecorder recorder;

	public SearchLoopWithNogoodFromRestart(
			final AbstractGlobalSearchStrategy searchStrategy,
			final RestartStrategy restartStrategy) {
		super(searchStrategy, restartStrategy);
		recorder = new NoGoodRecorder((CPSolver) searchStrategy.getSolver());
		super.setRestartMoveMask(AbstractGlobalSearchStrategy.OPEN_NODE);
	}



	@Override
	public void setRestartMoveMask(int restartMask) {
		throw new UnsupportedOperationException("setup the move mask is not allowed when nogood recording is on");
	}




	@Override
	protected void restoreRootNode(IntBranchingTrace ctx) {
		if(Choco.DEBUG){CPSolver.flushLogs();}
		recorder.reset();
		recorder.handleTrace(ctx);
		while (searchStrategy.currentTraceIndex > searchStrategy.baseWorld) {
			recorder.handleTrace(searchStrategy.popTrace());
		}
		searchStrategy.solver.worldPopUntil(searchStrategy.baseWorld + 1);
		recorder.generateNogoods();  //succeed
	}


}



class NoGoodRecorder {

	protected final CPSolver scheduler;

	private int nbPosLits;

	private final IntDomainVar[] positiveLiterals; 

	private int nbNegLits;

	private final IntDomainVar[] negativeLiterals;

	private final List<NoGoodTail> tails = new LinkedList<NoGoodTail>();


	public NoGoodRecorder(final CPSolver scheduler) {
		super();
		this.scheduler = scheduler;
		positiveLiterals = new IntDomainVar[scheduler.getNbBooleanVars()];
		negativeLiterals = new IntDomainVar[scheduler.getNbBooleanVars()];
	}


	public void reset() {
		nbPosLits = 0;
		nbNegLits = 0;
		tails.clear();
	}


	protected IntDomainVar getBranchingVar(IntBranchingTrace trace) {
		if(trace.getBranchingObject() instanceof IntDomainVar) {
			return (IntDomainVar) trace.getBranchingObject();
		}else if(trace.getBranchingObject() instanceof IntVarValPair) {
			return ( (IntVarValPair) trace.getBranchingObject()).var;
		}	
		return null;
	}

	public void handleTrace(final IntBranchingTrace trace) {
		final IntDomainVar bvar = getBranchingVar(trace);
		if(bvar==null) {
			System.err.println("warning: not a integer variable");
			reset();
		}else if( ! bvar.getDomain().isBoolean()) {
			System.err.println("warning: not a boolean variable");
			reset();
		}else {
			//binary node
			if(trace.getBranchIndex() == 0) {
				//positive decision
				if(!tails.isEmpty()) {
					//add litteral
					if(bvar.getVal() == 0) {positiveLiterals[nbPosLits++] = bvar;}
					else {negativeLiterals[nbNegLits++] = bvar;}
				}
			}else {
				//negative decision
				//create a new noGood by adding a tail to the list
				tails.add(new NoGoodTail(bvar, nbPosLits, nbNegLits));
			}
		}
	}


	public void generateNogoods() {
		IntDomainVar[] posLits, negLits;
		for (NoGoodTail tail : tails) {
			//create array
			final int sp = nbPosLits - tail.posLitsOffset;
			final int sn = nbNegLits - tail.negLitsOffset;
			if(tail.tail.getVal() == 0) {
				posLits = new IntDomainVar[sp];
				negLits = new IntDomainVar[sn + 1];
				negLits[sn] = tail.tail; 
			}else {
				posLits = new IntDomainVar[sp + 1];
				negLits = new IntDomainVar[sn];
				posLits[sp] = tail.tail;
			}
			//copy involved nogood
			System.arraycopy(positiveLiterals, tail.posLitsOffset, posLits, 0, sp);
			System.arraycopy(negativeLiterals, tail.negLitsOffset, negLits, 0, sn);
			if(Choco.DEBUG) {
				System.out.println(Arrays.toString(posLits)+" "+Arrays.toString(negLits));
			}
			scheduler.addNogood(posLits, negLits);
		}
	}

	private static class NoGoodTail {

		public final IntDomainVar tail;

		public final int posLitsOffset;

		public final int negLitsOffset;

		public NoGoodTail(IntDomainVar tail, int nbPosLits, int nbNegLits) {
			super();
			this.tail = tail;
			this.posLitsOffset = nbPosLits;
			this.negLitsOffset = nbNegLits;
		}
	}

}
