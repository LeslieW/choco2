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
package choco.cp.solver.constraints.integer.extension;

import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * A simple class that provides a method to check if a given
 * tuple is valid i.e. if it is ok regarding the current domain
 * of the variables
 */
public class ValidityChecker {

    public static int nbCheck = 0; 

    //variables sorted from the minimum domain to the max
	protected IntDomainVar[] sortedvs;
	protected int[] position;
	protected HashMap<IntDomainVar, Integer> mapinit;
	protected VarComparator vcomp;
	protected int arity;

	public ValidityChecker(int ari, IntDomainVar[] vars) {
		arity = ari;
		vcomp = new VarComparator();
		sortedvs = new IntDomainVar[arity];
		mapinit = new HashMap<IntDomainVar, Integer>(arity);
		position = new int[arity];
		for (int i = 0; i < vars.length; i++) {
			sortedvs[i] = vars[i];
			mapinit.put(vars[i], i);
			position[i] = i;
		}
	}

	/**
	 * Sort the variable to speedup the check
	 */
	public void sortvars() {
		Arrays.sort(sortedvs, vcomp);
		for (int i = 0; i < arity; i++) {
			position[i] = mapinit.get(sortedvs[i]);
		}
	}

	// Is tuple valide ?
	public boolean isValid(int[] tuple) {
		for (int i = 0; i < arity; i++)
			if (!sortedvs[i].canBeInstantiatedTo(tuple[position[i]])) return false;
		return true;
	}

    public boolean isValid(int[] tuple, int i) {
		return sortedvs[i].canBeInstantiatedTo(tuple[position[i]]);
	}

    /**
	 * Sort the variables by domain size
	 */
	public static class VarComparator implements Comparator {
		public int compare(Object o, Object o1) {
			IntDomainVar v1 = (IntDomainVar) o;
			IntDomainVar v2 = (IntDomainVar) o1;
			if (v1.getDomainSize() < v2.getDomainSize()) {
				return -1;
			} else if (v1.getDomainSize() == v2.getDomainSize()) {
				return 0;
			} else return 1;

		}
	}


}