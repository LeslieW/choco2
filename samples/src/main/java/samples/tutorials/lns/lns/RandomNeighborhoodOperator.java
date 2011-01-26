package samples.tutorials.lns.lns;

import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntVar;
import gnu.trove.TIntHashSet;

import java.util.Random;

/** @author Sophie Demassey */
public class RandomNeighborhoodOperator implements NeighborhoodOperator {

protected Random random;
protected int nbRelaxedVars;
protected TIntHashSet selected;

/**
 * Default constructor for a random value de-selector.
 * @param nbRelaxedVars number of variables to de-instantiate
 */
public RandomNeighborhoodOperator(int nbRelaxedVars)
{
	this(nbRelaxedVars, 0);
}

/**
 * Constructs a random value selector for branching with a specified seed.
 * @param nbRelaxedVars number of variables to de-instantiate
 * @param seed          random seed
 */
public RandomNeighborhoodOperator(int nbRelaxedVars, long seed)
{
	random = new Random(seed);
	this.nbRelaxedVars = nbRelaxedVars;
	selected = new TIntHashSet();
}

@Override
public boolean restrictNeighborhood(Solution solution)
{

	Solver solver = solution.getSolver();
	int nbTotalVars = solver.getNbIntVars() - 1;

	int n = nbTotalVars - nbRelaxedVars;
	;
	boolean b = false;
	IntVar var;
	selected.clear();
	while (n > 0) {
		int index = random.nextInt(nbTotalVars);
		var = solver.getIntVarQuick(index);
		if (var != solver.getObjective() && !selected.contains(index)) {
			if (solution.getIntValue(index) != Solution.NULL) {
				solver.LOGGER.info("fix " + var);
				solver.post(solver.eq(var, solution.getIntValue(index)));
				b = true;
			}
			selected.add(index);
			n--;
		}
	}
	return b;
}

}
