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

import choco.kernel.model.Model;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solver;
import parser.instances.AbstractInstanceModel;


/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 8 juil. 2010
 */
public class BinPackingModel extends AbstractInstanceModel{

    final CPpack pack;

    public BinPackingModel(Configuration defaultConfiguration) {
        super(new BinPackingFileParser(), defaultConfiguration);
        pack = new CPpack();
    }

    @Override
    public Boolean preprocess() {
        if(pack.iub==pack.ilb){
            return Boolean.TRUE;
        }
        return null;
    }

    @Override
    public Model buildModel() {
        // Get the instance
        final BinPackingFileParser parser = (BinPackingFileParser) this.parser;
		parser.parse(false);

        // Build the model
        pack.setUp(parser.getParameters());
        pack.buildModel();
        return pack.model;
    }

    @Override
    public Solver buildSolver() {
        pack.buildSolver();
        return pack.solver;
    }

    @Override
    public Boolean solve() {
        pack.solve();
        return pack.solver.isFeasible();
    }
}
