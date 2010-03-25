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
package choco.cp.solver;

import choco.cp.solver.propagation.BlockingVarEventQueue;
import choco.kernel.solver.propagation.queue.VarEventQueue;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 4 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class CPSolverDis extends CPSolver{

    public CPSolverDis() {
        super();
        VarEventQueue[] qs = new BlockingVarEventQueue[3];
        for(int i = 0; i < 3; i++){
            qs[i] = new BlockingVarEventQueue();
        }
        this.getPropagationEngine().setVarEventQueues(qs);
    }
}
