package choco.cp.model.managers.constraints.integer;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.Element2D;
import choco.cp.solver.constraints.integer.ElementG;
import choco.cp.solver.constraints.integer.ElementVG;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * User: ebeb
 * Date: 17 mars 2009
 * Time: 11:25:12
 */
public final class ElementGManager extends IntConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {        System.out.println("myElementManager");
    	if(solver instanceof CPSolver){
            if(parameters instanceof Integer){
                int offset = (Integer)parameters;
                IntDomainVar index = solver.getVar(variables[variables.length-2]);
                IntDomainVar val = solver.getVar(variables[variables.length-1]);
                if(variables[0] instanceof IntegerConstantVariable){
                    int[] values = new int[variables.length-2];
                    for(int i = 0; i < variables.length-2; i++){
                        values[i] = ((IntegerConstantVariable)variables[i]).getValue();
                    }
                    return new ElementG(index, values, val);
                }else{
                    if (index.hasEnumeratedDomain()) {
                        return new ElementVG(solver.getVar((IntegerVariable[])variables), offset);
                    }
                }
            }else if(parameters instanceof int[][]){
                int[][] varArray = (int[][])parameters;
                IntDomainVar index = solver.getVar(variables[0]);
                IntDomainVar index2 = solver.getVar(variables[1]);
                IntDomainVar val = solver.getVar(variables[2]);
                return new Element2D(index, index2, val, varArray);
            }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }
}
