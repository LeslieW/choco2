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
package choco.model.preprocessor.graph;

import choco.cp.common.util.preprocessor.graph.ArrayGraph;
import choco.cp.common.util.preprocessor.graph.MaxCliques;
import choco.kernel.common.logging.ChocoLogging;
import org.junit.Test;

import java.util.Random;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 2 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class ArrayGraphTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    public static void test(int n, int m, int seed) {
		double start = System.currentTimeMillis();
		ArrayGraph g = MaxCliques.generateGraph(n,m,seed,start);
	    MaxCliques myCliques = new MaxCliques(g);
	    LOGGER.info("cliques : \n" + MaxCliques.display(myCliques.getMaxCliques()));
	    LOGGER.info("Total time : " + (System.currentTimeMillis()-start) + " ms.\n");
	    if(n<=16) {
	    	LOGGER.info(g.toString());
	    }
	}

    @Test
	public void testEmptyGraph241108() {
		LOGGER.info("Graph without edges");
		test(6,0,1986);
	}

    @Test
	public void test2() {
        for (int i = 0; i < 100; i++) {
            Random r = new Random(i);
            int m = r.nextInt(10);
            int p = 1 +r.nextInt(10);
            int n = m + p;
            int seed = r.nextInt();

            test(n, m, seed);
        }
    }
}
