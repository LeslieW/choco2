package i_want_to_use_this_old_version_of_choco.reified;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 25 janv. 2008
 * Time: 09:46:25
 */
public abstract class AbstractUnaryReifiedConstraint extends AbstractReifiedConstraint {
    /**
     * the subconstraint of the unary reified constraint
     */
    protected AbstractConstraint const0;

    /**
     * attaching the constraint to a given problem (need to attach the sub-constraint)
     *
     * @param problem the given problem
     */
    public void setProblem(AbstractProblem problem) {
        super.setProblem(problem);
        const0.setProblem(problem);
    }

    /**
     * Builds a new unary constraint with the one specified
     * sub-constraint.
     *
     * @param constraint the sub-constraint
     */
    public AbstractUnaryReifiedConstraint(final AbstractConstraint constraint) {
        const0 = constraint;
    }

    /**
     * Builds a copy of this constraint.
     *
     * @return a copy of this constraint
     * @throws CloneNotSupportedException if an problem occurs when cloning
     *                                    elements pf this constraint
     */
    public Object clone() throws CloneNotSupportedException {
        AbstractBinaryReifiedConstraint newc =
                (AbstractBinaryReifiedConstraint) super.clone();
        newc.const0 = (AbstractConstraint) this.const0.clone();
        return newc;
    }

    /**
     * Assigns indices to variables for the global constraint involving
     * this one.
     *
     * @param root            the global constraint including this one
     * @param i               the first available index
     * @param dynamicAddition states if the constraint is added definitively
     * @return the next available index for the global constraint
     */
    public int assignIndices(final AbstractReifiedConstraint root,
                             final int i, final boolean dynamicAddition) {
        int j = i;
        j = const0.assignIndices(root, j, dynamicAddition);
        return j;
    }

    /**
     * Returns the index of the sub-constraint involving the variable
     * varIdx.
     *
     * @param varIdx the variable index
     * @return 0, the subConstraint index
     */
    public int getSubConstraintIdx(final int varIdx) {
        return 0;
    }

    /**
     * Determines the number of variables, that is the sum of all variables
     * in sub-constraint.
     *
     * @return the number of variables
     */
    public int getNbVars() {
        return const0.getNbVars();
    }


    /**
     * Accesses the variable i.
     *
     * @param i the index of the variable
     * @return the requested variable
     */
    public Var getVar(final int i) {
        return const0.getVar(i);
    }

    /**
     * Sets the variable i.
     *
     * @param i the variable index
     * @param v the variable
     */
    public void setVar(final int i, final Var v) {
        const0.setVar(i, v);
    }

    /**
     * Checks if all variables are instantiated, that if sub-constraint
     * variables are instantiated.
     *
     * @return true if all variables are instantiated
     */
    public boolean isCompletelyInstantiated() {
        return const0.isCompletelyInstantiated();
    }


    /**
     * Returns the constraint index according to the variable i.
     *
     * @param i the variable index
     * @return this constraint index according to the variable
     */
    public int getConstraintIdx(final int i) {
        return const0.getConstraintIdx(i);
    }

    /**
     * Sets the constraint index according to the variable i.
     *
     * @param i   the variable index
     * @param idx the requested constraint index
     */
    public void setConstraintIndex(final int i, final int idx) {
        const0.setConstraintIndex(i, idx);
    }

    /**
     * Accesses the sub-constraint.
     *
     * @param constIdx the constraint index (0 here)
     * @return the requested constraint
     */
    public Constraint getSubConstraint(final int constIdx) {
        return const0;
    }

    /**
     * Returns the number of direct sub-constraint (1 here since this is a
     * unary constraint).
     *
     * @return the number of direct sub-constraints
     */
    public int getNbSubConstraints() {
      return 1;
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
