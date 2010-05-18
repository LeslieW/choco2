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
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package samples.tutorials.trunk;


import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solution;
import samples.tutorials.PatternExample;

import java.text.MessageFormat;
import java.util.List;

import static choco.Choco.*;

public class Queen extends PatternExample {

	private int n = 8;
	IntegerVariable[] queens;


	@Override
	public void setUp(Object parameters) {
		if (parameters instanceof Integer) {
			n = (Integer) parameters;
		}
	}

	@Override
	public void buildModel() {
		model = new CPModel();

		// create variables
		queens = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			queens[i] = makeIntVar("Q" + i, 1, n);
		}
		model.addConstraint(Options.C_ALLDIFFERENT_BC, Choco.allDifferent(queens));

		// all different constraints
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int k = j - i;
				model.addConstraint(neq(queens[i], plus(queens[j], k)));
				model.addConstraint(neq(queens[i], minus(queens[j], k)));
			}
		}
	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.monitorBackTrackLimit(true);
		solver.read(model);
		solver.attachGoal(new AssignVar(new MinDomain(solver, solver.getVar(queens)), new IncreasingDomain()));
	}

	@Override
	public void solve() {
		solver.solveAll();
    }

	@Override
	public void prettyOut() {
	//	LOGGER.info("feasible: " + solver.isFeasible());
	//	LOGGER.info("nbSol: " + solver.getNbSolutions());
		// Display
		// -------
		StringBuffer ret = new StringBuffer();
		List<Solution> sols = solver.getSearchStrategy().getStoredSolutions();
		ret.append("The queen's problem asks how to place n queens on an n x n chess board " +
		"so that none of them can hit any other in one move.\n");
		ret.append(MessageFormat.format("Here n = {0}\n\n", n));
		ret.append(MessageFormat.format("The {0} last solutions (among {1} solutions) are:\n",sols.size(), solver.getNbSolutions()));
		String line = "+";
		for (int i = 0; i < n; i++) {
			line += "---+";
		}
		line += "\n";
		for (int sol = 0; sol < sols.size(); sol++) {
			Solution solution = sols.get(sol);
			ret.append(line);
			for (int i = 0; i < n; i++) {
				ret.append("|");
				for (int j = 0; j < n; j++) {
					ret.append((solution.getIntValue(i) == j + 1) ? " * |" : "   |");
				}
				ret.append(MessageFormat.format("\n{0}", line));
			}
			ret.append("\n\n\n");
		}
		LOGGER.info(ret.toString());
		solver.printRuntimeStatistics();
	}

	
	@Override
	public void execute() {
		execute(10);
	}

	public static void main(String[] args) {
		new Queen().execute(10);
		//        new Queen().execute(10);
	}
}
