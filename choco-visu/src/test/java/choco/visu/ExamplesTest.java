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
package choco.visu;

import static choco.Choco.allDifferent;
import static choco.Choco.eq;
import static choco.Choco.leq;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeIntVarArray;
import static choco.Choco.minus;
import static choco.Choco.neq;
import static choco.Choco.plus;
import static choco.Choco.scalar;
import static choco.Choco.sum;
import static choco.visu.components.papplets.ChocoPApplet.FULLDOMAIN;
import static choco.visu.components.papplets.ChocoPApplet.GRID;
import static choco.visu.components.papplets.ChocoPApplet.SUDOKU;
import static choco.visu.components.papplets.ChocoPApplet.TREESEARCH;
import static samples.seminar.ExDonaldGeraldRobert._a;
import static samples.seminar.ExDonaldGeraldRobert._b;
import static samples.seminar.ExDonaldGeraldRobert._d;
import static samples.seminar.ExDonaldGeraldRobert._e;
import static samples.seminar.ExDonaldGeraldRobert._g;
import static samples.seminar.ExDonaldGeraldRobert._l;
import static samples.seminar.ExDonaldGeraldRobert._n;
import static samples.seminar.ExDonaldGeraldRobert._o;
import static samples.seminar.ExDonaldGeraldRobert._r;
import static samples.seminar.ExDonaldGeraldRobert._t;
import static samples.seminar.ExDonaldGeraldRobert.modelIt2;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;

import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import samples.Picross;
import choco.Choco;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.visu.components.panels.VarChocoPanel;
import choco.visu.papplet.ColoringPApplet;
import choco.visu.papplet.DonaldAndFriendsPApplet;
import choco.visu.papplet.KnapsackPApplet;
import choco.visu.papplet.PicrossPApplet;
import choco.visu.papplet.QueenBoardPApplet;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 oct. 2008
 */

public class ExamplesTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    @Before
    public void checkEnvironment(){
        GraphicsEnvironment ge = GraphicsEnvironment
        .getLocalGraphicsEnvironment();
        if(ge.isHeadlessInstance()){
            System.exit(0);
        }
    }

    public static void main(String[] args){
        ExamplesTest e = new ExamplesTest();
        e.checkEnvironment();
//        e.donaldGeraldRobert();
//        e.knapsack();
//        e.magicSquare();
//        e.queens();
//        e.sudokuAdvanced();
//        e.usa();
        e.testPicross();
    }



    @Test
    public void sudokuAdvanced() {

        int[][] instance = new int[][]{
                {0, 0, 7, 5, 0, 0, 3, 0, 0},
                {0, 4, 0, 0, 2, 0, 1, 0, 0},
                {1, 0, 0, 0, 7, 0, 0, 5, 0},
                {0, 0, 3, 1, 4, 0, 2, 0, 6},
                {4, 0, 0, 0, 6, 2, 7, 0, 0},
                {0, 6, 5, 0, 3, 0, 0, 0, 8},
                {0, 7, 1, 0, 0, 0, 6, 0, 0},
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 0, 7, 0, 0, 0, 4, 1}
        };

        int n = instance.length;
        // Build model
        Model m = new CPModel();
        // Declare variables
        IntegerVariable[][] cols = new IntegerVariable[n][n];
        IntegerVariable[][] rows = makeIntVarArray("rows", n, n, 1, n);

        // Channeling between rows and columns
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cols[i][j] = rows[j][i];
            }
        }

        // Add alldifferent constraint
        for (int i = 0; i < n; i++) {
            m.addConstraint(allDifferent(cols[i]));
            m.addConstraint(allDifferent(rows[i]));
        }
        // Define sub regions
        IntegerVariable[][] carres = new IntegerVariable[n][n];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    carres[j + k * 3][i] = rows[(k * 3)][i + j * 3];
                    carres[j + k * 3][i + 3] = rows[1 + k * 3][i + j * 3];
                    carres[j + k * 3][i + 6] = rows[2 + k * 3][i + j * 3];
                }
            }
        }

        // Add alldifferent on sub regions
        for (int i = 0; i < n; i++) {
            Constraint c = allDifferent(carres[i]);
            m.addConstraint(c);
        }

        // Read the instance
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (instance[i][j] != 0) {
                    Constraint c = eq(rows[i][j], instance[i][j]);
                    m.addConstraint(c);
                }
            }
        }

        // Build solver
        Solver s = new CPSolver();

        // Read model
        s.read(m);
        Visu v = Visu.createVisu(200, 200);
