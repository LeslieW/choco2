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
import choco.cp.solver.constraints.integer.InverseChanneling;
import choco.cp.solver.constraints.integer.channeling.BooleanChanneling;
import choco.kernel.model.constraints.ConstraintType;
import static choco.kernel.model.constraints.ConstraintType.CHANNELING;
import static choco.kernel.model.constraints.ConstraintType.INVERSECHANNELING;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/*
 *  ______
 * (__  __)
 *    ||
 *   /__\                  Choco manager
 *    \                    =============
 *    \                      Aug. 2008
 *    \            All alldifferent constraints
 *    \
 *    |
 */
/**
 * A manager to build new all channeling constraints
 */
public class ChannelingManager extends IntConstraintManager {


    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
        if(solver instanceof CPSolver){
            if(parameters instanceof ConstraintType){
                ConstraintType type = (ConstraintType)parameters;
                IntDomainVar[] var;
                if(CHANNELING.equals(type)){
                    IntDomainVar yij = solver.getVar(variables[0]);
                    IntDomainVar xi = solver.getVar(variables[1]);
                    int j = ((IntegerConstantVariable)variables[2]).getValue();
                    IntDomainVar boolv, intv;
                    if ((yij.getInf() >= 0) && (yij.getSup() <= 1)) {
                        boolv = yij;
                        intv = xi;
                    } else {
                        boolv = xi;
                        intv = yij;
                    }
                    if ((boolv.getInf() >= 0) && (boolv.getSup() <= 1) && (intv.canBeInstantiatedTo(j))) {
                        return new BooleanChanneling(boolv, intv, j);
                    } else {
                        throw new SolverException(yij + " should be a boolean variable and " + j + " should belongs to the domain of " + xi);
                    }
                }
                if(INVERSECHANNELING.equals(type)){
                    var  = solver.getVar((IntegerVariable[])variables);
                    return new InverseChanneling(var, var.length/2);
                }
            }
        }
        if(Choco.DEBUG){
            LOGGER.severe("Could not found an implementation of channeling !");
        }


        return null;
    }
}
