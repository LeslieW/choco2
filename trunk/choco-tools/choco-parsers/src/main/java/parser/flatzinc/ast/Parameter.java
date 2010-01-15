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
package parser.flatzinc.ast;

import choco.Choco;
import choco.kernel.model.variables.set.SetConstantVariable;
import parser.flatzinc.ast.declaration.DArray;
import parser.flatzinc.ast.declaration.DInt2;
import parser.flatzinc.ast.declaration.Declaration;
import parser.flatzinc.ast.expression.*;

import static parser.flatzinc.parser.FZNParser.map;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 11 janv. 2010
* Since : Choco 2.1.1
*
* Parameter construction with flatzinc-like object in parameter.
* A Parameter is defined like :
* </br> 'type : identifier = expression'
*/
public final class Parameter extends ParVar{

    public Parameter(Declaration type, String identifier, Expression expression) {
        switch (type.typeOf) {
            case BOOL:
                buildBool(identifier, (EBool)expression);
                break;
            case INT1:
            case INT2:
            case INTN:
                buildInt(identifier, (EInt)expression);
                break;
            case SET:
                buildSet(identifier, (ESet)expression);
                break;
            case ARRAY:
                DArray arr = (DArray)type;
                DInt2 index = (DInt2) arr.getIndex();
                buildArray(identifier, index, arr.getWhat(), (EArray)expression);
                break;
        }

    }

    /**
     * Build a boolean primitive and add it to the {@code flatzinc.parser.FZNParser.map}.
     * @param name key name
     * @param value {@link EBool} storing the value
     * @return {@link boolean}
     */
    private static boolean buildBool(String name, EBool value){
        boolean b = value.value;
        map.put(name, b);
        return b;
    }

    /**
     * Build a int primitive and add it to the {@code flatzinc.parser.FZNParser.map}.
     * @param name key name
     * @param value {@link EInt} storing the value.
     * @return {@link int}
     */
    private static int buildInt(String name, EInt value){
        int i = value.value;
        map.put(name, i);
        return i;
    }

    /**
     * Build a {@link choco.kernel.model.variables.set.SetConstantVariable} object
     * and add it to the {@code flatzinc.parser.FZNParser.map}.
     * @param name key name
     * @param set {@link ESet} defining the set
     * @return {@link choco.kernel.model.variables.set.SetConstantVariable}
     */
    private static SetConstantVariable buildSet(String name, ESet set){
        final SetConstantVariable s;
        switch (set.getTypeOf()){
            case SET_B:
                ESetBounds bset = (ESetBounds)set;
                s = Choco.constant(bset.enumVal());
                map.put(name, s);
                return s;
            case SET_L:
                ESetList lset = (ESetList)set;
                s = Choco.constant(lset.enumVal());
                map.put(name, s);
                return s;
            default:
                return null;
        }
    }

    /**
     * Build an array of object and add it to the {@code flatzinc.parser.FZNParser.map}.
     * @param name key name
     * @param index size definition
     * @param what type of object
     * @param value input declaration
     */
    private static void buildArray(String name, DInt2 index, Declaration what, EArray value){
        // no need to get lowB, it is always 1 (see specification of FZN for more informations)
        int size = index.getUpp();
        switch (what.typeOf) {
            case BOOL:
                boolean[] barr = new boolean[size];
                for(int i = 0; i < size; i++){
                    barr[i] = ((EBool)value.getWhat_i(i)).value;
                }
                map.put(name, barr);
                break;
            case INT1:
            case INT2:
            case INTN:
                int[] iarr = new int[size];
                for(int i = 0; i < size; i++){
                    iarr[i] = ((EInt)value.getWhat_i(i)).value;
                }
                map.put(name, iarr);
                break;
            case SET:
                SetConstantVariable[] sarr = new SetConstantVariable[size];
                for(int i = 0; i < size; i++){
                    sarr[i] = buildSet(name+"_"+i, (ESet)value.getWhat_i(i));
                }
                map.put(name, sarr);
                break;
            case ARRAY:
                LOGGER.severe("Parameter#buildArray ARRAY: unexpected type for " + name);
                System.exit(-1);
        }
    }

}
