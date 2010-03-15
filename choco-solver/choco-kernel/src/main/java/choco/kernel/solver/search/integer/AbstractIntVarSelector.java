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
package choco.kernel.solver.search.integer;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractIntVarSelector extends AbstractSearchHeuristic implements IntVarSelector {

  /**
   * a specific array of IntVars from which the object seeks the one with smallest domain
   */
  protected IntDomainVar[] vars;
  
  /**
   * the IVarSelector can be asked to return a variable
   *
   * @return a variable on whose domain an alternative can be set (such as a non instantiated search variable)
   */
  public AbstractVar selectVar() throws ContradictionException {
    return (AbstractVar) selectIntVar();
  }

    /**
     * Get decision vars
     * @return decision vars
     */
    public IntDomainVar[] getVars() {
        return vars;
    }


    /**
     * Set decision vars
     * @return decision vars
     */
    public void setVars(IntDomainVar[] vars) {
        this.vars = vars;
    }
}
