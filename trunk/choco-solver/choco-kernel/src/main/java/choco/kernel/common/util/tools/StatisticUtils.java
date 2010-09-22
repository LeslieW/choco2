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
package choco.kernel.common.util.tools;

import java.util.Arrays;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 17 aožt 2010
 */
public class StatisticUtils {

    public static int sum(int... values){
        int sum = 0;
        for(int i = 0; i < values.length; i++){
            sum+= values[i];
        }
        return sum;
    }

    public static long sum(long... values){
        long sum = 0L;
        for(int i = 0; i < values.length; i++){
            sum+= values[i];
        }
        return sum;
    }

    public static double sum(double... values){
        double sum = 0.0;
        for(int i = 0; i < values.length; i++){
            sum+= values[i];
        }
        return sum;
    }

    public static double mean(int... values){
        return sum(values)/values.length;
    }

    public static float mean(long... values){
        return sum(values)/values.length;
    }

    public static double mean(double... values){
        return sum(values)/values.length;
    }


    public static double standarddeviation(int... values){
        double mean = mean(values);
        double[] psd = new double[values.length];
        for(int i = 0 ; i < values.length; i++){
            psd[i] = Math.pow(values[i] - mean, 2.0);
        }
        return Math.sqrt(mean(psd));
    }

    public static double standarddeviation(long... values){
        double mean = mean(values);
        double[] psd = new double[values.length];
        for(int i = 0 ; i < values.length; i++){
            psd[i] = Math.pow(values[i] - mean, 2.0);
        }
        return Math.sqrt(mean(psd));
    }

    public static int[] prepare(int... values){
        Arrays.sort(values);
        int[] back = new int[values.length - 2];
        System.arraycopy(values, 1, back, 0, back.length);
        return back;
    }

    public static long[] prepare(long... values){
        Arrays.sort(values);
        long[] back = new long[values.length - 2];
        System.arraycopy(values, 1, back, 0, back.length);
        return back;
    }

}
