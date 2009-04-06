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

import choco.kernel.model.ModelException;
import choco.kernel.model.variables.Variable;

import java.util.Iterator;

/**
 * @author Arnaud Malapert
 *
 */
public class BinaryConstraint<E extends Variable> extends AbstractConstraint {


	protected E v1, v2;



	public BinaryConstraint(final ConstraintType type, final E v1, final E v2) {
		super(type);
		this.v1 = v1;
		this.v2 = v2;
	}

	/**
	 * @see choco.kernel.model.constraints.Constraint#getNbVars()
	 */
	@Override
	public int getNbVars() {
		return 2;
	}

	/**
	 * @see choco.kernel.model.constraints.Constraint#getVariableIterator()
	 */
	@Override
	public Iterator<Variable> getVariableIterator() {
		return new Iterator<Variable>(){
            int i =-1;
            public boolean hasNext() {
                return i< 2;
            }

            public Variable next() {
                return (i++==1?v1:v2);
            }

            public void remove() {
                throw new ModelException("can not remove variable from binary constraint");
            }
        };
	}

	public E getV1() {
        return v1;
    }

    public E getV2() {
        return v2;
    }


}
