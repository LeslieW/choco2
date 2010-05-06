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
package parser.absconparseur.components;

import parser.absconparseur.PredicateTokens;


public class PWeightedSum extends PGlobalConstraint {
	private final int[] coeffs;

	private final PredicateTokens.RelationalOperator operator;

	private final int limit;

	public int[] getCoeffs() {
		return coeffs;
	}

	public PredicateTokens.RelationalOperator getOperator() {
		return operator;
	}

	public PWeightedSum(String name, PVariable[] scope, int[] coeffs, PredicateTokens.RelationalOperator operator, int limit) {
		super(name, scope);
		this.coeffs = coeffs;
		this.operator = operator;
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	public long computeCostOf(int[] tuple) {
		int sum = 0;
		for (int i = 0; i < coeffs.length; i++)
			sum += coeffs[i] * tuple[i];
		boolean satisfied = operator == PredicateTokens.RelationalOperator.EQ ? sum == limit : operator == PredicateTokens.RelationalOperator.NE ? sum != limit : operator == PredicateTokens.RelationalOperator.GE ? sum >= limit : operator == PredicateTokens.RelationalOperator.GT ? sum > limit
				: operator == PredicateTokens.RelationalOperator.LE ? sum <= limit : sum < limit;
		return satisfied ? 0 : 1;
	}

	public String toString() {
		String s = super.toString() + " : weightedSum\n\t";
		for (int i = 0; i < coeffs.length; i++)
			s += coeffs[i] + "*" + scope[i].getName() + ' ';
		s += PredicateTokens.RelationalOperator.getStringFor(operator) + ' ' + limit;
		return s;
	}

	public boolean isGuaranteedToBeOverflowFree() {
		int sumL = 0;
		double sumD = 0;

		for (int i = 0; i < scope.length; i++) {
			int[] values = scope[i].getDomain().getValues();
			int maxAbsoluteValue = Math.max(Math.abs(values[0]), Math.abs(values[values.length - 1]));
			sumL+=Math.abs(coeffs[i])*maxAbsoluteValue;
			sumD+=Math.abs(coeffs[i])*maxAbsoluteValue;
		}
        return !(sumL != sumD || Double.isInfinite(sumD));
    }
}
