package i_want_to_use_this_old_version_of_choco.real;

import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.real.var.RealDomain;

/**
 * An interface for a real variable : an expression and a variable with a domain.
 */
public interface RealVar extends Var, RealExp {
  public RealDomain getDomain();

  /**
   * Modifies bounds silently (does not propagate modifications). This is usefull for box cosistency.
   *
   * @param i
   */
  void silentlyAssign(RealInterval i);

  public RealInterval getValue();
}
