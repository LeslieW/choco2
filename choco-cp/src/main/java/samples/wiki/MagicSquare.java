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
package samples.wiki;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;


public class MagicSquare {

    protected final static Logger LOGGER = ChocoLogging.getSamplesLogger();

    public static void main(String[] args) {
        int n = 4;
        LOGGER.info("Magic Square Model with n = " + n);

        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = new IntegerVariable[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                vars[i * n + j] = makeIntVar("C" + i + "_" + j, 1, n * n);
            }
        IntegerVariable sum = makeIntVar("S", 1, n * n * (n * n + 1) / 2);

        m.addConstraint(eq(sum, n * (n * n + 1) / 2));
        for (int i = 0; i < n * n; i++)
            for (int j = 0; j < i; j++)
                m.addConstraint(neq(vars[i], vars[j]));

        int[] coeffs = new int[n];
        for (int i = 0; i < n; i++) {
            coeffs[i] = 1;
        }

        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(eq(scalar(coeffs, row), sum));
            m.addConstraint(eq(scalar(coeffs, col), sum));
        }
        s.read(m);
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
        LOGGER.info("NB_NODE: " + s.getSearchStrategy().getNodeCount());
        
    }

}

