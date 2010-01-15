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
package parser.flatzinc;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import parser.flatzinc.parser.FZNParser;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 13 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class FznModel {
    
    static Logger LOGGER = ChocoLogging.getParserLogger();

    public static void main(String[] args) throws IOException {
        scan(new File(args[0]));
    }

    private static final FilenameFilter FILTER = new FilenameFilter() {
        private static final String _EXT = ".fzn";
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(_EXT);
        }
    };

    private static void scan(File dir) throws IOException {
        if (dir.isFile()) {
            flatzincIt(dir);
        } else {
            File[] ts = dir.listFiles(FILTER);
            if(ts!=null){
                for (File sd : ts) {
                    scan(sd);
                }
            }
        }
    }

    private static void flatzincIt(File file) throws IOException {
        String filename = file.getAbsolutePath();

        LOGGER.log(Level.INFO, "% Traitement de :{0}",
                file.getName());
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        FZNParser.FLATZINC_MODEL(readFileAsString(filename), false);
        ChocoLogging.flushLogs();
    }

    /**
     * Read a {@link File} as a {@link String}.
     * @param filePath path name of the file
     * @return  {@link String}
     * @throws java.io.IOException
     */
    private static String readFileAsString(String filePath) throws java.io.IOException {
        byte[] buffer = new byte[(int) new File(filePath).length()];
        BufferedInputStream f = new BufferedInputStream(new FileInputStream(filePath));
        f.read(buffer);
        return new String(buffer);
    }


}
