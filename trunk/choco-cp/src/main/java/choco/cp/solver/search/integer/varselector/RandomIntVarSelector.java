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
package choco.cp.solver.search.integer.varselector;

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.search.integer.IntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;

import java.util.ArrayList;
import java.util.Random;

public class RandomIntVarSelector extends AbstractIntVarSelector implements IntVarSelector {
  protected ArrayList<IntVar> list = new ArrayList<IntVar>();
  protected Random random;

  /**
   * Creates a new random-based integer domain variable selector
   */
  public RandomIntVarSelector(Solver solver) {
    this.solver = solver;
    this.random = new Random();
  }


  public RandomIntVarSelector(Solver solver, IntDomainVar[] vs, long seed) {
    this.solver = solver;
    vars = vs;
    this.random = new Random(seed);
  }

  /**
   * Creates a new random-based integer domain variable selector with the specified seed
   * (to make the experiment determinist)
   */
  public RandomIntVarSelector(Solver solver, long seed) {
    this.solver = solver;
    this.random = new Random(seed);
  }


  public IntDomainVar selectIntVar() {
    // list supposed cleared !
    if (vars == null) {
      for (int i = 0; i < solver.getNbIntVars(); i++) {
        IntVar v = solver.getIntVar(i);
        if (!v.isInstantiated()) {
          list.add(v);
        }
      }
    } else {
        for (IntDomainVar v : vars) {
            if (!v.isInstantiated()) {
                list.add(v);
            }
        }
    }
    IntDomainVarImpl ret = null;
    if (list.size() > 0) ret = (IntDomainVarImpl) list.get(random.nextInt(list.size()));
    list.clear();
    return ret;
  }
}
