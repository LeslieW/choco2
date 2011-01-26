package samples.tutorials.lns.lns;

import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import gnu.trove.TIntHashSet;

import java.util.Random;

/** @author Sophie Demassey */
public class RandomTaskNeighborhoodOperator implements NeighborhoodOperator {
protected Random random;
protected int minSize;
protected int maxSize;


protected TIntHashSet selected;

/**
 * Default constructor for a random value de-selector.
 * @param nbRelaxedTasks number of task variables to let free
 */
public RandomTaskNeighborhoodOperator(int nbRelaxedTasks)
{
	this(nbRelaxedTasks, 0);
}

/**
 * Constructs a random value selector for branching with a specified seed.
 * @param nbRelaxedTasks number of task variables to let free
 * @param seed           random seed
 */
public RandomTaskNeighborhoodOperator(int nbRelaxedTasks, long seed)
{
	this(nbRelaxedTasks, nbRelaxedTasks, seed);
}

/**
 * Constructs a random value selector for branching with a specified seed.
 * @param minNbRelaxedTasks minimum number of task variables to let free
 * @param maxNbRelaxedTasks maximum number of task variables to let free
 * @param seed              random seed
 */
public RandomTaskNeighborhoodOperator(int minNbRelaxedTasks, int maxNbRelaxedTasks, long seed)
{
	random = new Random(seed);
	this.minSize = minNbRelaxedTasks;
	this.maxSize = maxNbRelaxedTasks;
	selected = new TIntHashSet();
}

@Override
public boolean restrictNeighborhood(Solution solution)
{
	Solver solver = solution.getSolver();
	int nbTotalVars = solver.getNbTaskVars();
	int val;

	int nbRelaxedTasks = (minSize == maxSize) ? minSize : random.nextInt(maxSize - minSize + 1) + minSize;

	int n = nbTotalVars - nbRelaxedTasks;
	boolean b = false;
	selected.clear();
	while (n > 0) {
		int taskIndex = random.nextInt(nbTotalVars);
		if (!selected.contains(taskIndex)) {
			TaskVar task = solver.getTaskVarQuick(taskIndex);
			Solver.LOGGER.info("fix " + task);

			IntVar var = task.start();
			if (!var.isInstantiated()) {
				val = solution.getIntValue(solver.getIntVarIndex(var));
				if (val != Solution.NULL) {
					solver.post(solver.eq(var, val));
					b = true;
				}
			}
			var = task.duration();
			if (!var.isInstantiated()) {
				val = solution.getIntValue(solver.getIntVarIndex(var));
				if (val != Solution.NULL) {
					solver.post(solver.eq(var, val));
					b = true;
				}
			}
			var = task.end();
			if (!var.isInstantiated()) {
				val = solution.getIntValue(solver.getIntVarIndex(var));
				if (val != Solution.NULL) {
					solver.post(solver.eq(var, val));
					b = true;
				}
			}
			selected.add(taskIndex);
			n--;
		}
	}
	return b;
}

}
