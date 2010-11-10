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

import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.visu.components.ColorConstant;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.papplets.AChocoPApplet;
import choco.visu.papplet.ColoringPApplet;
import processing.core.PShape;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 */

public class StateColorBrick extends AChocoBrick{

    private boolean isinstanciated;
    private int value;

    public StateColorBrick(final AChocoPApplet chopapplet, final Var var) {
        super(chopapplet, var);
        this.isinstanciated = false;
    }

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public void refresh(Object arg) {
        if(var.isInstantiated()){
            isinstanciated = true;
            value= ((IntDomainVar)var).getVal();
        }else{
            isinstanciated = false;
            value= -1;
        }
    }


    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public void drawBrick(final int x, final int y, final int width, final int height) {
        if(isinstanciated){
            PShape state = ((ColoringPApplet)chopapplet).card.getChild(var.getName());
            state.disableStyle();
            switch (value){
                case 1:
                    chopapplet.fill(ColorConstant.BROWN1);
                    break;
                case 2:
                    chopapplet.fill(ColorConstant.BROWN2);
                    break;
                case 3:
                    chopapplet.fill(ColorConstant.BROWN3);
                    break;
                case 4:
                    chopapplet.fill(ColorConstant.BROWN4);
                    break;
            }
            chopapplet.shape(state, 0, 0);
            chopapplet.noFill();
        }
    }
}
