/* ************************************************
 *           _       _                            *
 *          |  �(..)  |                           *
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
 *                   N. Jussien    1999-2009      *
 **************************************************/
package samples.documentation;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.automata.fast_multicostregular.FastMultiCostRegular;
import choco.kernel.model.Model;
//totex cmulticosteregular_import
import choco.kernel.model.constraints.automaton.FA.Automaton;
//totex
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 29 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* See Code4Doc1.java for more informations.
*/
public class Code4Doc4 {


    public void clexeq() {
        //totex clexeq
        Model m = new CPModel();
        Solver s = new CPSolver();
        int n = 4;
        int k = 2;
        IntegerVariable[] vs1 = new IntegerVariable[n];
        IntegerVariable[] vs2 = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            vs1[i] = makeIntVar("" + i, 0, k);
            vs2[i] = makeIntVar("" + i, 0, k);
        }
        m.addConstraint(lexeq(vs1, vs2));
        s.read(m);
        s.solve();
        //totex
    }

    public void cleximin() {
        //totex cleximin
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] u = makeIntVarArray("u", 3, 2, 5);
        IntegerVariable[] v = makeIntVarArray("v", 3, 2, 4);
        m.addConstraint(leximin(u, v));
        m.addConstraint(allDifferent(v));
        s.read(m);
        s.solve();
        //totex
    }

    public void clt() {
        //totex clt
        Model m = new CPModel();
        Solver s = new CPSolver();
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(lt(v, c));
        s.read(m);
        s.solve();
        //totex
    }

    public void cmax1() {
        //totex cmax1
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 5);
        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 1, 5);
        m.addVariables("cp:bound", x, y, z);
        m.addConstraint(max(y, z, x));
        s.read(m);
        s.solve();
        //totex
    }

    public void cmax2() {
        //totex cmax2
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] x = constantArray(new int[]{5, 7, 9, 10, 12, 3, 2});
        IntegerVariable max = makeIntVar("max", 1, 100);
        SetVariable set = makeSetVar("set", 0, x.length - 1);
        m.addConstraints(max(set, x, max), leqCard(set, constant(5)));
        s.read(m);
        s.solve();
        //totex
    }

    public void cmember() {
        //totex cmember
        Model m = new CPModel();
        Solver s = new CPSolver();
        int x = 3;
        int card = 2;
        SetVariable y = makeSetVar("y", 2, 4);
        m.addConstraint(member(y, x));
        m.addConstraint(eqCard(y, card));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cmin1() {
        //totex cmin1
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 5);
        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 1, 5);
        m.addVariables("cp:bound", x, y, z);
        m.addConstraint(min(y, z, x));
        s.read(m);
        s.solve();
        //totex
    }

    public void cmin2() {
        //totex cmin2
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] x = constantArray(new int[]{5, 7, 9, 10, 12, 3, 2});
        IntegerVariable min = makeIntVar("min", 1, 100);
        SetVariable set = makeSetVar("set", 0, x.length - 1);
        m.addConstraints(min(set, x, min), leqCard(set, constant(5)));
        s.read(m);
        s.solve();
        //totex
    }

    public void cmod() {
        //totex cmod
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 0, 10);
        IntegerVariable w = makeIntVar("w", 0, 10);
        m.addConstraint(mod(w, x, 1));
        s.read(m);
        s.solve();
        //totex
    }

    public void cmulticostregular() {
        //totex cmulticostregular
        //0- declare parameters
        int DAY = 0;
        int NIGHT = 1;
        int REST = 2;
        //1- create the model
        Model m = new CPModel();
        int nTime = 14; // 2 weeks: 14 days
        int nAct = 3; // 3 activities: DAY, NIGHT, REST
        int nRes = 4; // 4 resources: cost (0), #DAY (1), #NIGHT (2), #WORK (3)
        //2- Create the schedule variables: the activity processed at each time slot
        IntegerVariable[] sequence = makeIntVarArray("x", nTime, 0, nAct - 1, "cp:enum");
        // - create the cost variables (one for each resource)
        IntegerVariable[] bounds = new IntegerVariable[4];
        bounds[0] = makeIntVar("z_0", 30, 80, "cp:bound"); // 30 <= cost <= 80
        bounds[1] = makeIntVar("day", 0, 7, "cp:bound"); // 0 <= #DAY <= 7
        bounds[2] = makeIntVar("night", 3, 7, "cp:bound"); // 3 <= #NIGHT <= 7
        bounds[3] = makeIntVar("work", 7, 9, "cp:bound"); // 7 <= #WORK <= 9
        //3- Create the automaton
        Automaton auto = new Automaton();
        // state 0: starting and accepting state
        int start = auto.addState();
        auto.setStartingState(start);
        auto.setAcceptingState(start);
        // state 1 and a transition (0,DAY,1)
        int first = auto.addState();
        auto.addTransition(start, first, DAY);
        // state 2 and transitions (1,DAY,2), (1,NIGHT,2), (2,REST,0), (0,NIGHT,2)
        int second = auto.addState();
        auto.addTransition(first, second, new int[]{DAY, NIGHT});
        auto.addTransition(second, start, REST);
        auto.addTransition(start, second, NIGHT);
        //4- Declare the assignment/transition costs:
        // csts[i][j][s][r]: cost on resource r of assigning Xi to activity j at state s
        int[][][][] csts = new int[nTime][nAct][auto.getNbStates()][nRes];
        for (int i = 0; i < csts.length; i++) {
            csts[i][DAY][0] = new int[]{3, 1, 0, 1}; // costs of transition (0,DAY,1)
            csts[i][NIGHT][0] = new int[]{8, 0, 1, 1}; // costs of transition (0,NIGHT,2)
            csts[i][DAY][1] = new int[]{5, 1, 0, 1}; // costs of transition (1,DAY,2)
            csts[i][NIGHT][1] = new int[]{9, 0, 1, 1}; // costs of transition (1,NIGHT,2)
            csts[i][REST][2] = new int[]{2, 0, 0, 0}; // costs of transition (2,REST,0)
        }
        //5- Set a constraint parameter
        FastMultiCostRegular.DATA_STRUCT = FastMultiCostRegular.LIST;
        //6- add the constraint
        m.addConstraint(multiCostRegular(sequence, bounds, auto, csts));
        //7- create the solver, read the model and solve it
        Solver s = new CPSolver();
        s.read(m);
        s.solve();
        //totex
    }
    
    public void cneq1(){
        //totex cneq1
        Model m = new CPModel();
        Solver s = new CPSolver();
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(neq(v, c));
        s.read(m);
        s.solve();
        //totex
    }

    public void cneq2(){
        //totex cneq2
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 2);
        IntegerExpressionVariable w1 = plus(v1, 1);
        IntegerExpressionVariable w2 = minus(v2, 1);
        m.addConstraint(neq(w1, w2));
        s.read(m);
        s.solve();        
        //totex
    }

    public void cneqcard(){
        //totex cneqcard
        Model m = new CPModel();
        Solver s = new CPSolver();
        SetVariable set = makeSetVar("s", 1, 5);
        IntegerVariable card = makeIntVar("card", 2, 3);
        m.addConstraint(member(set, 3));
        m.addConstraint(neqCard(set, card));
        s.read(m);
        s.solve();
        //totex
    }

    public void cnot(){
        //totex cnot
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 10);
        m.addConstraint(not(geq(x, 3)));
        s.read(m);
        s.solve();
        //totex
    }

    public void cnotmember(){
        //totex cnotmember
        Model m = new CPModel();
        Solver s = new CPSolver();
        int x = 3;
        int card = 2;
        SetVariable y = makeSetVar("y", 2, 4);
        m.addConstraint(notMember(y, x));
        m.addConstraint(eqCard(y, card));
        s.read(m);
        s.solveAll();        
        //totex
    }

    public void cnth(){
        //totex cnth
        Model m = new CPModel();
        Solver s = new CPSolver();
        int[][] values = new int[][]{
            {1, 2, 0, 4, -323},
            {2, 1, 0, 3, 42},
            {6, 1, -7, 4, -40},
            {-1, 0, 6, 2, -33},
            {2, 3, 0, -1, 49}};
        IntegerVariable index1 = makeIntVar("index1", -3, 10);
        IntegerVariable index2 = makeIntVar("index2", -3, 10);
        IntegerVariable var = makeIntVar("value", -20, 20);
        m.addConstraint(nth(index1, index2, values, var));
        s.read(m);
        s.solveAll();        
        //totex
    }

    public void coccurrence(){
        //totex coccurrence
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x1 = makeIntVar("X1", 0, 10);
        IntegerVariable x2 = makeIntVar("X2", 0, 10);
        IntegerVariable x3 = makeIntVar("X3", 0, 10);
        IntegerVariable x4 = makeIntVar("X4", 0, 10);
        IntegerVariable x5 = makeIntVar("X5", 0, 10);
        IntegerVariable x6 = makeIntVar("X6", 0, 10);
        IntegerVariable x7 = makeIntVar("X7", 0, 10);
        IntegerVariable y1 = makeIntVar("Y1", 0, 10);
        m.addConstraint(occurrence(3, y1, new IntegerVariable[]{x1, x2, x3, x4, x5, x6, x7}));
        s.read(m);
        s.solve();        
        //totex
    }

    public void coccurrencemax(){
        //totex coccurrencemax
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x1 = makeIntVar("X1", 0, 10);
        IntegerVariable x2 = makeIntVar("X2", 0, 10);
        IntegerVariable x3 = makeIntVar("X3", 0, 10);
        IntegerVariable x4 = makeIntVar("X4", 0, 10);
        IntegerVariable x5 = makeIntVar("X5", 0, 10);
        IntegerVariable x6 = makeIntVar("X6", 0, 10);
        IntegerVariable x7 = makeIntVar("X7", 0, 10);
        IntegerVariable y1 = makeIntVar("Y1", 0, 10);
        m.addConstraint(occurrenceMax(3, y1, new IntegerVariable[]{x1, x2, x3, x4, x5, x6, x7}));
        s.read(m);
        s.solve();
        //totex
    }

    public void coccurrencemin(){
        //totex coccurrencemin
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x1 = makeIntVar("X1", 0, 10);
        IntegerVariable x2 = makeIntVar("X2", 0, 10);
        IntegerVariable x3 = makeIntVar("X3", 0, 10);
        IntegerVariable x4 = makeIntVar("X4", 0, 10);
        IntegerVariable x5 = makeIntVar("X5", 0, 10);
        IntegerVariable x6 = makeIntVar("X6", 0, 10);
        IntegerVariable x7 = makeIntVar("X7", 0, 10);
        IntegerVariable y1 = makeIntVar("Y1", 0, 10);
        m.addConstraint(occurrenceMin(3, y1, new IntegerVariable[]{x1, x2, x3, x4, x5, x6, x7}));
        s.read(m);
        s.solve();
        //totex
    }

    public void coppositesign(){
        //totex coppositesign
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", -1, 1);
        IntegerVariable y = makeIntVar("y", -1, 1);
        IntegerVariable z = makeIntVar("z", 0, 1000);
        m.addConstraint(oppositeSign(x,y));
        m.addConstraint(eq(z, plus(mult(x, -425), mult(y, 391))));
        s.read(m);
        s.solve();
        //totex
    }
}
