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
package choco.cp.solver.search.integer.varselector;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.search.integer.DoubleHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Iterator;

/**
 * History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 */
public class DomOverWDeg extends DoubleHeuristicIntVarSelector implements PropagationEngineListener {

    private AbstractSConstraint reuseCstr;

	private static final int ABSTRACTCONTRAINT_EXTENSION =
			AbstractSConstraint.getAbstractSConstraintExtensionNumber("choco.cp.cpsolver.search.integer.varselector.DomOverWDeg");

    protected static final class DomOverWDegConstraintExtension {
		private int nbFailure = 0;
	}

	public DomOverWDeg(Solver solver) {
		super(solver);
		for (Iterator iter = solver.getIntConstraintIterator(); iter.hasNext();) {
			AbstractSConstraint c = (AbstractSConstraint) iter.next();
			c.setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegConstraintExtension());
		}
		solver.getPropagationEngine().addPropagationEngineListener(this);
	}

	public DomOverWDeg(Solver solver, IntDomainVar[] vs) {
		super(solver);
		vars = vs;
		for (Iterator iter = solver.getIntConstraintIterator(); iter.hasNext();) {
			AbstractSConstraint c = (AbstractSConstraint) iter.next();
			c.setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegConstraintExtension());
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

    public void initConstraintForBranching(SConstraint c) {
        ((AbstractSConstraint) c).setExtension(ABSTRACTCONTRAINT_EXTENSION, new DomOverWDegConstraintExtension());        
    }

    public double getHeuristic(IntDomainVar v) {
		int dsize = v.getDomainSize();
		int weight = 0;
		// Calcul du poids:
		DisposableIntIterator c = v.getIndexVector().getIndexIterator();
		int idx = 0;
        while (c.hasNext()) {
            idx = c.next();
            reuseCstr = (AbstractSConstraint) v.getConstraint(idx);
			if (SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())
                && reuseCstr.getNbVarNotInst() > 1) {
				weight += ((DomOverWDegConstraintExtension) reuseCstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure + reuseCstr.getFineDegree(v.getVarIndex(idx));
			}
        }
        c.dispose();
		if (weight == 0)
			return Double.MAX_VALUE;
		else
			return (double) dsize / ((double) weight);
	}

	public void contradictionOccured(ContradictionException e) {
		Object cause = e.getContradictionCause();
		if (cause != null && e.getContradictionType() == ContradictionException.CONSTRAINT) {
			reuseCstr = (AbstractSConstraint) cause;
            if(SConstraintType.INTEGER.equals(reuseCstr.getConstraintType())){
			    ((DomOverWDegConstraintExtension) reuseCstr.getExtension(ABSTRACTCONTRAINT_EXTENSION)).nbFailure++;
            }
		}
	}
}
