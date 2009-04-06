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
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;


/*
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Since : Choco 2.0.0
 *
 */
public class Generate implements Goal {

	protected VarSelector varSelector;
  protected ValSelector valSelector;
  protected ValIterator valIterator;
  protected IntDomainVar[] vars;

  public Generate(IntDomainVar[] vars, VarSelector varSelector, ValIterator valIterator) {
    this(vars, varSelector);
    this.valIterator = valIterator;
  }

  public Generate(IntDomainVar[] vars, VarSelector varSelector, ValSelector valSelector) {
    this(vars, varSelector);
    this.valSelector = valSelector;
  }

  public Generate(IntDomainVar[] vars, VarSelector varSelector) {
    this.varSelector = varSelector;
    this.vars = new IntDomainVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
  }

  public Generate(IntDomainVar[] vars) {
    this(vars, new MinDomain(null, vars));
  }

  public String pretty() {
    return "Generate";
  }

  public Goal execute(Solver s) throws ContradictionException {
		AbstractVar var;
		var = varSelector.selectVar();
		if (var == null) {
      return null;
    } else {
      if (valSelector != null)
        return GoalHelper.and(new Instantiate((IntDomainVar) var, valSelector), this);
      else if (valIterator != null)
        return GoalHelper.and(new Instantiate((IntDomainVar) var, valIterator), this);
      else return GoalHelper.and(new Instantiate((IntDomainVar) var), this);
    }
	}
}
