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
import org.junit.Ignore;
import org.junit.Test;
import parser.absconparseur.tools.SolutionChecker;
import parser.chocogen.XmlModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.Permission;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ResolutionTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Properties properties = new Properties();
    String[] args = new String[24];

    @Before
    public void before() throws IOException, URISyntaxException {
        InputStream is = getClass().getResourceAsStream("/csp.properties");
        properties.load(is);
        String f = getClass().getResource("/csp").toURI().getPath();
        args[0] = "-file";
        args[1] = f;
        args[2] = "-h";
        args[3] = properties.getProperty("csp.h");
        args[4] = "-ac";
        args[5] = properties.getProperty("csp.ac");
        args[6] = "-s";
        args[7] = properties.getProperty("csp.s");
        args[8] = "-verb";
        args[9] = properties.getProperty("csp.verb");
        args[10] = "-time";
        args[11] = properties.getProperty("csp.time");
        args[12] = "-randval";
        args[13] = properties.getProperty("csp.randval");
        args[14] = "-rest";
        args[15] = properties.getProperty("csp.rest");
        args[16] = "-rb";
        args[17] = properties.getProperty("csp.rb");
        args[18] = "-rg";
        args[19] = properties.getProperty("csp.rg");
        args[20] = "-saclim";
        args[21] = properties.getProperty("csp.saclim");
        args[22] = "-seed";
        args[23] = properties.getProperty("csp.seed");
    }


    @Test
    public void mainTest() {

        String directory = args[1]; 
        int nbpb=Integer.parseInt((String) properties.get("pb.nbpb"));
        for(int i = 1; i < nbpb+1; i++){
            args[1] = directory + "/"+ properties.get("pb."+i+".name")+".xml";
            XmlModel xm = new XmlModel();
            try {
                xm.generate(args);
            } catch (Exception e) {
                LOGGER.severe(e.toString());
                Assert.fail();
            }
            System.setSecurityManager(new NoExitSecurityManager());
            try{
                if(xm.isFeasible()==Boolean.TRUE)
                    SolutionChecker.main(xm.getValues());
            }catch (ExitException e){
                LOGGER.severe(e.toString());
                Assert.fail();
            }finally {
                System.setSecurityManager(null);
            }
            int time = Integer.valueOf((String) properties.get("pb."+i+".buildtime"));
            LOGGER.info(xm.getBuildTime()  + " > " + time + "?" + " for " + "pb."+i);
            Assert.assertTrue(properties.get("pb."+i+".name")+": too much time spending in reading problem...", xm.getBuildTime() < time);

            time = Integer.valueOf((String) properties.get("pb."+i+".conftime"));
            LOGGER.info(xm.getConfTime()  + " > " + time + "?" + " for " + "pb."+i);
            Assert.assertTrue(properties.get("pb."+i+".name")+": too much time spending in preprocessing problem...", xm.getConfTime() < time);
        }
    }


    @Test
    public void bibdTest() {
        XmlModel xm = new XmlModel();
        args[1] = args[1] + "/bibd-8-14-7-4-3_glb.xml";
        int nbNodes = -1;
        for (int i = 0; i < 5; i++) {
        	try {
                xm.generate(args);
            } catch (Exception e) {
                LOGGER.severe(e.toString());
                Assert.fail();
            }
            if (nbNodes == -1) {
                nbNodes = xm.getNbNodes();
            } else {
                Assert.assertEquals("not same number of nodes", nbNodes, xm.getNbNodes());
            }
        }
    }

    @Test
    @Ignore
    public void taskTest() {
    	 XmlModel xm = new XmlModel();
         args[1] = args[1] + "/tasks.xml";
         try {
			xm.generate(args);
         } catch (Exception e) {
        	 LOGGER.log(Level.SEVERE, "unexpected exceptions", e);
             Assert.fail();
		}
    }

    @Test
    @Ignore
    public void aTest() {
        XmlModel xm = new XmlModel();
        args[1] = args[1] + "/protein.xml";
//        ChocoLogging.setVerbosity(Verbosity.VERBOSE);
        try {
            xm.generate(args);
        } catch (Exception e) {
            LOGGER.severe(e.toString());
            Assert.fail();
        }
            }


    //***************************************************************************************
/**
	 * An exception thrown when an System.exit() occurred.
	 * @author Fabien Hermenier
	 *
	 */
	class ExitException extends SecurityException {

		/**
		 * The exit status.
		 */
		private int status;

		/**
		 * A new exception.
		 * @param st the exit status
		 */
		public ExitException(int st) {
			super("There is no escape");
			this.status = st;
		}

		/**
		 * Return the error message.
		 * @return a String!
		 */
		public String getMessage() {
			return "Application execute a 'System.exit(" + this.status + ")'";
		}
	}

	/**
	 * A Mock security manager to "transform" a System.exit() into
	 * a ExitException.
	 * @author Fabien Hermenier
	 *
	 */
	class NoExitSecurityManager extends SecurityManager {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void checkPermission(Permission perm, Object ctx) {

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void checkPermission(Permission perm) {

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void checkExit(int st) {
			super.checkExit(st);
			throw new ExitException(st);
		}
	}
    

}
