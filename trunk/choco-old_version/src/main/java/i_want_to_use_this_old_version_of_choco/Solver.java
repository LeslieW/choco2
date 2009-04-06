// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.goals.Goal;
import i_want_to_use_this_old_version_of_choco.goals.solver.GoalSearchSolver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.*;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.search.*;
import i_want_to_use_this_old_version_of_choco.search.*;
import i_want_to_use_this_old_version_of_choco.set.search.*;
import i_want_to_use_this_old_version_of_choco.util.LightFormatter;

import java.security.AccessControlException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * This class serves both as a factory and as a handler for AbstractGlobalSearchSolvers:
 */
public class Solver extends AbstractEntity {

	/**
	 * The variable modelling the objective function
	 */
	protected Var objective;

	/**
	 * Maximization / Minimization problem
	 */
	protected boolean doMaximize;

	public void setLoggingMaxDepth(int loggingMaxDepth) {
		this.loggingMaxDepth = loggingMaxDepth;
	}

	/**
	 * maximal search depth for logging statements
	 */
	public int loggingMaxDepth = 5;

	/**
	 * Do we want to restart a new search after each solution
	 */
	protected boolean restart = false;

	/**
	 * do we want to explore one or all solutions (default=one solution)
	 */
	protected boolean firstSolution = true;

	/**
	 * The object controlling the global search exploration
	 */
	protected AbstractGlobalSearchSolver solver;

	/**
	 * Variable selector for integer
	 */
	protected VarSelector varIntSelector = null;

	/**
	 * Variable selector for real
	 */
	protected RealVarSelector varRealSelector = null;

	/**
	 * Variable selector for set
	 */
	protected SetVarSelector varSetSelector = null;


	/**
	 * Value iterator for integer
	 */
	protected ValIterator valIntIterator = null;

	/**
	 * Value iterator for real
	 */
	protected ValIterator valRealIterator = null;

	/**
	 * Value iterator for set
	 */
	protected ValIterator valSetIterator = null;


	/**
	 * Value selector for integer
	 */
	protected ValSelector valIntSelector = null;

	/**
	 * Value selector for real
	 */
	protected ValSelector valRealSelector = null;

	/**
	 * Value selector for set
	 */
	protected SetValSelector valSetSelector = null;


	protected int timeLimit = Integer.MAX_VALUE;

	protected int nodeLimit = Integer.MAX_VALUE;

	/**
	 * Temporary attached goal for the future generated solver.
	 */
	protected AbstractIntBranching tempGoal;

	/**
	 * Another way to define search is by using the api
	 * similar to ilog on search goals.
	 */
	protected Goal ilogGoal = null;


	public AbstractGlobalSearchSolver getSearchSolver() {
		return solver;
	}

	public Solver(AbstractProblem pb) {
		super(pb);
		setDefaultHandler();
		setVerbosity(SILENT);
	}

	public void generateSearchSolver(AbstractProblem pb) {
		problem = pb;
		if (null == objective) {
			if (ilogGoal != null)
				solver = new GoalSearchSolver(pb, ilogGoal);
			else solver = new Solve(this.getProblem());
		} else {
			if (ilogGoal != null)
				throw new Error("Ilog goal are not yet available in optimization");
			if (restart) {
				if (objective instanceof IntDomainVar)
					solver = new OptimizeWithRestarts((IntDomainVarImpl) objective, doMaximize);
				else if (objective instanceof RealVar)
					solver = new RealOptimizeWithRestarts((RealVar) objective, doMaximize);
			} else {
				if (objective instanceof IntDomainVar)
					solver = new BranchAndBound((IntDomainVarImpl) objective, doMaximize);
				else if (objective instanceof RealVar)
					solver = new RealBranchAndBound((RealVar) objective, doMaximize);
			}
		}
		solver.stopAtFirstSol = firstSolution;

		solver.setLoggingMaxDepth(this.loggingMaxDepth);

		solver.limits.add(new TimeLimit(solver, timeLimit));
		solver.limits.add(new NodeLimit(solver, nodeLimit));

		if (problem.useRecomputation())
			solver.setSearchLoop(new SearchLoopWithRecomputation(solver));

		if (ilogGoal == null) {
			if (tempGoal == null) {
				generateGoal(pb);
			} else {
				attachGoal(tempGoal);
				tempGoal = null;
			}
		}
	}

