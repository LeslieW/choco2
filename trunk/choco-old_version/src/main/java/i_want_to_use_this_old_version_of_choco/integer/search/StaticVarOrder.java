// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * A variable selector selecting the first non instantiated variable according to a given static order
 */
public class StaticVarOrder extends AbstractIntVarSelector {

  public StaticVarOrder(IntDomainVar[] vars) {
    this.vars = vars;
  }

  public IntDomainVar selectIntVar() {
    for (int i = 0; i < vars.length; i++) {
      if (!vars[i].isInstantiated()) {
        return vars[i];
      }
    }
    return null;
  }
}
