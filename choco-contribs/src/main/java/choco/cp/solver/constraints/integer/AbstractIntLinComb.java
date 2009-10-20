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
public abstract class AbstractIntLinComb extends AbstractLargeIntSConstraint implements ILinCombSConstraint {

	public final static ILinCombOperator EQ = new EqLinCombOperator();

	public final static ILinCombOperator GEQ = new GeqLinCombOperator();

	public final static ILinCombOperator NEQ = new NeqLinCombOperator();


	public final ILinCombOperator operator = EQ;

	/**
	 * The coefficients of the linear equations.
	 * The positive coefficents should be the first ones.
	 */
	protected final int[] coeffs;


	/**
	 * Field representing the number of variables
	 * with positive coeffficients in the linear combination.
	 */
	protected final int nbPosVars;

	private boolean anyChange;

	private boolean noFixPoint;

	/**
	 * Constructs the constraint with the specified variables and constant.
	 * Use the Model.createIntLinComb API instead of this constructor.
	 * This constructor assumes that there are no null coefficient
	 * and that the positive coefficients come before the negative ones.
	 * @param lvars the variables of the constraint
	 * @param lcoeffs the constant coefficients
	 * @param nbPositive number of positive coefficients
	 * @param cste the constant value of the constraint (the value the linear
	 * expression must equal)
	 * @param linOperator the operator to use (equality, inequality...)
	 */
	public AbstractIntLinComb(IntDomainVar[] lvars, int[] coeffs, int nbPosVars, int cste) {
		super(lvars);
		this.coeffs = coeffs;
		this.nbPosVars = nbPosVars;
		this.cste = cste;
	}

	public final int getCoefficient(int i) {
		return coeffs[i];
	}

	public final ILinCombOperator getOperator() {
		return operator;
	}

	//*****************************************************************//
	//*******************  EVENT MANAGEMENT **************************//
	//***************************************************************//



	/**
	 * The strategy starts with LB.
	 */
	protected final void filterLB() throws ContradictionException {
		do {
			noFixPoint = operator.filterOnImprovedLowerBound(this);
			if( noFixPoint ) noFixPoint = operator.filterOnImprovedUpperBound(this);
		} while (noFixPoint);
	}

	/**
	 * The strategy starts with UB.
	 */
	protected final void filterUB() throws ContradictionException {
		do {
			noFixPoint = operator.filterOnImprovedUpperBound(this);
			if( noFixPoint ) noFixPoint = operator.filterOnImprovedLowerBound(this);
		} while (noFixPoint);
	}

	@Override
	public int getFilteredEventMask(int idx) {
		return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
	}


	/**
	 * A strategy for chaotic iteration with two rules (LB and UB propagation).
	 * The fix point is reached individually for each rule in one function call
	 * but this call may break the stability condition for the other rule
	 * (in which case the second rule infers new information from the fresh
	 * inferences made by the first rule) .
	 * The algorithm oscilates between both rules until a global fix point is reached.
	 *
	 */
	public final void propagate() throws ContradictionException {
		if ( operator.filterOnImprovedLowerBound(this) ) filterUB();
		else if( operator.filterOnImprovedUpperBound(this)) filterLB();
	}


	/**
	 * Propagation whenever a variable is instantiated.
	 * @param idx the index of the modified variable
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	@Override
	public final void awakeOnInst(final int idx) throws ContradictionException {
		propagate();
	}


	@Override
	public final void awakeOnInf(int varIdx) throws ContradictionException {
		if ( varIdx < nbPosVars) filterLB();
		else filterUB();
	}



	@Override
	public final void awakeOnSup(int varIdx) throws ContradictionException {
		if ( varIdx < nbPosVars) filterUB();
		else filterLB();
	}


	/**
	 * Propagation whenever a value is removed from the variable domain.
	 * @param idx the index of the modified variable
	 * @param x the removed value
	 * @throws ContradictionException if a domain empties or a contradiction is
	 * infered
	 */
	@Override
	public final void awakeOnRem(final int idx, final int x)
	throws ContradictionException {
	}

	@Override
	public final void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {

	}

	//*****************************************************************//
	//*******************  UTILITY FUNCTIONS *************************//
	//***************************************************************//

