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
import org.junit.Assert;
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

    @Test
    public void test1() {
        Configuration conf = new Configuration();
        conf.put(USER_KEY, true);
        Boolean mykey = (Boolean) conf.get(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = conf.getAsBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        Assert.assertTrue(safs);
    }

    @Test(expected = NullPointerException.class)
    public void test2() {
        Configuration conf = new Configuration();
        conf.clear();
        conf.put(USER_KEY, true);
        Boolean mykey = (Boolean) conf.get(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = conf.getAsBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
    }

    @Test
    public void test3() throws IOException {
        Configuration conf = new Configuration();
        FileOutputStream fos = new FileOutputStream(File.createTempFile("CONF_", ".properties"));
        conf.store(fos, ChocoLogging.START_MESSAGE);
    }

    @Test
    public void test4() throws IOException {

        Properties properties = new Properties();
        try {
			final InputStream is = getClass().getResourceAsStream( "/conf1.properties" );
            properties.load(is);
		} catch (IOException e) {
			Assert.fail();
		}

        Configuration conf = new Configuration(properties);
        conf.putAsBoolean(USER_KEY, true);
        Boolean mykey = conf.getAsBoolean(USER_KEY);
        Assert.assertTrue(mykey);
        Boolean safs = conf.getAsBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        Assert.assertTrue(safs);
    }
    
}
