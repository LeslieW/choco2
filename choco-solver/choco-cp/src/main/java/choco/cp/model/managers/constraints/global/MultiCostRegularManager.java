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
import choco.cp.solver.constraints.global.automata.fast_multicostregular.MultiCostRegular;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.automaton.FA.IAutomaton;
import choco.kernel.model.constraints.automaton.FA.ICostAutomaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 23, 2009
 * Time: 4:43:36 PM
 */
public final class MultiCostRegularManager extends IntConstraintManager
{

public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options)
{
        if (solver instanceof CPSolver && parameters instanceof Object[])
        {

                IntDomainVar[] all = solver.getVar((IntegerVariable[]) variables);

                Object[] param = (Object[]) parameters;
                if (param.length == 3 && param[2] instanceof Object[][][])
                {
                        int nVars = (Integer) param[0]; // the first nVars variables in 'all' must be the sequence variables
                        int nCounters = all.length - nVars;  // the last nCounters variables in 'all' must be the counter variables
                        IAutomaton pi = (IAutomaton) param[1];
                        Object[][][] costs = (Object[][][])param[2];

                        // try to check whether vars and costVars has been inverted when posting the constraint
                        boolean inverted = (nVars != nCounters) ? costs.length == nCounters
                                : !all[0].hasEnumeratedDomain() && all[nVars].hasEnumeratedDomain();
                        if (nVars == nCounters && !(all[0].hasEnumeratedDomain() && !all[nVars].hasEnumeratedDomain())) {
                                for (int i=0; !inverted && i< nVars; i++) {
                                        if (all[i].getSup() > costs[i].length) {
                                                inverted = true;
                                        }
                                }
                        }
                        IntDomainVar[] vs;
                        IntDomainVar[] z;
                        if (!inverted) {
                                vs = new IntDomainVar[nVars];
                                z = new IntDomainVar[nCounters];
                                System.arraycopy(all, 0, vs, 0, nVars);
                                System.arraycopy(all, nVars, z, 0, nCounters);
                        } else {
                                nCounters = nVars;
                                nVars = costs.length;
                                vs = new IntDomainVar[nVars];
                                z = new IntDomainVar[nCounters];
                                System.arraycopy(all, 0, z, 0, nCounters);
                                System.arraycopy(all, nCounters, vs, 0, nVars);
                        }

                        // check arguments
                        if (vs.length != costs.length && z.length != costs[0][0].length)
                                throw new ModelException("length of arrays are invalid");


                        if (param[2] instanceof int[][][]) {
                                int[][][] csts = (int[][][]) param[2];
                                return new MultiCostRegular(vs,z,pi,csts, solver);
                        } else {
                                int[][][][] csts = (int[][][][]) param[2];
                                return new MultiCostRegular(vs,z,pi,csts, solver);
                        }
                }
                else if (param.length == 2)
                {
                        int nVars = (Integer) param[0]; // the first nVars variables in 'all' must be the sequence variables
                        int nCounters = all.length - nVars;  // the last nCounters variables in 'all' must be the counter variables
                        ICostAutomaton pi = (ICostAutomaton) param[1];

                        IntDomainVar[] vs;
                        IntDomainVar[] z;

                        vs = new IntDomainVar[nVars];
                        z = new IntDomainVar[nCounters];
                        System.arraycopy(all, 0, vs, 0, nVars);
                        System.arraycopy(all, nVars, z, 0, nCounters);

                        return new MultiCostRegular(vs,z,pi,solver);



                }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
}

}
