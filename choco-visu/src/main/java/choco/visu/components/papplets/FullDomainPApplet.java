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
package choco.visu.components.papplets;

import choco.kernel.solver.variables.Var;
import choco.kernel.visu.components.IVisuVariable;
import static choco.visu.components.ColorConstant.BLACK;
import static choco.visu.components.ColorConstant.WHITE;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.bricks.FullDomainBrick;
import processing.core.PFont;

import java.awt.*;
import java.util.ArrayList;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code FullDomainPApplet} is the {@code AChocoPApplet} that represents domain variables as arrays of squares.
 *
 * Powered by Processing    (http://processing.org/)
 */

public final class FullDomainPApplet extends AChocoPApplet{

    private final int size = 15;
    private String[] names;
    private static int maxNameLength = 25;

    public FullDomainPApplet(final Object parameters) {
        super(parameters);
    }

    /**
     * Initialize the ChocoPApplet with the list of concerning VisuVariables
     *
     * @param list of visu variables o watch
     */
    public void initialize(final ArrayList<IVisuVariable> list) {
        final Var[] vars = new Var[list.size()];
        for(int i = 0; i < list.size(); i++){
            vars[i] = list.get(i).getSolverVar();
        }
        names = new String[list.size()];
        bricks = new AChocoBrick[list.size()];
        for(int i = 0; i < list.size(); i++){
            IVisuVariable vv = list.get(i);
            Var v = vv.getSolverVar();
            bricks[i] = new FullDomainBrick(this, v, size);
            vv.addBrick(bricks[i]);
            String n = bricks[i].getVar().getName();
            if(n.length() > 25){
                n = n.substring(0, 22);
                n  = n.concat("...");
            }
            names[i] = n;
        }
        this.init();
    }

    /**
     * Return the ideal dimension of the chopapplet
     *
     * @return
     */
    public final Dimension getDimension() {
        return new Dimension(200, 10 + (bricks.length+1)*((4*size)/3));
    }

    /**
     * build the specific PApplet.
     * This method is called inside the {@code PApplet#setup()} method.
     */
    public final void build() {
        size(200, 10 + bricks.length*(size+5));
        background(WHITE);
        PFont font = loadFont("./fonts/FreeMono-18.vlw");
        textFont(font);
        noStroke();
    }

    /**
     * draws the back side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, the sudoku grid is considered as a back side
     */
    public final void drawBackSide() {
        background(WHITE);
        for(int i = 0; i < bricks.length; i++){
            int x = 10 + i*((4*size)/3);
            int y = 20;

            fill(BLACK);
            text(names[i], y, x+size);
            fill(WHITE);
        }
    }

    /**
     * draws the front side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, values of cells in a sudoku are considered as a back side
     */
    public final void drawFrontSide() {
        for(int i = 0; i < bricks.length; i++){
            bricks[i].drawBrick(10 + i*((4*size)/3), 20 + (maxNameLength+1)*5, size-1, size-1);
        }
    }
}
