package choco.model.constraints.set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

public class TestInverseSet {


	private final static int N = 12;
	private final static int NB_TESTS = 5;
	protected Model m;
	protected CPSolver s;
	protected SetVariable s1 = Choco.makeSetVar("s1", 0, N);
	protected SetVariable s2 = Choco.makeSetVar("s2", 0, N);
	protected SetVariable s3 = Choco.makeSetVar("s3", 0, N);
	protected SetVariable s4 = Choco.makeSetVar("s3", 0, N);

	protected IntegerVariable[] iv = Choco.makeIntVarArray("v", N+1,0,2);
	
	@Before
	public void initialize() {
		m = new CPModel();
		s = new CPSolver();
	}

	@Test
	public void testBadArgs() {
		m.addConstraint( Choco.inverseSet(Choco.makeIntVarArray("v", 2*N,0,2), new SetVariable[]{s1, s2, s3}));
		s.read(m);
		Assert.assertFalse("Bad Args", s.solve());
	}

	@Test
	public void testNbSolutions() {
		m.addConstraints( 
				Choco.inverseSet( iv, new SetVariable[]{s1, s2, s3}),
				Choco.eqCard(s1, N/3),
				Choco.eqCard(s2, N/4),
				Choco.eqCard(s3, N - N/3 - N/4 + 1)
		);
		for (int i = 0; i < NB_TESTS; i++) {
			s.read(m);
			s.setRandomSelectors(i);
			Assert.assertTrue("sat", s.solveAll());
			// nbSubsetN/3 * nbSubsetN/4
			int nbsols = MathUtils.combinaison(N + 1, N/3) * MathUtils.combinaison(N + 1 - N/3 , N/4); 
			Assert.assertEquals("nbsols", nbsols, s.getSolutionCount());
			s.clear();
		}
	}
	
	@Test
	public void testUnsatCSP() {
		m.addConstraints( 
				Choco.inverseSet( iv, new SetVariable[]{s1, s2, s3}),
				Choco.setInter(s1, s2, s4),
				Choco.geqCard(s4, 1)
		);
		for (int i = 0; i < NB_TESTS; i++) {
			s.read(m);
			s.setRandomSelectors(i);
			Assert.assertFalse("Unsat", s.solve());
			s.clear();
		}
	}

}
