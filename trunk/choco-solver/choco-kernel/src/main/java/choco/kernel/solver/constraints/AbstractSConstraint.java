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
package choco.kernel.solver.constraints;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBool;
import choco.kernel.solver.ContradictionException;
import static choco.kernel.solver.ContradictionException.Type.CONSTRAINT;
import choco.kernel.solver.Solver;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.queue.EventQueue;
import choco.kernel.solver.variables.Var;

import java.util.HashMap;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * An abstract class for all implementations of listeners
 */
public abstract class AbstractSConstraint implements Propagator {

    protected PropagationEngine propagationEngine;

	/**
	 * The priority of the constraint.
	 */

	protected int priority;


	/**
	 * The constraint <i>awake</i> var attached to the constraint.
	 */

	protected ConstraintEvent constAwakeEvent;


	/**
	 * a field for storing whether the constraint is active or not
	 */
	protected IStateBool active;


	/**
	 * Return the type of constraint.
	 * Can be INTEGER, SET, REAL, MIXED
	 */
	protected SConstraintType constraintType;


	/**
	 * The number of extensions registered to this class
	 */
	private static int ABSTRACTSCONSTRAINT_EXTENSIONS_NB = 0;

	/**
	 * The set of registered extensions (in order to deliver one and only one index for each extension !)
	 */
	private static final HashMap<String, Integer> REGISTERED_ABSTRACTSCONSTRAINT_EXTENSIONS = new HashMap<String, Integer>();

	/**
	 * Returns a new number of extension registration
	 * @param name A name for the extension (should be an UID, like the anbsolute path for instance)
	 * @return a number that can be used for specifying an extension (setExtension method)
	 */
	public static int getAbstractSConstraintExtensionNumber(String name) {
		Integer index = REGISTERED_ABSTRACTSCONSTRAINT_EXTENSIONS.get(name);
		if (index == null) {
			index = ABSTRACTSCONSTRAINT_EXTENSIONS_NB++;
			REGISTERED_ABSTRACTSCONSTRAINT_EXTENSIONS.put(name, index);
		}
		return index;
	}

	/**
	 * The extensions of this constraint, in order to add some data linked to this constraint (for specific algorithms)
	 */
	public Object[] extensions = new Object[4];

	/**
	 * Constraucts a constraint with the priority 0.
	 */

	public AbstractSConstraint() {
		this(0);
	}

	/**
	 * Constructs a constraint with the specified priority.
	 *
	 * @param priority The wished priority.
	 */

	public AbstractSConstraint(int priority) {
		this.priority = priority;
		this.constAwakeEvent = new ConstraintEvent(this, false, priority);
	}


	/**
	 * Adds a new extension.
	 * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
	 * @param extension the extension to store to add some algorithm specific data
	 */
	public void setExtension(int extensionNumber, Object extension) {
		if (extensionNumber >= extensions.length) {
			Object[] newArray = new Object[extensions.length * 2];
			System.arraycopy(extensions, 0, newArray, 0, extensions.length);
			extensions = newArray;
		}
		extensions[extensionNumber] = extension;
	}

	/**
	 * Returns the queried extension
	 * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
	 * @return the queried extension
	 */
	public Object getExtension(int extensionNumber) {
		return extensions[extensionNumber];
	}

	/**
	 * Returns the constraint awake var attached to the constraint.
	 * @return the constraint awake var attached to the constraint
	 */

	public PropagationEvent getEvent() {
		return constAwakeEvent;
	}


	/**
	 * Initial propagation of the constraint.
	 * @param isInitialPropagation indicates if it is the initial propagation
	 */

	public void constAwake(boolean isInitialPropagation) {
		propagationEngine.postConstAwake(this, isInitialPropagation);
	}


	/**
	 * Returns the priority.
	 * @return the priority
	 */

	public int getPriority() {
		return priority;
	}


	/**
	 * Default initial propagation: full constraint re-propagation.
	 */

	public void awake() throws ContradictionException {
		propagate();
	}


	/**
	 * Un-freezing a constraint (this is useful for mimicking dynamic
	 * constraint posts...).
	 */

	public void setActive() {
		if (!(isActive())) {
			setActiveSilently();
			constAwake(true);
		}
	}

	public void setActiveSilently() {
		active.set(true);
	}


	/**
	 * Freezing a constraint (this is useful for backtracking when mimicking
	 * dynamic constraint posts...).
	 */

	public void setPassive() {
		if (active != null) {
			active.set(false);
			ConstraintEvent evt = constAwakeEvent;
			EventQueue q = propagationEngine.getQueue(evt);
			q.remove(evt);
		}
	}

