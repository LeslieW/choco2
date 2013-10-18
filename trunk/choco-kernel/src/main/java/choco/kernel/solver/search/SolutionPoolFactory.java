/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
	public static ISolutionPool makeNoSolution() {
		return NoSolutionPool.SINGLETON;
	}

	/**
	 * pool of unit capacity, contains the last solution (not resizable);
	 * 
	 */
	public static ISolutionPool makeLastSolution(AbstractGlobalSearchStrategy strategy) {
		return new LastSolutionPool(strategy);
	}

	/**
	 * pool of unit capacity, contains the first solution (not resizable);
	 * 
	 */
	public static ISolutionPool makeFirstSolution(AbstractGlobalSearchStrategy strategy) {
		return new FirstSolutionPool(strategy);
	}



	/**
	 * contains the last/best solutions (capa > 0).
	 */
	public static ISolutionPool makeLastSolutions(AbstractGlobalSearchStrategy strategy, int capacity) {
		return new LastSolutionsPool(strategy, capacity);
	}

	/**
	 * contains the first/worst solutions (capa > 0).
	 */
	public static ISolutionPool makeFirstSolutions(AbstractGlobalSearchStrategy strategy, int capacity) {
		return new FirstSolutionsPool(strategy, capacity);
	}

	/**
	 * record all solution.
	 */
	public static ISolutionPool makeAllSolutions(AbstractGlobalSearchStrategy strategy) {
		return new AllSolutionsPool(strategy);
	}

	public static ISolutionPool makeDefaultSolutionPool(AbstractGlobalSearchStrategy strategy, int capacity, boolean lastsols) {
		if(capacity == 1) return lastsols ? makeLastSolution(strategy) : makeFirstSolution(strategy);
		else if(capacity == Integer.MAX_VALUE) return makeAllSolutions(strategy);
		else if( capacity < 1) return makeNoSolution();
		else return lastsols ? makeLastSolutions(strategy, capacity) : makeFirstSolutions(strategy, capacity);
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
		ChocoLogging.getEngineLogger().log(Level.SEVERE, "- Solution pool: not resizable (capacity:{0}",getCapacity());
	}

	@Override
	public int size() {
		return Math.min(capacity, strategy.getSolutionCount());
	}


}

abstract class OneSolutionPool extends AbstractSolutionPool {

	protected final Solution solution;

	public OneSolutionPool(AbstractGlobalSearchStrategy strategy) {
		super(strategy, 1);
		solution = new Solution(strategy.solver);
	}

	@Override
	public Solution getBestSolution() {
		return isEmpty() ? null : solution;
	}

	@Override
	public List<Solution> asList() {
		return isEmpty() ? Collections.<Solution>emptyList() : Arrays.<Solution>asList(solution);
	}
}

final class LastSolutionPool extends OneSolutionPool {

	public LastSolutionPool(AbstractGlobalSearchStrategy strategy) {
		super(strategy);
	}

	@Override
	public void recordSolution(Solver solver) {
		strategy.writeSolution(solution);
	}
}

final class FirstSolutionPool extends OneSolutionPool {

	public FirstSolutionPool(AbstractGlobalSearchStrategy strategy) {
		super(strategy);
	}

	@Override
	public void recordSolution(Solver solver) {
		if( strategy.getSolutionCount() ==1 ) strategy.writeSolution(solution);
	}
}


final class NoSolutionPool extends AbstractSolutionPool {

	public final static NoSolutionPool SINGLETON = new NoSolutionPool();

	private NoSolutionPool() {
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
		return 0;
	}


}



final class AllSolutionsPool extends AbstractSolutionPool {

	/**
	 * The historical record of solutions that were found
	 */
	protected final LinkedList<Solution> solutions = new LinkedList<Solution>();

	public AllSolutionsPool(AbstractGlobalSearchStrategy strategy) {
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



class LastSolutionsPool extends AbstractSolutionPool {

	/**
	 * The historical record of solutions that were found
	 */
	protected final LinkedList<Solution> solutions = new LinkedList<Solution>();

	protected LastSolutionsPool(AbstractGlobalSearchStrategy strategy,
			int capacity) {
		super(strategy, capacity);
		for (int i = 0; i < capacity; i++) {
			solutions.add(new Solution(strategy.solver));
		}
	}

	@Override
	public final List<Solution> asList() {
		return Collections.<Solution>unmodifiableList(solutions.subList(0, size()));
	}

	@Override
	public final Solution getBestSolution() {
		return isEmpty() ? null : solutions.peekFirst();
	}

	@Override
	public void recordSolution(Solver solver) {
		final Solution sol = solutions.removeLast();
		strategy.writeSolution(sol);
		solutions.addFirst(sol);
	}

	@Override
	public final void resizeCapacity(int capacity) {
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


final class FirstSolutionsPool extends LastSolutionsPool{


	protected FirstSolutionsPool(AbstractGlobalSearchStrategy strategy,
			int capacity) {
		super(strategy, capacity);
	}

	@Override
	public void recordSolution(Solver solver) {
		if( strategy.getSolutionCount() <= capacity) super.recordSolution(solver); 
	}

}