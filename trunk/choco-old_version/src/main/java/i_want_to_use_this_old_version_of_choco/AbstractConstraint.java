// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.bool.BoolConstraint;
import i_want_to_use_this_old_version_of_choco.bool.CompositeConstraint;
import i_want_to_use_this_old_version_of_choco.mem.IStateBool;
import i_want_to_use_this_old_version_of_choco.mem.trailing.StoredInt;
import i_want_to_use_this_old_version_of_choco.prop.*;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * An abstract class for all implementations of listeners
 */
public abstract class AbstractConstraint extends AbstractEntity implements Propagator {

  /**
   * The priority of the constraint.
   */

  protected int priority;


  /**
   * The constraint <i>awake</i> var attached to the constraint.
   */

  protected ConstraintEvent constAwakeEvent;


  /**
   * A field for attaching additional data util (useful for PaLM)
   */
  protected ConstraintPlugin hook;


  /**
   * a field for storing whether the constraint is active or not
   */
  protected IStateBool active;

    /**
     * * CPRU 07/12/2007: DomOverWDeg implementation
     * The number of failure of this constraint
     */
    protected int nbFailure;

    /**
     * CPRU 07/12/2007: DomOverWDeg implementation
     * The number of variable not already instanciated
     */
    protected StoredInt nbVarNotInst;

  /**
   * Constraucts a constraint with the priority 0.
   */

  public AbstractConstraint() {
    this(0);
  }


  /**
   * Constructs a constraint with the specified priority.
   *
   * @param priority The wished priority.
   */

  public AbstractConstraint(int priority) {
    this.priority = priority;
    this.constAwakeEvent = new ConstraintEvent(this, false, priority);
    //2007-12-07 FR_1873619 CPRU: DomOverDeg+DomOverWDeg
    this.nbFailure =1;
  }


  /**
   * Returns the constraint plugin. Useful for extending the solver.
   */

  public ConstraintPlugin getPlugIn() {
    return hook;
  }

  /**
   * Set the ConstraintPlugin of the constraint. The constraint plugin
   * gives access to the dependency net of each constraint, it can be useful
   * to design search heuristic based on explanation.
   *
   * @param hook the constraint plugin
   */
  public void setPlugIn(ConstraintPlugin hook) {
    this.hook = hook;
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
    getProblem().getPropagationEngine()
        .postConstAwake(this, isInitialPropagation);
  }


  /**
   * Returns the priority.
   * @return the priority
   */

  public int getPriority() {
    return priority;
  }


  /**
   * Default propagation on variable revision: full constraint re-propagation.
   * @param idx the index of the constraint to awake on var
   */

  public void awakeOnVar(int idx) throws ContradictionException {
    propagate();
  }


  /**
   * Default initial propagation: full constraint re-propagation.
   */

  public void awake() throws ContradictionException {
    propagate();
  }


  /**
   * Retrieve the problem of the constraint.
   * @return the problem linked to the constraint
   */

  // ??????????? add the case when the constraint c involves only
  // constant variables, considered as belonging to CURRENT_PB
  public AbstractProblem getProblem() {
    if (problem != null) return problem;
    int nVars = getNbVars();
    for (int i = 0; i < nVars; i++) {
      Var v;
      v = getVar(i);
      if (!(v.getProblem() == null))
        return v.getProblem();
    }
    return null;
  }


  /**
   * Returns the constraint that is in the network and involving the
   * current constraint as a subconstraint.
   * @return the root constraint
   */

  Constraint getRootConstraint() {
    Constraint rootConstraint;
    rootConstraint = getVar(0).getConstraint(getConstraintIdx(0));
    return rootConstraint;
  }


  /**
   * Un-freezing a constraint (this is useful for mimicking dynamic
   * constraint posts...).
   */

  public void setActive() {
    Constraint rootConstraint = getRootConstraint();
    if (rootConstraint == this) {
      if (!(isActive())) {
        active.set(true);
        if (hook != null) hook.activateListener();
        constAwake(true);
      }
    }
  }


  /**
   * Freezing a constraint (this is useful for backtracking when mimicking
   * dynamic constraint posts...).
   */

