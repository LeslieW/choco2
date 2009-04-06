package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * An interface for control objects that model a heuristic selectying at the same time
 * a variable and a value from its domain
 */
public interface VarValPairSelector {
  IntVarValPair selectVarValPair() throws ContradictionException;
}
