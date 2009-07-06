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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.visu.components.ColorConstant;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.papplets.AChocoPApplet;
import choco.visu.papplet.QueenBoardPApplet;
import processing.core.PShape;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 */

public class QueenBrick extends AChocoBrick{

    private boolean isinstanciated;
    private int value;

    public QueenBrick(final AChocoPApplet chopapplet, final Var var) {
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
        int name = Integer.valueOf(var.getName().substring(1));
        PShape pion;
        if(isinstanciated){
            name += ((value-1)*8);
             pion = ((QueenBoardPApplet)chopapplet).card.getChild("pion"+name);
            pion.disableStyle();
            chopapplet.fill(ColorConstant.BLACK);
            chopapplet.shape(pion, 0, 0);
            chopapplet.noFill();
        }else{
            DisposableIntIterator it = ((IntDomainVar)var).getDomain().getIterator();
            while(it.hasNext()){
                int val = it.next();
                val = name + ((val-1)*8);
                pion = ((QueenBoardPApplet)chopapplet).card.getChild("pion"+val);
                pion.disableStyle();
                chopapplet.fill(ColorConstant.GRAY);
                chopapplet.shape(pion, 0, 0);
                chopapplet.noFill();
            }
            it.dispose();
        }
    }
}