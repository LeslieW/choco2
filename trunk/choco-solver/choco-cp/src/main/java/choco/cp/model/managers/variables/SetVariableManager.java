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

import choco.Options;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.SetCard;
import choco.cp.solver.variables.set.SetVarImpl;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.VariableType;
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
public final class SetVariableManager implements VariableManager<SetVariable> {

    /**
     * Build a set variable for the given solver
     * @param solver
     * @param var
     * @return a set variable
     */
    public Var makeVariable(Solver solver, SetVariable var) {
        if(solver instanceof CPSolver){
            IntDomainVar card = (var.getCard()!=null?solver.getVar(var.getCard()):null);
            SetVar s;
            if(var.getVariableType()== VariableType.CONSTANT_SET){
                    s = new SetVarImpl(solver, var.getName(), var.getValues(), card, SetVar.BOUNDSET_CONSTANT);
            }else
            if (var.getValues() == null) {
                if(var.getOptions().contains(Options.V_BOUND)){
                    s = new SetVarImpl(solver, var.getName(), var.getLowB(), var.getUppB(), card, SetVar.BOUNDSET_BOUNDCARD);
                }else
                {
                    s = new SetVarImpl(solver, var.getName(), var.getLowB(), var.getUppB(), card, SetVar.BOUNDSET_ENUMCARD);
                }
            }else{
                int[] values = var.getValues();
                if(var.getOptions().contains(Options.V_BOUND)){
                    s = new SetVarImpl(solver, var.getName(), values, card, SetVar.BOUNDSET_BOUNDCARD);
                }else
                {
                    s = new SetVarImpl(solver, var.getName(), values, card, SetVar.BOUNDSET_ENUMCARD);
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
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        return null;
    }
}
