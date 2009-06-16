/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.strong;

import choco.cp.solver.constraints.strong.maxrpcrm.MaxRPCrm;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.search.integer.DoubleHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.common.util.IntIterator;

import java.util.Iterator;

/**
 * History: 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 */
public class DomOverWDegRPC extends DoubleHeuristicIntVarSelector implements
        PropagationEngineListener {

    private static final int ABSTRACTCONTRAINT_EXTENSION = AbstractSConstraint
            .getAbstractSConstraintExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

    protected final static class DomOverWDegConstraintExtension {
        private int nbFailure = 0;
    }

    public DomOverWDegRPC(Solver solver) {
        super(solver);
        for (Iterator<SConstraint> iter = solver.getIntConstraintIterator(); iter
                .hasNext();) {
            ((AbstractSConstraint) iter.next()).setExtension(
                    ABSTRACTCONTRAINT_EXTENSION,
                    new DomOverWDegConstraintExtension());
        }
        solver.getPropagationEngine().addPropagationEngineListener(this);
    }

    public DomOverWDegRPC(Solver solver, IntDomainVar[] vs) {
        super(solver);
        vars = vs;
        for (Iterator<SConstraint> iter = solver.getIntConstraintIterator(); iter
                .hasNext();) {
            ((AbstractSConstraint) iter.next()).setExtension(
                    ABSTRACTCONTRAINT_EXTENSION,
                    new DomOverWDegConstraintExtension());
        }
        solver.getPropagationEngine().addPropagationEngineListener(this);
    }

    /**
     * Define action to do just before a deletion.
     */
    @Override
    public void safeDelete() {
        solver.getPropagationEngine().removePropagationEngineListener(this);
    }

    public double getHeuristic(IntDomainVar v) {
        int dsize = v.getDomainSize();
        int weight = 0;
        int idx = 0;
        for (IntIterator it = v.getIndexVector().getIndexIterator(); it
                .hasNext();) {
            idx = it.next();
            SConstraint ct = v.getConstraint(idx);
            if (ct instanceof MaxRPCrm) {
                weight += ((MaxRPCrm) ct).getWDeg(v);
            } else {
                AbstractSConstraint cstr = (AbstractSConstraint) ct;
                if (cstr.getNbVarNotInst() > 1) {
                    weight += ((DomOverWDegConstraintExtension) cstr
                            .getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure
                            + ct.getFineDegree(v.getVarIndex(idx));
                }
            }
        }
        if (weight == 0) {
            return Double.MAX_VALUE;
        } else {
            return (double) dsize / ((double) weight);
        }
    }

    public void contradictionOccured(ContradictionException e) {
        Object cause = e.getContradictionCause();
        if (cause != null && cause instanceof AbstractSConstraint) {
            AbstractSConstraint c = (AbstractSConstraint) cause;
            ((DomOverWDegConstraintExtension) c
                    .getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure++;
        }
    }
}
