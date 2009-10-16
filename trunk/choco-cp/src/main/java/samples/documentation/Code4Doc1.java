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
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 16 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* the code example of documentation.
* Every method represents a sample for a specific chapter/part of the documentation.
* It ensures the code presented in the documentation is closed to the trunk source.
* Before creating the documentation, one has to run the j2tex jar to export code between
* commentary slashes // and the 'totex' tags. 
* The opening tag must be followed with the file name without extension
* and the closing tag must be empty.
 *
 * Example (remove '_' character to work)
 * //_totex filename    (<- opening tag)
 * ...                  (<- code to export)
 * //_totex             (<- closing tag)
 *
 * Tags can be overlap, it removes automatically totex tags in the exported file.
*/
public class Code4Doc1 {

    public static void main(String[] args) {
        new Code4Doc1().oabs();
    }

    public void oabs() {
        //totex oabs
        Model m = new CPModel();
        IntegerVariable x = makeIntVar("x", 1, 5, "cp:enum");
        IntegerVariable y = makeIntVar("y", -5, 5, "cp:enum");
        m.addConstraint(eq(abs(x), y));
        Solver s = new CPSolver();
        s.read(m);
        s.solve();
        //totex
        Assert.assertEquals(s.getVar(x).getVal(), Math.abs(s.getVar(y).getVal()));
    }

    public void odiv() {
        //totex odiv
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 10);
        IntegerVariable w = makeIntVar("w", 22, 44);
        IntegerVariable z = makeIntVar("z", 12, 21);
        m.addConstraint(eq(z, div(w, x)));
        s.read(m);
        s.solve();
        //totex
    }

    public void omax() {
        //totex omax
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] v = makeIntVarArray("v", 3, -3, 3);
        IntegerVariable maxv = makeIntVar("max", -3, 3);
        Constraint c = eq(maxv, max(v));
        m.addConstraint(c);
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        //totex
    }

    public void omin() {
        //totex omin
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] v = makeIntVarArray("v", 3, -3, 3);
        IntegerVariable minv = makeIntVar("min", -3, 3);
        Constraint c = eq(minv, min(v));
        m.addConstraint(c);
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        //totex
    }

    public void ominus() {
        //totex ominus
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable a = makeIntVar("a", 0, 4);
        m.addConstraint(eq(minus(a, 1), 2));
        s.read(m);
        s.solve();
        //totex
    }

    public void omod() {
        //totex omod
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 1, 10);
        IntegerVariable w = makeIntVar("w", 22, 44);
        m.addConstraint(eq(1, mod(w, x)));
        s.read(m);
        s.solve();
        //totex
    }

    public void omult() {
        //totex omult
        CPModel m = new CPModel();
        IntegerVariable x = makeIntVar("x", -10, 10);
        IntegerVariable z = makeIntVar("z", -10, 10);
        IntegerVariable w = makeIntVar("w", -10, 10);
        m.addVariables(x, z, w);
        CPSolver s = new CPSolver();
        // x >= z * w
        Constraint exp = geq(x, mult(z, w));
        m.setDefaultExpressionDecomposition(true);
        m.addConstraint(exp);
        s.read(m);
        s.solveAll();

        //totex        
    }

    public void oneg() {
        //totex oneg
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", -10, 10);
        IntegerVariable w = makeIntVar("w", -10, 10);
        // -x = w - 20
        m.addConstraint(eq(neg(x), minus(w, 20)));
        s.read(m);
        s.solve();
        //totex
    }

    public void oplus() {
        //totex oplus
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable a = makeIntVar("a", 0, 4);
        // a + 1 = 2
        m.addConstraint(eq(plus(a, 1), 2));
        s.read(m);
        s.solve();
        //totex
    }

    public void opower() {
        //totex opower
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable x = makeIntVar("x", 0, 10);
        IntegerVariable y = makeIntVar("y", 2, 4);
        IntegerVariable z = makeIntVar("z", 28, 80);
        m.addConstraint(eq(z, power(x, y)));
        s.read(m);
        s.solve();
        //totex
    }


    public void oscalar() {
        //totex oscalar
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = makeIntVarArray("C", 9, 1, 10);
        int[] coefficients = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        m.addConstraint(eq(165, scalar(coefficients, vars)));
        
        s.read(m);
        s.solve();
        System.out.print("165 = (" + coefficients[0] + "*" + s.getVar(vars[0]).getVal()+")");
        for (int i = 1; i < vars.length; i++) {
            System.out.print(" + (" + coefficients[i] + "*" + s.getVar(vars[i]).getVal()+")");
        }
        System.out.println();
        //totex
    }

    public void osum(){
        //totex osum
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = makeIntVarArray("C", 10, 1, 10);
        m.addConstraint(eq(99, sum(vars)));

        s.read(m);
        s.solve();
        if(s.isFeasible()){
            System.out.print("99 = " + s.getVar(vars[0]).getVal());
            for (int i = 1; i < vars.length; i++) {
                System.out.print(" + "+s.getVar(vars[i]).getVal());
            }
            System.out.println();
        }
        //totex
    }
}
