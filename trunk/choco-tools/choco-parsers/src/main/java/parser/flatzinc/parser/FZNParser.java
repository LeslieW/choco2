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
package parser.flatzinc.parser;

import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map4;
import org.codehaus.jparsec.misc.Mapper;
import parser.flatzinc.ast.*;
import parser.flatzinc.ast.declaration.*;
import parser.flatzinc.ast.expression.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
* 
*/

public final class FZNParser {

    public static Model model;
    public static Solver solver;
    /**
     * Map to find an object with its name.
     */
    public static Map<String, Object> map;

    /**
     * Intialisation of internal data structures.
     */
    public static void init() {
        model = new CPModel();
        solver = new PreProcessCPSolver();
        map = new HashMap<String, Object>();
    }


    /**
     * Scanners for "true" or "false" keyword.
     * Create a {@link parser.flatzinc.ast.expression.EBool} object.
     */
    final static Parser<EBool> BOOL_CONST = Parsers.or(
            TerminalParser.term("true").retn(EBool.instanceTrue),
            TerminalParser.term("false").retn(EBool.instanceFalse));

    /**
     * Scanner for int_const.
     * Create a {@link parser.flatzinc.ast.expression.EInt} object.
     */
    final static Parser<EInt> INT_CONST =
            Mapper.curry(EInt.class).sequence(Parsers.or(TerminalParser.term("+"), TerminalParser.term("-")).optional().source(),
                    TerminalParser.NUMBER);

    /**
     * Scanner for bounded set declaration, like 1..6.
     * Create {@link parser.flatzinc.ast.expression.ESetBounds} object.
     */
    final static Parser<ESetBounds> SET_CONST_1 =
            Mapper.curry(ESetBounds.class).sequence(INT_CONST, TerminalParser.term(".."), INT_CONST);

    /**
     * Scanner for listed set declaration, like {1,6,7}.
     * Create {@link parser.flatzinc.ast.expression.ESetList} object.
     */
    final static Parser<ESetList> SET_CONST_2 =
            Mapper.curry(ESetList.class).sequence(TerminalParser.term("{"), INT_CONST.sepBy(TerminalParser.term(",")),
                    TerminalParser.term("}"));
    /**
     * Scanner for sequence like "id[1]".
     */
    final static Parser<EIdArray> ID_ARRAY =
            Mapper.curry(EIdArray.class).sequence(TerminalParser.IDENTIFIER, TerminalParser.term("["), INT_CONST, TerminalParser.term("]"));


    /**
     * Scanner for common array declaration : '( {@code expr},...)'.
     * @param expr parser to apply
     * @param <T> expected return object
     * @return {@link Parser} of {@link EArray}
     */
    static <T> Parser<EArray> array(Parser<T> expr) {
        return Mapper.curry(EArray.class).sequence(TerminalParser.term("["), expr.sepBy(TerminalParser.term(",")), TerminalParser.term("]"));
    }

    /**
     * Scanner for expression surrounded by parenthesis.
     *
     * @param parser Scanner for expression
     * @param <T>    expected type
     * @return {@link Parser<T>}
     */
    static <T> Parser<T> paren(Parser<T> parser) {
        return parser.between(TerminalParser.term("("), TerminalParser.term(")"));
    }

    /**
     * Scanner for a list of {@link T} separeted by comma.
     *
     * @param parser Scanner for expression
     * @param <T>    expected type
     * @return List of {@link T}
     */
    final static <T> Parser<List<T>> list(Parser<T> parser) {
        return paren(parser.sepBy(TerminalParser.term(",")));
    }


