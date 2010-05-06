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
* Class for one annotation declaration based on flatsinc-like objects. 
*/
public final class EAnnotation extends Expression{
    
    public final EIdentifier id;
    public final List<Expression> exps;

    public EAnnotation(EIdentifier id, List<Expression> exps) {
        super(EType.ANN);
        this.id = id;
        this.exps = exps;
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder(id.value);
        if(exps!=null && !exps.isEmpty()){
            st.append('(').append(exps.get(0).toString());
            for(int i = 1; i < exps.size(); i++){
                st.append(',').append(exps.get(i).toString());
            }
            st.append(')');
        }
        return st.toString();
    }

    @Override
    public int intValue() {
        exit();
        return 0;
    }

    @Override
    public int[] toIntArray() {
        exit();
        return null;
    }

    @Override
    public IntegerVariable intVarValue() {
        exit();
        return null;
    }

    @Override
    public IntegerVariable[] toIntVarArray() {
        exit();
        return null;
    }

    @Override
    public SetVariable setVarValue() {
        exit();
        return null;
    }

    @Override
    public SetVariable[] toSetVarArray() {
        exit();
        return null;
    }
}
