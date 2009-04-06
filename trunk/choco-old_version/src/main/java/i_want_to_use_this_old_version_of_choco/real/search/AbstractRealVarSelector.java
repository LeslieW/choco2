package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractSearchHeuristic;

/**
 * An interface for real variable selector during a braching assigning intervals.
 */
public abstract class AbstractRealVarSelector extends AbstractSearchHeuristic implements RealVarSelector {
  public AbstractVar selectVar() {
    return (AbstractVar) selectRealVar();
  }
}
