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

import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 11 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class ForScalar extends CoeffPolicy {

    static CoeffPolicy get(final IntDomainVar[] vars, final int[] coeffs, final int nbPosVars, final int cste) {
        return new ForScalar(vars, coeffs, nbPosVars, cste);
    }

    private ForScalar(final IntDomainVar[] vars, final int[] coeffs, final int nbPosVars, final int cste) {
        super(vars, coeffs, nbPosVars, cste);
    }

    @Override
    public int getInfNV(final int i, final int mylb) {
        return MathUtils.divCeil(mylb, -coeffs[i]) + vars[i].getSup();
    }

    @Override
    public int getSupNV(final int i, final int myub) {
        return MathUtils.divFloor(myub, -coeffs[i]) + vars[i].getInf();
    }

    @Override
    public int getInfPV(final int i, final int myub) {
        return MathUtils.divCeil(-myub, coeffs[i]) + vars[i].getSup();
    }

    @Override
    public int getSupPV(final int i, final int mylb) {
        return MathUtils.divFloor(-mylb, coeffs[i]) + vars[i].getInf();
    }

    @Override
    public int computeLowerBound() {
        int lb = cste;
        for (int i = 0; i < nbPosVars; i++) {
            lb += coeffs[i] * vars[i].getInf();
        }
        for (int i = nbPosVars; i < vars.length; i++) {
            lb += coeffs[i] * vars[i].getSup();
        }
        return lb;
    }


    @Override
    public int computeUpperBound() {
        int ub = cste;
        for (int i = 0; i < nbPosVars; i++) {
            ub += coeffs[i] * vars[i].getSup();
        }
        for (int i = nbPosVars; i < vars.length; i++) {
            ub += coeffs[i] * vars[i].getInf();
        }
        return ub;
    }

}
