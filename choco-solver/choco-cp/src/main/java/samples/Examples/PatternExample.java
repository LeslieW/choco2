/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package samples.Examples;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;

import java.util.logging.Level;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 9 janv. 2009
 * Since : Choco 2.0.1
 * Update : Choco 2.0.1
 */
public abstract class PatternExample implements Example {

	public Model _m;

	public Solver _s;

	public void setUp(Object parameters){
	}


	public abstract void buildModel();

	public abstract void buildSolver();

	public abstract void solve();

	public abstract void prettyOut();

	public final void execute(Object parameters){
		LOGGER.log(Level.INFO,"\nexecute {0} ...", getClass().getSimpleName());
		this.setUp(parameters);
		this.buildModel();
		this.buildSolver();
		this.solve();
		this.prettyOut();
		if(LOGGER.isLoggable(Level.INFO)) {
			if( _s == null) {
				LOGGER.info("\n***********\n solver object is null.");
			}else {
				LOGGER.log(Level.INFO, "\n***********\n#sol : {0}\n{1}", new Object[]{ _s.getSolutionCount(), _s.runtimeStatistics()});
			}
			ChocoLogging.flushLogs();
		}
	}


	@Override
	public void execute() {
		execute(null);		
	}
	
	

}
