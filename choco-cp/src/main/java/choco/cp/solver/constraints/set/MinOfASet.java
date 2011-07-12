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
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public final class MinOfASet extends AbstractBoundOfASet {


	/**
	 * Index of the minimum variable.
	 */
	protected final IStateInt indexOfMinimumVariable;


	public MinOfASet(IEnvironment environment, IntDomainVar[] intvars, SetVar setvar, Integer defaultValueEmptySet) {
		super(intvars, setvar, defaultValueEmptySet);
		indexOfMinimumVariable = environment.makeInt(-1);
	}
	
	@Override
	protected boolean removeFromEnv(int idx) throws ContradictionException {
		return removeLowerFromEnv(idx, ivars[BOUND_INDEX].getInf());
	}



	@Override
	protected boolean updateEnveloppe() throws ContradictionException {
		final int maxValue = ivars[BOUND_INDEX].getInf();
		final DisposableIntIterator iter= getSetDomain().getOpenDomainIterator();
		boolean update = false;
		while(iter.hasNext()) {
			update |= removeLowerFromEnv(iter.next(), maxValue);
		}
        iter.dispose();
		return update;
	}

	protected void updateIndexOfMinimumVariables() throws ContradictionException {
		int minMin = Integer.MAX_VALUE, minMinIdx = -1;
		int minMin2 = Integer.MAX_VALUE;
		DisposableIntIterator iter= this.getSetDomain().getEnveloppeIterator();
		while(iter.hasNext()) {
			final int idx = iter.next() + VARS_OFFSET;
			final int val = ivars[idx].getInf();
			if (val <= minMin) {
				minMin2 = minMin;
				minMin = val;
				minMinIdx = idx;
			} else if (val < minMin2) {
				minMin2 = val;
			}
		}
        iter.dispose();
		if (minMin2 > ivars[BOUND_INDEX].getSup()) {
			this.indexOfMinimumVariable.set(minMinIdx);
		}
	}


	/**
	 * If only one candidate to be the max of the list, some additionnal
	 * propagation can be performed (as in usual x == y constraint).
	 */
	protected boolean onlyOneMinCandidatePropagation() throws ContradictionException {
		boolean update=false;
		if(isNotEmptySet()) {
			//if the set could be empty : we do nothing
			if (this.indexOfMinimumVariable.get() == -1) {
				updateIndexOfMinimumVariables();
			}
			int idx = this.indexOfMinimumVariable.get();
			if (idx != -1) {
				update = svars[SET_INDEX].addToKernel(idx-1, this, false);
				updateBoundSup(ivars[idx].getSup());
				ivars[idx].updateSup(ivars[BOUND_INDEX].getSup(), this, false);}
		}
		return update;

	}

	protected final int minInf() {
		if( isNotEmptySet()) {
			DisposableIntIterator iter= getSetDomain().getEnveloppeIterator();
			int min = Integer.MAX_VALUE;
			while(iter.hasNext()) {
				int val = ivars[VARS_OFFSET+iter.next()].getInf();
				if(val<min) {min=val;}
			}
            iter.dispose();
			return min;
		}else {return Integer.MIN_VALUE;}
	}


	protected final int minSup() {
		int min = Integer.MAX_VALUE;
		//if the set could be empty : we do nothing
		DisposableIntIterator iter= getSetDomain().getKernelIterator();
		while(iter.hasNext()) {
			int val = ivars[VARS_OFFSET+iter.next()].getSup();
			if(val<min) {min=val;}
		}
        iter.dispose();
		return min;	
	}

	protected final void updateKernelInf() throws ContradictionException {
		final int minValue = ivars[BOUND_INDEX].getInf();
		DisposableIntIterator iter= svars[SET_INDEX].getDomain().getKernelIterator();
		while(iter.hasNext()) {
			final int i = VARS_OFFSET+iter.next();
			ivars[i].updateInf(minValue, this, false);
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
		//CPSolver.flushLogs();
		boolean noFixPoint = true;
		while(noFixPoint) {
			noFixPoint =false;
			updateBoundInf(minInf());
			updateBoundSup(minSup());
			updateKernelInf();
			noFixPoint |= updateEnveloppe();
			noFixPoint |= onlyOneMinCandidatePropagation();

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
					updateBoundInf(minInf());
				}else {
					if( ( isInKernel(i) && updateBoundInf(minInf()) ) || removeFromEnv(i) ) {
						this.constAwake(false);
					}
				}
			}
		} else { // Maximum variable
			updateKernelInf();
			if(updateEnveloppe()) {
				//if the enveloppe changed, we need to propagate.
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
					//minOfaList case
					updateBoundSup(minSup());
					onlyOneMinCandidatePropagation();
				}else {
					if(removeFromEnv(i) || updateBoundSup(minSup())) {
						this.constAwake(false);
					}
				}
			}
		} else { // Maximum variable
			if(isSetInstantiated()) {
				//maxOfaList case
				onlyOneMinCandidatePropagation();
			}else if(updateEnveloppe() ||  onlyOneMinCandidatePropagation()) {
				this.constAwake(false);
			}
		}
	}
	
	

	@Override
	protected void awakeOnInstL(int i) throws ContradictionException {
		boolean propagate = updateBoundSup(minSup());
		if(isInKernel(i)) {	propagate |= updateBoundInf(minInf());}
		if(propagate && !isSetInstantiated()) {
			this.constAwake(false);
		}
		
	}

	@Override
	protected void awakeOnInstV() throws ContradictionException {
		updateKernelInf();
		boolean propagate = onlyOneMinCandidatePropagation();
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
//		if (idx >= 2*VARS_OFFSET) { //of the list
//			final int i = idx-2*VARS_OFFSET;
//			if(isInEnveloppe(i)) { //of the set
//				boolean propagate = updateBoundSup(minSup());
//				if(isInKernel(i)) {	propagate |= updateBoundInf(minInf());}
//				if(propagate && !isSetInstantiated()) {
//					this.constAwake(false);
//				}
//			}
//
//		} else if (idx == VARS_OFFSET) { // Maximum variable
//			updateKernelInf();
//			boolean propagate = onlyOneMinCandidatePropagation();
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
		if( updateBoundSup(minSup()) || onlyOneMinCandidatePropagation() ) {
			//if the max has changed or the maximum variable was found : propagate
			this.constAwake(false);
		}

	}

	@Override
	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if( updateBoundInf(minInf()) ) {
			if(updateEnveloppe() || onlyOneMinCandidatePropagation()) {
				//set has changed again
				this.constAwake(false);
			}
		}
	}
	
	@Override
	protected int isSatisfiedValue(DisposableIntIterator iter) {
		int v = Integer.MAX_VALUE;
		do {
			v = Math.min(v, ivars[VARS_OFFSET +iter.next()].getVal());
		}while(iter.hasNext());
        iter.dispose();
		return v;
	}
	
	@Override
	public String pretty() {
		return pretty("min");
	}


}
