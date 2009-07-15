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
package performance;

import choco.kernel.common.logging.ChocoLogging;
import parser.chocogen.XmlModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class Performance {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Properties properties = new Properties();
    String[] args = new String[24];

    public void before() throws IOException, URISyntaxException {
        InputStream is = getClass().getResourceAsStream("/perf.properties");
        properties.load(is);
        String f = getClass().getResource("/csp").toURI().getPath();
        args[0] = "-file";
        args[1] = f;
        args[2] = "-h";
        args[3] = properties.getProperty("perf.h");
        args[4] = "-ac";
        args[5] = properties.getProperty("perf.ac");
        args[6] = "-s";
        args[7] = properties.getProperty("perf.s");
        args[8] = "-verb";
        args[9] = properties.getProperty("perf.verb");
        args[10] = "-time";
        args[11] = properties.getProperty("perf.time");
        args[12] = "-randval";
        args[13] = properties.getProperty("perf.randval");
        args[14] = "-rest";
        args[15] = properties.getProperty("perf.rest");
        args[16] = "-rb";
        args[17] = properties.getProperty("perf.rb");
        args[18] = "-rg";
        args[19] = properties.getProperty("perf.rg");
        args[20] = "-saclim";
        args[21] = properties.getProperty("perf.saclim");
        args[22] = "-seed";
        args[23] = properties.getProperty("perf.seed");
    }


    private void execute() throws IOException, URISyntaxException {
        before();
        String directory = args[1];
        int nbpb=Integer.parseInt((String) properties.get("pb.nbpb"));
        for(int i = 1; i < nbpb+1; i++){
            args[1] = directory + "/"+ properties.get("pb."+i+".name")+".xml";

            XmlModel xm = new XmlModel();
            try {
                xm.generate(args);
            } catch (Exception e) {
                LOGGER.severe(e.toString());
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Performance().execute();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}