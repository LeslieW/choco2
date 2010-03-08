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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractBinSetIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class SetCard extends AbstractBinSetIntSConstraint {

	// operator pour la contrainte de cardinalit� :
	// inf & !sup -> card(set) <= int
	// sup & !inf -> card(set) => int
	// inf && sup -> card(set) = int
	protected boolean inf = false;
	protected boolean sup = false;

	public SetCard(SetVar sv, IntDomainVar iv, boolean inf, boolean sup) {
		super(iv, sv);
		this.inf = inf;
		this.sup = sup;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void reactOnInfAndEnvEvents(int envSize) throws ContradictionException {
		if (v0.getInf() > envSize)
			this.fail();
		else if (v0.getInf() == envSize) {
			DisposableIntIterator it = v1.getDomain().getOpenDomainIterator();
            try{
                while (it.hasNext())
                    v1.addToKernel(it.next(), this, false);
            }finally {
                it.dispose();
            }
		}
	}

	public void reactOnSupAndKerEvents(int kerSize) throws ContradictionException {
		if (v0.getSup() < kerSize)
			this.fail();
		else if (v0.getSup() == kerSize) {
			DisposableIntIterator it = v1.getDomain().getOpenDomainIterator();
            try{
                while (it.hasNext())
                    v1.remFromEnveloppe(it.next(), this, false);
            }finally {
                it.dispose();
            }
		}
	}

	public void filter() throws ContradictionException {
		int envSize = v1.getEnveloppeDomainSize();
		int kerSize = v1.getKernelDomainSize();
		if (inf && sup) {
			if (v0.getSup() < kerSize || v0.getInf() > envSize)
				this.fail();
			else if (kerSize < envSize) {
				if (v0.getInf() == envSize) {
					DisposableIntIterator it = v1.getDomain().getOpenDomainIterator();
                    try{
                        while (it.hasNext())
                            v1.addToKernel(it.next(), this, false);
                    }finally{
                        it.dispose();
                    }
				} else if (v0.getSup() == kerSize) {
					DisposableIntIterator it = v1.getDomain().getOpenDomainIterator();
                    try{
                        while (it.hasNext())
                            v1.remFromEnveloppe(it.next(), this, false);
                    }finally {
                        it.dispose();
                    }
				}
			}
		} else if (inf) {
			if (v0.getSup() < kerSize)
				this.fail();
			else if (kerSize < envSize) {
				if (v0.getSup() == kerSize) {
					DisposableIntIterator it = v1.getDomain().getOpenDomainIterator();
                    try{
                        while (it.hasNext())
                            v1.remFromEnveloppe(it.next(), this, false);
                    }finally {
                        it.dispose();
                    }
				}
			}
		} else {
			if (v0.getInf() > envSize)
				this.fail();
			else if (kerSize < envSize) {
				if (v0.getInf() == envSize) {
					DisposableIntIterator it = v1.getDomain().getOpenDomainIterator();
                    try{
                        while (it.hasNext())
                            v1.addToKernel(it.next(), this, false);
                    }finally {
                        it.dispose();
                    }
				}
			}
		}
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		if (inf) reactOnInfAndEnvEvents(v1.getEnveloppeDomainSize());
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		if (sup) reactOnSupAndKerEvents(v1.getKernelDomainSize());
	}

	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		filter();
	}

	public void awakeOnkerAdditions(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		if (inf) {
			int kerSize = v1.getKernelDomainSize();
			v0.updateInf(kerSize, this, false);
			reactOnSupAndKerEvents(kerSize);
		}
	}

	public void awakeOnEnvRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		if (sup) {
			int envSize = v1.getEnveloppeDomainSize();
			v0.updateSup(envSize, this, false);
			reactOnInfAndEnvEvents(envSize);
		}
	}


	public void awakeOnInst(int varIdx) throws ContradictionException {
		if (varIdx == 1) {
			int kerSize = v1.getKernelDomainSize();
			if (inf && sup)
				v0.instantiate(kerSize, this, false);
			else if (inf)
				v0.updateInf(kerSize, this, false);
			else
				v0.updateSup(kerSize, this, false);
		} else {
			filter();
		}
	}

	public boolean isSatisfied() {
		if (inf && sup)
			return v1.getKernelDomainSize() == v0.getVal();
		else if (inf)
			return v1.getKernelDomainSize() <= v0.getVal();
		else
			return v1.getKernelDomainSize() >= v0.getVal();
	}

	public String toString() {
		if (inf && !sup)
			return " |" + v1 + "| <= " + v0;
		else if (!inf && sup)
			return " |" + v1 + "| >= " + v0;
		else return " |" + v1 + "| = " + v0; 	
	}

	public String pretty() {
		if (inf && !sup)
			return " |" + v1.pretty() + "| <= " + v0.pretty();
		else if (!inf && sup)
			return " |" + v1.pretty() + "| >= " + v0.pretty();
		else return " |" + v1.pretty() + "| = " + v0.pretty();
	}


	public void awake() throws ContradictionException {
		if (inf && sup) {
			v0.updateInf(v1.getKernelDomainSize(), this, false);
			v0.updateSup(v1.getEnveloppeDomainSize(), this, false);
		} else if (inf) {
			v0.updateInf(v1.getKernelDomainSize(), this, false);
		} else
			v0.updateSup(v1.getEnveloppeDomainSize(), this, false);
        propagate();
    }

	public void propagate() throws ContradictionException {
		filter();
	}

	public boolean isConsistent() {
		return (v1.isInstantiated() && v0.isInstantiated() && isSatisfied());
	}

	public Boolean isEntailed() {
		if (inf & sup) {
			if (v0.getInf() > v1.getEnveloppeDomainSize())
				return Boolean.FALSE;
			else if (v0.getSup() < v1.getKernelDomainSize())
				return Boolean.FALSE;
			else if (v0.isInstantiated() && v1.isInstantiated())
				return Boolean.TRUE;
			else
				return null;
		} else if (inf) {
			if (v0.getSup() < v1.getKernelDomainSize())
				return Boolean.FALSE;
			else if (v0.isInstantiated() && v1.isInstantiated())
				return Boolean.TRUE;
			else
				return null;
		} else {
			if (v0.getInf() > v1.getEnveloppeDomainSize())
				return Boolean.FALSE;
			else if (v0.isInstantiated() && v1.isInstantiated())
				return Boolean.TRUE;
			else
				return null;
		}

	}
}
