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

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 6 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class FZNType {
    private final TType type;
    private final Object obj;
    private final boolean isVar;

    private FZNType(TType type, Object obj, boolean isVar) {
        this.type = type;
        this.obj = obj;
        this.isVar = isVar;
    }

    public static FZNType buildPar(TType type, Object obj){
        return new FZNType(type, obj, false);
    }

    public static FZNType buildVar(TType type, Object obj){
        return new FZNType(type, obj, true);
    }
}
