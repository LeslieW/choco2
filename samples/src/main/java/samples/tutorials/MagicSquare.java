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
package samples.tutorials;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MagicSquare extends PatternExample {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	/** order of the magic square */
	public int n;

	public int magicSum;

	protected IntegerVariable[][] vars;

	@Override
	public void setUp(Object parameters) {
		n = (Integer) parameters;
		magicSum = getMagicSum(n);
	}

	public static int getMagicSum(int n) {
		return n * (n * n + 1) / 2;
	}

	@Override
	public void buildModel() {
		_m = new CPModel();
		final int ub = n*n;
		vars = makeIntVarArray("v", n, n, 1, ub);
		// All cells of the matrix must be different
		_m.addConstraint( allDifferent(ArrayUtils.flatten(vars)));
		final IntegerVariable[] varDiag1 = new IntegerVariable[n];
		final IntegerVariable[] varDiag2 = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			// All rows must be equal to the magic sum
			_m.addConstraint(eq(sum(vars[i]), magicSum));
			// All columns must be equal to the magic sum
			_m.addConstraint(eq(sum( ArrayUtils.getColumn(vars, i)), magicSum));
			//record diagonals variable
			varDiag1[i] = vars[i][i];
			varDiag2[i] = vars[(n - 1) - i][i];
		}
		// Every diagonal have to be equal to the magic sum
		_m.addConstraint(eq(sum(varDiag1), magicSum));
		_m.addConstraint(eq(sum(varDiag2), magicSum));
		//symmetry breaking constraint: enforce that the upper left corner contains the minimum corner value.
		_m.addConstraint( and(
				lt(vars[0][0], vars[0][n-1]),
				lt(vars[0][0], vars[n-1][n-1]),
				lt(vars[0][0], vars[n-1][0])
		));

	}

	@Override
	public void buildSolver() {
		_s = new CPSolver();
		_s.monitorFailLimit(true);
		_s.read(_m);
		_s.setTimeLimit(500*1000);
	}

	@Override
	public void prettyOut() {
		if( LOGGER.isLoggable(Level.INFO)) {
			StringBuilder st = new StringBuilder();
			// Print of the solution
			st.append("order: ").append(n);
			st.append("\nmagic sum: ").append(magicSum);
			if( _s.existsSolution()) {
			for (int i = 0; i < n; i++) {
				st.append('\n');
				for (int j = 0; j < n; j++) {
					st.append(MessageFormat.format("{0} ", _s.getVar(vars[i][j]).getVal()));
				}
			}
			}else st.append("\nno solution to display!");
			LOGGER.info(st.toString());
		}
	}

	@Override
	public void solve() {
		_s.solve();
	}

    	@Override
	public void execute() {
		execute(5);
	}

	public static void main1(String[] args) {
		MagicSquare ex = new MagicSquare();
		ex.execute(5);
	}

    public static void main(String[] args) {
        int n = 7;
        LOGGER.info("Magic Square Model with n = " + n);

        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = new IntegerVariable[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                vars[i * n + j] = makeIntVar("C" + i + "_" + j, 1, n * n, Options.V_BOUND);
            }
        IntegerVariable sum = makeIntVar("S", 1, n * n * (n * n + 1) / 2);

        m.addConstraint(eq(sum, n * (n * n + 1) / 2));
        m.addConstraint(Options.C_ALLDIFFERENT_BC,allDifferent(vars));


        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(eq(sum(row), sum));
            m.addConstraint(eq(sum(col), sum));
        }

        s.read(m);
        s.monitorBackTrackLimit(true);
        s.setVarIntSelector(new MinDomain(s,s.getVar(vars)));

        s.solve();
        //LOGGER.info("" + pretty());
        for (int i = 0; i < n; i++) {
            StringBuffer st = new StringBuffer();
            for (int j = 0; j < n; j++) {
                st.append("" + s.getVar(vars[i * n + j]).getVal());
                if (s.getVar(vars[i * n + j]).getVal() > 9) st.append(" ");
                else st.append("  ");
            }
            LOGGER.info(st.toString());
        }
        LOGGER.info("BACK: " + s.getSearchStrategy().getBackTrackCount());
        LOGGER.info("NB_NODE: " + s.getSearchStrategy().getNodeCount());
        LOGGER.info("TIME: " + s.getSearchStrategy().getTimeCount());
        ChocoLogging.flushLogs();
    }

}