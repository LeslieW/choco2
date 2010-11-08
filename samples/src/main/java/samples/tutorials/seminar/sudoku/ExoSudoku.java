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
package samples.tutorials.seminar.sudoku;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Xavier Lorca
 * Date: 4 sept. 2007
 * Time: 09:31:10
 */
public class ExoSudoku {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	public static void sudokuSimple(int[][] instance, boolean onlyProp) {
		int n = instance.length;
		// Build Model
		Model m = new CPModel();

		// Build an array of integer variables
		IntegerVariable[][] rows = makeIntVarArray("rows", n, n, 1, n);

		// Not equal constraint between each case of a row
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = j; k < n; k++) {
					if (k != j) {
						m.addConstraint(neq(rows[i][j], rows[i][k]));
					}
				}
			}
		}
		// Not equal constraint between each case of a column
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				for (int k = 0; k < n; k++) {
					if (k != i) {
						m.addConstraint(neq(rows[i][j], rows[k][j]));
					}
				}
			}
		}
		// Not equal constraint between each case of a sub region
		for (int ci = 0; ci < n; ci += 3) {
			for (int cj = 0; cj < n; cj += 3) {
				// Extraction of disequality of a sub region
				for (int i = ci; i < ci + 3; i++) {
					for (int j = cj; j < cj + 3; j++) {
						for (int k = ci; k < ci + 3; k++) {
							for (int l = cj; l < cj + 3; l++) {
								if (k != i || l != j) m.addConstraint(neq(rows[i][j], rows[k][l]));
							}
						}
					}
				}
			}
		}
		// Read the instance given.
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (instance[i][j] != 0) {
					Constraint c = eq(rows[i][j], instance[i][j]);
					m.addConstraint(c);
				}
			}
		}

		// Build solver
		Solver s = new CPSolver();

		// Read model
		s.read(m);

		// First choice : only propagation
		if (onlyProp) {
			try {
				s.propagate();
				printGrid(rows, s);
			} catch (ContradictionException e) {
				LOGGER.info("pas de solutions");
			}
		}
		// Second choice : find a solution 
		else {
			s.solve();
			printGrid(rows, s);
		}
	}

	public static void sudokuAdvanced(int[][] instance, boolean onlyProp) {
		int n = instance.length;
		// Build model
		Model m = new CPModel();
		// Declare variables
		IntegerVariable[][] cols = new IntegerVariable[n][n];
		IntegerVariable[][] rows = makeIntVarArray("rows", n, n, 1, n);

		// Channeling between rows and columns
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				cols[i][j] = rows[j][i];
			}
		}

		// Add alldifferent constraint
		for (int i = 0; i < n; i++) {
			m.addConstraint(allDifferent(cols[i]));
			m.addConstraint(allDifferent(rows[i]));
		}
		// Define sub regions
		IntegerVariable[][] carres = new IntegerVariable[n][n];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					carres[j + k * 3][i] = rows[0 + k * 3][i + j * 3];
					carres[j + k * 3][i + 3] = rows[1 + k * 3][i + j * 3];
					carres[j + k * 3][i + 6] = rows[2 + k * 3][i + j * 3];
				}
			}
		}

		// Add alldifferent on sub regions
		for (int i = 0; i < n; i++) {
			Constraint c = allDifferent(carres[i]);
			m.addConstraint(c);
		}

		// Read the instance
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (instance[i][j] != 0) {
					Constraint c = eq(rows[i][j], instance[i][j]);
					m.addConstraint(c);
				}
			}
		}

		// Build solver
		Solver s = new CPSolver();

		// Read model
		s.read(m);

		// First choice : only propagation
		if (onlyProp) {
			try {
				s.propagate();
				printGrid(rows, s);
			} catch (ContradictionException e) {
				LOGGER.info("No solution");
			}
		}
		// Second choice : find a solution
		else {
			s.solve();
			printGrid(rows, s);
		}
	}

	public static void printGrid(IntegerVariable[][] rows, Solver s) {
		for (int i = 0; i < 9; i++) {
            StringBuffer st = new StringBuffer();
			for (int j = 0; j < 9; j++) st.append(s.getVar(rows[i][j]).getVal() + " ");
			LOGGER.info(st.toString());
		}
	}

	public static void main(String[] args) {
		String nomFic = "./dev/src/samples/seminar/sudoku/sudoku_instance.txt";
		SudokuParser p = new SudokuParser(nomFic);
		int[][] instance1 = p.convert();
		int[][] instance2 = p.convert();
		sudokuSimple(instance1, false);
		LOGGER.info("");
		LOGGER.info("****************************************");
		LOGGER.info("");
		sudokuAdvanced(instance2, true);
	}
}