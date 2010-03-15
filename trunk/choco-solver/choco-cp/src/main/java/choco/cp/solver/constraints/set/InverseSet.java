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
package choco.cp.solver.constraints.set;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractLargeSetIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;



/**
 * 
 * A constraint stating that
 * value j belongs to the s[i] set variable
 * iff integer variable x[j] equals to i.
 * This constraint models the inverse s: I -> P(J)
 * of a function x: J -> I (I and J sets of integers)
 * adapted from InverseChanneling
 *
 * @author Sophie Demassey
 */

public final class InverseSet extends AbstractLargeSetIntSConstraint {

	boolean init; 

	public InverseSet (IntDomainVar[] x, SetVar[] s) {
		super(x, s);
		init = true;
	}


	
	/**
	 * Filtering Rule 0 : i>s.length => x[j]!=i \forall j
	 * j>x.length => j \not\in s[i] \forall i 
	 */
	public void filterFromIndices() throws ContradictionException {
		int n = getNbSetVars() - 1;
		for (int j = 0; j < ivars.length; j++) {
			ivars[j].updateInf(0, this, false);
			ivars[j].updateSup(n, this, false);
		}
		n= getNbIntVars()-1;
		for (int i = 0; i < svars.length; i++) {
			while (svars[i].getEnveloppeInf()< 0) {
				svars[i].remFromEnveloppe( svars[i].getEnveloppeInf(), this, false);
			}
			while (svars[i].getEnveloppeSup()> n) {
				svars[i].remFromEnveloppe( svars[i].getEnveloppeSup(), this, false);
			}
		}
	}

	@Override
	public void awake() throws ContradictionException {
		filterFromIndices(); 
		super.awake();
	}

	/**
	 * Filtering Rule 1 : j \in s[i] => x[j]=i (then propagate Rule 3)
	 */
	@Override
	public void awakeOnKer (int i, int j) throws ContradictionException {
		assert( isSetVarIndex(i));
		ivars[j].instantiate( i, this, true);
	}

	/**
	 * Filtering Rule 2 : j \not\in s[i] => x[j]!=i
	 */
	@Override
	public void awakeOnEnv (int i, int j) throws ContradictionException {
		assert( isSetVarIndex(i));
		ivars[j].removeVal( i, this, false);
	}

	/**
	 * propagation on domain revision of set variable s[i] : rules 1 & 2
	 */
	private void filterSetVar (int i) throws ContradictionException {
		for (int j = 0; j < getNbIntVars(); j++) {
			if (svars[i].isInDomainKernel(j)) {
				ivars[j].instantiate(i, this, true);
			} else if (!svars[i].isInDomainEnveloppe(j)) {
				ivars[j].removeVal(i, this, false);
			}
		}
	}

	/**
	 * Filtering Rule 3 : x[j]=i => j \in s[i] and j \not\in s[i'] \forall i'!=i
	 * Rules 1 & 2 : s[i]=V => x[j]=i \iff j \in V
	 */
	@Override
	public void awakeOnInst (int x) throws ContradictionException {
		if( isSetVarIndex(x)) { filterSetVar(x);}
		else {
			final int iv = getIntVarIndex(x);
			final int s = ivars[iv].getVal();
			svars[s].addToKernel(iv, this, false);
			for (int i = 0; i < s; i++) {
				svars[i].remFromEnveloppe(iv, this, false);
			}	
			for (int i = s + 1; i < svars.length; i++) {
				svars[i].remFromEnveloppe(iv, this, false);
			}
		}
	}


	/**
	 * Filtering Rule 4 : x[j]!=i => j \not\in s[i]
	 */
	@Override
	public void awakeOnRem (int x, int v) throws ContradictionException {
		if (isSetVarIndex(x)) awakeOnEnv(x, v);
		else svars[v].remFromEnveloppe( getIntVarIndex(x), this, false);
	}

	/**
	 * propagation on a var: Rules 1 & 2 (integer) or 3 & 4 (set)
	 */
	public void awakeOnVar (int x) throws ContradictionException {
		if ( getVar(x).isInstantiated()) awakeOnInst(x);
		else if (isSetVarIndex(x)) filterSetVar(x);
		else {
			final int iv = getIntVarIndex(x);
			for (int i = 0; i < svars.length; i++) {
				if (!ivars[iv].canBeInstantiatedTo(i)) {
					svars[i].remFromEnveloppe(iv, this, false);
				}
			}
		} 
	}

	public void propagate () throws ContradictionException {
		for (int x = 0; x < getNbVars(); x++) {
			awakeOnVar(x);
		}        
	}

	/** @return true if the set vars are all consistent with the j-th integer (instantiated) */
	public boolean isSatisfied (int j) {
		int s = ivars[j].getVal();
		if (!svars[s].isInDomainKernel(j)) return false;
		for (int i = 0; i < svars.length; i++)
			if (svars[i].isInDomainEnveloppe(j) && i!=s) return false;
		return true;
	}

	public boolean isSatisfied () {
		for (int j = 0; j < getNbIntVars(); j++) {
			if (!isSatisfied(j)) return false;
		}
		return true;
	}

	public boolean isConsistent () {
		for (int j = 0; j < ivars.length; j++) {
			if (ivars[j].isInstantiated()) {
				if (!isSatisfied(j)) return false;
			} else {
				for (int i = 0; i < svars.length; i++) {
					if (!ivars[j].canBeInstantiatedTo(i) ) {
						if (svars[i].isInDomainEnveloppe(j)) return false;
					} else if (svars[i].isInDomainKernel(j) ||
							!svars[i].isInDomainEnveloppe(j)) return false;
				}
			}
		}
		return true;
	}
}
