package choco.ecp.solver.constraints.real;

import choco.ecp.solver.propagation.PalmIntVarListener;
import choco.ecp.solver.propagation.PalmRealVarListener;
import choco.kernel.solver.constraints.real.MixedSConstraint;

/**
 * Mixed constraint: such constraints handle both integer
 * and real variables (and their events).
 */
public interface PalmMixedConstraint extends MixedSConstraint,
        PalmRealVarListener, PalmIntVarListener {
}
