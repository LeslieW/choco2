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
package choco.cp.solver.search.real;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.real.RealVarSelector;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;

/**
 * A binary branching assigning interval to subinterval.
 */

public final class AssignInterval extends AbstractIntBranching{
	protected RealVarSelector varSelector;
	protected ValIterator valIterator;
	private final static String[] LOG_DECISION_MSG = new String[]{"in first half of", "in second half of", "??"};

	public AssignInterval(RealVarSelector varSelector, ValIterator valIterator) {
		this.varSelector = varSelector;
		this.valIterator = valIterator;
	}

	public Object selectBranchingObject() throws ContradictionException {
		return varSelector.selectRealVar();
	}

	@Override
	public void goDownBranch(Object x, int i) throws ContradictionException {
		if (i == 1) {
			((RealVar) x).intersect(RealMath.firstHalf((RealVar) x));
			getManager().solver.propagate();
		} else if (i == 2) {
			((RealVar) x).intersect(RealMath.secondHalf((RealVar) x));
			getManager().solver.propagate();
		} else {
			LOGGER.severe("!! Not a valid value for AssignInterval branching !!");
		}
	}

	@Override
	public void goUpBranch(Object x, int i) throws ContradictionException {}

	public int getFirstBranch(Object x) {
		return valIterator.getFirstVal((RealVar) x);
	}

	public int getNextBranch(Object x, int i) {
		return valIterator.getNextVal((RealVar) x, i);
	}

	public boolean finishedBranching(Object x, int i) {
		return !valIterator.hasNextVal((RealVar) x, i);
	}

    /**
     * used for logging messages related to the search tree
     *
     * @param branchObject is the object of the branching
     * @param branchIndex  is the index of the branching
     * @return an string that will be printed between the branching object and the branch index
     *         Suggested implementations return LOG_DECISION_MSG[0] or LOG_DECISION_MSG[branchIndex]
     */
    @Override
    public String getDecisionLogMsg(Object branchObject, int branchIndex) {
        StringBuffer st = new StringBuffer();
        RealVar v = (RealVar)branchObject;
        st.append(v.getName());
        switch(branchIndex){
            case 1:
                st.append(LOG_DECISION_MSG[0]);
            case 2:
                st.append(LOG_DECISION_MSG[1]);
            default:
                st.append(LOG_DECISION_MSG[2]);
        }
        st.append(v.getDomain().pretty());
        return st.toString();
    }
}
