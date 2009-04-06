// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;

import java.util.ArrayList;
import java.util.Random;

public class RandomIntVarSelector extends AbstractIntVarSelector implements IntVarSelector {
  protected ArrayList list = new ArrayList();
  protected Random random;

  /**
   * Creates a new random-based integer domain variable selector
   */
  public RandomIntVarSelector(AbstractProblem pb) {
    this.problem = pb;
    this.random = new Random();
  }


  public RandomIntVarSelector(AbstractProblem pb, IntDomainVar[] vs, long seed) {
    problem = pb;
    vars = vs;
    this.random = new Random(seed);
  }

  /**
   * Creates a new random-based integer domain variable selector with the specified seed
   * (to make the experiment determinist)
   */
  public RandomIntVarSelector(AbstractProblem pb, long seed) {
    this.problem = pb;
    this.random = new Random(seed);
  }


  public IntDomainVar selectIntVar() {
    // list supposed cleared !
    if (vars == null) {
      for (int i = 0; i < problem.getNbIntVars(); i++) {
        IntDomainVar v = (IntDomainVar) problem.getIntVar(i);
        if (!v.isInstantiated()) {
          list.add(v);
        }
      }
    } else {
      for (int i = 0; i < vars.length; i++) {
        IntDomainVar v = vars[i];
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
