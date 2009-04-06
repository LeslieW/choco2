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
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import static choco.cp.solver.SettingType.*;
import choco.cp.solver.constraints.BitFlags;
import choco.kernel.common.IndexFactory;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.RscData;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.HashSet;
import java.util.Set;



/**
 * @author Arnaud Malapert</br> 
 * @since 27 janv. 2009 version 2.0.0</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractResourceManager extends IntConstraintManager {
	

	protected IntDomainVar getUppBound(CPSolver solver, RscData param) {
		return  param.getUppBound() == null ? solver.getScheduler().createMakespan() : solver.getVar( param.getUppBound());
	}

	protected IntDomainVar[] readIntVar(final CPSolver solver, final Variable[] source, final int begin, final int length) {
		IntDomainVar[] vars=new IntDomainVar[length];
		for (int i = 0 ;  i< length; i++) {
			vars[i] = solver.getVar( (IntegerVariable) source[ begin + i]);
		}
		return vars;
	}

	protected TaskVar[] readTaskVar(final CPSolver solver, final Variable[] source, final int begin, final int length) {
		TaskVar[] vars=new TaskVar[length];
		for (int i = 0 ;  i< length; i++) {
			vars[i] = solver.getVar( (TaskVariable) source[ begin + i]);
		}
		return vars;
	}

	protected SetVar[] readSetVar(final CPSolver solver, final Variable[] source, final int begin, final int length) {
		SetVar[] vars=new SetVar[length];
		for (int i = 0 ;  i< length; i++) {
			vars[i] = solver.getVar( (SetVariable) source[ begin + i]);
		}
		return vars;
	}


	protected static BitFlags readPackSettings(final Set<String> options) {
		final BitFlags flags = new BitFlags();
		flags.read(options, ADDITIONAL_RULES, DYNAMIC_LB, FILL_BIN);
		return flags;
	}


	protected void readCumulativeSettings(final Set<String> options, BitFlags flags) {
		flags.read(options, TASK_INTERVAL, TASK_INTERVAL_SLOW, VHM_CEF_ALGO_N2K, VILIM_CEF_ALGO);
	}


	protected static void readDisjunctiveSettings(final Set<String> options, BitFlags flags) {
		flags.read(options, OVERLOAD_CHECKING, NF_NL, DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		if(flags.isEmpty()) {
			flags.set(NF_NL, DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		}
		int cpt = 0;
		if(options.contains(VILIM_FILTERING.getOptionName())) {
			flags.set(VILIM_FILTERING);
			cpt++;
		}
		if(options.contains(DEFAULT_FILTERING.getOptionName())) {
			flags.set(DEFAULT_FILTERING);
			cpt++;
		}
		if(options.contains(SINGLE_RULE_FILTERING.getOptionName())) {
			throw new SolverException("invalid disjunctive setting:" + SINGLE_RULE_FILTERING.getName());
		}
		if(cpt==0) {
			flags.set(DEFAULT_FILTERING);
		} else if(cpt>1) {
			throw new SolverException("cant set only one filtering algorithm");
		}

	}
	/**
	 * @see choco.kernel.model.constraints.ConstraintManager#getFavoriteDomains(java.util.HashSet)
	 */
	@Override
	public int[] getFavoriteDomains(final HashSet<String> options) {
		return getBCFavoriteIntDomains();
	}


}
