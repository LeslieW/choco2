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
package choco.kernel.common;

import java.lang.reflect.Array;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 13 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*
*
* Compute the hashCode of a given object
*/
public class HashCoding {
    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
     * values.
     */
    public static final int SEED = 23;

    /**
     * booleans.
     */
    private static int hash(int aSeed, boolean aBoolean) {
        return firstTerm(aSeed) + (aBoolean ? 1 : 0);
    }

    /**
     * chars.
     */
    private static int hash(int aSeed, char aChar) {
        return firstTerm(aSeed) + (int) aChar;
    }

    /**
     * ints.
     */
    private static int hash(int aSeed, int aInt) {
        /*
        * Implementation Note
        * Note that byte and short are handled by this method, through
        * implicit conversion.
        */
        return firstTerm(aSeed) + aInt;
    }

    /**
     * longs.
     */
    private static int hash(int aSeed, long aLong) {
        return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
    }

    /**
     * floats.
     */
    private static int hash(int aSeed, float aFloat) {
        return hash(aSeed, Float.floatToIntBits(aFloat));
    }

    /**
     * doubles.
     */
    private static int hash(int aSeed, double aDouble) {
        return hash(aSeed, Double.doubleToLongBits(aDouble));
    }

    /**
     * <code>aObject</code> is a possibly-null object field, and possibly an array.
     * <p/>
     * If <code>aObject</code> is an array, then each element may be a primitive
     * or a possibly-null object.
     */
    private static int hash(int aSeed, Object aObject) {
        int result = aSeed;
        if (aObject == null) {
            result = hash(result, 0);
        } else if (!isArray(aObject)) {
            result = hash(result, aObject.hashCode());
        } else {
            int length = Array.getLength(aObject);
            for (int idx = 0; idx < length; ++idx) {
                Object item = Array.get(aObject, idx);
                //recursive call!
                result = hash(result, item);
            }
        }
        return result;
    }


    /// PRIVATE ///
    private static final int fODD_PRIME_NUMBER = 37;

    private static int firstTerm(int aSeed) {
        return fODD_PRIME_NUMBER * aSeed;
    }

    private static boolean isArray(Object aObject) {
        return aObject.getClass().isArray();
    }

    public static int hashCodeMe(Object field) {
    	return hashCodeMe(new Object[]{field});
    }

    public static int hashCodeMe(Object[] fields) {
        int result = SEED;
        for(int field = 0; field < fields.length; field++){
            result = hash(result, field);
        }
        return result;
    }

    public static int hashCodeMe(long field) {
    	return hash(SEED, field);
    }

}
