/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
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
*                   N. Jussien    1999-2010      *
**************************************************/
package samples.tutorials.packing.parser;

import java.io.File;
import java.util.Random;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import parser.instances.AbstractInstanceModel;
import parser.instances.BasicSettings;
import parser.instances.checker.SCheckFactory;
import cli.AbstractBenchmarkCmd;

/**
 * An extension of <code>AbstractBenchmarkCmd</code> abstract class to load and solve BinPacking problems.
 *
 * Instances can be found <a href="http://www.wiwi.uni-jena.de/Entscheidung/binpp/index.htm">here</a>
 *
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 8 juil. 2010
 */
public class BinPackingCmd extends AbstractBenchmarkCmd {

	/**
	 * the branching strategy
	 */
	@Option(name="-l",aliases={"--light"},usage="activate global constraints decomposition")
	protected Boolean lightModel;
	
	
    public BinPackingCmd() {
        super(new BasicSettings());
    }

    @Override
    protected AbstractInstanceModel createInstance() {
        return new BinPackingModel(settings);
    }

    @Override
	protected void checkData() throws CmdLineException {
		super.checkData();
		seeder =  new Random(seed);
		//check for Boolean, if null then keep default setting (property file)
		if(lightModel != null) settings.putTrue(BasicSettings.LIGHT_MODEL);
		//load status checkers
		SCheckFactory.load("/bin-packing-tut/bin-packing-tut.properties");
    }

    @Override
    public boolean execute(File file) {
        instance.solveFile(file);
        return instance.getStatus().isValidWithOptimize();
    }

    public static void main(String[] args) {
        final BinPackingCmd cmd = new BinPackingCmd();
        if (args.length == 0) {
            cmd.help();
        } else {
            cmd.doMain(args);
        }
    }
}
