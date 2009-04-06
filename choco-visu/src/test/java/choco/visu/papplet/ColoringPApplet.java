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
package choco.visu.papplet;

import choco.kernel.solver.variables.Var;
import choco.kernel.visu.components.IVisuVariable;
import choco.visu.brick.StateColorBrick;
import static choco.visu.components.ColorConstant.WHITE;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.papplets.AChocoPApplet;
import processing.core.PShape;

import java.awt.*;
import java.util.ArrayList;
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
 * Date: 3 nov. 2008
 */

public class ColoringPApplet extends AChocoPApplet{

    public PShape card;
    private String cardname;

    public ColoringPApplet(final Object parameters) {
        super(parameters);
        cardname = (String)parameters;
    }

    /**
     * Initialize the ChocoPApplet with the list of concerning VisuVariables
     *
     * @param list of visu variables o watch
     */
    public void initialize(ArrayList<IVisuVariable> list) {
        Var[] vars = new Var[list.size()];
        for(int i = 0; i < list.size(); i++){
            vars[i] = list.get(i).getSolverVar();
        }
        bricks = new AChocoBrick[list.size()];
        for(int i = 0; i < list.size(); i++){
            IVisuVariable vv = list.get(i);
            Var v = vv.getSolverVar();
            bricks[i] = new StateColorBrick(this, v);
            vv.addBrick(bricks[i]);
        }
        this.init();
    }

    /**
     * build the specific PApplet.
     * This method is called inside the {@code PApplet#setup()} method.
     */
    public void build() {
        //background(LIGHTBLUE);
        size(800, 1000);
        card = loadShape(cardname);
        card.disableStyle();
        smooth();  // Improves the drawing quality of the SVG
    }

    /**
     * draws the back side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, the sudoku grid is considered as a back side
     */
    public void drawBackSide() {
        background(WHITE);
        shape(card, 0, 0);
    }

    /**
     * draws the front side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, values of cells in a sudoku are considered as a back side
     */
    public void drawFrontSide() {
        for(int x = 0; x < bricks.length; x++){
            bricks[x].drawBrick(0,0,0,0);
        }
    }

    /**
     * Return the ideal dimension of the chopapplet
     *
     * @return
     */
    public Dimension getDimension() {
        return new Dimension(1300, 800);
    }
}