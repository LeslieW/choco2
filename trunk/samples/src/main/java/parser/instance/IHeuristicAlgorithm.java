package parser.instance;

import java.util.logging.Logger;

import choco.Choco;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.measure.IOptimizationMeasures;
import choco.kernel.solver.search.measure.ISolutionMeasures;

public interface IHeuristicAlgorithm extends ISolutionMeasures, IOptimizationMeasures {

	public final static Logger LOGGER = ChocoLogging.makeUserLogger("heuristic");
	
	public final static IHeuristicAlgorithm SINGLOTON = new DoNothingHeuristics();
	
	public final static Integer NONE = Integer.valueOf(-1);
	
	void reset();
	
	void execute();
	
	/**
	 * indicates if the algorithm was executed
	 * @return <code><code>true</code> if a solution was found
	 */
	boolean hasSearched();

	/**
	 * get the objective if there is a solution
	 */
	Number getObjectiveValue();
	
	/**
	 * get computation time in seconds
	 * 
	 */
	double getTimeCount();
	
	/**
	 * get computation time in seconds
	 * 
	 */
	int getIterationCount();



	static final class DoNothingHeuristics implements IHeuristicAlgorithm {

		private DoNothingHeuristics() {
			super();
		}

		@Override
		public void execute() {}

		@Override
		public void reset() {}

		@Override
		public Number getObjectiveValue() {
			return Choco.MAX_UPPER_BOUND;
		}

		@Override
		public boolean isObjectiveOptimal() {
			return false;
		}

		@Override
		public int getIterationCount() {
			return 0;
		}

		@Override
		public double getTimeCount() {
			return 0;
		}

		@Override
		public boolean hasSearched() {
			return false;
		}

		@Override
		public boolean existsSolution() {
			return false;
		}

		@Override
		public int getSolutionCount() {
			return 0;
		}
		
		
		
	}
}

 
