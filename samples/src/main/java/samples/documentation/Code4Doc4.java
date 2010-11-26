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
 *                   N. Jussien    1999-2009      *
 **************************************************/
package samples.documentation;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.automaton.FA.CostAutomaton;
import choco.kernel.model.constraints.automaton.FA.IAutomaton;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.model.constraints.automaton.FA.ICostAutomaton;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
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
        m.addConstraint(lexEq(vs1, vs2));
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
        m.addVariables(Options.V_BOUND, x, y, z);
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
        m.addVariables(Options.V_BOUND, x, y, z);
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
        Model m = new CPModel();

        int nTime = 14; // 2 weeks: 14 days
        int nAct = 3; // 3 shift types:
        int DAY = 0, NIGHT = 1, REST = 2;
        int nCounters = 4; // cost (0), #DAY (1), #NIGHT (2), #WORK (3)

        IntegerVariable[] x = makeIntVarArray("x", nTime, 0, nAct - 1, Options.V_ENUM);

        IntegerVariable[] z = new IntegerVariable[4];
        z[0] = makeIntVar("z", 30, 800, Options.V_BOUND); // 30 <= cost <= 80
        z[1] = makeIntVar("D", 0, 7, Options.V_BOUND); // 0 <= #DAY <= 7
        z[2] = makeIntVar("N", 3, 7, Options.V_BOUND); // 3 <= #NIGHT <= 7
        z[3] = makeIntVar("W", 7, 9, Options.V_BOUND); // 7 <= #WORK <= 9

        FiniteAutomaton auto = new FiniteAutomaton();

        int start = auto.addState();
        auto.setInitialState(start);
        auto.setFinal(start);
        int first = auto.addState();
        auto.addTransition(start, first, DAY);         // transition (0,D,1)
        int second = auto.addState();
        auto.addTransition(first, second, DAY, NIGHT); // transitions (1,D,2), (1,N,2)
        auto.addTransition(second, start, REST);       // transition (2,R,0)
        auto.addTransition(start, second, NIGHT);      // transition (0,N,2)

        int[][][][] c = new int[nTime][nAct][nCounters][auto.getNbStates()];
        for (int i = 0; i < c.length; i++) {
            c[i][DAY][0] = new int[]{3, 5, 0};
            c[i][DAY][1] = new int[]{1, 1, 0};
            c[i][DAY][3] = new int[]{1, 1, 0};

            c[i][NIGHT][0] = new int[]{8, 9, 0};
            c[i][NIGHT][2] = new int[]{1, 1, 0};
            c[i][NIGHT][3] = new int[]{1, 1, 0};

            c[i][REST][0] = new int[]{0, 0, 2};



           
        }

        ICostAutomaton cauto = CostAutomaton.makeMultiResources(auto,c,z);

        m.addConstraint(multiCostRegular(z, x, cauto));
        Solver s = new CPSolver();
        s.read(m);
        System.out.println(s.minimize(s.getVar(z[0]),false));
          //  s.solve();
            System.out.println(s.getVar(z[0]).pretty());
            System.out.println(s.runtimeStatistics());
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
        int n=7;
        IntegerVariable[] x = makeIntVarArray("X", n, 0, 10);
        IntegerVariable z = makeIntVar("Z", 0, 10);
        m.addConstraint(occurrence(z, x, 3));
        s.read(m);
        s.solve();        
        //totex
    }

    public void coccurrencemax(){
        //totex coccurrencemax
        Model m = new CPModel();
        Solver s = new CPSolver();
        int n=7;
        IntegerVariable[] x = makeIntVarArray("X", n, 0, 10);
        IntegerVariable z = makeIntVar("Z", 0, 10);
        m.addConstraint(occurrenceMax(z, x, 3));
        s.read(m);
        s.solve();
        //totex
    }

    public void coccurrencemin(){
        //totex coccurrencemin
        Model m = new CPModel();
        Solver s = new CPSolver();
        int n=7;
        IntegerVariable[] x = makeIntVarArray("X", n, 0, 10);
        IntegerVariable z = makeIntVar("Z", 0, 10);
        m.addConstraint(occurrenceMin(z, x, 3));
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



public static void main(String[] args)
{
    new Code4Doc4().cmulticostregular();
}
}