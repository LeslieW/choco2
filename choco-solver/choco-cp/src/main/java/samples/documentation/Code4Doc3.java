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
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;

import java.util.ArrayList;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 16 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* See Code4Doc1.java for more informations.
*/
public class Code4Doc3 {

    public void cgeq1(){
        //totex cgeq1
        Model m = new CPModel();
        Solver s = new CPSolver();
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(geq(v, c));
        s.read(m);
        s.solve();
        //totex
    }

    public void cgeq2(){
        //totex cgeq2
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 2);
        IntegerExpressionVariable w1 = plus(v1, 1);
        IntegerExpressionVariable w2 = minus(v2, 1);
        m.addConstraint(geq(w1, w2));
        s.read(m);
        s.solve();
        //totex
    }

    public void cgeqcard(){
        //totex cgeqcard
        Model m = new CPModel();
        Solver s = new CPSolver();
        SetVariable set = makeSetVar("s", 1, 5);
        IntegerVariable i = makeIntVar("card", 2, 3);
        m.addConstraint(member(set, 3));
        m.addConstraint(geqCard(set, i));
        s.read(m);
        s.solve();
        //totex
    }

    public void cglobalcardinality1(){
        //totex cglobalcardinality1
        int n = 5;
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] vars = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
           vars[i] = makeIntVar("var " + i, 1, n);
        }
        int[] LB2 = {0, 1, 1, 0, 3};
        int[] UB2 = {0, 1, 1, 0, 3};
        m.addConstraint("cp:bc", globalCardinality(vars, LB2, UB2, 1));
        s.read(m);
        s.solve();
        //totex
    }

    public void cglobalcardinality2(){
        //totex cglobalcardinality2
        int n = 5;
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] vars = makeIntVarArray("vars", n, 1, n);
        IntegerVariable[] cards = makeIntVarArray("cards", n, 0, 1);


        m.addConstraint("cp:bc", globalCardinality(vars, cards, 1));
        s.read(m);
        s.solve();
        //totex
    }

    public void cgt(){
        //totex cgt
        Model m = new CPModel();
        Solver s = new CPSolver();
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(gt(v, c));
        s.read(m);
        s.solve();
        //totex
    }

    public void cifonlyif(){
        //totex cifonlyif
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 3);
        IntegerVariable y = makeIntVar("y", 1, 3);
        IntegerVariable z = makeIntVar("z", 1, 3);
        m.addVariables("cp:bound",x ,y, z);
        m.addConstraint(ifOnlyIf(lt(x, y), lt(y, z)));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cifthenelse(){
        //totex cifthenelse
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 3);
        IntegerVariable y = makeIntVar("y", 1, 3);
        IntegerVariable z = makeIntVar("z", 1, 3);
        // use API ifThenElse(Constraint, Constraint, Constraint)
        m.addConstraint(ifThenElse(lt((x), (y)), gt((y), (z)), FALSE));
         // and ifThenElse(Constraint, IntegerExpressionVariable, IntegerExpressionVariable)
        m.addConstraint(leq(z, ifThenElse(lt(x, y), constant(1), plus(x,y))));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cimplies(){
        //totex cimplies
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 2);
        IntegerVariable y = makeIntVar("y", 1, 2);
        IntegerVariable z = makeIntVar("z", 1, 2);
        m.addVariables("cp:bound",x ,y, z);
        Constraint e1 = implies(leq(x, y), leq(x, z));
        m.addConstraint(e1);
        s.read(m);
        s.solveAll();
        //totex
    }

    public static void main(String[] args) {
        ChocoLogging.setVerbosity(Verbosity.SEARCH);
        new Code4Doc3().cincreasingnvalue();
    }

    public void cincreasingnvalue(){
        //totex cincreasingnvalue
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable nval = makeIntVar("nval", 1, 3);
        IntegerVariable[] variables = makeIntVarArray("vars", 6, 1, 4);
        m.addConstraint(increasing_nvalue("cp:both", nval, variables));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cinfeaspairac(){
        //totex cinfeaspairac
        Model m = new CPModel();
        Solver s = new CPSolver();
        boolean[][] matrice2 = new boolean[][]{
                      {false, true, true, false},
                      {true, false, false, false},
                      {false, false, true, false},
                      {false, true, false, false}};
        IntegerVariable v1 = makeIntVar("v1", 1, 4);
        IntegerVariable v2 = makeIntVar("v2", 1, 4);
        m.addConstraint(feasPairAC("cp:ac32",v1, v2, matrice2));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cinfeastupleac(){
        //totex cinfeastupleac
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 5);
        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 1, 5);
        ArrayList<int[]> forbiddenTuples = new ArrayList<int[]>();
        forbiddenTuples.add(new int[]{1, 1, 1});
        forbiddenTuples.add(new int[]{2, 2, 2});
        forbiddenTuples.add(new int[]{2, 5, 3});
        m.addConstraint(infeasTupleAC(forbiddenTuples, x, y, z));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cinfeastuplefc(){
        //totex cinfeastuplefc
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 5);
        IntegerVariable y = makeIntVar("y", 1, 5);
        IntegerVariable z = makeIntVar("z", 1, 5);
        ArrayList<int[]> forbiddenTuples = new ArrayList<int[]>();
        forbiddenTuples.add(new int[]{1, 1, 1});
        forbiddenTuples.add(new int[]{2, 2, 2});
        forbiddenTuples.add(new int[]{2, 5, 3});
        m.addConstraint(infeasTupleFC(forbiddenTuples, x, y, z));
        s.read(m);
        s.solveAll();        
        //totex
    }

    public void cintdiv(){
        //totex cintdiv
        Model m = new CPModel();
        Solver s = new CPSolver();
        long seed = 0;
        IntegerVariable x = makeIntVar("x", 3, 5);
        IntegerVariable y = makeIntVar("y", 1, 2);
        IntegerVariable z = makeIntVar("z", 0, 5);
        m.addConstraint(intDiv(x, y, z));
        s.setVarIntSelector(new RandomIntVarSelector(s, seed));
        s.setValIntSelector(new RandomIntValSelector(seed + 1));
        s.read(m);
        s.solve();
        //totex
    }

    public void cinversechanneling(){
        //totex cinversechanneling
        int n = 8;
        Model m = new CPModel();
        IntegerVariable[] queens = new IntegerVariable[n];
        IntegerVariable[] queensdual = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = makeIntVar("Q" + i, 1, n);
            queensdual[i] = makeIntVar("QD" + i, 1, n);
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
               int k = j - i;
               m.addConstraint(neq(queens[i], queens[j]));
               m.addConstraint(neq(queens[i], plus(queens[j], k))); // diagonal constraints
               m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queensdual[i], queensdual[j]));
                m.addConstraint(neq(queensdual[i], plus(queensdual[j], k))); // diagonal constraints
                m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
            }
        }
        m.addConstraint(inverseChanneling(queens, queensdual));
        m.addVariables("cp:decision", queens);
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        //totex 
    }

    public void cinverseset(){
        //totex cinverseset
        int i = 4;
        int j = 2;
        Model m = new CPModel();
        IntegerVariable[] iv = makeIntVarArray("iv", i, 0, j);
        SetVariable[] sv = makeSetVarArray("sv", j, 0, i);

        m.addConstraint(inverseSet(iv, sv));
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cisincluded(){
        //totex cisincluded
        Model m = new CPModel();
        Solver s = new CPSolver();
        SetVariable v1 = makeSetVar("v1", 3, 4);
        SetVariable v2 = makeSetVar("v2", 3, 8);
        m.addConstraint(isIncluded(v1, v2));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cisnotincluded(){
        //totex cisnotincluded
        Model m = new CPModel();
        Solver s = new CPSolver();
        SetVariable v1 = makeSetVar("v1", 3, 4);
        SetVariable v2 = makeSetVar("v2", 3, 8);
        m.addConstraint(isNotIncluded(v1, v2));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cleq(){
        //totex cleq
        Model m = new CPModel();
        Solver s = new CPSolver();
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(leq(v, c));
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 2);
        IntegerExpressionVariable w1 = plus(v1, 1);
        IntegerExpressionVariable w2 = minus(v2, 1);
        m.addConstraint(leq(w1, w2));
        s.read(m);
        s.solve();
        //totex
    }

    public void cleqcard(){
        //totex cleqcard
        Model m = new CPModel();
        Solver s = new CPSolver();
        SetVariable set = makeSetVar("s", 1, 5);
        IntegerVariable i = makeIntVar("card", 2, 3);
        m.addConstraint(member(set, 3));
        m.addConstraint(leqCard(set, i));
        s.read(m);
        s.solve();
        //totex
    }

    public void clex(){
        //totex clex
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
        m.addConstraint(lex(vs1, vs2));
        s.read(m);
        s.solve();        
        //totex
    }

    public void clexchain(){
        //totex clexchain
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
        m.addConstraint(lexChain(vs1, vs2));
        s.read(m);
        s.solve();
        //totex
    }

    public void clexchaineq(){
        //totex clexchaineq
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
        m.addConstraint(lexChainEq(vs1, vs2));
        s.read(m);
        s.solve();
        //totex
    }
}
