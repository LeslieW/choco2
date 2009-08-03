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
package choco.visu.brick;

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.solver.variables.Var;
import choco.visu.components.ColorConstant;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.papplets.AChocoPApplet;
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
 **************************************************
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code ColorValueBrick} is a particular {@code IChocoBrick} where the domain of the variable is displayed as
 * a colored square.
 * A green square represents a value inside the domain,
 * A blue one representes a value out of the domain.
 * 
 * Powered by Processing    (http://processing.org/)
 *
 */

public final class PicrossBrick extends AChocoBrick{

    private boolean isinstanciated;

    /**
     * Constructor of {@code ColorValueBrick}
     * @param chopapplet
     * @param var
     */
    public PicrossBrick(final AChocoPApplet chopapplet, final Var var) {
        super(chopapplet, var);
        this.isinstanciated = false;
    }

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public final void refresh(final Object arg) {
        if(((IntDomainVarImpl)var).isInstantiatedTo(1)){
            isinstanciated = true;
        }else{
            isinstanciated = false;
        }
    }


    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public final void drawBrick(final int x, final int y, final int width, final int height) {
        chopapplet.noStroke();
        if(isinstanciated){
                chopapplet.fill(ColorConstant.BLACK);
            }else{
                chopapplet.fill(ColorConstant.WHITE);
            }
            chopapplet.rect(y, x , width, height);
    }
}