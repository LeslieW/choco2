/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * *
 * CHOCO: an open-source Constraint Programming  *
 *    System for Research and Education          *
 *                                               *
 *   contributors listed in choco.Entity.java    *
 *          Copyright (C) F. Laburthe, 1999-2006 *
 *************************************************/
package choco.cp.solver;

import static choco.kernel.solver.search.SolutionPoolFactory.makeDefaultSolutionPool;
import gnu.trove.TLongObjectHashMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.configure.LimitConfiguration;
import choco.cp.solver.configure.RestartConfiguration;
import choco.cp.solver.constraints.ConstantSConstraint;
import choco.cp.solver.constraints.global.Occurrence;
import choco.cp.solver.constraints.global.scheduling.SchedulerConfig;
import choco.cp.solver.constraints.integer.EqualXC;
import choco.cp.solver.constraints.integer.EqualXYC;
import choco.cp.solver.constraints.integer.GreaterOrEqualXC;
import choco.cp.solver.constraints.integer.GreaterOrEqualXYC;
import choco.cp.solver.constraints.integer.IntLinComb;
import choco.cp.solver.constraints.integer.LessOrEqualXC;
import choco.cp.solver.constraints.integer.MaxOfAList;
import choco.cp.solver.constraints.integer.NotEqualXC;
import choco.cp.solver.constraints.integer.NotEqualXYC;
import choco.cp.solver.constraints.integer.NotEqualXYCEnum;
import choco.cp.solver.constraints.integer.bool.BoolIntLinComb;
import choco.cp.solver.constraints.integer.bool.BoolSum;
import choco.cp.solver.constraints.integer.bool.sat.ClauseStore;
import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.cp.solver.constraints.integer.extension.AC2001BinSConstraint;
import choco.cp.solver.constraints.integer.extension.AC3BinSConstraint;
import choco.cp.solver.constraints.integer.extension.AC3rmBinSConstraint;
import choco.cp.solver.constraints.integer.extension.AC3rmBitBinSConstraint;
import choco.cp.solver.constraints.integer.extension.CspLargeSConstraint;
import choco.cp.solver.constraints.integer.extension.GAC2001LargeSConstraint;
import choco.cp.solver.constraints.integer.extension.GAC2001PositiveLargeConstraint;
import choco.cp.solver.constraints.integer.extension.GAC3rmLargeConstraint;
import choco.cp.solver.constraints.integer.extension.GAC3rmPositiveLargeConstraint;
import choco.cp.solver.constraints.integer.extension.GACstrPositiveLargeSConstraint;
import choco.cp.solver.constraints.real.Equation;
import choco.cp.solver.constraints.real.MixedEqXY;
import choco.cp.solver.constraints.real.exp.RealCos;
import choco.cp.solver.constraints.real.exp.RealIntegerPower;
import choco.cp.solver.constraints.real.exp.RealMinus;
import choco.cp.solver.constraints.real.exp.RealMult;
import choco.cp.solver.constraints.real.exp.RealPlus;
import choco.cp.solver.constraints.real.exp.RealSin;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.constraints.set.Disjoint;
import choco.cp.solver.constraints.set.IsIncluded;
import choco.cp.solver.constraints.set.MemberXY;
import choco.cp.solver.constraints.set.SetCard;
import choco.cp.solver.constraints.set.SetEq;
import choco.cp.solver.constraints.set.SetIntersection;
import choco.cp.solver.constraints.set.SetNotEq;
import choco.cp.solver.constraints.set.SetUnion;
import choco.cp.solver.goals.GoalSearchSolver;
import choco.cp.solver.propagation.ChocEngine;
import choco.cp.solver.propagation.EventQueueFactory;
import choco.cp.solver.search.AbstractSearchLoopWithRestart;
import choco.cp.solver.search.BranchAndBound;
import choco.cp.solver.search.DefaultStrategyMeasures;
import choco.cp.solver.search.GlobalSearchStrategy;
import choco.cp.solver.search.SearchLoop;
import choco.cp.solver.search.SearchLoopWithRecomputation;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.branching.DomOverWDegBinBranching2;
import choco.cp.solver.search.integer.branching.DomOverWDegBranching2;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.limit.LimitManager;
import choco.cp.solver.search.real.AssignInterval;
import choco.cp.solver.search.real.CyclicRealVarSelector;
import choco.cp.solver.search.real.RealBranchAndBound;
import choco.cp.solver.search.real.RealIncreasingDomain;
import choco.cp.solver.search.restart.BasicKickRestart;
import choco.cp.solver.search.restart.IKickRestart;
import choco.cp.solver.search.restart.NogoodKickRestart;
import choco.cp.solver.search.set.AssignSetVar;
import choco.cp.solver.search.set.MinDomSet;
import choco.cp.solver.search.set.MinEnv;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.cp.solver.variables.integer.BooleanVarImpl;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.cp.solver.variables.integer.IntTerm;
import choco.cp.solver.variables.real.RealVarImpl;
import choco.cp.solver.variables.set.SetVarImpl;
import choco.kernel.common.IndexFactory;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.recomputation.EnvironmentRecomputation;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.memory.structure.StoredBipartiteVarList;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.branch.BranchingWithLoggingStatements;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.MetaSConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.constraints.integer.extension.BinRelation;
import choco.kernel.solver.constraints.integer.extension.CouplesBitSetTable;
import choco.kernel.solver.constraints.integer.extension.CouplesTable;
import choco.kernel.solver.constraints.integer.extension.ExtensionalBinRelation;
import choco.kernel.solver.constraints.integer.extension.IterLargeRelation;
import choco.kernel.solver.constraints.integer.extension.IterTuplesTable;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.constraints.integer.extension.TuplesList;
import choco.kernel.solver.constraints.integer.extension.TuplesTable;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.constraints.set.SetSConstraint;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.propagation.AbstractPropagationEngine;
import choco.kernel.solver.propagation.ConstraintEvent;
import choco.kernel.solver.propagation.ConstraintEventQueue;
import choco.kernel.solver.propagation.EventQueue;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.VarEventQueue;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.AbstractOptimize;
import choco.kernel.solver.search.AbstractSearchStrategy;
import choco.kernel.solver.search.ISolutionPool;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;
import choco.kernel.solver.search.real.RealValIterator;
import choco.kernel.solver.search.real.RealVarSelector;
import choco.kernel.solver.search.set.AbstractSetVarSelector;
import choco.kernel.solver.search.set.SetValSelector;
import choco.kernel.solver.search.set.SetVarSelector;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;
import choco.kernel.visu.IVisu;


/**
 * This class serves both as a factory and as a handler for
 * AbstractGlobalSearchSolvers:
 */
public class CPSolver implements Solver {


	/**
	 * A constant denoting the true constraint (always satisfied)
	 */
	public static final SConstraint TRUE = new ConstantSConstraint(true);
	/**
	 * A constant denoting the false constraint (never satisfied)
	 */
	public static final SConstraint FALSE = new ConstantSConstraint(false);

	/**
	 * Reference to an object for logging trace statements related to Abtract
	 * Solver (using the java.util.logging package)
	 */

	protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

	protected final IndexFactory indexfactory;
	/**
	 * Allows to know if the model is feasible (null if it was not solved).
	 */
	public Boolean feasible = null;
	/**
	 * True if the model was solved.
	 */
	protected boolean solved = false;
	/**
	 * The environment managing the backtrackable data.
	 */
	protected IEnvironment environment;
	/**
	 * The propagation engine to propagate during solving.
	 */
	protected AbstractPropagationEngine propagationEngine;
	/**
	 * The EventQueue copies for worldPush/Pop during propation
	 */
	protected VarEventQueue[] veqCopy = null;
	protected ConstraintEventQueue[] ceqCopy = null;

	/**
	 * A constant denoting a null integer term. This is useful to make the API
	 * more robust, for instance with linear expression with null coefficients.
	 */
	public static final IntTerm ZERO = new IntTerm(0);
	public static final IntTerm UN = new IntTerm(1);


	/**
	 * tells the strategy wether or not use recomputation.
	 * The value of the parameter indicates the maximum recomputation gap, i.e. the maximum number of decisions between two storages.
	 * If the parameter is lower than or equal to 1, the trailing storage mechanism is used (default).
	 */
	protected int recomputationGap = 1;

	/**
	 * Decide if redundant constraints are automatically to the model to reason
	 * on cardinalities on sets as well as kernel and enveloppe
	 */
	public boolean cardinalityReasonningsOnSETS = true;

	/**
	 * an index useful for re-propagating cuts (static constraints) upon
	 * backtracking
	 */
	public IStateInt indexOfLastInitializedStaticConstraint;

	/**
	 * The (optimization or decision) model to which the entity belongs.
	 */

	public CPModel model;

	/**
	 * All the constraints of the model.
	 */
	protected PartiallyStoredVector<Propagator> constraints;

	/**
	 * All the search intVars in the model.
	 */
	protected StoredBipartiteVarList<IntDomainVar> intVars;
	/**
	 * All the set intVars in the model.
	 */
	protected StoredBipartiteVarList<SetVar> setVars;
	/**
	 * All the float vars in the model.
	 */
	protected StoredBipartiteVarList<RealVar> floatVars;

	protected StoredBipartiteVarList<TaskVar> taskVars;
	/**
	 * All the decision integer Vars in the model.
	 */
	protected ArrayList<IntDomainVar> intDecisionVars;
	/**
	 * All the decision set Vars in the model.
	 */
	protected ArrayList<SetVar> setDecisionVars;
	/**
	 * All the decision float vars in the model.
	 */
	protected ArrayList<RealVar> floatDecisionVars;

	/**
	 * All the decision task vars in the model.
	 */
	protected ArrayList<TaskVar> taskDecisionVars;

	/**
	 * All the integer constant variables in the model.
	 */
	protected HashMap<Integer, IntDomainVar> intconstantVars;

	/**
	 * All the real constant variables in the model.
	 */
	protected HashMap<Double, RealIntervalConstant> realconstantVars;

	protected TLongObjectHashMap<Var> mapvariables;

	protected TLongObjectHashMap<SConstraint> mapconstraints;

	/**
	 * Precision of the search for a real model.
	 */
	protected double precision = 1.0e-6;
	/**
	 * Minimal width reduction between two propagations.
	 */
	protected double reduction = 0.99;

	/**
	 * The variable modelling the objective function
	 */
	protected Var objective;

	/**
	 * Maximization / Minimization model
	 */
	protected boolean doMaximize;


	protected final SchedulerConfig scheduler;

	/**
	 * A global constraint to manage nogoods (as clauses)
	 */
	public ClauseStore nogoodStore;

	/**
	 * propNogoodWorld give the world above which the nogood constraint
	 * need to be propagated
	 */
	public int propNogoodWorld;

	public final void setLoggingMaxDepth(int loggingMaxDepth) {
		this.loggingMaxDepth = loggingMaxDepth;
	}

	public final int getLoggingMaxDepth() {
		return loggingMaxDepth;
	}

	/**
	 * maximal search depth for logging statements
	 */
	public int loggingMaxDepth = 10;

	/**
	 * set the number of stored solutions.
	 * it defines a default {@link ISolutionPool} for the solver. Default pools are partially resizable if 1 < capa < Integer.MaxValue.
	 * you can easily provide set your own policy with @{link {@link AbstractSearchStrategy#setSolutionPool(ISolutionPool)}.
	 */
	@Override
	public void setSolutionPoolCapacity(int capacity) {
		this.solutionPoolCapacity = capacity;
	}

	public int solutionPoolCapacity = 1;


	/**
	 * do we want to explore one or all solutions (default=one solution)
	 */
	protected boolean firstSolution = true;

	/**
	 * The object controlling the global search exploration
	 */
	protected AbstractGlobalSearchStrategy strategy;

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


	public final LimitConfiguration limitConfig = new LimitConfiguration();

	public final RestartConfiguration restartConfig = new RestartConfiguration();

	//protected LimitManager limitManager = new LimitManager();

	protected CPModelToCPSolver mod2sol;

	/**
	 * Temporary attached goal for the future generated strategy.
	 */
	public AbstractIntBranchingStrategy tempGoal;

	/**
	 * Another way to define search is by using the api similar to ilog on
	 * search goals.
	 */
	protected Goal ilogGoal = null;

	int eventQueueType = EventQueueFactory.BASIC;

	public AbstractGlobalSearchStrategy getSearchStrategy() {
		return strategy;
	}

	public void resetSearchStrategy() {
		strategy = null;
	}

	public CPSolver() {
		this(new EnvironmentTrailing());
	}

	public CPSolver(IEnvironment env) {
		mod2sol = new CPModelToCPSolver(this);
		mapvariables = new TLongObjectHashMap<Var>();
		mapconstraints = new TLongObjectHashMap<SConstraint>();
		intVars = new StoredBipartiteVarList<IntDomainVar>(env);
		setVars = new StoredBipartiteVarList<SetVar>(env);
		floatVars = new StoredBipartiteVarList<RealVar>(env);
		taskVars = new StoredBipartiteVarList<TaskVar>(env);
		intDecisionVars = new ArrayList<IntDomainVar>();
		setDecisionVars = new ArrayList<SetVar>();
		floatDecisionVars = new ArrayList<RealVar>();
		taskDecisionVars = new ArrayList<TaskVar>();
		intconstantVars = new HashMap<Integer, IntDomainVar>();
		realconstantVars = new HashMap<Double, RealIntervalConstant>();
		this.propagationEngine = new ChocEngine(this);
		this.environment = env;
		this.constraints = env.makePartiallyStoredVector();
		indexfactory = new IndexFactory();
		scheduler = new SchedulerConfig(this);
		if (env instanceof EnvironmentRecomputation) {
			setRecomputation(true);
		}
		this.indexOfLastInitializedStaticConstraint = env.makeInt(PartiallyStoredVector.getFirstStaticIndex() - 1);
	}

	/**
	 * Specify the visualization of the solver. Allow the visu to get
	 * informations from the solver to visualize it.
	 *
	 * @param visu the external visualizer
	 */
	public void visualize(IVisu visu) {
		// Change the var event queue to an observable one
		this.eventQueueType = EventQueueFactory.OBSERVABLE;
		this.propagationEngine.setVarEventQueues(this.eventQueueType);
		// Initialize the visu, create the component and observe the variables
		visu.init(this);
		// Make it visible
		visu.setVisible(true);
	}

	public IndexFactory getIndexfactory() {
		return indexfactory;
	}

	public boolean contains(Variable v) {
		return mapvariables.containsKey(v.getIndex());
	}

	public String getSummary() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Pb[");
		buffer.append(getNbIntVars() + getNbRealVars() + getNbSetVars())
		.append(" vars, ");
		buffer.append(getNbTaskVars()).append(" tasks, ");
		buffer.append(getNbIntConstraints()).append(" cons]");
		if (strategy != null) {
			buffer.append("\nLimits");
			buffer.append(strategy.runtimeStatistics());
		}
		return new String(buffer);
	}

	public String pretty() {
		StringBuffer buf = new StringBuffer(getSummary());
		buf.append('\n');
		buf.append(this.varsToString());
		buf.append(this.constraintsToString());
		return new String(buf);
	}

	public String varsToString() {
		StringBuffer buf = new StringBuffer();
		buf.append("==== VARIABLES ====\n");
		for (int i = 0; i < getNbIntVars(); i++) {
			buf.append(getIntVar(i).pretty());
			buf.append("\n");
		}
		for (int i1 = 0; i1 < floatVars.size(); i1++) {
			Object floatVar = floatVars.get(i1);
			RealVar realVar = (RealVar) floatVar;
			buf.append(realVar.pretty());
		}
		for (int i = 0; i < setVars.size(); i++) {
			buf.append(getSetVar(i).pretty());
			buf.append("\n");
		}
		buf.append("==== TASKS ====\n");
		//noinspection unchecked
		buf.append(StringUtils.prettyOnePerLine(taskVars.toList()));
		return new String(buf);
	}

	public String constraintsToString() {
		StringBuffer buf = new StringBuffer();
		buf.append("==== CONSTRAINTS ====\n");
		DisposableIntIterator it = constraints.getIndexIterator();
		while (it.hasNext()) {
			int i = it.next();
			AbstractSConstraint c = (AbstractSConstraint) constraints.get(i);
			buf.append(c.pretty());
			buf.append("\n");
		}
		it.dispose();
		return new String(buf);
	}

