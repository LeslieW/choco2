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
package samples.tutorials.scheduling.pack.binpacking;

import cli.AbstractBenchmarkCmd;
import parser.instances.AbstractInstanceModel;
import parser.instances.BasicSettings;

import java.io.File;

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

    public BinPackingCmd() {
        super(new BasicSettings());
    }

    @Override
    protected AbstractInstanceModel createInstance() {
        return new BinPackingModel(settings);
    }

    @Override
    public boolean execute(File file) {
        instance.solveFile(file);
        return instance.getStatus().isValidWithCSP();
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
