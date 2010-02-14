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
package choco.kernel.common.util.tools;

import choco.kernel.common.util.comparator.ConstantPermutation;
import choco.kernel.common.util.comparator.IPermutation;
import choco.kernel.common.util.comparator.Identity;
import choco.kernel.common.util.comparator.IntPermutation;
import choco.kernel.model.variables.integer.IntegerConstantVariable;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 3 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public final class PermutationUtils {
   
	
	
	private PermutationUtils() {
		super();
	}

	public static IPermutation getIdentity() {
        return Identity.SINGLETON;
    }

    public static IPermutation getSortingPermuation(int[] criteria) {
        return getSortingPermuation(criteria, false);
    }

    public static IPermutation getSortingPermuation(int[] criteria,boolean reverse) {
        return new IntPermutation(criteria,reverse);
    }

    public static IPermutation getSortingPermuation(IntegerConstantVariable[] criteria,boolean reverse) {
        return new ConstantPermutation(criteria,reverse);
    }

    public static IntegerConstantVariable[] applyPermutation(IPermutation permutation, IntegerConstantVariable[] source) {
        if(permutation.isIdentity()) {return source;}
        else {
            IntegerConstantVariable[] dest = new IntegerConstantVariable[source.length];
            permutation.applyPermutation(source,dest);
            return dest;
        }
    }
}