	public void read(Model m) {
		this.model = (CPModel) m;
		initReading();
		mod2sol.readVariables(model);
		mod2sol.readDecisionVariables();
		mod2sol.readConstraints(model);
	}

	/**
	 * Prepare Solver to read the model
	 */
	protected void initReading(){
		//0- create data structure for boolean variable
		this.getEnvironment().createSharedBipartiteSet(model.getNbBoolVar());
	}


	public SConstraint makeSConstraint(Constraint mc) {
		return mod2sol.makeSConstraint(mc);
	}

	public SConstraint makeSConstraint(Constraint mc, boolean b) {
		return mod2sol.makeSConstraint(mc, b);
	}

	public SConstraint[] makeSConstraintAndOpposite(Constraint mc) {
		return mod2sol.makeSConstraintAndOpposite(mc);
	}

	public SConstraint[] makeSConstraintAndOpposite(Constraint mc, boolean b) {
		return mod2sol.makeSConstraintAndOpposite(mc, b);
	}

	public void addConstraint(Constraint... tabic) {
		Constraint ic;
		for (Constraint aTabic : tabic) {
			ic = aTabic;
			Iterator<Variable> it = ic.getVariableIterator();
			while (it.hasNext()) {
				Variable v = it.next();
				if (!mapvariables.containsKey(v.getIndex())) {
					v.findManager(model.properties);
					mod2sol.readModelVariable(v);
				}
			}
			ic.findManager(model.properties);
			mod2sol.readConstraint(ic, model
					.getDefaultExpressionDecomposition());
		}
	}

