package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * An interface for float constraints.
 */
public interface RealConstraint extends Constraint, Propagator, RealListener {
  public RealVar getRealVar(int i);

  public int getRealVarNb();
}