	protected void generateGoal(AbstractProblem pb) {
		//default strategy choice for integer
		boolean first = true;
		if (pb.getNbSetVars() > 0) {
			//default strategy choice for set
			if (varSetSelector == null) varSetSelector = new MinDomSet(pb);
			if (valSetSelector == null) valSetSelector = new MinEnv(pb);
			attachGoal(new AssignSetVar(varSetSelector, valSetSelector));
			first = false;
		}
		if (pb.getNbIntVars() > 0) {
			if (varIntSelector == null) varIntSelector = new MinDomain(pb);
			if (valIntIterator == null && valIntSelector == null) valIntIterator = new IncreasingDomain();
			if (valIntIterator != null) {
				if (first) {
					attachGoal(new AssignVar(varIntSelector, valIntIterator));
					first = false;
				} else addGoal(new AssignVar(varIntSelector, valIntIterator));
			} else {
				if (first) {
					attachGoal(new AssignVar(varIntSelector, valIntSelector));
					first = false;
				} else addGoal(new AssignVar(varIntSelector, valIntSelector));
			}
		}
		if (pb.getNbRealVars() > 0) {
			//default strategy choice for real
			if (varRealSelector == null) varRealSelector = new CyclicRealVarSelector(pb);
			if (valRealIterator == null && valRealSelector == null) valRealIterator = new RealIncreasingDomain();
			if (valRealIterator != null) {
				if (first) {
					attachGoal(new AssignInterval(varRealSelector, valRealIterator));
					first = false;
				} else {
					addGoal(new AssignInterval(varRealSelector, valRealIterator));
				}
			} else {
				if (first) {
					attachGoal(new AssignVar(varRealSelector, valRealSelector));
					first = false;
				} else {
					addGoal(new AssignVar(varRealSelector, valRealSelector));
				}
			}
		}
	}

	public void attachGoal(AbstractIntBranching branching) {
		if (solver == null) {
			tempGoal = branching;
		} else {
			AbstractIntBranching br = branching;
			while (br != null) {
				br.setSolver(solver);
				br = (AbstractIntBranching) br.getNextBranching();
			}
			solver.mainGoal = branching;
		}
	}

	public void addGoal(AbstractIntBranching branching) {
		AbstractIntBranching br;
		if (solver == null) {
			br = tempGoal;
		} else {
			branching.setSolver(solver);
			br = solver.mainGoal;
		}
		while (br.getNextBranching() != null) {
			br = (AbstractIntBranching) br.getNextBranching();
		}
		br.setNextBranching(branching);
	}

	/**
	 * commands the solver to start
	 */
	public void launch() {
		// solver.run();
		solver.incrementalRun();
	}

	/**
	 * returns the number of solutions encountered during the search
	 *
	 * @return the number of solutions to the problem that were encountered during the search
	 */
	public int getNbSolutions() {
		return solver.nbSolutions;
	}

	/**
	 * Sets the time limit i.e. the maximal time before stopping the search algorithm
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	/**
	 * Sets the node limit i.e. the maximal number of nodes explored by the search algorithm
	 */
	public void setNodeLimit(int nodeLimit) {
		this.nodeLimit = nodeLimit;
	}

	/**
	 * @return true if only the first solution must be found
	 */
	public boolean getFirstSolution() {
		return firstSolution;
	}

	/**
	 * Sets wether only the first solution must be found
	 */
	public void setFirstSolution(boolean firstSolution) {
		this.firstSolution = firstSolution;
	}

	/**
	 * Sets the variable selector the search solver should use.
	 *
	 * @deprecated see setVarIntSelector(VarSelector varSelector)
	 */
	public void setVarSelector(VarSelector varSelector) {
		this.varIntSelector = varSelector;
	}

	/**
	 * Sets the integer variable selector the search solver should use.
	 */
	public void setVarIntSelector(VarSelector varSelector) {
		this.varIntSelector = varSelector;
	}

	/**
	 * Sets the real variable selector the search solver should use.
	 */
	public void setVarRealSelector(RealVarSelector realVarSelector) {
		this.varRealSelector = realVarSelector;
	}

	/**
	 * Sets the set variable selector the search solver should use.
	 */
	public void setVarSetSelector(SetVarSelector setVarSelector) {
		this.varSetSelector = setVarSelector;
	}

	/**
	 * Sets the value iterator the search should use
	 *
	 * @deprecated see setValIntIterator(ValIterator valIterator)
	 */
	public void setValIterator(ValIterator valIterator) {
		this.valIntIterator = valIterator;
	}

	/**
	 * Sets the integer value iterator the search should use
	 */
	public void setValIntIterator(ValIterator valIterator) {
		this.valIntIterator = valIterator;
	}

	/**
	 * Sets the real value iterator the search should use
	 */
	public void setValRealIterator(RealValIterator realValIterator) {
		this.valRealIterator = realValIterator;
	}

	/**
	 * Sets the integer value iterator the search should use
	 */
	public void setValSetIterator(ValIterator valIterator) {
		this.valSetIterator = valIterator;
	}

	/**
	 * Sets the value selector the search should use
	 *
	 * @deprecated see setValIntSelector(ValSelector valSelector)
	 */
	public void setValSelector(ValSelector valSelector) {
		this.valIntSelector = valSelector;
	}

	/**
	 * Sets the integer value selector the search should use
	 */
	public void setValIntSelector(ValSelector valSelector) {
		this.valIntSelector = valSelector;
	}

