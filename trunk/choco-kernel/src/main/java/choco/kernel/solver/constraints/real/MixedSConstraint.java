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
package choco.kernel.solver.constraints.real;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.RealVarEventListener;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;

/**
 * An interface for mixed constraint : interger and flot variables.
 */
public interface MixedSConstraint extends SConstraint, Propagator,
        RealVarEventListener, IntSConstraint {
  /**
   * Returns the real variable with index i.
   * @param i the variable index
   * @return the variable with index i
   */
  RealVar getRealVar(int i);

  /**
   * Returns the number of real variables. Note that here the number of 
   * variables should equal the number of real variables plus the number
   * of integer variables.
   * @return the number of <i>real</i> variables.
   */
  int getRealVarNb();

  /**
   * Returns the integer variable with index i.
   * @param i the variable index
   * @return the variable with index i
   */
  IntDomainVar getIntVar(int i);

  /**
   * Returns the number of integer variables. Note that here the number of 
   * variables should equal the number of real variables plus the number
   * of integer variables.
   * @return the number of <i>integer</i> variables.
   */  
  int getIntVarNb();
}
