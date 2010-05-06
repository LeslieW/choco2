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
package parser.flatzinc.ast.expression;

import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

import java.util.List;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 11 janv. 2010
* Since : Choco 2.1.1
*
* Class for array expressions based on flatzinc-like objects.
*/
public final class EArray extends Expression{

    public final List<Expression> what;

    public EArray(List<Expression> what) {
        super(EType.ARR);
        this.what = what;
    }

    public Expression getWhat_i(int i) {
        return what.get(i);
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder("[");
        st.append(what.get(0).toString());
        for(int i = 1; i < what.size(); i++){
            st.append(',').append(what.get(i).toString());
        }
        return st.append(']').toString();
    }

    @Override
    public int intValue() {
        exit();
        return 0;
    }

    @Override
    public int[] toIntArray() {
        int[] arr = new int[what.size()];
        for(int i = 0; i < what.size(); i++){
            arr[i] = what.get(i).intValue();
        }
        return arr;
    }

    @Override
    public IntegerVariable intVarValue() {
        exit();
        return null;
    }

    @Override
    public IntegerVariable[] toIntVarArray() {
        IntegerVariable[] arr = new IntegerVariable[what.size()];
        for(int i = 0; i < what.size(); i++){
            arr[i] = what.get(i).intVarValue();
        }
        return arr;
    }

    @Override
    public SetVariable setVarValue() {
        exit();
        return null;
    }

    @Override
    public SetVariable[] toSetVarArray() {
        SetVariable[] arr = new SetVariable[what.size()];
        for(int i = 0; i < what.size(); i++){
            arr[i] = what.get(i).setVarValue();
        }
        return arr;
    }
}
