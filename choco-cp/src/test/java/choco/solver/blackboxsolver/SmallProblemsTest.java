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

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.model.Model;
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
}
