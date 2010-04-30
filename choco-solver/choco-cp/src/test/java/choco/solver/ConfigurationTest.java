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
package choco.solver;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.search.limit.Limit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 22 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class ConfigurationTest {

    private static final String USER_KEY = "user.key";

    private Configuration configuration;


    @Before
    public void load() {
        configuration = new Configuration();
    }


    @Test
    public void test1() {
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = configuration.readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        Assert.assertTrue(safs);
    }

    @Test(expected = NullPointerException.class)
    public void testNoInt() {
        configuration.readInt(USER_KEY);
    }

    @Test
    public void testInt() {
        configuration.putInt(USER_KEY, 99);
        int value = configuration.readInt(USER_KEY);
        Assert.assertEquals(99, value);
        configuration.putInt(USER_KEY, 9);
        value = configuration.readInt(USER_KEY);
        Assert.assertEquals(9, value);
    }

    @Test(expected = NullPointerException.class)
    public void testNoBoolean() {
        configuration.readBoolean(USER_KEY);
    }

    @Test
    public void testBoolean() {
        configuration.putBoolean(USER_KEY, true);
        boolean value = configuration.readBoolean(USER_KEY);
        Assert.assertEquals(true, value);
        configuration.putBoolean(USER_KEY, false);
        value = configuration.readBoolean(USER_KEY);
        Assert.assertEquals(false, value);
    }

    @Test(expected = NullPointerException.class)
    public void testNoDouble() {
        configuration.readDouble(USER_KEY);
    }

    @Test
    public void testDouble() {
        configuration.putDouble(USER_KEY, 9.99);
        double value = configuration.readDouble(USER_KEY);
        Assert.assertEquals(9.99, value, 0.01);
        configuration.putDouble(USER_KEY, 1.e-9);
        value = configuration.readDouble(USER_KEY);
        Assert.assertEquals(1.e-9, value, 0.01);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Test(expected = NullPointerException.class)
    public void testNoEnum() {
        Limit lim = configuration.readEnum(USER_KEY, Limit.class);
    }

    @Test
    public void testEnum() {
        configuration.putEnum(USER_KEY, Limit.TIME);
        Limit value = configuration.readEnum(USER_KEY, Limit.class);
        Assert.assertEquals(Limit.TIME, value);
        configuration.putEnum(USER_KEY, Limit.UNDEF);
        value= configuration.readEnum(USER_KEY, Limit.class);
        Assert.assertEquals(Limit.UNDEF, value);
    }

    public void test2() {
        configuration.clear();
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = configuration.readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        Assert.assertTrue(safs);
    }

    @Test
    public void test3() throws IOException {
        FileOutputStream fos = new FileOutputStream(File.createTempFile("CONF_", ".properties"));
        configuration.store(fos, ChocoLogging.START_MESSAGE);
    }

    @Test
    public void test4() throws IOException {
        Properties properties = new Properties();
        try {
            final InputStream is = getClass().getResourceAsStream("/conf1.properties");
            properties.load(is);
        } catch (IOException e) {
            Assert.fail();
        }
        configuration = new Configuration(properties);
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = configuration.readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        Assert.assertTrue(safs);
    }

    @Test
    public void test5() throws IOException {
        Properties empty = new Properties();
        configuration = new Configuration(empty);
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = configuration.readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        Assert.assertTrue(safs);
    }

}
