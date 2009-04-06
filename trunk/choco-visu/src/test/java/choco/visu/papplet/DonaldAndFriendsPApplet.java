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
import static choco.visu.components.ColorConstant.BLACK;
import static choco.visu.components.ColorConstant.WHITE;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.bricks.HazardOrValueBrick;
import choco.visu.components.papplets.AChocoPApplet;
import processing.core.PFont;

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
 * Date: 12 nov. 2008
 * Since : Choco 2.0.1
 */

public class DonaldAndFriendsPApplet extends AChocoPApplet {

        private final int size = 25;


        public DonaldAndFriendsPApplet(final Object parameters) {
            super(parameters);
        }

        /**
         * Initialize the ChocoPApplet with the list of concerning VisuVariables
         *
         * @param list of visu variables o watch
         */
        public void initialize(final ArrayList<IVisuVariable> list) {
            Var[] vars = new Var[list.size()];
            for (int i = 0; i < list.size(); i++) {
                vars[i] = list.get(i).getSolverVar();
            }
            bricks = new AChocoBrick[list.size()];
            for (int i = 0; i < list.size(); i++) {
                IVisuVariable vv = list.get(i);
                Var v = vv.getSolverVar();
                bricks[i] = new HazardOrValueBrick(this, v, AChocoBrick.CENTER);
                vv.addBrick(bricks[i]);
            }
            this.init();
        }

        /**
         * Return the ideal dimension of the chopapplet
         *
         * @return
         */
        public Dimension getDimension() {
            return new Dimension(200, 250);
        }

        /**
         * build the specific PApplet.
         * This method is called inside the {@code PApplet#setup()} method.
         */
        public void build() {
            size(200, 200);
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
        public void drawBackSide() {
            delay(50);
            background(WHITE);
            fill(BLACK);
            //crossruling(size);
            text("+", 5, 15 + 2 * size);
            text("=", 5, 15 + 3 * size);
            fill(WHITE);
            stroke(BLACK);
            line(5, 3 * size - 5, 180, 3 * size - 5);
        }

        /**
         * draws the front side of the representation.
         * This method is called inside the {@code PApplet#draw()} method.
         * For exemple, values of cells in a sudoku are considered as a back side
         */
        public void drawFrontSide() {
            int w = bricks.length / 3;
            for (int x = 0; x < bricks.length; x++) {
                int i = x / w;
                int j = x % w;
                bricks[x].drawBrick(39 + (i * size), 29 + (j * size), size, size);
            }
        }
    }
