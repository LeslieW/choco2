package choco.kernel.solver.search.restart;

import choco.kernel.common.util.tools.MathUtils;

public final class LubyRestartStrategy  extends AbstractRestartStrategy {
	
	private int geometricalIntFactor;

	private int divFactor;


	public LubyRestartStrategy(int scaleFactor,
			int geometricalFactor) {
		super("LUBY", scaleFactor, geometricalFactor);
	}

	@Override
	public final void setGeometricalFactor(double geometricalFactor) {
		checkPositiveValue(geometricalFactor);
		double f= Math.floor(geometricalFactor);
		if(f != geometricalFactor) {throw new IllegalArgumentException("Luby geometrical parameter should be an integer");}
		super.setGeometricalFactor(geometricalFactor);
		geometricalIntFactor = (int) geometricalFactor;
		divFactor = geometricalIntFactor - 1;
	}

	private static final int geometricalSum(int value, int exponent) {
		return  ( MathUtils.pow(value,exponent)-1 ) / ( value -1 );
	}
	

	protected final int getLasVegasCoef(int i) {
		//<hca> I round it to PRECISION because of issues between versions of the jvm on mac and pc
		final double log = MathUtils.roundedLog( i * divFactor + 1,geometricalIntFactor);
		final int k = (int) Math.floor(log);
		if(log == k) {
			return MathUtils.pow(geometricalIntFactor,k-1);
		}else {
			//recursion
			return getLasVegasCoef(i - geometricalSum(geometricalIntFactor, k));
		}
	}


	@Override
	public int getNextCutoff(int nbRestarts) {
		return getLasVegasCoef(nbRestarts +1)*scaleFactor;
	}
	
}
