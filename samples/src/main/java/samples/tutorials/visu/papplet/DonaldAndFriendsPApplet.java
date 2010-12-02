/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package samples.tutorials.visu.papplet;

import choco.kernel.solver.variables.Var;
import choco.kernel.visu.components.IVisuVariable;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.bricks.HazardOrValueBrick;
import choco.visu.components.papplets.AChocoPApplet;

import java.awt.*;
import java.util.ArrayList;

import static choco.visu.components.ColorConstant.BLACK;
import static choco.visu.components.ColorConstant.WHITE;
/* 
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
