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
package choco.cp.solver.search.set;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.set.AbstractSetVarSelector;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class MinDomSet extends AbstractSetVarSelector {

  public MinDomSet(Solver solver, SetVar[] decisionvs) {
    vars = decisionvs;
    this.solver = solver;
  }

  public MinDomSet(Solver solver) {
    this.solver = solver;
  }

  public SetVar selectSetVar() {
    int minDomSize = Integer.MAX_VALUE;
    SetVar v0 = null;
    if (null != vars) {
      int n = vars.length;
      for (int i = 0; i < n; i++) {
        SetVar v = vars[i];
        if (!v.isInstantiated()) {
          int domSize = v.getEnveloppeDomainSize() - v.getKernelDomainSize();
          if (domSize < minDomSize) {
            minDomSize = domSize;
            v0 = v;
          }
        }
      }
    } else {
      int n = solver.getNbSetVars();
      for (int i = 0; i < n; i++) {
        SetVar v = solver.getSetVar(i);
        if (!v.isInstantiated()) {
          int domSize = v.getEnveloppeDomainSize() - v.getKernelDomainSize();
          if (domSize < minDomSize) {
            minDomSize = domSize;
            v0 = v;
          }
        }
      }
    }
    return v0;
  }


}
