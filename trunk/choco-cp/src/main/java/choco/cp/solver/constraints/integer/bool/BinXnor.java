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
* b0 XNOR b1
*/
public class BinXnor extends AbstractBinIntSConstraint{

public BinXnor(IntDomainVar v1, IntDomainVar v2) {
		super(v1, v2);
	}


    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    public void propagate() throws ContradictionException {
		if (v0.isInstantiated()){
            v1.instantiate(v0.getVal(), cIdx1);
        }
        if (v1.isInstantiated()){
            v0.instantiate(v1.getVal(), cIdx0);
        }
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		int val;
		if (idx == 0) {
			val = v0.getVal();
			if (val == 0) v1.instantiate(0, cIdx1);
            if (val == 1) v1.instantiate(1, cIdx1);
		} else {
			val = v1.getVal();
			if (val == 0) v0.instantiate(0, cIdx0);
            if (val == 1) v0.instantiate(1, cIdx0);
		}
	}

  public void awakeOnInf(int varIdx) throws ContradictionException {
  }

  public void awakeOnSup(int varIdx) throws ContradictionException {
  }

  public void awakeOnRem(int varIdx, int val) throws ContradictionException {
  }

  public boolean isSatisfied(int[] tuple) {
		return tuple[0] == tuple[1];
	}

	public Boolean isEntailed() {
		if (v0.isInstantiated() &&
				v1.isInstantiated())
			return v0.getVal() == v1.getVal();
		else return null;
	}


}
