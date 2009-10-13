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
package choco.memory;

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.memory.copy.EnvironmentCopying;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;
import samples.Examples.Queen;
import samples.seminar.ExDonaldGeraldRobert;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 13 oct. 2008
 * Time: 10:58:27
 * To change this template use File | Settings | File Templates.
 */
public class RecomputationTest {

        @Test
        public void donaldGeraldRobert(){
           //ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        	Model m = ExDonaldGeraldRobert.modelIt1();
            CPSolver s = new CPSolver();
            s.setRecomputation(true);
            s.setRecomputationGap(2);
            Solver _s = new CPSolver();
            // Read the model
            s.read(m);
            _s.read(m);

           // ChocoLogging.setVerbosity(Verbosity.SOLUTION);

            // Then solve it
            s.solve();
            //ChocoLogging.flushLogs();
            _s.solve();
            //ChocoLogging.flushLogs();
            // Print name value

            Assert.assertEquals("donald is not equal",_s.getVar(ExDonaldGeraldRobert._donald).getVal(),s.getVar(ExDonaldGeraldRobert._donald).getVal());
            Assert.assertEquals("gerald is not equal",_s.getVar(ExDonaldGeraldRobert._gerald).getVal(),s.getVar(ExDonaldGeraldRobert._gerald).getVal());
            Assert.assertEquals("robert is not equal",_s.getVar(ExDonaldGeraldRobert._robert).getVal(),s.getVar(ExDonaldGeraldRobert._robert).getVal());
        }

    @Test
        public void nQueen() {
        //ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        Queen pb = new Queen();
        pb.setUp(12);
        pb.buildModel();

        Solver _s = new CPSolver();
        Solver _sc = new CPSolver(new EnvironmentCopying());

        CPSolver s = new CPSolver();
        CPSolver sc = new CPSolver(new EnvironmentCopying());

        s.setRecomputation(true);
        s.setRecomputationGap(10);

        sc.setRecomputation(true);
        sc.setRecomputationGap(10);
        // Read the model
        s.read(pb._m);
        sc.read(pb._m);
        _s.read(pb._m);
        _sc.read(pb._m);
//        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        ChocoLogging.setVerbosity(Verbosity.VERBOSE);

        // Then solve it
        s.solveAll();
        sc.solveAll();
        //ChocoLogging.flushLogs();
        _s.solveAll();
        _sc.solveAll();

        //ChocoLogging.flushLogs();
        // Print name value

        Assert.assertEquals("nb solutions", s.getNbSolutions(), _s.getNbSolutions());
        Assert.assertEquals("nb nodes", s.getNodeCount(), _s.getNodeCount());
    }
        
        


}

