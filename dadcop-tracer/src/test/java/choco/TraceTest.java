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

package choco;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;
import trace.OADymPPACTracer;

public class TraceTest {

	@Test
	public void test1(){
		OADymPPACTracer.init();

        Model m = new CPModel();
        IntegerVariable v = Choco.makeIntVar("v", 1,10);
        m.addConstraint(Choco.lt(v, 5));
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        
        OADymPPACTracer.close();
	}
	
	@Test
	public void test2(){
        Model m = new CPModel();
        IntegerVariable v = Choco.makeIntVar("v", 1,10);
        m.addConstraint(Choco.lt(v, 5));
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
	}
	
	@Test
	public void test3(){
		OADymPPACTracer.init();

        Model m = new CPModel();
        IntegerVariable v = Choco.makeIntVar("v", 1,10, Options.V_ENUM);
        IntegerVariable w = Choco.makeIntVar("w", 1,10, Options.V_BOUND);
        m.addConstraint(Choco.lt(v, w));
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        
        OADymPPACTracer.close();
	}

    @Test
	public void testQueen4(){
		OADymPPACTracer.init();

        int n = 4;
        Model m = new CPModel();
        IntegerVariable[] queens = Choco.makeIntVarArray("Q", n, 1, n);

        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));
                m.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }

        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        OADymPPACTracer.close();
	}

    @Test
	public void testQueen8(){
		OADymPPACTracer.init();

        int n = 8;
        Model m = new CPModel();
        IntegerVariable[] queens = Choco.makeIntVarArray("Q", n, 1, n);

        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));
                m.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }

        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        OADymPPACTracer.close();
	}

    @Test
	public void testQueen9(){
		OADymPPACTracer.init();

        int n = 9;
        Model m = new CPModel();
        IntegerVariable[] queens = Choco.makeIntVarArray("Q", n, 1, n);

        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));
                m.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }

        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        OADymPPACTracer.close();
	}
}
