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

import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import parser.flatzinc.reader.DataReader;
import parser.flatzinc.reader.ModelReader;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 10 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class FlatZincModel {

    protected final static Logger LOGGER = ChocoLogging.getParserLogger();

    static boolean verb;
    static long[] time = new long[3];

    /**
     * Main method. Check arguments and set up the options
     * accordingly. example of command line :
     * -file mycsp.xml -verb true
     *
     * @param args arguments
     * @throws Exception
     */
    public void generate(String[] args) throws Exception {

        HashMap<String, String> options = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i++];
            String val = args[i];
            options.put(arg, val);
        }
        File modelF, dataF;
        if (options.containsKey("-mzn")) {
            modelF = new File(options.get("-mzn"));
            if (!modelF.exists()) {
                throw new Exception("Unknown mzn file");
            }
        } else {
            throw new Exception("file option -mzn is missing");
        }
        if (options.containsKey("-dzn")) {
            dataF = new File(options.get("-dzn"));
            if (!dataF.exists()) {
                throw new Exception("Unknown dzn file");
            }
        } else {
            throw new Exception("file option -dzn is missing");
        }
        if (options.containsKey("-verb")) {
            verb = Boolean.parseBoolean(options.get("-verb"));
        }
        try {
            solveFile(modelF, dataF);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Solve the csp given by file "fichier"
     * @param modelF
     * @param dataF
     */
    public void solveFile(File modelF, File dataF) {
        init();
        if (modelF.getName().endsWith(".mzn")
                && dataF.getName().endsWith(".dzn")) {
            try {
                CPModel model = loadAndParse(modelF, dataF);
//                CPModel model = buildModel(parser);
//                PreProcessCPSolver s = solve(model);
//                postAnalyze(fichier, parser, s);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
    }

    public void init() {
        time = new long[3];
    }


    /**
     * Parse the xml and return the parser object (Christophe parser) which
     * can be used to access variables, constraints, etc...
     * @param modelF
     * @param dataF
     * @return A parser object containing the description of the problem
     * @throws Exception
     * @throws Error
     */
    public CPModel loadAndParse(File modelF, File dataF) throws Exception, Error {
       try {
            if (verb) {
                LOGGER.info("========================================================");
                LOGGER.info("Traitement de : " + modelF.getName());
                LOGGER.info("Données : "+ dataF.getName());
            }
            // Parse the xml and get the abscon representation of the problem
            time[0] = System.currentTimeMillis();
            HashMap datas = DataReader.readDataFile(dataF.getAbsolutePath());
            CPModel model = ModelReader.readModelFile(modelF.getAbsolutePath(), datas);
           time[1] = System.currentTimeMillis();
           return model;
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (Error er) {
            er.printStackTrace();
        }finally {
           LOGGER.info("========================================================");
       }
        return null;
    }
}
