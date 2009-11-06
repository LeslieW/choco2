/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|        CHOCO solver       *
*                                                *
*     Choco is a java library for constraint     *
*     satisfaction problems (CSP), constraint    *
*     programming (CP) and explanation-based     *
*     constraint solving (e-CP). It is built     *
*     on a event-based propagation mechanism     *
*     with backtrackable structures.             *
*                                                *
*     Choco is an open-source software,          *
*     distributed under a BSD licence            *
*     and hosted by sourceforge.net              *
*                                                *
*     + website : http://choco.emn.fr            *
*     + support : choco@emn.fr                   *
*                                                *
*     Copyright (C) F. Laburthe,                 *
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.cp.solver.constraints.integer.bool;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* b0 XOR b1
*/
public class BinXor extends AbstractBinIntSConstraint {

    public BinXor(IntDomainVar b0, IntDomainVar b1) {
        super(b0, b1);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }


    public void propagate() throws ContradictionException {
		if (v0.isInstantiated()){
            v1.instantiate(Math.abs(v0.getVal()-1), cIdx1);
        }
        if (v1.isInstantiated()){
            v0.instantiate(Math.abs(v1.getVal()-1), cIdx0);
        }
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) {
			v1.instantiate(Math.abs(v0.getVal()-1), cIdx1);
		} else {
			v0.instantiate(Math.abs(v1.getVal()-1), cIdx0);
		}
	}

  public void awakeOnInf(int varIdx) throws ContradictionException {
  }

  public void awakeOnSup(int varIdx) throws ContradictionException {
  }

  public void awakeOnRem(int varIdx, int val) throws ContradictionException {
  }

  public boolean isSatisfied(int[] tuple) {
		return tuple[0] != tuple[1];
	}

	public Boolean isEntailed() {
		if (v0.isInstantiated() &&
				v1.isInstantiated())
			return v0.getVal() != v1.getVal();
		else return null;
	}

}
