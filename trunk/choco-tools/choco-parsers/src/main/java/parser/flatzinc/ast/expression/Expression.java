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
package parser.flatzinc.ast.expression;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

import java.util.logging.Logger;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 8 janv. 2010
* Since : Choco 2.1.1
*
* Class for expression definition based on flatzinc-like objects.
*/
public abstract class Expression {

    static final Logger LOGGER = ChocoLogging.getMainLogger();

    public enum EType{
        ANN, ARR, BOO, IDA, IDE, INT, SET_B, SET_L, STR 
    }

    protected static final Class int_arr = new int[0].getClass();
    protected static final Class bool_arr = new boolean[0].getClass();

    final EType typeOf;

    protected Expression(EType typeOf) {
        this.typeOf = typeOf;
    }

    public final EType getTypeOf() {
        return typeOf;
    }

    static final void exit(){
        LOGGER.severe("Expression  unexpected call");
        new Exception().printStackTrace();
        throw new UnsupportedOperationException();
    }

    /**
     * Get the int value of the {@link Expression}
     * @return int
     */
    public abstract int intValue();

    /**
     * Get array of int of the {@link Expression}
     * @return int[]
     */
    public abstract int[] toIntArray();

    /**
     * Get the {@link IntegerVariable} of the {@link Expression}
     * @return {@link IntegerVariable} or {@link choco.kernel.model.variables.integer.IntegerConstantVariable}
     */
    public abstract IntegerVariable intVarValue();

    /**
     * Get an array of {@link IntegerVariable} of the {@link Expression}
     * @return {{@link IntegerVariable},{@link choco.kernel.model.variables.integer.IntegerConstantVariable}}[]
     */
    public abstract IntegerVariable[] toIntVarArray();

    /**
     * Get the {@link SetVariable} of the {@link Expression}
     * @return {@link SetVariable} or {@link choco.kernel.model.variables.set.SetConstantVariable}
     */
    public abstract SetVariable setVarValue();

    /**
     * Get an array of {@link SetVariable} of the {@link Expression}
     * @return {{@link SetVariable},{@link choco.kernel.model.variables.set.SetConstantVariable}}[]
     */
    public abstract SetVariable[] toSetVarArray();

}
