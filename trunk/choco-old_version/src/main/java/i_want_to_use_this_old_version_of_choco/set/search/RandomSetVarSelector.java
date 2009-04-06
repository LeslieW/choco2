// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.set.var.SetVarImpl;

import java.util.ArrayList;
import java.util.Random;

public class RandomSetVarSelector extends AbstractSetVarSelector implements SetVarSelector {
  protected ArrayList list = new ArrayList();
  protected Random random;

  /**
   * Creates a new random-based set domain variable selector
   * @param pb the associated problem
   */
  public RandomSetVarSelector(AbstractProblem pb) {
    this.problem = pb;
    this.random = new Random();
  }


    /**
     * Creates a new random-based set domain variable selector with a specified seed
     * @param pb problem
     * @param vs SetVar array
     * @param seed specified seed
     */
  public RandomSetVarSelector(AbstractProblem pb, SetVar[] vs, long seed) {
    problem = pb;
    vars = vs;
    this.random = new Random(seed);
  }

  /**
   * Creates a new random-based set domain variable selector with the specified seed
   * (to make the experiment determinist)
   * @param pb problem
   * @param seed the specified seed
   */
  public RandomSetVarSelector(AbstractProblem pb, long seed) {
    this.problem = pb;
    this.random = new Random(seed);
  }


  public SetVar selectSetVar() {
    // list supposed cleared !
    if (vars == null) {
      for (int i = 0; i < problem.getNbSetVars(); i++) {
        SetVar v = problem.getSetVar(i);
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
}