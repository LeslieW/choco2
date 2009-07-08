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
package choco.cp.model.managers.constraints;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.model.managers.RealConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.ConstantSConstraint;
import choco.cp.solver.constraints.integer.*;
import choco.cp.solver.constraints.real.MixedEqXY;
import choco.cp.solver.constraints.real.exp.RealMinus;
import choco.cp.solver.constraints.reified.leaves.bool.*;
import choco.cp.solver.constraints.set.SetEq;
import choco.cp.solver.constraints.set.SetNotEq;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import static choco.kernel.model.constraints.ConstraintType.*;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealVar;

import java.util.HashSet;

/*
 * User:    charles
 * Date:    22 août 2008
 */
public class EqManager extends IntConstraintManager {

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
        if (solver instanceof CPSolver) {
            if (parameters instanceof ConstraintType) {
                ConstraintType type = (ConstraintType) parameters;
                CPSolver cpsolver = (CPSolver) solver;
                Variable v1 = variables[0];
                Variable v2 = variables[1];
                int ty = VariableUtils.checkType(v1.getVariableType(), v2.getVariableType());
                switch(type){
                    case EQ:
                        switch (ty) {
                            case 11:
                                return createIntEq(cpsolver, (IntegerVariable) v1, (IntegerVariable) v2);
                            case 22:
                                return new SetEq(cpsolver.getVar((SetVariable) v1), cpsolver.getVar((SetVariable) v2));
                            case 33:
                                return createRealEq(cpsolver, (RealExpressionVariable) v1, (RealExpressionVariable) v2);
                            case 13:
                                return new MixedEqXY(cpsolver.getVar((RealVariable) v2), cpsolver.getVar((IntegerVariable) v1));
                            case 31:
                                return new MixedEqXY(cpsolver.getVar((RealVariable) v1), cpsolver.getVar((IntegerVariable) v2));
                            case 21:
                                return createIntEq(cpsolver, ((SetVariable) v1).getCard(), (IntegerVariable) v2);
//                            return new SetCard(solver.getVar((SetVariable)v1), solver.getVar((IntegerVariable)v2), true, true);
                            case 12:
                                return createIntEq(cpsolver, (IntegerVariable) v1, ((SetVariable) v2).getCard());
//                            return new SetCard(solver.getVar((SetVariable)v2), solver.getVar((IntegerVariable)v1), true, true);
                            default:
                                return null;
                        }
                    case NEQ:
                        switch (ty) {
                            case 11:
                                return createIntNeq(cpsolver, (IntegerVariable) v1, (IntegerVariable) v2);
                            case 22:
                                return new SetNotEq(cpsolver.getVar((SetVariable) v1), cpsolver.getVar((SetVariable) v2));
                            case 12:
                                return createIntNeq(cpsolver, (IntegerVariable) v1, ((SetVariable) v2).getCard());
                            case 21:
                                return createIntNeq(cpsolver, ((SetVariable) v1).getCard(), (IntegerVariable) v2);
                            default:
                                return null;
                        }
                    case GEQ:
                        switch (ty) {
                            case 11:
                                return createIntGeq(cpsolver, (IntegerVariable) v1, (IntegerVariable) v2);
                            case 21:
                                return createIntGeq(cpsolver, ((SetVariable) v1).getCard(), (IntegerVariable) v2);
                            case 12:
                                return createIntGeq(cpsolver, (IntegerVariable) v1, ((SetVariable) v2).getCard());
                            case 33:
                                return createRealLeq(cpsolver, (RealExpressionVariable) v2, (RealExpressionVariable) v1);
                            default:
                                return null;
                        }
                    case GT:
                        switch (ty) {
                            case 11:
                                return createIntGt(cpsolver, (IntegerVariable) v1, (IntegerVariable) v2);
                            case 21:
                                return createIntGt(cpsolver, ((SetVariable) v1).getCard(), (IntegerVariable) v2);
                            case 12:
                                return createIntGt(cpsolver, (IntegerVariable) v1, ((SetVariable) v2).getCard());
                            default:
                                return null;
                        }
                    case LEQ:
                        switch (ty) {
                            case 11:
                                return createIntLeq(cpsolver, (IntegerVariable) v1, (IntegerVariable) v2);
                            case 21:
                                return createIntLeq(cpsolver, ((SetVariable) v1).getCard(), (IntegerVariable) v2);
                            case 12:
                                return createIntLeq(cpsolver, (IntegerVariable) v1, ((SetVariable) v2).getCard());
                            case 33:
                                return createRealLeq(cpsolver, (RealExpressionVariable) v1, (RealExpressionVariable) v2);
                            default:
                                return null;
                        }
                    case LT:
                        switch (ty) {
                            case 11:
                                return createIntLt(cpsolver, (IntegerVariable) v1, (IntegerVariable) v2);
                            case 21:
                                return createIntLt(cpsolver, ((SetVariable) v1).getCard(), (IntegerVariable) v2);
                            case 12:
                                return createIntLt(cpsolver, (IntegerVariable) v1, ((SetVariable) v2).getCard());
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            }
        }

        if (Choco.DEBUG) {
            throw new RuntimeException("Could not find manager for Eq !");
        }
        return null;
    }


    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param cstrs  constraints (can be null)
     * @param vars   variables
     * @return
     */
    @Override
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] vars) {
        ComponentConstraint cc = (ComponentConstraint) cstrs[0];
        if (cc.getParameters() instanceof ConstraintType) {
            ConstraintType type = (ConstraintType) cc.getParameters();
            INode[] nt = new INode[cc.getVariables().length];
            for (int i = 0; i < cc.getVariables().length; i++) {
                IntegerExpressionVariable v = (IntegerExpressionVariable) cc.getVariable(i);
                nt[i] = v.getEm().makeNode(solver, v.getConstraints(), v.getVariables());
            }
            if (EQ == type) {
                return new EqNode(nt);
            }else if(NEQ == type){
                return new NeqNode(nt);
            }else if(GEQ == type){
                return new GeqNode(nt);
            }else if(LEQ == type){
                return new LeqNode(nt);
            }else if(GT == type){
                return new GtNode(nt);
            }else if(LT == type){
                return new LtNode(nt);
            }
        }
        return null;
    }


    //##################################################################################################################
    //###                                    Integer equalities                                                      ###
    //##################################################################################################################

    SConstraint createIntEq(CPSolver s, IntegerVariable v1, IntegerVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        int c;
        switch (tv1) {
            case CONSTANT_INTEGER:
                c = ((IntegerConstantVariable) v1).getValue();
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        return new ConstantSConstraint(c == ((IntegerConstantVariable) v2).getValue());
                    case INTEGER:
                        return new EqualXC(s.getVar(v2), c);
                }
            case INTEGER:
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        c = ((IntegerConstantVariable) v2).getValue();
                        return new EqualXC(s.getVar(v1), c);
                    case INTEGER:
                        return new EqualXYC(s.getVar(v1), s.getVar(v2), 0);
                }
        }
        return null;
    }

    //##################################################################################################################
    //###                                       Real equalities                                                      ###
    //##################################################################################################################

    SConstraint createRealEq(CPSolver s, RealExpressionVariable v1, RealExpressionVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        RealExp t1;
        RealExp t2;
        RealIntervalConstant zero = new RealIntervalConstant(0,0);

        switch (tv1) {
            case CONSTANT_DOUBLE:
                t1 = (RealIntervalConstant)s.getVar(v1);
                switch (tv2) {
                    case CONSTANT_DOUBLE:
                        t2 = ((RealIntervalConstant) s.getVar(v2));
                        return new ConstantSConstraint(v1.getLowB() == v2.getLowB());
                    case REAL:
                        t2 = (RealVar)s.getVar(v2);
                        return s.makeEquation(t2, (RealIntervalConstant)t1);
                    case REAL_EXPRESSION:
                        t2 = ((RealConstraintManager)v2.getRcm()).makeRealExpression(s, v2.getVariables());
                        return s.makeEquation(t2, (RealIntervalConstant)t1);
                }
            case REAL:
                t1 = (RealVar)s.getVar(v1);
                switch (tv2) {
                    case CONSTANT_DOUBLE:
                        t2 = ((RealIntervalConstant) s.getVar(v2));
                        return s.makeEquation(t1, (RealIntervalConstant)t2);
                    case REAL:
                        t2 = (RealVar)s.getVar(v2);
                        return s.makeEquation(new RealMinus(s, t1, t2), zero);
                    case REAL_EXPRESSION:
                        t2 = ((RealConstraintManager)v2.getRcm()).makeRealExpression(s, v2.getVariables());
                        return s.makeEquation(new RealMinus(s, t1, t2), zero);
                }
            case REAL_EXPRESSION:
                t1 = ((RealConstraintManager)v1.getRcm()).makeRealExpression(s, v1.getVariables());
                switch (tv2) {
                    case CONSTANT_DOUBLE:
                        t2 = ((RealIntervalConstant) s.getVar(v2));
                        return s.makeEquation(t1, (RealIntervalConstant)t2);
                    case REAL:
                        t2 = (RealExp)s.getVar(v2);
                        return s.makeEquation(new RealMinus(s, t1, t2), zero);
                    case REAL_EXPRESSION:
                        t2 = ((RealConstraintManager)v2.getRcm()).makeRealExpression(s, v2.getVariables());
                        return s.makeEquation(new RealMinus(s, t1, t2), zero);
                }

        }
        return null;
    }

    //##################################################################################################################
    //###                                    Integer inequalities                                                    ###
    //##################################################################################################################

    SConstraint createIntNeq(CPSolver s, IntegerVariable v1, IntegerVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        int c;
        switch (tv1) {
            case CONSTANT_INTEGER:
                c = ((IntegerConstantVariable) v1).getValue();
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        return new ConstantSConstraint(c != ((IntegerConstantVariable) v2).getValue());
                    case INTEGER:
                        return new NotEqualXC(s.getVar(v2), c);
                }
            case INTEGER:
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        c = ((IntegerConstantVariable) v2).getValue();
                        return new NotEqualXC(s.getVar(v1), c);
                    case INTEGER:
                        if (s.getVar(v1).hasEnumeratedDomain() &&
                                s.getVar(v2).hasEnumeratedDomain()) {
                            return new NotEqualXYCEnum(s.getVar(v1), s.getVar(v2), 0);
                        } else {
                            return new NotEqualXYC(s.getVar(v1), s.getVar(v2), 0);
                        }
                }
        }
        return null;
    }


    //##################################################################################################################
    //###                                    Integer GEQ                                                             ###
    //##################################################################################################################

    SConstraint createIntGeq(CPSolver s, IntegerVariable v1, IntegerVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        int c;
        switch (tv1) {
            case CONSTANT_INTEGER:
                c = ((IntegerConstantVariable) v1).getValue();
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        return new ConstantSConstraint(c >= ((IntegerConstantVariable) v2).getValue());
                    case INTEGER:
                        return new LessOrEqualXC(s.getVar(v2), c);
                }
            case INTEGER:
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        c = ((IntegerConstantVariable) v2).getValue();
                        return new GreaterOrEqualXC(s.getVar(v1), c);
                    case INTEGER:
                        return new GreaterOrEqualXYC(s.getVar(v1), s.getVar(v2), 0);
                }
        }
        return null;
    }

    //##################################################################################################################
    //###                                    Integer GT                                                             ###
    //##################################################################################################################

    SConstraint createIntGt(CPSolver s, IntegerVariable v1, IntegerVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        int c;
        switch (tv1) {
            case CONSTANT_INTEGER:
                c = ((IntegerConstantVariable) v1).getValue();
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        return new ConstantSConstraint(c > ((IntegerConstantVariable) v2).getValue());
                    case INTEGER:
                        return new LessOrEqualXC(s.getVar(v2), c-1);
                }
            case INTEGER:
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        c = ((IntegerConstantVariable) v2).getValue();
                        return new GreaterOrEqualXC(s.getVar(v1), c+1);
                    case INTEGER:
                        return new GreaterOrEqualXYC(s.getVar(v1), s.getVar(v2), 1);
                }
        }
        return null;
    }

    //##################################################################################################################
    //###                                    Integer LEQ                                                             ###
    //##################################################################################################################
    SConstraint createIntLeq(CPSolver s, IntegerVariable v1, IntegerVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        int c;
        switch (tv1) {
            case CONSTANT_INTEGER:
                c = ((IntegerConstantVariable) v1).getValue();
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        return new ConstantSConstraint(c <= ((IntegerConstantVariable) v2).getValue());
                    case INTEGER:
                        return new GreaterOrEqualXC(s.getVar(v2), c);
                }
            case INTEGER:
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        c = ((IntegerConstantVariable) v2).getValue();
                        return new LessOrEqualXC(s.getVar(v1), c);
                    case INTEGER:
                        return new GreaterOrEqualXYC(s.getVar(v2), s.getVar(v1), 0);
                }
        }
        return null;
    }

    //##################################################################################################################
    //###                                       Real LEQ                                                             ###
    //##################################################################################################################

    SConstraint createRealLeq(CPSolver s, RealExpressionVariable v1, RealExpressionVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        RealExp t1;
        RealExp t2;
        RealIntervalConstant cst;
        Double POS = Double.POSITIVE_INFINITY;
        Double NEG = Double.NEGATIVE_INFINITY;
        RealIntervalConstant INF = new RealIntervalConstant(NEG,0.0);

        switch (tv1) {
            case CONSTANT_DOUBLE:
                cst = new RealIntervalConstant(v1.getLowB(),POS);
                switch (tv2) {
                    case CONSTANT_DOUBLE:
                        return new ConstantSConstraint(v1.getUppB() <= v2.getUppB());
                    case REAL:
                        t2 = (RealVar)s.getVar(v2);
                        return s.makeEquation(t2, cst);
                    case REAL_EXPRESSION:
                        t2 = ((RealConstraintManager)v2.getRcm()).makeRealExpression(s, v2.getVariables());
                        return s.makeEquation(t2, cst);
                }
            case REAL:
                t1 = (RealVar)s.getVar(v1);
                switch (tv2) {
                    case CONSTANT_DOUBLE:
                        cst = new RealIntervalConstant(NEG, v2.getLowB());
                        return s.makeEquation(t1, cst);
                    case REAL:
                        t2 = (RealVar)s.getVar(v2);
                        return s.makeEquation(new RealMinus(s, t1, t2), INF);
                    case REAL_EXPRESSION:
                        t2 = ((RealConstraintManager)v2.getRcm()).makeRealExpression(s, v2.getVariables());
                        return s.makeEquation(new RealMinus(s, t1, t2), INF);
                }
            case REAL_EXPRESSION:
                t1 = ((RealConstraintManager)v1.getRcm()).makeRealExpression(s, v1.getVariables());
                switch (tv2) {
                    case CONSTANT_DOUBLE:
                        cst = new RealIntervalConstant(NEG, v2.getLowB());
                        return s.makeEquation(t1, cst);
                    case REAL:
                        t2 = (RealExp)s.getVar(v2);
                        return s.makeEquation(new RealMinus(s, t1, t2), INF);
                    case REAL_EXPRESSION:
                        t2 = ((RealConstraintManager)v2.getRcm()).makeRealExpression(s, v2.getVariables());
                        return s.makeEquation(new RealMinus(s, t1, t2), INF);
                }

        }
        return null;
    }

    //##################################################################################################################
    //###                                    Integer LT                                                             ###
    //##################################################################################################################

    SConstraint createIntLt(CPSolver s, IntegerVariable v1, IntegerVariable v2) {
        VariableType tv1 = v1.getVariableType();
        VariableType tv2 = v2.getVariableType();
        switch (tv1) {
            case CONSTANT_INTEGER:
                int c1 = ((IntegerConstantVariable) v1).getValue();
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        return new ConstantSConstraint(c1 < ((IntegerConstantVariable) v2).getValue());
                    case INTEGER:
                        return new GreaterOrEqualXC(s.getVar(v2), c1+1);
                }
            case INTEGER:
                switch (tv2) {
                    case CONSTANT_INTEGER:
                        int c2 = ((IntegerConstantVariable) v2).getValue();
                        return new LessOrEqualXC(s.getVar(v1), c2-1);
                    case INTEGER:
                        return new GreaterOrEqualXYC(s.getVar(v2), s.getVar(v1), 1);
                }
        }
        return null;
    }

}