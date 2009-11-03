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

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;
import static parser.chocogen.mzn.FZVariableBuilder.*;

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

    // GLOBAL CONSTRAINTS
    private static final String _global = "global";
    private static final String _allDifferent = "_allDifferent";
    private static final String _cumulative = "_cumulative";
    private static final String _setDisjoint = "_setDisjoint";
    private static final String _elementBool = "_elementBool";
    private static final String _elementInt = "_elementInt";
    private static final String _globalCardinalityLowUp = "_globalCardinalityLowUp";
    private static final String _globalCardinality = "_globalCardinality";
    private static final String _inverseSet = "_inverseSet";
    private static final String _lexEq = "_lexEq";
    private static final String _lex = "_lex";
    private static final String _member = "_member";
    private static final String _sorting = "_sorting";


    public static void build(String name, FZVariableBuilder.ValType[] vts) {
        if(name.contains(_global)){
            buildGlobal(name, vts);
            return;
        }else
        if (name.contains(_int)) {
            buildInt(name, vts);
            return;
        } else
        if (name.contains(_float)) {
            buildFloat(name, vts);
            return;
        } else
        if (name.contains(_bool)) {
            buildBool(name, vts);
            return;
        } else
        if (name.contains(_set)) {
            buildSet(name, vts);
            return;
        } else
            LOGGER.severe("buildCstr::ERROR:: unknown type :" + name);
        System.exit(-1);
    }


    /**
     * Build a basic constraint based on int variables
     * @param name name of the constraint
     * @param vts parameters of the constraint
     */
    private static void buildInt(String name, ValType[] vts) {
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

    /**
     * Build a basic constraint based on float variables
     * @param name name of the constraint
     * @param vts parameters of the constraint
     */
    private static void buildFloat(String name, ValType[] vts) {
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
     * Build a basic constraint based on bool variables
     * FYI : bool == 1 is true
     * @param name name of the constraint
     * @param vts parameters of the constraint
     */
    private static void buildBool(String name, ValType[] vts) {
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

    /**
     * Build a basic constraint based on set variables
     * @param name name of the constraint
     * @param vts parameters of the constraint
     */
    private static void buildSet(String name, ValType[] vts) {
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

    /**
     * Build a global constraint
     * @param name name of the constraint
     * @param vts parameters of the constraint
     */
    private static void buildGlobal(String name, ValType[] vts){
        Constraint c = null;
        if(name.contains(_allDifferent)){
            IntegerVariable[] vars = getIntVars(vts);
            c = allDifferent(vars);
        }else
        if(name.contains(_cumulative)){
            IntegerVariable[] starts = getIntVars(vts[0]);
            IntegerVariable[] durations = getIntVars(vts[1]);
            // build task variables
            TaskVariable[] tvars = new TaskVariable[starts.length];
            for(int i = 0; i < tvars.length; i++){
                tvars[i] = makeTaskVar("t_"+i, starts[i], durations[i]);
            }
            IntegerVariable[] heights = getIntVars(vts[2]);
            IntegerVariable capa = getIntVar(vts[3]);
            c = cumulative(name, tvars, heights, null, constant(0), capa, (IntegerVariable)null, "");
        }else
        if(name.contains(_setDisjoint)){
            SetVariable s1 = getSetVar(vts[0]);
            SetVariable s2 = getSetVar(vts[1]);
            c = setDisjoint(s1, s2);
        }else
        if(name.contains(_elementBool)){
            IntegerVariable index = getIntVar(vts[0]);
            IntegerVariable[] varArray = getIntVars(vts[1]);
            IntegerVariable val = getIntVar(vts[2]);
            c = nth(index, varArray, val);
        }else
        if(name.contains(_elementInt)){
            IntegerVariable index = getIntVar(vts[0]);
            IntegerVariable[] varArray = getIntVars(vts[1]);
            IntegerVariable val = getIntVar(vts[2]);
            c = nth(index, varArray, val);
        }else
        if(name.contains(_globalCardinalityLowUp)){
            IntegerVariable[] vars = getIntVars(vts[0]);
            IntegerVariable[] cards = getIntVars(vts[1]);
            c = globalCardinality(vars, cards);
        }else
        if(name.contains(_globalCardinality)){
            IntegerVariable[] vars = getIntVars(vts[0]);
            //int[] covers = getInts(vts[1]);
            int[] lbound = getInts(vts[2]);
            int[] ubound = getInts(vts[3]);
            c = globalCardinality(vars, lbound, ubound);
        }else
        if(name.contains(_inverseSet)){
            IntegerVariable[] ivars = getIntVars(vts[0]);
            SetVariable[] svars = getSetVars(vts[1]);
            c = inverseSet(ivars, svars);
        }else
        if(name.contains(_lexEq)){
            IntegerVariable[] xs = getIntVars(vts[0]);
            IntegerVariable[] ys = getIntVars(vts[1]);
            c = lexeq(xs, ys);
        }else
        if(name.contains(_lex)){
            IntegerVariable[] xs = getIntVars(vts[0]);
            IntegerVariable[] ys = getIntVars(vts[1]);
            c = lex(xs, ys);
        }else
        if(name.contains(_max)){
            IntegerVariable[] xs = getIntVars(vts[0]);
            IntegerVariable max = getIntVar(vts[1]);
            c = max(xs, max);
        }else
        if(name.contains(_member)){
            IntegerVariable ivar = getIntVar(vts[0]);
            SetVariable svar = getSetVar(vts[1]);
            c = member(ivar, svar);
        }else
        if(name.contains(_min)){
            IntegerVariable[] xs = getIntVars(vts[0]);
            IntegerVariable min = getIntVar(vts[1]);
            c = min(xs, min);
        }else
        if(name.contains(_sorting)){
            IntegerVariable[] xs = getIntVars(vts[0]);
            IntegerVariable[] ys = getIntVars(vts[1]);
            c = sorting(xs, ys);
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
        LOGGER.severe("buildGlob::ERROR:: unknown type :" + name);
        System.exit(-1);
    }

}
