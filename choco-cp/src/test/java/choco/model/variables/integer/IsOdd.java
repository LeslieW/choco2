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

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractUnIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 13 juin 2008
 * Time: 14:06:50
 * To change this template use File | Settings | File Templates.
 */
public class IsOdd extends AbstractUnIntSConstraint {

    public static class IsOddManager extends IntConstraintManager {
        public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
            if(solver instanceof CPSolver){
                return new IsOdd(solver.getVar((IntegerVariable)variables[0]));
            }
            return null;
        }
    }

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
