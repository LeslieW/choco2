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
package choco.cp.solver.variables.integer;

import static choco.cp.solver.variables.integer.IntVarEvent.*;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.TwoStatesPartiallyStoredIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * Implements search valued domain variables.
 */
public class IntDomainVarImpl extends AbstractVar implements IntDomainVar {

	/**
	 * The backtrackable domain of the variable.
	 */

	protected AbstractIntDomain domain;

	protected PartiallyStoredIntVector[] events;

	protected IStateInt priority;


	/**
	 * Default constructor
	 */
	protected IntDomainVarImpl(Solver solver, String name) {
		super(solver, name);
		buildDataStructures(solver.getEnvironment());
	}

	/**
	 * Constructs a new variable for the specified model and with the
	 * specified name and bounds.
	 *
	 * @param solver         The model of the variable.
	 * @param name       Its name.
	 * @param domainType the type of encoding for the domain (BOUNDS, BITSET, ...)
	 * @param a          Its minimal value.
	 * @param b          Its maximal value.
	 */

	public IntDomainVarImpl(Solver solver, String name, int domainType, int a, int b) {
		super(solver, name);
		if (domainType == IntDomainVar.BITSET) {
			domain = new BitSetIntDomain(this, a, b);
		} else if (domainType == IntDomainVar.LINKEDLIST) {
			domain = new LinkedIntDomain(this, a, b);
		} else if (domainType == IntDomainVar.BINARYTREE) {
			domain = new IntervalBTreeDomain(this, a, b);
		} else if (domainType == IntDomainVar.BIPARTITELIST) {
			domain = new BipartiteIntDomain(this, a, b);
		} else if (domainType == IntDomainVar.BOOLEAN) {
			domain = new BooleanDomain(this);
		} else {
			domain = new IntervalIntDomain(this, a, b);
		}
		this.event = new IntVarEvent(this);
		buildDataStructures(solver.getEnvironment());
	}

	public IntDomainVarImpl(Solver solver, String name, int domainType, int[] distinctSortedValues) {
		super(solver, name);
		if (domainType == IntDomainVar.BINARYTREE) {
			domain = new IntervalBTreeDomain(this, distinctSortedValues);
		} else
			if (domainType == IntDomainVar.LINKEDLIST) {
				domain = new LinkedIntDomain(this, distinctSortedValues);
			} else if (domainType == IntDomainVar.BIPARTITELIST) {
				domain = new BipartiteIntDomain(this, distinctSortedValues);
			} else {
				domain = new BitSetIntDomain(this, distinctSortedValues);
			}
		this.event = new IntVarEvent(this);
		buildDataStructures(solver.getEnvironment());
	}


	protected void buildDataStructures(IEnvironment env){
		events = new PartiallyStoredIntVector[4];
		for(int i = 0; i < 4; i++){
			if(IntVarEvent.CHECK_ACTIVE){
                events[i] = env.makePartiallyStoredIntVector();
            }else{
                events[i] = new TwoStatesPartiallyStoredIntVector(env);
            }
		}
		priority = env.makeInt(0);
	}

	/**
	 * Adds a new constraints on the stack of constraints
	 * the addition can be dynamic (undone upon backtracking) or not.
	 *
	 * @param c               the constraint to add
	 * @param varIdx          the variable index accrding to the added constraint
	 * @param dynamicAddition states if the addition is definitic (cut) or
	 *                        subject to backtracking (standard constraint)
	 * @return the index affected to the constraint according to this variable
	 */
	@Override
	public int addConstraint(SConstraint c, int varIdx, boolean dynamicAddition) {
		int constraintIdx = super.addConstraint(c, varIdx, dynamicAddition);
		computePriority(c);
		int mask = ((Propagator)c).getFilteredEventMask(varIdx);
		if((mask & INSTINTbitvector) != 0){
			if(IntVarEvent.CHECK_ACTIVE){
                addEvent(dynamicAddition, 0, constraintIdx);
            }else{
                addEvent(dynamicAddition, 0, constraintIdx, ((AbstractSConstraint)c).isActive());
            }
		}
		if((mask & INCINFbitvector) != 0){
			if(IntVarEvent.CHECK_ACTIVE){
                addEvent(dynamicAddition, 1, constraintIdx);
            }else{
                addEvent(dynamicAddition, 1, constraintIdx, ((AbstractSConstraint)c).isActive());
            }
		}
		if((mask & DECSUPbitvector) != 0){
			if(IntVarEvent.CHECK_ACTIVE){
                addEvent(dynamicAddition, 2, constraintIdx);
            }else{
                addEvent(dynamicAddition, 2, constraintIdx, ((AbstractSConstraint)c).isActive());
            }
		}
		if((mask & REMVALbitvector) != 0){
			if(IntVarEvent.CHECK_ACTIVE){
                addEvent(dynamicAddition, 3, constraintIdx);
            }else{
                addEvent(dynamicAddition, 3, constraintIdx, ((AbstractSConstraint)c).isActive());
            }
		}
		return constraintIdx;
	}