	/**
	 * records that a constraint is now entailed (therefore it is now useless to propagate it again)
	 */
	public void setEntailed() {
		setPassive();
	}

	/**
	 * raise a contradiction during propagation when the constraint can definitely not be satisfied given the current domains
	 * @throws ContradictionException contradiction exception
	 */
	public void fail() throws ContradictionException {
		propagationEngine.raiseContradiction(this, CONSTRAINT);
	}

	/**
	 * Indicates if the constraint is entailed, from now on will be always satisfied
	 * @return wether the constraint is entailed
	 */
	public Boolean isEntailed() {
		if (isCompletelyInstantiated()) {
			return isSatisfied();
		} else {
			return null;
		}

	}

	/**
	 * Checks if the constraint is active (e.g. plays a role in the propagation phase).
	 *
	 * @return true if the constraint is indeed currently active
	 */

	public boolean isActive() {
		//    if (active != null)
		return active.get();
		//    return false;
	}

	/**
	 * This function connects a constraint with its variables in several ways.
	 * Note that it may only be called once the constraint
	 * has been fully created and is being posted to a model.
	 * Note that it should be called only once per constraint.
	 * This can be a dynamic addition (undone upon backtracking) or not
	 * @param dynamicAddition if the addition should be dynamical
	 */

	public void addListener(boolean dynamicAddition) {
		int n = getNbVars();
		for (int i = 0; i < n; i++) {
			setConstraintIndex(i, getVar(i).addConstraint(this, i, dynamicAddition));
			getVar(i).getEvent().addPropagatedEvents(getFilteredEventMask(i));
			//getVar(i).getEvent().addPropagatedEvents(0x0FFFF);
		}
	}

	public int getFilteredEventMask(int idx) {
		return 0x0FFFF;
	}

	/**
	 * returns the same numbering in a constraint and its counterpart
	 * @param i the idx of a variable
	 * @return the same numbering in a constraint and its counterpart
	 */
	// defaut implementation: returns the same numbering in a constraint and its counterpart.
	public int getVarIdxInOpposite(int i) {
		return i;
	}

	/**
	 * Get the opposite constraint
	 * @return the opposite constraint  @param solver
	 */
	public AbstractSConstraint opposite(Solver solver) {
		throw new UnsupportedOperationException();
	}

	/**
	 *
	 * @param v variable
	 * @param j indice
	 * @param dynamicAddition
	 * @return
	 */
	public int connectVar(Var v, int j, boolean dynamicAddition) {
		int cidx = v.addConstraint(this, j, dynamicAddition);
		setConstraintIndex(j, cidx);
		return cidx;
	}


	/**
	 * Clone the constraint
	 * @return the clone of the constraint
	 * @throws CloneNotSupportedException Clone not supported exception
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * substitues all occurrences of a variable in a constraint by another variable
	 *
	 * @param oldvar the variable to be removed
	 * @param newvar the variable to be introduced in place of the other
	 * @return the number of occurrences that have been substituted
	 */
	public int substituteVar(Var oldvar, Var newvar) {
		int nbSub = 0;
		int nbVars = this.getNbVars();
		for (int i = 0; i < nbVars; i++) {
			if (this.getVar(i) == oldvar) {
				this.setVar(i, newvar);
				nbSub++;
			}
		}
		return nbSub;
	}


	/**
	 * CPRU 07/12/2007: DomOverWDeg implementation
	 * This method returns the number of variables not already instanciated
	 *
	 * @return the number of failure
	 */
	public int getNbVarNotInst() {
		int notInst = 0;
		final int nbVars = this.getNbVars();
		for (int i = 0; i < nbVars; i++) {
			if (!this.getVar(i).isInstantiated()) {
				notInst++;
			}
		}
		return notInst;
	}

    /**
     * Activate a constraint.
     * @param environment current environment
     */
    @Override
    public void activate(IEnvironment environment) {
        this.active = environment.makeBool(false);
    }

    /**
     * Define the propagation engine within the constraint.
     * Mandatory to throw {@link ContradictionException}.
     * @param propEng the current propagation engine
     */
    @Override
    public void setPropagationEngine(PropagationEngine propEng) {
        this.propagationEngine = propEng;
    }

    public abstract SConstraintType getConstraintType();


	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append(getClass().getSimpleName()).append("{");
		final int n = getNbVars();
		for (int i = 0; i < n - 1; i++) {
			b.append(getVar(i).getName()).append(", ");
		}
		b.append(getVar(n-1).getName()).append("}");
		return b.toString();
	}

}
