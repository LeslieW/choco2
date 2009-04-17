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
package samples.Examples;


import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.memory.recomputation.EnvironmentRecomputation;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solution;

import java.text.MessageFormat;

public class Queen extends PatternExample {

	private int n = 8;
	IntegerVariable[] queens;


	@Override
	public void setUp(Object paramaters) {
		if (paramaters instanceof Integer) {
			n = (Integer) paramaters;
		}
	}

	@Override
	public void buildModel() {
		_m = new CPModel();

		// create variables
		queens = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			queens[i] = makeIntVar("Q" + i, 1, n);
		}
		_m.addConstraint("cp:bc", Choco.allDifferent(queens));

		// all different constraints
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int k = j - i;
				_m.addConstraint(neq(queens[i], plus(queens[j], k)));
				_m.addConstraint(neq(queens[i], minus(queens[j], k)));
			}
		}
	}

	@Override
	public void buildSolver() {
		_s = new CPSolver();
		_s.monitorBackTrackLimit(true);
		_s.read(_m);
		_s.attachGoal(new AssignVar(new MinDomain(_s, _s.getVar(queens)), new IncreasingDomain()));
	}

	@Override
	public void solve() {
		_s.solve();
	}

	@Override
	public void prettyOut() {
		LOGGER.info("feasible: " + _s.isFeasible());
		LOGGER.info("nbSol: " + _s.getNbSolutions());
		if (_s.getEnvironment() instanceof EnvironmentRecomputation) {
			LOGGER.info("nbSave: " + ((EnvironmentRecomputation) _s.getEnvironment()).getNbSaves());
		}
		// Display
		// -------
		StringBuffer ret = new StringBuffer();
		ret.append("The queen's problem asks how to place n queens on an n x n chess board " +
		"so that none of them can hit any other in one move.\n");
		ret.append(MessageFormat.format("Here n = {0}\n\n", n));
		ret.append(MessageFormat.format("The {0} last solutions (among {1} solutions) are:\n", _s.getSearchStrategy().solutions.size(), _s.getNbSolutions()));
		String line = "+";
		for (int i = 0; i < n; i++) {
			line += "---+";
		}
		line += "\n";
		for (int sol = 0; sol < _s.getSearchStrategy().solutions.size(); sol++) {
			Solution solution = _s.getSearchStrategy().solutions.get(sol);
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
		_s.printRuntimeSatistics();
	}

	public static void main(String[] args) {
		new Queen().execute(10);
		//        new Queen().execute(10);
	}
}
