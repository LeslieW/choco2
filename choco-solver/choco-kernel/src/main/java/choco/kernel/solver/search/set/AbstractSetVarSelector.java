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
package choco.kernel.solver.search.set;

import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractSetVarSelector extends AbstractSearchHeuristic implements SetVarSelector {

  /**
   * a specific array of SetVars from which the object seeks the one with smallest domain
   */
  protected SetVar[] vars;

  public AbstractVar selectVar() {
    return (AbstractVar) selectSetVar();
  }

    /**
     * Get decision vars
     * @return decision vars
     */
    public SetVar[] getVars() {
        return vars;
    }

    /**
     * Set decision vars
     * @return decision vars
     */
    public void setVars(SetVar[] vars) {
        this.vars = vars;
    }
}
