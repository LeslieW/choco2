package choco.kernel.solver.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;

public final class SolutionPoolFactory {


	private SolutionPoolFactory() {
		super();
	}

	/**
	 * pool with a nil capacity (not resizable).
	 */
	public static ISolutionPool makeNoSolutionPool() {
		return NoSolutionPool.SINGLETON;
	}

	/**
	 * pool of unit capacity, contains the last solution (not resizable);
	 * 
	 */
	public static ISolutionPool makeOneSolutionPool(AbstractGlobalSearchStrategy strategy) {
		return new OneSolutionPool(strategy);
	}



	/**
	 * contains the last/best solutions (capa > 0).
	 */
	public static ISolutionPool makeFifoSolutionPool(AbstractGlobalSearchStrategy strategy, int capacity) {
		return new FifoSolutionPool(strategy, capacity);
	}



	/**
	 * record all solution (not resizable).
	 */
	public static ISolutionPool makeInfiniteSolutionPool(AbstractGlobalSearchStrategy strategy) {
		return new InfiniteSolutionPool(strategy);
	}

	public static ISolutionPool makeDefaultSolutionPool(AbstractGlobalSearchStrategy strategy, int capacity) {
		if(capacity == 1) return makeOneSolutionPool(strategy);
		else if(capacity == Integer.MAX_VALUE) return makeInfiniteSolutionPool(strategy);
		if( capacity < 1) return makeNoSolutionPool();
		else return makeFifoSolutionPool(strategy, capacity);
	}



}


abstract class AbstractSolutionPool implements ISolutionPool {

	public final AbstractGlobalSearchStrategy strategy;

	protected int capacity;

	protected AbstractSolutionPool(AbstractGlobalSearchStrategy strategy,
			int capacity) {
		super();
		this.strategy = strategy;
		this.capacity = capacity;

	}


	public final AbstractGlobalSearchStrategy getSearchStrategy() {
		return strategy;
	}


	@Override
	public final int getCapacity() {
		return capacity;
	}

	@Override
	public final boolean isEmpty() {
		return size() == 0;
	}

	
	@Override
	public void clear() {}


	@Override
	public void resizeCapacity(int capacity) {
		ChocoLogging.getEngineLogger().log(Level.WARNING, "- Solution pool: not resizable (capacity:{0}",getCapacity());
	}

	@Override
	public int size() {
		return Math.min(capacity, strategy.getSolutionCount());
	}


}


final class OneSolutionPool extends AbstractSolutionPool {

	private final Solution solution;

	public OneSolutionPool(AbstractGlobalSearchStrategy strategy) {
		super(strategy, 1);
		solution = new Solution(strategy.solver);
	}

	@Override
	public List<Solution> asList() {
		return isEmpty() ? Collections.<Solution>emptyList() : Arrays.<Solution>asList(solution);
	}

	@Override
	public Solution getBestSolution() {
		return isEmpty() ? null : solution;
	}

	@Override
	public void recordSolution(Solver solver) {
		strategy.writeSolution(solution);
	}
}


final class NoSolutionPool extends AbstractSolutionPool {

	protected final static NoSolutionPool SINGLETON = new NoSolutionPool();


	
	protected NoSolutionPool() {
		super(null, 0);
	}


	@Override
	public List<Solution> asList() {
		return Collections.emptyList();
	}


	@Override
	public Solution getBestSolution() {
		return null;
	}

	@Override
	public void recordSolution(Solver solver) {}


	@Override
	public int size() {
		return capacity;
	}

	
}




class InfiniteSolutionPool extends AbstractSolutionPool {

	/**
	 * The historical record of solutions that were found
	 */
	protected final LinkedList<Solution> solutions = new LinkedList<Solution>();



	public InfiniteSolutionPool(AbstractGlobalSearchStrategy strategy) {
		super(strategy, Integer.MAX_VALUE);
	}

	@Override
	public final List<Solution> asList() {
		return Collections.unmodifiableList(solutions);
	}

	@Override
	public void clear() {
		solutions.clear();
	}

	@Override
	public Solution getBestSolution() {
		return solutions.peekFirst();
	}

	@Override
	public void recordSolution(Solver solver) {
		final Solution sol = new Solution(strategy.solver);
		strategy.writeSolution(sol);
		solutions.addFirst(sol);
	}
}

class FifoSolutionPool extends AbstractSolutionPool {
	
	/**
	 * The historical record of solutions that were found
	 */
	protected final LinkedList<Solution> solutions = new LinkedList<Solution>();
	
	protected FifoSolutionPool(AbstractGlobalSearchStrategy strategy,
			int capacity) {
		super(strategy, capacity);
		for (int i = 0; i < capacity; i++) {
			solutions.add(new Solution(strategy.solver));
		}
	}

	@Override
	public List<Solution> asList() {
		return Collections.<Solution>unmodifiableList(solutions.subList(0, size()));
	}

	@Override
	public Solution getBestSolution() {
		return isEmpty() ? null : solutions.peekFirst();
	}

	@Override
	public void recordSolution(Solver solver) {
		final Solution sol = solutions.removeLast();
		strategy.writeSolution(sol);
		solutions.addFirst(sol);
	}

	@Override
	public void resizeCapacity(int capacity) {
		if(capacity > this.capacity) {
			for (int i = this.capacity; i < capacity; i++) {
				solutions.add(new Solution(strategy.solver));
			}
		} else if(capacity < this.capacity) {
			for (int i = capacity; i < this.capacity; i++) {
				solutions.removeLast();
			}
		}
		this.capacity = capacity;
	}
	
	
	
}
