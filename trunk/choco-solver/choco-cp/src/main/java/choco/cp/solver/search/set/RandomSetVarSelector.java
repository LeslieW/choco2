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

import choco.cp.solver.variables.set.SetVarImpl;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.set.AbstractSetVarSelector;
import choco.kernel.solver.search.set.SetVarSelector;
import choco.kernel.solver.variables.set.SetVar;

import java.util.ArrayList;
import java.util.Random;

public class RandomSetVarSelector extends AbstractSetVarSelector implements SetVarSelector {
  protected ArrayList<SetVar> list = new ArrayList<SetVar>();
  protected Random random;

  /**
   * Creates a new random-based set domain variable selector
   * @param solver the associated model
   */
  public RandomSetVarSelector(Solver solver) {
    super(solver);
    this.random = new Random();
  }


    /**
     * Creates a new random-based set domain variable selector with a specified seed
     * @param solver model
     * @param vs SetVar array
     * @param seed specified seed
     */
  public RandomSetVarSelector(Solver solver, SetVar[] vs, long seed) {
   super(solver, vs);
    this.random = new Random(seed);
  }

  /**
   * Creates a new random-based set domain variable selector with the specified seed
   * (to make the experiment determinist)
   * @param solver model
   * @param seed the specified seed
   */
  public RandomSetVarSelector(Solver solver, long seed) {
    super(solver);
    this.random = new Random(seed);
  }


    @Override
  public SetVar selectSetVar() {
    // list supposed cleared !
    if (vars == null) {
      for (int i = 0; i < solver.getNbSetVars(); i++) {
        SetVar v = solver.getSetVar(i);
        if (!v.isInstantiated()) {
          list.add(v);
        }
      }
    } else {
        for (SetVar v : vars) {
            if (!v.isInstantiated()) {
                list.add(v);
            }
        }
    }
    SetVarImpl ret = null;
    if (list.size() > 0) ret = (SetVarImpl) list.get(random.nextInt(list.size()));
    list.clear();
      return ret;
  }

    @Override
    public int getHeuristic(SetVar v) {
        return random.nextInt();
    }
}