    /**
     * Scanner for expression.
     * @return {@link Parser} of {@link Expression}
     */
    final static Parser<Expression> expression() {
        Parser.Reference<Expression> ref = Parser.newReference();
        Parser<Expression> lazy = ref.lazy();
        Parser<Expression> parser = Parsers.or(
                // set_const
                Parsers.or(SET_CONST_1, SET_CONST_2),
                // bool_const
                BOOL_CONST,
                // int_const
                INT_CONST,
                // identifier[int_const]
                Mapper.curry(EIdArray.class).sequence(TerminalParser.IDENTIFIER, TerminalParser.term("["), INT_CONST, TerminalParser.term("]")),
                // annotation
                Mapper.curry(EAnnotation.class).sequence(
                        Mapper.curry(EIdentifier.class).sequence(TerminalParser.IDENTIFIER),
                        list(Parsers.or(lazy))
                ),
                // identifier
                Mapper.curry(EIdentifier.class).sequence(TerminalParser.IDENTIFIER),
                // [] | [expr,...]
                array(lazy),
                // "...string constant..."
                Mapper.curry(EString.class).sequence(Scanners.DOUBLE_QUOTE_STRING)
        );
        ref.set(parser);
        return parser;
    }


    /**
     * Scanner for "int" keyword.
     * Create {@link parser.flatzinc.ast.declaration.DInt} object.
     */
    final static Parser<DBool> BOOL = Mapper.curry(DBool.class).sequence(TerminalParser.term("var").succeeds(), TerminalParser.term("bool"));


    /**
     * Scanner for "int" keyword.
     * Create {@link parser.flatzinc.ast.declaration.DInt} object.
     */
    final static Parser<DInt> INT =
            Mapper.curry(DInt.class).sequence(TerminalParser.term("var").succeeds(), TerminalParser.term("int"));

    /**
     * Scanner for bounds of int declaration, like 1..3.
     * Create a {@link parser.flatzinc.ast.declaration.DInt2} object.
     */
    final static Parser<DInt2> INT2 =
            Mapper.curry(DInt2.class).sequence(
                    TerminalParser.term("var").succeeds(), INT_CONST, TerminalParser.term(".."), INT_CONST
            );


    /**
     * Scanner for list of int declaration, like {1, 5, 8}.
     * Create a {@link parser.flatzinc.ast.declaration.DManyInt} object.
     */
    final static Parser<DManyInt> MANY_INT =
            Mapper.curry(DManyInt.class).sequence(
                    TerminalParser.term("var").succeeds(), TerminalParser.term("{"), INT_CONST.sepBy(TerminalParser.term(",")), TerminalParser.term("}")
            );

    /**
     * Scanners for every int-like expression.
     * Create a {@link parser.flatzinc.ast.declaration.Declaration} object
     */
    private final static Parser<Declaration> INTS = Parsers.or(INT, INT2, MANY_INT);

    /**
     * Scanner for every primitive-like expression.
     * Create a {@link Declaration} object.
     */
    private final static Parser<Declaration> PRIMITIVES = Parsers.or(BOOL, INTS);

    /**
     * Scanner for a set of int, like "set of int", "set of 1..3" or "set of {1,2,3}".
     * Create a {@link DSet} object.
     */
    final static Parser<DSet> SET_OF_INT =
            Mapper.curry(DSet.class).sequence(
                    TerminalParser.term("var").succeeds(), TerminalParser.phrase("set of"),
                    INTS
            );

    /**
     * Scanner for array of smth, like "array [int] of bool".
     * Creat a {@link DArray} object
     */
    final static Parser<DArray> ARRAY_OF =
            Mapper.curry(DArray.class).sequence(
                    TerminalParser.phrase("array ["), Parsers.or(INT, INT2), TerminalParser.phrase("] of"),
                    Parsers.or(PRIMITIVES, SET_OF_INT)
            );

    /**
     * Scanner for parameter types or variables types.
     * See FZN specifications for more informations.
     * Create {@link Declaration} object
     */
    final static Parser<Declaration> TYPE =
            Parsers.or(PRIMITIVES, SET_OF_INT, ARRAY_OF);

    /**
     * Scanner for multiples annotations.
     * Create a {@link List} of {@link EAnnotation}.
     */
    final static Parser<List<Expression>> ANNOTATIONS =
            Parsers.sequence(TerminalParser.term("::"), expression()).many();

