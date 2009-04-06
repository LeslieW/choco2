package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.List;

/**
 * A class that applies two heuristics lexicographically for selecting a variable:
 *   a first heuristic is applied finding the best constraint
 *   ties are broken with the second heuristic
 */
public class LexIntVarSelector extends AbstractIntVarSelector {
  HeuristicIntVarSelector h1;
  HeuristicIntVarSelector h2;

  public LexIntVarSelector(HeuristicIntVarSelector h1, HeuristicIntVarSelector h2) {
    this.h1 = h1;
    this.h2 = h2;
  }

  public IntDomainVar selectIntVar() throws ContradictionException {
    List<IntDomainVar> ties = h1.selectTiedIntVars();
    switch (ties.size()) {
      case 0: return null;
      case 1: return ties.get(0);
      default: return h2.getMinVar(ties);
    }
  }
}
