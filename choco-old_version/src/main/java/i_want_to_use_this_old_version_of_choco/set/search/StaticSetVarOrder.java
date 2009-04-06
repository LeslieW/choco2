package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class StaticSetVarOrder extends AbstractSetVarSelector {

  public StaticSetVarOrder(SetVar[] vars) {
    this.vars = vars;
  }

  public SetVar selectSetVar() {
    for (int i = 0; i < vars.length; i++) {
      if (!vars[i].isInstantiated()) {
        return vars[i];
      }
    }
    return null;
  }

}
