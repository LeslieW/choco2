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

import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public abstract class PatternExample {

    protected final static Logger LOGGER = ChocoLogging.getSamplesLogger();

    public static Model _m;

    public static Solver _s;

    public void setUp(Object parameters){
    }


	public abstract void buildModel();

    public abstract void buildSolver();

    public abstract void solve();

    public abstract void prettyOut();

    public final void execute(Object parameters){
        this.setUp(parameters);
        this.buildModel();
        this.buildSolver();
        this.solve();
        this.prettyOut();
        LOGGER.info("\n *********** ");
        LOGGER.info("#sol : " + _s.getNbSolutions());
        _s.printRuntimeSatistics();
        ChocoLogging.flushLogs();
    }

}
