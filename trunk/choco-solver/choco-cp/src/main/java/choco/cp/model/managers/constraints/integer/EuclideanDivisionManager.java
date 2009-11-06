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
package choco.cp.model.managers.constraints.integer;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.EuclideanDivisionXYZ;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 9 déc. 2008
 * Since : Choco 2.0.1
 * Update : Choco 2.0.1
 */
/*
 *  ______
 * (__  __)
 *    ||
 *   /__\                  Choco manager
 *    \                    =============
 *    \                      Dec. 2008
 *    \                 Euclidean division constraints
 *    \
 *    |
 */
/**
 * A manager to build new euclidean division constraint
 */
public class EuclideanDivisionManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
        if(solver instanceof CPSolver){
            IntDomainVar[] vs = solver.getVar((IntegerVariable[]) variables);
            return new EuclideanDivisionXYZ(vs[0],vs[1],vs[2]);
        }
        if(Choco.DEBUG){
            LOGGER.severe("Could not found an implementation of Euclidean Division !");
        }
        return null;
    }
}