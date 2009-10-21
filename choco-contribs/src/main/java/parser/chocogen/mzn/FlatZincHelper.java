package parser.chocogen.mzn;/* ************************************************
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

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 13 oct. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/

import choco.Choco;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.model.variables.set.SetVariable;

import java.util.HashMap;
import java.util.logging.Logger;


public class FlatZincHelper {
    static HashMap<String, Object> memory = new HashMap<String, Object>();
    static Logger LOGGER = ChocoLogging.getParserLogger();

    static final String NAME_SEPARATOR = "_";

    public static void init(HashMap<String, Object> memory) {
        FlatZincHelper.memory = memory;
        memory.clear();
    }

    public enum EnumVar {
        iInt, iBounds, iValues, bBool, fFloat, fBounds, setOf
    }

    public static class VarType {
        public final EnumVar type;
        public final Object obj;

        public VarType(EnumVar type, Object obj) {
            this.type = type;
            this.obj = obj;
        }

    }

    public static VarType build(EnumVar type, Object obj) {
        return new VarType(type, obj);
    }

    public enum EnumVal {
        iInt, bBool, fFloat, sString, array, interval
    }

    public static class ValType {
        public final EnumVal type;
        public final Object obj;

        protected ValType(EnumVal type, Object obj) {
            this.type = type;
            this.obj = obj;
        }
    }

    public static ValType build(EnumVal type, Object obj) {
        return new ValType(type, obj);
    }


    public static class ArrayDecl {
        public final VarType var;
        public final String name;
        public final ValType val;
        public final boolean isParameter;

        protected ArrayDecl(VarType var, String name, ValType val, boolean isParameter) {
            this.var = var;
            this.name = name;
            this.val = val;
            this.isParameter = isParameter;
        }
    }

    public static ArrayDecl build(VarType var, String name, ValType val, boolean isParamater) {
        return new ArrayDecl(var, name, val, isParamater);
    }

    /**
     * Build a variable
     *
     * @param natet non array ti expression tail
     * @param name  name (+ annotations)
     * @param nafe  non array flat expr (can be null)
     * @return an Variable object (can be a constant)
     */
    public static void buildVar(VarType natet, String name, ValType nafe) {

        if (nafe == null) {
            // Build a simple variable
            switch (natet.type) {
                case iInt:
                    LOGGER.severe("buildVar::iInt:: ERROR");
                    System.exit(-1);
                    break;
                case bBool:
                    IntegerVariable ib1 = Choco.makeBooleanVar(name);
                    memory.put(name, ib1);
                    return;
                case iBounds:
                    int[] bounds1 = (int[]) natet.obj;
                    IntegerVariable iv1 = Choco.makeIntVar(name, bounds1[0], bounds1[1]);
                    memory.put(name, iv1);
                    return;
                case iValues:
                    int[] values1 = (int[]) natet.obj;
                    IntegerVariable iv2 = Choco.makeIntVar(name, values1);
                    memory.put(name, iv2);
                    return;
                case setOf:
                    VarType vt = (VarType) natet.obj;
                    switch (vt.type) {
                        case iValues:
                            int[] values2 = (int[]) natet.obj;
                            SetVariable sv1 = Choco.makeSetVar(name, values2);
                            memory.put(name, sv1);
                            return;
                        case iBounds:
                            int[] bounds2 = (int[]) natet.obj;
                            SetVariable sv2 = Choco.makeSetVar(name, bounds2[0], bounds2[1]);
                            memory.put(name, sv2);
                            return;
                        default:
                            LOGGER.severe("buildVar::setOf::ERROR:: Unknown type");
                            System.exit(-1);
                            break;
                    }
                    break;
                case fFloat:
                    LOGGER.severe("buildVar::fFloat:: ERROR");
                    System.exit(-1);
                    break;
                case fBounds:
                    double[] bounds3 = (double[]) natet.obj;
                    RealVariable rv1 = Choco.makeRealVar(name, bounds3[0], bounds3[1]);
                    memory.put(name, rv1);
                    return;
            }
        } else {
            // more complicated case...
            switch (nafe.type) {
                case sString:
                    memory.put(name, memory.get(nafe.obj));
                    return;
                default:
                    LOGGER.severe("buildVar::array::ERROR:: Unknown type");
                    System.exit(-1);
                    break;

            }
        }
        LOGGER.severe("buildVar:: ERROR");
        System.exit(-1);
    }

    /**
     * Build a parameter
     *
     * @param natet non array ti expression tail
     * @param name  name (+ annotations)
     * @param nafe  non array flat expr
     * @return an Variable object (can be a constant)
     */
    public static void buildPar(VarType natet, String name, ValType nafe) {

        if (nafe != null) {
            // Build a simple variable
            switch (natet.type) {
                case iInt:
                    if (nafe.type.equals(EnumVal.iInt)) {
                        Integer i = (Integer) nafe.obj;
                        memory.put(name, i);
                        return;
                    }
                    LOGGER.severe("buildInt::iInt:: ERROR");
                    System.exit(-1);
                    break;
                case bBool:
                    if (nafe.type.equals(EnumVal.bBool)) {
                        Boolean b = (Boolean) nafe.obj;
                        memory.put(name, b);
                        return;
                    }
                    LOGGER.severe("buildInt::bBool:: ERROR");
                    System.exit(-1);
                    break;
                case setOf:
                    VarType vt = (VarType) natet.obj;
                    switch (vt.type) {
                        case iInt:
                            switch (nafe.type) {
                                case interval:
                                    ValType[] i = (ValType[]) nafe.obj;
                                    int f = Integer.valueOf((String) i[0].obj);
                                    int t = Integer.valueOf((String) i[1].obj);
                                    int[] j = new int[t - f + 1];
                                    for (int k = 0; k <= t - f; k++) {
                                        j[k] = f + k;
                                    }
                                    SetVariable sv3 = Choco.constant(j);
                                    memory.put(name, sv3);
                                    return;
                                default:
                                    LOGGER.severe("buildVar::setOf::iInt::ERROR:: Unknown type");
                                    System.exit(-1);
                                    break;

                            }
                        case iValues:
                            int[] values2 = (int[]) nafe.obj;
                            SetConstantVariable sv1 = Choco.constant(values2);
                            memory.put(name, sv1);
                            return;
                        case iBounds:
                            int[] bounds2 = (int[]) nafe.obj;
                            SetVariable sv2 = Choco.constant(bounds2);
                            memory.put(name, sv2);
                            return;
                        default:
                            LOGGER.severe("buildVar::setOf::ERROR:: Unknown type");
                            System.exit(-1);
                            break;
                    }
                    break;
                case fFloat:
                    if (nafe.type.equals(EnumVal.fFloat)) {
                        Double d = (Double) nafe.obj;
                        memory.put(name, d);
                        return;
                    }
                    LOGGER.severe("buildVar::fFloat:: ERROR");
                    System.exit(-1);
                    break;
                case fBounds:
                case iBounds:
                case iValues:
                    LOGGER.severe("buildInt:: ERROR");
                    System.exit(-1);
                    break;
            }
        } else {
            // more complicated case...
        }
        LOGGER.severe("buildVar:: ERROR");
        System.exit(-1);
    }


    /**
     * Build each variable/parameter of an array, and put each value in the memory hashmap (not whole array)
     *
     * @param from from index
     * @param to   to index
     * @param adt  array declaration tail
     */
    public static void buildArray(int from, int to, ArrayDecl adt) {
        if (adt.isParameter) {
            for (int i = from; i <= to; i++) {
                buildPar(adt.var, adt.name + NAME_SEPARATOR + i, adt.val);
            }
            return;
        } else {
            if(adt.val==null){
                for (int i = from; i <= to; i++) {
                    buildVar(adt.var, adt.name + NAME_SEPARATOR + i, adt.val);
                }
                return;
            }else{
                switch (adt.val.type){
                    case array:
                        ValType[] objects = (ValType[])adt.val.obj;
                        for (int i = from; i <= to; i++) {
                            buildVar(adt.var, adt.name + NAME_SEPARATOR + i, objects[i-from]);
                        }
                        return;
                    default:
                        for (int i = from; i <= to; i++) {
                            buildVar(adt.var, adt.name + NAME_SEPARATOR + i, adt.val);
                        }
                        return;
                }
            }
        }
    }
}
