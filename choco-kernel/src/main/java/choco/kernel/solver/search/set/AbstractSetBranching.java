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

import java.util.logging.Level;

//**************************************************
//*                   J-CHOCO                      *
//*   Copyright (C) F. Laburthe, 1999-2003         *
//**************************************************
//*  an open-source Constraint Programming Kernel  *
//*     for Research and Education                 *
//**************************************************

public abstract class AbstractSetBranching extends AbstractIntBranching {

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
		super.goDownBranch(x,numBranch);
		Object[] xx = (Object[]) x;
		SetVar var = (SetVar) xx[0];
		int val = ((Integer) xx[1]).intValue();
		if (numBranch == 1) {
			//System.out.println("addToKer[" + y + "," + i + "]");
			var.setValIn(val);
			manager.solver.propagate();
		} else if (numBranch == 2) {
			//System.out.println("remFromEnv[" + y + "," + i + "]");
			var.setValOut(val);
			manager.solver.propagate();
		}
	}



	/**
	 * @see choco.kernel.solver.branch.AbstractIntBranching#goUpBranch(java.lang.Object, int)
	 */
	@Override
	public void goUpBranch(Object x, int i) throws ContradictionException {
		super.goUpBranch(x, i);
	}


	public void goUpBranch(Object x, int i, int numBranch) throws ContradictionException {
		this.goUpBranch(x, numBranch);

	}
	
	
	@Override
	protected final String getLogMessage() {
		return getLogMessageWithBranch();
	}

	@Override
	protected Object getValueLogParameter(Object x, int branch) {
		return ((Object[]) x)[1];
	}

	@Override
	protected Object getVariableLogParameter(Object x) {
		return ((Object[]) x)[0];
	}

	@Override
	public int getFirstBranch(Object x) {
		return 0;
	}



	/*
	 * @deprecated replaced by the management incremental search (with a stack of BranchingTrace storing the
	 *             environment (local variables) associated to each choice point
	 */
	/*public boolean branchOn(Object x, int n) throws ContradictionException {
    AbstractGlobalSearchStrategy algo = manager;
    AbstractModel pb = algo.model;
    boolean nodeSuccess = false;
    boolean nodeFinished = false;
    int numBranch = getFirstBranch(x);
    algo.newTreeNode();
    try {
      do {
        boolean branchSuccess = false;
        try {
          //pb.getPropagationEngine().checkCleanState();
          pb.getEnvironment().worldPush();
          goDownBranch(x, numBranch);
          if (explore(n + 1)) {
            branchSuccess = true;
          }
        } catch (ContradictionException e) {
          ;
        }
        if (!branchSuccess) {
          pb.worldPop();
        }
        algo.endTreeNode();
        algo.postDynamicCut();
        goUpBranch(x, numBranch);
        if (branchSuccess) {
          nodeSuccess = true;
        }
        if (numBranch == 1) {
          numBranch = 2;
        } else {
          nodeFinished = true;
        }
      } while (!nodeSuccess && !nodeFinished);
    } catch (ContradictionException e) {
      nodeSuccess = false;
    }
    return nodeSuccess;
  }*/

}
