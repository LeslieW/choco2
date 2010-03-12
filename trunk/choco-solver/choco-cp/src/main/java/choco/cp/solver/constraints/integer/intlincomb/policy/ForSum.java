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
public final class ForSum extends CoeffPolicy {

    static CoeffPolicy get(final IntDomainVar[] vars, final int[] coeffs, final int nbPosVars, final int cste) {
        return new ForSum(vars, coeffs, nbPosVars, cste);
    }

    private ForSum(final IntDomainVar[] vars, final int[] coeffs, final int nbPosVars, final int cste) {
        super(vars, coeffs, nbPosVars, cste);
    }

    @Override
    public int getInfNV(int i, int mylb) {
        return vars[i].getSup() + mylb;
    }

    @Override
    public int getInfPV(int i, int myub) {
        return vars[i].getSup() - myub;
    }

    @Override
    public int getSupNV(int i, int myub) {
        return vars[i].getInf() + myub;
    }

    @Override
    public int getSupPV(int i, int mylb) {
        return vars[i].getInf() - mylb;
    }

    @Override
    public int computeLowerBound() {
        int lb = cste;
        for (int i = 0; i < nbPosVars; i++) {
            lb += vars[i].getInf();
        }
        for (int i = nbPosVars; i < vars.length; i++) {
            lb -= vars[i].getSup();
        }
        return lb;
    }


    @Override
    public int computeUpperBound() {
        int ub = cste;
        for (int i = 0; i < nbPosVars; i++) {
            ub += vars[i].getSup();
        }
        for (int i = nbPosVars; i < vars.length; i++) {
            ub -= vars[i].getInf();
        }
        return ub;
    }
}
