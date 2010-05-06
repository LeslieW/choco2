package choco.cp.solver.configure;

import choco.cp.solver.search.AbstractSearchLoopWithRestart;
import choco.cp.solver.search.BranchAndBound;
import choco.cp.solver.search.SearchLoop;
import choco.cp.solver.search.SearchLoopWithRecomputation;
import choco.cp.solver.search.real.RealBranchAndBound;
import choco.cp.solver.search.restart.BasicKickRestart;
import choco.cp.solver.search.restart.IKickRestart;
import choco.cp.solver.search.restart.NogoodKickRestart;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.solver.Configuration;
import static choco.kernel.solver.Configuration.*;
import choco.kernel.solver.ResolutionPolicy;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.propagation.ShavingTools;
import choco.kernel.solver.search.*;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;

public final class StrategyFactory {

	private StrategyFactory() {
		super();
	}

	public static boolean isOptimize(Configuration conf) {
        final ResolutionPolicy policy = conf.readEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.class);
        return ! ResolutionPolicy.SATISFACTION.equals(policy);
	}
	
	
	public static void setDoOptimize(Solver solver, boolean maximize) {
		if(maximize) solver.getConfiguration().putEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.MAXIMIZE);
		else solver.getConfiguration().putEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.MINIMIZE);
    
	}
	
	public static boolean doMaximize(Solver solver) {
		final Configuration conf = solver.getConfiguration();
        final ResolutionPolicy policy = conf.readEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.class);
		return ! ResolutionPolicy.SATISFACTION.equals(policy) && ResolutionPolicy.MAXIMIZE.equals(policy);
	}
	
	public static Boolean doMaximize(Configuration conf) {
		final ResolutionPolicy policy = conf.readEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.class);
        switch (policy) {
            case MAXIMIZE:
                return Boolean.TRUE;
            case MINIMIZE:
                return Boolean.FALSE;
            case SATISFACTION:
            default:
                return null;
        }
	}
	
	

	public static int getRecomputationGap(Solver solver) {
		return solver.getConfiguration().readInt(RECOMPUTATION_GAP);
	}

	public static void validateCSP(Solver solver) {
		final Configuration conf = solver.getConfiguration();
		if( isOptimize(conf) || conf.readBoolean(BOTTOM_UP) ) {
			throw new SolverException("no_objective/optimize conflict");
		}
	}


	public static AbstractOptimize createBranchAndBound(Solver solver) {
		final Var var = solver.getObjective();
		if (var instanceof IntDomainVar) {
			return new BranchAndBound(solver, (IntDomainVar) var, doMaximize(solver));
		}else if (var instanceof RealVar) {
			return new RealBranchAndBound(solver, (RealVar) var, doMaximize(solver));
		}else throw new SolverException("invalid objective: "+var.pretty());
	}
	
	
	public static IKickRestart createKickRestart(AbstractGlobalSearchStrategy strategy) {
		return strategy.solver.getConfiguration().readBoolean(NOGOOD_RECORDING_FROM_RESTART) ?
				new NogoodKickRestart(strategy) : new BasicKickRestart(strategy);
	}

	private static AbstractSearchLoop createSearchLoop(AbstractGlobalSearchStrategy strategy, IKickRestart kickRestart, int recomputationGap) {
		return recomputationGap>1 ?
				new SearchLoopWithRecomputation(strategy, kickRestart, recomputationGap):
					new SearchLoop(strategy, kickRestart); 
	}

	public static AbstractSearchLoop createSearchLoop(AbstractGlobalSearchStrategy strategy) {
		final AbstractSearchLoop searchLoop = createSearchLoop(strategy, createKickRestart(strategy),getRecomputationGap(strategy.solver));
		if (searchLoop instanceof AbstractSearchLoopWithRestart) {
			( (AbstractSearchLoopWithRestart) searchLoop).setRestartAfterEachSolution(strategy.solver.getConfiguration().readBoolean(Configuration.RESTART_AFTER_SOLUTION));
		}
		return searchLoop;
	}

	public static ISolutionPool createSolutionPool(AbstractGlobalSearchStrategy strategy) {
		return SolutionPoolFactory.makeDefaultSolutionPool(strategy, strategy.solver.getConfiguration().readInt(Configuration.SOLUTION_POOL_CAPACITY));
	}
	
	public static boolean isUsingShavingTools(Solver solver) {
		final Configuration conf = solver.getConfiguration();
		return conf.readBoolean(INIT_SHAVING) ||
		conf.readBoolean(INIT_DESTRUCTIVE_LOWER_BOUND) ||
		conf.readBoolean(BOTTOM_UP);
	}
	
	public static ShavingTools createShavingTools(Solver solver) {
		if( isUsingShavingTools(solver) ) {
			final Configuration conf = solver.getConfiguration();
			ShavingTools shavingTools = new ShavingTools(solver, 
					conf.readBoolean(INIT_SHAVE_ONLY_DECISIONS) ? 
							solver.getIntDecisionVars() : VariableUtils.getIntVars(solver));
			shavingTools.setShavingLowerBound( conf.readBoolean(INIT_DLB_SHAVING));
			shavingTools.setDetectLuckySolution( conf.readBoolean(BOTTOM_UP) );
			return shavingTools;
		}
		return null;
	}
	

}
