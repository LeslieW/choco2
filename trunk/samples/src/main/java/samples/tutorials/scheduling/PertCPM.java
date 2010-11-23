/* * * * * * * * * * * * * * * * * * * * * * * * *
 *           _      _                            *
 *          |  (..)  |                           *
 *          |_ J||L _|        CHOCO solver       *
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
package samples.tutorials.scheduling;

import static choco.Choco.constantArray;
import static choco.Choco.makeTaskVar;
import static choco.Choco.startsAfterEnd;

import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * The following example is inspired from an example of the Ilog Scheduler userguide.
 * The task network has been slightly modified.
 *
 * @author Arnaud Malapert
 *         Date : 2 déc. 2008
 *         Since : 2.0.1
 *         Update : 2.1.1
 */
public class PertCPM extends PatternExample {


	public final int nbTasks = 10;

	private final int horizon; // sum of durations (default)
	
	private TaskVariable masonry, carpentry, plumbing, ceiling, 
	roofing, painting, windows,facade, garden, moving;

	public PertCPM() {
		this(29);
	}

	
	public PertCPM(int horizon) {
		super();
		this.horizon = horizon;
	}



	protected IntegerVariable[] buildDurations() {
		return constantArray(new int[]{7, 3, 8, 3, 1, 2, 1, 2, 1, 1});
	}

	@Override
	public void buildModel() {
		model = new CPModel();
		masonry=makeTaskVar("masonry",horizon, 7,Options.V_BOUND);
		carpentry=makeTaskVar("carpentry",horizon, 3);
		plumbing=makeTaskVar("plumbing",horizon, 8);
		ceiling=makeTaskVar("ceiling",horizon, 3);
		roofing=makeTaskVar("roofing",horizon, 1);
		painting=makeTaskVar("painting",horizon, 2);
		windows=makeTaskVar("windows",horizon, 1);
		facade=makeTaskVar("facade",horizon, 2);
		garden=makeTaskVar("garden",horizon, 1);
		moving=makeTaskVar("moving",horizon, 1);
		
		//add temporal constraints
		model.addConstraints(
				startsAfterEnd(carpentry,masonry),
				startsAfterEnd(plumbing,masonry),
				startsAfterEnd(ceiling,masonry),
				startsAfterEnd(roofing,carpentry),
				startsAfterEnd(roofing,ceiling),
				startsAfterEnd(windows,roofing),
				startsAfterEnd(painting,windows),
				startsAfterEnd(facade,roofing),
				startsAfterEnd(facade,plumbing),
				startsAfterEnd(garden,roofing),
				startsAfterEnd(garden,plumbing),
				startsAfterEnd(moving,facade),
				startsAfterEnd(moving,garden),
				startsAfterEnd(moving,painting)
		);
	}


	@Override
	public void buildSolver() {
		solver = new CPSolver();
		((CPSolver) solver).createMakespan();
		solver.read(model);
	}

	@Override
	public void solve() {
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			LOGGER.log(Level.SEVERE, "Infeasible Pert Problem", e);
		}
		try {
			//then we instantiate the makespan variable and compute slack times
			final IntDomainVar makespan = solver.getMakespan();
			makespan.instantiate(makespan.getInf(), null, true);
			solver.propagate();
		} catch (ContradictionException e) {
			LOGGER.log(Level.SEVERE, "CPM should not lead to a contradiction", e);
		}
	}



	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) LOGGER.info( StringUtils.prettyOnePerLine(solver.getTaskVarIterator()));
	}

	public static void main(String[] args) {
		(new PertCPM()).execute();
	}
}

