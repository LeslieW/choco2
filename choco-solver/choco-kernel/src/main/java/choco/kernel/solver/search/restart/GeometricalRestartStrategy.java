package choco.kernel.solver.search.restart;

public final class GeometricalRestartStrategy extends AbstractRestartStrategy {

	public GeometricalRestartStrategy(int scaleFactor,
			double geometricalFactor) {
		super("GEOM", scaleFactor, geometricalFactor);
	}

	@Override
	public int getNextCutoff(int nbRestarts) {
		return (int) Math.ceil( Math.pow(geometricalFactor, nbRestarts) * scaleFactor) ; 
	}

	
}
