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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;


public class Fzn {

    static Logger LOGGER = ChocoLogging.getParserLogger();

    public static void main(String[] args) throws IOException {
        args = new String[]{"/home/charles/Choco/minizinc/fzn/"};
        scan(new File(args[0]));
    }

    private static void scan(File dir) throws IOException {
        if(dir.isFile()){
            flatzincIt(dir.getAbsolutePath());
        }else{
            File[] ts = dir.listFiles();
            Arrays.sort(ts);
            for(File sd : ts){
                scan(sd);
            }
        }
    }

    private static void flatzincIt(String filename) throws IOException {

        LOGGER.info(filename);
        CharStream cs = new ANTLRFileStream(filename);

        FlatZincLexer fznLex = new FlatZincLexer(cs);

        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(fznLex);

        FlatZincParser fzn = new FlatZincParser(tokens);
        try {
            fzn.model();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
        LOGGER.info("done");
    }

}