	protected abstract int getInfNV(final int i, final int mylb);

	protected abstract int getSupNV(final int i, final int myub);

	protected abstract int getInfPV(final int i, final int myub);
	
	protected abstract int getSupPV(final int i, final int mylb);
	

	//*****************************************************************//
	//*******************  CONSISTENCY  *******************************//
	//***************************************************************//




	@Override
	public final boolean hasConsistentLowerBound() {
		final int mylb = computeLowerBound();
		if( mylb > 0) return false;
		else {
			for (int i = 0; i < nbPosVars; i++) {
				if( vars[i].getSup() < getSupPV(i, mylb) ) return false;
			}
			for (int i = nbPosVars; i < vars.length; i++) {
				if( vars[i].getInf() > getInfNV(i, mylb)) return false;
			}
			return true;
		}

	}



	@Override
	public final boolean hasConsistentUpperBound() {
		final int myub = computeUpperBound();
		if(myub < 0) return false;
		else {
			for (int i = 0; i < nbPosVars; i++) {
				if( vars[i].getInf() > getInfPV(i, myub) ) return false;
			}
			for (int i = nbPosVars; i < vars.length; i++) {
				if( vars[i].getSup() < getSupNV(i, myub)) return false;
			}
			return true;
		}
	}



	//*****************************************************************//
	//*******************  PROPAGATION  ******************************//
	//***************************************************************//




	@Override
	public final boolean propagateNewLowerBound(int mylb)
	throws ContradictionException {
		anyChange = false;
		if( mylb > 0) this.fail();
		else {
			for (int i = 0; i < nbPosVars; i++) {
				anyChange |= vars[i].updateSup( getSupPV(i, mylb), cIndices[i]);
			}
			for (int i = nbPosVars; i < vars.length; i++) {
				anyChange |= vars[i].updateInf( getInfNV(i, mylb), cIndices[i]);
			}
		}
		return anyChange;
	}



	@Override
	public final boolean propagateNewUpperBound(int myub)
	throws ContradictionException {
		anyChange = false;
		if( myub < 0) this.fail();
		else {
			for (int i = 0; i < nbPosVars; i++) {
				anyChange |= vars[i].updateInf( getInfPV(i, myub), cIndices[i]);
			}
			for (int i = nbPosVars; i < vars.length; i++) {
				anyChange |= vars[i].updateSup( getSupNV(i, myub), cIndices[i]);
			}	
		}
		return anyChange;
	}


	//*****************************************************************//
	//*******************  CONSTRAINT INTERFACE **********************//
	//***************************************************************//

	@Override
	public final boolean isConsistent() {
		return operator.isConsistent(this);
	}

	@Override
	public final Boolean isEntailed() {
		return operator.isEntailed(this);
	}

	@Override
	public AbstractSConstraint opposite() {
		final Solver s = getSolver();
		return operator.opposite(s, s.scalar(coeffs, vars), cste);
	}	

	@Override
	public boolean isSatisfied(int[] tuple) {
		int s = cste;
		for (int i = 0; i < vars.length; i++) {
			s += coeffs[i] * tuple[i];
		}
		return operator.isSatisfied(s);
	}


	private void pretty(StringBuilder b, int i) {
		if( coeffs[i] > 0) {
			b.append("+ ");
			if(coeffs[i] > 1) b.append(coeffs[i]).append("*");
		} else if( coeffs[i] < 0) {
			b.append("- ");
			if(coeffs[i] < -1) b.append(-coeffs[i]).append("*");
		} else {b.append('?');} 
		b.append(vars[i]).append(' ');
	}

