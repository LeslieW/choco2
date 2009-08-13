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
import choco.kernel.common.logging.Verbosity;
import choco.kernel.solver.Solver;
import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parser.chocogen.mzn.FlatzincParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
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
    String[] args = new String[2];

    @Before
    public void before() throws IOException, URISyntaxException {
        InputStream is = getClass().getResourceAsStream("/flatzinc.properties");
        properties.load(is);
        String f = getClass().getResource("/flatzinc").toURI().getPath();
        args[0] = "-file";
        args[1] = f;
    }

    @Test
    public void xmlTest(){
        boolean hasFailed = false;
        String directory = args[1];
        int nbpb=Integer.parseInt((String) properties.get("pb.nbpb"));
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        ChocoLogging.setLevel(Level.INFO, LOGGER);
        for(int i = 1; i < nbpb+1; i++){
            String fname = directory + "/"+ properties.get("pb."+i+".name")+".xml";
            LOGGER.info(fname);
            File xmlFile = new File(fname);
            try {
                Solver s = FlatzincParser.parseFile(xmlFile);
                s.launch();
            } catch (XmlException e) {
                Assert.fail("xml exception:"+e.getMessage());
            } catch (IOException e) {
                Assert.fail("io exception:"+e.getMessage());
            } catch (Exception e){
                hasFailed = true;
                LOGGER.warning(properties.get("pb."+i+".name")+" has encountered an exception...");
                LOGGER.warning(e.getMessage());
            }
            LOGGER.info("...done");
            ChocoLogging.flushLogs();
        }
        Assert.assertFalse("One or more file has failed", hasFailed);
    }

}
