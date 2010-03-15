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

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.cp.solver.variables.set.SetVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.set.AbstractLargeSetIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.set.SetDomain;
import choco.kernel.solver.variables.set.SetVar;

/**
 * An abstract class used for MaxOfASet and MinOfaSet constraints
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
abstract class AbstractBoundOfASet extends AbstractLargeSetIntSConstraint {


	/** Index of the set variable*/
	public static final int SET_INDEX = 0;

	/**
	 * Index of the maximum variable.
	 */
	public static final int BOUND_INDEX = 0;

	/**
	 * First index of the variables among which the maximum should be chosen.
	 */
	public static final int VARS_OFFSET = 1;

	protected final static int SET_EVENTMASK = SetVarEvent.INSTSETEVENT + SetVarEvent.KEREVENT + SetVarEvent.ENVEVENT;

	protected final static int INT_EVENTMASK = IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;

	protected final Integer defaultValueEmptySet;

	public AbstractBoundOfASet(IntDomainVar[] intvars, SetVar setvar, Integer defaultValueEmptySet) {
		super(intvars, new SetVar[]{setvar});
		this.defaultValueEmptySet = defaultValueEmptySet;
		if(setvar.getEnveloppeInf()<0 || setvar.getEnveloppeSup()>intvars.length-2) {
			throw new SolverException("The enveloppe of the set variable "+setvar.pretty()+" is greater than the array");
		}
	}



	@Override
	public int getFilteredEventMask(int idx) {
		return idx > 0 ? INT_EVENTMASK : SET_EVENTMASK;
	}



	protected final boolean isInKernel(int idx) {
		return svars[SET_INDEX].isInDomainKernel(idx);
	}

	protected final boolean isInEnveloppe(int idx) {
		return svars[SET_INDEX].isInDomainEnveloppe(idx);
	}

	protected final SetDomain getSetDomain() {
		return svars[SET_INDEX].getDomain();
	}


	protected final boolean isEmptySet() {
		return this.svars[SET_INDEX].getEnveloppeDomainSize() == 0;
	}

	protected final boolean isNotEmptySet() {
		return this.svars[SET_INDEX].getKernelDomainSize()> 0;
	}

	protected final boolean isSetInstantiated() {
		return svars[SET_INDEX].isInstantiated();
	}

	protected final boolean updateBoundInf(int val) throws ContradictionException {
		return ivars[BOUND_INDEX].updateInf(val, this, false);
	}

	protected final boolean updateBoundSup(int val) throws ContradictionException {
		return ivars[BOUND_INDEX].updateSup(val, this, false);
	}

	protected abstract boolean removeFromEnv(int idx) throws ContradictionException;

	protected final boolean removeGreaterFromEnv(int idx, int maxValue) throws ContradictionException {
		if(ivars[VARS_OFFSET+idx].getInf()>maxValue) {
			return this.svars[SET_INDEX].remFromEnveloppe(idx, this, false);
		}
		return false;
	}

	protected final boolean removeLowerFromEnv(int idx, int minValue) throws ContradictionException {
		if(ivars[VARS_OFFSET+idx].getSup() < minValue ) {
			return this.svars[SET_INDEX].remFromEnveloppe(idx, this, false);
		}
		return false;
	}

	protected abstract boolean updateEnveloppe() throws ContradictionException;

	@Override
	public void awakeOnEnvRemovals(int idx, DisposableIntIterator deltaDomain)
	throws ContradictionException {
		if(idx==SET_INDEX && deltaDomain.hasNext()) {
			awakeOnEnv(idx, deltaDomain.next());
		}
	}


	@Override
	public void awakeOnkerAdditions(int idx, DisposableIntIterator deltaDomain)
	throws ContradictionException {
		if(idx==SET_INDEX && deltaDomain.hasNext()) {
			awakeOnKer(idx, deltaDomain.next());
		}
	}

	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not yet implemented on MaxOfAList");
	}


	@Override
	public boolean isConsistent() {
		return false;
	}

	protected void filterEmptySet()  throws ContradictionException {
		if(defaultValueEmptySet != null) {
			//a default value is assigned to the variable when the set is empty
			ivars[BOUND_INDEX].instantiate(defaultValueEmptySet.intValue(), this, false);
		}
		setEntailed();
	}
	@Override
	public final void propagate() throws ContradictionException {
		if( isEmptySet() ) filterEmptySet();
		else filter();
	}

	protected abstract void filter() throws ContradictionException;


	/**
	 * Propagation when a variable is instantiated.
	 *
	 * @param idx the index of the modified variable.
	 * @throws choco.kernel.solver.ContradictionException if a domain becomes empty.
	 */
	@Override
	public final void awakeOnInst(final int idx) throws ContradictionException {
		//CPSolver.flushLogs();
		if (idx >= 2*VARS_OFFSET) { //of the list
			final int i = idx-2*VARS_OFFSET;
			if(isInEnveloppe(i)) { //of the set
				awakeOnInstL(i);
			}
		} else if (idx == VARS_OFFSET) { // Minimum/Maximum variable
			awakeOnInstV();
		}else { 
			//set is instantiated, propagate
			propagate();
//			if(isEmptySet()) filterEmptySet();
//			else this.propagate();
		}
	}


	protected abstract void awakeOnInstL(int i) throws ContradictionException;

	protected abstract void awakeOnInstV() throws ContradictionException;


	protected abstract int isSatisfiedValue(DisposableIntIterator iter);
	
	@Override
	public boolean isSatisfied() {
		final DisposableIntIterator iter = svars[SET_INDEX].getDomain().getKernelIterator();
		if(iter.hasNext()) {
			return isSatisfiedValue(iter) == ivars[BOUND_INDEX].getVal(); 
		}else if(defaultValueEmptySet == null) return true;
		else return defaultValueEmptySet.intValue() == ivars[BOUND_INDEX].getVal();
	}

	protected String pretty(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(ivars[BOUND_INDEX].pretty());
		sb.append(" = ").append(name).append("(");
		sb.append(svars[SET_INDEX].pretty()).append(", ");
		sb.append(StringUtils.pretty(ivars, VARS_OFFSET, ivars.length));
		if(defaultValueEmptySet != null) sb.append(", defVal:").append(defaultValueEmptySet);
		sb.append(")");
		return new String(sb);

	}

}

