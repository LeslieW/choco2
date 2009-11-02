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
import choco.kernel.model.variables.set.SetVariable;

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
    private static final String _var = "_var";

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

    private static int getInt(FZVariableBuilder.ValType vt){
        try{
            return (Integer) vt.obj;
        }catch (ClassCastException ee){
            return Integer.parseInt((String) vt.obj);
        }
    }

    public static int[] getInts(FZVariableBuilder.ValType vt){
        if(vt.type.equals(FZVariableBuilder.EnumVal.objects)){
            Integer[] ints = (Integer[])vt.obj;
            int[] pints = new int[ints.length];
            for (int i = 0; i < pints.length; i++) {
                //noinspection UnnecessaryUnboxing
                pints[i] = ints[i].intValue();
            }
            return pints;
        }else{
            FZVariableBuilder.ValType[] vts = (FZVariableBuilder.ValType[])vt.obj;
            int[] ints = new int[vts.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = getInt(vts[i]);
            }
            return ints;
        }
    }

    public static IntegerVariable getIntVar(FZVariableBuilder.ValType vt){
        try {
            return (IntegerVariable) vt.obj;
        } catch (ClassCastException cc) {
            return Choco.constant((Integer) vt.obj);
        }
    }

    public static IntegerVariable[] getIntVars(FZVariableBuilder.ValType vt)throws ClassCastException{
        if(vt.type.equals(FZVariableBuilder.EnumVal.objects)){
                return (IntegerVariable[])vt.obj;
        }else{
            return getIntVars((FZVariableBuilder.ValType[])vt.obj);
        }
    }

    public static IntegerVariable[] getIntVars(FZVariableBuilder.ValType[] vts){
        IntegerVariable[] vars = new IntegerVariable[vts.length];
        for (int i = 0; i < vts.length; i++) {
                vars[i] = getIntVar(vts[i]);
            }
        return vars;
    }

    private static void buildInt(String name, FZVariableBuilder.ValType[] vts) {
        Constraint c = null;
        if (name.contains(_lin)) {

            int[] coeffs = getInts(vts[0]);
            IntegerVariable[] vars = getIntVars(vts[1]);
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
            IntegerVariable index = getIntVar(vts[0]);
            IntegerVariable val = getIntVar(vts[2]);
            if(name.contains(_var)){
                try{
                    IntegerVariable[] values = getIntVars(vts[1]);
                    c = nth(index, values, val, -1);
                }catch (ClassCastException e){
                    int[] values = getInts(vts[1]);
                    c = nth(index, values, val, -1);
                }
            }else{
                int[] values = getInts(vts[1]);
                c = nth(index, values, val, -1);
            }

        } else {
            IntegerVariable[] vars = getIntVars(vts);

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


    private static IntegerVariable getBoolVar(FZVariableBuilder.ValType vt) {
        try {
            return (IntegerVariable) vt.obj;
        } catch (ClassCastException cc) {
            try {
                return Choco.constant((Integer) vt.obj);
            } catch (ClassCastException cce) {
                return Choco.constant(((Boolean) vt.obj) ? 1 : 0);
            }
        }
    }

    public static IntegerVariable[] getBoolVars(FZVariableBuilder.ValType vt){
        if(vt.type.equals(FZVariableBuilder.EnumVal.objects)){
            return (IntegerVariable[])vt.obj;
        }else{
            return getBoolVars((FZVariableBuilder.ValType[])vt.obj);
        }
    }

    public static IntegerVariable[] getBoolVars(FZVariableBuilder.ValType[] vts){
        IntegerVariable[] vars = new IntegerVariable[vts.length];
        for (int i = 0; i < vts.length; i++) {
                vars[i] = getBoolVar(vts[i]);
            }
        return vars;
    }

    /**
     * FYI : bool == 1 is true
     * @param name
     * @param vts
     */
    private static void buildBool(String name, FZVariableBuilder.ValType[] vts) {
        Constraint c = null;
        if (name.contains((_array))) {
            if (name.contains(_element)) {
                if (name.contains(_var)) {
                    IntegerVariable index = getIntVar(vts[0]);
                    IntegerVariable[] values = getBoolVars(vts[1]);
                    IntegerVariable val = getBoolVar(vts[2]);
                    c = nth(index, values, val);
                } else {
                    IntegerVariable index = getIntVar(vts[0]);
                    int[] values = getInts(vts[1]);
                    IntegerVariable val = getBoolVar(vts[2]);
                    c = nth(index, values, val);
                }
            } else {

                IntegerVariable[] vars = getBoolVars(vts[0]);
                IntegerVariable result = getBoolVar(vts[1]);

                if (name.contains(_and)) {
                    c = (reifiedAnd(result, vars));
                } else if (name.contains(_or)) {
                    c = (reifiedOr(result, vars));
                }
            }
        } else if (name.contains(_clause)) {

            IntegerVariable[] posLits = getBoolVars(vts[0]);
            IntegerVariable[] negLits = getBoolVars(vts[1]);

            c = (clause(posLits, negLits));
        } else {
            IntegerVariable[] vars = new IntegerVariable[vts.length];
            for (int i = 0; i < vars.length; i++) {
                vars[i] = getBoolVar(vts[i]);
            }
            if (name.contains(_eq)) {
                // 2 or 3 arguments...
                switch (vars.length) {
                    case 2:
                        c = (eq(vars[0], vars[1]));
                        break;
                    case 3:
                        c = (reifiedXnor(vars[2], vars[0], vars[1]));
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
                c = (reifiedAnd(vars[2], vars[0], vars[1]));
            } else if (name.contains(_or)) {
                c = (reifiedOr(vars[2], vars[0], vars[1]));
            } else if (name.contains(_xor)) {
                c = (reifiedXor(vars[2], vars[0], vars[1]));
            } else if (name.contains(_not)) {
                c = (reifiedIntConstraint(vars[2], neq(vars[0], vars[1])));
            } else if (name.contains(_right_imp)) {
                c = (reifiedRightImp(vars[2], vars[0], vars[1]));
            } else if (name.contains(_left_imp)) {
                c = (reifiedLeftImp(vars[2], vars[0], vars[1]));
            }
        }
        if (c != null) {
            if (!name.contains(_reif)) {
                model.addConstraint(c);
            } else {
                IntegerVariable vr = (IntegerVariable) vts[vts.length - 1].obj;
                model.addConstraint(reifiedIntConstraint(vr, c));
            }
            return;
        }
        LOGGER.severe("buildBool::ERROR:: unknown type :" + name);
        System.exit(-1);
    }

    private static SetVariable getSetVar(FZVariableBuilder.ValType vt) {
        if(vt.type.equals(FZVariableBuilder.EnumVal.interval)){
            FZVariableBuilder.ValType[] vts = (FZVariableBuilder.ValType[])vt.obj;
            int i1 = getInt(vts[0]);
            int i2 = getInt(vts[1]);
            int[] values = new int[i2-i1+1];
            for(int i = i1; i <= i2; i++){
                values[i-i1] = i;
            }
            return constant(values);
        }

        return (SetVariable) vt.obj;
    }

    public static SetVariable[] getSetVars(FZVariableBuilder.ValType vt){
        if(vt.type.equals(FZVariableBuilder.EnumVal.objects)){
            return (SetVariable[])vt.obj;
        }else{
            return getSetVars((FZVariableBuilder.ValType[])vt.obj);
        }
    }

    public static SetVariable[] getSetVars(FZVariableBuilder.ValType[] vts){
        SetVariable[] vars = new SetVariable[vts.length];
        for (int i = 0; i < vts.length; i++) {
                vars[i] = getSetVar(vts[i]);
            }
        return vars;
    }

    private static void buildSet(String name, FZVariableBuilder.ValType[] vts) {
        Constraint c = null;
        if (name.contains(_eq)) {

        } else if (name.contains(_ne)) {

        } else if (name.contains(_in)) {
            IntegerVariable iv = getIntVar(vts[0]);
            SetVariable sv = getSetVar(vts[1]);
            c = member(iv, sv);
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
