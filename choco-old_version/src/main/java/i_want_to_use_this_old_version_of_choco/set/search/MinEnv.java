package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.search.AbstractSearchHeuristic;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class MinEnv extends AbstractSearchHeuristic implements SetValSelector {

  public MinEnv(AbstractProblem pb) {
    problem = pb;
  }

  public int getBestVal(SetVar v) {
    int val = Integer.MIN_VALUE;
    IntIterator it = v.getDomain().getEnveloppeIterator();
    while (it.hasNext()) {
      val = it.next();
      if (!v.isInDomainKernel(val)) break;
    }
    return val;
  }


}