/**
 * Implements a constraint X = max(Y_i | i \in S).
 * I only modified the maxOfAList constraint
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public class MaxOfASet extends AbstractBoundOfASet {

	/**
	 * Index of the maximum variable.
	 */
	protected final IStateInt indexOfMaximumVariable;


	public MaxOfASet(IEnvironment environment, IntDomainVar[] intvars, SetVar setvar, Integer defaultValueEmptySet) {
		super(intvars, setvar, defaultValueEmptySet);
		indexOfMaximumVariable = environment.makeInt(-1);
	}



	@Override
	protected boolean removeFromEnv(int idx) throws ContradictionException {
		return removeGreaterFromEnv(idx, ivars[BOUND_INDEX].getSup());
	}

	@Override
	protected boolean updateEnveloppe() throws ContradictionException {
		final int maxValue = ivars[BOUND_INDEX].getSup();
		final DisposableIntIterator iter= getSetDomain().getOpenDomainIterator();
		boolean update = false;
		while(iter.hasNext()) {
			update |= removeGreaterFromEnv(iter.next(), maxValue);
		}
		iter.dispose();
		return update;
	}



	protected void updateIndexOfMaximumVariables() {
		int maxMax = Integer.MIN_VALUE, maxMaxIdx = -1;
		int maxMax2 = Integer.MIN_VALUE;
		final DisposableIntIterator iter= this.getSetDomain().getEnveloppeIterator();
		while(iter.hasNext()) {
			final int idx = iter.next() + VARS_OFFSET;
			final int val = ivars[idx].getSup();
			if (val >= maxMax) {
				maxMax2 = maxMax;
				maxMax = val;
				maxMaxIdx = idx;
			} else if (val > maxMax2) {
				maxMax2 = val;
			}
		}
		iter.dispose();
		if (maxMax2 < ivars[BOUND_INDEX].getInf()) {
			this.indexOfMaximumVariable.set(maxMaxIdx);

		}

	}

	/**
	 * If only one candidate to be the max of the list, some additionnal
	 * propagation can be performed (as in usual x == y constraint).
	 */
	protected boolean onlyOneMaxCandidatePropagation() throws ContradictionException {
		boolean update=false;
		if(isNotEmptySet()) {
			//if the set could be empty : we do nothing
			if (this.indexOfMaximumVariable.get() == -1) {
				updateIndexOfMaximumVariables();
			}
			final int idx = this.indexOfMaximumVariable.get();
			if (idx != -1) {
				update = svars[SET_INDEX].addToKernel(idx-1, this, false);
				updateBoundInf(ivars[idx].getInf());
				ivars[idx].updateInf(ivars[BOUND_INDEX].getInf(), this, false);
			}
		}
		return update;

	}

	protected final int maxInf() {
		DisposableIntIterator iter= getSetDomain().getKernelIterator();
		int max = Integer.MIN_VALUE;
		while(iter.hasNext()) {
			int val = ivars[VARS_OFFSET+iter.next()].getInf();
			if(val>max) {max=val;}
		}
		iter.dispose();
		return max;
	}


	protected final int maxSup() {
		if( isNotEmptySet()) {
			int max = Integer.MIN_VALUE;
			//if the set could be empty : we do nothing
			DisposableIntIterator iter= getSetDomain().getEnveloppeIterator();
			while(iter.hasNext()) {
				int val = ivars[VARS_OFFSET+iter.next()].getSup();
				if(val>max) {max=val;}
			}
			iter.dispose();
			return max;
		}else {
			return Integer.MAX_VALUE;
		}
	}

	protected final void updateKernelSup() throws ContradictionException {
		final int maxValue = ivars[BOUND_INDEX].getSup();
		DisposableIntIterator iter= svars[SET_INDEX].getDomain().getKernelIterator();
		while(iter.hasNext()) {
			final int i = VARS_OFFSET+iter.next();
			ivars[i].updateSup(maxValue, this, false);
		}
		iter.dispose();
	}

	/**
	 * Propagation of the constraint.
	 *
	 * @throws choco.kernel.solver.ContradictionException if a domain becomes empty.
	 */
	@Override
	public void filter() throws ContradictionException {
		boolean noFixPoint = true;
		while(noFixPoint) {
			noFixPoint =false;
			updateBoundInf(maxInf());
			updateBoundSup(maxSup());
			updateKernelSup();
			noFixPoint |= updateEnveloppe();
			noFixPoint |= onlyOneMaxCandidatePropagation();
		} 
	}

	/**
	 * Propagation when lower bound is increased.
	 *
	 * @param idx the index of the modified variable.
	 * @throws ContradictionException if a domain becomes empty.
	 */
	@Override
	public void awakeOnInf(final int idx) throws ContradictionException {
		if (idx >= 2*VARS_OFFSET) { // Variable in the list
			final int i = idx-2*VARS_OFFSET;
			if(isInEnveloppe(i)) {
				if(isSetInstantiated()) {
					//maxOfaList case
					updateBoundInf(maxInf());
				}else {
					if( ( isInKernel(i) && updateBoundInf(maxInf()) ) || removeFromEnv(i) ) {
						this.constAwake(false);
					}
				}
			}
		} else { // Maximum variable
			if(isSetInstantiated()) {
				//maxOfaList case
				onlyOneMaxCandidatePropagation();
			}else if(updateEnveloppe() ||  onlyOneMaxCandidatePropagation()) {
				this.constAwake(false);
			}
		}
	}

	/**
	 * Propagation when upper bound is decreased.
	 *
	 * @param idx the index of the modified variable.
	 * @throws choco.kernel.solver.ContradictionException if a domain becomes empty.
	 */
	@Override
	public void awakeOnSup(final int idx) throws ContradictionException {
		if (idx >= 2*VARS_OFFSET) { // Variable in the list
			final int i = idx-2*VARS_OFFSET;
			if(isInEnveloppe(i)) {
				if(isSetInstantiated()) {
					//maxOfaList case
					updateBoundSup(maxSup());
					onlyOneMaxCandidatePropagation();
				}else {
					if(removeFromEnv(i) || updateBoundSup(maxSup())) {
						this.constAwake(false);
					}
				}
			}
		} else { // Maximum variable
			updateKernelSup();
			if(updateEnveloppe()) {
				//if the enveloppe changed, we need to propagate.
				this.constAwake(false);
			}
		}
	}




	@Override
	protected void awakeOnInstL(int i) throws ContradictionException {
		boolean propagate = updateBoundSup(maxSup());
		if(isInKernel(i)) {	propagate |= updateBoundInf(maxInf());}
		if(propagate && !isSetInstantiated()) {
			this.constAwake(false);
		}		
	}



	@Override
	protected void awakeOnInstV() throws ContradictionException {
		updateKernelSup();
		boolean propagate = onlyOneMaxCandidatePropagation();
		if(!isSetInstantiated()) {
			propagate |= updateEnveloppe();
			if(propagate) {this.constAwake(false);}
		}		
	}



	//	/**
	//	 * Propagation when a variable is instantiated.
	//	 *
	//	 * @param idx the index of the modified variable.
	//	 * @throws choco.kernel.solver.ContradictionException if a domain becomes empty.
	//	 */
	//	@Override
	//	public void awakeOnInst(final int idx) throws ContradictionException {
	//		//CPSolver.flushLogs();
	//		if (idx >= 2*VARS_OFFSET) { //of the list
	//			final int i = idx-2*VARS_OFFSET;
	//			if(isInEnveloppe(i)) { //of the set
	//				boolean propagate = updateBoundSup(maxSup());
	//				if(isInKernel(i)) {	propagate |= updateBoundInf(maxInf());}
	//				if(propagate && !isSetInstantiated()) {
	//					this.constAwake(false);
	//				}
	//			}
	//
	//		} else if (idx == VARS_OFFSET) { // Maximum variable
	//			updateKernelSup();
	//			boolean propagate = onlyOneMaxCandidatePropagation();
	//			if(!isSetInstantiated()) {
	//				propagate |= updateEnveloppe();
	//				if(propagate) {this.constAwake(false);}
	//			}
	//		}else { //set is instantiated, propagate
	//			this.propagate();
	//		}
	//	}



	@Override
	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if( updateBoundSup(maxSup()) || onlyOneMaxCandidatePropagation() ) {
			//if the max has changed or the maximum variable was found : propagate
			this.constAwake(false);
		}

	}

	@Override
	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if( updateBoundInf(maxInf()) ) {
			if(updateEnveloppe() || onlyOneMaxCandidatePropagation()) {
				//set has changed again
				this.constAwake(false);
			}
		}
	}

	

	@Override
	protected int isSatisfiedValue(DisposableIntIterator iter) {
		int v = Integer.MIN_VALUE;
		do {
			v = Math.max(v, ivars[VARS_OFFSET +iter.next()].getVal());
		}while(iter.hasNext());
		return v;
	}


	@Override
	public String pretty() {
		return pretty("max");
	}


}
