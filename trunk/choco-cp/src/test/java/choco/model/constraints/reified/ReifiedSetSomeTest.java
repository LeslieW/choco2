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

package choco.model.constraints.reified;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

import static choco.Choco.*;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 17 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class ReifiedSetSomeTest {

    @Test
    public void test_jussien1() {
        int NV = 2;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }
        IntegerVariable bool = Choco.makeBooleanVar("b");

        m.addConstraint(Choco.reifiedConstraint(bool, eq(vars[0], vars[1]), neq(vars[0], vars[1])));

        s.read(m);
//        ChocoLogging.toSolution();
        s.solveAll();
    }

    @Test
    public void test_jussien2() {
        int NV = 2;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }
        IntegerVariable bool = Choco.makeBooleanVar("b");

        m.addConstraint(Choco.reifiedConstraint(bool, isIncluded(vars[0], vars[1]), isNotIncluded(vars[0], vars[1])));

        s.read(m);
//        ChocoLogging.toSolution();
        s.solveAll();
    }

    @Test
    public void test_jussien3() {
        int NV = 3;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }

        m.addConstraint(Choco.implies(eq(vars[0], vars[1]), neq(vars[1], vars[2])));

        s.read(m);
//        ChocoLogging.toSolution();
        s.solveAll();
    }

    @Test
    public void test_jussien3ref() {
        int NV = 3;

        Model m = new CPModel();
        Solver s = new CPSolver();

        SetVariable[] vars = new SetVariable[NV];
        for (int i = 0; i < NV; i++) {
            vars[i] = Choco.makeSetVar("v", 0, 2);
        }
        IntegerVariable[] bool = Choco.makeBooleanVarArray("b", NV);

//        m.addConstraint(Choco.implies(eq(vars[0], vars[1]), neq(vars[1], vars[2])));
        m.addConstraint(reifiedConstraint(bool[0], eq(vars[0], vars[1]), neq(vars[0], vars[1])));
        m.addConstraint(reifiedConstraint(bool[1], neq(vars[1], vars[2]), eq(vars[1], vars[2])));
        m.addConstraint(implies(eq(bool[0],1), eq(bool[1],1)));


        s.read(m);
//        ChocoLogging.toSolution();
        s.solveAll();
    }
}
