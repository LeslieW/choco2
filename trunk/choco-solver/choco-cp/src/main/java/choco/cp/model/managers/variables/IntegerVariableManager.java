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

import choco.Choco;
import choco.cp.CPOptions;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.ConstantLeaf;
import choco.cp.solver.constraints.reified.leaves.VariableLeaf;
import choco.cp.solver.variables.integer.BooleanVarImpl;
import choco.cp.solver.variables.integer.IntDomainCst;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.BitSet;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 10:14:01
 */
public final class IntegerVariableManager implements VariableManager<IntegerVariable> {


	protected  IntDomainVar makeConstant(CPSolver solver,IntegerVariable iv) {
		int value = iv.getLowB();
        IntDomainVar v = (IntDomainVar)solver.getIntConstant(value);
        if(v == null){
            v = new IntDomainCst(solver, iv.getName(), value);
            solver.addIntConstant(value, v);
        }
         return v;
	}

    /**
     * Build a integer variable for the given solver
     *
     * @param solver the solver defining the variable
     * @param var model variable
     * @return an integer variable
     */
    public Var makeVariable(Solver solver, IntegerVariable var) {
        if (solver instanceof CPSolver) {
            IntDomainVar v = null;
            // Interception of boolean variable
            if(var.isConstant()){
                return makeConstant((CPSolver)solver, var);
            }else
            if(var.isBoolean()){
                v =  new BooleanVarImpl(solver, var.getName());
            }else
            if (var.getValues() == null) {
                if (var.getLowB() != var.getUppB()) {
                    int type; // default type
                    if (var.getOptions().contains(CPOptions.V_ENUM)) {
                        type = IntDomainVar.BITSET;
                    } else if (var.getOptions().contains(CPOptions.V_BOUND)) {
                        type = IntDomainVar.BOUNDS;
                    } else if (var.getOptions().contains(CPOptions.V_LINK)) {
                        type = IntDomainVar.LINKEDLIST;
                    } else if (var.getOptions().contains(CPOptions.V_BTREE)) {
                        type = IntDomainVar.BINARYTREE;
                    } else if (var.getOptions().contains(CPOptions.V_BLIST)) {
                        type = IntDomainVar.BIPARTITELIST;
                    } else{
                        type = getIntelligentDomain(solver.getModel(),var);
                    }
                    v =  new IntDomainVarImpl(solver, var.getName(), type, var.getLowB(), var.getUppB());
                }
            } else {
                int[] values = var.getValues();
                if(values.length>1) {
                    int type = IntDomainVar.BITSET; // default type
                    if (var.getOptions().contains(CPOptions.V_LINK)) {
                        type = IntDomainVar.LINKEDLIST;
                    } else if (var.getOptions().contains(CPOptions.V_BTREE)) {
                        type = IntDomainVar.BINARYTREE;
                    } else if (var.getOptions().contains(CPOptions.V_BLIST)) {
                        type = IntDomainVar.BIPARTITELIST;
                    }
                    v = new IntDomainVarImpl(solver, var.getName(), type, values);
                }
            }
            ((CPSolver) solver).addIntVar(v);
            return v;
        }
        throw new ModelException("Could not found a variable manager in " + this.getClass() + " !");
    }

    /**
     * Build a expression node
     *
     * @param solver associated solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return a variable leaf or constant leaf (for expression tree)
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        if (vars[0] instanceof IntegerConstantVariable) {
            IntegerConstantVariable c = (IntegerConstantVariable) vars[0];
            return new ConstantLeaf(c.getValue());
        } else if (vars[0] instanceof IntegerVariable) {
            return new VariableLeaf((IntegerVariable) vars[0]);
        }
        return null;
    }

    /**
     * try to find the most suitable domain for v regarding constraints wish
     * a simple heuristic is applied to rank the domains
     *
     * @param v unknown domain type variable
     * @return a domain type
     */
    public int getIntelligentDomain(Model model,IntegerVariable v) {
        // specific case, deal with unbounded domain
        if(v.getLowB()<= Choco.MIN_LOWER_BOUND && v.getUppB() >= Choco.MAX_UPPER_BOUND){
            return IntDomainVar.BOUNDS;
        }

        int[] scoreForDomain = new int[10]; // assume there is no more than 10 kind of domains

        //all type of domains are initially possible
        BitSet posDom = new BitSet(10);
        posDom.set(0, 20);
        if (v.getValues() != null) { //if initial holes, bounds impossible
            posDom.clear(IntDomainVar.BOUNDS);
        }
        // big domain without "holes"
        if(v.getUppB() - v.getLowB() +1 == v.getDomainSize()) {
            if (v.getDomainSize() > 300) {
                posDom.clear(IntDomainVar.BITSET);
                posDom.clear(IntDomainVar.LINKEDLIST);
            }
        }

        //take preferences and possibilities of constraints
        Iterator<Constraint> it = v.getConstraintIterator(model);
        while (it.hasNext()) {
            Constraint cc = it.next();
            int[] prefereddoms = cc.getFavoriteDomains();
            if (prefereddoms.length > 0) {
                BitSet posCdom = new BitSet();
                for (int i = 0; i < prefereddoms.length; i++) {
                    scoreForDomain[prefereddoms[i]] += (i + 1);
                    posCdom.set(prefereddoms[i]);
                }
                posDom.and(posCdom);
            }
        }

        //find the best prefered domain
        int bestDom = possibleArgMin(scoreForDomain, posDom);
        if (bestDom == -1) {
			throw new ModelException("no suitable domain for " + v + " that can be accepted by all constraints");
		}
        return bestDom;
    }


    public int possibleArgMin(int[] tab, BitSet posDom) {
       int bestDom = -1;
        int minScore = Integer.MAX_VALUE;
        for (int i = 0; i < tab.length; i++) {
            if (posDom.get(i) && minScore > tab[i]) {
                minScore = tab[i];
                bestDom = i;
            }
        }
        return bestDom;
    }
}
