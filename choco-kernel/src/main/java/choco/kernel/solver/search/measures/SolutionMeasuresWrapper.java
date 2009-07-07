package choco.kernel.solver.search.measures;

public class SolutionMeasuresWrapper implements IOptimizationMeasures {
	
	private final ISolutionMeasures solMeasures;

	public SolutionMeasuresWrapper(ISolutionMeasures solMeasures) {
		super();
		this.solMeasures = solMeasures;
	}

	@Override
	public Number getObjectiveValue() {
		return null;
	}

	@Override
	public boolean isObjectiveOptimal() {
		return false;
	}

	@Override
	public boolean existsSolution() {
		return solMeasures.existsSolution();
	}

	@Override
	public int getSolutionCount() {
		return solMeasures.getSolutionCount();
	}
	
	
}