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

import junit.framework.Assert;
import org.codehaus.jparsec.Parser;
import org.junit.Test;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 8 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class TerminalParserTest {

    @Test
    public void testInteger() {
        assertResult(TerminalParser.NUMBER, "1", String.class, "1");
    }

    static <T> void assertResult(
            Parser<T> parser, String source, Class<? extends T> expectedType, String expectedResult) {
        assertToString(expectedType, expectedResult, TerminalParser.parse(parser, source));
    }

    static <T> void assertToString(
            Class<? extends T> expectedType, String expectedResult, T result) {
        Assert.assertTrue(expectedType.isInstance(result));
        Assert.assertEquals(expectedResult, result.toString());
    }

}
