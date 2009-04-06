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
import choco.cp.solver.constraints.integer.Element;
import choco.cp.solver.constraints.integer.Element2D;
import choco.cp.solver.constraints.integer.ElementV;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 9 août 2008
 * Time: 16:25:12
 */
public class ElementManager extends IntConstraintManager{

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
        if(solver instanceof CPSolver){
            if(parameters instanceof Integer){
                int offset = (Integer)parameters;
                IntDomainVar index = solver.getVar((IntegerVariable)variables[variables.length-2]);
                IntDomainVar val = solver.getVar((IntegerVariable)variables[variables.length-1]);
                if(variables[0] instanceof IntegerConstantVariable){
                    int[] values = new int[variables.length-2];
                    for(int i = 0; i < variables.length-2; i++){
                        values[i] = ((IntegerConstantVariable)variables[i]).getValue();
                    }
                    return new Element(index, values, val, offset);
                }else{
                    if (index.hasEnumeratedDomain()) {
                        return new ElementV(solver.getVar((IntegerVariable[])variables), offset);
                    }else{
                        throw new SolverException(index.getName()+" has not an enumerated domain");
                    }
                }
            }else if(parameters instanceof int[][]){
                int[][] varArray = (int[][])parameters;
                IntDomainVar index = solver.getVar((IntegerVariable)variables[0]);
                IntDomainVar index2 = solver.getVar((IntegerVariable)variables[1]);
                IntDomainVar val = solver.getVar((IntegerVariable)variables[2]);
                return new Element2D(index, index2, val, varArray);
            }
        }

        if(Choco.DEBUG){
            throw new RuntimeException("Could not found implementation for Element !");
        }
        return null;
    }
}
