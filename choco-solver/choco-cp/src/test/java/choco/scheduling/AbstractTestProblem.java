/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.scheduling;

import java.util.Random;
import java.util.logging.Logger;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.AbstractResourceSConstraint;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.preprocessor.PreProcessConfiguration;
import choco.cp.solver.preprocessor.SolverDetectorFactory;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.bitmask.BitMask;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.MetaSConstraint;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
abstract class AbstractTestProblem {

	private final static Logger LOGGER = ChocoLogging.getTestLogger();

	public CPModel model;

	public CPSolver solver;

	public Constraint rsc;

	public IntegerVariable[] starts;

	public IntegerVariable[] durations;

	public TaskVariable[] tasks;

	public int horizon = 10000000;

	private static final Random RANDOM =  new Random();

	public AbstractTestProblem() {
		super();
	}

	public AbstractTestProblem(final IntegerVariable[] starts, final IntegerVariable[] durations) {
		super();
		this.starts = starts;
		this.durations = durations;
	}

	public AbstractTestProblem(final IntegerVariable[] durations) {
		super();
		this.durations = durations;
	}

	public final void setFlags(final BitMask flags) {
		final SConstraint cstr = solver.getCstr(this.rsc);
		BitMask dest = null;
		if (cstr instanceof AbstractResourceSConstraint) {
			dest = ( (AbstractResourceSConstraint) cstr).getFlags();
		} else if (cstr instanceof MetaSConstraint) {
			dest = ( (AbstractResourceSConstraint) ( (MetaSConstraint) cstr).getSubConstraints(0)).getFlags();
		} 
		if(dest != null) {
			dest.clear();
			dest.set(flags);
		}else ChocoLogging.getTestLogger().info("can not apply resource filtering rules");
	}


	public final static Configuration getConfig(boolean b) {
		final PreProcessConfiguration config = new PreProcessConfiguration();
		PreProcessConfiguration.cancelPreProcess(config);
		config.putTrue(PreProcessConfiguration.DISJUNCTIVE_MODEL_DETECTION);
		config.putTrue(PreProcessConfiguration.DMD_USE_TIME_WINDOWS);
		if(b) {
			config.putTrue(PreProcessConfiguration.DMD_REMOVE_DISJUNCTIVE);
			//can change the number of solutions if it substitutes a cumulative with variable height by a disjunctive. 
			//config.putTrue(PreProcessConfiguration.DISJUNCTIVE_FROM_CUMULATIVE_DETECTION);
		}
		return config;
	}

	public void generateSolver(Configuration conf) {
		SolverDetectorFactory.resetIndexes(model);
		solver = conf == null ? new CPSolver() : new PreProcessCPSolver(conf);
		solver.setHorizon(horizon);
		solver.read(model);	
	}

	protected abstract Constraint[] generateConstraints();

	public void initializeModel() {
		model = new CPModel();
		initializeTasks();
		final Constraint[] cstr = generateConstraints();
		if(cstr!=null) {
			rsc = cstr[0];
			model.addConstraints(cstr);
		}else {
			LOGGER.severe("no model constraint ?");
		}

	}

	public void initializeTasks() {
		if(starts==null) { tasks= Choco.makeTaskVarArray("T", 0, horizon, durations);}
		else {
			tasks=new TaskVariable[durations.length];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i]= Choco.makeTaskVar(String.format("T_%d", i), starts[i],
						Choco.makeIntVar(String.format("end-%d", i), 0, horizon, Options.V_BOUND), durations[i]);
			}
		}
	}

	public void setHorizon(final int horizon) {
		this.horizon = horizon;
	}

	protected void horizonConstraints(final IntegerVariable[] starts, final IntegerVariable[] durations) {
		if (horizon > 0) {
			for (int i = 0; i < starts.length; i++) {
				model.addConstraint(Choco.geq(horizon, Choco.plus(starts[i], durations[i])));
			}
		}
	}

	public IntegerVariable[] generateRandomDurations(final int n) {
		final IntegerVariable[] durations = new IntegerVariable[n];
		final int gap = horizon / n;
		int max = gap + horizon % n;
		for (int i = 0; i < n - 1; i++) {
			final int v = RANDOM.nextInt(max) + 1;
			max += gap - v;
			durations[i] = Choco.constant(v);
		}
		durations[n - 1] = Choco.constant(max);
		return durations;
	}

	public void setRandomProblem(final int size) {
		starts = null;
		durations = generateRandomDurations(size);
	}
}
