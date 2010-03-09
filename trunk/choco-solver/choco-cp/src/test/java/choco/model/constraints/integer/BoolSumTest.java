package choco.model.constraints.integer;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import junit.framework.Assert;
import org.junit.Test;

public class BoolSumTest {

	private final IntegerVariable[] bvars = Choco.makeBooleanVarArray("b", 10);

	private final IntegerExpressionVariable sum = Choco.sum(bvars);

	private final int nbTotSols = 1 << bvars.length;


	private int solveWith(Constraint c) {
		final Model m = new CPModel();
		m.addConstraint(c);
		final CPSolver s =new CPSolver();
		s.read(m);
		s.setRandomSelectors(0);
		s.solveAll();
		return s.getNbSolutions();
	}

	private final void testNbSols(int[] sols1, int[] sols2) {
		//		LOGGER.info(nbTotSols);
		//		LOGGER.info(Arrays.toString(sols1));
		//		LOGGER.info(Arrays.toString(sols2));
		for (int i = 0; i < sols1.length; i++) {
			Assert.assertEquals(nbTotSols, sols1[i] + sols2[i]);
		}
	}

	@Test
	public void testEqNeq() {
		//ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		final int[] sols1 = new int[bvars.length+3];
		final int[] sols2 = new int[bvars.length+3];
		for(int i = -1; i < bvars.length + 2; i++) {
			sols1[i+1] = solveWith(Choco.eq(sum, i));
			sols2[i+1] = solveWith(Choco.neq(sum, i));
		}
		testNbSols(sols1, sols2);
		final int n2 = sols1.length/2;
		for (int i = 0; i < n2; i++) {
			Assert.assertEquals(sols1[i], sols1[ sols1.length - i-1]);
			Assert.assertEquals(sols2[i], sols2[ sols2.length - i-1]);
		}
	}

	@Test
	public void testLeqGeq() {
		final int[] sols1 = new int[bvars.length+2];
		final int[] sols2 = new int[bvars.length+2];
		for(int i = 0; i < bvars.length + 2; i++) {
			sols1[i] = solveWith(Choco.geq(sum, i));
			sols2[i] = solveWith(Choco.lt(sum, i));
		}
		testNbSols(sols1, sols2);
		for (int i = 0; i < sols1.length; i++) {
			Assert.assertEquals(sols1[i], sols2[ sols1.length - i-1]);
		}

	}

	@Test(expected=ContradictionException.class)
	public void testEq() throws ContradictionException{
		final Model m = new CPModel();
		m.addConstraint(Choco.eq(sum, 1));
		final CPSolver s =new CPSolver();
		s.read(m);
		try{
			for (int i = 0; i < bvars.length-2; i++) {
				s.getVar(bvars[i]).setVal(0);
			}
			s.propagate();
		}catch (ContradictionException ignored){
			Assert.fail();
		}
		for (int i = bvars.length-2; i < bvars.length; i++) {
			s.getVar(bvars[i]).setVal(0);
		}
		s.propagate();
	}
}
