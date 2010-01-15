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
package parser.flatzinc.parser;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.misc.Mapper;


/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 8 janv. 2010
* Since : Choco 2.1.1
* 
*/

public final class TerminalParser {

    final static String[] OPERATORS =
            {"..", ".", "{", "}", ",", "[", "]", "=", "+", "-", ";", ":", "::", "(", ")"};

    final static String[] KEYWORDS =
            {"bool", "int", "set", "of", "array", "var", "true", "false", "predicate",
            "constraint", "solve", "satisfy", "minimize", "maximize"};

    /**
     * {@link Terminals} object for lexing and parsing the operators with names specified in
     * {@code ops}, and for lexing and parsing the keywords case sensitively.
     */
    final static Terminals TERMS = Terminals.caseSensitive(OPERATORS, KEYWORDS);

    static final Parser<String> NUMBER = Terminals.IntegerLiteral.PARSER;

    static final Parser<String> IDENTIFIER = Terminals.Identifier.PARSER;

    final static Parser<?> TOKENIZER =
            Parsers.or(
                    TERMS.tokenizer(),
                    Terminals.IntegerLiteral.TOKENIZER
            );

    /**
     * Scanner for fzn line comment
     */
    private static final Parser<Void> COMMENT = Scanners.lineComment("%");
    /**
     * Scanner for a line feed character ({@code '\n'}).
     */
    private static final Parser<Void> BACKSLASH = Scanners.isChar('\n');

    static final Indentation INDENTATION = new Indentation();

    /**
     * A {@link Parser} that takes as input the tokens returned by {@code TOKENIZER}
     * delimited by {@code FZN_DELIMITER}, and runs {@code this} to parse the tokens.
     * <p/>
     * <p> {@code this} must be a token level parser.
     */
    public static <T> T parse(Parser<T> parser, String source) {
        return parser.from(INDENTATION.lexer(TOKENIZER, Indentation.WHITESPACES.or(COMMENT).or(BACKSLASH).many()))
                .parse(source);
    }


    /**
     * Scans anw skip the {@code name} expression when encountered
     */
    public static Parser<?> term(String name) {
        return Mapper._(TERMS.token(name));
    }

    public static Parser<?> phrase(String phrase) {
        return Mapper._(TERMS.phrase(phrase.split("\\s+")));
    }

}
