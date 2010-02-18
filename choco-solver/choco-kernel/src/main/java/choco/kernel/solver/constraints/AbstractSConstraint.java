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

import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.Extension;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.variables.Var;

import java.util.HashMap;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */

/**
 * An abstract class for all implementations of listeners
 */
public abstract class AbstractSConstraint<V extends Var> extends Propagator implements SConstraint<V> {

    /**
     * The list of variables of the constraint.
     */

    protected V[] vars;


    /**
     * The list, containing, for each variable, the index of the constraint among all
     * its incident listeners.
     */

    public int[] cIndices;

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
     *
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
    public Extension[] extensions = new Extension[1];

    /**
     * Constructs a constraint with the priority 0.
     * @param vars variables involved in the constraint
     */

    protected AbstractSConstraint(V[] vars) {
        super();
        this.vars = vars;
        cIndices = new int[vars.length];
    }

    /**
     * Constructs a constraint with the specified priority.
     *
     * @param priority The wished priority.
     * @param vars variables involved in the constraint
     */

    protected AbstractSConstraint(int priority, V[] vars) {
        super(priority);
        this.vars = vars;
        cIndices = new int[vars.length];
    }


    /**
     * Adds a new extension.
     *
     * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
     */
    public void addExtension(int extensionNumber) {
        if (extensionNumber > extensions.length) {
            Extension[] newArray = new Extension[extensions.length * 2];
            System.arraycopy(extensions, 0, newArray, 0, extensions.length);
            extensions = newArray;
        }
        extensions[extensionNumber] = new Extension();
    }

    /**
     * Returns the queried extension
     *
     * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
     * @return the queried extension
     */
    public Extension getExtension(int extensionNumber) {
        return extensions[extensionNumber];
    }

    /**
     * Indicates if the constraint is entailed, from now on will be always satisfied
     *
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
     * This function connects a constraint with its variables in several ways.
     * Note that it may only be called once the constraint
     * has been fully created and is being posted to a model.
     * Note that it should be called only once per constraint.
     * This can be a dynamic addition (undone upon backtracking) or not
     *
     * @param dynamicAddition if the addition should be dynamical
     */

    public void addListener(boolean dynamicAddition) {
        int n = getNbVars();
        for (int i = 0; i < n; i++) {
            setConstraintIndex(i, getVar(i).addConstraint(this, i, dynamicAddition));
            getVar(i).getEvent().addPropagatedEvents(getFilteredEventMask(i));
        }
    }

    /**
     * Let <i>v</i> be the <i>i</i>-th var of <i>c</i>, records that <i>c</i> is the
     * <i>n</i>-th constraint involving <i>v</i>.
     */
    public void setConstraintIndex(int i, int val) {
        if (i >= 0 && i < vars.length) {
            cIndices[i] = val;
        } else {
            throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
        }
    }


    /**
     * Returns the index of the constraint in the specified variable.
     */

    public int getConstraintIdx(int i) {
        if (i >= 0 && i < vars.length) {
            return cIndices[i];
        } else {
            return -1;
        }
    }


    /**
     * Checks wether all the variables are instantiated.
     */

    @Override
    public boolean isCompletelyInstantiated() {
        int nVariables = vars.length;
        for (int i = 0; i < nVariables; i++) {
            if (!(vars[i].isInstantiated())) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns the number of variables.
     */

    public int getNbVars() {
        return vars.length;
    }


    /**
     * Returns the <code>i</code>th variable.
     */

    public V getVar(int i) {
        if (i >= 0 && i < vars.length) {
            return vars[i];
        } else {
            return null;
        }
    }

    public void setVar(int i, V v) {
        if (i >= 0 && i < vars.length) {
            this.vars[i] = v;
        } else {
            throw new SolverException("BUG in CSP network management: too large index for setVar");
        }
    }

    /**
     * Get the opposite constraint
     *
     * @return the opposite constraint  @param solver
     */
    public AbstractSConstraint opposite(Solver solver) {
        throw new UnsupportedOperationException();
    }

    /**
     * Clone the constraint
     *
     * @return the clone of the constraint
     * @throws CloneNotSupportedException Clone not supported exception
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    public abstract SConstraintType getConstraintType();


    public String pretty() {
        StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append("{");
        final int n = getNbVars();
        for (int i = 0; i < n - 1; i++) {
            b.append(getVar(i).getName()).append(", ");
        }
        b.append(getVar(n - 1).getName()).append("}");
        return b.toString();
    }


    /**
     * Some global constraint might be able to provide
     * some fine grained information about the "real" degree of a variables.
     * For example the global constraint on clauses can give the real number of
     * clauses on each variable
     *
     * @param idx index of the variable in the constraint
     * @return a weight given to the variable by the constraint
     */
    public int getFineDegree(int idx) {
        return 1;
    }

}