//        Visu v = Visu.createFullVisu(200, 200);
        Variable[] vars = new Variable[9 * 9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(rows[i], 0, vars, i * 9, 9);
        }
        v.addPanel(new VarChocoPanel("Grid", vars, SUDOKU, null));
        v.addPanel(new VarChocoPanel("Domain", vars, FULLDOMAIN, null));

        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();

        v.kill();
    }


    @Test
    public void magicSquare() {
        int n = 4;
        int magicSum = n * (n * n + 1) / 2;
        Model m = new CPModel();

        IntegerVariable[][] var = new IntegerVariable[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                var[i][j] = makeIntVar("var_" + i + "_" + j, 1, n * n);
                // Associate the variable to the model.
                m.addVariable(var[i][j]);
            }
        }

        // All cells of the matrix must be different
        for (int i = 0; i < n * n; i++) {
            for (int j = i + 1; j < n * n; j++) {
                m.addConstraint(neq(var[i / n][i % n], var[j / n][j % n]));
            }
        }

        // All rows must be equal to the magic sum
        for (int i = 0; i < n; i++) {
            m.addConstraint(eq(sum(var[i]), magicSum));
        }

        IntegerVariable[][] varCol = new IntegerVariable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Copy of var in the column order
                varCol[i][j] = var[j][i];
            }
            // Each column's sum is equal to the magic sum
            m.addConstraint(eq(sum(varCol[i]), magicSum));
        }

        IntegerVariable[] varDiag1 = new IntegerVariable[n];
        IntegerVariable[] varDiag2 = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            // Copy of var in varDiag1
            varDiag1[i] = var[i][i];
            // Copy of var in varDiag2
            varDiag2[i] = var[(n - 1) - i][i];
        }
        // Every diagonal have to be equal to the magic sum
        m.addConstraint(eq(sum(varDiag1), magicSum));
        m.addConstraint(eq(sum(varDiag2), magicSum));

//        Solver s = new DotCPSolver("/home/charles/Bureau/toto.dot");
        Solver s = new CPSolver();
        // Read the model
        s.read(m);

//        s.solve();
        Visu v = Visu.createVisu();
//          Visu v = Visu.createFullVisu();
        Variable[] vars = new Variable[n * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(var[i], 0, vars, i * n, n);
        }
        v.addPanel(new VarChocoPanel("Grid", vars, GRID, null));
        v.addPanel(new VarChocoPanel("TreeSearch", vars, TREESEARCH, null));
