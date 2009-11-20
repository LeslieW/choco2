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
package choco.kernel.model.constraints;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 18 mars 2008
 * Time: 09:30:58
 * Define every type of constraint that exist in Choco API.
 */
public enum ConstraintType {
    ABS("abs", "constraint.abs"),
    ALLDIFFERENT("allDifferent", "constraint.allDifferent"),
    AND("and", "constraint.and"),
    AROUND("around"),
    ATMOSTNVALUE("atMostNValue", "constraint.atMostNValue"),
    //BATCH("batchresource", "constraint.batchresource"),
    CHANNELING("channeling", "constraint.channeling"),
    CLAUSES("clauses", "constraint.clauses"),
    CST("cst"),
    COSTREGULAR("costregular", "constraint.costregular"),
    CUMULATIVE("cumulative", "constraint.cumulative"),
    DISJOINT("disjoint", "constraint.disjoint"),
    DISJUNCTIVE("disjunctive", "constraint.disjunctive"),
    DISTANCE("distance", "constraint.distance"),
    EQ("eq", "constraint.eq"),
    EUCLIDEANDIVISION("div", "constraint.div"),
    EXPRESSION("expression"),
    FALSE("false", "constraint.false"),
    FASTREGULAR("fastregular", "constraint.fastregular"),
    GEOST("geost", "constraint.geost"),
    GEQ("geq", "constraint.geq"),
    GLOBALCARDINALITY("globalCardinaly", "constraint.globalCardinaly"),
    GLOBALCARDINALITYMAX("globalCardinaly max", "constraint.globalCardinaly"),
    GLOBALCARDINALITYVAR("globalCardinaly var", "constraint.globalCardinaly"),
    GT("gt", "constraint.gt"),
    IFONLYIF("ifonlyif", "constraint.ifonlyif"),
    IFTHENELSE("ifthenelse", "constraint.ifthenelse"),
    IMPLIES("implies", "constraint.implies"),
    INVERSECHANNELING("inverse channeling", "constraint.channeling"),
    DOMAIN_CHANNELING("domain channeling", "constraint.channeling"),
    INVERSE_SET("inverse set", "constraint.inverseset"),
    ISINCLUDED("isIncluded", "constraint.isIncluded"),
    ISNOTINCLUDED("isNotIncluded", "constraint.isNotIncluded"),
    LEQ("leq", "constraint.leq"),
    LEX("lex", "constraint.lex"),
    LEXEQ("lexeq", "constraint.lex"),
    LEXCHAIN("lexChain", "constraint.lexChain"),
    LEXIMIN("leximin", "constraint.leximin"),
    LT("lt", "constraint.lt"),
    MAX("max", "constraint.max"),
    MEMBER("member", "constraint.member"),
    MIN("min", "constraint.min"),
    MOD("mod", "constraint.mod"),
    MULTICOSTREGULAR("multicostregular", "constraint.multicostregular"),
    NEQ("neq", "constraint.neq"),
    NONE("none"),
    NOT("not", "constraint.not"),
    NOTMEMBER("notMember", "constraint.notMember"),
    NTH("nth", "constraint.nth"),
    OCCURRENCE("occurence", "constraint.occurence"),
    OR("or", "constraint.or"),
    PACK("binpacking1D", "constraint.binpacking1D"),
    //PERT("pertprecedence", "constraint.pertprecedence"),
    PRECEDENCE_REIFIED("precedence reified", "constraint.precedencereified"),
    PRECEDENCE_IMPLIED("precedence implied", "constraint.precedenceimplied"),
    PRECEDENCE_DISJOINT("precedence disjoint", "constraint.precedencedisjoint"),
    REGULAR("regular", "constraint.regular"),
    REIFIEDAND("reifiedAnd", "constraint.reifiedAnd"),
    REIFIEDIMPLICATION("reifiedImplication", "constraint.reifiedImplication"),
    REIFIEDINTCONSTRAINT("reifiedintconstraint", "constraint.reifiedintconstraint"),
    REIFIEDOR("reifiedOr", "constraint.reifiedOr"),
    REIFIEDXNOR("reifiedXnor", "constraint.reifiedXnor"),
    REIFIEDXOR("reifiedXor", "constraint.reifiedXor"),
    SETDISJOINT("setDisjoint", "constraint.setDisjoint"),
    SETINTER("setInter", "constraint.setInter"),
    SETUNION("union", "constraint.union"),
    SIGNOP("signop", "constraint.signop"),
    SORTING("sorting", "constraint.sorting"),
    STRETCHPATH("stretchPath", "constraint.stretchPath"),
    TABLE("table", "constraint.table"),
    METATASKCONSTRAINT("Meta Task Constraint","constraint.metaTaskConstraint"),
    TIMES("times", "constraint.times"),
    TREE("tree", "constraint.tree"),
    TRUE("true", "constraint.true"),
    XNOR("xnor", "constraint.xnor"),
    XOR("xor", "constraint.xor"),
    ;

    public final String name;
    public final String property;

    ConstraintType(String name, String property) {
        this.property = property;
        this.name = name;
    }

    ConstraintType(String name) {
        this(name, "");
    }
}
