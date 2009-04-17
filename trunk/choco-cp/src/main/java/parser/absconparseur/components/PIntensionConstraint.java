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

import java.util.BitSet;


public class PIntensionConstraint extends PConstraint {
	private PPredicate predicate;

	private String[] universalPostfixExpression;

	private String effectiveParametersExpression;

	private boolean knownAsExtensional = false;

	public PPredicate getPredicate() {
		return predicate;
	}

	public String[] getUniversalPostfixExpression() {
		return universalPostfixExpression;
	}

	public void setKnownAsExtensional() {
		knownAsExtensional = true;
	}

	public boolean isKnownAsExtensional() {
		return knownAsExtensional;
	}

	public PIntensionConstraint(String name, boolean known, PVariable[] scope, PPredicate predicate, String effectiveParametersExpression) {
		super(name, scope);
		this.predicate = predicate;
		this.effectiveParametersExpression = effectiveParametersExpression.trim();
		String[] variableNames = new String[scope.length];
		for (int i = 0; i < variableNames.length; i++)
			variableNames[i] = scope[i].getName();

		this.universalPostfixExpression = PredicateManager.buildNewUniversalPostfixExpression(predicate.getUniversalPostfixExpression(), effectiveParametersExpression, variableNames);
		knownAsExtensional = known;
	}

	public PIntensionConstraint(String name, PVariable[] scope, PPredicate predicate, String effectiveParametersExpression) {
		this(name,false,scope,predicate,effectiveParametersExpression);
	}

	public int computeCostOf(int[] tuple) {
		EvaluationManager evaluationManager = new EvaluationManager(universalPostfixExpression);
		boolean satisfied = evaluationManager.checkValues(tuple);
		return satisfied ? 0 : 1;
	}

	public boolean isADifference() {
		if (getArity() != 2) {
			return false;
		} else if (!knownAsExtensional) {
			PDomain p1 = getScope()[0].getDomain();
			PDomain p2 = getScope()[1].getDomain();
			if (p1.getNbValues() <= 100 && p2.getNbValues() <= 100) {
				int[] val1 = p1.getValues();
				BitSet b2 = p2.getBitSetDomain();
				int[] couple = new int[2];
				for (int i = 0; i < val1.length; i++) {
					int val = val1[i];
					if ((b2 != null && (val < 0 || b2.get(val)))
							|| (b2 == null && p2.contains(val))) {
						couple[0] = val;
						couple[1] = val;
						if (computeCostOf(couple) == 0)
							return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return super.toString() + ", and associated predicate " + predicate.getName() + " and universal predicate = " + Toolkit.buildStringFromTokens(universalPostfixExpression);
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

	public String getEffectiveParametersExpression() {
		return effectiveParametersExpression;
	}

	public void setEffectiveParametersExpression(String effectiveParametersExpression) {
		this.effectiveParametersExpression = effectiveParametersExpression;
	}
}
