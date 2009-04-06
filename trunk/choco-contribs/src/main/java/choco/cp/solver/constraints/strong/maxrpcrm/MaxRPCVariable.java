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
package choco.cp.solver.constraints.strong.maxrpcrm;

import choco.cp.solver.constraints.strong.SCVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Collection;

public class MaxRPCVariable extends SCVariable<AbstractMaxRPCConstraint> {
	private final Collection<Clique> threeCliques;

	public MaxRPCVariable(IntDomainVar sVariable, Integer id) {
		super(sVariable, id);
		threeCliques = new ArrayList<Clique>();
	}

	public Collection<Clique> getCliques() {
		return threeCliques;
	}

	public void addClique(Clique threeClique) {
		threeCliques.add(threeClique);
	}

}
