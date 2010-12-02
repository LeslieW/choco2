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

package samples.tutorials.seminar;

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

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

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
