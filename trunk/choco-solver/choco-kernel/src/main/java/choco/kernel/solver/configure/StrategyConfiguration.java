package choco.kernel.solver.configure;

public class StrategyConfiguration {
	
	/**
	 * indicates whether the control should stop when the first solution is found
	 */
	public boolean firstSolution = true;
	
	/**
	 * a boolean indicating whether we want to maximize (true) or minimize (false) the objective variable
	 */
	public boolean doMaximize = true;
	
		
	public final boolean isFirstSolution() {
		return firstSolution;
	}

	public final void setFirstSolution(boolean firstSolution) {
		this.firstSolution = firstSolution;
	}
	
	
}
