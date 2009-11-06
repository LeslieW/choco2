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
package samples.seminar;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.text.MessageFormat;
import java.util.logging.Logger;

/*
* Created by IntelliJ IDEA.
* User: charles
* Date: 29 mai 2008
* Since : Choco 2.0.0
*
*/
public class ExMagicSquare {

    protected final static Logger LOGGER = ChocoLogging.getSamplesLogger();

    public static void main(String[] args) {
        // Constant declaration
// Order of the magic square
        int n = 3;
// Magic sum
        int magicSum = n * (n * n + 1) / 2;

// Build the model
        Model m = new CPModel();

// Creation of an array of variables
        IntegerVariable[][] var = new IntegerVariable[n][n];

// For each variables, we define its name and the bound of its domain.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                var[i][j] = Choco.makeIntVar("var_" + i + "_" + j, 1, n * n);
                // Associate the variable to the model.
                m.addVariable(var[i][j]);
            }
        }

        // All cells of the matrix must be different
        for (int i = 0; i < n * n; i++) {
            for (int j = i + 1; j < n * n; j++) {
                Constraint c  = Choco.neq(var[i / n][i % n], var[j / n][j % n]);
                m.addConstraint(c);
            }
        }

// All rows must be equal to the magic sum
        for (int i = 0; i < n; i++) {
            m.addConstraint(Choco.eq(Choco.sum(var[i]), magicSum));
        }

        IntegerVariable[][] varCol = new IntegerVariable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Copy of var in the column order
                varCol[i][j] = var[j][i];
            }
            // Each column's sum is equal to the magic sum
            m.addConstraint(Choco.eq(Choco.sum(varCol[i]), magicSum));
        }

        IntegerVariable[] varDiag1 = new IntegerVariable[n];
        IntegerVariable[] varDiag2 = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            // Copy of var in varDiag1
            varDiag1[i] = var[i][i];
            // Copy of var in varDiag2
            varDiag2[i] = var[(n - 1) - i][i];
        }
// Every diagonal have to be equal to the magic sum
        m.addConstraint(Choco.eq(Choco.sum(varDiag1), magicSum));
        m.addConstraint(Choco.eq(Choco.sum(varDiag2), magicSum));

// Build the solver
        Solver s = new CPSolver();

// Read the model
        s.read(m);
// Solve the model
        s.solve();
// Print of the solution
        for (int i = 0; i < n; i++) {
            StringBuffer st = new StringBuffer();
            for (int j = 0; j < n; j++) {
                st.append(MessageFormat.format("{0} ", s.getVar(var[i][j]).getVal()));
            }
            LOGGER.info(st.toString());
        }
    }

}
