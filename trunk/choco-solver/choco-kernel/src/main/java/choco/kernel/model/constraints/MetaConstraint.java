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
package choco.kernel.model.constraints;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.model.variables.Variable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/* 
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 5 aout 2008
 * Since : Choco 2.0.0
 *
 */
public class MetaConstraint<E extends Constraint> extends AbstractConstraint {

    protected E[] constraints;

    public MetaConstraint(final ConstraintType type, final E... constraints) {
        super(type);
        this.constraints = constraints;
    }

    public MetaConstraint(final Class metaManager, final E... constraints) {
        super(metaManager.getName());
        this.constraints = constraints;
    }

    public MetaConstraint(final String metaManager, final E... constraints) {
        super(metaManager);
        this.constraints = constraints;
    }

    /**
     * Preprocessing that helps the garbage collector.
     */
    @Override
    public void freeMemory() {
        Arrays.fill(constraints, null);
        constraints = null;
    }

    public E[] getConstraints() {
        return constraints;
    }

    public final E getConstraint(final int idx) {
        return constraints[idx];
    }

    public Iterator<Variable> getVariableIterator() {
        return IteratorUtils.iterator(extractVariables());
    }


    public int getNbVars() {
        return 0;
    }

    /**
     * Extract variables of a constraint
     * and return an array of variables.
     * @return an array of every variables contained in the Constraint.
     */
    public Variable[] extractVariables() {
        Variable[] listVars = new Variable[0];
        for (Constraint c : constraints) {
            listVars = ArrayUtils.append(listVars, c.extractVariables());
        }
        return ArrayUtils.getNonRedundantObjects(Variable.class, listVars);
    }

    @Override
    public void findManager(Properties propertiesFile) {
        super.findManager(propertiesFile);
        for (int i = 0; i < constraints.length; i++) {
            E constraint = constraints[i];
            constraint.findManager(propertiesFile);
        }
    }
}
