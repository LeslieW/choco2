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

package choco.kernel.solver.constraints.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;


/**
 * An abstract class for all implementations of listeners over search variables.
 */
public abstract class AbstractIntSConstraint extends AbstractSConstraint implements IntSConstraint {


    /**
     * Constructs a constraint with the specified priority.
     *
     * @param priority The wished priority.
     */
    protected AbstractIntSConstraint(int priority) {
        super(priority);
    }

    /**
   * Default propagation on instantiation: full constraint re-propagation.
   */

  public void awakeOnInst(int idx) throws ContradictionException {
    constAwake(false);
  }



  /**
   * The default implementation of propagation when a variable has been modified
   * consists in iterating all values that have been removed (the delta domain)
   * and propagate them one after another, incrementally.
   *
   * @param idx
   * @throws choco.kernel.solver.ContradictionException
   */
  public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
    if (deltaDomain != null) {
        try{
      for (; deltaDomain.hasNext();) {
        int val = deltaDomain.next();
        awakeOnRem(idx, val);
      }
        }finally {
            deltaDomain.dispose();
        }
    }
  }

  /**
   * Checks if all the variables are instantiated.
   */

  public boolean isCompletelyInstantiated() {
    final int n = getNbVars();
    for (int i = 0; i < n; i++) {
      if (!(getIntVar(i).isInstantiated())) {
		return false;
	}
    }
    return true;
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
      this.awakeOnInf(varIndex);
      this.awakeOnSup(varIndex);
  }

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   *
   * @return true if the constraint is entailed (default approximate definition)
   */
  public boolean isConsistent() {
    return (isEntailed() == Boolean.TRUE);
  }

  /**
   * returns the (global) index of the constraint among all constraints of the model
   *
   * This method is dangerous since the introduction of dynamic constraint post.
   * @deprecated
   */
  @Deprecated
public int getSelfIndex() {
    final Solver solver = getSolver();
    for (int i = 0; i < solver.getNbIntConstraints(); i++) {
      SConstraint c = solver.getIntConstraint(i);
      if (c == this) {
        return i;
      }
    }
    return -1;
  }

    /**
	 * Default implementation of the isSatisfied by
	 * delegating to the isSatisfied(int[] tuple)
	 * @return
	 */
	public boolean isSatisfied() {
		final int[] tuple = new int[getNbVars()];
		for (int i = 0; i < tuple.length; i++) {
			assert(getIntVar(i).isInstantiated());
			tuple[i] = getIntVar(i).getVal();
		}
		return isSatisfied(tuple);
	}

	/**
	 * TEMPORARY: if not overriden by the constraint, throws an error
	 * to avoid bug using reified constraints in constraints
	 * that have not been changed to fulfill this api yet !
	 * @param tuple
	 * @return
	 */
	public boolean isSatisfied(int[] tuple) {
		throw new UnsupportedOperationException(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     */

    public void awakeOnInf(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     */

    public void awakeOnSup(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    /**
     * Default propagation on one value removal: propagation on domain revision.
     */

    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        this.constAwake(false);
    }

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.INTEGER;
    }

    //by default, no information is known
    public int getFineDegree(int idx) {
        return 1;
    }
}



