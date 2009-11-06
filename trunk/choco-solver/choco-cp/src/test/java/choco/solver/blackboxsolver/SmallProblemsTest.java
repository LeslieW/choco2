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
 **************************************************/
package choco.solver.blackboxsolver;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 10 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class SmallProblemsTest {


    @Test
    public void domino(){
        int size = 10;
        Model m = new CPModel();
        Solver s = new PreProcessCPSolver();

        IntegerVariable[] vars = makeIntVarArray("d", size, 0, size-1);

        for(int i = 0; i< size-1; i++){
            m.addConstraint(eq(vars[i], vars[i+1]));
        }
        m.addConstraint(or(eq(plus(vars[0],1),vars[size-1]),and(eq(vars[0],size-1),eq(vars[size-1],size-1))));

        s.read(m);
        s.solve();
        Assert.assertTrue("v0 == size-1", s.getVar(vars[0]).getVal() == size-1);
        
    }

    @Test
    public void testQ(){
        int n = 4;
        Model m = new CPModel();
        IntegerVariable[] queens = Choco.makeIntVarArray("q", n, 1, n);

        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++){
                m.addConstraints(noAttack(i+1,j+1, queens[i], queens[j]));
            }
        }

        CPSolver s = new PreProcessCPSolver();
//        CPSolver s = new CPSolver();
        s.read(m);
        System.out.println(s.pretty());
        s.solve();
        Assert.assertTrue(s.getNbSolutions()>0);

    }

    @Test
    public void testSimpleNeq(){
        int n = 2;
        Model m = new CPModel();
        IntegerVariable[] x = Choco.makeIntVarArray("q", n, 1, n);

        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++){
                m.addConstraints(neq(x[i], plus(x[j],3)));
            }
        }
        CPSolver s = new CPSolver();
        s.read(m);
        System.out.println(s.pretty());
        s.solve();
        Assert.assertTrue(s.getNbSolutions()>0);
    }

    private Constraint[] noAttack(int i, int j, IntegerVariable Qi, IntegerVariable Qj) {
        Constraint[] cs = new Constraint[3];
        cs[0] = Choco.neq(Qi, Qj);
        cs[1] = Choco.neq(Choco.plus(Qi, i), Choco.plus(Qj, j));
        cs[2] = Choco.neq(Choco.minus(Qi, i), Choco.minus(Qj, j));
        return cs;
    }
}
