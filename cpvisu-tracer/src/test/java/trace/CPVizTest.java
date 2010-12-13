/**
 * Copyright (c) 1999-2010, Ecole des Mines de Nantes
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Ecole des Mines de Nantes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package trace;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;
import trace.Display;
import trace.Visualization;
import trace.visualizers.Vector;

import static choco.Choco.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 6 déc. 2010
 */
public class CPVizTest {

    @Test
    public void testNQ() {
        int n = 4;

        Model m = new CPModel();
        IntegerVariable[] Q = Choco.makeIntVarArray("Q", n, 1, n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(Choco.neq(Q[i], Q[j]));
                m.addConstraint(Choco.neq(Q[i], Choco.plus(Q[j], k)));
                m.addConstraint(Choco.neq(Q[i], Choco.minus(Q[j], k)));
            }
        }

        Solver s = new CPSolver();
        s.read(m);

        String dir = System.getProperty("user.dir");

        Visualization visu = new Visualization("NQ" + n, s, dir + "/out");

        visu.createTree();
        visu.createViz();

        Vector vector = new Vector(s.getVar(Q), Display.preset(), n, n);
        vector.setMinMax(1, n);

        visu.addVisualizer(vector);

        s.solveAll();

        visu.close();
    }

    @Test
    public void testSendMoreMoney() {
        Model model;
        IntegerVariable S, E, N, D, M, O, R, Y;
        IntegerVariable[] SEND, MORE, MONEY;

        model = new CPModel();

        S = makeIntVar("S", 0, 9);
        E = makeIntVar("E", 0, 9);
        N = makeIntVar("N", 0, 9);
        D = makeIntVar("D", 0, 9);
        M = makeIntVar("M", 0, 9);
        O = makeIntVar("0", 0, 9);
        R = makeIntVar("R", 0, 9);
        Y = makeIntVar("Y", 0, 9);
        SEND = new IntegerVariable[]{S, E, N, D};
        MORE = new IntegerVariable[]{M, O, R, E};
        MONEY = new IntegerVariable[]{M, O, N, E, Y};

        model.addConstraints(neq(S, 0), neq(M, 0));
        model.addConstraint(allDifferent(S, E, N, D, M, O, R, Y));
        model.addConstraints(
                eq(plus(scalar(new int[]{1000, 100, 10, 1}, SEND),
                        scalar(new int[]{1000, 100, 10, 1}, MORE)),
                        scalar(new int[]{10000, 1000, 100, 10, 1}, MONEY))
        );


        Solver solver = new CPSolver();
        solver.read(model);


        String dir = System.getProperty("user.dir");
        Visualization visu = new Visualization("SendMoreMoney", solver, dir + "/out");

        visu.createTree();
        visu.createViz();

        Vector visualizer = new Vector(solver.getVar(S, E, N, D, M, O, R, Y), Display.preset(), 8, 10);
        visualizer.setMinMax(0, 9);

        visu.addVisualizer(visualizer);

        solver.solve();

        visu.close();

    }

}
