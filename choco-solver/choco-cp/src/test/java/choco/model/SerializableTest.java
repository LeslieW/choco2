/* * * * * * * * * * * * * * * * * * * * * * * * * 
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
package choco.model;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.junit.Assert;
import org.junit.Test;
import samples.Examples.CycloHexan;
import samples.Examples.Queen;
import samples.scheduling.DisjunctiveWebEx;

import java.io.*;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 30 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class SerializableTest {

    private static final Logger LOGGER = ChocoLogging.getTestLogger();

    private static File create() throws IOException {
        return File.createTempFile("MODEL", ".ser");
    }


    private static File write(final Object o) throws IOException {
        final File file = create();
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        fos = new FileOutputStream(file);
        out = new ObjectOutputStream(fos);
        out.writeObject(o);
        out.close();
        return file;
    }


    private static Object read(final File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        fis = new FileInputStream(file);
        in = new ObjectInputStream(fis);
        final Object o = in.readObject();
        in.close();
        return o;
    }


    @Test
    public void testEmptyModel(){
        Model m = new CPModel();
        File file = null;
        try {
            file = write(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m = null;
        Assert.assertNull(m);
        try {
            m = (Model)read(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(m);
        LOGGER.info(m.pretty());
    }

    @Test
    public void testIntegerVariable(){
        IntegerVariable var = Choco.makeIntVar("var", 1 , 10);
        File file = null;
        try {
            file = write(var);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var = null;
        try {
            var = (IntegerVariable)read(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(var);
        LOGGER.info(var.pretty());
    }

    @Test
    public void testConstraint(){
        final IntegerVariable var = Choco.makeIntVar("var", 1 , 10);
        Constraint cstr = Choco.eq(var, 5);
        File file = null;
        try {
            file = write(cstr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cstr = null;
        Assert.assertNull(cstr);
        try {
            cstr = (Constraint)read(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(cstr);
        LOGGER.info(cstr.pretty());
    }

    @Test
    public void testQueen() throws IOException {
        final Queen pb = new Queen();
        pb.buildModel();
        final File file = create();
        try {
            CPModel.writeInFile((CPModel)pb._m, file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        }
        pb._m = null;
        Assert.assertNull(pb._m);
        try {
            pb._m = CPModel.readFromFile(file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("ClassNotFoundException");
        }
        pb.buildSolver();
        pb.solve();
        Assert.assertEquals(92, pb._s.getSolutionCount());
    }


    @Test
    public void testDisjunctiveWebEx() throws IOException {
        final DisjunctiveWebEx pb = new DisjunctiveWebEx();
        pb.buildModel();
        File file = null;
        try {
            file = CPModel.writeInFile((CPModel)pb._m);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        }
        pb._m = null;
        Assert.assertNull(pb._m);
        try {
            pb._m = CPModel.readFromFile(file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("ClassNotFoundException");
        }
        pb.buildSolver();
        pb.solve();
        Assert.assertEquals(9, pb._s.getSolutionCount());
    }

    @Test
    public void testCycloHexan() throws IOException {
        final CycloHexan pb = new CycloHexan();
        pb.buildModel();
        File file = null;
        try {
            file = CPModel.writeInFile((CPModel)pb._m);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        }
        pb._m = null;
        Assert.assertNull(pb._m);
        try {
            pb._m = CPModel.readFromFile(file);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("IOException");
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            Assert.fail("ClassNotFoundException");
        }
        pb.buildSolver();
        pb.solve();
        Assert.assertEquals(69, pb._s.getSolutionCount());
    }

}
