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
package choco.solver.branch;

import choco.Choco;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * ***********************************************
 * _       _                            *
 * |  °(..)  |                           *
 * |_  J||L _|        ChocoSolver.net    *
 * *
 * Choco is a java library for constraint     *
 * satisfaction problems (CSP), constraint    *
 * programming (CP) and explanation-based     *
 * constraint solving (e-CP). It is built     *
 * on a event-based propagation mechanism     *
 * with backtrackable structures.             *
 * *
 * Choco is an open-source software,          *
 * distributed under a BSD licence            *
 * and hosted by sourceforge.net              *
 * *
 * + website : http://choco.emn.fr            *
 * + support : choco@emn.fr                   *
 * *
 * Copyright (C) F. Laburthe,                 *
 * N. Jussien    1999-2008      *
 * *************************************************
 * User:    charles
 * Date:    9 sept. 2008
 */
public class StrategyTest {


    class IncorrectVarSelector extends AbstractIntVarSelector{
        IncorrectVarSelector(IntDomainVar[] vars) {
            super(null, vars);
        }

        public IntDomainVar selectIntVar() throws ContradictionException {
            if(!vars[0].isInstantiated()){
                return vars[0];
            }
            return null;
        }
    }

    @Test
    @Ignore
    public void badSelectors(){
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = Choco.makeIntVar("v1", 0, 2, CPOptions.V_ENUM);
        IntegerVariable v2 = Choco.makeIntVar("v2", 0, 3);
        m.addConstraint(Choco.leq(v1, v2));
        s.read(m);
        s.setVarIntSelector(new IncorrectVarSelector(s.getVar(v1, v2)));
        s.setValIntSelector(new MinVal());
        try{
            s.solve();
            Assert.fail("Every variables have been instantiated");
        }catch (Exception e){

        }
    }



}
