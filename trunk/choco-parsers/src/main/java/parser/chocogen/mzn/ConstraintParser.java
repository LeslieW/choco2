/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package parser.chocogen.mzn;

import choco.Choco;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import noNamespace.ConstraintDocument;
import noNamespace.ExprDocument;
import noNamespace.FlatzincDocument;
import noNamespace.PredDocument;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class ConstraintParser {

    final ExpressionParser ep;

    private static final String int_eq = "int_eq";
    private static final String int_ne = "int_ne";
    private static final String int_lin_eq = "int_lin_eq";
    private static final String int_lin_ne = "int_lin_ne";

    public ConstraintParser(ExpressionParser ep) {
        this.ep = ep;
    }

    void readConstraints(FlatzincDocument.Flatzinc flatzinc, Model m){
        for(ConstraintDocument.Constraint c : flatzinc.getConstraintArray()){
            Constraint constraint  = buildConstraint(c, m);
            m.addConstraint(constraint);
        }
    }

    private Constraint buildConstraint(ConstraintDocument.Constraint c, Model m) {
        PredDocument.Pred pred = c.getPred();
        String type = pred.getName().getStringValue();

        if(type.equals(int_eq)){
            return c_int_eq(pred);
        }else
        if(type.equals(int_ne)){
            return c_int_ne(pred);
        }else
        if(type.equals(int_lin_eq)){
            return c_int_lin_eq(pred);
        }else
        if(type.equals(int_lin_ne)){
            return c_int_lin_ne(pred);
        }else{
            throw new ModelException("ConstraintParser:buildConstraint: unknown constraint type \""+type+"\"");
        }
    }

    /**
     * Build a neq constraint
     * @param pred predicat
     * @return constraint
     */
    private Constraint c_int_eq(PredDocument.Pred pred) {
        ExprDocument.Expr e1 = pred.getExprArray(0);
        ExprDocument.Expr e2 = pred.getExprArray(1);
        Object o1 = ep.readExpression(e1);
        Object o2 = ep.readExpression(e2);

        if (o1 instanceof IntegerVariable) {
            if (o2 instanceof IntegerVariable) {
                return Choco.eq((IntegerVariable) o1, (IntegerVariable) o2);
            } else if (o2 instanceof Integer) {
                return Choco.eq((IntegerVariable) o1, (Integer) o2);
            } else {
                throw new ModelException("ConstraintParser:c_int_eq: not yet implemented");
            }
        } else if (o1 instanceof Integer) {
            if (o2 instanceof IntegerVariable) {
                return Choco.eq((Integer) o1, (IntegerVariable) o2);
            } else if (o2 instanceof Integer) {
                return (o1 != o2 ? Choco.TRUE : Choco.FALSE);
            } else {
                throw new ModelException("ConstraintParser:c_int_eq: not yet implemented");
            }
        } else {
            throw new ModelException("ConstraintParser:c_int_eq: not yet implemented");
        }
    }

    /**
     * Build a neq constraint
     * @param pred predicat
     * @return constraint
     */
    private Constraint c_int_ne(PredDocument.Pred pred) {
        ExprDocument.Expr e1 = pred.getExprArray(0);
        ExprDocument.Expr e2 = pred.getExprArray(1);
        Object o1 = ep.readExpression(e1);
        Object o2 = ep.readExpression(e2);

        if (o1 instanceof IntegerVariable) {
            if (o2 instanceof IntegerVariable) {
                return Choco.neq((IntegerVariable) o1, (IntegerVariable) o2);
            } else if (o2 instanceof Integer) {
                return Choco.neq((IntegerVariable) o1, (Integer) o2);
            } else {
                throw new ModelException("ConstraintParser:c_int_ne: not yet implemented");
            }
        } else if (o1 instanceof Integer) {
            if (o2 instanceof IntegerVariable) {
                return Choco.neq((Integer) o1, (IntegerVariable) o2);
            } else if (o2 instanceof Integer) {
                return (o1 != o2 ? Choco.TRUE : Choco.FALSE);
            } else {
                throw new ModelException("ConstraintParser:c_int_ne: not yet implemented");
            }
        } else {
            throw new ModelException("ConstraintParser:c_int_ne: not yet implemented");
        }
    }

    private Constraint c_int_lin_eq(PredDocument.Pred pred) {
        ExprDocument.Expr e1 = pred.getExprArray(0);
        ExprDocument.Expr e2 = pred.getExprArray(1);
        ExprDocument.Expr e3 = pred.getExprArray(2);
        Object o1 = ep.readExpression(e1);
        Object o2 = ep.readExpression(e2);
        Object o3 = ep.readExpression(e3);

        if(o1 instanceof int[]){
            return Choco.eq(Choco.scalar((int[])o1, toIntegerVariableArray(o2)), (Integer)o3);
        }else if(o1 instanceof Object[]){
            return Choco.eq(Choco.scalar(toIntegerVariableArray(o1), (int[])(o2)), (Integer)o3);
        }
        throw new ModelException("ConstraintParser:c_int_lin_eq: not yet implemented");
    }

    private Constraint c_int_lin_ne(PredDocument.Pred pred) {
        ExprDocument.Expr e1 = pred.getExprArray(0);
        ExprDocument.Expr e2 = pred.getExprArray(1);
        ExprDocument.Expr e3 = pred.getExprArray(2);
        Object o1 = ep.readExpression(e1);
        Object o2 = ep.readExpression(e2);
        Object o3 = ep.readExpression(e3);

        if(o1 instanceof int[]){
            return Choco.neq(Choco.scalar((int[])o1, toIntegerVariableArray(o2)), (Integer)o3);
        }else if(o1 instanceof Object[]){
            return Choco.neq(Choco.scalar(toIntegerVariableArray(o1), (int[])(o2)), (Integer)o3);
        }
        throw new ModelException("ConstraintParser:c_int_lin_eq: not yet implemented");
    }

    /**
     * Build an array of IntegerVariable from an object
     * @param o hidden array
     * @return array
     */
    private static IntegerVariable[] toIntegerVariableArray(Object o) {
        Object[] os = (Object[])o;
        IntegerVariable[] variables  = new IntegerVariable[os.length];
        System.arraycopy(os, 0, variables, 0, os.length);
        return variables;
    }
}
