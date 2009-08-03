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
package choco.kernel.solver.search.set;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.variables.set.SetVar;

//**************************************************
//*                   J-CHOCO                      *
//*   Copyright (C) F. Laburthe, 1999-2003         *
//**************************************************
//*  an open-source Constraint Programming Kernel  *
//*     for Research and Education                 *
//**************************************************

public abstract class AbstractSetBranching extends AbstractIntBranching {

    final static String[] LOG_DECISION_MSG = new String[]{"contains ", "contains not ", "??"};

	public int getNextBranch(Object x, int i) {
		if (i == 1) {
			return 2;
		}
		return 0;
	}

	public boolean finishedBranching(Object x, int i) {

		return i == 2;
	}

	@Override
	public void goDownBranch(Object x, int numBranch) throws ContradictionException {
		Object[] xx = (Object[]) x;
		SetVar var = (SetVar) xx[0];
		int val = (Integer) xx[1];
		if (numBranch == 1) {
			var.setValIn(val);
			getManager().solver.propagate();
		} else if (numBranch == 2) {
			var.setValOut(val);
			getManager()
                    .solver.propagate();
		}
	}



	/**
	 * @see choco.kernel.solver.branch.AbstractIntBranching#goUpBranch(java.lang.Object, int)
	 */
	@Override
	public void goUpBranch(Object x, int i) throws ContradictionException {}

//	public void goUpBranch(Object x, int i, int numBranch) throws ContradictionException {
//		this.goUpBranch(x, numBranch);
//	}

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
        Object[] o = (Object[])branchObject;
        st.append(o[0]);
        switch(branchIndex){
            case 1:
                st.append(LOG_DECISION_MSG[0]);
            case 2:
                st.append(LOG_DECISION_MSG[1]);
            default:
                st.append(LOG_DECISION_MSG[2]);
        }
        st.append(o[1]);
        return st.toString();
    }

    @Override
	protected final String getLogMessage() {
		return LOG_MSG_FORMAT_WITH_BRANCH;
	}
}
