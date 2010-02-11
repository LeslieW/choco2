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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.set.SetVar;

/**
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public class MinOfASet extends AbstractBoundOfASet {


	/**
	 * Index of the minimum variable.
	 */
	protected final IStateInt indexOfMinimumVariable;


	public MinOfASet(IEnvironment environment, IntVar[] intvars, SetVar setvar) {
		super(intvars, setvar);
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
			removeLowerFromEnv(iter.next(), maxValue);
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
				update = svars[SET_INDEX].addToKernel(idx-1, getConstraintIdx(SET_INDEX));
				updateBoundSup(ivars[idx].getSup());
				ivars[idx].updateSup(ivars[BOUND_INDEX].getSup(),int_cIndices[idx]);}
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
			ivars[i].updateInf(minValue, int_cIndices[i]);
		}
        iter.dispose();
	}

	/**
	 * Propagation of the constraint.
	 *
	 * @throws choco.kernel.solver.ContradictionException if a domain becomes empty.
	 */
	@Override
	public void propagate() throws ContradictionException {
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

	/**
	 * Propagation when a variable is instantiated.
	 *
	 * @param idx the index of the modified variable.
	 * @throws choco.kernel.solver.ContradictionException if a domain becomes empty.
	 */
	@Override
	public void awakeOnInst(final int idx) throws ContradictionException {
		if (idx >= 2*VARS_OFFSET) { //of the list
			final int i = idx-2*VARS_OFFSET;
			if(isInEnveloppe(i)) { //of the set
				boolean propagate = updateBoundSup(minSup());
				if(isInKernel(i)) {	propagate |= updateBoundInf(minInf());}
				if(propagate && !isSetInstantiated()) {
					this.constAwake(false);
				}
			}

		} else if (idx == VARS_OFFSET) { // Maximum variable
			updateKernelInf();
			boolean propagate = onlyOneMinCandidatePropagation();
			if(!isSetInstantiated()) {
				propagate |= updateEnveloppe();
				if(propagate) {this.constAwake(false);}
			}
		}else { //set is instantiated, propagate
			this.propagate();
		}
	}

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
	public boolean isSatisfied() {
		DisposableIntIterator iter = svars[SET_INDEX].getDomain().getKernelIterator();
		if( iter.hasNext()) {
			int v = Integer.MAX_VALUE;
			while(iter.hasNext()) {
				v = Math.min(v, ivars[VARS_OFFSET +iter.next()].getVal());
			}
			return v == ivars[BOUND_INDEX].getVal(); 
		}
		return true;
	}

	@Override
	public String pretty() {
		return pretty(MIN);
	}


}
