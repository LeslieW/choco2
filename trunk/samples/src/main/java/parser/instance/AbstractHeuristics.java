package parser.instance;

public abstract class AbstractHeuristics implements IHeuristicAlgorithm {
	
	protected int objective;
	
	private boolean executed = false;
	
	private double time;

	@Override
	public void reset() {
		executed = false;
		objective = Integer.MAX_VALUE;
	}
	
	@Override
	public boolean isObjectiveOptimal() {
		return false;
	}
	
	@Override
	public int getIterationCount() {
		return hasSearched() ? 1 : 0;
	}


	@Override
	public int getSolutionCount() {
		return hasSearched() ? 1 : 0;
	}



	@Override
	public final void execute() {
		time = - System.currentTimeMillis();
		objective = apply();
		time += System.currentTimeMillis();
		time/=1000;
		executed = true;
	}



	protected abstract int apply();


	@Override
	public final Number getObjectiveValue() {
		return Integer.valueOf(objective);
	}

	@Override
	public final double getTimeCount() {
		return time;
	}

	@Override
	public final boolean hasSearched() {
		return executed;
	}


	@Override
	public boolean existsSolution() {
		return hasSearched();
	}
	
	
}

