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
package choco.kernel.solver.search;

import java.util.logging.Level;

import choco.kernel.common.util.Arithm;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractOptimize extends AbstractGlobalSearchStrategy {
	/**
	 * a boolean indicating whether we want to maximize (true) or minize (false) the objective variable
	 */
	public boolean doMaximize;
	/**
	 * the variable modelling the objective value
	 */
	public IntDomainVar objective;
	/**
	 * the lower bound of the objective value.
	 * This value comes from the model definition; it is strengthened by the search history (solutions found & no-goods)
	 */
	public int lowerBound = Integer.MIN_VALUE;
	/**
	 * the upper bound of the objective value
	 * This value comes from the model definition; it is strengthened by the search history (solutions found & no-goods)
	 */
	public int upperBound = Integer.MAX_VALUE;

	/**
	 * a tentative upper bound
	 */
	public int targetUpperBound = Integer.MAX_VALUE;

	/**
	 * a tentative lower bound
	 */
	public int targetLowerBound = Integer.MIN_VALUE;

	/**
	 * constructor
	 *
	 * @param obj      the objective variable
	 * @param maximize maximization or minimization ?
	 */
	protected AbstractOptimize(IntDomainVar obj, boolean maximize) {
		super(obj.getSolver());
		objective = obj;
		doMaximize = maximize;
	}

	/**
	 * v1.0 accessing the objective value of an optimization model
	 * (note that the objective value may not be instantiated, while all other variables are)
	 *
	 * @return the current objective value
	 */
	public int getObjectiveValue() {
		if (doMaximize) {
			return objective.getSup();
		} else {
			return objective.getInf();
		}
	}

	public int getBestObjectiveValue() {
		if (doMaximize) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * the target for the objective function: we are searching for a solution at least as good as this (tentative bound)
	 */
	public int getObjectiveTarget() {
		if (doMaximize) {
			return targetLowerBound;
		} else {
			return targetUpperBound;
		}
	}

	/**
	 * initialization of the optimization bound data structure
	 */
	public void initBounds() {
		lowerBound = objective.getInf();
		upperBound = objective.getSup();
		targetLowerBound = objective.getInf();
		targetUpperBound = objective.getSup();

	}


	@Override
	public void recordSolution() {
		if(LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "solution with cost {1}", new Object[]{ Integer.valueOf(-1), objective});
		}
		super.recordSolution();
		//nbSolutions = nbSolutions + 1;
		// trace(SVIEW,"... solution with ~A:~S [~S]\n",obj.name,objval,a.limits),  // v1.011 <thb>
		setBound();
		setTargetBound();

	}

	/**
	 * resetting the optimization bounds
	 */
	public void setBound() {
		int objval = getObjectiveValue();
		if (doMaximize) {
			lowerBound = Arithm.max(lowerBound, objval);
		} else {
			upperBound = Arithm.min(upperBound, objval);
		}
	}

	/**
	 * resetting the values of the target bounds (bounds for the remaining search)
	 */
	public void setTargetBound() {
		if (doMaximize) {
			setTargetLowerBound();
		} else {
			setTargetUpperBound();
		}
	}

	protected void setTargetLowerBound() {
		int newBound = lowerBound + 1;
		if (solver.getFeasible() != Boolean.TRUE) {
			// trace(STALK,"search first sol ...")
		} else {
			// trace(STALK,"search target: ~A >= ~S ... ",a.objective.name,newBound))
			targetLowerBound = newBound;
		}
	}

	protected void setTargetUpperBound() {
		int newBound = upperBound - 1;
		if (solver.getFeasible()!= Boolean.TRUE) {
			// trace(STALK,"search first sol ...")
		} else {
			// trace(STALK,"search target: ~A <= ~S ... ",a.objective.name,newBound))
			targetUpperBound = newBound;
		}
	}

	/**
	 * propagating the optimization cuts from the new target bounds
	 */
	public void postTargetBound() throws ContradictionException {
		if (doMaximize) {
			postTargetLowerBound();
		} else {
			postTargetUpperBound();
		}
	}

	public void postTargetLowerBound() throws ContradictionException {
		objective.setInf(targetLowerBound);
	}

	public void postTargetUpperBound() throws ContradictionException {
		objective.setSup(targetUpperBound);
	}

	/**
	 * we use  targetBound data structures for the optimization cuts
	 */
	@Override
	public void postDynamicCut() throws ContradictionException {
		postTargetBound();
		//model.propagate();
	}

	@Override
	public void endTreeSearch() {
		if (LOGGER.isLoggable(Level.CONFIG)) {
			LOGGER.log(Level.CONFIG, "{1} => {2}", new Object[]{-1, doMaximize ? "maximize" : "minimize", objective});
		}
		super.endTreeSearch();
	}
	
	
}
