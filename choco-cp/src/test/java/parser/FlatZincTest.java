/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package parser;

import choco.kernel.common.logging.ChocoLogging;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parser.flatzinc.FlatZincModel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class FlatZincTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Properties properties = new Properties();
    String[] args = new String[6];

    @Before
    public void before() throws IOException, URISyntaxException {
        InputStream is = getClass().getResourceAsStream("/flatzinc.properties");
        properties.load(is);
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        args[0] = "-mzn";
        args[1] = f;
        args[2] = "-dzn";
        args[3] = f;
        args[4] = "-verb";
        args[5] = "true";

    }


    @Test
    public void aTest() {
        FlatZincModel fzm = new FlatZincModel();
        args[1] = args[1] + "/golomb.mzn";
        args[3] = args[3] + "/golomb.1.dzn";
        try {
            fzm.generate(args);
        } catch (Exception e) {
            LOGGER.severe(e.toString());
            Assert.fail();
        }
    }

    class DZNFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".dzn"));
        }
    }


    @Test
    public void dataTest() {
        File directory = new File(args[3]);
        args[1] = args[1] + "/golomb.mzn";
        String data = args[3];
        if(directory.isDirectory()){
            File[] files  = directory.listFiles(new DZNFilter());
            for (File file : files) {
                FlatZincModel fzm = new FlatZincModel();
                args[3] = data + "/" + file.getName();
                try {
                    fzm.generate(args);
                } catch (Exception e) {
                    LOGGER.severe(e.toString());
                    System.exit(-1);
//                    Assert.fail();
                }
            }
        }else{
            Assert.fail("Directory expected");
        }
    }

}
