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
 *                   N. Jussien    1999-2009      *
 **************************************************/
package db;

import choco.kernel.common.logging.ChocoLogging;
import org.hsqldb.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 oct. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class DbServer {

    public final static Logger LOGGER = ChocoLogging.getParserLogger();

    /**
     * The HSQL server
     */
    Server server;
    /**
     * The database file
     */
    @Option(name = "-database", usage = "Sets database file name")
    String databaseFile;

    /**
     * The database name
     */
    @Option(name = "-dbname", usage = "Sets database name")
    String databaseName;


    /**
     * Run the database server with following parameters
     *
     * @param args
     */
    public static void main(String[] args) {
        LOGGER.setLevel(Level.INFO);
        args = new String[]{"-database", "/home/charles/Choco/s- source/choco/trunk/choco-contribs/src/main/resources/chocodb.odb", "-dbname", "testdb"};
        DbServer db = new DbServer();
        CmdLineParser parser = new CmdLineParser(db);
        try {
            parser.parseArgument(args);
            db.start();
            db.stop();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    protected void start() {
        server = new Server();
        server.setDatabasePath(0, databaseFile);
        server.setDatabaseName(0, databaseName);
        server.start();
        System.err.println(DbUrlFactory.makeNetworkURL(getIp(), server.getPort(), databaseName));
    }

    protected void stop() {
        server.stop();
    }


    /**
     * Retourne toutes les adresses ips des carte réseau de la machine. Retourne seulement les addresses IPV4
     * et commencant par 192...
     *
     * @return Une liste des addresses ip
     */
    protected String getIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {  // carte reseau trouvee
                NetworkInterface interfaceN = (NetworkInterface) interfaces.nextElement();
                Enumeration<InetAddress> ienum = interfaceN.getInetAddresses();
                while (ienum.hasMoreElements()) {  // retourne l adresse IPv4 et IPv6
                    InetAddress ia = ienum.nextElement();
                    String adress = ia.getHostAddress();
                    if(adress.length() < 16 && adress.startsWith("192")){
                        return adress;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("pas de carte reseau");
            e.printStackTrace();
        }
        return null;
    }

}
