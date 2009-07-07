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

import choco.IObserver;
import choco.cp.solver.search.SearchLoopWithRestart;
import choco.cp.solver.search.restart.RestartStrategy;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;

import java.util.Vector;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 6 nov. 2008
 * Since : Choco 2.0.1
 */

public class ObservableStepSearchLoopWithRestart extends SearchLoopWithRestart implements IObservableStepSearchLoop {

    private Vector obs;
    private boolean run = false;
    private Step action = Step.PAUSE; // 0: pause; 1: next; 2: play
    //for tree search vizualisation
    public State state;

    public ObservableStepSearchLoopWithRestart(AbstractGlobalSearchStrategy searchStrategy, RestartStrategy restartStrategy) {
        super(searchStrategy, restartStrategy);
        obs = new Vector();
    }

    public Boolean run() {
        int previousNbSolutions = searchStrategy.getSolutionCount();
        searchStrategy.setEncounteredLimit(null);
        init();
        boolean restartLimit;
        do {
            restartLimit = false;
            while (!stop) {
                state = State.NONE;
                //if action is pause, wait
                while (action.equals(Step.PAUSE)) {/*wait*/}
                // if action is next, pause after one execution
                if (action.equals(Step.NEXT)) {
                    action = Step.PAUSE;
                }
                while (!stop) {
                    if (checkRestartMoveMask(searchStrategy.nextMove) &&
                            restartStrategy.shouldRestart(searchStrategy)) {
                        LOGGER.finest("=== restarting ...");
                        stop = restart(ctx);
                        if (!stop) {
                            restartLimit = true;
                        }
                        break;
                    }
                    switch (searchStrategy.nextMove) {
                        case AbstractGlobalSearchStrategy.OPEN_NODE: {
                            openNode();
                            if (action.equals(Step.PAUSE)) {
                                action = Step.NEXT;
                            }
                            break;
                        }
                        case AbstractGlobalSearchStrategy.UP_BRANCH: {
                            upBranch();
                            break;
                        }
                        case AbstractGlobalSearchStrategy.DOWN_BRANCH: {
                            downBranch();
                            break;
                        }
                        default:
                            if (run) {
                            }
                            break;
                    }
                }
            }
        } while (restartLimit);
        state = State.END;
        notifyObservers(this);
        searchStrategy.resetLimits(false);
        if (searchStrategy.getSolutionCount() > previousNbSolutions) {
            return Boolean.TRUE;
        } else if (searchStrategy.isEncounteredLimit()) {
            return null;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * perform the restart.
     *
     * @param ctx the branching trace
     * @return <code>true</code> if the loop should stop
     */
    protected boolean restart(IntBranchingTrace ctx) {
        state = State.RESTART;
        notifyObservers(this);
        return super.restart(ctx);
    }

    public void openNode() {
        super.openNode();
        if(searchStrategy.nextMove == AbstractGlobalSearchStrategy.UP_BRANCH
                && stop){
                state = State.SOLUTION;
        }
        notifyObservers(this);
    }

    public void upBranch() {
        super.upBranch();
        state = State.UP;
        notifyObservers(this);
    }

    public void downBranch() {
        state = State.DOWN;
        super.downBranch();
        notifyObservers(this);
    }


    public IntBranchingTrace getCtx(){
        return this.ctx;
    }

    ////////////////////////////////////////TO INTERACT WITH GUI////////////////////////////////////////////////////////

    public void runStepByStep(){
        action = Step.NEXT;
    }

    public void runForAWhile(){
        action = Step.PLAY;
    }

    public void pause(){
        action = Step.PAUSE;
    }

    public void setAction(Step action) {
        this.action = action;
    }

    ////////////////////////////////IObservable implementation////////////////////////////

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    public synchronized void addObserver(IObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p/>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param arg any object.
     * @see choco.IObserver#update(choco.IObservable , Object)
     */
    public void notifyObservers(Object arg) {
	/*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

	synchronized (this) {
	    /* We don't want the Observer doing callbacks into
	     * arbitrary code while holding its own Monitor.
	     * The code where we extract each Observable from
	     * the Vector and store the state of the Observer
	     * needs synchronization, but notifying observers
	     * does not (should not).  The worst result of any
	     * potential race-condition here is that:
	     * 1) a newly-added Observer will miss a
	     *   notification in progress
	     * 2) a recently unregistered Observer will be
	     *   wrongly notified when it doesn't care
	     */
            arrLocal = obs.toArray();
        }

        for (int i = arrLocal.length-1; i>=0; i--)
            ((IObserver)arrLocal[i]).update(this, arg);
    }
}