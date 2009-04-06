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
package samples.wiki;

public class MinSizeMinLB{ //extends AbstractIntVarSelector {


//    public MinSizeMinLB(Model pb, IntDomainVar[] vs) {
//        model = pb;
//        vars = vs;
//    }
//
//
//    public IntDomainVar selectIntVar() throws ContradictionException {
//        int minSize = IStateInt.MAXINT;
//        int minValue = IStateInt.MAXINT;
//        IntDomainVar v0 = null;
//        for (IntDomainVar v : vars) {
//            if (!v.isInstantiated()) {
//                int sizev = v.getDomainSize();
//                if (sizev <= minSize) {
//                    int infv = v.getInf();
//                    if (infv <= minValue) {
//                        minValue = infv;
//                        minSize = sizev;
//                        v0 = v;
//                    }
//                }
//            }
//        }
//        return v0;
//    }
}
