/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.restart;

import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.Limit;



/**
 * @author Arnaud Malapert
 *
 */
public class GeometricalRestart extends AbstractRestartStrategyOnLimit {

	protected int scaleFactor=1;

	protected double geometricalFactor=1;

	public GeometricalRestart(Limit type, double geometricalFactor,
			int scaleFactor) {
		super(type, scaleFactor);
		this.scaleFactor = scaleFactor;
		this.setGeometricalFactor(geometricalFactor);
		
	}

	protected void checkNonNegative(double v) {
		if(v<1) {throw new SolverException("paramter should be a positive number");}
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

	@Override
	protected int getNextLimit() {
		return (int) Math.ceil( Math.pow(geometricalFactor,nbRestarts) * scaleFactor );
	}
}
