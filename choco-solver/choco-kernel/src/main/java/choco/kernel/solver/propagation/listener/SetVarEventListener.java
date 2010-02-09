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
package choco.kernel.solver.propagation.listener;

import choco.kernel.solver.ContradictionException;

/*
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Since : Choco 2.0.0
 *
 */
public interface SetVarEventListener extends VarEventListener {
  /**
   * Default propagation on kernel modification: propagation on adding a value to the kernel.
   */
  public void awakeOnKer(int varIdx, int x) throws ContradictionException;


  /**
   * Default propagation on enveloppe modification: propagation on removing a value from the enveloppe.
   */
  public void awakeOnEnv(int varIdx, int x) throws ContradictionException;


  /**
   * Default propagation on instantiation.
   */
  public void awakeOnInst(int varIdx) throws ContradictionException;

}
