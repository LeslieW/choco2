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
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import static choco.kernel.common.util.tools.StringUtils.pad;
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
        	Model m = ExDonaldGeraldRobert.modelIt1();
            CPSolver s = new CPSolver();
            s.setRecomputation(true);
            s.setRecomputationGap(10);
            Solver _s = new CPSolver();
            // Read the model
            s.read(m);
            s.attachGoal(new AssignOrForbidIntVarVal(new MinDomain(s), new MinVal()));
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
        //ChocoLogging.setVerbosity(Verbosity.OFF);


        System.out.print(pad("Q", 10, " "));
        System.out.print(pad("T", 15, " "));
        System.out.print(pad("C", 15, " "));
        System.out.print(pad("R+T", 15, " "));
        System.out.println(pad("R+C", 15, " "));
        for(int q = 4; q <= 11; q++){
            System.out.print(pad(pad(""+q, -2, " "),10, " "));
            Queen pb = new Queen();
            pb.setUp(q);
            pb.buildModel();

            Solver s1 = new CPSolver();
            Solver s2 = new CPSolver(new EnvironmentCopying());

            CPSolver sr1 = new CPSolver();
            CPSolver sr2 = new CPSolver(new EnvironmentCopying());

            sr1.setRecomputation(true);
            sr1.setRecomputationGap(10);
//            sr1.attachGoal(new AssignOrForbidIntVarVal(new MinDomain(sr1), new MinVal()));

            sr2.setRecomputation(true);
            sr2.setRecomputationGap(10);
//            sr2.attachGoal(new AssignOrForbidIntVarVal(new MinDomain(sr2), new MinVal()));
            // Read the model
            sr1.read(pb._m);
            sr2.read(pb._m);
            s1.read(pb._m);
            s2.read(pb._m);

            // Then solve it
            ChocoLogging.flushLogs();
            s1.solveAll();
            System.out.print(pad(s1.getTimeCount()+"ms", -15, " "));
            s2.solveAll();
            System.out.print(pad(s2.getTimeCount()+"ms", -15, " "));
            sr1.solveAll();
            System.out.print(pad(sr1.getTimeCount()+"ms", -15, " "));
            sr2.solveAll();
            System.out.println(pad(sr2.getTimeCount()+"ms", -15, " "));

            //ChocoLogging.flushLogs();
            // Print name value

            Assert.assertEquals("nb solutions 1", sr1.getNbSolutions(), sr2.getNbSolutions());
            Assert.assertEquals("nb solutions 2", s1.getNbSolutions(), s2.getNbSolutions());
            Assert.assertEquals("nb solutions 3", s1.getNbSolutions(), sr1.getNbSolutions());

            Assert.assertEquals("nb nodes 1", sr1.getNodeCount(), sr2.getNodeCount());
            Assert.assertEquals("nb nodes 2", s1.getNodeCount(), s2.getNodeCount());
        }
        
    }
        


}

