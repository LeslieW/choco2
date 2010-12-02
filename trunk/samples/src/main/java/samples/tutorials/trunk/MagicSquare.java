/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package samples.tutorials.trunk;

import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import samples.tutorials.PatternExample;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import static choco.Choco.*;


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
		model = new CPModel();
		final int ub = n*n;
		vars = makeIntVarArray("v", n, n, 1, ub);
		// All cells of the matrix must be different
		model.addConstraint( allDifferent(ArrayUtils.flatten(vars)));
		final IntegerVariable[] varDiag1 = new IntegerVariable[n];
		final IntegerVariable[] varDiag2 = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			// All rows must be equal to the magic sum
			model.addConstraint(eq(sum(vars[i]), magicSum));
			// All columns must be equal to the magic sum
			model.addConstraint(eq(sum( ArrayUtils.getColumn(vars, i)), magicSum));
			//record diagonals variable
			varDiag1[i] = vars[i][i];
			varDiag2[i] = vars[(n - 1) - i][i];
		}
		// Every diagonal have to be equal to the magic sum
		model.addConstraint(eq(sum(varDiag1), magicSum));
		model.addConstraint(eq(sum(varDiag2), magicSum));
		//symmetry breaking constraint: enforce that the upper left corner contains the minimum corner value.
		model.addConstraint( and(
				lt(vars[0][0], vars[0][n-1]),
				lt(vars[0][0], vars[n-1][n-1]),
				lt(vars[0][0], vars[n-1][0])
		));

	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.monitorFailLimit(true);
		solver.read(model);
		solver.setTimeLimit(500*1000);
	}

	@Override
	public void prettyOut() {
		if( LOGGER.isLoggable(Level.INFO)) {
			StringBuilder st = new StringBuilder();
			// Print of the solution
			st.append("order: ").append(n);
			st.append("\nmagic sum: ").append(magicSum);
			if( solver.existsSolution()) {
			for (int i = 0; i < n; i++) {
				st.append('\n');
				for (int j = 0; j < n; j++) {
					st.append(MessageFormat.format("{0} ", solver.getVar(vars[i][j]).getVal()));
				}
			}
			}else st.append("\nno solution to display!");
			LOGGER.info(st.toString());
		}
	}

	@Override
	public void solve() {
		solver.solve();
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