	/**
	 * Add event to the correct partially stored int vector
	 * @param dynamicAddition
	 * @param indice
	 * @param constraintIdx
	 */
	private void addEvent(boolean dynamicAddition, int indice, int constraintIdx) {
		if (dynamicAddition) {
			events[indice].add(constraintIdx);
		} else {
			events[indice].staticAdd(constraintIdx);
		}
	}

    /**
	 * Add event to the correct partially stored int vector
	 * @param dynamicAddition
	 * @param indice
	 * @param constraintIdx
	 */
	private void addEvent(boolean dynamicAddition, int indice, int constraintIdx, boolean active) {
		if (dynamicAddition) {
			((TwoStatesPartiallyStoredIntVector)events[indice]).add(constraintIdx, active);
		} else {
			((TwoStatesPartiallyStoredIntVector)events[indice]).staticAdd(constraintIdx, active);
		}
	}

    /**
     * Update the constraint state
     *
     * @param vidx  index of the variable in the constraint
     * @param cidx  constraint idx
     * @param c     the constraint
     * @param state new state (active/passive)
     */
    public void updateConstraintState(int vidx, int cidx, SConstraint c, boolean state) {
        if(!IntVarEvent.CHECK_ACTIVE){
            int mask = ((Propagator)c).getFilteredEventMask(vidx);
            if((mask & INSTINTbitvector) != 0){
                ((TwoStatesPartiallyStoredIntVector)events[0]).set(cidx, state);
            }
            if((mask & INCINFbitvector) != 0){
                ((TwoStatesPartiallyStoredIntVector)events[1]).set(cidx, state);
            }
            if((mask & DECSUPbitvector) != 0){
                ((TwoStatesPartiallyStoredIntVector)events[2]).set(cidx, state);
            }
            if((mask & REMVALbitvector) != 0){
                ((TwoStatesPartiallyStoredIntVector)events[3]).set(cidx, state);
            }
        }
    }

    /**
	 * Compute the priotity of the variable
	 * @param c
	 */
	private void computePriority(SConstraint c) {
		priority.set(Math.max(priority.get(),((Propagator)c).getPriority()));
	}

	/**
	 * Removes (permanently) a constraint from the list of constraints
	 * connected to the variable.
	 *
	 * @param c the constraint that should be removed from the list this variable
	 *          maintains.
	 */
	@Override
	public final void eraseConstraint(SConstraint c) {
		int constraintIdx = constraints.remove(c);
		indices.remove(constraintIdx);
		int mask = ((Propagator)c).getFilteredEventMask(indices.get(constraintIdx));
		if((mask & INSTINTbitvector) != 0){
			events[0].remove(constraintIdx);
		}
		if((mask & INCINFbitvector) != 0){
			events[1].remove(constraintIdx);
		}
		if((mask & DECSUPbitvector) != 0){
			events[2].remove(constraintIdx);
		}
		if((mask & REMVALbitvector) != 0){
			events[3].remove(constraintIdx);
		}
	}

	public final PartiallyStoredIntVector[] getEventsVector(){
		return events;
	}

	public final int getPriority() {
		return priority.get();
	}

	// ============================================
	// Methods of the interface
	// ============================================

