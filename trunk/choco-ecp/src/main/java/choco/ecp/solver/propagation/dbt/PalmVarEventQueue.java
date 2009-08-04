//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.propagation.dbt;

import choco.cp.solver.propagation.BasicVarEventQueue;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.propagation.PropagationEvent;

import java.util.Iterator;
import java.util.Queue;

public class PalmVarEventQueue extends BasicVarEventQueue {
  /**
   * Reset the last event if a contradiction occured when it was handled.
   */

  public void resetPopping() {
    if ((this.lastPopped != null) && ((PalmVarEvent) this.lastPopped).isPopping()) {
      ((PalmVarEvent) this.lastPopped).setPopping(false);
      ((PalmVarEvent) this.lastPopped).reset();
      if (!this.queue.contains(this.lastPopped)) {
        this.queue.add(this.lastPopped);
      }
    }

  }


  /**
   * Resets all the events of the queue.
   */

  public void reset() {
    this.resetPopping();
    /*for (Iterator iterator = queue.iterator(); iterator.hasNext();) {
      PalmIntVarEvent event = (PalmIntVarEvent) iterator.next();
      //event.reset();
    } */
  }


  /**
   * Updates variable explanations.
   */

  public void restoreVariableExplanations() {
    for (Iterator iterator = queue.iterator(); iterator.hasNext();) {
      PalmVarEvent event = (PalmVarEvent) iterator.next();
      event.restoreVariableExplanation();
    }
  }

  public void assertValidQueue() {
    // TODO : TESTS
    Iterator it = this.getQueue().iterator();
    while (it.hasNext()) {
      PalmIntVarEvent evt = (PalmIntVarEvent) it.next();
      assert(evt.getEventType() != IntVarEvent.EMPTYEVENT);
    }
  }

  // TODO : dans Choco ?
  public boolean contains(Object obj) {
    return this.queue.contains(obj);
  }

  public Queue<PropagationEvent> getQueue() {
    return queue;
  }
}
