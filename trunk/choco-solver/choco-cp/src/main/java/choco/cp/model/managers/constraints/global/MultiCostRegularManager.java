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
package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.automata.fast_multicostregular.FastMultiCostRegular;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 23, 2009
 * Time: 4:43:36 PM
 */
public class MultiCostRegularManager extends IntConstraintManager
{

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options)
    {
        if (solver instanceof CPSolver && parameters instanceof Object[])
        {

            IntDomainVar[] all = solver.getVar((IntegerVariable[]) variables);


            Object[] param = (Object[]) parameters;
            if (param.length == 3)
            {
                int sz = (Integer) param[0];
                Automaton pi = (Automaton) param[1];
                IntDomainVar[] vs = new IntDomainVar[sz];
                IntDomainVar[] z = new IntDomainVar[all.length-sz];
                System.arraycopy(all, 0, vs, 0, vs.length);
                System.arraycopy(all, vs.length, z, 0, all.length - vs.length);

                if (param[2] instanceof double[][][])
                {
                    double[][][] csts = (double[][][]) param[2];
                    return new FastMultiCostRegular(vs,z,pi,csts, solver.getEnvironment());
                }
                else if (param[2] instanceof double[][][][])
                {
                    double[][][][] csts = (double[][][][]) param[2];
                    return new FastMultiCostRegular(vs,z,pi,csts, solver.getEnvironment());
                }
            }
        }
        return null;
    }

}