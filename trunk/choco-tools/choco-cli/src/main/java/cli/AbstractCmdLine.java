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
package cli;

import choco.kernel.common.logging.ChocoLogging;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * The abstract class helps to define java command line.
 * @author Arnaud Malapert</br> 
 * @since 20 oct. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal","PMD.MethodArgumentCouldBeFinal"})
public abstract class AbstractCmdLine {

	public final static String CMD = "java [-jar myCmd.jar| -cp myCmd.jar MyCmd.class] ";
	
	public final static Logger LOGGER= ChocoLogging.getParserLogger();
	
	public final static int NONE= Integer.MIN_VALUE;
	
	/**
	 *  receives other command line parameters than options
	 */
	@Argument
	public List<String> arguments = new ArrayList<String>();
	
	protected final boolean hasExtraArgument;

	protected final CmdLineParser parser;

	/**
	 * the mode of this Command Line
	 * @param hasExtraArguments
	 */
	public AbstractCmdLine(boolean hasExtraArguments) {
		super();
		hasExtraArgument = hasExtraArguments;
		parser = new CmdLineParser(this);
	}

	
	/**
	 * check the validity of the command line
	 * @throws CmdLineException
	 */
	protected abstract void checkData() throws CmdLineException;
	
	
	protected abstract void execute();

	public void help() {
		System.err.println("the available options are: ");
		//parser.printUsage(System.err);
		System.err.println("Example of command with required arguments:\n"+CMD + parser.printExample(ExampleMode.REQUIRED));
		System.err.println("Example of command with all arguments:\n"+CMD + parser.printExample(ExampleMode.ALL));
		System.err.println("Options:");
		parser.printUsage(System.err);
	}
	
	public final void exitOnException(Exception e) {
		LOGGER.log(Level.SEVERE,"cmd...[FAIL]",e);
		ChocoLogging.flushLogs();
		help();
		System.exit(1);
	}

	/**
	 * parse the command line
	 * @param args CL arguments
	 */
	public void doMain(final String[] args) {
		if(args==null) {
			LOGGER.config("cmd...[NO_ARGS]");
		}
		try {
			// parse the arguments.
			parser.parseArgument(args == null ? new String[0] : args);
			checkData();
			if( !hasExtraArgument  && !arguments.isEmpty()) {
				LOGGER.log(Level.SEVERE, "cmd...[ignored_args:{0}]", this.arguments);
			}
		} catch( CmdLineException e ) {
			exitOnException(e);
		}
		LOGGER.config("cmd...[OK]");
		execute();
		LOGGER.config("cmd...[END]");
		ChocoLogging.flushLogs();
	}

	
}
