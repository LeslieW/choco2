package choco;

import trace.OADymPPACTracer;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

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
        IntegerVariable v = Choco.makeIntVar("v", 1,10, "cp:enum");
        IntegerVariable w = Choco.makeIntVar("w", 1,10, "cp:bound");
        m.addConstraint(Choco.lt(v, w));
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        
        OADymPPACTracer.close();
	}
	
}
