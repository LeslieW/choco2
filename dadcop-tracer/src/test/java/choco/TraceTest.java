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
