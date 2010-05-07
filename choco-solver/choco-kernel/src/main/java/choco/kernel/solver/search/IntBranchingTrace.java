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
package choco.kernel.solver.search;


import choco.IPretty;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

/**
 * A class for keeping a trace of the search algorithm, through an IntBranching
 * (storing the current branching object, as well as the label of the current branch)
 */
public final class IntBranchingTrace implements IntBranchingDecision {

	private AbstractIntBranchingStrategy branching;

	private Object branchingObject;

	private int branchIndex;

	private int branchingValue = Integer.MAX_VALUE;



	public IntBranchingTrace() {
		super();
	}



	private IntBranchingTrace(AbstractIntBranchingStrategy branching,
			Object branchingObject, int branchIndex, int branchingValue) {
		super();
		this.branching = branching;
		this.branchingObject = branchingObject;
		this.branchIndex = branchIndex;
		this.branchingValue = branchingValue;
	}



	public final AbstractIntBranchingStrategy getBranching() {
		return branching;
	}

	public final void setBranching(AbstractIntBranchingStrategy branching) {
		this.branching = branching;
	}

	public final int getBranchIndex() {
		return branchIndex;
	}

	public final void setBranchIndex(final int branchIndex) {
		this.branchIndex = branchIndex;
	}

	public final void incrementBranchIndex() {
		branchIndex++;
	}

	public final int getBranchingValue() {
		return branchingValue;
	}

	public final void setBranchingValue(final int branchingValue) {
		this.branchingValue = branchingValue;
	}

	public final Object getBranchingObject() {
		return branchingObject;
	}

	@Override
	public final IntDomainVar getBranchingIntVar() {
		return (IntDomainVar) branchingObject;
	}

	@Override
	public final SetVar getBranchingSetVar() {
		return (SetVar) branchingObject;
	}

	@Override
	public final RealVar getBranchingRealVar() {
		return (RealVar) branchingObject;
	}

	public final void setBranchingObject(final Object branchingObject) {
		this.branchingObject = branchingObject;
	}

	public void clear() {
		branchIndex = 0;
		branchingObject = null;
		branching = null;
		branchingValue = Integer.MAX_VALUE;
	}

	public IntBranchingTrace copy() {
		return new IntBranchingTrace(branching, branchingObject, branchIndex, branchingValue);
	}
	
	//utility function
	public final void setIntVal() throws ContradictionException {
		( (IntVar) branchingObject).setVal(branchingValue);
	}
	
	public final void remIntVal() throws ContradictionException {
		( (IntDomainVar) branchingObject).remVal(branchingValue);
	}
	
	public final void setValInSet() throws ContradictionException {
		( (SetVar) branchingObject).setValIn(branchingValue);
	}
	
	public final void setValOutSet() throws ContradictionException {
		( (SetVar) branchingObject).setValOut(branchingValue);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(128);
		if (branchingObject instanceof IPretty) {
			b.append( ( (IPretty) branchingObject).pretty());
		}else {
			b.append(branchingObject);
		}
		if(branchingValue != Integer.MAX_VALUE) {
			b.append(" value=").append(branchingValue);
		}
		b.append(" branch ").append(branchIndex);
		return new String(b);
	}




}
