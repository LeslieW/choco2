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

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.AssignSetVar;
import choco.cp.solver.search.set.MinDomSet;
import choco.cp.solver.search.set.MinEnv;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solution;
import samples.tutorials.PatternExample;

import static choco.Choco.*;
import static java.text.MessageFormat.format;

public class SteinerSystem extends PatternExample {

	/**
	 * A ternary Steiner system of order n is a set of triplets of distinct elements
	 * taking their values between 1 and n, such that all the pairs included in two different triplets are different.
	 * a solution for n = 7 :
	 * [{1, 2, 3}, {2, 4, 5}, {3, 4, 6}, {1, 4, 7}, {1, 5, 6}, {2,6, 7}, {3, 5, 7}]
	 * we must have n % 6 = 1 or n % 6 = 3 to get a valid n for the problem
	 */

	static int p = 7;
	static int n = p * (p - 1) / 6;

	SetVariable[] vars;

	@Override
	public void setUp(Object paramaters) {
		if(paramaters instanceof Integer){
			p = (Integer)paramaters;
			n = p * (p - 1) / 6;
		}
	}

	@Override
	public void buildModel() {
		model = new CPModel();

		vars = new SetVariable[n];
		SetVariable[] intersect = new SetVariable[n * n];

		// Create Variables
		for (int i = 0; i < n; i++)
			vars[i] = makeSetVar("set " + i, 1, n);
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				intersect[i * n + j] = makeSetVar("interSet " + i + " " + j, 1, n);

		// Post constraints
		for (int i = 0; i < n; i++){
			model.addConstraint(eqCard(vars[i], 3));
		}
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				model.addConstraint(setInter(vars[i], vars[j], intersect[i * n + j]));
				model.addConstraint(leqCard(intersect[i * n + j], 1));
			}
		}
	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
	}

	@Override
	public void solve() {
		solver.setFirstSolution(true);
		solver.generateSearchStrategy();
		solver.addGoal(new AssignSetVar(new MinDomSet(solver, solver.getVar(vars)), new MinEnv()));
		solver.launch();
	}

	@Override
	public void prettyOut() {
		StringBuffer s = new StringBuffer();
		Solution sol = solver.getSearchStrategy().getSolutionPool().getBestSolution();
		solver.restoreSolution(sol);
		s.append("A ternary Steiner system of order n is a set of triplets of n*(n - 1) / 6 " +
				"distinct elements taking their values between 1 and n," +
				" such that all the pairs included in two different triplets are different. " +
		" see http://mathworld.wolfram.com/SteinerTripleSystem.html \n \n");

		s.append(format("A solution for n = {0}\n\n", p));
		for (int i = 0; i < n; i++) {
			s.append(format("set[{0}]:{1}\n", i, solver.getVar(vars[i]).pretty()));
		}
		LOGGER.info(s.toString());

	}


	@Override
	public void execute() {
		execute(7);
	}

	public static void main(String[] args) {
		new SteinerSystem().execute();
	}


}