	/**
	 * Retrieves the model of the entity
	 */

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = (CPModel) model;
	}

	/**
	 * Set the precision of the search for a real model.
	 */
	@Override
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * Get the precision of the search for a real model.
	 */
	@Override
	public double getPrecision() {
		return precision;
	}

	/**
	 * Set the minimal width reduction between two propagations.
	 */
	@Override
	public void setReduction(double reduction) {
		this.reduction = reduction;
	}

	/**
	 * Get the minimal width reduction between two propagations.
	 */
	@Override
	public double getReduction() {
		return reduction;
	}

	/**
	 * Returns the memory environment used by the model.
	 */

	public final IEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Return the type of eventQueues. It can be equal to -
	 * {@value choco.cp.solver.propagation.EventQueueFactory#BASIC} -
	 * {@value choco.cp.solver.propagation.EventQueueFactory#OBSERVABLE}
	 *
	 * @return the type of event queue
	 */
	public final int getEventQueueType() {
		return eventQueueType;
	}

	public void setFeasible(Boolean b) {
		this.feasible = b;
	}

	public Boolean getFeasible() {
		return feasible;
	}

	public String solutionToString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < getNbIntVars(); i++) {
			final IntVar v = getIntVar(i);
			if (v.isInstantiated()) {
				buf.append(v.toString());
				buf.append(", ");
			}
		}
		for (int j = 0; j < getNbRealVars(); j++) {
			final RealVar v = getRealVar(j);
			if (v.isInstantiated()) {
				buf.append(v.toString());
				buf.append(", ");
			}
		}

		for (int k = 0; k < getNbSetVars(); k++) {
			final SetVar v = getSetVar(k);
			if (v.isInstantiated()) {
				buf.append(v.toString());
				buf.append(", ");
			}
		}
		return new String(buf);
	}

	public void setRandomSelectors(long seed) {
		this.setRandomSelectors(new Random(seed));
	}

	public void setRandomSelectors() {
		this.setRandomSelectors(new Random());
	}

	public void setRandomSelectors(Random manager) {
		if (intDecisionVars == null || intDecisionVars.isEmpty()) {
			this.setVarIntSelector(new RandomIntVarSelector(this, manager
					.nextLong()));
		} else {
			IntDomainVar[] v = intDecisionVars
			.toArray(new IntDomainVar[intDecisionVars.size()]);
			this.setVarIntSelector(new RandomIntVarSelector(this, v, manager
					.nextLong()));
		}
		this.setValIntSelector(new RandomIntValSelector(manager.nextLong()));

		if (setDecisionVars == null || setDecisionVars.isEmpty()) {
			this.setVarSetSelector(new RandomSetVarSelector(this, manager
					.nextLong()));
		} else {
			SetVar[] v = setDecisionVars.toArray(new SetVar[setDecisionVars
			                                                .size()]);
			this.setVarSetSelector(new RandomSetVarSelector(this, v, manager
					.nextLong()));
		}
		this.setValSetSelector(new RandomSetValSelector(manager.nextLong()));
	}


	/**
	 * Generate a search strategy to run over the tree search. The search
	 * strategy is build, according to the choice of the user :
	 * <ul>
	 * <li>what are the options and goals? (ilogGoal, basic goals, objective,
	 * restart),</li>
	 * <li>search for one or every solution? (solve, solveAll),</li>
	 * <li>what are the limits wanted? (time, node, backtrack, fail)</li>
	 * <li>does it use recomputation?</li>
	 * </ul>
	 */
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
				if (objective instanceof IntDomainVar) {
					strategy = new BranchAndBound(
							(IntDomainVar) objective, doMaximize);
				} else if (objective instanceof RealVar) {
					strategy = new RealBranchAndBound((RealVar) objective,
							doMaximize);
				}
			}
		}


		assert strategy != null;
		strategy.stopAtFirstSol = firstSolution;

		strategy.setSolutionPool(makeDefaultSolutionPool(solutionPoolCapacity));

		strategy.setSearchMeasures(
				new DefaultStrategyMeasures(strategy, 
						generateSearchLoop(), 
						generateLimitManager()
				)
		);


		if (ilogGoal == null) {
			if (tempGoal == null) {
				generateGoal();
			} else {
				attachGoal(tempGoal);
				tempGoal = null;
			}
		}

		//logging statements
		if( ChocoLogging.getBranchingLogger().isLoggable(BranchingWithLoggingStatements.LOGGING_LEVEL)) {
			strategy.mainGoal = BranchingWithLoggingStatements.setLoggingStatement(strategy.mainGoal);
		}
	}

	protected LimitManager generateLimitManager() {
		final LimitManager limitManager = new LimitManager(strategy);
		limitManager.setSearchLimit(limitConfig.makeSearchLimit(strategy)); //controlling the search
		limitManager.setRestartLimit(limitConfig.makeRestartLimit(strategy)); //controlling the restart
		//controlling the restart strategy
		limitManager.setRestartStrategy(
				restartConfig.getRestartPolicy(), 
				limitConfig.createLimit(strategy,limitConfig.getRestartStrategyLimitType(), Integer.MAX_VALUE)
		); 
		strategy.setLimitManager(limitManager);
		return limitManager;
	}

	protected AbstractSearchLoopWithRestart generateSearchLoop() {
		final IKickRestart kickRestart = ( 
				restartConfig.isRecordNogoodFromRestart() ? 
						new NogoodKickRestart(strategy) : 
							new BasicKickRestart(strategy)
		); 

		final AbstractSearchLoopWithRestart searchLoop =  (
				useRecomputation() ? 
						new SearchLoopWithRecomputation(strategy, kickRestart, getRecomputationGap()):
							new SearchLoop(strategy, kickRestart) )
							;
		searchLoop.setRestartAfterEachSolution(restartConfig.isRestartAfterEachSolution());
		searchLoop.setInitializeSearchAfterRestart(restartConfig.isInitializingSearchAfterRestart());
		strategy.setSearchLoop(searchLoop);
		return searchLoop;
	}

	public AbstractIntBranchingStrategy generateRealGoal() {
		// default strategy choice for real
		if (varRealSelector == null) {
			//			if (floatDecisionVars.isEmpty()) {
			//				varRealSelector = new CyclicRealVarSelector(this);
			//			} else {
			//				varRealSelector = new CyclicRealVarSelector(this);
			//			}
			varRealSelector = new CyclicRealVarSelector(this);
		}
		if (valRealIterator == null && valRealSelector == null) {
			valRealIterator = new RealIncreasingDomain();
		}
		return valRealIterator != null ? new AssignInterval(varRealSelector,
				valRealIterator) : new AssignVar(varRealSelector,
						valRealSelector);
	}

	public AbstractIntBranchingStrategy generateSetGoal() {
		// default strategy choice for set
		if (varSetSelector == null) {
			if (setDecisionVars.isEmpty()) {
				varSetSelector = new MinDomSet(this);
			} else {
				SetVar[] t = new SetVar[setDecisionVars.size()];
				setDecisionVars.toArray(t);
				varSetSelector = new MinDomSet(this, t);
			}
		}
		if (valSetSelector == null) {
			valSetSelector = new MinEnv(this);
		}
		return new AssignSetVar(varSetSelector, valSetSelector);
	}

	public AbstractIntBranchingStrategy generateIntGoal() {
		// default strategy choice for integer
		if (valIntIterator == null && valIntSelector == null) {
			valIntIterator = new IncreasingDomain();
		}

		if (varIntSelector == null) {
			if (intDecisionVars.isEmpty()) {
				return valIntIterator == null ? new DomOverWDegBinBranching2(this, valIntSelector) : new DomOverWDegBranching2(this, valIntIterator);
			} else {
				IntDomainVar[] t = new IntDomainVar[intDecisionVars.size()];
				intDecisionVars.toArray(t);
				return valIntIterator == null ? new DomOverWDegBinBranching2(this, valIntSelector, t) : new DomOverWDegBranching2(this, valIntIterator, t);
			}
		} else {
			return valIntIterator == null ? new AssignVar(varIntSelector, valIntSelector) : new AssignVar(varIntSelector, valIntIterator);
		}
	}

	// default strategy
	protected void generateGoal() {
		boolean first = true;
		if (getNbSetVars() > 0) {
			attachGoal(generateSetGoal());
			first = false;
		}

		if (getNbIntVars() > 0) {
			if (first) {
				attachGoal(generateIntGoal());
				first = false;
			} else {
				addGoal(generateIntGoal());
			}
		}

		if (getNbRealVars() > 0) {
			if (first) {
				attachGoal(generateRealGoal());
			} else {
				addGoal(generateRealGoal());
			}
		}
	}

	// protected void generateGoal() {
	// //default strategy choice for integer
	// boolean first = true;
	// if (this.getNbSetVars() > 0) {
	// //default strategy choice for set
	// if (varSetSelector == null) {
	// if(setDecisionVars.isEmpty()){
	// varSetSelector = new MinDomSet(this);
	// }else{
	// SetVar[] t = new SetVar[setDecisionVars.size()];
	// setDecisionVars.toArray(t);
	// varSetSelector = new MinDomSet(this, t);
	// }
	// }
	// if (valSetSelector == null) {
	// valSetSelector = new MinEnv(this);
	// }
	// attachGoal(new AssignSetVar(varSetSelector, valSetSelector));
	// first = false;
	// }
	// if (this.getNbIntVars() > 0) {
	// if (varIntSelector == null) {
	// if(intDecisionVars.isEmpty()){
	// varIntSelector = new MinDomain(this);
	// }else{
	// IntDomainVar[] t = new IntDomainVar[intDecisionVars.size()];
	// intDecisionVars.toArray(t);
	// varIntSelector = new MinDomain(this, t);
	// }
	// }
	// if (valIntIterator == null && valIntSelector == null) {
	// valIntIterator = new IncreasingDomain();
	// }
	// if (valIntIterator != null) {
	// if (first) {
	// attachGoal(new AssignVar(varIntSelector, valIntIterator));
	// first = false;
	// } else {
	// addGoal(new AssignVar(varIntSelector, valIntIterator));
	// }
	// } else {
	// if (first) {
	// attachGoal(new AssignVar(varIntSelector, valIntSelector));
	// first = false;
	// } else {
	// addGoal(new AssignVar(varIntSelector, valIntSelector));
	// }
	// }
	// }
	// if (this.getNbRealVars() > 0) {
	// //default strategy choice for real
	// if (varRealSelector == null) {
	// if(floatDecisionVars.isEmpty()){
	// varRealSelector = new CyclicRealVarSelector(this);
	// }else{
	// varRealSelector = new CyclicRealVarSelector(this);
	// }
	// }
	// if (valRealIterator == null && valRealSelector == null) {
	// valRealIterator = new RealIncreasingDomain();
	// }
	// if (valRealIterator != null) {
	// if (first) {
	// attachGoal(new AssignInterval(varRealSelector, valRealIterator));
	// first = false;
	// } else {
	// addGoal(new AssignInterval(varRealSelector, valRealIterator));
	// }
	// } else {
	// if (first) {
	// attachGoal(new AssignVar(varRealSelector, valRealSelector));
	// first = false;
	// } else {
	// addGoal(new AssignVar(varRealSelector, valRealSelector));
	// }
	// }
	// }
	//
	// }

	/**
	 * Attach the FIRST branching strategy to the search strategy
	 *
	 * @param branching
	 *            the branching strategy
	 */
	public void attachGoal(AbstractIntBranchingStrategy branching) {
		if (strategy == null) {
			tempGoal = branching;
		} else {
			// To remove properly the listener from the Propagation engine
			if(strategy.mainGoal!=null
					&& strategy.mainGoal instanceof PropagationEngineListener){
				((PropagationEngineListener)strategy.mainGoal).safeDelete();
			}
			AbstractIntBranchingStrategy br = branching;
			while (br != null) {
				br.setSolver(strategy);
				br = (AbstractIntBranchingStrategy) br.getNextBranching();
			}
			strategy.mainGoal = branching;
		}
	}

	/**
	 * Add branching strategy to the search strategy. (Do not have to be called
	 * before attachGoal(AbstractIntBranching branching))
	 *
	 * @param branching
	 *            the next branching strategy Branching strategies are run in
	 *            the order given by the adding.
	 */
	public void addGoal(AbstractIntBranchingStrategy branching) {
		AbstractIntBranchingStrategy br;
		if (strategy == null) {
			br = tempGoal;
		} else {
			branching.setSolver(strategy);
			br = strategy.mainGoal;
		}
		while (br.getNextBranching() != null) {
			br = (AbstractIntBranchingStrategy) br.getNextBranching();
		}
		br.setNextBranching(branching);
	}



	/**
	 * Set the ilogGoal of the search strategy
	 *
	 * @param ilogGoal
	 *            to take into account in the search strategy
	 */
	public void setIlogGoal(Goal ilogGoal) {
		this.ilogGoal = ilogGoal;
	}

	/**
	 * commands the strategy to start
	 */
	public void launch() {
		// strategy.run();
		ChocoLogging.flushLogs();
		strategy.incrementalRun();
		ChocoLogging.flushLogs();
	}

	/**
	 * returns the number of solutions encountered during the search
	 *
	 * @return the number of solutions to the model that were encountered during
	 *         the search
	 */
	public int getNbSolutions() {
		return strategy.getSolutionCount();
	}


	/**
	 * Monitor the time limit (default to true)
	 *
	 * @param b
	 *            indicates wether the search stategy monitor the time limit
	 *            @deprecated the limit is always monitored
	 */
	@Deprecated
	public void monitorTimeLimit(boolean b) {}


	/**
	 * Monitor the node limit (default to true)
	 *
	 * @param b
	 *            indicates wether the search stategy monitor the node limit
	 *            @deprecated the limit is always monitored
	 */
	@Deprecated
	public void monitorNodeLimit(boolean b) {}

	/**
	 * Monitor the backtrack limit (default to false)
	 *
	 * @param b
	 *            indicates wether the search stategy monitor the backtrack
	 *            limit
	 *            @deprecated the limit is always monitored
	 */
	@Deprecated
	public void monitorBackTrackLimit(boolean b) {}

	/**
	 * Monitor the fail limit (default to false)
	 *
	 * @param b
	 *            indicates wether the search stategy monitor the fail limit
	 */
	public void monitorFailLimit(boolean b) {
		limitConfig.monitorFailLimit(b);
	}

	/**
	 * Sets the time limit i.e. the maximal time before stopping the search
	 * algorithm
	 */
	public void setTimeLimit(int timeLimit) {
		limitConfig.setSearchLimit(Limit.TIME, timeLimit);
	}


	/**
	 * Sets the node limit i.e. the maximal number of nodes explored by the
	 * search algorithm
	 */
	public void setNodeLimit(int nodeLimit) {
		limitConfig.setSearchLimit(Limit.NODE, nodeLimit);
	}

	/**
	 * Sets the backtrack limit i.e. the maximal number of backtracks explored
	 * by the search algorithm
	 */
	public void setBackTrackLimit(int backTrackLimit) {
		limitConfig.setSearchLimit(Limit.BACKTRACK, backTrackLimit);
	}

	/**
	 * Sets the fail limit i.e. the maximal number of fail explored by the
	 * search algorithm
	 */
	public void setFailLimit(int failLimit) {
		limitConfig.setSearchLimit(Limit.FAIL, failLimit);
	}

	/**
	 * Sets the restart limit i.e. the maximal number of restart performed during the search algorithm.
	 * The limit does not stop the search only the restart process.
	 */
	public void setRestartLimit(int restartLimit) {
		limitConfig.setRestartLimit(Limit.RESTART, restartLimit);
	}


	@Override
	public int getLimitCount(Limit type) {
		return strategy.getSearchMeasures().getLimitCount(type);
	}

	/**
	 * Get the time count of the search algorithm
	 *
	 * @return time count
	 */
	public int getTimeCount() {
		return strategy.getSearchMeasures().getTimeCount();
	}


	/**
	 * Get the node count of the search algorithm
	 *
	 * @return node count
	 */
	public int getNodeCount() {
		return strategy.getSearchMeasures().getNodeCount();
	}

	/**
	 * Get the backtrack count of the search algorithm
	 *
	 * @return backtrack count
	 */
	public int getBackTrackCount() {
		return strategy.getSearchMeasures().getBackTrackCount();
	}

	/**
	 * Get the fail count of the search algorithm
	 *
	 * @return fail count
	 */
	public int getFailCount() {
		return strategy.getSearchMeasures().getFailCount();
	}


	@Override
	public int getRestartCount() {
		return strategy.getSearchMeasures().getRestartCount();
	}

	@Override
	public int getSolutionCount() {
		return strategy.getSolutionCount();
	}

	@Override
	public Number getObjectiveValue() {
		if (strategy instanceof AbstractOptimize) {
			return ( (AbstractOptimize) strategy).getObjectiveValue();
		}
		return (Number) null;
	}

	@Override
	public boolean isObjectiveOptimal() {
		return existsSolution() &&  !firstSolution && !isEncounteredLimit();
	}

	@Override
	public boolean existsSolution() {
		return strategy.existsSolution();
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
	 * Sets a unique integer variable selector the search olver should use.
	 *
	 * @see choco.cp.solver.CPSolver#addGoal(choco.kernel.solver.branch.AbstractIntBranchingStrategy)
	 * @see choco.cp.solver.CPSolver#attachGoal(choco.kernel.solver.branch.AbstractIntBranchingStrategy)
	 */
	public void setVarIntSelector(VarSelector varSelector) {
		// To remove properly the listener from the Propagation engine
		if(this.varIntSelector!=null && this.varIntSelector instanceof PropagationEngineListener){
			((PropagationEngineListener)this.varIntSelector).safeDelete();
		}
		this.varIntSelector = varSelector;
		IntDomainVar[] vars = ((AbstractIntVarSelector) varSelector).getVars();
		if (vars != null) {
			intDecisionVars.clear();
			intDecisionVars.addAll(Arrays.asList(vars));
		} else if(!intDecisionVars.isEmpty()){
			vars = new IntDomainVar[intDecisionVars.size()];
			intDecisionVars.toArray(vars);
			((AbstractIntVarSelector) varSelector).setVars(vars);
		}else{
			intDecisionVars.addAll(intVars.toList());
		}
	}

	/**
	 * Sets a unique real variable selector the search strategy should use.
	 *
	 * @see choco.cp.solver.CPSolver#addGoal(choco.kernel.solver.branch.AbstractIntBranchingStrategy)
	 * @see choco.cp.solver.CPSolver#attachGoal(choco.kernel.solver.branch.AbstractIntBranchingStrategy)
	 */
	public void setVarRealSelector(RealVarSelector realVarSelector) {
		this.varRealSelector = realVarSelector;
		floatDecisionVars.addAll(floatVars.toList());
	}

	/**
	 * Sets unique set variable selector the search strategy should use.
	 *
	 * @see choco.cp.solver.CPSolver#addGoal(choco.kernel.solver.branch.AbstractIntBranchingStrategy)
	 * @see choco.cp.solver.CPSolver#attachGoal(choco.kernel.solver.branch.AbstractIntBranchingStrategy)
	 */
	public void setVarSetSelector(SetVarSelector setVarSelector) {
		this.varSetSelector = setVarSelector;
		SetVar[] vars = ((AbstractSetVarSelector) setVarSelector).getVars();
		if (vars != null) {
			setDecisionVars.clear();
			setDecisionVars.addAll(Arrays.asList(vars));
		} else if(!setDecisionVars.isEmpty()){
			vars = new SetVar[setDecisionVars.size()];
			setDecisionVars.toArray(vars);
			((AbstractSetVarSelector) setVarSelector).setVars(vars);
		} else {
			setDecisionVars.addAll(setVars.toList());
		}
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
	public void setValSetSelector(SetValSelector setValIntSelector) {
		this.valSetSelector = setValIntSelector;
	}


	public void cancelRestartConfiguration() {
		limitConfig.setRestartStrategyLimitType(null); //set default;
		restartConfig.cancelRestarts();
	}

	/**
	 * Perform a search with restarts regarding the number of backtrack. An
	 * initial allowed number of backtrack is given (parameter base) and once
	 * this limit is reached a restart is performed and the new limit imposed to
	 * the search is increased by multiplying the previous limit with the
	 * parameter grow. Restart strategies makes really sense with strategies
	 * that make choices based on the past experience of the search :
	 * DomOverWdeg or Impact based search. It could also be used with a random
	 * heuristic
	 *
	 * @param base
	 *            : the initial number of fails limiting the first search
	 */
	public void setGeometricRestart(int base) {
		restartConfig.setGeometricalRestartPolicy(base, 1.2);
	}
	/**
	 * Perform a search with restarts regarding the number of backtrack. An
	 * initial allowed number of backtrack is given (parameter base) and once
	 * this limit is reached a restart is performed and the new limit imposed to
	 * the search is increased by multiplying the previous limit with the
	 * parameter grow. Restart strategies makes really sense with strategies
	 * that make choices based on the past experience of the search :
	 * DomOverWdeg or Impact based search. It could also be used with a random
	 * heuristic
	 *
	 * @param base
	 *            : the initial number of fails limiting the first search
	 * @param grow
	 *            : the limit in number of fails grows at each restart by grow *
	 *            base;
	 */
	public void setGeometricRestart(int base, double grow) {
		restartConfig.setGeometricalRestartPolicy(base, grow);
	}

	/**
	 * Perform a search with restarts regarding the number of backtrack. An
	 * initial allowed number of backtrack is given (parameter base) and once
	 * this limit is reached a restart is performed and the new limit imposed to
	 * the search is increased by multiplying the previous limit with the
	 * parameter grow. the strategy restart until the number of restart is equal
	 * to the restartLimit Restart strategies makes really sense with strategies
	 * that make choices based on the past experience of the search :
	 * DomOverWdeg or Impact based search. It could also be used with a random
	 * heuristic
	 *
	 * @param base
	 *            : the initial number of fails limiting the first search
	 * @param grow
	 *            : the limit in number of fails grows at each restart by grow *
	 *            base;
	 * @param restartLimit
	 *            the maximum number of restarts
	 */
	public void setGeometricRestart(int base, double grow, int restartLimit) {
		restartConfig.setGeometricalRestartPolicy(base, grow);
		limitConfig.setRestartLimit(Limit.RESTART, restartLimit);
	}


	/**
	 * Perform a search with restarts regarding the number of backtrack. One way
	 * to describe this strategy is to say that all run lengths are power of
	 * two, and that each time a pair of runs of a given length has been
	 * completed, a run of twice that length is immediatly executed. <br>
	 * example with growing factor of 2 : [1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1,
	 * 2, 4, 8, 1,...] <br>
	 * <br>
	 * example with growing factor of 3 : [1, 1, 1, 3, 1, 1, 1, 3, 9,...] <br>
	 * the limit is length * base.
	 *
	 * Restart strategies makes really sense with strategies that make choices
	 * based on the past experience of the search : DomOverWdeg or Impact based
	 * search. It could also be used with a random heuristic
	 *
	 * @param base
	 *            : the initial number of fails limiting the first search
	 * @param grow
	 *            : the geometrical factor for Luby restart strategy
	 * @param restartLimit
	 *            the maximum number of restarts
	 */
	public void setLubyRestart(int base, int grow, int restartLimit) {
		restartConfig.setLubyRestartPolicy(base, grow);
		limitConfig.setRestartLimit(Limit.RESTART, restartLimit);
	}

	/**
	 * Perform a search with restarts regarding the number of backtrack. One way
	 * to describe this strategy is to say that all run lengths are power of
	 * two, and that each time a pair of runs of a given length has been
	 * completed, a run of twice that length is immediatly executed. <br>
	 * example with growing factor of 2 : [1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1,
	 * 2, 4, 8, 1,...] <br>
	 * <br>
	 * example with growing factor of 3 : [1, 1, 1, 3, 1, 1, 1, 3, 9,...] <br>
	 * the limit is length * base.
	 *
	 * Restart strategies makes really sense with strategies that make choices
	 * based on the past experience of the search : DomOverWdeg or Impact based
	 * search. It could also be used with a random heuristic
	 *
	 * @param base
	 *            : the initial number of fails limiting the first search
	 * @param grow
	 *            : the geometrical factor for Luby restart strategy
	 */
	public void setLubyRestart(int base, int grow) {
		restartConfig.setLubyRestartPolicy(base, grow);
	}

	/**
	 * default growing factor:2
	 *
	 * @param base scale factor
	 */
	public void setLubyRestart(int base) {
		restartConfig.setLubyRestartPolicy(base, 2);
	}


	public final boolean isRecordingNogoodFromRestart() {
		return restartConfig.isRecordNogoodFromRestart();
	}

	public final void setRecordNogoodFromRestart(boolean recordNogoodFromRestart) {
		restartConfig.setRecordNogoodFromRestart(recordNogoodFromRestart);
	}

	/**
	 * set the optimization strategy: - restart or not after each solution found
	 *
	 * @param restart indicates wether to restart or not
	 */
	public void setRestart(boolean restart) {
		restartConfig.setRestartAfterEachSolution(restart);
	}

	/**
	 * a boolean indicating if the strategy minize or maximize the objective
	 * function
	 *
	 * @param doMaximize indicates wether the strategy is maximizing or not (minimizing)
	 */
	public void setDoMaximize(boolean doMaximize) {
		this.doMaximize = doMaximize;
	}

	/**
	 * Set the variable to optimize
	 *
	 * @param objective objective variable
	 */
	public final void setObjective(Var objective) {
		this.objective = objective;
	}
	
	@Override
	public Var getObjective() {
		return objective;
	}

	@Override
	public boolean isOptimizationSolver() {
		return objective != null && ( strategy == null || strategy instanceof AbstractOptimize);
	}

	@Deprecated
	public Number getOptimumValue() {
		//		if (strategy instanceof AbstractOptimize) {
		//			return ((AbstractOptimize) strategy).getBestObjectiveValue();
		//		} else if (strategy instanceof AbstractRealOptimize) {
		//			return ((AbstractRealOptimize) strategy).getBestObjectiveValue();
		//		}
		//		return null;
		return getObjectiveValue();
	}




	public final SchedulerConfig getScheduler() {
		return scheduler;
	}

	public final void setHorizon(int horizon) {
		scheduler.setMakespan(horizon);
	}

	public final IntDomainVar getMakespan() {
		return scheduler.getMakespan();
	}

	public final int getMakespanValue() {
		return scheduler.getMakespanValue();
	}

	public final void postMakespanConstraint() {
		if (getMakespan()!= null) {
			// create makespan constraint : makespan = max (end(T)
			IntDomainVar[] vars = new IntDomainVar[getNbTaskVars() + 1];
			vars[0] = getMakespan();
			for (int i = 0; i < getNbTaskVars(); i++) {
				vars[i + 1] = getTaskVar(i).end();
			}
			post(new MaxOfAList(vars));
		}
	}

	/**
	 * Post the redundant constraint that captures the reasonnings on tasks consistency.
	 */
	public final void postRedundantTaskConstraints() {
		if (scheduler.isRedundantReasonningsOnTasks()) {
			for (int i = 0; i < getNbTaskVars(); i++) {
				postRedundantTaskConstraint(getTaskVar(i));
			}
		} else {
			//FIXME update because of precedence constraints
			//linear prec do not ensure task consistency but are counted ?
			//if there is a prec network then it is ok
			for (int i = 0; i < getNbTaskVars(); i++) {
				TaskVar t = getTaskVar(i);
				if (t.getNbConstraints() == 0) {
					postRedundantTaskConstraint(getTaskVar(i));
				}
			}
		}
		postMakespanConstraint();
	}

	protected void postRedundantTaskConstraint(TaskVar t) {
		// we must enforce the task consistency
		if (t.duration().isInstantiatedTo(0)) {
			// nil duration
			if (!t.start().equals(t.end())) {
				// not fictive
				post(eq(t.start(), t.end()));
			}
		} else {
			// s + d = e
			post(eq(plus(t.start(), t.duration()), t.end()));
		}
	}

	/**
	 * Checks if a limit has been encountered
	 */
	public boolean isEncounteredLimit() {
		return strategy.isEncounteredLimit();
	}

	/**
	 * If a limit has been encounteres, return the involved limit
	 */
	public AbstractGlobalSearchLimit getEncounteredLimit() {
		return strategy.getEncounteredLimit();
	}

	/**
	 * Returns the propagation engine associated to the model
	 */

	public PropagationEngine getPropagationEngine() {
		return propagationEngine;
	}

	protected <E> List<E> getDecisionList(List<E> decisions, List<E> all) {
		return Collections.unmodifiableList(decisions.isEmpty() ? all
				: decisions);
	}

	/**
	 * get the list of decision integer variables.
	 *
	 * @return an unmodifiable list
	 */
	public final List<IntDomainVar> getIntDecisionVars() {
		return getDecisionList(intDecisionVars, intVars.toList());
	}

	/**
	 * get the list of decision set variables.
	 *
	 * @return an unmodifiable list
	 */
	public final List<SetVar> getSetDecisionVars() {
		return getDecisionList(setDecisionVars, setVars.toList());

	}

	/**
	 * get the list of decision real variables.
	 *
	 * @return an unmodifiable list
	 */
	public final List<RealVar> getRealDecisionVars() {
		return getDecisionList(floatDecisionVars, floatVars.toList());
	}

	/**
	 * get the list of decision task variables.
	 *
	 * @return an unmodifiable list
	 */
	public final List<TaskVar> getTaskDecisionVars() {
		return getDecisionList(taskDecisionVars, taskVars.toList());
	}

	/**
	 * <i>Network management:</i> Retrieve a variable by its index (all integer
	 * variables of the model are numbered in sequence from 0 on)
	 *
	 * @param i
	 *            index of the variable in the model
	 */

	public final IntVar getIntVar(int i) {
		return intVars.get(i);
	}

	/**
	 * Add a integer variable to the integer variables list
	 *
	 * @param v
	 *            the variable to add
	 */
	public final void addIntVar(IntDomainVar v) {
		intVars.add(v);
	}

	public final Var getIntConstant(int i) {
		return intconstantVars.get(i);
	}

	public final void addIntConstant(int value, IntDomainVar i) {
		intconstantVars.put(value, i);
	}

	public final Var getRealConstant(double i) {
		return realconstantVars.get(i);
	}

	public final void addrealConstant(double value, RealIntervalConstant i) {
		realconstantVars.put(value, i);
	}

	public final Collection<Integer> getIntConstantSet() {
		return intconstantVars.keySet();
	}

	public final Collection<Double> getRealConstantSet() {
		return realconstantVars.keySet();
	}

	public final int getNbIntConstants() {
		return intconstantVars.size();
	}

	public final int getNbRealConstants() {
		return realconstantVars.size();
	}

	public final int getNbConstants() {
		return intconstantVars.size() + realconstantVars.size();
	}

	public int getIntVarIndex(IntVar c) {
		return intVars.indexOf(c);
	}

	public int getIntVarIndex(IntDomainVar c) {
		return intVars.indexOf(c);
	}

	/**
	 * retrieving the total number of variables
	 *
	 * @return the total number of variables in the model
	 */
	public final int getNbIntVars() {
		return intVars.size();
	}

	/**
	 * Returns a real variable.
	 *
	 * @param i
	 *            index of the variable
	 * @return the i-th real variable
	 */
	public final RealVar getRealVar(int i) {
		return floatVars.get(i);
	}

	/**
	 * Add a real variable to the real variables list
	 *
	 * @param rv
	 *            the variable to add
	 */
	public final void addRealVar(RealVar rv) {
		floatVars.add(rv);
	}

	/**
	 * Returns the number of variables modelling real numbers.
	 */
	public final int getNbRealVars() {
		return floatVars.size();
	}

	/**
	 * Returns a set variable.
	 *
	 * @param i
	 *            index of the variable
	 * @return the i-th real variable
	 */
	public final SetVar getSetVar(int i) {
		return setVars.get(i);
	}

	/**
	 * Add a set variable to the set variables list
	 *
	 * @param sv
	 *            the variable to add
	 */
	public final void addSetVar(SetVar sv) {
		setVars.add(sv);
	}

	/**
	 * Returns the number of variables modelling real numbers.
	 */
	public final int getNbSetVars() {
		return setVars.size();
	}

	@Override
	public int getNbTaskVars() {
		return taskVars.size();
	}

	@Override
	public TaskVar getTaskVar(int i) {
		return taskVars.get(i);
	}

	/**
	 * retrieving the total number of constraints over integers
	 *
	 * @return the total number of constraints over integers in the model
	 */
	public final int getNbIntConstraints() {
		return this.constraints.size();
	}

	/**
	 * <i>Network management:</i> Retrieve a constraint by its index.
	 *
	 * @param i
	 *            index of the constraint in the model
	 * @deprecated
	 */

	@Deprecated
	public final IntSConstraint getIntConstraint(int i) {
		return (IntSConstraint) constraints.get(i);
	}

	public Iterator<SConstraint> getIntConstraintIterator() {
		return new Iterator<SConstraint>() {
			DisposableIntIterator it = constraints.getIndexIterator();

			public boolean hasNext() {
				return it.hasNext();
			}

			public Propagator next() {
				return constraints.get(it.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * currentElement if the model has been found to be feasible (there exist
	 * solutions) or not. precondition : has to be called after a search
	 *
	 * @return Boolean.TRUE if a solution was found, Boolean.FALSE if the model
	 *         was proven infeasible, null otherwise
	 */
	public final Boolean isFeasible() {
		return feasible;
	}

	public final boolean isConsistent() {
		Iterator<SConstraint> ctit = this.getIntConstraintIterator();
		while (ctit.hasNext()) {
			if (!((Propagator) (ctit.next())).isConsistent()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all the variables are instantiated.
	 * @return indicates wether every integer variables are instantiated
	 */
	public boolean isCompletelyInstantiated() {
		int n = getNbIntVars();
		for (int i = 0; i < n; i++) {
			if (!(getIntVar(i).isInstantiated())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * removes (permanently) a constraint from the constraint network Beware,
	 * this is a permanent removal, it may not be backtracked Warnin : For a
	 * composition of constraint by boolean connectors, only the root constraint
	 * may be removed
	 */
	public void eraseConstraint(SConstraint c) {
		constraints.remove(c);
		((AbstractSConstraint) c).setPassive();
		for (int i = 0; i < c.getNbVars(); i++) {
			AbstractVar v = (AbstractVar) c.getVar(i);
			v.eraseConstraint(c);
		}
	}

	/**
	 * <i>Network management:</i> adding a constraint to the model. Note that
	 * this does not propagate anything ! This addition of a constraint is local
	 * to the current search (sub)tree: the constraint will be un-posted upon
	 * backtracking
	 *
	 * @param cc
	 *            the constraint to add
	 */

	public void post(SConstraint cc) {
		if (cc instanceof Propagator) {
			if ((!cc.equals(TRUE) || !constraints.contains(TRUE))
					&& (!cc.equals(FALSE) || !constraints.contains(FALSE))) {
				// avoid adding the TRUE or FALSE constraint more than once
				Propagator c = (Propagator) cc;
				c.setSolver(this);
				constraints.add(c);
				c.addListener(true);
				ConstraintEvent event = (ConstraintEvent) c.getEvent();
				PropagationEngine pe = getPropagationEngine();
				pe.registerEvent(event);
				pe.postConstAwake(c, true);
				postRedundantSetConstraints(cc);
			}
		} else if (cc instanceof ExpressionSConstraint) {
			ExpressionSConstraint p = (ExpressionSConstraint) cc;
			p.setScope(this);
			decisionOnExpression(p);
		} else if (cc instanceof MetaSConstraint) {
			MetaSConstraint p = (MetaSConstraint) cc;
			p.addListener(true); //involved tasks record the constraint, useless for propagation
			final int nbc = p.getNbSubConstraints();
			for (int i = 0; i < nbc; i++) {
				post(p.getSubConstraints(i)); //post the subconstraints
			}
		} else {
			throw new SolverException(
			"impossible to post to a Model constraints : " + cc.getClass().getSimpleName());
		}
	}

	public void post(SConstraint... ccs) {
		for (SConstraint cc : ccs) {
			post(cc);
		}
	}

	/**
	 * Decide what kind of constraints to post in case on an Expression: -
	 * Extract the list, build the table and post GAC - Post Gac on the implicit
	 * table - Post FC on the explicit table
	 *
	 * @param exp expressions constraint
	 */
	protected void decisionOnExpression(ExpressionSConstraint exp) {
		Boolean decomp = exp.isDecomposeExp();
		if (decomp != null) {
			if (decomp && exp.checkDecompositionIsPossible()) {
				this.post(exp.getDecomposition(this));
			} else {
				this.post(exp.getExtensionnal(this));
			}
		} else
			// If it is a constant constraint
			if (exp.getNbVars() == 0) {
				if (exp.checkTuple(new int[] {})) {
					this.post(CPSolver.TRUE);
				} else {
					this.post(CPSolver.FALSE);
				}
			} else {
				// todo: put the right default test
				this.post(exp.getExtensionnal(this));
			}
	}

	/**
	 * Post the redundant constraint that allows to capture the reasonnings on
	 * cardinalities
	 *
	 * @param p constraint
	 */
	public void postRedundantSetConstraints(SConstraint p) {
		if (cardinalityReasonningsOnSETS && p instanceof SetSConstraint
				&& p.getNbVars() > 1) {

			if (p instanceof MemberXY) {
				IntDomainVar card0 = ((SetVar) p.getVar(1)).getCard();
				post(geq(card0, 1));
			} else if (p instanceof IsIncluded) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();
				post(leq(card0, card1));
			} else if (p instanceof SetUnion) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();
				IntDomainVar card3 = ((SetVar) p.getVar(2)).getCard();
				post(geq(plus(card0, card1), card3));
			} else if (p instanceof SetIntersection) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();
				IntDomainVar card3 = ((SetVar) p.getVar(2)).getCard();
				post(geq(card0, card3));
				post(geq(card1, card3));
			} else if (p instanceof Disjoint) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();
				int ub = Math.max(((SetVar) p.getVar(0)).getEnveloppeSup(),
						((SetVar) p.getVar(1)).getEnveloppeSup());
				int lb = Math.min(((SetVar) p.getVar(0)).getEnveloppeInf(),
						((SetVar) p.getVar(1)).getEnveloppeInf());
				SetVar z = this.createBoundSetVar("var_inter: " + p, lb, ub);
				setVars.add(z);
				intVars.add(z.getCard());
				post(new SetUnion((SetVar) p.getVar(0), (SetVar) p.getVar(1), z));
				post(eq(plus(card0, card1), z.getCard()));
			}
		}
	}

	/**
	 * <i>Network management:</i> adding a constraint to the model. Note that
	 * this does not propagate anything ! This addition of a constraint is
	 * global: the constraint will NOT be un-posted upon backtracking
	 *
	 * @param cc
	 *            the constraint to add
	 */

	public void postCut(SConstraint cc) {
		if (cc instanceof Propagator) {
			if ((!cc.equals(TRUE) || !constraints.contains(TRUE))
					&& (!cc.equals(FALSE) || !constraints.contains(FALSE))) {
				// avoid adding the TRUE or FALSE constraint more than once
				Propagator c = (Propagator) cc;
				c.setSolver(this);
				int idx = constraints.staticAdd(c);
				indexOfLastInitializedStaticConstraint.set(idx);
				c.addListener(false);
				ConstraintEvent event = (ConstraintEvent) c.getEvent();
				PropagationEngine pe = getPropagationEngine();
				pe.registerEvent(event);
				pe.postConstAwake(c, true);
				if (strategy != null)
					strategy.initMainGoal(cc);
			}

		} else {
			throw new SolverException(
			"impossible to post to a Model cuts that are not Propagators");
		}
	}

	/**
	 * Add a nogood to the solver. This method can be called at any
	 * point in the search (specially at a leaf) and will ensure that
	 * the nogood added is propagated through the remaining search tree.
	 * A nogood is considered as a clause over boolean variables.
	 *
	 * @param poslit : a table of Boolean variables standing for the positive
	 *        literals
	 * @param neglit : a table of Boolean variables standing for the negative
	 *        literals
	 */
	public void addNogood(IntDomainVar[] poslit, IntDomainVar[] neglit) {
		if (nogoodStore == null) {
			nogoodStore = new ClauseStore(getBooleanVariables());
			postCut(nogoodStore);
		}
		nogoodStore.addDynamicClause(poslit,neglit);
		propNogoodWorld = this.getWorldIndex();
		nogoodStore.constAwake(false);
		//put the nogood store last in the static list
	}


	public void initNogoodBase() {
		if (nogoodStore != null) {
			nogoodStore.setActiveSilently();
			nogoodStore.constAwake(false);
		}
	}

	/**
	 * @return the number of boolean variables
	 */
	public int getNbBooleanVars() {
		int cpt = 0;
		for (int i = 0; i < getNbIntVars(); i++) {
			final IntDomainVar v = (IntDomainVar) getIntVar(i);
			if (v.hasBooleanDomain()) cpt++;
		}
		return cpt;
	}

	/**
	 * @return the boolean variables of the problem
	 */
	public IntDomainVar[] getBooleanVariables() {
		ArrayList<IntDomainVar> bvs = new ArrayList<IntDomainVar>();
		for (int i = 0; i < getNbIntVars(); i++) {
			IntDomainVar v = (IntDomainVar) getIntVar(i);
			if (v.hasBooleanDomain()) bvs.add(v);
		}
		IntDomainVar[] boolvars = new IntDomainVar[bvs.size()];
		bvs.toArray(boolvars);
		return boolvars;
	}

	/**
	 * <i>Propagation:</i> Computes consistency on the model (the model may no
	 * longer be consistent since the last propagation because of listeners that
	 * have been posted and variables that have been reduced
	 *
	 * @throws choco.kernel.solver.ContradictionException
	 *
	 */

	public void propagate() throws ContradictionException {
		PropagationEngine pe = getPropagationEngine();
		boolean someEvents = true;
		while (someEvents) {
			EventQueue q = pe.getNextActiveEventQueue();
			if (q != null) {
				q.propagateSomeEvents();
			} else {
				someEvents = false;
			}
		}
		assert (pe.checkCleanState());
		// pe.checkCleanState();
	}

	/**
	 * pushing one world on the stack
	 */
	public void worldPush() {
		environment.worldPush();
	}

	/**
	 * popping one world from the stack: overrides AbstractModel.worldPop
	 * because the Model class adds the notion of static constraints that need
	 * be repropagated upon backtracking
	 */
	public void worldPop() {
		environment.worldPop();
		propagationEngine.flushEvents();
		final int lastStaticIdx = constraints.getLastStaticIndex();
		for (int i = indexOfLastInitializedStaticConstraint.get() + 1; i <= lastStaticIdx; i++) {
			Propagator c = constraints.get(i);
			if (c != null) {
				c.setPassive(); // Set passive to ensure correct first
				// propagation (as in addListerner)
				c.constAwake(true);
			}
		}
		//        indexOfLastInitializedStaticConstraint.set(environment.getWorldIndex());
		//TODO avoid first conditions and test only propNogoodWorld
		if (nogoodStore != null && propNogoodWorld > this.getWorldIndex()) {
			nogoodStore.setActiveSilently();
			nogoodStore.constAwake(false);
			propNogoodWorld = this.getWorldIndex();
		}
	}

	/**
	 * Backtracks to a given level in the search tree.
	 */
	public void worldPopUntil(int n) {
		while (environment.getWorldIndex() > n) {
			worldPop();
		}
	}

	/**
	 * pushing the world during propagation
	 */
	public final void worldPushDuringPropagation() {
		veqCopy = new VarEventQueue[propagationEngine.getVarEventQueues().length];
		for(int q = 0; q < veqCopy.length; q++){
			veqCopy[q] = propagationEngine.getVarEventQueues()[q];
		}
		propagationEngine.setVarEventQueues(this.eventQueueType);

		ceqCopy = new ConstraintEventQueue[propagationEngine.getConstraintEventQueues().length];
		for(int q = 0; q < ceqCopy.length; q++){
			ceqCopy[q] = propagationEngine.getConstraintEventQueues()[q];
			propagationEngine.getConstraintEventQueues()[q] = new ConstraintEventQueue(propagationEngine);
		}
		environment.worldPush();
	}

	/**
	 * poping the world during propagation
	 */
	public final void worldPopDuringPropagation() {
		environment.worldPop();
		propagationEngine.flushEvents();
		propagationEngine.setVarEventQueues(veqCopy);
		propagationEngine.setConstraintEventQueues(ceqCopy);
		veqCopy = null;
		ceqCopy = null;
	}

	/**
	 * returning the index of the current worl
	 */
	public int getWorldIndex() {
		return environment.getWorldIndex();
	}

	public Boolean solve(boolean all) {
		setFirstSolution(!all);
		generateSearchStrategy();
		launch();
		return isFeasible();
	}

	public Boolean solve() {
		return solve(false);
	}

	public Boolean solveAll() {
		return solve(true);
	}

	public Boolean nextSolution() {
		return getSearchStrategy().nextSolution();
	}

    /**
	 * Solution checker. Usefull for debug and development.
     * Check also constraints with not instantiated variables
	 *
     * @param logged pretty print
	 * @return a boolean indicating wether the solution is correct or not.
	 */
	public Boolean checkSolution(boolean logged) {
        return checkSolution(logged, true);
    }

	/**
	 * Solution checker. Usefull for debug and development.
	 *
     * @param logged pretty print
     * @param enableConsistency check also constraints with not instantiated variables
	 * @return a boolean indicating wether the solution is correct or not.
	 */
	public Boolean checkSolution(boolean logged, boolean enableConsistency) {
		Boolean isSolution = true;
		StringBuffer st = new StringBuffer("~~~~~SOLUTION CHECKER~~~~~")
		.append("\n");
		st.append("(check wether every constraints define isSatisfied())")
		.append("\n");

		// Check variables
        isSolution &= checkDecisionVariables(st);

        // Check constraints
        isSolution &= checkConstraints(st, enableConsistency);

		st.append("\n");
		if (isSolution) {
			st.append("This solution satisfies every constraints.");
		} else {
			st.append("One or more constraint is not satisfied.").append("\n")
			.append("Or one or more constraint is not consistent.\n")
            .append("Or the search is not finished.");
		}

		st.append("\n").append("~~~~~~~~~~~~~~~~~~~~~~~~~~").append("\n");
		if(logged)LOGGER.log(Level.FINE, st.toString());
		return isSolution;
	}

    /**
	 * Check wether every decisions variables are instantiated
	 *
	 * @return true if all variables are instantiated
	 */
	public boolean checkDecisionVariables() {
        return checkDecisionVariables(new StringBuffer());
    }

    /**
	 * Check wether every decisions variables are instantiated
	 *
     * @param st a stringbuffer to get the log
	 * @return true if all variables are instantiated
	 */
	private boolean checkDecisionVariables(StringBuffer st) {
        boolean isOk = true;
		if (intDecisionVars != null) {
			for (IntDomainVar intDecisionVar : intDecisionVars) {
				if (!intDecisionVar.isInstantiated()) {
                    st.append(StringUtils.pad(""+intDecisionVar.getName(), 100, ".")).append("ko <= WARNING!\n");
					isOk = false;
				}else{
                    st.append(StringUtils.pad(""+intDecisionVar.getName(), 100, ".")).append("ok\n");
                }
			}
		}

		if (setDecisionVars != null) {
			for (SetVar setDecisionVar : setDecisionVars) {
				if (!setDecisionVar.isInstantiated()) {
                    st.append(StringUtils.pad(""+setDecisionVar.getName(), 100, ".")).append("ko <= WARNING!\n");
					isOk = false;
				}else{
                    st.append(StringUtils.pad(""+setDecisionVar.getName(), 100, ".")).append("ok\n");
                }
			}
		}

		if (floatDecisionVars != null) {
			for (RealVar floatDecisionVar : floatDecisionVars) {
				if (!floatDecisionVar.isInstantiated()) {
                    st.append(StringUtils.pad(""+floatDecisionVar.getName(), 100, ".")).append("ko <= WARNING!\n");
					isOk = false;
				}else{
                    st.append(StringUtils.pad(""+floatDecisionVar.getName(), 100, ".")).append("ok\n");
                }
			}
		}
		return isOk;
	}

    /**
     * Check the constraints
     * @param enableConsistency check also constraints with not instantiated variables
     * @return boolean
     */
    public boolean checkConstraints(boolean enableConsistency){
        return checkConstraints(new StringBuffer(), enableConsistency);
    }

    /**
     * Check the constraints
     * @param st a string buffer for the log
     * @param enableConsistency check also constraints with not instantiated variables
     * @return boolean
     */
    private boolean checkConstraints(StringBuffer st, boolean enableConsistency){
        boolean isOk = true;
        // Checck constraints
		Iterator<SConstraint> ctit = this.getIntConstraintIterator();
		while (ctit.hasNext()) {
			SConstraint c = ctit.next();
            if(c instanceof AbstractIntSConstraint){
                AbstractIntSConstraint ic = (AbstractIntSConstraint)c;
                int[] tuple = new int[c.getNbVars()];
                int[] tupleL = new int[c.getNbVars()];
                int[] tupleU = new int[c.getNbVars()];
                boolean fullInstantiated = true;
                for(int i = 0; i < c.getNbVars(); i++){
                    IntDomainVar v = (IntDomainVar)c.getVar(i);
                    if(v.isInstantiated()){
                        tuple[i] = v.getVal();
                        tupleL[i] = tuple[i];
                        tupleU[i] = tuple[i];
                    }else{
                        fullInstantiated = false;
                        tupleL[i] = v.getInf();
                        tupleU[i] = v.getSup();
                    }
                }
                if(fullInstantiated){
                    if(ic.isSatisfied(tuple)){
                        st.append(StringUtils.pad(""+ic.pretty(), 200, ".")).append("ok\n");
                    }else{
                        st.append(StringUtils.pad(""+c.pretty(), 200, ".")).append("ko <= WARNING!\n");
				        isOk = false;
                    }
                }else if(enableConsistency){
                    if(ic.isSatisfied(tupleL) && ic.isSatisfied(tupleU)){
                        st.append(StringUtils.pad(""+ic.pretty(), 200, ".")).append("ok\n");
                    }else{
                        st.append(StringUtils.pad(""+c.pretty(), 200, ".")).append("ko <= WARNING!\n");
                        st.append("=> NOT CONSISTENT\n");
				        isOk = false;
                    }
                }
            }else{
                if(c.isSatisfied()){
                    st.append(StringUtils.pad(""+c.pretty(), 200, ".")).append("ok\n");
                }else{
                    st.append(StringUtils.pad(""+c.pretty(), 200, ".")).append("ko <= WARNING!\n");
                    isOk = false;
                }
		    }
        }
        return isOk;
    }

	/**
	 * Displays all the runtime statistics.
	 */
	public void printRuntimeSatistics() {
		getSearchStrategy().printRuntimeStatistics();
	}

	public String runtimeSatistics() {
		return getSearchStrategy().runtimeStatistics();
	}

	/**
	 * <i>Resolution:</i> Searches for the solution minimizing the objective
	 * criterion.
	 *
	 * @param obj
	 *            The variable modelling the optimization criterion
	 * @param restart
	 *            If true, then a new search is restarted from scratch after
	 *            each solution is found; otherwise a single branch-and-bound
	 *            search is performed
	 */

	public Boolean minimize(Var obj, boolean restart) {
		return optimize(false, obj, restart);
	}

	public Boolean minimize(boolean restart) {
		if (this.objective == null) {
			throw new SolverException("No objective variable defined");
		}
		return optimize(false, this.objective, restart);
	}

	/**
	 * <i>resolution:</i> Searches for the solution maximizing the objective
	 * criterion.
	 *
	 * @param obj
	 *            The variable modelling the optimization criterion
	 * @param restart
	 *            If true, then a new search is restarted from scratch after
	 *            each solution is found; otherwise a single branch-and-bound
	 *            search is performed
	 */
	public Boolean maximize(Var obj, boolean restart) {
		return optimize(true, obj, restart);
	}

	public Boolean maximize(boolean restart) {
		if (this.objective == null) {
			throw new SolverException("No objective variable defined");
		}
		return optimize(true, this.objective, restart);
	}

	protected Boolean optimize(boolean maximize, Var obj, boolean restart) {
		setDoMaximize(maximize);
		setObjective(obj);
		setRestart(restart);
		setFirstSolution(false);
		generateSearchStrategy();
		launch();
		return this.isFeasible();
	}

	public final void setMinimizationObjective(IntVar obj) {
		objective = obj;
		doMaximize = false;
	}

	public final void setMaximizationObjective(IntVar obj) {
		objective = obj;
		doMaximize = true;
	}

	public final boolean useRecomputation() {
		return recomputationGap > 1;
	}

	public final void setRecomputation(boolean on) {
		recomputationGap = on ? 10 : 1;
	}


	public final int getRecomputationGap() {
		return recomputationGap;
	}

	public final void setRecomputationGap(int recomputationGap) {
		this.recomputationGap = recomputationGap;
	}

	@SuppressWarnings("unchecked")
	public <V extends Var> V[] getVar(Class<?> c, Variable... v) {
		V[] tmp = (V[]) Array.newInstance(c, v.length);
		for (int i = 0; i < v.length; i++) {
			//noinspection unchecked
			tmp[i] = (V) mapvariables.get(v[i].getIndex());
		}
		return tmp;
	}


	public Var getVar(Variable v) {
		return mapvariables.get(v.getIndex());
	}

	public Var[] getVar(Variable... v) {
		return getVar(Variable.class, v);

	}

	public IntDomainVar getVar(IntegerVariable v) {
		return (IntDomainVar) mapvariables.get(v.getIndex());
	}

	public IntDomainVar[] getVar(IntegerVariable... v) {
		return getVar(IntDomainVar.class, v);
	}

	public RealVar getVar(RealVariable v) {
		return (RealVar) mapvariables.get(v.getIndex());
	}

	public RealVar[] getVar(RealVariable... v) {
		return getVar(RealVar.class, v);
	}

	public SetVar getVar(SetVariable v) {
		return (SetVar) mapvariables.get(v.getIndex());
	}

	public SetVar[] getVar(SetVariable... v) {
		return getVar(SetVar.class, v);
	}

	public TaskVar getVar(TaskVariable v) {
		return (TaskVar) mapvariables.get(v.getIndex());
	}

	public TaskVar[] getVar(TaskVariable... v) {
		return getVar(TaskVar.class, v);
	}


	public SConstraint getCstr(Constraint ic) {
		return mapconstraints.get(ic.getIndex());
	}

	public void setCardReasoning(boolean creas) {
		cardinalityReasonningsOnSETS = creas;
	}

	/**
	 * post redundant task constraints start + duration =end
	 * @param treas redundant reasonning on tasks
	 */
	public void setTaskReasoning(boolean treas) {
		scheduler.setRedundantReasonningsOnTasks(treas);
	}

	/**
	 * Record a solution by getting every variables' value.
	 */
	@Override
	public final Solution recordSolution() {
		final Solution sol = new Solution(this);
		strategy.writeSolution(sol);
		return sol;
	}


	/**
	 * Record a solution by getting every variables' value.
	 */
	@Override
	public void restoreSolution(Solution sol) {
		try {
			// Integer variables
			int nbv = getNbIntVars();
			for (int i = 0; i < nbv; i++) {
				if (sol.getIntValue(i) != Integer.MAX_VALUE) {
					getIntVar(i).setVal(sol.getIntValue(i));
				}
			}

			// Set variables
			nbv = getNbSetVars();
			for (int i = 0; i < nbv; i++) {
				getSetVar(i).setVal(sol.getSetValue(i));
			}

			// Real variables
			nbv = getNbRealVars();
			for (int i = 0; i < nbv; i++) {
				getRealVar(i).intersect(sol.getRealValue(i));
			}
			if (Choco.DEBUG) {
				if (nogoodStore != null)
					nogoodStore.setPassive();
				ChocoLogging.flushLogs();
				propagate();
				if (nogoodStore != null)
					nogoodStore.setActive();
				// Iterator<Propagator> ctit =
				// solver.getIntConstraintIterator();
				// while (ctit.hasNext()) {
				// SConstraint c = ctit.next();
				// if(!c.isSatisfied()){
				// throw(new
				// SolverException("Restored solution not consistent !!"));
				// }
				// }
			}
		} catch (ContradictionException e) {
			LOGGER.severe("BUG in restoring solution !!");
			throw (new SolverException("Restored solution not consistent !!"));
			// TODO : � voir comment g�rer les erreurs en g�n�ral
		}
	}


	// **********************************************************************
	// LOGGERS MANAGEMENT
	// **********************************************************************

	public static final int SILENT = 0;
	public static final int SOLUTION = 1;
	public static final int SEARCH = 2;
	public static final int PROPAGATION = 3;
	public static final int FINEST = 4;


	/**
	 * use {@link ChocoLogging#setVerbosity(Verbosity)}
	 * @param verbosity logger verbosity
	 */
	@Deprecated
	public static void setVerbosity(int verbosity) {
		switch (verbosity) {
		case SOLUTION:
			ChocoLogging.setVerbosity(Verbosity.SOLUTION);break;
		case SEARCH:
			ChocoLogging.setVerbosity(Verbosity.SEARCH);break;
		case PROPAGATION:
			ChocoLogging.setVerbosity(Verbosity.FINEST);	break;
		case FINEST:
			ChocoLogging.setVerbosity(Verbosity.FINEST);break;
		case SILENT:
			ChocoLogging.setVerbosity(Verbosity.SILENT);break;
		default:
			ChocoLogging.setVerbosity(Verbosity.SILENT);break;
		}
	}

	/**
	 * use {@link ChocoLogging#flushLogs()}
	 */
	@Deprecated
	public static void flushLogs() {
		ChocoLogging.flushLogs();
	}


	// All abstract methods for constructing constraint
	// that need be defined by a Model implementing a model

	// ***********************************************************************
	// VARIABLES DECLARATION
	// ***********************************************************************

	public IntDomainVar createIntVar(String name, int domainType, int min,
			int max) {
		IntDomainVar v = new IntDomainVarImpl(this, name, domainType, min, max);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createBooleanVar(String name) {
		IntDomainVar v = new BooleanVarImpl(this, name);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createEnumIntVar(String name, int min, int max) {
		IntDomainVar v = new IntDomainVarImpl(this, name, IntDomainVar.BITSET,
				min, max);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createBoundIntVar(String name, int min, int max) {
		IntDomainVar v = new IntDomainVarImpl(this, name, IntDomainVar.BOUNDS,
				min, max);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createBinTreeIntVar(String name, int min, int max) {
		IntDomainVar v = new IntDomainVarImpl(this, name,
				IntDomainVar.BINARYTREE, min, max);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createListIntVar(String name, int min, int max) {
		IntDomainVar v = new IntDomainVarImpl(this, name,
				IntDomainVar.LINKEDLIST, min, max);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createListIntVar(String name, int[] sortedValues) {
		IntDomainVar v = new IntDomainVarImpl(this, name,
				IntDomainVar.LINKEDLIST, sortedValues);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createEnumIntVar(String name, int[] sortedValues) {
		IntDomainVar v = new IntDomainVarImpl(this, name, IntDomainVar.BITSET,
				sortedValues);
		intVars.add(v);
		return v;
	}

	public IntDomainVar createBinTreeIntVar(String name, int[] sortedValues) {
		IntDomainVar v = new IntDomainVarImpl(this, name,
				IntDomainVar.BINARYTREE, sortedValues);
		intVars.add(v);
		return v;
	}

	public RealVar createRealVal(String name, double min, double max) {
		RealVarImpl v = new RealVarImpl(this, name, min, max, RealVar.BOUNDS);
		floatVars.add(v);
		return v;
	}

	public RealIntervalConstant createRealIntervalConstant(double a, double b) {
		return new RealIntervalConstant(a, b);
	}

	/**
	 * Makes a constant interval from a double d ([d,d]).
	 */
	public RealIntervalConstant cst(double d) {
		return createRealIntervalConstant(d, d);
	}

	/**
	 * Makes a constant interval between two doubles [a,b].
	 */
	public RealIntervalConstant cst(double a, double b) {
		return createRealIntervalConstant(a, b);
	}

	public SetVar createSetVar(String name, int a, int b, IntDomainVar card) {
		SetVar s = new SetVarImpl(this, name, a, b, card);
		setVars.add(s);
		post(new SetCard(s, s.getCard(), true, true)); // post |v| = v.getCard()
		return s;
	}

	public SetVar createSetVar(String name, int a, int b, int type) {
		SetVar s = new SetVarImpl(this, name, a, b, null, type);
		setVars.add(s);
		intVars.add(s.getCard());
		post(new SetCard(s, s.getCard(), true, true)); // post |v| = v.getCard()
		return s;
	}

	public SetVar createBoundSetVar(String name, int a, int b) {
		return createSetVar(name, a, b, SetVar.BOUNDSET_BOUNDCARD);
	}

	public SetVar createEnumSetVar(String name, int a, int b) {
		return createSetVar(name, a, b, SetVar.BOUNDSET_ENUMCARD);
	}

	@Override
	public TaskVar createTaskVar(String name, IntDomainVar start,
			IntDomainVar end, IntDomainVar duration) {
		TaskVar t = new TaskVar(this, getNbTaskVars(), name, start, end,
				duration);
		taskVars.add(t);
		return t;
	}

	public IntDomainVar createIntegerConstant(String name, int val) {
		if (intconstantVars.containsKey(val)) {
			return intconstantVars.get(val);
		}
		IntDomainVar v = createIntVar(name, IntDomainVar.BOUNDS, val, val);
		intconstantVars.put(val, v);
		return v;
	}

	public RealIntervalConstant createRealConstant(String name, double val) {
		if (realconstantVars.containsKey(val)) {
			return realconstantVars.get(val);
		}
		RealIntervalConstant v = createRealIntervalConstant(val, val);
		realconstantVars.put(val, v);
		return v;
	}

	// TODO: can be optimized (no need to attach propagation events)
	public IntDomainVar makeConstantIntVar(String name, int val) {
		return createIntegerConstant(name, val);
	}

	public IntDomainVar makeConstantIntVar(int val) {
		return createIntegerConstant("", val);
	}

	// ************************************************************************
	// CONSTRAINTS DECLARATION
	// ************************************************************************

	// /**
	// * Cumulative : Given a set of tasks defined by their starting dates,
	// ending dates, durations and
	// * consumptions/heights, the cumulative ensures that at any time t, the
	// sum of the heights of the tasks
	// * which are executed at time t does not exceed a given limit C (the
	// capacity of the ressource).
	// * The notion of task does not exist yet in choco. The cumulative takes
	// therefore as input three arrays
	// * of integer variables (of same size n) denoting the starting, ending,
	// and duration of each task.
	// * The heights of the tasks are considered constant and given via an array
	// of size n of positive integers.
	// * The last parameter Capa denotes the Capacity of the cumulative (of the
	// ressource).
	// * The implementation is based on the paper of Bediceanu and al :
	// * "A new multi-resource cumulatives constraint with negative heights" in
	// CP02
	// */
	// public SConstraint cumulative(IntDomainVar[] starts, IntDomainVar[] ends,
	// IntDomainVar[] durations, IntDomainVar[] heigths, IntDomainVar capa) {
	// //create tasks;
	// TaskVar[] tasks=new TaskVar[heigths.length];
	// for (int i = 0; i < tasks.length; i++) {
	// tasks[i]=createTaskVar("internal-Task", starts[i], ends[i],
	// durations[i]);
	// }
	// CumulativeSResource rsc=new
	// CumulativeSResource("internal-cumul-resource",
	// capa,tasks,heigths,manager.getDefaultSettings());
	// return new Cumulative(rsc);
	// }
	//
	// public SConstraint cumulative(IntDomainVar[] starts, IntDomainVar[] ends,
	// IntDomainVar[] durations, int[] heigths, int capa) {
	// IntDomainVar[] h = new IntDomainVar[heigths.length];
	// for (int j = 0; j < heigths.length; j++) {
	// h[j] = makeConstantIntVar(heigths[j]);
	// }
	// return cumulative(starts, ends, durations, h, makeConstantIntVar(capa));
	// }
	//
	// // public SConstraint disjunctive(TaskVar[] tasks) {
	// // UnarySResource rsc = new UnarySResource("internal rsc",
	// manager.getDefaultSettings(), tasks);
	// // return new Disjunctive(rsc);
	// // }

	public SConstraint eq(IntExp x, IntExp y) {
		if (x instanceof IntVar && y instanceof IntVar) {
			return new EqualXYC((IntDomainVar) x, (IntDomainVar) y, 0);
		} else if ((x instanceof IntTerm || x instanceof IntVar)
				&& (y instanceof IntTerm || y instanceof IntVar)) {
			return eq(minus(x, y), 0);
		} else if (x == null) {
			return eq(y, 0);
		} else if (y == null) {
			return eq(x, 0);
		} else {
			throw new SolverException("IntExp not a good exp");
		}
	}

	public SConstraint eq(IntExp x, int c) {
		if (x instanceof IntTerm) {
			IntTerm t = (IntTerm) x;
			int nbvars = t.getSize();
			int c2 = c - t.getConstant();
			if (t.getSize() == 1) {
				if(t.getCoefficient(0) == 0){
					if(c2 == 0){
						return TRUE;
					}else{
						return FALSE;
					}
				}
				if (c2 % t.getCoefficient(0) == 0) {
					return new EqualXC((IntDomainVar) t.getVariable(0), c2
							/ t.getCoefficient(0));
				} else {
					return FALSE;
				}
			} else if ((nbvars == 2)
					&& (t.getCoefficient(0) + t.getCoefficient(1) == 0)) {
				return new EqualXYC((IntDomainVar) t.getVariable(0),
						(IntDomainVar) t.getVariable(1), c2
						/ t.getCoefficient(0));
			} else {
				return makeIntLinComb(t.getVariables(), t.getCoefficients(),
						-(c2), IntLinComb.EQ);
			}
		} else if (x instanceof IntVar) {
			return new EqualXC((IntDomainVar) x, c);
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public SConstraint eq(int c, IntExp x) {
		return eq(x, c);
	}

	public SConstraint eq(RealVar r, IntDomainVar i) {
		return new MixedEqXY(r, i);
	}

	public SConstraint eqCard(SetVar s, IntDomainVar i) {
		// return new SetCard(s, i, true, true);
		return eq(s.getCard(), i);
	}

	public SConstraint eqCard(SetVar s, int i) {
		// IntDomainVar var = makeConstantIntVar("cste" + i, i);
		// return new SetCard(s, var, true, true);
		return eq(s.getCard(), i);
	}

	public SConstraint geq(IntExp x, IntExp y) {
		if (x instanceof IntVar && y instanceof IntVar) {
			return new GreaterOrEqualXYC((IntDomainVar) x, (IntDomainVar) y, 0);
		} else if ((x instanceof IntTerm || x instanceof IntVar)
				&& (y instanceof IntTerm || y instanceof IntVar)) {
			return geq(minus(x, y), 0);
		} else if (y == null) {
			return geq(x, 0);
		} else if (x == null) {
			return geq(0, y);
		} else {
			throw new SolverException("IntExp not a good exp");
		}
	}

	/**
	 * Creates a constraint by stating that a term is greater or equal than a
	 * constant
	 *
	 * @param x
	 *            the expression
	 * @param c
	 *            the constant
	 * @return the linear inequality constraint
	 */
	public SConstraint geq(IntExp x, int c) {

		if (x instanceof IntTerm) {
			IntTerm t = (IntTerm) x;
			if ((t.getSize() == 2)
					&& (t.getCoefficient(0) + t.getCoefficient(1) == 0)) {
				if (t.getCoefficient(0) > 0) {
					return new GreaterOrEqualXYC((IntDomainVar) t
							.getVariable(0), (IntDomainVar) t.getVariable(1),
							(c - t.getConstant()) / t.getCoefficient(0));
				} else {
					return new GreaterOrEqualXYC((IntDomainVar) t
							.getVariable(1), (IntDomainVar) t.getVariable(0),
							(c - t.getConstant()) / t.getCoefficient(1));
				}
			} else {
				return makeIntLinComb(((IntTerm) x).getVariables(),
						((IntTerm) x).getCoefficients(), ((IntTerm) x)
						.getConstant()
						- c, IntLinComb.GEQ);
			}
		} else if (x instanceof IntVar) {
			return new GreaterOrEqualXC((IntDomainVar) x, c);
		} else if (x == null) {
			if (c <= 0) {
				return TRUE;
			} else {
				return FALSE;
			}
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public SConstraint geq(int c, IntExp x) {
		if (x instanceof IntTerm) {
			int[] coeffs = ((IntTerm) x).getCoefficients();
			int n = coeffs.length;
			int[] oppcoeffs = new int[n];
			for (int i = 0; i < n; i++) {
				oppcoeffs[i] = -(coeffs[i]);
			}
			return makeIntLinComb(((IntTerm) x).getVariables(), oppcoeffs, c
					- ((IntTerm) x).getConstant(), IntLinComb.GEQ);
		} else if (x instanceof IntVar) {
			return new LessOrEqualXC((IntDomainVar) x, c);
		} else if (x == null) {
			if (c <= 0) {
				return TRUE;
			} else {
				return FALSE;
			}
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public SConstraint geqCard(SetVar sv, IntDomainVar v) {
		// return new SetCard(sv, v, false, true);
		return geq(sv.getCard(), v);
	}

	public SConstraint geqCard(SetVar sv, int v) {
		// IntDomainVar var = makeConstantIntVar("cste" + sv, v);
		// return new SetCard(sv, var, false, true);
		return geq(sv.getCard(), v);
	}

	public SConstraint gt(IntExp x, IntExp y) {
		return geq(minus(x, y), 1);
	}

	public SConstraint gt(IntExp x, int c) {
		return geq(x, c + 1);
	}

	public SConstraint gt(int c, IntExp x) {
		return geq(c - 1, x);
	}

	public SConstraint leq(IntExp v1, IntExp v2) {
		return geq(v2, v1);
	}

	public SConstraint leq(IntExp v1, int v2) {
		return geq(v2, v1);
	}

	public SConstraint leq(int v1, IntExp v2) {
		return geq(v2, v1);
	}

	public SConstraint leqCard(SetVar sv, IntDomainVar i) {
		// return new SetCard(sv, i, true, false);
		return leq(sv.getCard(), i);
	}

	public SConstraint leqCard(SetVar sv, int i) {
		// IntDomainVar var = makeConstantIntVar("cste" + sv, i);
		// return new SetCard(sv, var, true, false);
		return leq(sv.getCard(), i);
	}

	public SConstraint lt(IntExp v1, IntExp v2) {
		return gt(v2, v1);
	}

	public SConstraint lt(IntExp v1, int v2) {
		return gt(v2, v1);
	}

	public SConstraint lt(int v1, IntExp v2) {
		return gt(v2, v1);
	}

	/**
	 * Subtracting two terms one from another
	 *
	 * @param t1
	 *            first term
	 * @param t2
	 *            second term
	 * @return the term (a fresh one)
	 */
	public IntExp minus(IntExp t1, IntExp t2) {
		if (t1 == ZERO) {
			return mult(-1, t2);
		}
		if (t2 == ZERO) {
			return t1;
		}
		if (t1 instanceof IntTerm) {
			if (t2 instanceof IntTerm) {
				int[] coeffs2 = ((IntTerm) t2).getCoefficients();
				int n2 = coeffs2.length;
				int[] oppcoeffs2 = new int[n2];
				for (int i = 0; i < n2; i++) {
					oppcoeffs2[i] = -(coeffs2[i]);
				}
				return plus(((IntTerm) t1).getCoefficients(), ((IntTerm) t1)
						.getVariables(), ((IntTerm) t1).getConstant(),
						oppcoeffs2, ((IntTerm) t2).getVariables(),
						-((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				return plus(((IntTerm) t1).getCoefficients(), ((IntTerm) t1)
						.getVariables(), ((IntTerm) t1).getConstant(),
						new int[] { -1 }, new IntVar[] { (IntVar) t2 }, 0);
			} else {
				throw new SolverException("IntExp not a term, not a var");
			}
		} else if (t1 instanceof IntVar) {
			if (t2 instanceof IntTerm) {
				int[] coeffs2 = ((IntTerm) t2).getCoefficients();
				int n2 = coeffs2.length;
				int[] oppcoeffs2 = new int[n2];
				for (int i = 0; i < n2; i++) {
					oppcoeffs2[i] = -(coeffs2[i]);
				}
				return plus(new int[] { 1 }, new IntVar[] { (IntVar) t1 }, 0,
						oppcoeffs2, ((IntTerm) t2).getVariables(),
						-((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				IntTerm t = new IntTerm(2);
				t.setCoefficient(0, 1);
				t.setCoefficient(1, -1);
				t.setVariable(0, (IntVar) t1);
				t.setVariable(1, (IntVar) t2);
				t.setConstant(0);
				return t;
			} else {
				throw new SolverException("IntExp not a term, not a var");
			}
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public IntExp minus(IntExp t, int c) {
		if (t == ZERO) {
			IntTerm t2 = new IntTerm(0);
			t2.setConstant(-c);
			return t2;
		} else if (t instanceof IntTerm) {
			IntTerm t2 = new IntTerm((IntTerm) t);
			t2.setConstant(((IntTerm) t).getConstant() - c);
			return t2;
		} else if (t instanceof IntVar) {
			IntTerm t2 = new IntTerm(1);
			t2.setCoefficient(0, 1);
			t2.setVariable(0, (IntVar) t);
			t2.setConstant(-c);
			return t2;
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public IntExp minus(int c, IntExp t) {
		if (t instanceof IntTerm) {
			IntTerm t1 = (IntTerm) t;
			int n = t1.getSize();
			IntTerm t2 = new IntTerm(n);
			for (int i = 0; i < n; i++) {
				t2.setCoefficient(i, -t1.getCoefficient(i));
				t2.setVariable(i, t1.getVariable(i));
			}
			t2.setConstant(c - t1.getConstant());
			return t2;
		} else if (t instanceof IntVar) {
			IntTerm t2 = new IntTerm(1);
			t2.setCoefficient(0, -1);
			t2.setVariable(0, (IntVar) t);
			t2.setConstant(c);
			return t2;
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	/**
	 * Adding two terms one to another
	 *
	 * @param t1
	 *            first term
	 * @param t2
	 *            second term
	 * @return the term (a fresh one)
	 */
	public IntExp plus(IntExp t1, IntExp t2) {
		if (t1 == ZERO) {
			return t2;
		}
		if (t2 == ZERO) {
			return t1;
		}
		if (t1 instanceof IntTerm) {
			if (t2 instanceof IntTerm) {
				return plus(((IntTerm) t1).getCoefficients(), ((IntTerm) t1)
						.getVariables(), ((IntTerm) t1).getConstant(),
						((IntTerm) t2).getCoefficients(), ((IntTerm) t2)
						.getVariables(), ((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				return plus(((IntTerm) t1).getCoefficients(), ((IntTerm) t1)
						.getVariables(), ((IntTerm) t1).getConstant(),
						new int[] { 1 }, new IntVar[] { (IntVar) t2 }, 0);
			} else {
				throw new SolverException("IntExp not a term, not a var");
			}
		} else if (t1 instanceof IntVar) {
			if (t2 instanceof IntTerm) {
				return plus(new int[] { 1 }, new IntVar[] { (IntVar) t1 }, 0,
						((IntTerm) t2).getCoefficients(), ((IntTerm) t2)
						.getVariables(), ((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				IntTerm t = new IntTerm(2);
				t.setCoefficient(0, 1);
				t.setCoefficient(1, 1);
				t.setVariable(0, (IntVar) t1);
				t.setVariable(1, (IntVar) t2);
				t.setConstant(0);
				return t;
			} else {
				throw new SolverException("IntExp not a term, not a var");
			}
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public IntExp plus(IntExp t, int c) {
		if (t == ZERO) {
			IntTerm t2 = new IntTerm(0);
			t2.setConstant(c);
			return t2;
		} else if (t instanceof IntTerm) {
			IntTerm t2 = new IntTerm((IntTerm) t);
			t2.setConstant(((IntTerm) t).getConstant() + c);
			return t2;
		} else if (t instanceof IntVar) {
			IntTerm t2 = new IntTerm(1);
			t2.setCoefficient(0, 1);
			t2.setVariable(0, (IntVar) t);
			t2.setConstant(c);
			return t2;
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public final IntExp plus(int c, IntExp t1) {
		return plus(t1, c);
	}

	/**
	 * Utility method for constructing a term from two lists of variables, list
	 * of coeffcicients and constants
	 *
	 * @param coeffs1
	 *            coefficients from the first term
	 * @param vars1
	 *            variables from the first term
	 * @param cste1
	 *            constant from the fisrt term
	 * @param coeffs2
	 *            coefficients from the second term
	 * @param vars2
	 *            variables from the second term
	 * @param cste2
	 *            constant from the second term
	 * @return the term (a fresh one)
	 */
	protected static IntExp plus(int[] coeffs1, IntVar[] vars1, int cste1,
			int[] coeffs2, IntVar[] vars2, int cste2) {
		int n1 = vars1.length;
		int n2 = vars2.length;
		IntTerm t = new IntTerm(n1 + n2);
		for (int i = 0; i < n1; i++) {
			t.setVariable(i, vars1[i]);
			t.setCoefficient(i, coeffs1[i]);
		}
		for (int i = 0; i < n2; i++) {
			t.setVariable(n1 + i, vars2[i]);
			t.setCoefficient(n1 + i, coeffs2[i]);
		}
		t.setConstant(cste1 + cste2);
		return t;
	}

	/**
	 * Creates a simple linear term from one coefficient and one variable
	 *
	 * @param a
	 *            the coefficient
	 * @param x
	 *            the variable
	 * @return the term
	 */
	public IntExp mult(int a, IntExp x) {
		if (a != 0 && x != ZERO) {
			IntTerm t = new IntTerm(1);
			t.setCoefficient(0, a);
			t.setVariable(0, (IntVar) x);
			return t;
		} else {
			return ZERO;
		}
	}

	/**
	 * Creates a constraint by stating that a term is not equal than a constant
	 *
	 * @param x
	 *            the expression
	 * @param c
	 *            the constant
	 * @return the linear disequality constraint
	 */
	public SConstraint neq(IntExp x, int c) {
		if (x instanceof IntTerm) {
			IntTerm t = (IntTerm) x;
			if ((t.getSize() == 2)
					&& (t.getCoefficient(0) + t.getCoefficient(1) == 0)) {
				IntDomainVar v1 = (IntDomainVar) t.getVariable(0);
				IntDomainVar v2 = (IntDomainVar) t.getVariable(1);
				if (v1.hasEnumeratedDomain() && v2.hasEnumeratedDomain()) {
					return new NotEqualXYCEnum(v1, v2, (c - t.getConstant())
							/ t.getCoefficient(0));
				} else {
					return new NotEqualXYC(v1, v2, (c - t.getConstant())
							/ t.getCoefficient(0));
				}
			} else {
				return makeIntLinComb(((IntTerm) x).getVariables(),
						((IntTerm) x).getCoefficients(), -(c), IntLinComb.NEQ);
			}
		} else if (x instanceof IntVar) {
			return new NotEqualXC((IntDomainVar) x, c);
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public SConstraint neq(int c, IntExp x) {
		return neq(x, c);
	}

	public SConstraint neq(IntExp x, IntExp y) {
		if (x instanceof IntTerm) {
			return neq(minus(x, y), 0);
		} else if (x instanceof IntVar) {
			if (y instanceof IntTerm) {
				return neq(minus(x, y), 0);
			} else if (y instanceof IntVar) {
				if (((IntDomainVar) x).hasEnumeratedDomain()
						&& ((IntDomainVar) y).hasEnumeratedDomain()) {
					return new NotEqualXYCEnum((IntDomainVar) x,
							(IntDomainVar) y, 0);
				} else {
					return new NotEqualXYC((IntDomainVar) x, (IntDomainVar) y,
							0);
				}
			} else if (y == null) {
				return neq(x, 0);
			} else {
				throw new SolverException("IntExp not a term, not a var");
			}
		} else if (x == null) {
			return neq(0, y);
		} else {
			throw new SolverException("IntExp not a term, not a var");
		}
	}

	public SConstraint eq(SetVar s1, SetVar s2) {
		return new SetEq(s1, s2);
	}

	public SConstraint neq(SetVar s1, SetVar s2) {
		return new SetNotEq(s1, s2);
	}

	public SConstraint occurence(IntDomainVar[] vars, IntDomainVar occ,
			int value) {
		IntDomainVar[] tmpvars = new IntDomainVar[vars.length + 1];
		System.arraycopy(vars, 0, tmpvars, 0, vars.length);
		tmpvars[tmpvars.length - 1] = occ;
		return new Occurrence(tmpvars, value, true, true);
	}

	// rewriting utility: remove all null coefficients
	// TODO: could be improved to remove duplicates (variables that would appear
	// twice in the linear combination)
	public int countNonNullCoeffs(int[] lcoeffs) {
		int nbNonNull = 0;
		for (int lcoeff : lcoeffs) {
			if (lcoeff != 0) {
				nbNonNull++;
			}
		}
		return nbNonNull;
	}

	protected SConstraint makeIntLinComb(IntVar[] lvars, int[] lcoeffs, int c,
			int linOperator) {
		int nbNonNullCoeffs = countNonNullCoeffs(lcoeffs);
		int nbPositiveCoeffs;
		int[] sortedCoeffs = new int[nbNonNullCoeffs];
		IntVar[] sortedVars = new IntVar[nbNonNullCoeffs];

		int j = 0;
		// fill it up with the coefficients and variables in the right order
		for (int i = 0; i < lvars.length; i++) {
			if (lcoeffs[i] > 0) {
				sortedVars[j] = lvars[i];
				sortedCoeffs[j] = lcoeffs[i];
				j++;
			}
		}
		nbPositiveCoeffs = j;

		for (int i = 0; i < lvars.length; i++) {
			if (lcoeffs[i] < 0) {
				sortedVars[j] = lvars[i];
				sortedCoeffs[j] = lcoeffs[i];
				j++;
			}
		}
		if (nbNonNullCoeffs == 0) { // All coefficients of the linear
			// combination are null !
			if (linOperator == IntLinComb.EQ && c == 0) {
				return TRUE;
			} else if (linOperator == IntLinComb.GEQ && 0 <= c) {
				return TRUE;
			} else if (linOperator == IntLinComb.LEQ && 0 >= c) {
				return TRUE;
			} else {
				return FALSE;
			}
		}
		return createIntLinComb(sortedVars, sortedCoeffs, nbPositiveCoeffs, c,
				linOperator);
	}

	protected SConstraint createIntLinComb(IntVar[] sortedVars,
			int[] sortedCoeffs, int nbPositiveCoeffs, int c, int linOperator) {
		IntDomainVar[] tmpVars = new IntDomainVar[sortedVars.length];
		//noinspection SuspiciousSystemArraycopy
		System.arraycopy(sortedVars, 0, tmpVars, 0, sortedVars.length);
		if (isBoolLinComb(tmpVars, sortedCoeffs, linOperator)) {
			return createBoolLinComb(tmpVars, sortedCoeffs, c, linOperator);
		} else {
			return new IntLinComb(tmpVars, sortedCoeffs, nbPositiveCoeffs, c,
					linOperator);
		}
	}

	/**
	 * Check if the combination is made of a single integer variable and only
	 * boolean variables
	 */
	protected boolean isBoolLinComb(IntDomainVar[] lvars, int[] lcoeffs,
			int linOperator) {
		if (linOperator == IntLinComb.NEQ) {
			return false;
		}
		if (lvars.length <= 1) {
			return false;
		}
		int nbEnum = 0;
		for (IntDomainVar lvar : lvars) {
			if (!lvar.hasBooleanDomain()) {
				nbEnum++;
			}
			if (nbEnum > 1) {
				return false;
			}
		}
		return true;
	}

	protected SConstraint createBoolLinComb(IntVar[] vars, int[] lcoeffs,
			int c, int linOperator) {
		IntDomainVar[] lvars = new IntDomainVar[vars.length];
		System.arraycopy(vars, 0, lvars, 0, vars.length);
		int idxSingleEnum = -1; // index of the enum intvar (the single non
		// boolean var)
		int coefSingleEnum = Integer.MIN_VALUE; // coefficient of the enum
		// intvar
		for (int i = 0; i < lvars.length; i++) {
			if (!lvars[i].hasBooleanDomain()) {
				idxSingleEnum = i;
				coefSingleEnum = -lcoeffs[i];
			}
		}
		// construct arrays of coefficients and variables
		int nbVar = (idxSingleEnum == -1) ? lvars.length : lvars.length - 1;
		IntDomainVar[] vs = new IntDomainVar[nbVar];
		int[] coefs = new int[nbVar];
		int cpt = 0;
		for (int i = 0; i < lvars.length; i++) {
			if (i != idxSingleEnum) {
				vs[cpt] = lvars[i];
				coefs[cpt] = lcoeffs[i];
				cpt++;
			}
		}
		if (idxSingleEnum == -1) {
			return createBoolLinComb(vs, coefs, null, Integer.MAX_VALUE, c,
					linOperator);
		} else {
			return createBoolLinComb(vs, coefs, lvars[idxSingleEnum],
					coefSingleEnum, c, linOperator);
		}
	}

	protected SConstraint createBoolLinComb(IntDomainVar[] vs, int[] coefs,
			IntDomainVar obj, int objcoef, int c, int linOperator) {
		VariableUtils.quicksort(coefs, vs, 0, coefs.length - 1);
		if (obj == null) { // is there an enum variable ?
			boolean isAsum = true;
			for (int i = 0; i < vs.length && isAsum; i++) {
				if (coefs[i] != 1) {
					isAsum = false;
				}
			}
			if (isAsum) {
				return new BoolSum(vs, -c, linOperator);
			} else {
				IntDomainVar dummyObj = makeConstantIntVar(-c);
				return new BoolIntLinComb(vs, coefs, dummyObj, 1, 0,
						linOperator);
			}
		} else {
			int newLinOp = linOperator;
			if (objcoef < 0) {
				if (linOperator != IntLinComb.NEQ) {
					objcoef = -objcoef;
					c = -c;
					VariableUtils.reverse(coefs, vs);
					ArrayUtils.inverseSign(coefs);
				}
				if (linOperator == IntLinComb.GEQ) {
					newLinOp = IntLinComb.LEQ;
				} else if (linOperator == IntLinComb.LEQ) {
					newLinOp = IntLinComb.GEQ;
				}
			}
			return new BoolIntLinComb(vs, coefs, obj, objcoef, c, newLinOp);
		}
	}

	// ------------------------ constraints over reals
	/**
	 * Makes an equation from an expression and a constant interval. It is used
	 * by all methods building constraints. This is useful for subclassing this
	 * modeller for another kind of model (like PaLM).
	 *
	 * @param exp
	 *            The expression
	 * @param cst
	 *            The interval this expression should be in
	 * @return the equation constraint
	 */
	public SConstraint makeEquation(RealExp exp, RealIntervalConstant cst) {
		// Collect the variables
		Set<RealVar> collectedVars = new HashSet<RealVar>();
		exp.collectVars(collectedVars);
		RealVar[] tmpVars = new RealVar[0];
		tmpVars = collectedVars.toArray(tmpVars);
		return createEquation(tmpVars, exp, cst);
	}

	/**
	 * Equality constraint.
	 *
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return the constraint enforcing exp1=exp2
	 */
	public SConstraint eq(RealExp exp1, RealExp exp2) {
		if (exp1 instanceof RealIntervalConstant) {
			return makeEquation(exp2, (RealIntervalConstant) exp1);
		} else if (exp2 instanceof RealIntervalConstant) {
			return makeEquation(exp1, (RealIntervalConstant) exp2);
		} else {
			return makeEquation(minus(exp1, exp2), cst(0.0));
		}
	}

	public SConstraint eq(RealExp exp, double cst) {
		return makeEquation(exp, cst(cst));
	}

	public SConstraint eq(double cst, RealExp exp) {
		return makeEquation(exp, cst(cst));
	}

	/**
	 * Inferority constraint.
	 *
	 * @param exp1
	 *            the fisrt expression
	 * @param exp2
	 *            the second expression
	 * @return the constraint enforcing exp1<=exp2
	 */
	public SConstraint leq(RealExp exp1, RealExp exp2) {
		if (exp1 instanceof RealIntervalConstant) {
			return makeEquation(exp2, cst(exp1.getInf(),
					Double.POSITIVE_INFINITY));
		} else if (exp2 instanceof RealIntervalConstant) {
			return makeEquation(exp1, cst(Double.NEGATIVE_INFINITY, exp2
					.getSup()));
		} else {
			return makeEquation(minus(exp1, exp2), cst(
					Double.NEGATIVE_INFINITY, 0.0));
		}
	}

	public SConstraint leq(RealExp exp, double cst) {
		return makeEquation(exp, cst(Double.NEGATIVE_INFINITY, cst));
	}

	public SConstraint leq(double cst, RealExp exp) {
		return makeEquation(exp, cst(cst, Double.POSITIVE_INFINITY));
	}

	/**
	 * Superiority constraint.
	 *
	 * @param exp1
	 *            the fisrt expression
	 * @param exp2
	 *            the second expression
	 * @return the constraint enforcing exp1>=exp2
	 */
	public SConstraint geq(RealExp exp1, RealExp exp2) {
		return leq(exp2, exp1);
	}

	public SConstraint geq(RealExp exp, double cst) {
		return leq(cst, exp);
	}

	public SConstraint geq(double cst, RealExp exp) {
		return leq(exp, cst);
	}

	/**
	 * Addition of two expressions.
	 *
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return the sum of exp1 and exp2 (exp1+exp2)
	 */
	public RealExp plus(RealExp exp1, RealExp exp2) {
		return createRealPlus(exp1, exp2);
	}

	/**
	 * Substraction of two expressions.
	 *
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return the difference of exp1 and exp2 (exp1-exp2)
	 */
	public RealExp minus(RealExp exp1, RealExp exp2) {
		return createRealMinus(exp1, exp2);
	}

	/**
	 * Multiplication of two expressions.
	 *
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return the product of exp1 and exp2 (exp1*exp2)
	 */
	public RealExp mult(RealExp exp1, RealExp exp2) {
		return createRealMult(exp1, exp2);
	}

	/**
	 * Power of an expression.
	 *
	 * @param exp
	 *            the expression to x
	 * @param power
	 *            the second expression
	 * @return the difference of exp1 and exp2 (exp1-exp2)
	 */
	public RealExp power(RealExp exp, int power) {
		return createRealIntegerPower(exp, power);
	}

	/**
	 * Cosinus of an expression.
	 */
	public RealExp cos(RealExp exp) {
		return createRealCos(exp);
	}

	/**
	 * Sinus of an expression.
	 */
	public RealExp sin(RealExp exp) {
		return createRealSin(exp);
	}

	/**
	 * Arounds a double d to <code>[d - epsilon, d + epilon]</code>.
	 */
	public RealIntervalConstant around(double d) {
		return cst(RealMath.prevFloat(d), RealMath.nextFloat(d));
	}

	protected RealExp createRealSin(RealExp exp) {
		return new RealSin(this, exp);
	}

	protected RealExp createRealCos(RealExp exp) {
		return new RealCos(this, exp);
	}

	protected RealExp createRealPlus(RealExp exp1, RealExp exp2) {
		return new RealPlus(this, exp1, exp2);
	}

	protected RealExp createRealMinus(RealExp exp1, RealExp exp2) {
		return new RealMinus(this, exp1, exp2);
	}

	protected RealExp createRealMult(RealExp exp1, RealExp exp2) {
		return new RealMult(this, exp1, exp2);
	}

	protected RealExp createRealIntegerPower(RealExp exp, int power) {
		return new RealIntegerPower(this, exp, power);
	}

	protected SConstraint createEquation(RealVar[] tmpVars, RealExp exp,
			RealIntervalConstant cst) {
		return new Equation(this, tmpVars, exp, cst);
	}

	/**
	 * Building a term from a scalar product of coefficients and variables
	 *
	 * @param lc
	 *            the array of coefficients
	 * @param lv
	 *            the array of variables
	 * @return the term
	 */
	public IntExp scalar(int[] lc, IntDomainVar[] lv) {
		int n = lc.length;
		assert (lv.length == n);
		IntTerm t = new IntTerm(n);
		for (int i = 0; i < n; i++) {
			t.setCoefficient(i, lc[i]);
			if (lv[i] instanceof IntVar) {
				t.setVariable(i, lv[i]);
			} else {
				throw new SolverException("unknown kind of IntDomainVar");
			}
		}
		return t;
	}

	/**
	 * Building a term from a scalar product of coefficients and variables
	 *
	 * @param lv
	 *            the array of variables
	 * @param lc
	 *            the array of coefficients
	 * @return the term
	 */
	public final IntExp scalar(IntDomainVar[] lv, int[] lc) {
		return scalar(lc, lv);
	}

	/**
	 * Building a term from a sum of integer expressions
	 *
	 * @param lv
	 *            the array of integer expressions
	 * @return the term
	 */
	public IntExp sum(IntExp... lv) {
		int n = lv.length;
		IntTerm t = new IntTerm(n);
		for (int i = 0; i < n; i++) {
			t.setCoefficient(i, 1);
			if (lv[i] instanceof IntVar) {
				t.setVariable(i, (IntVar) lv[i]);
			} else {
				throw new SolverException("unexpected kind of IntExp");
			}
		}
		return t;
	}

	// ------------------------ Boolean connectors

	public SConstraint feasiblePairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, int ac) {
		return makePairAC(v1, v2, mat, true, ac);
	}

	public SConstraint feasiblePairAC(IntDomainVar v1, IntDomainVar v2,
			List<int[]> mat, int ac) {
		return makePairAC(v1, v2, mat, true, ac);
	}

	public SConstraint infeasiblePairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, int ac) {
		return makePairAC(v1, v2, mat, false, ac);
	}

	public SConstraint infeasiblePairAC(IntDomainVar v1, IntDomainVar v2,
			List<int[]> mat, int ac) {
		return makePairAC(v1, v2, mat, false, ac);
	}

	/**
	 * Create a binary relation that represent the list of compatible or
	 * incompatible pairs of values (depending on feas) given in argument tp be
	 * stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument. So such that : min[0] <= x.getInf(), max[0] >=
	 * x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >=
	 * y.getSup() for any pairs of variable x,y where an ac algorithm will be
	 * used with this relation. This is mandatory in the api to be able to
	 * compute the opposite of the relation if needed so the min[i]/max[i] can
	 * be smaller/bigger than min_{j \in pairs} pairs.get(j)[i] or max_{j \in
	 * pairs} pairs.get(j)[i]
	 *
	 * @param min
	 * @param max
	 * @param mat
	 *            the list of tuples defined as int[] of size 2
	 * @param feas
	 *            specify if the relation is defined in feasibility or not i.e.
	 *            if the tuples corresponds to feasible or infeasible tuples
	 * @param bitset
	 *            specify if the relation is intended to be used in ac3rm
	 *            enhanced with bitwise operations
	 * @return
	 */
	public BinRelation makeBinRelation(int[] min, int[] max, List<int[]> mat,
			boolean feas, boolean bitset) {
		int n1 = max[0] - min[0] + 1;
		int n2 = max[1] - min[1] + 1;
		ExtensionalBinRelation relation;
		if (bitset) {
			relation = new CouplesBitSetTable(feas, min[0], min[1], n1, n2);
		} else {
			relation = new CouplesTable(feas, min[0], min[1], n1, n2);
		}
		for (int[] couple : mat) {
			if (couple.length != 2) {
				throw new SolverException("Wrong dimension : " + couple.length
						+ " for a couple");
			}
			relation.setCouple(couple[0], couple[1]);
		}
		return relation;
	}

	/**
	 * Create a binary relation that represent the list of compatible or
	 * incompatible pairs of values (depending on feas) given in argument tp be
	 * stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument. So such that : min[0] <= x.getInf(), max[0] >=
	 * x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >=
	 * y.getSup() for any pairs of variable x,y where an ac algorithm will be
	 * used with this relation. This is mandatory in the api to be able to
	 * compute the opposite of the relation if needed so the min[i]/max[i] can
	 * be smaller/bigger than min_{j \in pairs} pairs.get(j)[i] or max_{j \in
	 * pairs} pairs.get(j)[i]
	 *
	 * @param min
	 * @param max
	 * @param mat
	 *            the list of tuples defined as int[] of size 2
	 * @param feas
	 *            specify if the relation is defined in feasibility or not i.e.
	 *            if the tuples corresponds to feasible or infeasible tuples
	 * @return
	 */
	public BinRelation makeBinRelation(int[] min, int[] max, List<int[]> mat,
			boolean feas) {
		return makeBinRelation(min, max, mat, feas, false);
	}

	/**
	 * Create a binary relation from the given matrix of consistency
	 *
	 * @param v1
	 * @param v2
	 * @param mat
	 *            the consistency matrix
	 * @param feas
	 *            specify if the relation is defined in feasibility or not
	 * @return
	 */
	public BinRelation makeBinRelation(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, boolean feas, boolean bitset) {
		IntDomainVar x = v1;
		IntDomainVar y = v2;
		int n1 = x.getSup() - x.getInf() + 1;
		int n2 = y.getSup() - y.getInf() + 1;
		if (n1 == mat.length && n2 == mat[0].length) {
			ExtensionalBinRelation relation;
			relation = bitset ? new CouplesBitSetTable(feas, v1.getInf(), v2
					.getInf(), n1, n2) : new CouplesTable(feas, v1.getInf(), v2
							.getInf(), n1, n2);

			for (int i = 0; i < n1; i++) {
				for (int j = 0; j < n2; j++) {
					if (mat[i][j]) {
						relation.setCouple(i + v1.getInf(), j + v2.getInf());
					}
				}
			}
			return relation;
		} else {
			throw new SolverException(
					"Wrong dimension for the matrix of consistency : "
					+ mat.length + " X " + mat[0].length
					+ " instead of " + n1 + "X" + n2);
		}
	}

	public BinRelation makeBinRelation(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, boolean feas) {
		return makeBinRelation(v1, v2, mat, feas, false);
	}

	private SConstraint makePairAC(IntDomainVar x, IntDomainVar y,
			List<int[]> mat, boolean feas, int ac) {
		int[] min = new int[] { x.getInf(), y.getInf() };
		int[] max = new int[] { x.getSup(), y.getSup() };
		BinRelation relation = makeBinRelation(min, max, mat, feas,
				(ac == 322) ? true : false);
		return relationPairAC(x, y, relation, ac);
	}

	/**
	 * Create a constraint to enforce GAC on a list of feasible or infeasible
	 * tuples
	 *
	 * @param vs
	 * @param tuples
	 *            the list of tuples
	 * @param feas
	 *            specify if the tuples are feasible or infeasible tuples
	 * @return
	 */
	public SConstraint makeTupleAC(IntDomainVar[] vs, List<int[]> tuples,
			boolean feas) {
		int[] min = new int[vs.length];
		int[] max = new int[vs.length];
		for (int i = 0; i < vs.length; i++) {
			min[i] = vs[i].getInf();
			max[i] = vs[i].getSup();
		}
		LargeRelation relation = makeLargeRelation(min, max, tuples, feas);
		return relationTupleAC(vs, relation);
	}

	private SConstraint makePairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, boolean feas, int ac) {
		BinRelation relation = makeBinRelation(v1, v2, mat, feas,
				ac == 322 ? true : false);
		return relationPairAC(v1, v2, relation, ac);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list of
	 * feasible tuples. Default algorithm is GAC3rm
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to feasible tuples
	 */
	public SConstraint feasibleTupleAC(IntDomainVar[] vars, List<int[]> tuples) {
		return feasibleTupleAC(vars, tuples, 32);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list of
	 * infeasible tuples. Default algorithm is GAC3rm
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to infeasible tuples
	 */
	public SConstraint infeasibleTupleAC(IntDomainVar[] vars, List<int[]> tuples) {
		return infeasibleTupleAC(vars, tuples, 32);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list of
	 * feasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to feasible tuples
	 */
	public SConstraint feasibleTupleAC(IntDomainVar[] vars, List<int[]> tuples,
			int ac) {
		LargeRelation relation = makeRelation(vars, tuples, true);
		if (ac == 2001) {
			return new GAC2001PositiveLargeConstraint(vars,
					(IterTuplesTable) relation);
		} else if (ac == 32) {
			return new GAC3rmPositiveLargeConstraint(vars,
					(IterTuplesTable) relation);
		} else {
			throw new SolverException(
			"unknown ac algorithm, must be 32 or 2001");
		}
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list of
	 * infeasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to infeasible tuples
	 */
	public SConstraint infeasibleTupleAC(IntDomainVar[] vars,
			List<int[]> tuples, int ac) {
		LargeRelation relation = makeRelation(vars, tuples, false);
		if (ac == 2001) {
			return new GAC2001PositiveLargeConstraint(vars,
					(IterTuplesTable) relation);
		} else if (ac == 32) {
			return new GAC3rmPositiveLargeConstraint(vars,
					(IterTuplesTable) relation);
		} else {
			throw new SolverException(
			"unknown ac algorithm, must be 32 or 2001");
		}
	}

	public SConstraint relationPairAC(IntDomainVar v1, IntDomainVar v2,
			BinRelation binR, int ac) {
		if (ac == 3) {
			return new AC3BinSConstraint(v1, v2, binR);
		} else if (ac == 4) {
			throw new SolverException("ac4 not implemented in choco2");
		} else if (ac == 2001) {
			return new AC2001BinSConstraint(v1, v2, binR);
		} else if (ac == 32) {
			return new AC3rmBinSConstraint(v1, v2, binR);
		} else if (ac == 322) {
			return new AC3rmBitBinSConstraint(v1, v2, (CouplesBitSetTable) binR);
			// TODO: add the bitset implementation
		} else {
			throw new UnsupportedOperationException("Ac " + ac
					+ " algorithm not yet implemented");
		}
	}

	/**
	 * @deprecated use makeLargeRelation instead
	 */
	@Deprecated
	public LargeRelation makeRelation(IntVar[] vs, List<int[]> tuples,
			boolean feas) {
		int[] min = new int[vs.length];
		int[] max = new int[vs.length];
		for (int i = 0; i < vs.length; i++) {
			min[i] = ((IntDomainVar) vs[i]).getInf();
			max[i] = ((IntDomainVar) vs[i]).getSup();
		}
		return makeLargeRelation(min, max, tuples, feas);
	}

	/**
	 * Create a nary relationship that can be used to state a GAC constraint
	 * using after the api relationTupleAC(relation). Typically GAC algorithms
	 * uses two main schemes to seek the next support : - either by looking in
	 * the domain of the variable (here put feas = false to get such a relation)
	 * - or in the table itself in which case one need to be able to iterate
	 * over the tuples and not only check consistency (here put feas = true to
	 * get such a relation)
	 *
	 * @param min
	 *            : min[i] has to be greater or equal the minimum value of any
	 *            i-th variable on which this relation will be used
	 * @param max
	 *            : max[i] has to be greater or equal the maximum value of any
	 *            i-th variable on which this relation will be used
	 * @param tuples
	 * @param feas
	 *            specifies if you want an Iterable relation or not
	 * @return an nary relation.
	 */
	public LargeRelation makeLargeRelation(int[] min, int[] max,
			List<int[]> tuples, boolean feas) {
		return makeLargeRelation(min, max, tuples, feas, (feas ? 0 : 1));
	}

	/**
	 * Create a nary relationship that can be used to state a GAC constraint
	 * using after the api relationTupleAC(relation). Typically GAC algorithms
	 * uses two main schemes to seek the next support : - either by looking in
	 * the domain of the variable (here put feas = false to get such a relation)
	 * - or in the table itself in which case one need to be able to iterate
	 * over the tuples and not only check consistency (here put feas = true to
	 * get such a relation)
	 *
	 * @param min
	 *            : min[i] has to be greater or equal the minimum value of any
	 *            i-th variable on which this relation will be used
	 * @param max
	 *            : max[i] has to be greater or equal the maximum value of any
	 *            i-th variable on which this relation will be used
	 * @param tuples
	 * @param feas
	 *            : specifies if you want an Iterable relation or not
	 * @param scheme
	 *            : specifies the desired scheme allowed tuples (0) or valid
	 *            tuples (1) or both (2). The GAC constraint stated on this
	 *            relation will then work in the corresponding scheme. Allowed
	 *            means that the search for support is made through the lists of
	 *            tuples and valid that it is made through the domains of the
	 *            variables
	 * @return an nary relation.
	 */

	public LargeRelation makeLargeRelation(int[] min, int[] max,
			List<int[]> tuples, boolean feas, int scheme) {
		int n = min.length;
		int[] offsets = new int[n];
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++) {
			sizes[i] = max[i] - min[i] + 1;
			offsets[i] = min[i];
		}
		LargeRelation relation;
		if (scheme == 0) {
			relation = new IterTuplesTable(tuples, offsets, sizes);
		} else if (scheme == 1) {
			relation = new TuplesTable(feas, offsets, sizes);
			Iterator<int[]> it = tuples.iterator();
			while (it.hasNext()) {
				int[] tuple = it.next();
				if (tuple.length != n) {
					throw new SolverException("Wrong dimension : "
							+ tuple.length + " for a tuple (should be " + n
							+ ")");
				}
				((TuplesTable) relation).setTuple(tuple);
			}
		} else {
			relation = new TuplesList(tuples);
		}

		return relation;
	}

	public SConstraint makeTupleFC(IntDomainVar[] vs, List<int[]> tuples,
			boolean feas) {
		int n = vs.length;
		int[] offsets = new int[n];
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++) {
			IntDomainVar vi = vs[i];
			sizes[i] = vi.getSup() - vi.getInf() + 1; // vi.getDomainSize();
			offsets[i] = vi.getInf();
		}
		TuplesTable relation = new TuplesTable(feas, offsets, sizes);
		Iterator<int[]> it = tuples.iterator();
		while (it.hasNext()) {
			int[] tuple = it.next();
			if (tuple.length != n) {
				throw new SolverException("Wrong dimension : " + tuple.length
						+ " for a tuple (should be " + n + ")");
			}
			relation.setTuple(tuple);
		}
		return createFCLargeConstraint(vs, relation);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list of
	 * feasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to feasible tuples
	 */
	public SConstraint feasibleTupleFC(IntDomainVar[] vars, TuplesTable tuples) {
		return new CspLargeSConstraint(vars, tuples);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list of
	 * infeasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to infeasible tuples
	 */
	public SConstraint infeasibleTupleFC(IntDomainVar[] vars, TuplesTable tuples) {
		return new CspLargeSConstraint(vars, tuples);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list of
	 * infeasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to infeasible tuples
	 */
	public SConstraint infeasTupleFC(IntDomainVar[] vars, List<int[]> tuples) {
		return makeTupleFC(vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list of
	 * feasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to feasible tuples
	 */
	public SConstraint feasTupleFC(IntDomainVar[] vars, List<int[]> tuples) {
		return makeTupleFC(vars, tuples, true);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list of
	 * infeasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to infeasible tuples
	 */
	public SConstraint infeasTupleAC(IntDomainVar[] vars, List<int[]> tuples) {
		return makeTupleAC(vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list of
	 * feasible tuples
	 *
	 * @param vars
	 * @param tuples
	 *            : a list of int[] corresponding to feasible tuples
	 */
	public SConstraint feasTupleAC(IntDomainVar[] vars, List<int[]> tuples) {
		return makeTupleAC(vars, tuples, true);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given consistency
	 * relation
	 *
	 * @param vs
	 * @param rela
	 */
	public SConstraint relationTupleFC(IntDomainVar[] vs, LargeRelation rela) {
		return createFCLargeConstraint(vs, rela);
	}

	protected SConstraint createFCLargeConstraint(IntDomainVar[] vars,
			LargeRelation relation) {
		IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
		System.arraycopy(vars, 0, tmpVars, 0, vars.length);
		return new CspLargeSConstraint(tmpVars, relation);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given consistency
	 * relation. The GAC algorithm depends on the kind of relation : -
	 * IterIndexedLargeRelation is used in GAC3rm with allowed tuples -
	 * Otherwise a GAC3rm in valid tuples will be used
	 *
	 * @param vs
	 * @param rela
	 */
	public SConstraint relationTupleAC(IntDomainVar[] vs, LargeRelation rela) {
		return relationTupleAC(vs, rela, 32);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given consistency
	 * relation. The GAC algorithm depends on the kind of relation and the ac
	 * algorithm : - IterIndexedLargeRelation is used in GAC3rm with allowed
	 * tuples - Otherwise a GAC3rm in valid tuples will be used
	 *
	 * @param vs
	 * @param rela
	 */
	public SConstraint relationTupleAC(IntDomainVar[] vs, LargeRelation rela,
			int ac) {
		if (rela instanceof IterLargeRelation) {
			if (ac == 32) {
				return new GAC3rmPositiveLargeConstraint(vs,
						(IterTuplesTable) rela);
			} else if (ac == 2001) {
				return new GAC2001PositiveLargeConstraint(vs,
						(IterTuplesTable) rela);
			} else {
				throw new SolverException(
				"GAC algo unknown, choose between 32 or 2001");
			}
		} else {
			if (ac == 32) {
				return new GAC3rmLargeConstraint(vs, rela);
			} else if (ac == 2001) {
				return new GAC2001LargeSConstraint(vs, rela);
			} else if (ac == 2008) {
				return new GACstrPositiveLargeSConstraint(vs, rela);
			} else {
				throw new SolverException(
				"GAC algo unknown, choose between 32, 2001, 2008");
			}
		}
	}

	public SConstraint relationPairAC(IntDomainVar v1, IntDomainVar v2,
			BinRelation binR) {
		return relationPairAC(v1, v2, binR,
				(binR instanceof CouplesBitSetTable ? 322 : 32));
	}

	public SConstraint infeasPairAC(IntDomainVar v1, IntDomainVar v2,
			List<int[]> mat) {
		return makePairAC(v1, v2, mat, false, 322);
	}

	public SConstraint infeasPairAC(IntDomainVar v1, IntDomainVar v2,
			List<int[]> mat, int ac) {
		return makePairAC(v1, v2, mat, false, ac);
	}

	public SConstraint feasPairAC(IntDomainVar v1, IntDomainVar v2,
			List<int[]> mat) {
		return makePairAC(v1, v2, mat, true, 322);
	}

	public SConstraint feasPairAC(IntDomainVar v1, IntDomainVar v2,
			List<int[]> mat, int ac) {
		return makePairAC(v1, v2, mat, true, ac);
	}

	public SConstraint infeasPairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat) {
		return makePairAC(v1, v2, mat, false, 322);
	}

	public SConstraint infeasPairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, int ac) {
		return makePairAC(v1, v2, mat, false, ac);
	}

	public SConstraint feasPairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat) {
		return makePairAC(v1, v2, mat, true, 322);
	}

	public SConstraint feasPairAC(IntDomainVar v1, IntDomainVar v2,
			boolean[][] mat, int ac) {
		return makePairAC(v1, v2, mat, true, ac);
	}

	public SConstraint reifiedIntConstraint(IntDomainVar binVar, SConstraint c) {
		return new ReifiedIntSConstraint(binVar, (AbstractIntSConstraint) c);
	}

	public SConstraint reifiedIntConstraint(IntDomainVar binVar, SConstraint c,
			SConstraint opc) {
		return new ReifiedIntSConstraint(binVar, (AbstractIntSConstraint) c,
				(AbstractIntSConstraint) opc);
	}

}

