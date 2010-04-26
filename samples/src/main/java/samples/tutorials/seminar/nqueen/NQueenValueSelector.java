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
package samples.tutorials.seminar.nqueen;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;


/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 28 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class NQueenValueSelector implements ValSelector<IntDomainVar> {

    // Column variable
    protected IntDomainVar[] dualVar;

    // Constructor of the value selector,
    public NQueenValueSelector(IntDomainVar[] cols) {
       this.dualVar = cols;
    }

    // Returns the "best val" that is:
   // the smallest column domain size
   //  OR -1 (a value that is not in the domain of the variable)
    public int getBestVal(IntDomainVar intDomainVar) {
        int minValue = 10000;
        int v0 = -1;
        DisposableIntIterator it = intDomainVar.getDomain().getIterator();
        while (it.hasNext()){
            int i = it.next();
            int val = dualVar[i - 1].getDomainSize();
            if (val < minValue)  {
                minValue = val;
                v0 = i;
             }
        }
        it.dispose();
        return v0;
    }

}
