// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Iterator;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * An abstract class for all implementations of domain variables.
 */
public abstract class AbstractVar extends AbstractEntity implements Var {

  /**
   * A name may be associated to each variable.
   */
  protected String name;

  /**
   * The variable var associated to this variable.
   */
  protected VarEvent event;


  /**
   * The list of constraints (listeners) observing the variable.
   */
  protected PartiallyStoredVector constraints;

    /**
     * * CPRU 07/12/2007: DomOverFailureDeg implementation
     * The number of failure of this variable
     */
    protected int nbFailure;

    /**
   * List of indices encoding the constraint network.
   * <i>v.indices[i]=j</i> means that v is the j-th variable
   * of its i-th constraint.
   */
  protected PartiallyStoredIntVector indices;

  /**
   * Initializes a new variable.
   * @param pb The problem this variable belongs to
   * @param name The name of the variable
   */
  public AbstractVar(final AbstractProblem pb, final String name) {
    super(pb);
    this.name = name;
    IEnvironment env = pb.getEnvironment();
    constraints = env.makePartiallyStoredVector();
    indices = env.makePartiallyStoredIntVector();
      //CPRU 07/12/2007: init the failure counter.
    nbFailure = 1;
  }

  /**
   * Useful for debugging.
   * @return the name of the variable
   */
  public String toString() {
    return name;
  }

  /**
   * Returns the variable event.
   * @return the event responsible for propagating variable modifications
   */
  public VarEvent getEvent() {
    return event;
  }


  /**
   * Retrieve the constraint i involving the variable.
   * Be careful to use the correct constraint index (constraints are not
   * numbered from 0 to number of constraints minus one, since an offset
   * is used for some of the constraints).
   * @param i the number of the required constraint
   * @return the constraint number i according to the variable
   */
  public Constraint getConstraint(final int i) {
    return ((Constraint) constraints.get(i));
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
  public PartiallyStoredVector getConstraintVector() {
    return constraints;
  }

  /**
   * Access the data structure storing indices associated to constraints 
   * involving a given variable.
   * @return the indices associated to this variable in each constraint
   */
  public PartiallyStoredIntVector getIndexVector() {
    return indices;
  }

  /**
   * Returns the index of the variable in its constraint i.
   * @param constraintIndex the index of the constraint 
   * (among all constraints linked to the variable)
   * @return the index of the variable
   */
  public int getVarIndex(final int constraintIndex) {
    return indices.get(constraintIndex);
  }

  /**
   * Removes (permanently) a constraint from the list of constraints 
   * connected to the variable.
   * @param c the constraint that should be removed from the list this variable
   * maintains.
   */
  public void eraseConstraint(final Constraint c) {
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
  public int addConstraint(final Constraint c, final int varIdx,
                           final boolean dynamicAddition) {
    int constraintIdx;
    if (dynamicAddition) {
      constraintIdx = constraints.add(c);
      indices.add(varIdx);
    } else {
      constraintIdx = constraints.staticAdd(c);
      indices.staticAdd(varIdx);
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
  public Iterator getConstraintsIterator() {
    return new Iterator() {
      IntIterator indices = constraints.getIndexIterator();

      public boolean hasNext() {
        return indices.hasNext();
      }

      public Object next() {
        return constraints.get(indices.next());
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

  }

    /**
     * CPRU 07/12/2007:
     * This method returns the number of failure that have encountered
     *
     * @return the number of failure
     */
    public int getNbFailure() {
        return nbFailure;
    }

    /**
     * CPRU 07/12/2007: DomOverFailureDeg implementation
     * This method adds i to the failure counter
     * @param i number of failure to add
     */
    public void incNbFailure(int i) {
        nbFailure += i;
    }

    /**
     * CPRU 07/12/2007: DomOverFailureDeg implementation
     * This method add 1 to the failure counter
     */
    public void incNbFailure() {
        incNbFailure(1);
    }

    /**
     * * CPRU 07/12/2007: DomOverFailureDeg implementation
     * This methods at least raise the number of failure.
     *
     * @throws ContradictionException
     */
    public void fail() throws ContradictionException {
        incNbFailure();
    }


    /**
     * CPRU 07/12/2007: DomOverFailureDeg implementation
     * This method take into account the instanciation of the variables.
     * CPRU_BUG_1877365
     *
     */
    public void updateNbVarInstanciated(){
        Iterator itCstr = this.getConstraintsIterator();
        while(itCstr.hasNext()){
            AbstractConstraint cstr = (AbstractConstraint)itCstr.next();
            cstr.decNbVarNotInst();
        }
    }

}
