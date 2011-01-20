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
package samples.tutorials.visu;

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.visu.Visu;
import choco.visu.components.panels.VarChocoPanel;
import samples.tutorials.trunk.Picross;
import samples.tutorials.visu.papplet.DonaldAndFriendsPApplet;
import samples.tutorials.visu.papplet.PicrossPApplet;

import java.util.logging.Logger;

import static choco.visu.components.papplets.ChocoPApplet.TREESEARCH;
import static samples.tutorials.seminar.ExDonaldGeraldRobert.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 8 nov. 2010
 */
public final class ExamplesTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    public static void donaldGeraldRobert() {
        Model m = modelIt2();
        Solver s = new CPSolver();
        s.read(m);
        Visu v = Visu.createVisu(0, 0);
        Variable[] vars = new Variable[]{
                _d, _o, _n, _a, _l, _d,
                _g, _e, _r, _a, _l, _d,
                _r, _o, _b, _e, _r, _t
        };
        v.addPanel(new VarChocoPanel("Grid", vars, DonaldAndFriendsPApplet.class, null));
        v.addPanel(new VarChocoPanel("TreeSearch", vars, TREESEARCH, null));

        s.generateSearchStrategy();
        v.listen(s);
        s.launch();
        s.printRuntimeStatistics();
        // Print name value
        LOGGER.info("donald = " + s.getVar(_d).getVal() + s.getVar(_o).getVal() + s.getVar(_n).getVal() + s.getVar(_a).getVal() + s.getVar(_l).getVal() + s.getVar(_d).getVal());
        LOGGER.info("gerald = " + s.getVar(_g).getVal() + s.getVar(_e).getVal() + s.getVar(_r).getVal() + s.getVar(_a).getVal() + s.getVar(_l).getVal() + s.getVar(_d).getVal());
        LOGGER.info("robert = " + s.getVar(_r).getVal() + s.getVar(_o).getVal() + s.getVar(_b).getVal() + s.getVar(_e).getVal() + s.getVar(_r).getVal() + s.getVar(_t).getVal());
        v.kill();
    }

    public static void testPicross() {
        Solver s = new CPSolver();

        int[][] cols = new int[][]{
                {2, 2},
                {2, 4},
                {1, 2, 2},
                {2, 1, 1, 2},
                {1, 1, 1, 3, 1},
                {2, 1, 2, 1, 5, 1, 1},
                {1, 2, 2, 8, 1, 1},
                {2, 2, 3, 6, 1, 1},
                {2, 12, 1, 1},
                {2, 10, 1},
                {9, 5, 2},
                {4, 2},
                {3, 2, 8},
                {3, 2, 1, 2},
                {3, 3, 1, 5},
                {2, 4, 1, 1, 2},
                {2, 2, 1, 1, 1},
                {1, 1, 4, 2, 2, 2},
                {1, 1, 1, 2, 3},
                {3, 2, 1},
                {4, 3},
                {8},
                {5}
        };
        int[][] rows = new int[][]
                {
                        {0},
                        {3, 2},
                        {1, 2, 3, 4},
                        {1, 2, 3, 3},
                        {4, 2, 2, 6},
                        {2, 1, 1, 2, 3},
                        {5, 2, 1, 2, 3, 2},
                        {2, 2, 1, 1, 2, 1, 1, 2},
                        {1, 3, 1, 3, 2, 3},
                        {2, 1, 2, 7},
                        {9, 1, 2, 2},
                        {2, 1, 3},
                        {1, 8},
                        {1, 6, 7},
                        {2, 9, 2},
                        {1, 10, 1},
                        {6, 3, 2},
                        {1, 5, 1, 1, 2},
                        {2, 2, 1, 1},
                        {7, 2, 1},
                        {2, 2, 1},
                        {6, 3},
                        {0}
                };


        Picross p = new Picross(rows, cols, s);

        Visu v = Visu.createVisu();
        Variable[] vars = ArrayUtils.append(p.myvars);
        v.addPanel(new VarChocoPanel("Picross", vars, PicrossPApplet.class, new int[]{rows.length, cols.length}));
        s.generateSearchStrategy();
        v.listen(s);
        s.launch();
        v.kill();
    }

    public static void main(String[] args) {
        donaldGeraldRobert();
        testPicross();
    }

}
