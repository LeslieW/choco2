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
import parser.absconparseur.intension.PredicateManager;

import static java.lang.Integer.parseInt;

public class PPredicate { 

	private String name;

	private String[] formalParameters;

	private String functionalExpression;

	private String[] unversalPostfixExpression;

    private int index;

    public String getName() {
		return name;
	}

	public String[] getFormalParameters() {
		return formalParameters;
	}

	public String[] getUniversalPostfixExpression() {
		return unversalPostfixExpression;
	}

	public PPredicate(String name, String formalParametersExpression, String functionalExpression) {
		this.name = name;
		this.formalParameters =  PredicateManager.extractFormalParameters(formalParametersExpression,true);
		this.functionalExpression = functionalExpression.trim();
		this.unversalPostfixExpression = PredicateManager.buildUniversalPostfixExpression(functionalExpression, formalParameters);
        this.index = parseInt(name.substring(1).replaceAll("_", "00"));
    }

    public PPredicate(String name, String[] formalParametersExpression, String functionalExpression) {
		this.name = name;
		this.formalParameters =  formalParametersExpression;
		this.functionalExpression = functionalExpression.trim();
		this.unversalPostfixExpression = PredicateManager.buildUniversalPostfixExpression(functionalExpression, formalParameters);
        this.index = parseInt(name.substring(1).replaceAll("_", "000"));
    }


    public void setFormalParameters(String[] formalParameters) {
        this.formalParameters = formalParameters;
    }

    public void setFunctionalExpression(String functionalExpression) {
        this.functionalExpression = functionalExpression;
    }

    public String toString() {
		return "  predicate " + name + " with functional expression = " + functionalExpression + " and (universal) postfix expression = " + Toolkit.buildStringFromTokens(unversalPostfixExpression);
	}

    public String getFunctionalExpression() {
        return functionalExpression;
    }

    public int hashCode() {
		return index;
	}
}
