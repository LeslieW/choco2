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
package choco.model.constraints;

import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 sept. 2008
 * Time: 14:56:33
 * To change this template use File | Settings | File Templates.
 */
public class ConstraintTest {

    @Test
    /**
     * John Horan bug on PartiallyStoredVector#staticRemove(int idx)
     */
    public void eraseConstraintTest(){
        Solver s = new CPSolver();
        IntDomainVar v = s.createEnumIntVar("v", 0, 3);
        SConstraint c1 = s.lt(v, 1);
        SConstraint c2 = s.lt(v, 2);
        SConstraint c3 = s.lt(v, 3);
        s.postCut(c1);
        s.postCut(c2);
        s.postCut(c3);
        s.eraseConstraint(c2);
        s.eraseConstraint(c3);
        try{
            System.out.println(s.pretty());
        }catch (Exception e){
            Assert.fail();
        }
    }
}
