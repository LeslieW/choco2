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

import choco.IObservable;
import choco.IObserver;
import choco.kernel.solver.propagation.event.PropagationEvent;

import java.util.Vector;

public class ObservableVarEventQueue extends BasicVarEventQueue implements IObservable{

    private Vector obs;

    public ObservableVarEventQueue() {
        this.obs = new Vector();
    }

    /**
     * Clear datastructures for safe reuses
     */
    @Override
    public void clear() {
        super.clear();
        this.obs.clear();
    }

    /**
   * Pops an event to propagate.
   */
  public PropagationEvent popEvent() {
    PropagationEvent event = super.popEvent();
     notifyObservers(event);
      return event;
  }


  /**
   * Adds an event to the queue.
   */

  public boolean pushEvent(PropagationEvent event) {
      notifyObservers(event);
      return super.pushEvent(event);
  }


  /**
   * Removes all the events (including the popping one).
   */

  public void flushEventQueue() {
    super.flushEventQueue();
    notifyObservers(1);
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