	/**
	 * Sets the integer value selector the search should use
	 */
	public void setValRealSelector(ValSelector valSelector) {
		this.valRealSelector = valSelector;
	}

	/**
	 * Sets the integer value selector the search should use
	 */
	public void setValSetSelector(SetValSelector setValSelector) {
		this.valSetSelector = setValSelector;
	}


	public void setIlogGoal(Goal ilogGoal) {
		this.ilogGoal = ilogGoal;
	}

	/**
	 * set the optimization strategy:
	 * - restart or not after each solution found
	 *
	 * @param restart
	 */
	public void setRestart(boolean restart) {
		this.restart = restart;
	}

	/**
	 * a boolean indicating if the solver minize or maximize the objective function
	 *
	 * @param doMaximize
	 */
	public void setDoMaximize(boolean doMaximize) {
		this.doMaximize = doMaximize;
	}

	/**
	 * Set the variable to optimize
	 *
	 * @param objective
	 */
	public void setObjective(Var objective) {
		this.objective = objective;
	}

	public Number getOptimumValue() {
		if (solver instanceof AbstractOptimize) {
			return ((AbstractOptimize) solver).getBestObjectiveValue();
		} else if (solver instanceof AbstractRealOptimize) {
			return ((AbstractRealOptimize) solver).getBestObjectiveValue();
		}
		return null;
	}

	/**
	 * Checks if a limit has been encountered
	 */
	public boolean isEncounteredLimit() {
		return solver.isEncounteredLimit();
	}

	/**
	 * If a limit has been encounteres, return the involved limit
	 */
	public GlobalSearchLimit getEncounteredLimit() {
		return solver.getEncounteredLimit();
	}

	// **********************************************************************
	//                       LOGGERS MANAGEMENT
	// **********************************************************************
	public static final int SILENT = 0;
	public static final int SOLUTION = 1;
	public static final int SEARCH = 2;
	public static final int PROPAGATION = 3;

	static {
		try {
			setDefaultHandler();
			setVerbosity(SILENT);
		} catch (AccessControlException e) {
			// Do nothing if this is an applet !
			// TODO: see how to make it work with an applet !
		}
	}

	private static void setDefaultHandler() {
		// define default levels, take into account only info, warning and severe messages
		StreamHandler sh = new StreamHandler(System.out, new LightFormatter());
		setHandler(Logger.getLogger("choco"), sh);

		sh = new StreamHandler(System.out, new LightFormatter());
		setHandler(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search"), sh);

		sh = new StreamHandler(System.out, new LightFormatter());
		setHandler(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop"), sh);

		// Some loggers for debug purposes... not available for final user !
		Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").setLevel(Level.SEVERE);
		Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem").setLevel(Level.SEVERE);
		Logger.getLogger("choco.currentElement").setLevel(Level.SEVERE);
	}

	private static void setHandler(Logger l, Handler h) {
		// remove existing handler on choco logger and define choco handler
		// the handler defined here could be reused by other packages
		l.setUseParentHandlers(false);
		Handler[] handlers = l.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			Handler handler = handlers[i];
			l.removeHandler(handler);
		}
		// by default, handle (so print) only severe message, it could be modified in other package
		l.addHandler(h);
	}

	public static void setVerbosity(int verbosity) {
		switch (verbosity) {
			case SOLUTION:
				setVerbosity(Logger.getLogger("choco"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching"), Level.SEVERE);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop"), Level.SEVERE);
				break;
			case SEARCH:
				setVerbosity(Logger.getLogger("choco"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop"), Level.SEVERE);
				break;
			case PROPAGATION:
				setVerbosity(Logger.getLogger("choco"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching"), Level.ALL);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop"), Level.INFO);
				break;
			case SILENT:
			default:
				setVerbosity(Logger.getLogger("choco"), Level.SEVERE);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search"), Level.SEVERE);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching"), Level.SEVERE);
				setVerbosity(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop"), Level.SEVERE);
		}
	}

	public static void flushLogs() {
		flushLog(Logger.getLogger("choco"));
		flushLog(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search"));
		flushLog(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching"));
		flushLog(Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop"));
	}

	/**
	 * Sets the level of log for the Logger and the Handler. This means that
	 * inherited loggers will have at least same level if they do not have
	 * custom handler.
	 *
	 * @param logger the logger to modify its level
	 * @param level  the new level
	 */
	protected static void setVerbosity(Logger logger, Level level) {
		logger.setLevel(level);
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			Handler handler = handlers[i];
			handler.setLevel(level);
		}
	}

	protected static void flushLog(Logger log) {
		Handler[] handlers = log.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			Handler handler = handlers[i];
			handler.flush();
		}
	}

	// **********************************************************************
	//                       END OF LOGGERS MANAGEMENT
	// **********************************************************************

}
