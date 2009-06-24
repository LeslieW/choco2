/**
 * 
 */
package choco.kernel.solver.variables.scheduling;

import choco.kernel.common.IIndex;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.model.variables.scheduling.ITaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Iterator;

/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public final class TaskVar extends AbstractTask implements Var, ITaskVariable<IntDomainVar>, IIndex {


	/**
	 * The (optimization or decision) model to which the entity belongs.
	 */
	public Solver solver;

	protected final IntDomainVar start;

	protected final IntDomainVar end;

	protected final IntDomainVar duration;

    private long index;
	/**
	 * The list of constraints (listeners) observing the variable.
	 */
	protected PartiallyStoredVector<SConstraint> constraints;

	
	/**
	 * Initializes a new variable.
	 * @param solver The model this variable belongs to
	 * @param name The name of the variable
	 */
	public TaskVar(final Solver solver, final int id, final String name, final IntDomainVar start, final IntDomainVar end, final IntDomainVar duration) {
		super(id, name);
		this.solver =solver;
		this.start = start;
		this.end = end;
		this.duration = duration;
		IEnvironment env = solver.getEnvironment();
		constraints = env.<SConstraint>makePartiallyStoredVector();
        index = solver.getIndexfactory().getIndex();
	}

	/**
	 * Useful for debugging.
	 * @return the name of the variable
	 */
	@Override
	public String toString() {
		return name;
	}

    /**
     * Unique index of an object in the master object
     * (Different from hashCode, can change from one execution to another one)
     *
     * @return
     */
    @Override
    public long getIndex() {
        return index;
    }

    //*****************************************************************//
	//*******************  TaskVariable  ********************************//
	//***************************************************************//
	
	public final IntDomainVar start() {
		return start;
	}

	public final IntDomainVar end() {
		return end;
	}

	public final IntDomainVar duration() {
		return duration;
	}
	
	//*****************************************************************//
	//*******************  ITask  ********************************//
	//***************************************************************//
	


	
	public int getECT() {
		return end.getInf();
	}

	public int getEST() {
		return start.getInf();
	}

	public int getLCT() {
		return end.getSup();
	}

	public int getLST() {
		return start.getSup();
	}

	public int getMaxDuration() {
		return duration.getSup();
	}

	public int getMinDuration() {
		return duration.getInf();
	}

	public boolean hasConstantDuration() {
		return duration.isInstantiated();
	}

	@Override
	public boolean isScheduled() {
		return isInstantiated();
	}
	
	
	//*****************************************************************//
	//*******************  Var  ********************************//
	//***************************************************************//
	/**
	 * Returns the variable event.
	 * @return the event responsible for propagating variable modifications
	 */
	public VarEvent<? extends AbstractVar> getEvent() {
		return null;
	}


	/**
	 * Retrieve the constraint i involving the variable.
	 * Be careful to use the correct constraint index (constraints are not
	 * numbered from 0 to number of constraints minus one, since an offset
	 * is used for some of the constraints).
	 * @param i the number of the required constraint
	 * @return the constraint number i according to the variable
	 */
	public SConstraint getConstraint(final int i) {
		return constraints.get(i);
	}


	/**
	 * Returns the number of constraints involving the variable.
	 * @return the number of constraints containing this variable
	 */
	public int getNbConstraints() {
		return constraints.size();
	}

	/**
	 * Access the data structure storing constraints involving a given variable.
	 * @return the backtrackable structure containing the constraints
	 */
	public PartiallyStoredVector<SConstraint> getConstraintVector() {
		return constraints;
	}

	/**
	 * Access the data structure storing indices associated to constraints 
	 * involving a given variable.
	 * @return the indices associated to this variable in each constraint
	 */
	public PartiallyStoredIntVector getIndexVector() {
		return null;
	}

	/**
	 * Returns the index of the variable in its constraint i.
	 * @param constraintIndex the index of the constraint 
	 * (among all constraints linked to the variable)
	 * @return the index of the variable
	 */
	public int getVarIndex(final int constraintIndex) {
		return -1;
	}

	/**
	 * Removes (permanently) a constraint from the list of constraints 
	 * connected to the variable.
	 * @param c the constraint that should be removed from the list this variable
	 * maintains.
	 */
	public void eraseConstraint(final SConstraint c) {
		constraints.remove(c);
	}

	// ============================================
	// Managing Listeners.
	// ============================================

	 /**
	   * Adds a new constraints on the stack of constraints
	   * the addition can be dynamic (undone upon backtracking) or not.
	   * @param c the constraint to add
	   * @param varIdx the variable index accrding to the added constraint
	   * @param dynamicAddition states if the addition is definitic (cut) or
	   * subject to backtracking (standard constraint)
	   * @return the index affected to the constraint according to this variable
	   */
	  public int addConstraint(final SConstraint c, final int varIdx,
	                           final boolean dynamicAddition) {
	    int constraintIdx;
	    if (dynamicAddition) {
	      constraintIdx = constraints.add(c);
	    } else {
	      constraintIdx = constraints.staticAdd(c);
	    }
	    return constraintIdx;
	  }

	/**
	 * This methods should be used if one want to access the different constraints
	 * currently posted on this variable.
	 *
	 * Indeed, since indices are not always
	 * consecutive, it is the only simple way to achieve this.
	 *
	 * Warning ! this iterator should not be used to remove elements.
	 * The <code>remove</code> method throws an
	 * <code>UnsupportedOperationException</code>.
	 *
	 * @return an iterator over all constraints involving this variable
	 */
	public Iterator<SConstraint> getConstraintsIterator() {
		return new Iterator<SConstraint>() {
			IntIterator indices = constraints.getIndexIterator();

			public boolean hasNext() {
				return indices.hasNext();
			}

			public SConstraint next() {
				return constraints.get(indices.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

	}
	
	@Override
	public void setSolver(Solver solver) {
		this.solver = solver;
	}

	@Override
	public Solver getSolver() {
		return solver;
	}

	@Override
	public boolean isInstantiated() {
		return  start.isInstantiated() && end.isInstantiated() && duration.isInstantiated();
	}

	@Override
	public void fail() throws ContradictionException {
		solver.getPropagationEngine().raiseContradiction(this, ContradictionException.VARIABLE);		
	}

    /**
     * Update the constraint state
     *
     * @param vidx  index of the variable in the constraint
     * @param cidx  constraint idx
     * @param c     the constraint
     * @param state new state (active/passive)
     */
    @Override
    public void updateConstraintState(int vidx, int cidx, SConstraint c, boolean state) {
    }
}
