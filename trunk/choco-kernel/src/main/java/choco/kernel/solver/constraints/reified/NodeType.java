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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.solver.constraints.reified;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public enum NodeType {
    ABS,
    AND,
    CONSTANTLEAF,
    CONSTRAINTLEAF,
    DIST,
    DISTEQ,
    DISTGT,
    DISTLT,
    DISTNEQ,
    DIV,
    EQ,
    FALSE,
    GEQ,
    GT,
    IFTHENELSE,
    LEQ,
    LT,
    MAX,
    MIN,
    MINUS,
    MOD,
    MULT,
    NEG,
    NEQ,
    NOT,
    OPPSIGN,
    OR,
    PLUS,
    POW,
    SAMESIGN,
    SCALAR,
    SQUARE,
    TRUE,
    VARIABLELEAF,
    XNOR,
    XOR,
    ;
}