	@Override
	public String pretty() {
		StringBuilder b = new StringBuilder();
		int i;
		for (i = 0; i < vars.length; i++) {
			pretty(b, i);
		}
		b.append(" ").append(operator.pretty()).append(" ").append(-cste);
		return b.toString();
	}


}

	final class EqLinCombOperator implements ILinCombOperator {

		public EqLinCombOperator() {}

		@Override
		public boolean filterOnImprovedLowerBound(ILinCombSConstraint linComb)
		throws ContradictionException {
			return linComb.propagateNewLowerBound( linComb.computeLowerBound());
		}

		@Override
		public boolean filterOnImprovedUpperBound(ILinCombSConstraint linComb)
		throws ContradictionException {
			return linComb.propagateNewUpperBound( linComb.computeUpperBound());
		}

		@Override
		public boolean isConsistent(ILinCombSConstraint linComb) {
			return linComb.hasConsistentLowerBound() && linComb.hasConsistentUpperBound();
		}

		@Override
		public Boolean isEntailed(ILinCombSConstraint linComb) {
			final int a = linComb.computeLowerBound();
			final int b = linComb.computeUpperBound();
			if (b < 0 || a > 0) return Boolean.FALSE;
			else if (b > 0 || a < 0) return null;
			else return Boolean.TRUE;
		}

		@Override
		public boolean isSatisfied(int val) {
			return val == 0;
		}

		@Override
		public AbstractSConstraint opposite(Solver solver, IntExp leftMember, int rightMember) {
			return (AbstractSConstraint) solver.neq(leftMember, rightMember);
		}

		@Override
		public String pretty() {
			return "=";
		}

	}


	final class GeqLinCombOperator implements ILinCombOperator {

		public GeqLinCombOperator() {}

		@Override
		public boolean filterOnImprovedLowerBound(ILinCombSConstraint linComb)
		throws ContradictionException {
			// nothing to propagate => nothing was inferred, so return false
			return false;
		}

		@Override
		public boolean filterOnImprovedUpperBound(ILinCombSConstraint linComb)
		throws ContradictionException {
			return linComb.propagateNewUpperBound( linComb.computeUpperBound());
		}

		@Override
		public boolean isConsistent(ILinCombSConstraint linComb) {
			return linComb.hasConsistentUpperBound();
		}

		@Override
		public Boolean isEntailed(ILinCombSConstraint linComb) {
			if (linComb.computeUpperBound() < 0) return Boolean.FALSE;
			else if (linComb.computeLowerBound() >= 0) return Boolean.TRUE;
			else return null;
		}

		@Override
		public boolean isSatisfied(int val) {
			return val >=0;
		}

		@Override
		public AbstractSConstraint opposite(Solver solver, IntExp leftMember, int rightMember) {
			return (AbstractSConstraint) solver.lt(leftMember, rightMember);
		}

		@Override
		public String pretty() {
			return ">=";
		}

	}


	final class NeqLinCombOperator implements ILinCombOperator {


		public NeqLinCombOperator() {
			super();
		}

		@Override
		public boolean filterOnImprovedLowerBound(ILinCombSConstraint linComb)
		throws ContradictionException {
			if (linComb.computeLowerBound() == 0) { 
				// propagate the constraint sigma(ai) + c >= 1
				return linComb.propagateNewUpperBound(linComb.computeUpperBound() - 1);
				// TODO I have doubts.....
			} else return false;
		}

		@Override
		public boolean filterOnImprovedUpperBound(ILinCombSConstraint linComb)
		throws ContradictionException {
			if ( linComb.computeUpperBound() == 0) { 
				// propagate the constraint sigma(ai) + c <= -1
				return linComb.propagateNewLowerBound( linComb.computeLowerBound() + 1);
				// TODO I have doubts.....
			} else return false;
		}

		@Override
		public boolean isConsistent(ILinCombSConstraint linComb) {
			return true;
		}

		@Override
		public Boolean isEntailed(ILinCombSConstraint linComb) {
			final int a = linComb.computeLowerBound();
			if (a > 0) {
				return Boolean.TRUE;
			} else {
				final int b = linComb.computeUpperBound();
				if (b < 0) {
					return Boolean.TRUE;
				} else if (b == 0 && a == 0) {
					return Boolean.FALSE;
				} else {
					return null;
				}
			}
		}

		@Override
		public boolean isSatisfied(int val) {
			return val != 0;
		}

		@Override
		public AbstractSConstraint opposite(Solver solver, IntExp leftMember,
				int rightMember) {
			return (AbstractSConstraint) solver.eq(leftMember, rightMember);
		}

		@Override
		public String pretty() {
			return "!=";
		}

	}