  public void setPassive() {
    Constraint rootConstraint = getRootConstraint();
    if (rootConstraint == this) {
      if (isActive()) {
        active.set(false);
        ConstraintEvent evt = constAwakeEvent;
        EventQueue q = ((ChocEngine) getProblem().getPropagationEngine()).getQueue(evt);
        if (this.hook != null) this.hook.deactivateListener();
        q.remove(evt);
      }
    }
  }

  /**
   * records that a constraint is now entailed (therefore it is now useless to propagate it again)
   */
  public void setEntailed() {
    Constraint rootConstraint = getRootConstraint();
    if (rootConstraint == this) {
      setPassive();
    } else {
      CompositeConstraint root = (CompositeConstraint) rootConstraint;
      int varOffset = root.getGlobalVarIndex(this, 0);
      ((BoolConstraint) root).setSubConstraintStatus(this, true, varOffset);
    }
  }

  /**
   * Removes a constraint from the network.
   * Beware, this is a permanent removal, it may not be backtracked
   */

  public void delete() {
     getProblem().eraseConstraint(this);
  }

  /**
   * raise a contradiction during propagation when the constraint can definitely not be satisfied given the current domains
   * @throws ContradictionException contradiction exception
   */
  public void fail() throws ContradictionException {
    getProblem().getPropagationEngine().raiseContradiction(this);
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
    return active.get();
  }

  /**
   * This function connects a constraint with its variables in several ways.
   * Note that it may only be called once the constraint
   * has been fully created and is being posted to a problem.
   * Note that it should be called only once per constraint.
   * This can be a dynamic addition (undone upon backtracking) or not
   * @param dynamicAddition if the addition should be dynamical
   */

  public void addListener(boolean dynamicAddition) {
    int n = getNbVars();
    for (int i = 0; i < n; i++) {
      setConstraintIndex(i, getVar(i).addConstraint(this, i, dynamicAddition));
    }
    if (this.hook != null) this.hook.addListener();
    active = this.getProblem().getEnvironment().makeBool(true);
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
     * @return the opposite constraint
     */
  public AbstractConstraint opposite() {
    throw new UnsupportedOperationException();
  }

    /**
     *
     * @param v 
     * @param j
     * @param dynamicAddition
     * @return
     */
  public int connectVar(Var v, int j, boolean dynamicAddition) {
    int cidx = v.addConstraint(this, j, dynamicAddition);
    setConstraintIndex(j, cidx);
    return cidx;
  }

    /**
     * Indicates if a constraint is equivalent to the current constraint
     * @param compareTo constraint to compare to
     * @return wether the two constraints are equal
     */
  public boolean isEquivalentTo(Constraint compareTo) {
    return this.equals(compareTo);
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
     * This method returns the number of failure that have encountered
     *
     * @return the number of failure
     */
    public int getNbFailure() {
        return nbFailure;
    }

    /**
     * CPRU 07/12/2007: DomOverWDeg implementation
     * This method adds i to the failure counter
     *
     * @param i number to add at the failure counter
     */
    public void incNbFailure(int i) {
        nbFailure += i;
    }

    /**
     * CPRU 07/12/2007: DomOverWDeg implementation
     * This method add 1 to the failure counter
     *
     */
    public void incNbFailure() {
        incNbFailure(1);
    }


    /**
     * CPRU 07/12/2007: DomOverWDeg implementation
     * This method returns the number of variables not already instanciated
     *
     * @return the number of failure
     */
    public StoredInt getNbVarNotInst() {
        return nbVarNotInst;
    }

    /**
     * CPRU 07/12/2007: DomOverWDeg implementation
     * This method sets the number of variables not already instanciated to nbVarNotInst
     *
     * @param nbVarNotInst number of not instantiated variables
     * @return the number of failure
     */
    public void setNbVarNotInst(StoredInt nbVarNotInst) {
        this.nbVarNotInst = nbVarNotInst;
    }


    /**
     * CPRU 07/12/2007: DomOverWDeg implementation
     * This method adds i to the failure counter
     */
    public void decNbVarNotInst() {
       if (nbVarNotInst != null) // <hca> todo : pb with Palm
	       nbVarNotInst.set(nbVarNotInst.get()-1);
    }

    public void setProblem(AbstractProblem pb){
        super.setProblem(pb);
        // 07/12/2007 CPRU: init the number of variables not instanciated of the constraint.
        this.nbVarNotInst = (StoredInt)this.getProblem().getEnvironment().makeInt(this.getNbVars());

    }

}
