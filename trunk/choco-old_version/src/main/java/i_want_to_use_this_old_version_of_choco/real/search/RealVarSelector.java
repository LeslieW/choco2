package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * An interface for selecting a real interval variable to narrow.
 */
public interface RealVarSelector extends VarSelector {
  public RealVar selectRealVar();
}
