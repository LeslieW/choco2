/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  �(..)  |                           *
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
package parser.flatzinc.ast;

import choco.Choco;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import parser.flatzinc.ast.declaration.*;
import parser.flatzinc.ast.expression.EAnnotation;
import parser.flatzinc.ast.expression.EArray;
import parser.flatzinc.ast.expression.Expression;

import java.util.List;
import java.util.logging.Logger;

import static parser.flatzinc.parser.FZNParser.map;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 11 janv. 2010
* Since : Choco 2.1.1
*
* Class for Variable definition based on flatzinc-like objects.
* A variable is defined like:
* </br> type : identifier annotations [= expression]
*
*/

public final class PVariable extends ParVar{

    static Logger LOGGER = ChocoLogging.getParserLogger();

    public static final String OUTPUT = "output_";

    public PVariable(Declaration type, String identifier, List<EAnnotation> annotations, Expression expression) {
        try {
            // value is always null, except for ARRAY, it can be defined
            // see Flatzinc specifications for more informations.
            switch (type.typeOf) {
                case INT1:
                    buildWithInt(identifier);
                    break;
                case INT2:
                    buildWithInt2(identifier, (DInt2) type);
                    return;
                case INTN:
                    buildWithManyInt(identifier, (DManyInt) type);
                    return;
                case BOOL:
                    buildWithBool(identifier);
                    return;
                case SET:
                    buildWithSet(identifier, (DSet) type);
                    return;
                case ARRAY:
                    if (expression == null) {
                        buildWithDArray(identifier, (DArray) type);
                        return;
                    } else {
                        buildWithDArray(identifier, (DArray) type, (EArray) expression);
                        return;
                    }
            }
        } finally {
            readAnnotations(annotations);
        }
    }

    private static void readAnnotations(List<EAnnotation> annotations) {
        //TODO: manage annotations
        }

    /**
     * Build a {@link IntegerVariable} named {@code name}.
     *
     * @param name name of the boolean variable
     * @return {@link IntegerVariable}
     */
    private static IntegerVariable buildWithBool(String name) {
        final IntegerVariable bi = Choco.makeBooleanVar(name);
        map.put(name, bi);
        return bi;
    }

    /**
     * Build an unbounded {@link IntegerVariable} named {@code name}, defined by {@code type}.
     *
     * @param name name of the variable
     * @return {@link IntegerVariable}
     */
    private static IntegerVariable buildWithInt(String name) {
        final IntegerVariable iv = Choco.makeIntVar(name);
        map.put(name, iv);
        return iv;
    }

    /**
     * Build a {@link IntegerVariable} named {@code name}, defined by {@code type}.
     *
     * @param name name of the variable
     * @param type {@link DInt2} object
     * @return {@link IntegerVariable}
     */
    private static IntegerVariable buildWithInt2(String name, DInt2 type) {
        final IntegerVariable iv = Choco.makeIntVar(name, type.getLow(), type.getUpp());
        map.put(name, iv);
        return iv;
    }

    /**
     * Build a {@link IntegerVariable} named {@code name}, defined by {@code type}.
     * {@code type} is expected to be a {@link parser.flatzinc.ast.declaration.DManyInt} object.
     *
     * @param name name of the variable
     * @param type {@link parser.flatzinc.ast.declaration.DManyInt} object.
     * @return {@link IntegerVariable}
     */
    private static IntegerVariable buildWithManyInt(String name, DManyInt type) {
        final IntegerVariable iv = Choco.makeIntVar(name, type.getValues());
        map.put(name, iv);
        return iv;
    }


    /**
     * Build a {@link SetVariable} named {@code name}, defined by {@code type}.
     *
     * @param name name of the variable
     * @param type {@link DSet} object.
     * @return {@link SetVariable}.
     */
    private static SetVariable buildWithSet(String name, DSet type) {
        final Declaration what = type.getWhat();
        final SetVariable sv;
        switch (what.typeOf) {
            case INT1:
                LOGGER.severe("PVariable#buildWithSet INT1: unknown constructor for " + name);
                System.exit(-1);
                break;
            case INT2:
                DInt2 bounds = (DInt2) what;
                sv = Choco.makeSetVar(name, bounds.getLow(), bounds.getUpp());
                map.put(name, sv);
                return sv;
            case INTN:
                DManyInt values = (DManyInt) what;
                sv = Choco.makeSetVar(name, values.getValues());
                map.put(name, sv);
                return sv;
        }
        return null;
    }

    /**
     * Build an array of <? extends {@link choco.kernel.model.variables.Variable}>.
     * </br>WARNING: array's indice are from 1 to n.
     *
     * @param name name of the array of variables.</br> Each variable is named like {@code name}_i.
     * @param type {@link DArray} object.
     */
    private static void buildWithDArray(String name, DArray type) {
        final DInt2 index = (DInt2) type.getIndex();
        // no need to get lowB, it is always 1 (see specification of FZN for more informations)
        final int size = index.getUpp();
        final Declaration what = type.getWhat();
        final IntegerVariable[] vs;
        switch (what.typeOf) {
            case BOOL:
                vs = new IntegerVariable[size];
                for (int i = 1; i <= size; i++) {
                    vs[i - 1] = buildWithBool(name + "_" + i);
                }
                map.put(name, vs);
                break;
            case INT1:
                vs = new IntegerVariable[size];
                for (int i = 1; i <= size; i++) {
                    vs[i - 1] = buildWithInt(name + "_" + i);
                }
                map.put(name, vs);
                break;
            case INT2:
                vs = new IntegerVariable[size];
                for (int i = 1; i <= size; i++) {
                    vs[i - 1] = buildWithInt2(name + "_" + i, (DInt2) what);
                }
                map.put(name, vs);
                break;
            case INTN:
                vs = new IntegerVariable[size];
                for (int i = 1; i <= size; i++) {
                    vs[i - 1] = buildWithManyInt(name + "_" + i, (DManyInt) what);
                }
                map.put(name, vs);
                break;
            case SET:
                final SetVariable[] svs = new SetVariable[size];
                for (int i = 1; i <= size; i++) {
                    svs[i - 1] = buildWithSet(name + "_" + i, (DSet) what);
                }
                map.put(name, svs);
                break;
            default:
                break;
        }

    }

    /**
     * Build an array of <? extends {@link choco.kernel.model.variables.Variable}>.
     * </br>WARNING: array's indice are from 1 to n.
     *
     * @param name name of the array of variables.</br> Each variable is named like {@code name}_i.
     * @param type {@link DArray} object.
     * @param earr array of {@link Expression}
     */
    private static void buildWithDArray(String name, DArray type, EArray earr) {
        final DInt2 index = (DInt2) type.getIndex();
        // no need to get lowB, it is always 1 (see specification of FZN for more informations)
        final int size = index.getUpp();
        final Declaration what = type.getWhat();

        switch (what.typeOf) {
            case BOOL:
            case INT1:
            case INT2:
            case INTN:
                final IntegerVariable[] vs = new IntegerVariable[size];
                for (int i = 0; i < size; i++) {
                    vs[i] = earr.getWhat_i(i).intVarValue();
                }
                map.put(name, vs);
                break;
            case SET:
                final SetVariable[] svs = new SetVariable[size];
                for (int i = 0; i < size; i++) {
                    svs[i] = earr.getWhat_i(i).setVarValue();
                }
                map.put(name, svs);
                break;
            default:
                break;
        }
    }
}
