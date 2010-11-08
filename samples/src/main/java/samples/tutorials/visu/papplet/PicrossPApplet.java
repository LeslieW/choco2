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
package samples.tutorials.visu.papplet;

import choco.kernel.solver.variables.Var;
import choco.kernel.visu.components.IVisuVariable;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.papplets.AChocoPApplet;
import samples.tutorials.visu.brick.PicrossBrick;

import java.awt.*;
import java.util.ArrayList;

import static choco.visu.components.ColorConstant.WHITE;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code SudokuPApplet} is the {@code AChocoPApplet} that represents a sudoku grid where variables values are printed
 * inside a cell.
 *
 * Powered by Processing    (http://processing.org/)
 */

public final class PicrossPApplet extends AChocoPApplet{

    private static final int SIZE = 15;
    private static final int MARGE = 20;
    private final int n;
    private final int m;

    public PicrossPApplet(final Object parameters) {
        super(parameters);
        int[] tmp = (int[])parameters;
        n = tmp[0];
        m = tmp[1];
    }

    /**
     * Initialize the ChocoPApplet with the list of concerning VisuVariables
     *
     * @param list of visu variables o watch
     */
    public final void initialize(final ArrayList<IVisuVariable> list) {
        final Var[] vars = new Var[list.size()];
        for(int i = 0; i < list.size(); i++){
            vars[i] = list.get(i).getSolverVar();
        }
        bricks = new AChocoBrick[list.size()];
        for(int i = 0; i < list.size(); i++){
            IVisuVariable vv = list.get(i);
            Var v = vv.getSolverVar();
            bricks[i] = new PicrossBrick(this, v);
            vv.addBrick(bricks[i]);
        }
        this.init();
    }

    /**
     * Return the ideal dimension of the chopapplet
     *
     * @return
     */
    public final Dimension getDimension() {
        return new Dimension(n * SIZE, m * SIZE);
    }

    /**
     * build the specific PApplet.
     * This method is called inside the {@code PApplet#setup()} method.
     */
    public final void build() {
        size(n * SIZE, m * SIZE);
        background(WHITE);
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
    }

    /**
     * draws the front side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, values of cells in a sudoku are considered as a back side
     */
    public final void drawFrontSide() {
        for(int x = 0; x < bricks.length; x++){
            int i = x%n;
            int j = x/m;
            bricks[x].drawBrick(MARGE + (i* SIZE), MARGE + (j* SIZE), SIZE, SIZE);
        }
    }
}