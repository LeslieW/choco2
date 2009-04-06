package i_want_to_use_this_old_version_of_choco.palm.real.constraints;

import i_want_to_use_this_old_version_of_choco.palm.integer.PalmIntVarListener;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVarListener;
import i_want_to_use_this_old_version_of_choco.real.constraint.MixedConstraint;

/**
 * Mixed constraint: such constraints handle both integer
 * and real variables (and their events).
 */
public interface PalmMixedConstraint extends MixedConstraint,
    PalmRealVarListener, PalmIntVarListener {
}
