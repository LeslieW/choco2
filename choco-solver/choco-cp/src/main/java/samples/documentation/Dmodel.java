/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package samples.documentation;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.constraints.integer.extension.TuplesTest;

import java.util.ArrayList;
import java.util.List;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 7 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
class Dmodel {

    public static void modelvariable() {
        Model model = new CPModel();
        Variable var1 = Choco.makeIntVar("v1", 0, 10);
        Variable var2 = Choco.makeIntVar("v2", 0, 10);
        Variable var3 = Choco.makeIntVar("v3", 0, 10);
        Variable var4 = Choco.makeIntVar("v4", 0, 10);
        Variable var5 = Choco.makeIntVar("v5", 0, 10);
        Variable var6 = Choco.makeIntVar("v6", 0, 10);
        //totex mvariabledeclaration1
        model.addVariable(var1);
        model.addVariables(var2, var3);
        //totex
        //totex mvariabledeclaration2
        model.addVariable(Options.V_OBJECTIVE, var4);
        model.addVariables(Options.V_NO_DECISION, var5, var6);
        //totex

        //totex mexpressionvariable
        IntegerVariable v1 = Choco.makeIntVar("v1", 1, 3);
        IntegerVariable v2 = Choco.makeIntVar("v2", 1, 3);
        IntegerExpressionVariable v1Andv2 = Choco.plus(v1, v2);
        //totex

        //totex mconstant
        IntegerConstantVariable c10 = Choco.constant(10);
        RealConstantVariable c0dot0 = Choco.constant(0.0);
        SetConstantVariable c0_12 = Choco.constant(new int[]{0, 12});
        SetConstantVariable cEmpty = Choco.emptySet();
        //totex

        //totex mnodecision1
        IntegerVariable vNoDec = Choco.makeIntVar("vNoDec", 1, 3, Options.V_NO_DECISION);
        //totex
        //totex mnodecision2
        IntegerVariable vNoDec1 = Choco.makeIntVar("vNoDec1", 1, 3);
        IntegerVariable vNoDec2 = Choco.makeIntVar("vNoDec2", 1, 3);
        model.addOptions(Options.V_NO_DECISION, vNoDec1, vNoDec2);
        //totex
        CPSolver solver = new CPSolver();
        //totex mobjective
        IntegerVariable x = Choco.makeIntVar("x", 1, 1000, Options.V_OBJECTIVE);
        IntegerVariable y = Choco.makeIntVar("y", 20, 50);
        model.addConstraint(Choco.eq(x, Choco.mult(y, 20)));
        solver.read(model);
        solver.minimize(true);
        //totex
    }
    
    public static void modelconstraint() {
        Model model = new CPModel();
        IntegerVariable var1 = Choco.makeIntVar("v1", 0, 10);
        IntegerVariable var2 = Choco.makeIntVar("v2", 0, 10);
        IntegerVariable var3 = Choco.makeIntVar("v3", 0, 10);
        Constraint c1 = Choco.eq(var1, 1);
        Constraint c2 = Choco.eq(var2, 2);
        Constraint c3 = Choco.eq(var3, 3);
        //totex mconstraintdeclaration1
        model.addConstraint(c1);
        model.addConstraints(c2,c3);
        //totex
        //totex mconstraintdeclaration2
        model.addConstraint(Choco.neq(var1, var2));
        //totex
    }
    public static void cchannelingreified(){
        CPModel model = new CPModel();
        //totex cchannelingreified
        IntegerVariable reifiedB = Choco.makeIntVar("bvar", 0, 1);
	    IntegerVariable x = Choco.makeIntVar("a", 0, 10);
        IntegerVariable y = Choco.makeIntVar("b", 0, 10);
        model.addConstraint(Choco.reifiedConstraint(reifiedB, Choco.eq(x, y)));
        //totex
    }

    public static void mlargerelation(){
        CPModel model = new CPModel();
        IntegerVariable[] vars = new IntegerVariable[3];
	    int[] min  = new int[3];
        int[] max = new int[3];
        List<int[]> tuples = new ArrayList<int[]>();
        //totex mlargerelation
        LargeRelation r = Choco.makeLargeRelation(min, max, tuples, true);
        model.addConstraint(Choco.relationTupleAC(vars, r));
        //totex
    }

    //totex mnotallequal
    public static class NotAllEqual extends TuplesTest {
      public boolean checkTuple(int[] tuple) {
          for (int i = 1; i < tuple.length; i++) {
              if (tuple[i - 1] != tuple[i]) return true;
          }
          return false;
      }
        //totex

        public static void mrelationtuplefc(){
            //totex mrelationtuplefc
            CPModel model = new CPModel();
            IntegerVariable[] vars = Choco.makeIntVarArray("v", 3, 1, 3);
            model.addConstraint(Choco.relationTupleFC(vars, new NotAllEqual()));
            //totex
        }
    }

}
