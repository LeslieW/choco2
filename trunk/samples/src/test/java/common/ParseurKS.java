/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
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
*                   N. Jussien    1999-2010      *
**************************************************/
package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 27 juil. 2010
 */
public class ParseurKS {

    public static int[][] instances;
    public static int[] bounds;


    public static void parseFile(String filename, int n) throws IOException {

        ArrayList<Integer> weight = new ArrayList<Integer>(16);
        ArrayList<Integer> profit = new ArrayList<Integer>(16);
        int min = 0;
        int max = 0;

        FileReader f = new FileReader(filename);
        BufferedReader r = new BufferedReader(f);

        String line;

        Pattern wPattern = Pattern.compile(" *calorie.*\\+(\\d+)");
        Pattern pPattern = Pattern.compile(" *poids.*\\+(\\d+)");
        Pattern cmaxPattern = Pattern.compile(" *cmax.*\\+(\\d+)");
        Pattern cminPattern = Pattern.compile(" *cmin.*\\+(\\d+)");


        while ((line = r.readLine()) != null) {

            Matcher mw = wPattern.matcher(line);
            Matcher mp = pPattern.matcher(line);
            Matcher mc1 = cmaxPattern.matcher(line);
            Matcher mc2 = cminPattern.matcher(line);

            if (mw.matches()) {
                weight.add(Integer.parseInt(mw.group(1)));
            } else if (mp.matches()) {
                profit.add(Integer.parseInt(mp.group(1)));
            } else if (mc1.matches()) {
                max = Integer.parseInt(mc1.group(1));
            } else if (mc2.matches()) {
                min = Integer.parseInt(mc2.group(1));
            }


        }

        if(n < 0){
            n = weight.size();
        }
        int[] weights = new int[n];
        int[] profits = new int[n];

        for (int i = 0; i < n; i++) {
            weights[i] = weight.get(i);
            profits[i] = profit.get(i);

        }
        instances = new int[][]{profits, weights};
        bounds  = new int[]{min, max};

    }

}
