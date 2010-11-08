/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
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
*                   N. Jussien    1999-2010      *
**************************************************/
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
public class ExamplesTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    public void donaldGeraldRobert() {
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
        s.visualize(v);
        s.launch();
        s.printRuntimeStatistics();
        // Print name value
        LOGGER.info("donald = " + s.getVar(_d).getVal() + s.getVar(_o).getVal() + s.getVar(_n).getVal() + s.getVar(_a).getVal() + s.getVar(_l).getVal() + s.getVar(_d).getVal());
        LOGGER.info("gerald = " + s.getVar(_g).getVal() + s.getVar(_e).getVal() + s.getVar(_r).getVal() + s.getVar(_a).getVal() + s.getVar(_l).getVal() + s.getVar(_d).getVal());
        LOGGER.info("robert = " + s.getVar(_r).getVal() + s.getVar(_o).getVal() + s.getVar(_b).getVal() + s.getVar(_e).getVal() + s.getVar(_r).getVal() + s.getVar(_t).getVal());
        v.kill();
    }

    public void testPicross() {
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
        s.visualize(v);
        s.launch();
        v.kill();
    }

    public static void main(String[] args) {
        new ExamplesTest().donaldGeraldRobert();
        new ExamplesTest().testPicross();
    }

}