	/**
	 * Checks if the variable is instantiated to a specific value.
	 */

	public boolean isInstantiatedTo(int x) {
		return isInstantiated() && (getVal() == x);
	}


	/**
	 * Checks if the variables is instantiated to any value.
	 */

	public boolean isInstantiated() {
		return domain.getSize() == 1;//(value.isKnown());
	}


	/**
	 * Checks if a value is still in the domain.
	 */

	public boolean canBeInstantiatedTo(int x) {
		//return domain.contains(x);
		return (getInf() <= x && x <= getSup() && (domain == null || domain.contains(x)));
	}

	/**
	 * Checks if a value is still in the domain assuming the value is
	 * in the initial bound of the domain
	 */
	public boolean fastCanBeInstantiatedTo(int x) {
		return domain.contains(x);
	}

	/**
	 * Sets the minimum value.
	 */

	public final void setInf(int x) throws ContradictionException {
		updateInf(x, VarEvent.NOCAUSE);
	}

	/**
	 * @deprecated replaced by setInf
	 */
	public final void setMin(int x) throws ContradictionException {
		updateInf(x, VarEvent.NOCAUSE);
	}

	/**
	 * Sets the maximal value.
	 */

	public final void setSup(int x) throws ContradictionException {
		updateSup(x, VarEvent.NOCAUSE);
	}

	/**
	 * @deprecated replaced by setSup
	 */
	public final void setMax(int x) throws ContradictionException {
		updateSup(x, VarEvent.NOCAUSE);
	}

	/**
	 * Instantiates the variable.
	 *
	 * @throws choco.kernel.solver.ContradictionException
	 */

	public final void setVal(int x) throws ContradictionException {
		instantiate(x, VarEvent.NOCAUSE);
	}


	/**
	 * Removes a value.
	 *
	 * @throws choco.kernel.solver.ContradictionException
	 */

	public final void remVal(int x) throws ContradictionException {
		removeVal(x, VarEvent.NOCAUSE);
	}

	public final void wipeOut() throws ContradictionException {
		this.getSolver().getPropagationEngine().raiseContradiction(this, ContradictionException.DOMAIN);
	}

	public boolean hasEnumeratedDomain() {
		return domain.isEnumerated();
	}

	public boolean hasBooleanDomain() {
		return domain.isBoolean();
	}

	public IntDomain getDomain() {
		return domain;
	}

	/**
	 * Gets the domain size.
	 */

	public int getDomainSize() {
		return domain.getSize();
	}

	/**
	 * Checks if it can be equals to another variable.
	 */

	public boolean canBeEqualTo(IntDomainVar x) {
		if (x.getInf() <= this.getSup()) {
			if (this.getInf() <= x.getSup()) {
				if (!this.hasEnumeratedDomain() || !x.hasEnumeratedDomain())
					return true;
				else {
					for (IntIterator it = this.getDomain().getIterator(); it.hasNext();) {
						int v = it.next();
						if (x.canBeInstantiatedTo(v))
							return true;
					}
					return false;
				}
			} else
				return false;
		} else
			return false;
	}


	/**
	 * Checks if the variables can be instantiated to at least one value
	 * in the array.
	 *
	 * @param sortedValList The value array.
	 * @param nVals         The number of interesting value in this array.
	 */

	public boolean canBeInstantiatedIn(int[] sortedValList, int nVals) {
		if (getInf() <= sortedValList[nVals - 1]) {
			if (getSup() >= sortedValList[0]) {
				if (domain == null)
					return true;
				else {
					for (int i = 0; i < nVals; i++) {
						if (canBeInstantiatedTo(sortedValList[i]))
							return true;
					}
					return false;
				}
			} else
				return false;
		} else
			return false;
	}


	/**
	 * Returns a randomly choosed value in the domain.
	 * <p/>
	 * Not implemented yet.
	 */

	public int getRandomDomainValue() {
		if (domain == null)
			return getInf();
		// TODO     return inf.get() + random(sup.get() - inf.get() + 1);
		else
			return domain.getRandomValue();
	}


	/**
	 * Gets the next value in the domain.
	 */

