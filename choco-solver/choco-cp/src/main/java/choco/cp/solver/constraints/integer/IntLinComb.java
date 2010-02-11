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


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint Sigma (ai Xi) <=/>=/= C,
 * with Xi variables, ai and C constants.
 */
public class IntLinComb extends AbstractLargeIntSConstraint {
	/**
	 * Constant, to be assigned to <code>op</code>,
	 * representing linear equalities.
	 */
	public static final int EQ = 0;

	/**
	 * Constant, to be assigned to <code>op</code>,
	 * representing linear inequalities.
	 */
	public static final int GEQ = 1;

	/**
	 * Constant, to be assigned to <code>op</code>,
	 * representing linear disequalities.
	 */
	public static final int NEQ = 2;

	/**
	 * Constant, to be assigned to <code>op</code>,
	 * representing linear inequalities.
	 * Only used vby BoolIntLinComb
	 */
	public static final int LEQ = 3;

	/**
	 * Field representing the type of linear constraint
	 * (equality, inequality, disequality).
	 */
	protected final int op;

	/**
	 * The coefficients of the linear equations.
	 * The positive coefficents should be the first ones.
	 */
	protected int[] coeffs;

	/**
	 * Field representing the number of variables
	 * with positive coeffficients in the linear combination.
	 */
	protected final int nbPosVars;

	/**
	 * The constant of the constraint.
	 */
	protected final int cste;

	/**
	 * Constructs the constraint with the specified variables and constant.
	 * Use the Model.createIntLinComb API instead of this constructor.
	 * This constructor assumes that there are no null coefficient
	 * and that the positive coefficients come before the negative ones.
	 * @param lvars the variables of the constraint
	 * @param lcoeffs the constant coefficients
	 * @param nbPositive number of positive coefficients
	 * @param c the constant value of the constraint (the value the linear
	 * expression must equal)
	 * @param linOperator the operator to use (equality, inequality...)
	 */
	public IntLinComb(final IntDomainVar[] lvars, final int[] lcoeffs,
			final int nbPositive, final int c, final int linOperator) {
		// create the appropriate data structure
		super(lvars);
		init(lcoeffs);
		nbPosVars = nbPositive;
		op = linOperator;
		cste = c;
	}

	public int getFilteredEventMask(int idx) {
		return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
		// return 0x0B;
	}

	/**
	 * Builds a copy of this contraint.
	 * @return a clone of this constraint
	 * @throws CloneNotSupportedException if an error occurs during cloning
	 */
	public Object clone() throws CloneNotSupportedException {
		IntLinComb newc = (IntLinComb) super.clone();
		newc.init(this.coeffs);
		return newc;
	}

	/**
	 * Initializes the constraint by copying the coefficent array.
	 * @param lcoeffs the coefficients of the linear equation
	 */
	public void init(final int[] lcoeffs) {
		this.coeffs = new int[lcoeffs.length];
		System.arraycopy(lcoeffs, 0, this.coeffs, 0, lcoeffs.length);
	}

