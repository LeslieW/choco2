package i_want_to_use_this_old_version_of_choco.reified;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: narendra
 * Date: 14 janv. 2008
 * Time: 16:41:26
 */
public abstract class AbstractBinaryReifiedConstraint extends AbstractReifiedConstraint {

    /**
     *  the first subconstraint of the binary reified constraint
     */
    protected AbstractConstraint const0;

    /**
     * the second subconstraint of the binary reified constraint
     */
    protected AbstractConstraint const1;


    /**
     * taking into account the number of variables in the first subconstraint
     */
    protected int offset = 0;


    /**
     * attaching the constraint to a given problem (need to attach the sub-constraints)
     * @param problem the given problem
     */

    public void setProblem(AbstractProblem problem) {
        super.setProblem(problem);
        const0.setProblem(problem);
        const1.setProblem(problem);
    }

    /**
   * Builds a new binary composite constraint with the two specified
   * sub-constraints.
   * @param c1 the first sub-constraint
   * @param c2 the second sub-constraint
   */
  public AbstractBinaryReifiedConstraint (final AbstractConstraint c1,
      final AbstractConstraint c2) {
    const0 = c1;
    const1 = c2;
    offset = const0.getNbVars();
  }

  /**
   * Builds a copy of this constraint.
   * @return a copy of this constraint
   * @throws CloneNotSupportedException if an problem occurs when cloning
   * elements pf this constraint
   */
  public Object clone() throws CloneNotSupportedException {
    AbstractBinaryReifiedConstraint newc =
        (AbstractBinaryReifiedConstraint) super.clone();
    newc.const0 = (AbstractConstraint) this.const0.clone();
    newc.const1 = (AbstractConstraint) this.const1.clone();
    return newc;
  }

  /**
   * Assigns indices to variables for the global constraint involving
   * this one.
   * @param root the global constraint including this one
   * @param i the first available index
   * @param dynamicAddition states if the constraint is added definitively
   * @return the next available index for the global constraint
   */
  public int assignIndices(final AbstractReifiedConstraint root,
      final int i, final boolean dynamicAddition) {
    int j = i;
    j = const0.assignIndices(root, j, dynamicAddition);
    this.offset = j - i;
    j = const1.assignIndices(root, j, dynamicAddition);
    return j;
  }


  /**
   * Returns the index of the sub-constraint involving the variable
   * varIdx.
   * @param varIdx the variable index
   * @return 0 if this is the first sub-constraint, 1 else
   */
  public int getSubConstraintIdx(final int varIdx) {
    if (varIdx < offset) {
      return 0;
    } else {
      return 1;
    }
  }


  /**
   * Determines the number of variables, that is the sum of all variables
   * in sub-constraints.
   * @return the number of variables
   */
  public int getNbVars() {
    return const0.getNbVars() + const1.getNbVars();
  }

  /**
   * Accesses the variable i.
   * @param i the index of the variable
   * @return the requested variable
   */
  public Var getVar(final int i) {
    return ((i < offset) ? const0.getVar(i) : const1.getVar(i - offset));
  }

  /**
   * Sets the variable i.
   * @param i the variable index
   * @param v the variable
   */
  public void setVar(final int i, final Var v) {
    if (i < offset) {
      const0.setVar(i, v);
    } else {
      const1.setVar(i - offset, v);
    }
  }

  /**
   * Checks if all variables are instantiated, that if sub-constraints
   * variables are instantiated.
   * @return true if all variables are instantiated
   */
  public boolean isCompletelyInstantiated() {
    return (const0.isCompletelyInstantiated()
    && const1.isCompletelyInstantiated());
  }

  /**
   * Returns the constraint index according to the variable i.
   * @param i the variable index
   * @return this constraint index according to the variable
   */
  public int getConstraintIdx(final int i) {
    return ((i < offset) ? const0.getConstraintIdx(i)
    : const1.getConstraintIdx(i - offset));
  }

  /**
   * Sets the constraint index according to the variable i.
   * @param i the variable index
   * @param idx the requested constraint index
   */
  public void setConstraintIndex(final int i, final int idx) {
    if (i < offset) {
      const0.setConstraintIndex(i, idx);
    } else {
      const1.setConstraintIndex(i - offset, idx);
    }
  }

  /**
   * Accesses the sub-constraints.
   * @param constIdx the constraint index (0 or 1 here)
   * @return the requested constraint
   */
  public Constraint getSubConstraint(final int constIdx) {
    return ((constIdx == 0) ? const0 : const1);
  }

  /**
   * Returns the number of direct sub-constraints (2 here since this is a
   * binary composite constraint).
   * @return the number of direct sub-constraints
   */
  public int getNbSubConstraints() {
    return 2;
  }


    public IntDomainVar getIntVar(int i) {
        return null;
    }

    public RealVar getRealVar(int i) {
        return null;
    }

    public int getRealVarNb() {
        return 0;
    }

    public SetVar getSetVar(int i) {
        return null;
    }

    public void awakeOnkerAdditions(int varIdx, IntIterator deltaDomain) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnEnvRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnBounds(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnInf(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnSup(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnInst(int varIdx) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        this.constAwake(false);
    }
}
