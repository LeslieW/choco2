package choco.kernel.solver.search.restart;


public abstract class AbstractRestartStrategy implements UniversalRestartStrategy {
	
	private final String name;
	
	protected int scaleFactor = 1;
	
	protected double geometricalFactor = 1;
	

	public AbstractRestartStrategy(String name, int scaleFactor,
			double geometricalFactor) {
		super();
		this.name = name;
		setScaleFactor(scaleFactor);
		setGeometricalFactor(geometricalFactor);
	}



	protected final static void checkPositiveValue(double value) {
		if( value <= 0) {throw new IllegalArgumentException("arguments should be strictly positive.");}
	}
	
	@Override
	public double getGeometricalFactor() {
		return geometricalFactor;
	}

	@Override
	public final String getName() {
		return name;
	}


	@Override
	public final int getScaleFactor() {
		return scaleFactor;
	}

	@Override
	public void setGeometricalFactor(double geometricalFactor) {
		checkPositiveValue(geometricalFactor);
		this.geometricalFactor = geometricalFactor;
		
	}

	@Override
	public final void setScaleFactor(int scaleFactor) {
		checkPositiveValue(scaleFactor);
		this.scaleFactor = scaleFactor;
	}
	
	
	
}