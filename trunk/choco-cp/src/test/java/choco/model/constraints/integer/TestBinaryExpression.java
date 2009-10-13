package choco.model.constraints.integer;

import static choco.Choco.eq;
import static choco.Choco.geq;
import static choco.Choco.makeIntVar;
import static choco.Choco.minus;
import static choco.Choco.*;
import static choco.Choco.neq;
import static choco.Choco.scalar;

import java.util.Random;

import org.junit.Test;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.scheduling.SchedUtilities;

public class TestBinaryExpression {


	public IntegerVariable v0, v1, v2;
	public static final int MAX = 100;

	public static final int NB_TESTS = 25;

	public int c0, c1, cste;

	private Model m1, m2;
	private CPSolver s1, s2;



	protected void generateData(int seed) {
		Random rnd= new Random(seed);
		v0 = makeIntVar("v0", - rnd.nextInt(MAX), rnd.nextInt(MAX));
		v1 = makeIntVar("v1", - rnd.nextInt(MAX), rnd.nextInt(MAX));
		v2 = makeIntVar("v2", - v1.getUppB(), - v1.getLowB());
		c0 = rnd.nextInt(2);
		c1 = rnd.nextInt(2);
		cste = v0.getLowB() - v1.getLowB() + rnd.nextInt( v0.getDomainSize() + v1.getDomainSize() + 5);
	}


	public final void compare() {
		//ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		s1 = new CPSolver();
		s1.read(m1);
		//System.out.println(s1.pretty());
		s2 = new CPSolver();
		s2.read(m2);
		//System.out.println(s2.pretty());
		SchedUtilities.compare(-1, SchedUtilities.CHECK_NODES, "Equal", s1, s2);
	}


	private final IntegerExpressionVariable getScalar(boolean plus) {
		return scalar(new int[]{ c0, plus ? c1 : -c1}, new IntegerVariable[]{v0, plus ? v1 : v2});
	}
	
	public final void init(Constraint c1, Constraint c2) {
		m1 = new CPModel();
		m1.addConstraint( c1);
		m2 = new CPModel();
		m2.addConstraint( c2);
	}

	public final void testEqual() {
		init( eq( plus(v0, v1), cste), eq( minus(v0, v2), cste));
		compare();
		init( 
				eq( getScalar(true), cste), 
				eq( getScalar(false), cste)
		);
		compare();

	}

	public final void testGreaterOrEqual() {
		init( geq( plus(v0, v1), cste),  geq( minus(v0, v2), cste));
		compare();
		init( 
				geq( getScalar(true), cste), 
				geq( getScalar(false), cste)
		);
		compare();
	}

	public final void testNotEqual() {
		init( neq( plus(v0, v1), cste) , neq( minus(v0, v2), cste));
		compare();
		init( 
				neq( getScalar(true), cste), 
				neq( getScalar(false), cste)
		);
		compare();
	}
	@Test
	public void testBinaryExpression() {
		for (int i = 0; i < NB_TESTS; i++) {
			generateData(i);
			//testEqual();
			testGreaterOrEqual();
			//testNotEqual();
		}
	}

	@Test
	public final void testIrreductible() {
		m1 = new CPModel();
		v0 = makeIntVar("v0", 1, 5);
		v1 = makeIntVar("v1", 1, 5);
		final IntegerExpressionVariable exp=  minus( mult(2,v0), mult(2, v1) );

		//Equal
		m1.addConstraint( eq( exp, 3) );
		s1 = new CPSolver();
		s1.read(m1);
		SchedUtilities.solveRandom(s1, 0, -1, "Equal");

		//GreaterOrEqual
		m1 = new CPModel();
		m1.addConstraint( geq( exp, 9));
		s1 = new CPSolver();
		s1.read(m1);
		System.out.println(s1.pretty());
		SchedUtilities.solveRandom(s1, 0, -1, "Equal");
	}
	
	@Test
	public final void testDivideByZero() {
		generateData(-100);
		c0 = 0;
		testEqual();
		testGreaterOrEqual();
		testNotEqual();
		c1 = 0;
		testEqual();
		testGreaterOrEqual();
		testNotEqual();
	}
}
