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
package choco.kernel.visu;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;
import choco.kernel.visu.components.panels.AVarChocoPanel;

import java.util.logging.Logger;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 27 oct. 2008
 * Since : Choco 2.0.1
 *
 * Interface {@code IVisu} is a Choco object to define visualization over Choco's objects.
 */

public interface IVisu {

       final static Logger LOGGER = ChocoLogging.getSolverLogger();
    

    /**
     * Add a new panel to the main frame of the Choco visualizer.
     * Allow user to observe variables during resolution.
     * @param vpanel the new panel to add
     */
    public void addPanel(final AVarChocoPanel vpanel);

    /**
     * Shows or hides this {@code IVisu} depending on the value of parameter
     * {@code visible}.
     * @param visible  if {@code true}, makes the {@code IVisu} visible, 
     * otherwise hides the {@code IVisu}.
     */
    public void setVisible(final boolean visible);

    /**
     * Initializes the {@code IVisu} from the {@code Solver}
     * @param s solver
     */
    public void init(final Solver s);

}
