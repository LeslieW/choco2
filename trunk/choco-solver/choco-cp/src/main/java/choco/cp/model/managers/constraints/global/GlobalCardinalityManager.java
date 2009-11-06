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

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.BoundGcc;
import choco.cp.solver.constraints.global.BoundGccVar;
import choco.cp.solver.constraints.global.matching.GlobalCardinality;
import choco.kernel.model.constraints.ConstraintType;
import static choco.kernel.model.constraints.ConstraintType.*;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
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
 * A manager to build new all different constraints (and more... soon)
 */
public class GlobalCardinalityManager extends IntConstraintManager {


    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
        if(solver instanceof CPSolver){
            if(parameters instanceof Object[]){
                Object[] params = (Object[])parameters;
                ConstraintType type = (ConstraintType)params[0];
                if(GLOBALCARDINALITYMAX.equals(type)){
                    int min = (Integer)params[1];
                    int max = (Integer)params[2];
                    int[] low = (int[])params[3];
                    int[] up = (int[])params[4];
                    IntDomainVar[] vars = solver.getVar(variables);
                    if(options.contains("cp:ac")){
                        return new GlobalCardinality(vars, min, max, low, up);
                    }
                    if(options.contains("cp:bc")){
                        return new BoundGcc(vars, min, max, low, up);
                    }
                    if(vars[0].hasEnumeratedDomain()){
                        return new GlobalCardinality(vars, min, max, low, up);
                    }else{
                        return new BoundGcc(vars, min, max, low, up);
                    }
                }
                if(GLOBALCARDINALITY.equals(type)){
                    int[] low = (int[])params[1];
                    int[] up = (int[])params[2];
                    IntDomainVar[] vars = solver.getVar(variables);
                    if(options.contains("cp:ac")){
                        return new GlobalCardinality(vars, 1, low.length, low, up);
                    }
                    if(options.contains("cp:bc")){
                        return new BoundGcc(vars, 1, low.length, low, up);
                    }
                    if ((vars[0]).hasEnumeratedDomain()) {
                        return new GlobalCardinality(vars, 1, low.length, low, up);
                    } else {
                        return new BoundGcc(vars, 1, low.length, low, up);
                    }
                }
                if(GLOBALCARDINALITYVAR.equals(type)){
                    int min = (Integer)params[1];
                    int max = (Integer)params[2];
                    int n = (Integer)params[3];
                    IntDomainVar[] vars = solver.getVar(variables);
                    IntDomainVar[] varT = new IntDomainVar[n];
                    IntDomainVar[] card = new IntDomainVar[vars.length-n];
                    System.arraycopy(vars, 0, varT, 0, n);
                    System.arraycopy(vars, n, card, 0, card.length);
                    return new BoundGccVar(varT, card, min ,max);
                }
            }
        }
        if(Choco.DEBUG){
            LOGGER.severe("Could not found an implementation of alldifferent !");
        }

        return null;
    }

    public int[] getFavoriteDomains(HashSet<String> options) {
        if (options.contains("cp:bc")) {
            return getBCFavoriteIntDomains();
        } else {
            return getACFavoriteIntDomains();
        }
    }
}
