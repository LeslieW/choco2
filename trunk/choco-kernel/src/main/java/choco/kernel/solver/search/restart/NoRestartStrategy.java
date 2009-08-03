package choco.kernel.solver.search.restart;

public final class NoRestartStrategy implements UniversalRestartStrategy {
	
	public final static String NO_RESTART_NAME = "NO_RESTART";
	
	public final static NoRestartStrategy SINGLOTON = new NoRestartStrategy();
	
	private NoRestartStrategy() {
		super();
	}

	@Override
	public double getGeometricalFactor() {
		return 0;
	}

	@Override
	public String getName() {
		return "NO_RESTART";
	}

	@Override
	public int getScaleFactor() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void setGeometricalFactor(double geometricalFactor) {}

	@Override
	public void setScaleFactor(int scaleFactor) {}

	@Override
	public String pretty() {
		return getName();
	}

	@Override
	public int getNextCutoff(int nbRestarts) {
		return Integer.MAX_VALUE;
	}

}
