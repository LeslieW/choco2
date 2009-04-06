// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;

import java.util.Iterator;

/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */

/**
 * Interface for all implementations of domain variables.
 */
public interface Var extends Entity {

    /**
     * Returns the number of listeners involving the variable.
     * @return the numbers of listeners involving the variable
     */

    public int getNbConstraints();

    /**
     * a constraint may fail during propagation, raising a contradiction
     * @throws ContradictionException contradiction exception
     */

    public void fail() throws ContradictionException;

    /**
     * Returns the <code>i</code>th constraint. <code>i</code>
     * should be more than or equal to 0, and less or equal to
     * the number of constraint minus 1.
     * @param i number of constraint to be returned
     * @return the ith constraint
     */

    public Constraint getConstraint(int i);


    /**
     * returns the index of the variable in it i-th constraint
     *
     * @param constraintIndex the index of the constraint (among all constraints linked to the variable)
     * @return the index of the variable (0 if this is the first variable of that constraint)
     */
    public int getVarIndex(int constraintIndex);


    /**
     * access the data structure storing constraints involving a given variable
     *
     * @return the vector of constraints
     */
    public PartiallyStoredVector getConstraintVector();

    /**
     * access the data structure storing indices associated to constraints involving a given variable
     *
     * @return the vector of index
     */
    public PartiallyStoredIntVector getIndexVector();

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> testing whether a variable is instantiated or not.
     * @return a boolean giving if a variable is instanciated or not
     */

    public boolean isInstantiated();

    /**
     * Adds a new listener for the variable, that is a constraint which
     * should be informed as soon as the variable domain is modified.
     * The addition can be dynamic (undone upon backtracking) or not
     * @param c the constraint to add
     * @param varIdx index of the variable
     * @param dynamicAddition dynamical addition or not
     *  @return the number of the listener
     */
    public int addConstraint(Constraint c, int varIdx, boolean dynamicAddition);

    /**
     * returns the object used by the propagation engine to model a propagation event associated to the variable
     * (an update to its domain)
     *
     * @return the propagation event
     */
    public VarEvent getEvent();

    /**
     * This methods should be used if one want to access the different constraints
     * currently posted on this variable.
     * <p/>
     * Indeed, since indices are not always
     * consecutive, it is the only simple way to achieve this.
     *
     * @return an iterator over all constraints involving this variable
     */
    public Iterator getConstraintsIterator();


    /**
     * CPRU 07/12/2007: DomOverFailureDeg implementation
     * This methods returns the number of failure that have encountered
     *
     * @return the number of failure
     */
    public int getNbFailure();

}
