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
package choco.cp.model.managers.variables;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.SetCard;
import choco.cp.solver.variables.set.SetVarImpl;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 13:26:41
 */
public class SetVariableManager implements VariableManager {

    /**
     * Build a set variable for the given solver
     * @param solver
     * @param var
     * @return a set variable
     */
    public Var makeVariable(Solver solver, Variable var) {
        if(solver instanceof CPSolver){
            SetVariable sv = (SetVariable)var;
            IntDomainVar card = (sv.getCard()!=null?solver.getVar(sv.getCard()):null);
            SetVar s = null;
            if(sv.getVariableType()== VariableType.CONSTANT_SET){
                    s = new SetVarImpl(solver, sv.getName(), sv.getValues(), card, SetVar.BOUNDSET_CONSTANT);
            }else
            if (sv.getValues() == null) {
                if(sv.getOptions().contains("cp:boundCard")){
                    s = new SetVarImpl(solver, sv.getName(), sv.getLowB(), sv.getUppB(), card, SetVar.BOUNDSET_BOUNDCARD);
                }else
                //if(options.contains("cp:enumCard")){
                {
                    s = new SetVarImpl(solver, sv.getName(), sv.getLowB(), sv.getUppB(), card, SetVar.BOUNDSET_ENUMCARD);
                }
            }else{
                int[] values = sv.getValues();
                if(sv.getOptions().contains("cp:boundCard")){
                    s = new SetVarImpl(solver, sv.getName(), values, card, SetVar.BOUNDSET_BOUNDCARD);
                }else
                //if(options.contains("cp:enumCard")){
                {
                    s = new SetVarImpl(solver, sv.getName(), values, card, SetVar.BOUNDSET_ENUMCARD);
                }
            }
            ((CPSolver)solver).addSetVar(s);
            solver.post(new SetCard(s, s.getCard(), true, true)); //post |v| = v.getCard() 
            return s;
        }
        throw new ModelException("Could not found a variable manager in " + this.getClass() + " !");
    }

    /**
     * Build a expression node
     *
     * @param solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] vars) {
        return null;
    }
}
