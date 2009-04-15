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
package choco.kernel.solver.propagation;

import choco.kernel.solver.Solver;

/**
 * An abstract class for all implementations of propagation engines.
 */
public abstract class AbstractPropagationEngine implements PropagationEngine {

    public Solver solver;

        /**
   * Retrieves the solver of the entity.
   */

  public Solver getSolver(){
            return solver;
        }


  public void setSolver(Solver solver){
      this.solver = solver;
  }

  /**
   * Storing the cause of the last contradiction.
   */

  protected Object contradictionCause;



  public AbstractPropagationEngine(Solver solver) {
    this.solver = solver;
  }

  /**
   * Erase the cause of the last contradiction.
   */

  public void setNoContradictionCause() {
    contradictionCause = null;
  }



    /**
   * Retrieving the cause of the last contradiction.
   */

  public Object getContradictionCause() {
    return contradictionCause;
  }

  /**
   * Gets the next queue from which a var will be propagated.
   */

  public EventQueue getNextActiveEventQueue() {
    return null;
  }
}
