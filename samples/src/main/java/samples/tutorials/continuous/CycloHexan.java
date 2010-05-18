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
package samples.tutorials.continuous;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.real.Equation;
import choco.cp.solver.search.real.AssignInterval;
import choco.cp.solver.search.real.CyclicRealVarSelector;
import choco.cp.solver.search.real.RealIncreasingDomain;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import samples.tutorials.PatternExample;
import samples.tutorials.basics.RealVarExample;

import java.text.MessageFormat;
import java.util.List;

import static choco.Choco.*;
import static java.text.MessageFormat.format;

public class CycloHexan extends PatternExample {

    RealVariable x, y, z;
        Constraint c1, c2, c3;

        @Override
        public void printDescription() {
            StringBuffer st = new StringBuffer(24);
            st.append("The CycloHexan problem consists in finding the 3D configuration of a cyclohexane molecule.\n");
            st.append("It is decribed with a system of three non linear equations : \n");
            st.append(" y^2 * (1 + z^2) + z * (z - 24 * y) = -13 \n" +
                    " x^2 * (1 + y^2) + y * (y - 24 * x) = -13 \n" +
                    " z^2 * (1 + x^2) + x * (x - 24 * z) = -13 \n");
            st.append("This example comes from the Elisa project (LINA) examples. \n");

            LOGGER.info(st.toString());
        }

        @Override
        public void buildModel() {
            model = new CPModel();

            x = makeRealVar("x", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            y = makeRealVar("y", -1.0e8, 1.0e8);
            z = makeRealVar("z", -1.0e8, 1.0e8);


            RealExpressionVariable exp1 = plus(mult(power(y, 2), plus(1, power(z, 2))),
                    mult(z, minus(z, mult(24, y))));

            RealExpressionVariable exp2 = plus(mult(power(z, 2), plus(1, power(x, 2))),
                    mult(x, minus(x, mult(24, z))));

            RealExpressionVariable exp3 = plus(mult(power(x, 2), plus(1, power(y, 2))),
                    mult(y, minus(y, mult(24, x))));

            c1 = eq(exp1, -13);
            c2 = eq(exp2, -13);
            c3 = eq(exp3, -13);

            model.addConstraints(c1, c2, c3);
        }

        @Override
        public void buildSolver() {
            solver = new CPSolver();
            solver.read(model);


            Equation eq1 = (Equation) solver.getCstr(c1);
            eq1.addBoxedVar(solver.getVar(y));
            eq1.addBoxedVar(solver.getVar(z));

            Equation eq2 = (Equation) solver.getCstr(c2);
            eq2.addBoxedVar(solver.getVar(x));
            eq2.addBoxedVar(solver.getVar(z));

            Equation eq3 = (Equation) solver.getCstr(c3);
            eq3.addBoxedVar(solver.getVar(x));
            eq3.addBoxedVar(solver.getVar(y));

        }

        @Override
        public void solve() {
            solver.generateSearchStrategy();
            solver.addGoal(new AssignInterval(new CyclicRealVarSelector(solver), new RealIncreasingDomain()));
            solver.launch();
        }

        @Override
        public void prettyOut() {
            packSolutions(solver, model);
            StringBuffer st = new StringBuffer(24);
            List solutions = solver.getSearchStrategy().getStoredSolutions();
            st.append(MessageFormat.format("{0} solution(s) : \n", solutions.size()));
            for (Object solution1 : solutions) {
                Solution solution = (Solution) solution1;
                for (int v = 0; v < model.getNbRealVars(); v++) {
                    st.append(format("var nb {0} in {1}\n", v, solution.getRealValue(v)));
                }
                st.append('\n');
            }
            LOGGER.info(st.toString());
        }

        private static void packSolutions(Solver solver, Model m) {
            List solus = solver.getSearchStrategy().getStoredSolutions();
            double precision = solver.getConfiguration().readDouble(Configuration.REAL_PRECISION);
            for (int i = 0; i < solus.size(); i++) {
                Solution sol = (Solution) solus.get(i);
                for (int j = 0; j < i; j++) {
                    Solution prev = (Solution) solus.get(j);
                    boolean ok = true;
                    for (int v = 0; v < m.getNbRealVars(); v++) {
                        RealInterval inter1 = sol.getRealValue(v);
                        RealInterval inter2 = prev.getRealValue(v);
                        double inf = Math.min(inter1.getInf(), inter2.getInf());
                        double sup = Math.max(inter1.getSup(), inter2.getSup());
                        if ((sup - inf) > precision * 10) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        for (int v = 0; v < m.getNbRealVars(); v++) {
                            RealInterval inter1 = sol.getRealValue(v);
                            RealInterval inter2 = prev.getRealValue(v);
                            double inf = Math.min(inter1.getInf(), inter2.getInf());
                            double sup = Math.max(inter1.getSup(), inter2.getSup());
                            prev.recordRealValue(v, new RealIntervalConstant(inf, sup));
                        }
                        solus.remove(sol);
                        i--;
                        break;
                    }
                }
            }
        }



        public static void main(String[] args) {
            new RealVarExample().execute();
        }

}
