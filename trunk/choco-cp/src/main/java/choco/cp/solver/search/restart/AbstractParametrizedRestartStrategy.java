package choco.cp.solver.search.restart;

import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.limit.Limit;

public abstract class AbstractParametrizedRestartStrategy extends
		AbstractRestartStrategyOnLimit implements ParametrizedRestartStrategy {

	protected int scaleFactor=1;

	protected double geometricalFactor=1;

	public AbstractParametrizedRestartStrategy(Limit type, int scaleFactor, double geometricalFactor) {
		super(type, scaleFactor);
		this.setScaleFactor(scaleFactor);
		this.setGeometricalFactor(geometricalFactor);
		
	}
	

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	protected final static void checkNonNegative(double v) {
		if(v<1) {throw new SolverException("scale and geom. factor should be a positive numbers");}
	}

	public final int getScaleFactor() {
		return scaleFactor;
	}

	public final void setScaleFactor(int scaleFactor) {
		checkNonNegative(scaleFactor);
		this.scaleFactor = scaleFactor;
	}

	public final double getGeometricalFactor() {
		return geometricalFactor;
	}

	public void setGeometricalFactor(double geometricalFactor) {
		checkNonNegative(geometricalFactor);
		this.geometricalFactor = geometricalFactor;
	}

}
