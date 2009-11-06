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
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.ConstantLeaf;
import choco.cp.solver.constraints.reified.leaves.VariableLeaf;
import choco.cp.solver.variables.integer.BooleanVarImpl;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
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
public class IntegerVariableManager implements VariableManager {


	protected  IntDomainVar makeConstant(CPSolver solver,IntegerVariable iv) {
		int value = iv.getLowB();
        IntDomainVar v = (IntDomainVar)solver.getIntConstant(value);
        if(v == null){
            if(iv.isBoolean()){
                v = new BooleanVarImpl(solver, iv.getName());
                v.getDomain().restrict(value);
            }else{
                v = new IntDomainVarImpl(solver, iv.getName(), IntDomainVar.BOUNDS, value, value);
            }
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
    public Var makeVariable(Solver solver, Variable var) {
        if (solver instanceof CPSolver) {
            IntegerVariable iv = (IntegerVariable) var;
            IntDomainVar v = null;
            // Interception of boolean variable
            if(iv.isConstant()){
                return makeConstant((CPSolver)solver, iv);
            }else
            if(iv.isBoolean()){
                v =  new BooleanVarImpl(solver, iv.getName());
            }else
            if (iv.getValues() == null) {
                if (iv.getLowB() != iv.getUppB()) {
                    int type; // default type
                    if (iv.getOptions().contains("cp:enum")) {
                        type = IntDomainVar.BITSET;
                    } else if (iv.getOptions().contains("cp:bound")) {
                        type = IntDomainVar.BOUNDS;
                    } else if (iv.getOptions().contains("cp:link")) {
                        type = IntDomainVar.LINKEDLIST;
                    } else if (iv.getOptions().contains("cp:btree")) {
                        type = IntDomainVar.BINARYTREE;
                    } else if (iv.getOptions().contains("cp:blist")) {
                        type = IntDomainVar.BIPARTITELIST;
                    } else{
                        type = getIntelligentDomain(iv);
                    }
                    v =  new IntDomainVarImpl(solver, iv.getName(), type, iv.getLowB(), iv.getUppB());
                }
            } else {
                int[] values = iv.getValues();
                if(values.length>1) {
                    int type = IntDomainVar.BITSET; // default type
                    if (iv.getOptions().contains("cp:link")) {
                        type = IntDomainVar.LINKEDLIST;
                    } else if (iv.getOptions().contains("cp:btree")) {
                        type = IntDomainVar.BINARYTREE;
                    } else if (iv.getOptions().contains("cp:blist")) {
                        type = IntDomainVar.BIPARTITELIST;
                    }
                    v = new IntDomainVarImpl(solver, iv.getName(), type, values);
                }
            }
            ((CPSolver) solver).addIntVar(v);
            return v;
        }
        if (Choco.DEBUG) {
            LOGGER.severe("Count not found implementation for IntegerVariable !");
            System.exit(-1);
        }
        return null;
    }

    /**
     * Build a expression node
     *
     * @param solver associated solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return a variable leaf or constant leaf (for expression tree)
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] vars) {
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
    public int getIntelligentDomain(IntegerVariable v) {
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
        Iterator<Constraint> it = v.getConstraintIterator();
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
