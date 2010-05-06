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
package choco.visu.components.bricks;

import choco.kernel.solver.search.ISearchLoop;
import choco.kernel.solver.variables.Var;
import choco.visu.components.papplets.AChocoPApplet;
import choco.visu.components.papplets.DottyTreeSearchPApplet;
import choco.visu.searchloop.ObservableStepSearchLoop;
import choco.visu.searchloop.State;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 12 nov. 2008
 * Since : Choco 2.0.1
 *
 * {@code DotBrick} is a {@IChocoBrick} used inside {@code DottyTreeSearchPApplet}.
 * It is used to deal with tree search representation in a Dotty file.
 */

public final class DotBrick extends AChocoBrick{

    public DotBrick(AChocoPApplet chopapplet, Var var) {
        super(chopapplet, var);
    }

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public final void refresh(final Object arg) {
        if (arg instanceof ISearchLoop) {
            ObservableStepSearchLoop ossl = (ObservableStepSearchLoop)arg;
            State state = ossl.state;
            switch (state) {
                case SOLUTION:
                    ((DottyTreeSearchPApplet)chopapplet).updateNodes(this.var.getName()+ ':' +getValues(), true);
                    break;
                case DOWN:
                    ((DottyTreeSearchPApplet)chopapplet).updateNodes(this.var.getName()+ ':' +getValues(), false);
                    ((DottyTreeSearchPApplet)chopapplet).updateEdges(false);
                    break;
                case UP:
                    ((DottyTreeSearchPApplet)chopapplet).updateEdges(true);
                    break;
                case END:
                    ((DottyTreeSearchPApplet)chopapplet).printGraph();
                    ((DottyTreeSearchPApplet)chopapplet).clean(true);
                    break;
                case RESTART:
                    ((DottyTreeSearchPApplet)chopapplet).clean(false);
                default:
                    break;
            }
        }
    }

    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public final void drawBrick(int x, int y, int widht, int height) {
        // nothing to do
    }
}