//        v.addPanel(new VarChocoPanel("FullDomain", vars, FULLDOMAIN, null));
//        v.addPanel(new VarChocoPanel("NameOrValue", vars, NAMEORVALUE, null));
//        v.addPanel(new VarChocoPanel("ColorOrValue", vars, COLORORVALUE, null));
//        v.addPanel(new VarChocoPanel("Dotty", vars, DOTTYTREESEARCH, new Object[]{DotTest.createDotFileName("choco"), 100, null, null, null}));
//

        // Solve the model
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
        for (int i = 0; i < n; i++) {
        StringBuffer st = new StringBuffer();
            for (int j = 0; j < n; j++) {
                st.append(s.getVar(var[i][j]).getVal()).append(" ");
            }
            LOGGER.info(st.toString());
        }
        v.kill();
    }

    @Test
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

        s.setFirstSolution(true);
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


        private static final TObjectIntHashMap<String> states;
    static{
        states = new TObjectIntHashMap<String>();
        states.put("WA",0);
        states.put("OR",1);
        states.put("CA",2);
        states.put("ID",3);
        states.put("NV",4);
        states.put("MT",5);
        states.put("WY",6);
        states.put("UT",7);
        states.put("AZ",8);
        states.put("CO",9);
        states.put("NM",10);
        states.put("ND",11);
        states.put("SD",12);
        states.put("NE",13);
        states.put("KS",14);
        states.put("OK",15);
        states.put("TX",16);
        states.put("MN",17);
        states.put("IA",18);
        states.put("MO",19);
        states.put("AR",20);
        states.put("LA",21);
        states.put("WI",22);
        states.put("IL",23);
        states.put("MS",24);
        states.put("MI",25);
        states.put("IN",26);
        states.put("KY",27);
        states.put("TN",28);
        states.put("AL",29);
        states.put("GA",30);
        states.put("OH",31);
        states.put("FL",32);
        states.put("SC",33);
        states.put("NC",34);
        states.put("VA",35);
        states.put("MD",36);
        states.put("WV",37);
        states.put("DE",38);
        states.put("NJ",39);
        states.put("PA",40);
        states.put("NY",41);
        states.put("VT",42);
        states.put("ME",43);
        states.put("NH",44);
        states.put("MA",45);
        states.put("CT",46);
        states.put("RI",47);
        states.put("AK",48);
        states.put("HI",49);
    }

    @Test
    public void usa() {
        Model m = new CPModel();
        Solver s = new PreProcessCPSolver();
        int nbColor = 4;
        TIntObjectHashMap<String> invStates = new TIntObjectHashMap<String>();
        for(int i = 0; i< states.keys().length; i++){
            String name = (String) states.keys()[i];
            invStates.put(states.get(name), name);
        }


        IntegerVariable[] etats = new IntegerVariable[states.keys().length];
        for(int i = 0; i < states.keys().length; i++){
            etats[i] = makeIntVar(invStates.get(i), 1, nbColor);
        }
        m.addVariables(etats);

        //Contraintes de voisinage
        m.addConstraint(neq(etats[states.get("WA")], etats[states.get("ID")]));
        m.addConstraint(neq(etats[states.get("WA")], etats[states.get("OR")]));

        m.addConstraint(neq(etats[states.get("OR")], etats[states.get("ID")]));
        m.addConstraint(neq(etats[states.get("OR")], etats[states.get("NV")]));
        m.addConstraint(neq(etats[states.get("OR")], etats[states.get("CA")]));

        m.addConstraint(neq(etats[states.get("CA")], etats[states.get("NV")]));
        m.addConstraint(neq(etats[states.get("CA")], etats[states.get("AZ")]));

        m.addConstraint(neq(etats[states.get("ID")], etats[states.get("MT")]));
        m.addConstraint(neq(etats[states.get("ID")], etats[states.get("WY")]));
        m.addConstraint(neq(etats[states.get("ID")], etats[states.get("UT")]));
        m.addConstraint(neq(etats[states.get("ID")], etats[states.get("NV")]));

        m.addConstraint(neq(etats[states.get("NV")], etats[states.get("UT")]));
        m.addConstraint(neq(etats[states.get("NV")], etats[states.get("AZ")]));

        m.addConstraint(neq(etats[states.get("UT")], etats[states.get("WY")]));
        m.addConstraint(neq(etats[states.get("UT")], etats[states.get("CO")]));
        m.addConstraint(neq(etats[states.get("UT")], etats[states.get("AZ")]));

        m.addConstraint(neq(etats[states.get("AZ")], etats[states.get("NM")]));

        m.addConstraint(neq(etats[states.get("AZ")], etats[states.get("NM")]));

        m.addConstraint(neq(etats[states.get("MT")], etats[states.get("WY")]));
        m.addConstraint(neq(etats[states.get("MT")], etats[states.get("ND")]));
        m.addConstraint(neq(etats[states.get("MT")], etats[states.get("SD")]));

        m.addConstraint(neq(etats[states.get("AZ")], etats[states.get("ND")]));
        m.addConstraint(neq(etats[states.get("AZ")], etats[states.get("SD")]));

        m.addConstraint(neq(etats[states.get("WY")], etats[states.get("SD")]));
        m.addConstraint(neq(etats[states.get("WY")], etats[states.get("NE")]));
        m.addConstraint(neq(etats[states.get("WY")], etats[states.get("CO")]));

        m.addConstraint(neq(etats[states.get("CO")], etats[states.get("NE")]));
        m.addConstraint(neq(etats[states.get("CO")], etats[states.get("KS")]));
        m.addConstraint(neq(etats[states.get("CO")], etats[states.get("OK")]));
        m.addConstraint(neq(etats[states.get("CO")], etats[states.get("NM")]));

        m.addConstraint(neq(etats[states.get("NM")], etats[states.get("TX")]));
        m.addConstraint(neq(etats[states.get("NM")], etats[states.get("OK")]));

        m.addConstraint(neq(etats[states.get("ND")], etats[states.get("MN")]));
        m.addConstraint(neq(etats[states.get("ND")], etats[states.get("SD")]));

        m.addConstraint(neq(etats[states.get("SD")], etats[states.get("MN")]));
        m.addConstraint(neq(etats[states.get("SD")], etats[states.get("IA")]));
        m.addConstraint(neq(etats[states.get("SD")], etats[states.get("NE")]));

        m.addConstraint(neq(etats[states.get("NE")], etats[states.get("MO")]));
        m.addConstraint(neq(etats[states.get("NE")], etats[states.get("IA")]));
        m.addConstraint(neq(etats[states.get("NE")], etats[states.get("KS")]));

        m.addConstraint(neq(etats[states.get("KS")], etats[states.get("MO")]));
        m.addConstraint(neq(etats[states.get("KS")], etats[states.get("OK")]));

        m.addConstraint(neq(etats[states.get("OK")], etats[states.get("MO")]));
        m.addConstraint(neq(etats[states.get("OK")], etats[states.get("AR")]));
        m.addConstraint(neq(etats[states.get("OK")], etats[states.get("TX")]));

        m.addConstraint(neq(etats[states.get("TX")], etats[states.get("AR")]));
        m.addConstraint(neq(etats[states.get("TX")], etats[states.get("LA")]));

        m.addConstraint(neq(etats[states.get("MN")], etats[states.get("WI")]));
        m.addConstraint(neq(etats[states.get("MN")], etats[states.get("MI")]));
        m.addConstraint(neq(etats[states.get("MN")], etats[states.get("IA")]));

        m.addConstraint(neq(etats[states.get("IA")], etats[states.get("WI")]));
        m.addConstraint(neq(etats[states.get("IA")], etats[states.get("IL")]));
        m.addConstraint(neq(etats[states.get("IA")], etats[states.get("MO")]));

        m.addConstraint(neq(etats[states.get("MO")], etats[states.get("IL")]));
        m.addConstraint(neq(etats[states.get("MO")], etats[states.get("KY")]));
        m.addConstraint(neq(etats[states.get("MO")], etats[states.get("TN")]));
        m.addConstraint(neq(etats[states.get("MO")], etats[states.get("AR")]));

        m.addConstraint(neq(etats[states.get("AR")], etats[states.get("TN")]));
        m.addConstraint(neq(etats[states.get("AR")], etats[states.get("MS")]));
        m.addConstraint(neq(etats[states.get("AR")], etats[states.get("LA")]));

        m.addConstraint(neq(etats[states.get("LA")], etats[states.get("MS")]));

        m.addConstraint(neq(etats[states.get("WI")], etats[states.get("MI")]));
        m.addConstraint(neq(etats[states.get("WI")], etats[states.get("IL")]));

        m.addConstraint(neq(etats[states.get("IL")], etats[states.get("MI")]));
        m.addConstraint(neq(etats[states.get("IL")], etats[states.get("IN")]));
        m.addConstraint(neq(etats[states.get("IL")], etats[states.get("KY")]));

        m.addConstraint(neq(etats[states.get("KY")], etats[states.get("IN")]));
        m.addConstraint(neq(etats[states.get("KY")], etats[states.get("OH")]));
        m.addConstraint(neq(etats[states.get("KY")], etats[states.get("WV")]));
        m.addConstraint(neq(etats[states.get("KY")], etats[states.get("VA")]));
        m.addConstraint(neq(etats[states.get("KY")], etats[states.get("TN")]));

        m.addConstraint(neq(etats[states.get("TN")], etats[states.get("NC")]));
        m.addConstraint(neq(etats[states.get("TN")], etats[states.get("GA")]));
        m.addConstraint(neq(etats[states.get("TN")], etats[states.get("AL")]));
        m.addConstraint(neq(etats[states.get("TN")], etats[states.get("MS")]));
        m.addConstraint(neq(etats[states.get("TN")], etats[states.get("VA")]));

        m.addConstraint(neq(etats[states.get("MS")], etats[states.get("AL")]));

        m.addConstraint(neq(etats[states.get("MI")], etats[states.get("IN")]));
        m.addConstraint(neq(etats[states.get("MI")], etats[states.get("OH")]));


        m.addConstraint(neq(etats[states.get("IN")], etats[states.get("OH")]));
        m.addConstraint(neq(etats[states.get("IN")], etats[states.get("KY")]));

        m.addConstraint(neq(etats[states.get("AL")], etats[states.get("GA")]));
        m.addConstraint(neq(etats[states.get("AL")], etats[states.get("FL")]));

        m.addConstraint(neq(etats[states.get("FL")], etats[states.get("GA")]));

        m.addConstraint(neq(etats[states.get("OH")], etats[states.get("PA")]));
        m.addConstraint(neq(etats[states.get("OH")], etats[states.get("WV")]));

        m.addConstraint(neq(etats[states.get("GA")], etats[states.get("NC")]));
        m.addConstraint(neq(etats[states.get("GA")], etats[states.get("SC")]));

        m.addConstraint(neq(etats[states.get("NY")], etats[states.get("PA")]));
        m.addConstraint(neq(etats[states.get("NY")], etats[states.get("NJ")]));
        m.addConstraint(neq(etats[states.get("NY")], etats[states.get("CT")]));
        m.addConstraint(neq(etats[states.get("NY")], etats[states.get("MA")]));
        m.addConstraint(neq(etats[states.get("NY")], etats[states.get("VT")]));

        m.addConstraint(neq(etats[states.get("PA")], etats[states.get("NJ")]));
        m.addConstraint(neq(etats[states.get("PA")], etats[states.get("DE")]));
        m.addConstraint(neq(etats[states.get("PA")], etats[states.get("MD")]));
        m.addConstraint(neq(etats[states.get("PA")], etats[states.get("WV")]));

        m.addConstraint(neq(etats[states.get("WV")], etats[states.get("MD")]));
        m.addConstraint(neq(etats[states.get("WV")], etats[states.get("VA")]));

        m.addConstraint(neq(etats[states.get("VA")], etats[states.get("MD")]));
        m.addConstraint(neq(etats[states.get("VA")], etats[states.get("ND")]));

        m.addConstraint(neq(etats[states.get("NC")], etats[states.get("SC")]));

        m.addConstraint(neq(etats[states.get("MD")], etats[states.get("DE")]));

        m.addConstraint(neq(etats[states.get("DE")], etats[states.get("NJ")]));

        m.addConstraint(neq(etats[states.get("CT")], etats[states.get("RI")]));
        m.addConstraint(neq(etats[states.get("CT")], etats[states.get("MA")]));

        m.addConstraint(neq(etats[states.get("RI")], etats[states.get("MA")]));

        m.addConstraint(neq(etats[states.get("MA")], etats[states.get("VT")]));
        m.addConstraint(neq(etats[states.get("MA")], etats[states.get("NH")]));

        m.addConstraint(neq(etats[states.get("VT")], etats[states.get("NH")]));

        m.addConstraint(neq(etats[states.get("NH")], etats[states.get("ME")]));

        s.read(m);

        Visu v = Visu.createVisu(1024, 1600);
        v.addPanel(new VarChocoPanel("Map", etats, ColoringPApplet.class, "./images/usa.svg"));
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
//        v.kill();
    }


    @Test
       public void queens(){
    //public static void main(String [] ars){
        Model m = new CPModel();
        int n = 8;
        IntegerVariable[] queens = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = makeIntVar("Q" + (i+1), 1, n,CPOptions.V_ENUM);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
            int k = j - i;
            m.addConstraint(neq(queens[i], queens[j]));
            m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
            m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
            }
        }

        Solver s = new CPSolver();
        s.read(m);                                                                                

        Visu v = Visu.createVisu(700, 700);
        v.addPanel(new VarChocoPanel("Damier", queens, QueenBoardPApplet.class, "./images/damier.svg"));
        v.addPanel(new VarChocoPanel("TreeSearch", queens, TREESEARCH, null));
        v.addPanel(new VarChocoPanel("Domain", queens, FULLDOMAIN, null));

        s.setFirstSolution(false);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
        v.kill();

    }

    @Test
       public void xyz(){
//    public static void main(String [] ars){
        Model m = new CPModel();
        int n = 3;
        IntegerVariable x = Choco.makeIntVar("x", 0, 3);
        IntegerVariable y = Choco.makeIntVar("y", 0, 3);
        IntegerVariable z = Choco.makeIntVar("z", 0, 3);
        IntegerVariable[] vars = new IntegerVariable[]{x,y,z};

        m.addConstraint(Choco.gt(x,y));
        m.addConstraint(Choco.gt(y, z));

        Solver s = new CPSolver();
        s.read(m);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(vars)));

        Visu v = Visu.createVisu(700, 700);
        v.addPanel(new VarChocoPanel("Domain", vars, FULLDOMAIN, null));
        v.addPanel(new VarChocoPanel("TreeSearch", vars, TREESEARCH, null));

        s.setFirstSolution(false);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
        v.kill();

    }

    @Test
    public void knapsack(){
//    public static void main(String[] args){
         Model m = new CPModel();

        IntegerVariable obj1 = makeIntVar("obj1", 0, 5);
        IntegerVariable obj2 = makeIntVar("obj2", 0, 7);
        IntegerVariable obj3 = makeIntVar("obj3", 0, 10);
        IntegerVariable c = makeIntVar("power", 0, 40);
        IntegerVariable capa = makeIntVar("capa", 0, 34);
        m.addVariable(CPOptions.V_BOUND, c);

        int[] volumes = new int[]{7, 5, 3};
        int[] energy = new int[]{6, 4, 2};

        m.addConstraint(eq(scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3}), capa));
        m.addConstraint(leq(capa, 34));
        m.addConstraint(eq(scalar(energy, new IntegerVariable[]{obj1, obj2, obj3}), c));

        Solver s = new CPSolver();
        s.read(m);

        Visu v = Visu.createVisu(200, 150);
//        Visu v = Visu.createFullVisu(200, 150);
        v.addPanel(new VarChocoPanel("K", new IntegerVariable[]{obj1, obj2, obj3, c, capa}, KnapsackPApplet.class, "./images/knapsack.svg"));
        v.addPanel(new VarChocoPanel("FullDomain", new IntegerVariable[]{obj1, obj2, obj3, c, capa}, FULLDOMAIN, null));
        s.setFirstSolution(false);
        s.setDoMaximize(true);
		s.setObjective(s.getVar(c));
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
        v.kill();
    }

    @Test
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

        Visu v = Visu.createFullVisu();
        Variable[] vars = ArrayUtils.append(p.myvars);
        v.addPanel(new VarChocoPanel("Picross", vars, PicrossPApplet.class, new int[]{rows.length, cols.length}));
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.visualize(v);
        s.launch();
        v.kill();
    }
}
