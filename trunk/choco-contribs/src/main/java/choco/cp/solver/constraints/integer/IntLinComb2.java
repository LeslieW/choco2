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
public class IntLinComb2 extends AbstractLargeIntSConstraint implements ILinCombSConstraint {

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

	private final ILinCombFiltering filtering;

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
	public IntLinComb2(IntDomainVar[] lvars, int[] coeffs, int nbPosVars, int cste) {
		super(lvars);
		this.coeffs = coeffs;
		this.nbPosVars = nbPosVars;
		this.cste = cste;
		if( nbPosVars == vars.length) filtering = new PosFiltering();
		else if( nbPosVars == 0) filtering = new NegFiltering();
		else filtering = new PosAndNegFiltering();
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
		filtering.awakeOnInf(varIdx);
	}



	@Override
	public final void awakeOnSup(int varIdx) throws ContradictionException {
		filtering.awakeOnSup(varIdx);
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

	protected int getInfNV(final int i, final int mylb) {
		return MathUtils.divCeil(mylb, -coeffs[i]) + vars[i].getSup();
	}

	protected int getSupNV(final int i, final int myub) {
		return MathUtils.divFloor(myub, -coeffs[i]) + vars[i].getInf();
	}

	protected int getInfPV(final int i, final int myub) {
		return MathUtils.divCeil(-myub, coeffs[i]) + vars[i].getSup();
	}

	protected int getSupPV(final int i, final int mylb) {
		return MathUtils.divFloor(-mylb, coeffs[i]) + vars[i].getInf();
	}

	//*****************************************************************//
	//*******************  CONSISTENCY  *******************************//
	//***************************************************************//


	protected final boolean hasConsistentLowerBoundPV(int mylb) {
		for (int i = 0; i < nbPosVars; i++) {
			if( vars[i].getSup() < getSupPV(i, mylb) ) return false;
		}
		return true;
	}

	protected final boolean hasConsistentLowerBoundNV(int mylb) {
		for (int i = nbPosVars; i < vars.length; i++) {
			if( vars[i].getInf() > getInfNV(i, mylb)) return false;
		}
		return true;
	}



	@Override
	public final boolean hasConsistentLowerBound() {
		final int mylb = computeLowerBound();
		if( mylb > 0) return false;
		else return filtering.hasConsitentLowerBound(mylb);

	}


	protected final boolean hasConsistentUpperBoundPV(int myub) {
		for (int i = 0; i < nbPosVars; i++) {
			if( vars[i].getInf() > getInfPV(i, myub) ) return false;
		}
		return true;
	}


	protected final boolean hasConsistentUpperBoundNV(int myub) {
		for (int i = nbPosVars; i < vars.length; i++) {
			if( vars[i].getSup() < getSupNV(i, myub)) return false;
		}
		return true;
	}


	@Override
	public final boolean hasConsistentUpperBound() {
		final int myub = computeUpperBound();
		if(myub < 0) return false;
		else {
			return filtering.hasConsitentUpperBound(myub);
		}
	}



	//*****************************************************************//
	//*******************  PROPAGATION  ******************************//
	//***************************************************************//



	protected final void propagateLowerBoundNV(int mylb) throws ContradictionException {
		for (int i = nbPosVars; i < vars.length; i++) {
			anyChange |= vars[i].updateInf( getInfNV(i, mylb), cIndices[i]);
		}
	}


	protected final void propagateLowerBoundPV(int mylb) throws ContradictionException {
		for (int i = 0; i < nbPosVars; i++) {
			anyChange |= vars[i].updateSup( getSupPV(i, mylb), cIndices[i]);
		}
	}



	@Override
	public final boolean propagateNewLowerBound(int mylb)
	throws ContradictionException {
		anyChange = false;
		if( mylb > 0) this.fail();
		else filtering.propagateNewLowerBound(mylb);
		return anyChange;
	}



	protected final void propagateUpperBoundNV(int myub) throws ContradictionException {
		for (int i = nbPosVars; i < vars.length; i++) {
			anyChange |= vars[i].updateSup( getSupNV(i, myub), cIndices[i]);
		}		
	}


	protected final void propagateUpperBoundPV(int myub) throws ContradictionException {
		for (int i = 0; i < nbPosVars; i++) {
			anyChange |= vars[i].updateInf( getInfPV(i, myub), cIndices[i]);
		}
	}


	@Override
	public final boolean propagateNewUpperBound(int myub)
	throws ContradictionException {
		anyChange = false;
		if( myub < 0) this.fail();
		else filtering.propagateNewUpperBound(myub);
		return anyChange;
	}

	//*****************************************************************//
	//*******************  BOUNDING  *********************************//
	//***************************************************************//


	protected int computeLowerBoundPV() {
		int lb = 0; 
		for (int i = 0; i < nbPosVars; i++) {
			lb += coeffs[i] * vars[i].getInf();
		}
		return lb;
	}

	protected int computeLowerBoundNV() {
		int lb = 0; 
		for (int i = nbPosVars; i < vars.length; i++) {
			lb += coeffs[i] * vars[i].getSup();
		}
		return lb;
	}

	protected int computeUpperBoundPV() {
		int ub = 0; 
		for (int i = 0; i < nbPosVars; i++) {
			ub += coeffs[i] * vars[i].getSup();
		}
		return ub;
	}

	protected int computeUpperBoundNV() {
		int ub = 0; 
		for (int i = nbPosVars; i < vars.length; i++) {
			ub += coeffs[i] * vars[i].getInf();
		}
		return ub;
	}

	@Override
	public int computeLowerBound() {
		return filtering.computeLowerBound();
	}

	@Override
	public int computeUpperBound() {
		return filtering.computeUpperBound();
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

	abstract class ILinCombFiltering implements ILinCombBounds {

		abstract void awakeOnInf(int varIdx) throws ContradictionException;

		abstract void awakeOnSup(int varIdx) throws ContradictionException;

		abstract void propagateNewLowerBound(int mylb) throws ContradictionException;

		abstract void propagateNewUpperBound(int myub) throws ContradictionException;

		abstract boolean hasConsitentLowerBound(int mylb);

		abstract boolean hasConsitentUpperBound(int myub);

	}

	final class PosAndNegFiltering extends ILinCombFiltering {

		@Override
		public void awakeOnInf(int varIdx) throws ContradictionException {
			if ( varIdx < nbPosVars) filterLB();
			else filterUB();
		}

		@Override
		public void awakeOnSup(int varIdx) throws ContradictionException {
			if ( varIdx < nbPosVars) filterUB();
			else filterLB();
		}

		@Override
		public boolean hasConsitentLowerBound(int mylb) {
			return hasConsistentLowerBoundPV(mylb) && hasConsistentLowerBoundNV(mylb);
		}

		@Override
		public boolean hasConsitentUpperBound(int myub) {
			return hasConsistentUpperBoundPV(myub) && hasConsistentUpperBoundNV(myub);			
		}

		@Override
		public int computeLowerBound() {
			return cste + computeLowerBoundPV() + computeLowerBoundNV();
		}

		@Override
		public int computeUpperBound() {
			return cste + computeUpperBoundPV() + computeUpperBoundNV();
		}

		@Override
		public void propagateNewLowerBound(int mylb)
		throws ContradictionException {
			propagateLowerBoundPV(mylb);
			propagateLowerBoundNV(mylb);
		}

		@Override
		public void propagateNewUpperBound(int myub)
		throws ContradictionException {
			propagateUpperBoundPV(myub);
			propagateUpperBoundNV(myub);
		}
	}

	final class PosFiltering extends ILinCombFiltering {

		@Override
		public void awakeOnInf(int varIdx) throws ContradictionException {
			filterLB();
		}

		@Override
		public void awakeOnSup(int varIdx) throws ContradictionException {
			filterUB();
		}

		@Override
		public boolean hasConsitentLowerBound(int mylb) {
			return hasConsistentLowerBoundPV(mylb);
		}

		@Override
		public boolean hasConsitentUpperBound(int myub) {
			return hasConsistentUpperBoundPV(myub);			
		}

		@Override
		public int computeLowerBound() {
			return cste + computeLowerBoundPV();
		}

		@Override
		public int computeUpperBound() {
			return cste + computeUpperBoundPV();
		}

		@Override
		public void propagateNewLowerBound(int mylb)
		throws ContradictionException {
			propagateLowerBoundPV(mylb);
		}

		@Override
		public void propagateNewUpperBound(int myub)
		throws ContradictionException {
			propagateUpperBoundPV(myub);
		}
	}

	final class NegFiltering extends ILinCombFiltering {

		@Override
		public void awakeOnInf(int varIdx) throws ContradictionException {
			filterUB();
		}

		@Override
		public void awakeOnSup(int varIdx) throws ContradictionException {
			filterLB();
		}

		@Override
		public boolean hasConsitentLowerBound(int mylb) {
			return hasConsistentLowerBoundNV(mylb);
		}

		@Override
		public boolean hasConsitentUpperBound(int myub) {
			return hasConsistentUpperBoundNV(myub);			
		}

		@Override
		public int computeLowerBound() {
			return cste + computeLowerBoundNV();
		}

		@Override
		public int computeUpperBound() {
			return cste + computeUpperBoundNV();
		}

		@Override
		public void propagateNewLowerBound(int mylb)
		throws ContradictionException {
			propagateLowerBoundNV(mylb);
		}

		@Override
		public void propagateNewUpperBound(int myub)
		throws ContradictionException {
			propagateUpperBoundNV(myub);
		}
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
