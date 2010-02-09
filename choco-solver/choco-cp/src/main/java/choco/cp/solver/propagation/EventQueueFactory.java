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
package choco.cp.solver.propagation;

import choco.kernel.solver.propagation.queue.VarEventQueue;

/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 oct. 2008
 */

public class EventQueueFactory {

    public static final int BASIC = 0;
    public static final int OBSERVABLE = 1;


    /**
     * Return the VarEventQueue defined by {@code type}
     * @return {@code VarEventQueue} object
     */
    public static VarEventQueue getVarEventQueue(final int type) {
        switch (type) {
            case OBSERVABLE:
                return new ObservableVarEventQueue();
            default:
                return new BasicVarEventQueue();
        }
    }


    /**
     * Return the common VarEventQueue
     *
     * @return {@code BasicVarEventQueue}
     */
    public static VarEventQueue getVarEventQueue() {
        return new BasicVarEventQueue();
    }

    /**
     * Return the observable VarEventQueue
     *
     * @return {@code ObservableVarEventQueue}
     */
    public static VarEventQueue getObservableVarEventQueue() {
        return new ObservableVarEventQueue();
    }
}
