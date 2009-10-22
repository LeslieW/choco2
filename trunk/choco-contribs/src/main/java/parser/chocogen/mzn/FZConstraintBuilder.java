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
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.HashMap;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 21 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class FZConstraintBuilder {

    static HashMap<String, Object> memory;
    static CPModel model;
    static Logger LOGGER = ChocoLogging.getParserLogger();

    public static void init(HashMap<String, Object> memory, CPModel model) {
        FZConstraintBuilder.memory = memory;
        FZConstraintBuilder.model = model;
    }

    // VARIABLE TYPE
    private static final String _int = "int";
    private static final String _float = "float";
    private static final String _bool = "bool";
    private static final String _set = "set";

    // COMPARISONS OPERATIONS
    private static final String _eq = "_eq";
    private static final String _ne = "_ne";
    private static final String _lt = "_lt";
    private static final String _gt = "_gt";
    private static final String _ge = "_ge";
    private static final String _le = "_le";

    private static final String _lin = "_lin";

    private static final String _reif = "_reif";

    // ARITHMETIC OPERATIONS
    private static final String _plus = "_plus";
    private static final String _minus = "_minus";
    private static final String _times = "_times";
    private static final String _negate = "_negate";
    private static final String _div = "_div";
    private static final String _mod = "_mod";
    private static final String _min = "_min";
    private static final String _max = "_max";
    private static final String _abs = "_abs";

    // LOGICAL OPERATIONS
    // CONJUNCTIONS
    private static final String _and = "_and";
    private static final String _or = "_or";
    private static final String _left_imp = "_left_imp";
    private static final String _right_imp = "_right_imp";
    private static final String _xor = "_xor";
    private static final String _not = "_not";
    // N-ARY CONJUNCTIONS
    private static final String _array = "array";
    // CLAUSES
    private static final String _clause = "_clause";

    // SET OPERATIONS
    private static final String _in = "_in";
    private static final String _subset = "_subset";
    private static final String _superset = "_superset";
    private static final String _union = "_union";
    private static final String _intersect = "_intersect";
    private static final String _diff = "_diff";
    private static final String _symdiff = "_symdiff";
    private static final String _card = "_card";

    // ARRAY OPERATIONS
    private static final String _element = "_element";

    // COERCION OPERATIONS
    private static final String _int2float = "int2float";
    private static final String _bool2int = "bool2int";


    public static void build(String name, FZVariableBuilder.ValType[] vts) {
        if (name.contains(_int)) {
            buildInt(name, vts);
            return;
        } else if (name.contains(_float)) {
            buildFloat(name, vts);
            return;
        } else if (name.contains(_bool)) {
            buildBool(name, vts);
            return;
        } else if (name.contains(_set)) {
            buildSet(name, vts);
            return;
        } else
            LOGGER.severe("buildCstr::ERROR:: unknown type :" + name);
        System.exit(-1);
    }

    private static void buildInt(String name, FZVariableBuilder.ValType[] vts) {
        Constraint c = null;
        if (name.contains(_lin)) {
            // get coeffs
            FZVariableBuilder.ValType[] vttmp = ((FZVariableBuilder.ValType[]) vts[0].obj);
            int[] coeffs = new int[vttmp.length];
            for (int i = 0; i < coeffs.length; i++) {
                coeffs[i] = (Integer) vttmp[i].obj;
            }
            vttmp = ((FZVariableBuilder.ValType[]) vts[1].obj);
            IntegerVariable[] vars = new IntegerVariable[vttmp.length];
            for (int i = 0; i < vars.length; i++) {
                try {
                    vars[i] = (IntegerVariable) vttmp[i].obj;
                } catch (ClassCastException cc) {
                    vars[i] = Choco.constant((Integer) vttmp[i].obj);
                }
            }
            int result = (Integer) vts[2].obj;
            if (name.contains(_eq)) {
                c = (eq(scalar(coeffs, vars), result));
            } else if (name.contains(_ne)) {
                c = (neq(scalar(coeffs, vars), result));
            } else if (name.contains(_gt)) {
                c = (gt(scalar(coeffs, vars), result));
            } else if (name.contains(_lt)) {
                c = (lt(scalar(coeffs, vars), result));
            } else if (name.contains(_ge)) {
                c = (geq(scalar(coeffs, vars), result));
            } else if (name.contains(_le)) {
                c = (leq(scalar(coeffs, vars), result));
            }
        } else if (name.contains(_array) && name.contains(_element)) {


        } else {
            IntegerVariable[] vars = new IntegerVariable[vts.length];
            for (int i = 0; i < vars.length; i++) {
                try {
                    vars[i] = (IntegerVariable) vts[i].obj;
                } catch (ClassCastException cc) {
                    vars[i] = Choco.constant((Integer) vts[i].obj);
                }
            }
            if (name.contains(_eq)) {
                c = (eq(vars[0], vars[1]));
            } else if (name.contains(_ne)) {
                c = (neq(vars[0], vars[1]));
            } else if (name.contains(_gt)) {
                c = (gt(vars[0], vars[1]));
            } else if (name.contains(_lt)) {
                c = (lt(vars[0], vars[1]));
            } else if (name.contains(_ge)) {
                c = (geq(vars[0], vars[1]));
            } else if (name.contains(_le)) {
                c = (leq(vars[0], vars[1]));
            } else if (name.contains(_plus)) {
                c = (eq(plus(vars[0], vars[1]), vars[2]));
            } else if (name.contains(_minus)) {
                c = (eq(minus(vars[0], vars[1]), vars[2]));
            } else if (name.contains(_times)) {
                c = (times(vars[0], vars[1], vars[2]));
            } else if (name.contains(_negate)) {
                c = (eq(neg(vars[0]), vars[1]));
            } else if (name.contains(_div)) {
                c = (eq(div(vars[0], vars[1]), vars[2]));
            } else if (name.contains(_mod)) {
                c = (eq(mod(vars[0], vars[1]), vars[2]));
            } else if (name.contains(_min)) {
                c = (min(vars[0], vars[1], vars[2]));
            } else if (name.contains(_max)) {
                c = (max(vars[0], vars[1], vars[2]));
            } else if (name.contains(_abs)) {
                c = (eq(abs(vars[0]), vars[1]));
            } else if(name.contains(_bool)){
                // beware... it is due to the fact that in choco, there are no boolean variable
                // but integer variable with [0,1] domain.
                c = (eq(vars[0], vars[1]));
            }
        }
        if(c!=null){
            if (!name.contains(_reif)) {
                model.addConstraint(c);
            } else {
                IntegerVariable vr = (IntegerVariable) vts[vts.length-1].obj;
                model.addConstraint(reifiedIntConstraint(vr, c));
            }
            return;
        }
        LOGGER.severe("buildInt::ERROR:: unknown type :" + name);
        System.exit(-1);
    }

    private static void buildFloat(String name, FZVariableBuilder.ValType[] vts) {
        /*if (name.contains(_eq)) {

        } else if (name.contains(_ne)) {

        } else if (name.contains(_gt)) {

        } else if (name.contains(_lt)) {

        } else if (name.contains(_ge)) {

        } else if (name.contains(_le)) {

        } else if (name.contains(_plus)) {

        } else if (name.contains(_minus)) {

        } else if (name.contains(_times)) {

        } else if (name.contains(_div)) {

        } else*/ {
            LOGGER.severe("buildFloat::ERROR:: unknown type :" + name);
            System.exit(-1);
        }
    }


    /**
     * FYI : bool == 1 is true
     * @param name
     * @param vts
     */
    private static void buildBool(String name, FZVariableBuilder.ValType[] vts) {
        Constraint c = null;
        if(name.contains((_array))){
            FZVariableBuilder.ValType[] vttmp = ((FZVariableBuilder.ValType[]) vts[0].obj);
            IntegerVariable[] vars = new IntegerVariable[vttmp.length];
            Constraint[] eqs = new Constraint[vttmp.length];
            for (int i = 0; i < vars.length; i++) {
                try {
                    vars[i] = (IntegerVariable) vttmp[i].obj;
                } catch (ClassCastException cc) {
                    vars[i] = Choco.constant((Integer) vttmp[i].obj);
                }
                eqs[i] = eq(vars[i], 1);
            }
            IntegerVariable result;
            try {
                result = (IntegerVariable) vts[1].obj;
            } catch (ClassCastException cc) {
                result = Choco.constant((Integer) vts[1].obj);
            }
            if(name.contains(_and)){
                c = (reifiedIntConstraint(result, and(eqs)));
            }else
            if(name.contains(_or)){
                c = (reifiedIntConstraint(result, or(eqs)));
            }
        }else
        if(name.contains(_clause)){
            FZVariableBuilder.ValType[] vttmp = ((FZVariableBuilder.ValType[]) vts[0].obj);
            IntegerVariable[] posLits = new IntegerVariable[vttmp.length];
            for (int i = 0; i < posLits.length; i++) {
                try {
                    posLits[i] = (IntegerVariable) vttmp[i].obj;
                } catch (ClassCastException cc) {
                    posLits[i] = Choco.constant((Integer) vttmp[i].obj);
                }
            }
            vttmp = ((FZVariableBuilder.ValType[]) vts[1].obj);
            IntegerVariable[] negLits = new IntegerVariable[vttmp.length];
            for (int i = 0; i < negLits.length; i++) {
                try {
                    negLits[i] = (IntegerVariable) vttmp[i].obj;
                } catch (ClassCastException cc) {
                    negLits[i] = Choco.constant((Integer) vttmp[i].obj);
                }
            }
            c = (clause(posLits, negLits));
        }else
        {
            IntegerVariable[] vars = new IntegerVariable[vts.length];
            for (int i = 0; i < vars.length; i++) {
                try {
                    vars[i] = (IntegerVariable) vts[i].obj;
                } catch (ClassCastException cc) {
                    try{
                        vars[i] = Choco.constant((Integer) vts[i].obj);
                    } catch (ClassCastException ce) {
                        Boolean b = (Boolean) vts[i].obj;
                        vars[i] = Choco.constant(b?1:0);
                    }
                }
            }
            if (name.contains(_eq)) {
                // 2 or 3 arguments...
                switch (vars.length){
                    case 2:
                        c = (eq(vars[0], vars[1]));
                        break;
                    case 3:
                        c = (reifiedIntConstraint(vars[2], eq(vars[0], vars[1])));
                        break;
                    default:
                        break;
                }
            } else if (name.contains(_ne)) {
                c = (eq(vars[0], vars[1]));
            } else if (name.contains(_gt)) {
                c = (gt(vars[0], vars[1]));
            } else if (name.contains(_lt)) {
                c = (lt(vars[0], vars[1]));
            } else if (name.contains(_ge)) {
                c = (geq(vars[0], vars[1]));
            } else if (name.contains(_le)) {
                c = (leq(vars[0], vars[1]));
            } else if (name.contains(_and)) {
                c = (reifiedIntConstraint(vars[2], and(eq(vars[0], 1), eq(vars[1], 1))));
            } else if (name.contains(_or)) {
                c = (reifiedIntConstraint(vars[2], or(eq(vars[0], 1), eq(vars[1], 1))));
            } else if (name.contains(_xor)) {
                c = (reifiedIntConstraint(vars[2], or(and(eq(vars[0], 1), eq(vars[1], 0)), and(eq(vars[0], 0), eq(vars[1], 1)))));
            } else if (name.contains(_not)) {
                c = (reifiedIntConstraint(vars[2], not(eq(vars[0], vars[1]))));
            }
        }
        if(c!=null){
            if (!name.contains(_reif)) {
                model.addConstraint(c);
            } else {
                IntegerVariable vr = (IntegerVariable) vts[vts.length-1].obj;
                model.addConstraint(reifiedIntConstraint(vr, c));
            }
            return;
        }
        LOGGER.severe("buildBool::ERROR:: unknown type :" + name);
        System.exit(-1);
    }

    private static void buildSet(String name, FZVariableBuilder.ValType[] vts) {
        Constraint c = null;
        if (name.contains(_eq)) {

        } else if (name.contains(_ne)) {

        } else if (name.contains(_in)) {

        } else if (name.contains(_subset)) {

        } else if (name.contains(_superset)) {

        } else if (name.contains(_intersect)) {

        } else if (name.contains(_card)) {

        }
        if(c!=null){
            if (!name.contains(_reif)) {
                model.addConstraint(c);
            } else {
                IntegerVariable vr = (IntegerVariable) vts[vts.length-1].obj;
                model.addConstraint(reifiedIntConstraint(vr, c));
            }
            return;
        }
        LOGGER.severe("buildSet::ERROR:: unknown type :" + name);
        System.exit(-1);
    }
}
