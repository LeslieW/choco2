package choco.kernel.solver.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;

public final class SolutionPoolFactory {

	public static final int DEFAULT_CAPACITY = 5;
	
	private SolutionPoolFactory() {
		super();
	}

	/**
	 * pool with a nil capacity (not resizable).
	 */
	public static ISolutionPool makeEmptySolutionPool() {
		return EmptySolutionPool.SINGLETON;
	}
	
	/**
	 * pool of unit capacity, contains the last solution (not resizable);
	 * 
	 */
	public static ISolutionPool makeSingleSolutionPool() {
		return new SingleSolutionPool();
	}
	
	
	public static ISolutionPool makeLastSolutionPool() {
		return makeLastSolutionPool(DEFAULT_CAPACITY);
	}
	
	/**
	 * contains the last/best solutions (capa > 0).
	 */
	public static ISolutionPool makeLastSolutionPool(int capacity) {
		return new LastSolutionPool(capacity);
	}
	
	
	public static ISolutionPool makeFirstSolutionPool() {
		return makeFirstSolutionPool(DEFAULT_CAPACITY);
	}
	
	/**
	 * contains the first solutions, useful for a solveAll. Keep some solutions but do not record each solution ((capa > 0).
	 */
	public static ISolutionPool makeFirstSolutionPool(int capacity) {
		return new FirstSolutionPool(capacity);
	}
	
	/**
	 * record all solution (not resizable).
	 */
	public static ISolutionPool makeAllSolutionPool() {
		return new ListSolutionPool();
	}
	
	public static ISolutionPool makeDefaultSolutionPool(int capacity) {
		if(capacity == 1) return makeSingleSolutionPool();
		else if(capacity == Integer.MAX_VALUE) return makeAllSolutionPool();
		if( capacity < 1) return makeEmptySolutionPool();
		else return makeLastSolutionPool(capacity);
	}
	
	
	
}


final class SingleSolutionPool implements ISolutionPool {
	
	private Solution solution;

	@Override
	public List<Solution> asList() {
		return solution == null ? Collections.<Solution>emptyList() : Arrays.<Solution>asList(solution);
	}

	@Override
	public void clear() {
		solution = null;
	}

	@Override
	public Solution getBestSolution() {
		return solution;
	}

	@Override
	public int getCapacity() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return solution == null;
	}

	@Override
	public void recordSolution(Solver solver) {
		if(solution == null) { solution = new Solution(solver);}
		else {solution.setSolver(solver);}
		solver.getSearchStrategy().writeSolution(solution);
	}

	@Override
	public void setCapacity(int capacity) {
		if(capacity > 0) {
			LOGGER.warning("single solution pool has a unit capacity.");
		}
		
	}

	@Override
	public int size() {
		return isEmpty() ? 0 : 1;
	}
	
	
}


final class EmptySolutionPool implements ISolutionPool {

	protected final static EmptySolutionPool SINGLETON = new EmptySolutionPool();
	
	
	private EmptySolutionPool() {
		super();
	}

	@Override
	public List<Solution> asList() {
		return Collections.emptyList();
	}

	@Override
	public void clear() {}

	@Override
	public Solution getBestSolution() {
		return null;
	}

	@Override
	public int getCapacity() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public void recordSolution(Solver solver) {}

	@Override
	public void setCapacity(int capacity) {
		if(capacity > 0) {
			LOGGER.warning("empty solution pool has a nil capacity.");
		}

	}

	@Override
	public int size() {
		return 0;
	}

}



 class ListSolutionPool implements ISolutionPool {
	
	/**
	 * The historical record of solutions that were found
	 */
	protected final LinkedList<Solution> solutions = new LinkedList<Solution>();

	
	public ListSolutionPool() {
		super();
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
	public int getCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public final boolean isEmpty() {
		return solutions.isEmpty();
	}

	@Override
	public final int size() {
		return solutions.size();
	}
	
	@Override
	public void setCapacity(int capacity) {
		LOGGER.warning("the solution pool has an infinite capacity");
	}

	@Override
	public Solution getBestSolution() {
		return solutions.peekFirst();
	}

	@Override
	public void recordSolution(Solver solver) {
		Solution sol = new Solution(solver);
		solver.getSearchStrategy().writeSolution(sol);
		solutions.addFirst(sol);
	}
	
	
}

abstract class AbstractCapacitedListSolutionPool extends ListSolutionPool {
	
	/**
	 * capacity of the history record (keeping solutions)
	 */
	protected int capacity;

	public AbstractCapacitedListSolutionPool(int capacity) {
		super();
		setCapacity(capacity);
	}

	@Override
	public final int getCapacity() {
		return capacity;
	}


	
	protected void resizeCapacity() {
		while(solutions.size() > capacity) {
			solutions.removeLast();
		}
	}

	@Override
	public void setCapacity(int capacity) {
		if(capacity > 0) {
			this.capacity = capacity;
		}else {
			LOGGER.log(Level.WARNING, "Invalid capacity: {0}\nThe solution pool must record at least one solution", capacity);
			capacity = 1;
		}
		resizeCapacity();
	}
}


final class LastSolutionPool extends AbstractCapacitedListSolutionPool {


	public LastSolutionPool(int capacity) {
		super(capacity);
	}

	
	@Override
	public void recordSolution(Solver solver) {
		Solution sol;
		if (solutions.size() < capacity) {
			//this condition is activated at most capacity time
			sol = new Solution(solver);
		}else {
			//this condition is activated more oftne than the next
			sol = solutions.removeLast();
			sol.setSolver(solver);
		}
		solver.getSearchStrategy().writeSolution(sol);
		solutions.addFirst(sol);
	}

}


final class FirstSolutionPool extends AbstractCapacitedListSolutionPool {

	public FirstSolutionPool(int capacity) {
		super(capacity);
	}

	@Override
	public void recordSolution(Solver solver) {
		if (solutions.size() < capacity) {
			final Solution sol = new Solution(solver);
			solver.getSearchStrategy().writeSolution(sol);
			solutions.addFirst(sol);
		}
	}
	
	
	
}