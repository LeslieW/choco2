package choco.cp.solver;

import choco.cp.solver.goals.GoalSearchSolver;
import choco.cp.solver.search.*;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.search.SolutionPoolFactory;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import samples.Examples.MinimumEdgeDeletion;

public class CPSolver2 extends CPSolver {

	public CPSolver2() {
		super();
	}

	public CPSolver2(IEnvironment env) {
		super(env);

	}

    /**
     * returning the index of the current worl
     */
    @Override
    public int getWorldIndex() {
        return sl.getCurrentDepth();
    }
    private AbstractSearchLoopWithRestart sl;

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
				
				//do not need restart option anymore
				else {
					if (objective instanceof IntDomainVar) {
						strategy = new BranchAndBound2(
								(IntDomainVar) objective, doMaximize);
					} else if (objective instanceof RealVar) {
						strategy = new RealBranchAndBound2((RealVar) objective,
								doMaximize);
					}
				}
			}
		}

        assert strategy != null;
        strategy.stopAtFirstSol = firstSolution;

		strategy.setLoggingMaxDepth(this.loggingMaxDepth);

		strategy.setSolutionPool( SolutionPoolFactory.makeDefaultSolutionPool(solutionPoolCapacity));

		addLimitsAndRestartStrategy();

		//SearchLoop2 sl = new SearchLoop2(strategy);
		sl = this.useRecomputation ? new SearchLoopWithRecomputation2(strategy) : new SearchLoop2(strategy);
		sl.setRestartAfterEachSolution(restart);
		strategy.setSearchLoop(sl);
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
//			_s =  useNew ? new CPSolver2() : new CPSolver(new EnvironmentRecomputation());
            _s = new CPSolver2();
			//if(useNew) ((CPSolver) _s).setRecomputation(true);
            ((CPSolver)_s).setRecomputation(useNew);
			_s.monitorBackTrackLimit(true);
			_s.setLoggingMaxDepth(25);
			_s.read(_m);
			_s.setRestart(false);
			_s.setFirstSolution(false);
			_s.attachGoal(new AssignVar(new MinDomain(_s), new IncreasingDomain()));
			//_s.setObjective(null);
			_s.generateSearchStrategy();
			
		}
		

		@Override
		public void solve() {
			super.solve();
			//System.out.println(_s.checkSolution(false));  
		}


		@Override
		public void prettyOut() {
			LOGGER.info(_s.getClass().getSimpleName());
			//LOGGER.info(_s.pretty());
			super.prettyOut();
		}

		@Override
		public void execute() {
			//super.execute();
			//super.execute(new Object[]{4,0.5,0}); //diff et pas beaucoup de noeuds
			
			//super.execute(new Object[]{20,0.5,0});
			//super.execute(new Object[]{19,0.5,0});
			execute(new Object[]{8,0.5,0});
			
			//assertEquals(Math.min( capa, _s.getNbSolutions()),  PatternExample._s.getSearchStrategy().getSolutionPool().size());
		}
	}
	
	public static void main(String[] args) {
		ChocoLogging.setVerbosity(Verbosity.SEARCH);
//		useNew = false;
//		new TestMed().execute();
		useNew = true;
		new TestMed().execute();
	}
}





