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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;

import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

public abstract class PConstraint {

    protected final static Logger LOGGER = ChocoLogging.getParserLogger();

    private int index;

    protected String name;

	protected PVariable[] scope;

    protected Constraint chocoCstr;

    public String getName() {
		return name;
	}

	public PVariable[] getScope() {
		return scope;
	}

	public int getPositionInScope(PVariable variable) {
		for (int i = 0; i < scope.length; i++)
			if (variable == scope[i])
				return i;
		return -1;
	}

	public int getArity() {
		return scope.length;
	}

	public PConstraint(String name, PVariable[] scope) {
		this.name = name;
		this.scope = scope;
        this.index = parseInt(name.substring(1).replaceAll("_", "00"));
    }

	public int getMaximalCost() {
		return 1;
	}

	/**
	 * For CSP, returns 0 is the constraint is satified and 1 if the constraint is violated. <br>
	 * For WCSP, returns the cost for the given tuple.
	 */
	public abstract long computeCostOf(int[] tuple);

	public String toString() {
		String s = "  constraint " + name + " with arity = " + scope.length + ", scope = ";
		s += scope[0].getName();
		for (int i = 1; i < scope.length; i++)
			s += " " + scope[i].getName();
		return s;
	}

	public boolean isGuaranteedToBeDivisionByZeroFree() {
		return true;
	}

	public boolean isGuaranteedToBeOverflowFree() {
		return true;
	}

    public Constraint getChocoCstr() {
        return chocoCstr;
    }

    public void setChocoCstr(Constraint chocoCstr) {
        this.chocoCstr = chocoCstr;
    }

    public int hashCode() {
        return index;
    }
}
