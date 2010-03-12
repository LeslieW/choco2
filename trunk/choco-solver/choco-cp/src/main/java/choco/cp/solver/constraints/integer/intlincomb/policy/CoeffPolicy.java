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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.integer.intlincomb.policy;

import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 11 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public abstract class CoeffPolicy {

    final IntDomainVar[] vars;

    final int cste;

    final int nbPosVars;

    final int[] coeffs;


    public static CoeffPolicy build(final IntDomainVar[] vars, final int[] coeffs, final int nbPosVars, final int cste){
        if(CoeffPolicy.isIntSum(coeffs, nbPosVars)){
            return ForSum.get(vars, coeffs, nbPosVars, cste);
        }else{
            return ForScalar.get(vars, coeffs, nbPosVars, cste);
        }
    }

    private static boolean isIntSum(int[] sortedCoeffs, int nbPositiveCoeffs) {
		for (int i = 0; i < nbPositiveCoeffs; i++) {
			if( sortedCoeffs[i] != 1) return false;
		}
		for (int i = nbPositiveCoeffs; i < sortedCoeffs.length; i++) {
			if(sortedCoeffs[i] != -1) return false;
		}
		return true;
	}

    CoeffPolicy(final IntDomainVar[] vars, final int[] coeffs, final int nbPosVars, final int cste) {
        this.vars = vars;
        this.coeffs = coeffs;
        this.nbPosVars = nbPosVars;
        this.cste = cste;
    }

    public abstract int getInfNV(final int i, final int mylb);

    public abstract int getSupNV(final int i, final int myub);

    public abstract int getInfPV(final int i, final int myub);

    public abstract int getSupPV(final int i, final int mylb);

    /**
     * Computes an upper bound estimate of a linear combination of variables.
     *
     * @return the new upper bound value
     */
    public abstract int computeUpperBound();

    /**
     * Computes a lower bound estimate of a linear combination of variables.
     *
     * @return the new lower bound value
     */
    public abstract int computeLowerBound();
}
