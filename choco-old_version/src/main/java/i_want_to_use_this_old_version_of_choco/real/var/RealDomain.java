package i_want_to_use_this_old_version_of_choco.real.var;

import i_want_to_use_this_old_version_of_choco.Entity;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;

/**
 * An interface for real variable domains.
 */
public interface RealDomain extends Entity, RealInterval {
  public void clearDeltaDomain();

  boolean releaseDeltaDomain();

  void freezeDeltaDomain();

  boolean getReleasedDeltaDomain();

  void silentlyAssign(RealInterval i);
}
