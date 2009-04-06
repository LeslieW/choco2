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
package choco.kernel.model.variables;

/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public enum VariableType {
    CONSTANT_INTEGER("constant_integer", "variable.constantinteger"),
    CONSTANT_DOUBLE("constant_double", "variable.constantdouble"),
    CONSTANT_SET("constant_set","variable.constantset"),

    INTEGER("integer", "variable.integer"),
    INTEGER_EXPRESSION("integer expression", "variable.integerexpression"),

    MULTIPLE_VARIABLES("mutliple variables"),

    NONE("none"),

    REAL("real", "variable.real"),
    REAL_EXPRESSION("real expression"),

    SET("set", "variable.set"),
    SET_EXPRESSION("set expression"),

    TASK("scheduling task", "variable.task"),
    ;

    public final String name;
    public final String property;

    VariableType(String name, String property) {
        this.name=name;
        this.property = property;
    }

    VariableType(String name) {
        this(name, "");
    }


}
