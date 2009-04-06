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
package choco.cp.solver.goals.choice;

import choco.cp.solver.goals.GoalHelper;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;


/*
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Since : Choco 2.0.0
 *
 */
public class Instantiate implements Goal {

	protected IntDomainVar var;
	protected ValSelector valSelector;
  protected ValIterator valIterator;
  protected int previousVal = Integer.MAX_VALUE;

  public Instantiate(IntDomainVar var, ValSelector s) {
		this.var = var;
		this.valSelector = s;
	}

  public Instantiate(IntDomainVar var, ValIterator valIterator) {
    this.var = var;
		this.valIterator = valIterator;
  }

  public Instantiate(IntDomainVar var) {
		this.var = var;
		this.valSelector = new MinVal();
	}

  public String pretty() {
    return "Instantiate " + var.pretty();
  }

  public Goal execute(Solver s) throws ContradictionException {
		if (var.isInstantiated()) return null;
    int val = -1;
    if (valIterator != null) {
      if (previousVal == Integer.MAX_VALUE) {
        val = valIterator.getFirstVal(var);
      } else {
        if (valIterator.hasNextVal(var, previousVal))
          val = valIterator.getNextVal(var, previousVal);
      }
      previousVal = val;
    } else {
      val = valSelector.getBestVal(var);
    }
    return GoalHelper.or(GoalHelper.setVal(var, val),
				GoalHelper.and(GoalHelper.remVal(var, val), this));
	}
}
