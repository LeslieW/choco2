package samples;

import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.tutorials.PatternExample;
import samples.tutorials.trunk.MagicSquare;

import static choco.Choco.*;

/**
 * A set n distinct numbers taken from the interval [1,n^2] form a magic series if their sum is the nth magic constant M_n=n*(n^2+1)/2
 * A magic series is potentially making up a line in a magic square.
 * @author Arnaud Malapert</br> 
 * @since 12 oct. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public class MagicSeries extends PatternExample {

	/** from http://robert.gerbicz.googlepages.com/magic.txt */
	public final static int[] NUMBER_OF_MAGIC_SERIES = 
	{ 1, 2, 8, 86, 1394, 32134, 957332, 35154340, 1537408202};
	
	protected int magicSum;
	protected int n;
	
	private Constraint magicSumConstraint;
	
	protected IntegerVariable[] magicSerie;

	@Override
	public void buildModel() {
		model = new CPModel();
		magicSerie = makeIntVarArray("s", n, 1, n*n, Options.V_ENUM);
		magicSumConstraint = eq ( sum(magicSerie), magicSum);
		model.addConstraint( magicSumConstraint);
		for (int i = 1; i < magicSerie.length; i++) {
			model.addConstraint( lt(magicSerie[i-1], magicSerie[i]));
		}
	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);

	}

	@Override
	public void prettyOut() {
		LOGGER.info( solver.getCstr(magicSumConstraint).pretty());

	}

	@Override
	public void setUp(Object parameters) {
		n = (Integer) parameters;
		magicSum = MagicSquare.getMagicSum(n);
	}

	@Override
	public void solve() {
		solver.solveAll();
	}

	public static void main(String[] args) {
		new MagicSeries().execute(7);
	}


}
