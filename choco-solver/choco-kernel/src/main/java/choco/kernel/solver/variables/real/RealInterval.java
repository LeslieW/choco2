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
package choco.kernel.solver.variables.real;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.Domain;

/**
 * An interface for real intervals.
 */
public interface RealInterval extends Domain {
  /**
   * @return the lower bound.
   */
  public double getInf();

  /**
   * @return the upper bound.
   */
  public double getSup();

  /**
   * Modifies the bounds for intersecting with the specified interval.
   *
   * @param interval
   * @throws ContradictionException
   */
  public void intersect(RealInterval interval) throws ContradictionException;

 }
