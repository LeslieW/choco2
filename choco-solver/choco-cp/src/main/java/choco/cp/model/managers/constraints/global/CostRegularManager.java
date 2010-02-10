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
package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.automata.costregular.CostRegular;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 21 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class CostRegularManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {

            if (parameters instanceof Object[] && ((Object[])parameters).length == 2)
            {
                IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));
                IntDomainVar[] vs = new IntDomainVar[vars.length-1];
                System.arraycopy(vars,0,vs,0,vs.length);
                IntDomainVar z = vars[vs.length];

                Automaton auto;
                int [][] csts;
                Object[] tmp = (Object[]) parameters;
                try {
                    auto = (Automaton)tmp[0];
                    csts = (int[][])tmp[1];
                }
                catch (Exception e)
                {
                    LOGGER.severe("Invalid parameters in costregular manager");
                    return null;
                }
                return CostRegular.make(vs,z,auto,csts);

            }
            return null;

        }
}
