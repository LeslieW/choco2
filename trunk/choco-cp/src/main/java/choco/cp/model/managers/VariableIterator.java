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
package choco.cp.model.managers;

import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.variables.ComponentVariable;
import choco.kernel.model.variables.Variable;

import java.util.Iterator;

/**
 * @author Arnaud Malapert
 *
 */
public class VariableIterator implements Iterator<Variable> {

	public final Variable[] variables;

	private int n=0;

	Iterator<Variable> it;

	public VariableIterator(Variable[] variables) {
		super();
		this.variables = variables;
		it = (variables != null && variables.length > 0 ? getIterator(variables[n]) : null);
	}

	protected Iterator<Variable> getIterator(Variable v) {
		if (v instanceof ComponentVariable) {
			return ( (ComponentVariable) v).getVariableIterator();
		} else {
			return ChocoUtil.iterator(v);
		}
	}




	public boolean hasNext() {
		if (it == null) {
			return false;
		}
		while (n < variables.length && !it.hasNext()) {
			n++;
			if (n < variables.length) {
				it = getIterator(variables[n]);
			}
		}
		return n < variables.length && it.hasNext();
	}

	public Variable next() {
		return it.next();
	}

	public void remove() {
		it.remove();
	}




}
