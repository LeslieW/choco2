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

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 13 oct. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/


import choco.kernel.common.logging.ChocoLogging;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import parser.chocogen.mzn.FlatZincLexer;
import parser.chocogen.mzn.FlatZincParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;


public class Fzn {

    public static void main(String[] args) throws IOException {
        CharStream cs = new ANTLRFileStream("/media/Documents/c-choco/minizinc/antlr/fzn/queens04.fzn");

        FlatZincLexer fznLex = new FlatZincLexer(cs);

        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(fznLex);

        FlatZincParser fzn = new FlatZincParser(tokens);
        try {
            fzn.model();
        } catch (RecognitionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static Logger LOGGER = ChocoLogging.getParserLogger();

    HashMap<String, Object> memory = new HashMap<String, Object>();




}
