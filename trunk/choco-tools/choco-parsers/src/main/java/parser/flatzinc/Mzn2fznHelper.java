package parser.flatzinc;/* ************************************************
*           _       _                            *
*          |  ?(..)  |                           *
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
* Date : 22 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* Class to build a FlatZinc file from a MiniZinc file and data (if required).
*
* Using this class requires to MiniZinc installed.
*
*/

import choco.kernel.common.logging.ChocoLogging;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * For internal uses
 */

public class Mzn2fznHelper {

    static Logger LOGGER = ChocoLogging.getMainLogger();

    @Option(name = "--mzn-dir", usage = "Specify the MiniZinc directory.", required=true,metaVar = "<minizinc dir>")
    private static String mznDir;

    @Option(name = "-lib", usage = "Specify directory containing the CHOCO standard library directory.",
            required=false, metaVar = "<choco_std dir>")
    private static String chocoLib;

    @Option(name = "-m", usage = "File named <model file> contains the model.",required=true, metaVar = "<model file>")
    private static String mznFile;

    @Option(name = "-d", usage = "File named <data file> contains data used by the model.",required=false, metaVar = "<data file>")
    private static String dznFile;

    @Option(name = "-o", usage = "Output the FlatZinc to the specified file rather than temp directory.",required=true,
    metaVar = "<fzn file>")
    private static String fznFile;

    private static final String CHOCO_STD = "choco_std";

    private final static String WHITSPACE = " ";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        // set default value to chocoLib
        chocoLib = Mzn2fznHelper.class.getResource("/std_lib").toURI().getPath();
        new Mzn2fznHelper().doMain(args);
    }

    public void doMain(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(160);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...]");
            parser.printUsage(System.err);
            System.err.println("\nCheck MiniZinc is correctly installed.");
            System.err.println();
            return;
        }
        mzn2fzn();
    }

    protected static void mzn2fzn() throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append(mznDir).append(File.separator).append("bin").append(File.separator).append("mzn2fzn");
        cmd.append(WHITSPACE)
                .append("-v ")
                .append("--stdlib-dir").append(WHITSPACE)
                .append(chocoLib).append(WHITSPACE)
                .append("-G").append(WHITSPACE)
                .append(CHOCO_STD).append(WHITSPACE)
                .append(mznFile).append(WHITSPACE)
                .append("-o").append(WHITSPACE)
                .append(fznFile).append(WHITSPACE);
        if(dznFile!=null){
            cmd.append("-d ").append(WHITSPACE)
                .append(dznFile).append(WHITSPACE);
        }
        //System.out.println(cmd.toString());
        run(cmd.toString());
    }

    /**
     * Run a specific command
     *
     * @param cmd the command
     */
    private static void run(String cmd) {
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
