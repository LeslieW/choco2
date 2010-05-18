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
package samples.tutorials;

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

	public Model model;

	public Solver solver;
    

	public void setUp(Object parameters){
	}

    public void printDescription(){
    } 

	public abstract void buildModel();

	public abstract void buildSolver();

	public abstract void solve();

	public abstract void prettyOut();

	public final void execute(Object parameters){
	    LOGGER.log(Level.INFO, ChocoLogging.START_MESSAGE);
		LOGGER.log(Level.INFO,"* Sample library: executing {0}.java ... \n", getClass().getName());

     	this.setUp(parameters);
        this.printDescription();
		this.buildModel();
		this.buildSolver();
		this.solve();
		this.prettyOut();

        LOGGER.log(Level.INFO, "* Choco usage statistics");
     	if(LOGGER.isLoggable(Level.INFO)) {
			if( solver == null) {
				LOGGER.info(" - solver object is null. No statistics available.");
			}else {
                LOGGER.log(Level.INFO, " - #sol : {0}\n - {1}", new Object[]{ solver.getSolutionCount(), solver.runtimeStatistics()});
			}
     		ChocoLogging.flushLogs();
		}
	}


	@Override
	public void execute() {
		execute(null);		
	}
	
	

}
