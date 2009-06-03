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
import choco.cp.solver.constraints.reified.leaves.bool.AndNode;
import choco.cp.solver.constraints.reified.leaves.bool.EqNode;
import choco.cp.solver.constraints.reified.leaves.bool.OrNode;
import choco.cp.solver.constraints.reified.leaves.bool.NeqNode;
import choco.cp.solver.constraints.reified.leaves.VariableLeaf;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;
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

    /**
     * Build a constraint and its opposite for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters
     * @param options
     * @return array of 2 SConstraint object, the constraint and its opposite
     */
    @Override
    public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {

        SConstraint[] cs = new SConstraint[2];
        if(solver instanceof CPSolver){
            if(parameters instanceof Integer){
                int offset = (Integer)parameters;
                IntDomainVar Y;
                final IntDomainVar X = solver.getVar((IntegerVariable)variables[variables.length-2]);
                // Introduces a intermediary variable
                if(X.hasBooleanDomain()){
                    Y = solver.createBooleanVar("Y_opp");
                }else if(X.hasEnumeratedDomain()){
                    Y = solver.createEnumIntVar("Y_opp", X.getInf(), X.getSup());
                }else{
                    Y = solver.createBoundIntVar("Y_opp", X.getInf(), X.getSup());
                }

                IntDomainVar val = solver.getVar((IntegerVariable)variables[variables.length-1]);
                if(variables[0] instanceof IntegerConstantVariable){
                    int[] values = new int[variables.length-2];
                    for(int i = 0; i < variables.length-2; i++){
                        values[i] = ((IntegerConstantVariable)variables[i]).getValue();
                    }
                    solver.post(new Element(Y, values, val, offset));
                }else{
                    if (Y.hasEnumeratedDomain()) {
                        IntDomainVar[] tvars = solver.getVar((IntegerVariable[])variables);
                        tvars[variables.length-2] = Y;
                        solver.post(new ElementV(tvars, offset));
                    }else{
                        throw new SolverException(X.getName()+" has not an enumerated domain");
                    }
                }
                cs[0] = solver.eq(Y, X);
                cs[1] = solver.neq(Y, X);
            }else if(parameters instanceof int[][]){
                IntDomainVar Y1, Y2;
                int[][] varArray = (int[][])parameters;
                final IntDomainVar X1 = solver.getVar((IntegerVariable)variables[0]);
                final IntDomainVar X2 = solver.getVar((IntegerVariable)variables[1]);
                if(X1.hasBooleanDomain()){
                    Y1 = solver.createBooleanVar("Y1_opp");
                }else if(X1.hasEnumeratedDomain()){
                    Y1 = solver.createEnumIntVar("Y1_opp", X1.getInf(), X1.getSup());
                }else{
                    Y1 = solver.createBoundIntVar("Y1_opp", X1.getInf(), X1.getSup());
                }
                if(X2.hasBooleanDomain()){
                    Y2 = solver.createBooleanVar("Y2_opp");
                }else if(X2.hasEnumeratedDomain()){
                    Y2 = solver.createEnumIntVar("Y2_opp", X2.getInf(), X2.getSup());
                }else{
                    Y2 = solver.createBoundIntVar("Y2_opp", X2.getInf(), X2.getSup());
                }

                IntDomainVar val = solver.getVar((IntegerVariable)variables[2]);
                solver.post(new Element2D(Y1, Y2, val, varArray));
                cs[0] = new ExpressionSConstraint(
                        new AndNode(new EqNode(new INode[]{new VariableLeaf(Y1), new VariableLeaf(X1)}),
                                new EqNode(new INode[]{new VariableLeaf(Y2), new VariableLeaf(X2)})));
                cs[1] = new ExpressionSConstraint(
                        new OrNode(new NeqNode(new INode[]{new VariableLeaf(Y1), new VariableLeaf(X1)}),
                                new NeqNode(new INode[]{new VariableLeaf(Y2), new VariableLeaf(X2)})));
            }
            return cs;
        }

        if(Choco.DEBUG){
            throw new RuntimeException("Could not found implementation for Element !");
        }
        return null;
    }
}
