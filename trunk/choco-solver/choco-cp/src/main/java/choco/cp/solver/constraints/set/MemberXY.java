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

/**
 * Ensure that an int variable belongs to a set variable
 */
public final class MemberXY extends AbstractBinSetIntSConstraint {


	public MemberXY(SetVar set, IntDomainVar iv) {
		super(iv, set);
	}

	public void filter() throws ContradictionException {
		DisposableIntIterator it = v0.getDomain().getIterator();
		int count = 0, val = Integer.MAX_VALUE;
		while (it.hasNext()) {
			val = it.next();
			if (v1.isInDomainEnveloppe(val)) {
				count += 1;
				if (count > 1) break;
			}
		}
        it.dispose();
		if (count == 0)
			this.fail();
		else if (count == 1) {
			v0.instantiate(val, this, false);
			v1.addToKernel(val, this, false);
		}
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		filter();
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		filter();
	}

	//TODO : Store the number of values shared by the Int and the Set domain
	public void awakeOnRem(int idx, int x) throws ContradictionException {
		filter();
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		// Nothing to do
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		v0.removeVal(x, this, false);
		filter();
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		if (varIdx == 0)
			v1.addToKernel(v0.getVal(), this, false);
		else
			filter();
	}


	public void propagate() throws ContradictionException {
		DisposableIntIterator it = v0.getDomain().getIterator();
        try{
            while (it.hasNext()) {
                int val = it.next();
                if (!v1.isInDomainEnveloppe(val)) {
                    v0.removeVal(val, this, false);
                }
            }
        }finally {
            it.dispose();
        }
		filter();
	}

	public boolean isSatisfied() {
		return v1.isInDomainKernel(v0.getVal());
	}

	public boolean isConsistent() {
		DisposableIntIterator it = v0.getDomain().getIterator();
		while (it.hasNext()) {
			if (!v1.isInDomainKernel(it.next())){
                it.dispose();
                return false;
            }
		}
        it.dispose();
		return true;
	}

	public String toString() {
		return v0 + " is in " + v1;
	}

	public String pretty() {
		return v0.pretty() + " is in " + v1.pretty();
	}


	public Boolean isEntailed() {
		boolean allInKernel = true;
        boolean allOutEnv = true;
        DisposableIntIterator it = v0.getDomain().getIterator();
        while(it.hasNext()){
            int val = it.next();
            if(!v1.isInDomainKernel(val)){
                allInKernel = false;
            }
            if(v1.isInDomainEnveloppe(val)){
                allOutEnv = false;
            }
        }
        it.dispose();
        if(allInKernel){
            return Boolean.TRUE;
        }else if(allOutEnv){
            return Boolean.FALSE;
        }
        return null;
	}
}
