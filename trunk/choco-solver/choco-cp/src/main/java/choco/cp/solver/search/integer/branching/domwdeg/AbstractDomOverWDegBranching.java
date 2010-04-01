/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.branching.domwdeg;

import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addConstraintExtension;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addIncFailure;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.computeWeightedDegreeFromScratch;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getConstraintExtension;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getConstraintFailures;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getVarExtension;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getVariableIncWDeg;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.hasTwoNotInstVars;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.initConstraintExtensions;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.initVarExtensions;

import java.util.Iterator;

import choco.cp.solver.search.integer.branching.IRandomBreakTies;
import choco.cp.solver.search.integer.varselector.ratioselector.IntVarRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.MinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandMinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractLargeIntBranchingStrategy;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;

public abstract class AbstractDomOverWDegBranching extends
AbstractLargeIntBranchingStrategy implements PropagationEngineListener, IRandomBreakTies {

	protected final Solver solver;

	protected final IntRatio[] varRatios;
	
	private IntVarRatioSelector ratioSelector;
	
	public AbstractDomOverWDegBranching(Solver solver, IntRatio[] varRatios, Number seed) {
		super();
		this.solver = solver;
		this.varRatios = varRatios;
		initConstraintExtensions(this.solver);
		initVarExtensions(this.solver);
		this.solver.getPropagationEngine().addPropagationEngineListener(this);
		if(seed == null) cancelRandomBreakTies();
		else setRandomBreakTies(seed.longValue());
	}


	public final Solver getSolver() {
		return solver;
	}

	protected final IntVarRatioSelector getRatioSelector() {
		return ratioSelector;
	}

	@Override
	public void cancelRandomBreakTies() {
		ratioSelector = new MinRatioSelector(solver, varRatios);
	}


	@Override
	public void setRandomBreakTies(long seed) {
		ratioSelector = new RandMinRatioSelector(solver, varRatios, seed);
		
	}


	//*****************************************************************//
	//*******************  Weighted degrees and failures managment ***//
	//***************************************************************//

	@Override
	public void initConstraintForBranching(SConstraint c) {
		//FIXME already called in the constructor ?
		addConstraintExtension(c);
	}

	
	@Override
	public void initBranching() {
		for (int i = 0; i < solver.getNbIntVars(); i++) {
			// Pour etre sur, on verifie toutes les contraintes... au cas ou une d'entre elle serait deja instantiee !!
			final Var v = solver.getIntVar(i);
			getVarExtension(v).set(computeWeightedDegreeFromScratch(v));
		}
	}

	protected final void updateVarWeights(Var currentVar, boolean assign) {
		for (Iterator<SConstraint> iter = currentVar.getConstraintsIterator(); iter.hasNext();) {
			final SConstraint cstr = iter.next();
			if (SConstraintType.INTEGER.equals(cstr.getConstraintType()) && hasTwoNotInstVars(cstr) ) {
				int delta = assign ? -getConstraintExtension(cstr).get() : getConstraintExtension(cstr).get();
				//System.out.println("branching "+reuseCstr.pretty()+" "+getConstraintExtension(reuseCstr).getNbFailure());
				for (int k = 0; k < cstr.getNbVars(); k++) {
					AbstractVar var = (AbstractVar) cstr.getVar(k);
					if (var != currentVar && !var.isInstantiated()) {
						getVarExtension(var).add(delta);
					}
				}
			}
		}
	}

	public final void contradictionOccured(ContradictionException e) {
		addIncFailure(e.getDomOverDegContradictionCause());
	}

	@Override
	public final void safeDelete() {
		solver.getPropagationEngine().removePropagationEngineListener(this);
	}
	//*****************************************************************//
	//*******************  Variable Selection *************************//
	//***************************************************************//


	public Object selectBranchingObject() throws ContradictionException {
		return ratioSelector.selectIntVar();
	}

	@Override
	public String toString() {
		return getVariableIncWDeg(solver) + "\n" + getConstraintFailures(solver);
	}

	

}