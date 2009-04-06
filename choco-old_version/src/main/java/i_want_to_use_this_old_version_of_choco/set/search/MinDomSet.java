package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class MinDomSet extends AbstractSetVarSelector {

  public MinDomSet(AbstractProblem pb, SetVar[] decisionvs) {
    vars = decisionvs;
    problem = pb;
  }

  public MinDomSet(AbstractProblem pb) {
    problem = pb;
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
      int n = problem.getNbSetVars();
      for (int i = 0; i < n; i++) {
        SetVar v = problem.getSetVar(i);
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
