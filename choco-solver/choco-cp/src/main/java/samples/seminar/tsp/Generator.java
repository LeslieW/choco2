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
package samples.seminar.tsp;

import choco.kernel.common.logging.ChocoLogging;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;


public class Generator {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

    protected Random rand;

    protected int n;

    protected int maxDist;
    protected int[][] dist;

    protected int[] hamPath;

    public Generator(Random rand, int n, int maxDist) {
        this.rand = rand;
        this.n = n;
        this.maxDist= maxDist;
        this.dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.dist[i][j] = -1;
            }
        }
        this.hamPath = new int[n];
        generateHamCycle();
    }

    public int[][] generateMatrix() {  // matrix with splitted depot node
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = rand.nextInt(maxDist);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j || (i == n - 1 && j == 0) ) {
                    dist[i][j] = 0;
                } else {
                    if (hamPath[i] == j) dist[i][j] = 1;
                }
                //LOGGER.info("dist["+i+"]["+j+"] = "+dist[i][j]);
            }
        }
        return dist;
    }

    private void generateHamCycle() {
        for (int i = 0; i < n; i++) hamPath[i] = -1;
        ArrayList<Integer> dispo = new ArrayList<Integer>(n-2);
        for (int i = 1; i < n-1; i++) dispo.add(i);
        //LOGGER.info(dispo.toString());
        int next = 0;
        while(!dispo.isEmpty()) {
            hamPath[next] = dispo.remove(rand.nextInt(dispo.size()));
            next = hamPath[next];
        }
        for (int i = 1; i < n-1; i++) {
            if (hamPath[i] == -1) hamPath[i] = n-1;
        }
        hamPath[n-1] = 0;
        LOGGER.info(showHamPath());
    }

    public String showHamPath() {
        String s = ""+hamPath[0];
        int i = 1;
        while(i < hamPath.length) {
            s += " "+hamPath[i];
            i++;
        }
        return s;
    }

}
