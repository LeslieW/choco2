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
package choco.kernel.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.variables.real.RealInterval;

/**
 * A class storing a state of the model
 */
public class Solution {

	/**
	 * the solver owning the solution
	 */
	protected final Solver solver;

	/**
	 * data storage for values of search variables
	 */
	protected final int[] intVarValues;

	protected final RealInterval[] realVarValues;

	protected final int[][] setVarValues;

	protected int objectiveValue;

	protected  final Map<AbstractGlobalSearchLimit, AbstractGlobalSearchLimit> limits;


	/**
	 * Constructor
	 *
	 * @param solver the model owning the solution
	 */
	public Solution(Solver solver) {
		this.solver = solver;
		int nbv = solver.getNbIntVars();
		intVarValues = new int[nbv];
		setVarValues = new int[solver.getNbSetVars()][];
		realVarValues = new RealInterval[solver.getNbRealVars()];
		for (int i = 0; i < nbv; i++) {
			intVarValues[i] = Integer.MAX_VALUE;
		}
		objectiveValue = Integer.MAX_VALUE;
		final List<AbstractGlobalSearchLimit> tmp = solver.getSearchStrategy().limits;
		limits = new HashMap<AbstractGlobalSearchLimit, AbstractGlobalSearchLimit>(tmp.size());
		for (AbstractGlobalSearchLimit l : tmp) {
			limits.put(l, new StoredLimit(l));
		}

	}


	public final int getObjectiveValue() {
		return objectiveValue;
	}


	public void recordIntValue(int intVarIndex, int intVarValue) {
		intVarValues[intVarIndex] = intVarValue;
	}

	public void recordSetValue(int setVarIndex, int[] setVarValue) {
		setVarValues[setVarIndex] = setVarValue;
	}

	public void recordRealValue(int realVarIndex, RealInterval realVarValue) {
		realVarValues[realVarIndex] = realVarValue;
	}

	public void recordIntObjective(int intObjectiveValue) {
		objectiveValue = intObjectiveValue;
	}

	public void recordLimit(AbstractGlobalSearchLimit limit){
		final StoredLimit tmp = (StoredLimit) limits.get(limit);
		tmp.synchronize(limit);
	}


	/**
	 * Accessor to the value of a variable in a solution
	 *
	 * @param varIndex the index of the variable among all variables of the model
	 * @return its value (whenever it is instantiated in the solution), or Integer.MAX_VALUE otherwise
	 */
	public int getIntValue(int varIndex) {
		return intVarValues[varIndex];
	}

	public int[] getSetValue(int varIndex) {
		return setVarValues[varIndex];
	}

	public RealInterval getRealValue(int varIndex) {
		return realVarValues[varIndex];
	}

	public Collection<AbstractGlobalSearchLimit> getLimits() {
		return limits.values();
	}
}


class StoredLimit extends AbstractGlobalSearchLimit {

	/**
	 * A copy constructor.
	 */
	public StoredLimit(AbstractGlobalSearchLimit toCopy) {
		super(toCopy.getStrategy(),toCopy.getNbMax(),toCopy.getType());
		nb = toCopy.getNb();
		nbTot = toCopy.getNbTot();
		nbMax = toCopy.getNbMax();
	}

	/**
	 * synchronize the counters of the two limits.
	 */
	public final void synchronize(AbstractGlobalSearchLimit toSync) {
		nb = toSync.getNb();
		nbTot = toSync.getNbTot();
	}
	@Override
	public boolean endNode(AbstractGlobalSearchStrategy strategy) {
		return false;
	}

	@Override
	public boolean newNode(AbstractGlobalSearchStrategy strategy) {
		return false;
	}



}
