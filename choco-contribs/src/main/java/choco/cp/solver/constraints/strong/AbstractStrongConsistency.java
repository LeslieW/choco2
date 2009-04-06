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
package choco.cp.solver.constraints.strong;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStrongConsistency<MyVariable extends SCVariable<? extends SCConstraint>>
		extends AbstractLargeIntSConstraint {

	protected final SCConstraint[] constraints;
	private final List<MyVariable> variables;
	protected final Map<IntDomainVar, MyVariable> variablesMap;

	public AbstractStrongConsistency(IntDomainVar[] vars,
			ISpecializedConstraint[] constraints,
			Class<? extends MyVariable> myVariable,
			Class<? extends SCConstraint> myConstraint) {
		super(vars);
		// Création des variables encapsulantes + map avec variables
		// originales
		this.variables = new ArrayList<MyVariable>(getNbVars());

		variablesMap = new HashMap<IntDomainVar, MyVariable>(getNbVars());

		final Constructor<? extends MyVariable> variableConstructor;
		try {
			variableConstructor = myVariable.getConstructor(IntDomainVar.class,
					Integer.class);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
		for (int i = 0; i < getNbVars(); i++) {
			final MyVariable var;
			try {
				var = variableConstructor.newInstance(getIntVar(i), i);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException(e);
			}
			this.variables.add(var);
			variablesMap.put(getIntVar(i), var);
		}

		// Initialisation du réseau interne
		this.constraints = new SCConstraint[constraints.length];

		final Constructor<? extends SCConstraint> constraintConstructor;
		try {
			constraintConstructor = myConstraint.getConstructor(
					ISpecializedConstraint.class, Map.class);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}

		for (int i = constraints.length; --i >= 0;) {

			try {
				this.constraints[i] = constraintConstructor.newInstance(
						constraints[i], variablesMap);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException(e);
			}
			// for (int j = 2; --j >= 0;) {
			// this.constraints[i].getVariable(j).addConstraint(
			// this.constraints[i]);
			// }

		}

	}

	public MyVariable getVariable(int id) {
		return variables.get(id);
	}
	
	public boolean isSatisfied(int[] tuple) {
		final int[] subTuple = new int[2];
		for (SCConstraint c: constraints) {
			for (int i = c.getArity(); --i>=0;) {
				subTuple[i] = tuple[c.getVariable(i).getId()];
			}
			if (!c.check(subTuple)) {
				return false;
			}
		}
		return true;
	}
	
	public void awake() throws ContradictionException {
		for (MyVariable v: variables) {
			v.setCId(this);
		}
	}
}