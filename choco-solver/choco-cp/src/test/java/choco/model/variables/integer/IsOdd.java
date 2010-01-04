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
package choco.model.variables.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractUnIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 13 juin 2008
 * Time: 14:06:50
 * To change this template use File | Settings | File Templates.
 */
//totex isodd
public class IsOdd extends AbstractUnIntSConstraint {

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

        public IsOdd(IntDomainVar v0) {
            super(v0);
        }

        /**
         * Default initial propagation: full constraint re-propagation.
         */

        public void awake() throws ContradictionException {
            DisposableIntIterator it = v0.getDomain().getIterator();
            try{
                while(it.hasNext()){
                    int val = it.next();
                    if(val%2==0){
                        v0.removeVal(val, cIdx0);
                    }
                }
            }finally {
                it.dispose();
            }
        }

        /**
         * <i>Propagation:</i>
         * Propagating the constraint until local consistency is reached.
         *
         * @throws ContradictionException
         *          contradiction exception
         */

        public void propagate() throws ContradictionException {
            if(v0.isInstantiated()){
                if(v0.getVal()%2==0){
                    fail();
                }
            }
        }


    }
//totex

