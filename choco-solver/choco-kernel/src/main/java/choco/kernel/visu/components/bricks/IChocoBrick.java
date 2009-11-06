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
package choco.kernel.visu.components.bricks;

import choco.kernel.solver.variables.Var;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 24 oct. 2008
 * Since : Choco 2.0.1
 *
 * Interface {@code IChocoBrick} is the one of the root of the visualization in Choco.
 * It allows the definition of the way a variable will be displayed inside the visualization.
 */

public interface IChocoBrick {

    /**
     * Returns the associated Var object
     * @return var
     */
    public Var getVar();

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     * @param arg an object to precise the refreshing
     */
    public void refresh(final Object arg);

    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public void drawBrick(final int x, final int y, final int widht, final int height);
}
