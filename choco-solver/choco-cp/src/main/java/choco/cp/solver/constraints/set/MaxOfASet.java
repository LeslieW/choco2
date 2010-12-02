/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.cp.solver.constraints.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

/**
 * Implements a constraint X = max(Y_i | i \in S).
 * I only modified the maxOfAList constraint
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public final class MaxOfASet extends AbstractBoundOfASet {

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
