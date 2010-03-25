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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.solver.blackboxsolver;

import choco.Choco;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import org.junit.Assert;
import org.junit.Test;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 9 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class PostPonedConstraintTest {

    @Test
    public void test1(){
        final IntegerVariable[] vars = Choco.makeBooleanVarArray("b", 2);

        final Constraint c1 = Choco.eq(vars[1], 1);
        final Constraint c2 = Choco.eq(vars[0], vars[1]);

        final Model m = new CPModel();

        m.addConstraint(CPOptions.C_POST_PONED, c1);
        m.addConstraint(c2);

        final Solver s= new CPSolver();
        s.read(m);
        final SConstraint sc1 = s.getCstr(c1);
        final SConstraint sc2 = s.getCstr(c2);

        DisposableIterator<SConstraint> it = s.getConstraintIterator();
        Assert.assertTrue("no constraint", it.hasNext());
        Assert.assertEquals("wrong order", sc2, it.next());
        Assert.assertTrue("no constraint", it.hasNext());
        Assert.assertEquals("wrong order", sc1, it.next());
        Assert.assertFalse("still constraint", it.hasNext());


    }
}
