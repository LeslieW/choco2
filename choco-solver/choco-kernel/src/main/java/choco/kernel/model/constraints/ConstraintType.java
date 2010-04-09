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
    ABS("abs", "constraint.abs", false),
    ALLDIFFERENT("allDifferent", "constraint.allDifferent", false),
    AMONG("among", "constraint.among", false),
    AMONGSET("among_set", "constraint.amongset", false),
    AND("and", "constraint.and", true),
    AROUND("around"),
    ATMOSTNVALUE("atMostNValue", "constraint.atMostNValue", false),
    //BATCH("batchresource", "constraint.batchresource"),
    CHANNELING("channeling", "constraint.channeling", false),
    CLAUSES("clauses", "constraint.clauses", false),
    CST("cst"),
    COSTKNAPSACK("costknapsack","constraint.costknapsack", false),
    COSTREGULAR("costregular", "constraint.costregular", false),
    CUMULATIVE("cumulative", "constraint.cumulative", false),
    DISJOINT("disjoint", "constraint.disjoint", false),
    DISJUNCTIVE("disjunctive", "constraint.disjunctive", false),
    DISTANCE("distance", "constraint.distance", true),
    EQ("eq", "constraint.eq", true),
    EUCLIDEANDIVISION("div", "constraint.div", false),
    EXACTLY("exactly", "constraint.exactly", false),
    EXPRESSION("expression"),
    FALSE("false", "constraint.false", false),
    FASTCOSTREGULAR("fastcostregular", "constraint.fastcostregular", false),
    FASTREGULAR("fastregular", "constraint.fastregular", false),
    FORBIDDEN_INTERVALS("forbidden intervals", "constraint.forbiddenIntervals", false),
    GEOST("geost", "constraint.geost", false),
    GEQ("geq", "constraint.geq", true),
    GLOBALCARDINALITY("globalCardinaly", "constraint.globalCardinaly", false),
    GLOBALCARDINALITYMAX("globalCardinaly max", "constraint.globalCardinaly", false),
    GLOBALCARDINALITYVAR("globalCardinaly var", "constraint.globalCardinaly", false),
    GT("gt", "constraint.gt", true),
    IFONLYIF("ifonlyif", "constraint.ifonlyif", false),
    IFTHENELSE("ifthenelse", "constraint.ifthenelse", true),
    IMPLIES("implies", "constraint.implies", false),
    INVERSECHANNELING("inverse channeling", "constraint.channeling", false),
    INCREASINGNVALUE("increasing n value", "constraint.increasingnvalue", false),
    DOMAIN_CHANNELING("domain channeling", "constraint.channeling", false),
    INVERSE_SET("inverse set", "constraint.inverseset", false),
    ISINCLUDED("isIncluded", "constraint.isIncluded", false),
    ISNOTINCLUDED("isNotIncluded", "constraint.isNotIncluded", false),
    LEQ("leq", "constraint.leq", true),
    LEX("lex", "constraint.lex", false),
    LEXEQ("lexeq", "constraint.lex", false),
    LEXCHAIN("lexChain", "constraint.lexChain", false),
    LEXIMIN("leximin", "constraint.leximin", false),
    LT("lt", "constraint.lt", true),
    MAX("max", "constraint.max", false),
    MEMBER("member", "constraint.member", false),
    MIN("min", "constraint.min", false),
    MOD("mod", "constraint.mod", false),
    MULTICOSTREGULAR("fast_multicostregular", "constraint.multicostregular", false),
    NEQ("neq", "constraint.neq", true),
    NONE("none"),
    NOT("not", "constraint.not", true),
    NOTMEMBER("notMember", "constraint.notMember", false),
    NTH("nth", "constraint.nth", false),
    OCCURRENCE("occurence", "constraint.occurence", false),
    OR("or", "constraint.or", true),
    PACK("binpacking1D", "constraint.binpacking1D", false),
    //PERT("pertprecedence", "constraint.pertprecedence"),
    PRECEDENCE_REIFIED("precedence reified", "constraint.precedencereified", false),
    PRECEDENCE_IMPLIED("precedence implied", "constraint.precedenceimplied", false),
    PRECEDENCE_DISJOINT("precedence disjoint", "constraint.precedencedisjoint", false),
    REGULAR("regular", "constraint.regular", false),
    REIFIEDAND("reifiedAnd", "constraint.reifiedAnd", false),
    REIFIEDIMPLICATION("reifiedImplication", "constraint.reifiedImplication", false),
    REIFIEDCONSTRAINT("reifiedconstraint", "constraint.reifiedconstraint", false),
    REIFIEDOR("reifiedOr", "constraint.reifiedOr", false),
    REIFIEDXNOR("reifiedXnor", "constraint.reifiedXnor", false),
    REIFIEDXOR("reifiedXor", "constraint.reifiedXor", false),
    SETDISJOINT("setDisjoint", "constraint.setDisjoint", false),
    SETINTER("setInter", "constraint.setInter", false),
    SETUNION("union", "constraint.union", false),
    SIGNOP("signop", "constraint.signop", true),
    SORTING("sorting", "constraint.sorting", false),
    STRETCHPATH("stretchPath", "constraint.stretchPath", false),
    TABLE("table", "constraint.table", false),
    METATASKCONSTRAINT("Meta Task Constraint","constraint.metaTaskConstraint", false),
    TIMES("times", "constraint.times", false),
    TREE("tree", "constraint.tree", false),
    TRUE("true", "constraint.true", false),
   // USE_RESOURCES("useResources", "constraint.useResources", false),
    XNOR("xnor", "constraint.xnor", true),
    XOR("xor", "constraint.xor", true);

    public final String name;
    public final String property;
    public final boolean canContainExpression;


ConstraintType(String name, String property, boolean canContainExpression) {
        this.property = property;
        this.name = name;
        this.canContainExpression = canContainExpression;
    }

    ConstraintType(String name) {
        this(name, "", false);
    }

	public final String getName() {
		return name;
	}
    
    
}
