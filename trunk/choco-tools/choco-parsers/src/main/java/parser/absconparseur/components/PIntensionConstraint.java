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


import parser.absconparseur.Toolkit;
import parser.absconparseur.intension.EvaluationManager;
import parser.absconparseur.intension.PredicateManager;



public class PIntensionConstraint extends PConstraint {
	private final PFunction function; // a predicate is a kind of function - so function may be a PPredicate

	private final String[] universalPostfixExpression;

	public PFunction getFunction() {
		return function;
	}

	public String[] getUniversalPostfixExpression() {
		return universalPostfixExpression;
	}

	public PIntensionConstraint(String name, PVariable[] scope, PFunction function, String effectiveParametersExpression) {
		super(name, scope);
		this.function = function;

		String[] variableNames = new String[scope.length];
		for (int i = 0; i < variableNames.length; i++)
			variableNames[i] = scope[i].getName();

		this.universalPostfixExpression = PredicateManager.buildNewUniversalPostfixExpression(function.getUniversalPostfixExpression(), effectiveParametersExpression, variableNames);
		// System.out.println(universalPredicateExpression);
	}

	public long computeCostOf(int[] tuple) {
		EvaluationManager evaluationManager = new EvaluationManager(universalPostfixExpression);
		long result = evaluationManager.evaluate(tuple);
		if (function instanceof PPredicate) {
			boolean satisfied = result == 1;
		return satisfied ? 0 : 1;
	}
		return result;
	}

	public String toString() {
		return super.toString() + ", and associated function/predicate " + function.getName() + " and universal expression = " + Toolkit.buildStringFromTokens(universalPostfixExpression);
	}

	public boolean isGuaranteedToBeDivisionByZeroFree() {
		EvaluationManager evaluationManager = new EvaluationManager(universalPostfixExpression);
		return evaluationManager.isGuaranteedToBeDivisionByZeroFree(scope);
	}

	public boolean isGuaranteedToBeOverflowFree() {
		// LOGGER.info("cons " + name);
		EvaluationManager evaluationManager = new EvaluationManager(universalPostfixExpression);
		return evaluationManager.isGuaranteedToBeOverflowFree(scope);
	}
}
