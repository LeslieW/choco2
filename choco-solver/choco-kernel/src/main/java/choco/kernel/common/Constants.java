package choco.kernel.common;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Feb 10, 2010
 * Time: 7:36:01 PM
 */
public class Constants {

    /**
     * Defines the rounding precision for multicostregular algorithm
     */
    public static final int MCR_PRECISION = 5; // MUST BE < 13 as java messes up the precisions starting from 10E-12 (34.0*0.05 == 1.70000000000005)

      /**
     * Defines the smallest used double for multicostregular
     */
    public static final double MCR_DECIMAL_PREC = Math.pow(10.0,-MCR_PRECISION);


}
