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
import choco.cp.solver.variables.real.RealVarImpl;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 13:36:45
 */
public class RealVariableManager implements VariableManager {

    /**
     * Build a real variable for the given solver
     *
     * @param solver
     * @param var
     * @return a real variable
     */
    public Var makeVariable(Solver solver, Variable var) {
        if (solver instanceof CPSolver) {
            if (var instanceof RealConstantVariable) {
                RealConstantVariable rcv = (RealConstantVariable) var;
                // Constant treatment
                double value = rcv.getValue();
//                RealVar s = new RealVarImpl(solver, rcv.getName(), value, value, RealVar.BOUNDS);
                RealIntervalConstant s = new RealIntervalConstant(value, value);
                ((CPSolver) solver).addrealConstant(value, s);
                return s;
            }
            RealVariable rv = (RealVariable) var;
            RealVar s = new RealVarImpl(solver, rv.getName(), rv.getLowB(), rv.getUppB(), RealVar.BOUNDS);
            ((CPSolver) solver).addRealVar(s);
            return s;
        }
        if (Choco.DEBUG) {
            LOGGER.severe("Count not found implementation for RealVariable !");
            System.exit(-1);
        }
        return null;
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
