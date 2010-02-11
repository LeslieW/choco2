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
package choco.cp.solver.constraints.global.scheduling;


import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.global.scheduling.IPrecedenceNetwork;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public class Precedence  extends AbstractTaskSConstraint {

	protected static final int DIR_IDX = 6;

	protected static final int ORIG = 0;

	protected static final int DEST = 1;

	protected final IPrecedenceNetwork network;

	/*Builder*/
	public Precedence(final IPrecedenceNetwork network, final TaskVar task1, final TaskVar task2, final IntDomainVar direction){
		super(task1,task2, direction);
		this.network = network;
	}

	protected Precedence(final IPrecedenceNetwork network, final TaskVar task1, final TaskVar task2, final IntDomainVar... otherVars){
		super(task1,task2, otherVars);
		this.network = network;
	}

	protected final int opposite(int val) {
		return val == ORIG ? DEST : ORIG;
	}

	protected boolean needCheck() {
		return !vars[DIR_IDX].isInstantiated();
	}


	protected final boolean isSatisfied(int i, int j) {
		return taskvars[i].getLCT() < taskvars[j].getEST();
	}

	protected final boolean isNotSatisfied(int i, int j) {
		return taskvars[i].getECT() >  taskvars[j].getLST();
	}

	protected final boolean checkTimeBounds(int i, int j) throws ContradictionException {
		boolean res = true;
		if(isSatisfied(i,j)) {
			//the constraint is already satisfied
			setPrecedence(j);
		}else if(isNotSatisfied(i, j)) {
			//the opposite precedence should be added
			setPrecedence(i);
			notifyNetwork(j, i);
			notifySolver(j, i);
		} else {res = false;}
		return res;
	}

	protected final boolean checkTimeBounds() throws ContradictionException {
		return checkTimeBounds(ORIG, DEST) || checkTimeBounds(DEST, ORIG);
	}

	protected final boolean checkNetwork() throws ContradictionException {
		boolean res = true;
		if(network.isConnected( taskvars[ORIG], taskvars[DEST])) {
			setPrecedence(DEST);
			notifySolver(ORIG, DEST);

		}else if(network.isConnected( taskvars[DEST], taskvars[ORIG])) {
			setPrecedence(ORIG);
			notifySolver(DEST, ORIG);
		}else { res= false;}
		return res;
	}

	protected final void setPrecedence(int dir) throws ContradictionException {
		vars[DIR_IDX].instantiate(dir, cIndices[DIR_IDX]);
	}

	protected final void notifyNetwork(int i, int j) throws ContradictionException {
		network.firePrecedenceAdded(taskvars[i], taskvars[j]);
	}

	protected final void notifySolver(int i, int j) {
		//notify propagation engine
		final int idxI = getStartIndex(i);
		final int idxJ = getStartIndex(j);
		propagationEngine.postUpdateInf(vars[idxI], cIndices[idxI]);
		propagationEngine.postUpdateSup(vars[idxJ], cIndices[idxJ]);
	}

	protected final void notifyDecision() throws ContradictionException {
		final int dest = vars[DIR_IDX].getVal();
		final int orig = opposite(dest);
		notifyNetwork(orig, dest);
		notifySolver(orig, dest);

	}



	@Override
	public void awake() throws ContradictionException {
		if(vars[DIR_IDX].isInstantiated()) {
			notifyDecision();
		}
		super.awake();
	}



	@Override
	public void awakeOnInf(int varIdx) throws ContradictionException {
		if( needCheck()) {
			if( ! checkNetwork()) {
				final int n = getNbTasks();
				int orig, dest;
				if( varIdx < n) {
					//start event
					orig = opposite(varIdx);
					dest = varIdx;
					if(isSatisfied(orig, dest)) {
						setPrecedence(dest);
					}
				}else if(varIdx < 2*n) {
					//end event
					orig = varIdx - n ;
					dest = opposite(orig);
					if(isNotSatisfied(orig, dest)) {
						setPrecedence(orig);
						notifyNetwork(dest, orig);
						notifySolver(dest, orig);
					}
				} 
			}
		}
	}




	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		if( idx == DIR_IDX) {
			notifyDecision();
		}else {
			this.constAwake(false);
		}
	}

	@Override
	public void awakeOnSup(int varIdx) throws ContradictionException {
		if( needCheck()) {
			if(!checkNetwork()) {
				final int n = getNbTasks();
				int orig, dest;
				if( varIdx < n) {
					//start event
					dest = varIdx;
					orig = opposite(dest);
					if(isNotSatisfied(orig, dest)) {
						setPrecedence(orig);
						notifyNetwork(dest, orig);
						notifySolver(dest, orig);
					}
				}else if(varIdx < 2*n) {
					//end event
					orig = varIdx - n;
					dest = opposite(varIdx);
					if(isSatisfied(orig, dest)) {
						setPrecedence(dest);
					}
				}
			}
		}
	}

	@Override
	public void propagate() throws ContradictionException {
		if( needCheck() ) {
			if( ! checkNetwork()) {
				checkTimeBounds();
			}
		}
	}

	@Override
	public boolean isSatisfied() {
		int dest = vars[DIR_IDX].getVal();
		int orig = opposite(dest);
		return isSatisfied(orig, dest);
	}

	@Override
	public Boolean isEntailed() {
		if(vars[DIR_IDX].isInstantiated()) {
			int dest = vars[DIR_IDX].getVal();
			int orig = opposite(dest);
			if( isSatisfied(orig, dest)) {return Boolean.TRUE;}
			else if( isNotSatisfied(orig, dest)) {return Boolean.FALSE;}
		}
		return null;
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		//TODO
		return null;
	}


	protected final String getSign() {
		return vars[DIR_IDX].isInstantiatedTo(DEST) ? " << " : 
			vars[DIR_IDX].isInstantiatedTo(ORIG) ? " >> " : " ? ";
	}

	@Override
	public String pretty() {
		return taskvars[ORIG].pretty()+getSign()+taskvars[DEST].pretty();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return taskvars[ORIG].getName()+getSign()+taskvars[DEST].getName();
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		if(tuple[DIR_IDX] == 1) return tuple[startOffset + ORIG] <= tuple[DEST];
		else if(tuple[DIR_IDX] == 0) return tuple[startOffset + DEST] <= tuple[ORIG];
		else return false;
	}
	
	
}