	public int getNextDomainValue(int currentv) {
		if (currentv < getInf())
			return getInf();
		else if (domain == null)
			return currentv + 1;
		else
			return domain.getNextValue(currentv);
	}


	/**
	 * Gets the previous value in the domain.
	 */

	public int getPrevDomainValue(int currentv) {
		if (currentv > getSup())
			return getSup();
		else if (domain == null)
			return currentv - 1;
		else
			return domain.getPrevValue(currentv);
	}


	/**
	 * Internal var: update on the variable lower bound caused by its i-th
	 * constraint.
	 * Returns a boolean indicating whether the call indeed added new information
	 *
	 * @param x   The new lower bound.
	 * @param idx The index of the constraint (among all constraints linked to
	 *            the variable) responsible for the update.
	 */

	public boolean updateInf(int x, int idx) throws ContradictionException {
		return domain.updateInf(x, idx);
	}

	/**
	 * Internal var: update on the variable upper bound caused by its i-th
	 * constraint.
	 * Returns a boolean indicating whether the call indeed added new information.
	 *
	 * @param x   The new upper bound
	 * @param idx The index of the constraint (among all constraints linked to
	 *            the variable) responsible for the update
	 */

	public boolean updateSup(int x, int idx) throws ContradictionException {
		return domain.updateSup(x, idx);
	}


	/**
	 * Internal var: update (value removal) on the domain of a variable caused by
	 * its i-th constraint.
	 * <i>Note:</i> Whenever the hole results in a stronger var (such as a bound update or
	 * an instantiation, then we forget about the index of the var generating constraint.
	 * Indeed the propagated var is stronger than the initial one that
	 * was generated; thus the generating constraint should be informed
	 * about such a new var.
	 * Returns a boolean indicating whether the call indeed added new information.
	 *
	 * @param x   The removed value
	 * @param idx The index of the constraint (among all constraints linked to the variable) responsible for the update
	 */

	public boolean removeVal(int x, int idx) throws ContradictionException {
		return domain.removeVal(x, idx);
	}

	/**
	 * Internal var: remove an interval (a sequence of consecutive values) from
	 * the domain of a variable caused by its i-th constraint.
	 * Returns a boolean indicating whether the call indeed added new information.
	 *
	 * @param a   the first removed value
	 * @param b   the last removed value
	 * @param idx the index of the constraint (among all constraints linked to the variable)
	 *            responsible for the update
	 */

	public boolean removeInterval(int a, int b, int idx) throws ContradictionException {
		return domain.removeInterval(a, b, idx);
	}

	/**
	 * Internal var: instantiation of the variable caused by its i-th constraint
	 * Returns a boolean indicating whether the call indeed added new information.
	 *
	 * @param x   the new upper bound
	 * @param idx the index of the constraint (among all constraints linked to the
	 *            variable) responsible for the update
	 */

	public boolean instantiate(int x, int idx) throws ContradictionException {
		return domain.instantiate(x, idx);
	}


	@Override
	public final void fail() throws ContradictionException {
		solver.getPropagationEngine().raiseContradiction(this, ContradictionException.DOMAIN);
	}


	/**
	 * Gets the minimal value of the variable.
	 */

	public int getInf() {
		return domain.getInf();
	}


	/**
	 * Gets the maximal value of the variable.
	 */

	public int getSup() {
		return domain.getSup();
	}


	/**
	 * Gets the value of the variable if instantiated.
	 */

	public int getVal() {
		return domain.getInf();//value.get();
	}

	/**
	 * @deprecated replaced by getVal
	 */
	public int getValue() {
		return domain.getInf();//value.get();
	}

	/**
	 * pretty printing
	 *
	 * @return a String representation of the variable
	 */
	@Override
	public String toString() {
		return (super.toString() + ":" + (isInstantiated() ? getVal() : "?"));
	}

	/**
	 * pretty printing
	 *
	 * @return a String representation of the variable
	 */
	public String pretty() {
		return (this.toString() + "[" + this.domain.getSize() + "]" + this.domain.pretty());
	}

	public final Solver getSolver() {
		return this.solver;
	}

	public final void setSolver(Solver solver) {
		this.solver = solver;
	}


}
