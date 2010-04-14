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
	
	/**
	 * indicates whether the control applies a top-down or bottom-up search algorithm
	 */
	public boolean topDownSearch = true;
	
	/**
	 * indicates whether the control applies a singloton consistency algorithm before starting the search 
	 */
	public boolean rootSinglotonConsistency = false;
	
	/**
	 * indicates the singloton consistency algorithm considers variable with non enumerated. 
	 */
	public boolean doSinglotonBoundDomain = false;
	
	/**
	 * indicates whether the control applies a destructive lower bound before starting the search 
	 */
	public boolean destructiveLowerBound = false;
	
	public final boolean isFirstSolution() {
		return firstSolution;
	}

	public final void setFirstSolution(boolean firstSolution) {
		this.firstSolution = firstSolution;
	}

	public final boolean isTopDownSearch() {
		return topDownSearch;
	}

	public final void setTopDownSearch() {
		this.topDownSearch = true;
	}

	public final void setBottomUpSearch() {
		this.topDownSearch = false;
	}
	public final boolean isBottomUpSearch() {
		return ! topDownSearch;
	}
	
	public final boolean isRootSinglotonConsistency() {
		return rootSinglotonConsistency;
	}

	public final void setRootSinglotonConsistency(boolean rootSinglotonConsistency) {
		this.rootSinglotonConsistency = rootSinglotonConsistency;
	}

	public final boolean isDoSinglotonBoundDomain() {
		return doSinglotonBoundDomain;
	}

	public final void setDoSinglotonBoundDomain(boolean doSinglotonBoundDomain) {
		this.doSinglotonBoundDomain = doSinglotonBoundDomain;
	}

	public final boolean isDestructiveLowerBound() {
		return destructiveLowerBound;
	}

	public final void setDestructiveLowerBound(boolean destructiveLowerBound) {
		this.destructiveLowerBound = destructiveLowerBound;
	}
	
	
}
