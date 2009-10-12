package choco.model.constraints.integer;

import static choco.Choco.eq;
import static choco.Choco.geq;
import static choco.Choco.makeIntVar;
import static choco.Choco.minus;
import static choco.Choco.mult;
import static choco.Choco.neq;
import static choco.Choco.plus;

import java.util.Random;

import org.junit.Test;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.scheduling.SchedUtilities;

public class TestBinaryExpression {

	
	public IntegerVariable v0, v1, v2;
	public static final int MAX = 100;
	
	public static final int NB_TESTS = 25;
	
	public int cste;
		
	private Model m;
	private CPSolver s1, s2;
	
	protected void generateVariables(int seed) {
		Random rnd= new Random(seed);
		v0 = makeIntVar("v0", - rnd.nextInt(MAX), rnd.nextInt(MAX));
		v1 = makeIntVar("v1", - rnd.nextInt(MAX), rnd.nextInt(MAX));
		v2 = makeIntVar("v2", - v1.getUppB(), - v1.getLowB());
		cste = v0.getLowB() - v1.getLowB() + rnd.nextInt( v0.getDomainSize() + v1.getDomainSize() + 5);
	}
	
	
	public final void testEqual() {
		m = new CPModel();
		m.addConstraint( eq( plus(v0, v1), cste));
		s1 = new CPSolver();
		s1.read(m);
		
		m = new CPModel();
		m.addConstraint( eq( minus(v0, v2), cste));
		s2 = new CPSolver();
		s2.read(m);
		SchedUtilities.compare(-1, SchedUtilities.CHECK_NODES, "Equal", s1, s2);
	}
	
	public final void testGreaterOrEqual() {
		m = new CPModel();
		m.addConstraint( geq( plus(v0, v1), cste));
		s1 = new CPSolver();
		s1.read(m);
		
		m = new CPModel();
		m.addConstraint( geq( minus(v0, v2), cste));
		s2 = new CPSolver();
		s2.read(m);
		SchedUtilities.compare(-1, SchedUtilities.CHECK_NODES, "GreaterOrEqual", s1, s2);
	}
	
	public final void testNotEqual() {
		m = new CPModel();
		m.addConstraint( neq( plus(v0, v1), cste));
		s1 = new CPSolver();
		s1.read(m);
		
		m = new CPModel();
		m.addConstraint( neq( minus(v0, v2), cste));
		s2 = new CPSolver();
		s2.read(m);
		SchedUtilities.compare(-1, SchedUtilities.CHECK_NODES, "NotEqual", s1, s2);
	}
	@Test
	public void testBinaryExpression() {
		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		for (int i = 0; i < NB_TESTS; i++) {
			generateVariables(i);
			testEqual();
			testGreaterOrEqual();
			testNotEqual();
		}
	}
	
	@Test
	public final void testIrreductible() {
		m = new CPModel();
		v0 = makeIntVar("v0", 1, 5);
		v1 = makeIntVar("v1", 1, 5);
		final IntegerExpressionVariable exp=  minus( mult(2,v0), mult(2, v1) );
		
		//Equal
		m.addConstraint( eq( exp, 3) );
		s1 = new CPSolver();
		s1.read(m);
		SchedUtilities.solveRandom(s1, 0, -1, "Equal");
		
		//GreaterOrEqual
		m = new CPModel();
		m.addConstraint( geq( exp, 9));
		s1 = new CPSolver();
		s1.read(m);
		System.out.println(s1.pretty());
		SchedUtilities.solveRandom(s1, 0, -1, "Equal");
		
	}
}
