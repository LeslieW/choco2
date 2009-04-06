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
package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * The notequal can be optimized when we know that the variable are
 * enumerated (in that case no need to do anything on the bound modifications) 
 **/
public class NotEqualXYCEnum extends NotEqualXYC {


  /**
   * Constructs the constraint with the specified variables and constant.
   *
   * @param x0 first IntDomainVar
   * @param x1 second IntDomainVar
   * @param c  The search constant used in the disequality.
   */

  public NotEqualXYCEnum(IntDomainVar x0, IntDomainVar x1, int c) {
    super(x0, x1,c);
  }

  public int getFilteredEventMask(int idx) {
    return IntVarEvent.INSTINTbitvector;
    // return 0x0B;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * The one and only propagation method, using foward checking
   */

  public void propagate() throws ContradictionException {
    if (v0.isInstantiated()) {
      v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
    } else if (v1.isInstantiated()) {
      v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {

  }

  public void awakeOnSup(int idx) throws ContradictionException {

  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
      v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
    } else {
      v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
    }
  }


  /**
   * Nothing to do when only a hole in a domain is made
   */

  public void awakeOnRem(int idx, int x) throws ContradictionException {

  }


}