	/**
	 * Launchs the filtering algorithm.
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	public void propagate() throws ContradictionException {
		filter(true, 2);
	}


	/**
	 * Propagation whenever the lower bound of a variable is modified.
	 * @param idx the index of the modified variable
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	public void awakeOnInf(final int idx) throws ContradictionException {
		if (idx < nbPosVars) {
			filter(true, 1);
		} else {
			filter(false, 1);
		}
	}

	/**
	 * Propagation whenever the upper bound of a variable is modified.
	 * @param idx the index of the modified variable
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	public void awakeOnSup(final int idx) throws ContradictionException {
		if (idx < nbPosVars) {
			filter(false, 1);
		} else {
			filter(true, 1);
		}
	}

	/**
	 * Propagation whenever a variable is instantiated.
	 * @param idx the index of the modified variable
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	public void awakeOnInst(final int idx) throws ContradictionException {
		propagate();
	}

	/**
	 * Propagation whenever a value is removed from the variable domain.
	 * @param idx the index of the modified variable
	 * @param x the removed value
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	public void awakeOnRem(final int idx, final int x)
	throws ContradictionException {
	}

	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {

	}

	/**
	 * Checks if the constraint is entailed.
	 * @return Boolean.TRUE if the constraint is satisfied, Boolean.FALSE if it
	 * is violated, and null if the filtering algorithm cannot infer yet.
	 */
	public Boolean isEntailed() {
		if (op == EQ) {
			int a = computeLowerBound();
			int b = computeUpperBound();
			if (b < 0 || a > 0) {
				return Boolean.FALSE;
			} else if (a == 0 && b == 0) {
				return Boolean.TRUE;
			} else {
				return null;
			}
		} else if (op == GEQ) {
			if (computeUpperBound() < 0) {
				return Boolean.FALSE;
			} else if (computeLowerBound() >= 0) {
				return Boolean.TRUE;
			} else {
				return null;
			}
		} else {
			assert op == NEQ;
			int a = computeLowerBound();
			if (a > 0) {
				return Boolean.TRUE;
			} else {
				int b = computeUpperBound();
				if (b < 0) {
					return Boolean.TRUE;
				} else if (b == 0 && a == 0) {
					return Boolean.FALSE;
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * Checks if the constraint is satisfied when all variables are instantiated.
	 * @return true if the constraint is satisfied
	 */
	public boolean isSatisfied(int[] tuple) {
		int s = cste;
		int nbVars = getNbVars();
		int i;
		for (i = 0; i < nbVars; i++) {
			s += (tuple[i] * coeffs[i]);
		}
		if (op == EQ) {
			return (s == 0);
		} else if (op == GEQ) {
			return (s >= 0);
		} else {
			assert op == NEQ;
			return (s != 0);
		}
	}

	/**
	 * Computes an upper bound estimate of a linear combination of variables.
	 * @return the new upper bound value
	 */
	protected int computeUpperBound() {
		int s = cste;
		int nbVars = getNbVars();
		int i;
		for (i = 0; i < nbPosVars; i++) {
			s += (vars[i].getSup() * coeffs[i]);
		}
		for (i = nbPosVars; i < nbVars; i++) {
			s += (vars[i].getInf() * coeffs[i]);
		}
//		if (LOGGER.isLoggable(Level.FINEST)) {
//			LOGGER.log(Level.FINEST, "ub for {0} : {1}", new Object[]{toString(), s});
//		}
		return s;
	}

	/**
	 * Computes a lower bound estimate of a linear combination of variables.
	 * @return the new lower bound value
	 */
	protected int computeLowerBound() {
		int s = cste;
		int nbVars = getNbVars();
		int i;
		for (i = 0; i < nbPosVars; i++) {
			s += (vars[i].getInf() * coeffs[i]);
		}
		for (i = nbPosVars; i < nbVars; i++) {
			s += (vars[i].getSup() * coeffs[i]);
		}
//		if (LOGGER.isLoggable(Level.FINEST)) {
//			LOGGER.log(Level.FINEST, "lb for {0} : {1}", new Object[]{toString(), s});
//		}
		return s;
	}

	// Note: additional propagation pass are sometimes useful:
	// For instance : 3*X[0.3] + Y[1.10] = 10
	//                Y >= 2 causes X < 3 -> updateSup(X,2)
	//                and this very var (the new sup of X) causes (Y >= 4).
	//                this induced var (Y>=4) could not be infered
	//                at first (with only Y>=2)
	//

	/**
	 * A strategy for chaotic iteration with two rules (LB and UB propagation).
	 * The fix point is reached individually for each rule in one function call
	 * but this call may break the stability condition for the other rule
	 * (in which case the second rule infers new information from the fresh
	 * inferences made by the first rule) .
	 * The algorithm oscilates between both rules until
	 * a global fix point is reached.
	 * @param startWithLB whether LB must be the first rule applied
	 * @param minNbRules  minimum number of rules required to reach fix point.
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	protected void filter(final boolean startWithLB,
			final int minNbRules) throws ContradictionException {
		boolean lastRuleEffective = true;
		// whether the last rule indeed perform some reductions
		int nbr = 0;
		// number of rules applied
		boolean nextRuleIsLB = startWithLB;
		// whether the next rule that should be filtered is LB (or UB)
		////////////////////////////////DEBUG ONLY ///////////////////////////
		//Logging statements really decrease performance
		while (lastRuleEffective || nbr < minNbRules) {
			if (nextRuleIsLB) {
				//				if (LOGGER.isLoggable(Level.FINER)) {
				//					LOGGER.log(Level.FINER, "-- LB propagation for {0}", toString());
				//				}
				lastRuleEffective = filterOnImprovedLowerBound();
			} else {
				//				if (LOGGER.isLoggable(Level.FINER)) {
				//					LOGGER.log(Level.FINER, "-- UB propagation for {0}", toString());
				//				}
				lastRuleEffective = filterOnImprovedUpperBound();
			}
			nextRuleIsLB = !nextRuleIsLB;
			nbr++;
		}
		//////////////////////////////////////////////////////////////////////////////////
	}

	/**
	 * Checks a new lower bound.
	 * @return true if filtering has been infered
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	protected boolean filterOnImprovedLowerBound()
	throws ContradictionException {
		if (op == EQ) {
			// the constraint check is needed only for
			// equality constraints (otherwise passive constraint)
			return propagateNewLowerBound(computeLowerBound());
		} else if (op == GEQ) {
			return false;
			// nothing to propagate => nothing was inferred, so return false
		} else {
			assert op == NEQ;
			int mylb = computeLowerBound();
			if (mylb == 0) { // propagate the constraint sigma(ai) + c >= 1
				//				if (LOGGER.isLoggable(Level.FINER)) {
				//					LOGGER.log(Level.FINER, "propagate > 0 for {0}", toString());
				//				}
				return propagateNewUpperBound(computeUpperBound() - 1);
				// TODO I have doubts.....
			} else {
				return false;
			}
		}
	}

	/**
	 * Checks a new upper bound.
	 * @return true if filtering has been infered
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	protected boolean filterOnImprovedUpperBound()
	throws ContradictionException {
		int myub = computeUpperBound();
		if (op == EQ) {
			return propagateNewUpperBound(myub);
		} else if (op == GEQ) {
			return propagateNewUpperBound(myub);
		} else {
			assert op == NEQ;
			if (myub == 0) { // propagate the constraint sigma(ai) + c <= -1
				//				if (LOGGER.isLoggable(Level.FINER)) {
				//					LOGGER.log(Level.FINER, "propagate < 0 for {0}", toString());
				//				}
				return propagateNewLowerBound(computeLowerBound() + 1);
				// TODO I have doubts.....
			} else {
				return false;
			}
		}
	}

	/**
	 * Propagates the constraint sigma(ai Xi) + c <= 0
	 * where mylb = sigma(ai inf(Xi)) + c.
	 * Note: this does not reach saturation (fix point),
	 * but returns a boolean indicating whether
	 * it infered new information or not.
	 * @param mylb the computed lower bound
	 * @return true if filtering has been infered
	 * @throws ContradictionException if a domain empties or a contradiction
	 * is infered
	 */
	protected boolean propagateNewLowerBound(final int mylb)
	throws ContradictionException {
		////////////////////////////////DEBUG ONLY ///////////////////////////
		//Logging statements really decrease performance
		boolean anyChange = false;
		int nbVars = getNbVars();
		if (mylb > 0) {
			//			if (LOGGER.isLoggable(Level.FINER)) {
			//				LOGGER.log(Level.FINER, "lb = {0} > 0 => fail", mylb);
			//			}
			this.fail();
		}
		int i;
		for (i = 0; i < nbPosVars; i++) {
			int newSupi = MathUtils.divFloor(-(mylb), coeffs[i]) + vars[i].getInf();
			if (vars[i].updateSup(newSupi, cIndices[i])) {
				//				if (LOGGER.isLoggable(Level.FINER)) {
				//					LOGGER.log(Level.FINER,  "SUP({0}) <= {1}/{2} + {3} = {4}",
				//							new Object[]{vars[i].toString(), -(mylb), coeffs[i], vars[i].getInf(), newSupi});
				//				}
				anyChange = true;
			}
		}
		for (i = nbPosVars; i < nbVars; i++) {
			int newInfi = MathUtils.divCeil(mylb, -(coeffs[i])) + vars[i].getSup();
			if (vars[i].updateInf(newInfi, cIndices[i])) {
				//				if (LOGGER.isLoggable(Level.FINER)) {
				//					LOGGER.log(Level.FINER, "INF({0}) >= {1}/{2} + {3} = {4}",
				//							new Object[]{vars[i].toString(), mylb, -(coeffs[i]), vars[i].getSup(), newInfi});
				//				}
				anyChange = true;
			}
		}
		return anyChange;
	}

	/**
	 * Propagates the constraint sigma(ai Xi) + c <= 0
	 * where myub = sigma(ai sup(Xi)) + c.
	 * Note: this does not reach saturation (fix point),
	 * but returns a boolean indicating whether
	 * it infered new information or not.
	 * @param myub the computed upper bound
	 * @return true if filtering has been infered
	 * @throws ContradictionException if a domain empties or a contradiction
	 * is infered
	 */
	protected boolean propagateNewUpperBound(final int myub)
	throws ContradictionException {
		////////////////////////////////DEBUG ONLY ///////////////////////////
		//Logging statements really decrease performance
		boolean anyChange = false;
		int nbVars = getNbVars();
		if (myub < 0) {
//			if (LOGGER.isLoggable(Level.FINER)) {
//				LOGGER.log(Level.FINER, "ub = {0} < 0 => fail", myub);
//			}
			this.fail();
		}
		int i;
		for (i = 0; i < nbPosVars; i++) {
			int newInfi = MathUtils.divCeil(-(myub), coeffs[i]) + vars[i].getSup();
			if (vars[i].updateInf(newInfi, cIndices[i])) {
//				if (LOGGER.isLoggable(Level.FINER)) {
//					LOGGER.log(Level.FINEST, "INF({0}) >= {1}/{2} + {3} = {4}",
//							new Object[]{vars[i].toString(), -(myub), coeffs[i], vars[i].getSup(), newInfi});
//				}
				anyChange = true;
			}
		}
		for (i = nbPosVars; i < nbVars; i++) {
			int newSupi = MathUtils.divFloor(myub, -(coeffs[i])) + vars[i].getInf();
			if (vars[i].updateSup(newSupi, cIndices[i])) {
//				if (LOGGER.isLoggable(Level.FINER)) {
//					{LOGGER.log(Level.FINER, "SUP({0}) <= {1}/{2} + {3} = {4} ",
//							new Object[]{vars[i].toString(), myub, -(coeffs[i]), vars[i].getInf(), newSupi});}
//				}
				anyChange = true;
			}
		}
		return anyChange;
	}

	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 * @return true iff the constraint is bound consistent
	 * (weaker than arc consistent)
	 */
	public boolean isConsistent() {
		//    int ub = computeUpperBound();
		//    int lb = computeLowerBound();
		//    int nbVars = getNbVars();

		if (op == EQ) {
			return (hasConsistentLowerBound() && hasConsistentUpperBound());
		} else if (op == GEQ) {
			return hasConsistentUpperBound();
		}
		return true;
	}

	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 * @return true iff the constraint is bound consistent
	 * (weaker than arc consistent)
	 */
	protected boolean hasConsistentLowerBound() {
		int lb = computeLowerBound();
		int nbVars = getNbVars();

		if (lb > 0) {
			return false;
		} else {
			for (int i = 0; i < nbPosVars; i++) {
				int newSupi = MathUtils.divFloor(-(lb), coeffs[i]) + vars[i].getInf();
				if (vars[i].getSup() < newSupi) {
					return false;
				}
			}
			for (int i = nbPosVars; i < nbVars; i++) {
				int newInfi = MathUtils.divCeil(lb, -(coeffs[i])) + vars[i].getSup();
				if (vars[i].getInf() > newInfi) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 * @return true iff the constraint is bound consistent
	 * (weaker than arc consistent)
	 */
	protected boolean hasConsistentUpperBound() {
		int ub = computeUpperBound();
		int nbVars = getNbVars();

		if (ub < 0) {
			return false;
		} else {
			for (int i = 0; i < nbPosVars; i++) {
				int newInfi = MathUtils.divCeil(-(ub), coeffs[i]) + vars[i].getSup();
				if (vars[i].getInf() > newInfi) {
					return false;
				}
			}
			for (int i = nbPosVars; i < nbVars; i++) {
				int newSupi = MathUtils.divFloor(ub, -(coeffs[i])) + vars[i].getInf();
				if (vars[i].getSup() < newSupi) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Computes the opposite of this constraint.
	 * @return a constraint with the opposite semantic  @param solver
	 */
	public AbstractSConstraint opposite(Solver solver) {
		IntExp term = solver.scalar(coeffs, vars);
		if (op == EQ) {
			return (AbstractSConstraint) solver.neq(term, -cste);
		} else if (op == NEQ) {
			return (AbstractSConstraint) solver.eq(term, -cste);
		} else if (op == GEQ) {
			return (AbstractSConstraint) solver.lt(term, -cste);
		} else {
			return null;
		}
	}


	/**
	 * Pretty print for this constraint. This method prints the complete
	 * equations.
	 * @return a strring representation of the constraint
	 */
	public String pretty() {
		StringBuilder linComb = new StringBuilder();
		for (int i = 0; i < coeffs.length - 1; i++) {
            linComb.append(coeffs[i]).append("*").append(vars[i]).append(" + ");
		}
        linComb.append(coeffs[coeffs.length - 1]).append("*").append(vars[coeffs.length - 1]);
		if (op == 0) {
			linComb.append(" = ");
		} else if (op == 1) {
			linComb.append(" >= ");
		} else if (op == 2) {
			linComb.append(" != ");
		}
		linComb.append(-cste);
		return linComb.toString();
	}
}
