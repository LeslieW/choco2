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
package parser.flatzinc;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 10 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public enum Keywords {
    Annotation("annotation"),
    Any("any"),
    Array("array"),
    Bool("bool"),
    Case("case"),
    Constraint("constraint"),
    Else("else"),
    Elseif("elseif"),
    Endif("endif"),
    Enum("enum"),
    False("false"),
    Float("float"),
    Function("function"),
    If("if"),
    Include("include"),
    Int("int"),
    Let("let"),
    Maximize("maximize"),
    Minimize("minimize"),
    Of("of"),
    Satisfy("satisfy"),
    Output("output"),
    Par("par"),
    Predicate("predicate"),
    Record("record"),
    Set("set"),
    Solve("solve"),
    String("string"),
    Test("test"),
    Then("then"),
    True("true"),
    Tuple("tuple"),
    Type("type"),
    Var("var"),
    Variant_record("variant_record"),
    Where("where"),
    ;

    final String value;

    Keywords(String value) {
        this.value = value;
    }
}