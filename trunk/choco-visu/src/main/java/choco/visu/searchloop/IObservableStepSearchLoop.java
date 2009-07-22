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
package choco.visu.searchloop;

import choco.IObservable;
import choco.kernel.solver.search.ISearchLoop;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 12 nov. 2008
 * Since : Choco 2.0.1
 */

public interface IObservableStepSearchLoop extends ISearchLoop, IObservable{

    static enum Step{
        PAUSE, NEXT, PLAY
    }

    /**
     * Action to do in a step-by-step run loop
     */
    public void runStepByStep();

    /**
     * Action to do in a normal run loop
     */
    public void runForAWhile();

    /**
     * Pause the normal run loop
     */
    public void pause();

    public void setAction(final Step action);
}
