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
package choco.cp.solver.search.real;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 20 juil. 2004
 */
public class RealOptimizeWithRestarts extends AbstractRealOptimize {
	/**
	 * counting the number of iterations
	 */
	protected int nbIter = 0;

	/**
	 * counting the overall number of solutions
	 */
	protected int baseNbSol = 0;

	/**
	 * total nb of backtracks (all trees in the optimization process)
	 */
	protected int nbBkTot = 0;

	/**
	 * total nb of nodes expanded in all trees
	 */
	protected int nbNdTot = 0;

	public RealOptimizeWithRestarts(RealVar obj, boolean maximize) {
		super(obj, maximize);
	}

	public void newTreeSearch() throws ContradictionException {
		super.newTreeSearch();
		nbIter = nbIter + 1;
		baseNbSol = getSolutionCount();
		postTargetBound();
		solver.propagate();
	}

	/**
	 * called before a new search tree is explored
	 */
	public void endTreeSearch() {
		limitManager.reset();	
		clearTrace();
		solver.worldPopUntil(baseWorld);
	}

	// should we call a fullReset on limits ? (to reset cumulated counter?)
	private void newLoop() {
		initBounds();
		// time_set()
	}

	private void endLoop() {
		/*
    -> let t := time_get() in
       trace(SVIEW,"Optimisation over => ~A(~A) = ~S found in ~S iterations, [~S], ~S ms.\n",
              (if a.doMaximize "max" else "min"),a.objective.name,
              getBestObjectiveValue(a),  // v1.013 using the accessor
              a.nbIter,a.limits,t)]
		 */
	}

	private void recordNoSolution() {
		// (trace(SVIEW,"... no solution with ~A:~S [~S]\n",obj.name,objtgt,a.limits),
		if (doMaximize) {
			upperBound = Math.min(upperBound, RealMath.prevFloat(getObjectiveTarget()));
		} else {
			lowerBound = Math.max(lowerBound, RealMath.nextFloat(getObjectiveTarget()));
		}
	}

	/**
	 * loop until the lower bound equals the upper bound
	 *
	 * @return true if one more loop is needed
	 */
	private boolean oneMoreLoop() {
		return (lowerBound < upperBound);
	}

	/*
	 * @deprecated replaced by the incrementalRun
	 */
	/*public void run() {
    int w = model.getWorldIndex() + 1;
    AbstractModel pb = model;
    boolean finished = false;
    newLoop();
    try {
      pb.propagate();
    } catch (ContradictionException e) {
      finished = true;
      recordNoSolution();
    }
    if (!finished) {
      pb.worldPush();
      while (oneMoreLoop()) {
        boolean foundSolution = false;
        try {
          newTreeSearch();
          if (mainGoal.explore(1)) {
            foundSolution = true;
          }
        } catch (ContradictionException e) {
          LOGGER.fine("boff :(");
        }
        endTreeSearch();
        if (!foundSolution) {
          recordNoSolution();
        }
      }
      assert(model.getWorldIndex() == w);
      model.worldPop();
    }
    endLoop();
    if ((maxNbSolutionStored > 0) && existsSolution()) {
      restoreBestSolution();
    }
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      if (LOGGER.isLoggable(Level.SEVERE))
        LOGGER.severe(lim.pretty());
    }
  } */
}
