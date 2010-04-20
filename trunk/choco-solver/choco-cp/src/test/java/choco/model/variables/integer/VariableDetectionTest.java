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
package choco.model.variables.integer;

import choco.Choco;
import static choco.Choco.allDifferent;
import static choco.Choco.makeIntVarArray;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntervalIntDomain;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 8, 2008
 * Time: 4:42:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariableDetectionTest {

    @Test
    public void testAutoDetectionBoundVar() {
        Model m = new CPModel();
        IntegerVariable[] intvs = makeIntVarArray("v", 10, 0, 10);
        m.addConstraint(allDifferent(Options.C_ALLDIFFERENT_BC, intvs));
        CPSolver s = new CPSolver();
        s.read(m);
        for (int i = 0; i < intvs.length; i++) {
            assertTrue(s.getVar(intvs[i]).getDomain() instanceof IntervalIntDomain);
        }
    }

    @Test
    public void testRemoveVal(){
        IntegerVariable v = Choco.makeIntVar("v", 1, 10);
        v.removeVal(0);
        Assert.assertTrue("v can not be equal to 10",v.canBeEqualTo(10));
        v.removeVal(11);
        Assert.assertTrue("v can not be equal to 10",v.canBeEqualTo(10));
        v.removeVal(5);
        Assert.assertFalse("v can be equal to 5",v.canBeEqualTo(5));
        v.removeVal(5);
        Assert.assertFalse("v can be equal to 5",v.canBeEqualTo(5));


    }

}
