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
package parser.flatzinc;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 15 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class Mzn2fznTest {

    @Test
    public void testNoArgs() throws IOException, InterruptedException, URISyntaxException {
        Mzn2fznHelper.main(new String[]{""});
    }

    @Test
    public void testNoData() throws IOException, InterruptedException, URISyntaxException {
        String[] args = new String[]{
                "--mzn-dir", "/home/charles/Bureau/minizinc-rotd-2009-11-02",
                "-m", "/home/charles/Bureau/minizinc-rotd-2009-11-02/benchmarks/alpha/alpha.mzn",
                "-o", "/tmp/alpha.fzn"
        };
        Mzn2fznHelper.main(args);
    }

    @Test
    public void testData() throws IOException, InterruptedException, URISyntaxException {
        String[] args = new String[]{
                "--mzn-dir", "/home/charles/Bureau/minizinc-rotd-2009-11-02",
                "-lib", "/home/charles/Choco/sources/choco/trunk/choco-tools/choco-parsers/src/main/resources/std_lib",
                "-m", "/home/charles/Bureau/minizinc-rotd-2009-11-02/benchmarks/queens/queens.mzn",
                "-d", "/home/charles/Bureau/minizinc-rotd-2009-11-02/benchmarks/queens/004.dzn",
                "-o", "/tmp/queens_004.fzn"
        };
        Mzn2fznHelper.main(args);
    }

}
