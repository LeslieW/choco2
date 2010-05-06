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

import choco.kernel.model.variables.integer.IntegerVariable;


public class PVariable {
	private final int index;

	private final String name;

	private final PDomain domain;

	private PVariable representative;

	protected IntegerVariable chocovar;

	public String getName() {
		return name;
	}

	public PDomain getDomain() {
		return domain;
	}

	public PVariable(String name, PDomain domain) {
		this.name = name;
		this.domain = domain;
		this.index = Integer.parseInt(name.substring(1,name.length()));
	}

	public int getIdx() {
		return index;
	}

	public String toString() {
		return "  variable " + name + " with associated domain " + domain.getName();
	}

	public PVariable getRepresentative() {
		if (representative == null)
			return this;
		else return representative;
	}

	public void setRepresentative(PVariable representative) {
		this.representative = representative;
	}

	public boolean isFake() {
		return representative != null;
	}

	public IntegerVariable getChocovar() {
		return chocovar;
	}

	public void setChocovar(IntegerVariable chocovar) {
		this.chocovar = chocovar;
	}

	public int hashCode() {
		return index;
	}
}