    /**
     * Scanner for predicate parameters.
     * Create a {@link PredParam} object.
     */
    final static Parser<PredParam> PRED_PARAM =
            Mapper.curry(PredParam.class).sequence(TYPE, TerminalParser.term(":"), TerminalParser.IDENTIFIER);


    /**
     * Scanner for predicate declaration
     * Create a {@link parser.flatzinc.ast.Predicate} object
     */
    final static Parser<Predicate> PRED_DECL =
            Mapper.curry(Predicate.class).sequence(TerminalParser.term("predicate"), TerminalParser.IDENTIFIER, list(PRED_PARAM), TerminalParser.term(";"));


    /**
     * Mapper for Parameter or PVariable declaration
     */
    private static final Map4 PARVAR = new Map4<Declaration, String, List<EAnnotation>, Expression, ParVar>() {

        @Override
        public ParVar map(Declaration type, String id, List<EAnnotation> annotations, Expression expression) {
            if (type.isVar) {
                return new PVariable(type, id, annotations, expression);
            } else {
                return new Parameter(type, id, expression);
            }
        }

        @Override
        public String toString() {
            return "parvar sequence";
        }
    };

    /**
     * Scanner for variable declaration.
     * Create {@link ?} object
     */
    @SuppressWarnings({"unchecked"})
    public final static Parser<ParVar> PAR_VAR_DECL =
            Parsers.sequence(TYPE.followedBy(TerminalParser.term(":")), TerminalParser.IDENTIFIER, ANNOTATIONS,
                    Parsers.sequence(TerminalParser.term("="), expression()).optional(), PARVAR).followedBy(TerminalParser.term(";"));

    /**
     * Scanner for constraint declaration.
     * Create a {@link parser.flatzinc.ast.PConstraint} object.
     */
    public final static Parser<PConstraint> CONSTRAINT =
            Mapper.curry(PConstraint.class).sequence(TerminalParser.term("constraint"), TerminalParser.IDENTIFIER,
                    list(expression()), ANNOTATIONS, TerminalParser.term(";"));


    /**
     * Scanner for satisfy declaration.
     */
    final static Parser<SatisfyGoal> SATISFY =
            Mapper.curry(SatisfyGoal.class).sequence(
                    TerminalParser.term("solve"), ANNOTATIONS, TerminalParser.term("satisfy"), TerminalParser.term(";"));

    /**
     * Scanner for optimize declaration.
     */
    final static Parser<SolveGoal> OPTIMIZE =
            Mapper.curry(SolveGoal.class).sequence(
                    TerminalParser.term("solve"), ANNOTATIONS, Parsers.or(
                            TerminalParser.term("maximize").retn(SolveGoal.Solver.MAXIMIZE),
                            TerminalParser.term("minimize").retn(SolveGoal.Solver.MINIMIZE)
                    ), expression(), TerminalParser.term(";"));
    /**
     * Scanner for solve goals declaration.
     * Create a {@link parser.flatzinc.ast.SolveGoal} object.
     */
    @SuppressWarnings({"unchecked"})
    public final static Parser<SolveGoal> SOLVE_GOAL =
            Parsers.or(SATISFY, OPTIMIZE);

    /**
     * Scanner for flatzinc model.
     *
     * @param source {@link String} to parse
     * @param solve  solve instruction
     */
    public final static void FLATZINC_MODEL(String source, boolean solve) {
        init();
        long ts = -System.currentTimeMillis();
        Parser<?> parser = Parsers.sequence(
                PRED_DECL.many(),
                PAR_VAR_DECL.many(),
                CONSTRAINT.many(),
                SOLVE_GOAL
        );
        TerminalParser.parse(parser, source);
        ts += System.currentTimeMillis();
        ChocoLogging.getParserLogger().info(MessageFormat.format("% parse time : {0}ms", ts));
        solver.setTimeLimit(10000);
        if (solve) solver.launch();
        ChocoLogging.getParserLogger().info("==========");
    }

}