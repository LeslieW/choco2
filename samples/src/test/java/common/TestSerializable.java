package common;/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */

import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import org.junit.Assert;
import org.junit.Test;
import samples.tutorials.continuous.CycloHexan;
import samples.tutorials.scheduling.DisjunctiveWebEx;
import samples.tutorials.trunk.Queen;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class TestSerializable {

    private static final Logger LOGGER = ChocoLogging.getTestLogger();

    private static File create() throws IOException {
        return File.createTempFile("MODEL", ".ser");
    }

     @Test
    public void testQueen() throws IOException {
        final Queen pb = new Queen();
        pb.buildModel();
        final File file = create();
        try {
            CPModel.writeInFile((CPModel)pb.model, file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        }
        pb.model = null;
        Assert.assertNull(pb.model);
        try {
            pb.model = CPModel.readFromFile(file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("ClassNotFoundException");
        }
        pb.buildSolver();
        pb.solve();
        Assert.assertEquals(92, pb.solver.getSolutionCount());
    }


    @Test
    public void testDisjunctiveWebEx() throws IOException {
        final DisjunctiveWebEx pb = new DisjunctiveWebEx();
        pb.buildModel();
        File file = null;
        try {
            file = CPModel.writeInFile((CPModel)pb.model);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        }
        pb.model = null;
        Assert.assertNull(pb.model);
        try {
            pb.model = CPModel.readFromFile(file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("ClassNotFoundException");
        }
        pb.buildSolver();
        pb.solve();
        Assert.assertEquals(9, pb.solver.getSolutionCount());
    }

    @Test
    public void testCycloHexan() throws IOException {
        final CycloHexan pb = new CycloHexan();
        pb.buildModel();
        File file = null;
        try {
            file = CPModel.writeInFile((CPModel)pb.model);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        }
        pb.model = null;
        Assert.assertNull(pb.model);
        try {
            pb.model = CPModel.readFromFile(file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("ClassNotFoundException");
        }
        pb.buildSolver();
        pb.solve();
        Assert.assertEquals(69, pb.solver.getSolutionCount());
    }
}
