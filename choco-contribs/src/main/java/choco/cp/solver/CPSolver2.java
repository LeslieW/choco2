package choco.cp.solver;

import samples.Examples.MinimumEdgeDeletion;
import choco.cp.solver.goals.GoalSearchSolver;
import choco.cp.solver.search.BranchAndBound2;
import choco.cp.solver.search.GlobalSearchStrategy;
import choco.cp.solver.search.SearchLoop2;
import choco.cp.solver.search.SearchLoopWithRecomputation;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.real.RealBranchAndBound;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.search.SolutionPoolFactory;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;

public class CPSolver2 extends CPSolver {

	public CPSolver2() {
		super();
	}

	public CPSolver2(IEnvironment env) {
		super(env);

	}

	@Override
	public void generateSearchStrategy() {
		if (!(tempGoal != null && tempGoal instanceof ImpactBasedBranching)
				|| strategy == null) { // <hca> really ugly to remove once
			// impact ok

			// There is no objective to reach
			if (null == objective) {
				// an ilogGoal has been defined
				if (ilogGoal != null) {
					strategy = new GoalSearchSolver(this, ilogGoal);
				}
				// Basic search strategy
				else {
					strategy = new GlobalSearchStrategy(this);
				}
			}
			// there is an objective to reach
			else {
				// ilogGoal has been defined => Error
				if (ilogGoal != null) {
					throw new UnsupportedOperationException(
					"Ilog goal are not yet available in optimization");
				}
				// If restart option has been precised
				//				if (restart) {
				//					// strategy
				//					if (objective instanceof IntDomainVar) {
				//						strategy = new OptimizeWithRestarts(
				//								(IntDomainVarImpl) objective, doMaximize);
				//					} else if (objective instanceof RealVar) {
				//						strategy = new RealOptimizeWithRestarts(
				//								(RealVar) objective, doMaximize);
				//					}
				//				}
				// if no restart option
				else {
					if (objective instanceof IntDomainVar) {
						strategy = new BranchAndBound2(
								(IntDomainVar) objective, doMaximize);
					} else if (objective instanceof RealVar) {
						strategy = new RealBranchAndBound((RealVar) objective,
								doMaximize);
					}
				}
			}
		}

		strategy.stopAtFirstSol = firstSolution;

		strategy.setLoggingMaxDepth(this.loggingMaxDepth);

		strategy.setSolutionPool( SolutionPoolFactory.makeDefaultSolutionPool(solutionPoolCapacity));

		addLimitsAndRestartStrategy();

		SearchLoop2 sl = new SearchLoop2(strategy);
		sl.setRestartAfterEachSolution(restart);
		strategy.setSearchLoop(sl);
		if (this.useRecomputation()) {
			strategy.setSearchLoop(new SearchLoopWithRecomputation(strategy));
		}
		if (ilogGoal == null) {
			if (tempGoal == null) {
				generateGoal();
			} else {
				attachGoal(tempGoal);
				tempGoal = null;
			}
		}
	}
	
	public static boolean useNew = true;
	
	static class TestMed extends MinimumEdgeDeletion {

		
		@Override
		public void buildSolver() {
			_s =  useNew ? new CPSolver2() : new CPSolver();
			_s.setLoggingMaxDepth(100);
			_s.read(_m);
			_s.setRestart(true);
			_s.setFirstSolution(false);
			_s.setDoMaximize(false);
			//_s.attachGoal(new AssignVar(new MinDomain(_s), new MinVal()));
			_s.generateSearchStrategy();
			
		}

		@Override
		public void prettyOut() {
			LOGGER.info(_s.getClass().getSimpleName());
			super.prettyOut();
		}

		@Override
		public void execute() {
			//super.execute();
			super.execute(new Object[]{4,0.5,0}); //diff et pas beaucoup de noeuds
			
			//super.execute(new Object[]{10,0.5,0});
			//super.execute(new Object[]{15,0.5,0});
			
			//assertEquals(Math.min( capa, _s.getNbSolutions()),  PatternExample._s.getSearchStrategy().getSolutionPool().size());
		}
	}
	
	public static void main(String[] args) {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		useNew = false;
		new TestMed().execute();
		useNew = true;
		new TestMed().execute();
	}
}





