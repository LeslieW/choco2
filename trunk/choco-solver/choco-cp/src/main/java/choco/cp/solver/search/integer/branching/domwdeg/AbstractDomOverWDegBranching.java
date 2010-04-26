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
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addConstraintToVarWeights;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addFailure;
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
	
	//helps to synchronize incremental weights
	protected int updateWeightsCount;

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

	public final IntVarRatioSelector getRatioSelector() {
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
		addConstraintExtension(c);
		addConstraintToVarWeights(c);
	}


	protected abstract int getExpectedUpdateWeightsCount();
	
	@Override
	public final void initBranching() {
		final int n = solver.getNbIntVars();
		for (int i = 0; i < n; i++) {
			final Var v = solver.getIntVar(i);
			getVarExtension(v).set(computeWeightedDegreeFromScratch(v));
		}
		updateWeightsCount =  getExpectedUpdateWeightsCount();
	}

	protected final void reinitBranching() {
		if( updateWeightsCount != getExpectedUpdateWeightsCount()) initBranching();
	}

	private void updateVarWeights(final Var currentVar, final SConstraint<?> cstr, final int delta) {
		//TODO retrieve index of the variable and duplicate loop ?
		if(delta != 0) {
			final int n = cstr.getNbVars();
			for (int k = 0; k < n; k++) {
				final AbstractVar var = (AbstractVar) cstr.getVarQuick(k);
				if (var != currentVar && ! var.isInstantiated()) {
					getVarExtension(var).add(delta);
					if(getVarExtension(var).get() < 0) {
						System.out.println(DomWDegUtils.getVariableWDeg(solver));
						System.out.println();
						System.out.println(this);
					}
					assert getVarExtension(var).get() >= 0; //check robustness of the incremental weights
				}
			}
		}
	}


	private boolean isDisconnected(SConstraint<?> cstr) {
		return SConstraintType.INTEGER.equals(cstr.getConstraintType()) && hasTwoNotInstVars(cstr); 
	}

	protected final void increaseVarWeights(final Var currentVar) {
		updateWeightsCount--;
		final Iterator<SConstraint> iter = currentVar.getConstraintsIterator();
		while(iter.hasNext()) {
			final SConstraint cstr = iter.next();
			if (isDisconnected(cstr) ) {
				updateVarWeights(currentVar, cstr, getConstraintExtension(cstr).get());
			}
		}
	}

	protected final void decreaseVarWeights(final Var currentVar) {
		updateWeightsCount++;
		final Iterator<SConstraint> iter = currentVar.getConstraintsIterator();
		while(iter.hasNext()) {
			final SConstraint cstr = iter.next();
			if (isDisconnected(cstr) ) {
				updateVarWeights(currentVar, cstr, - getConstraintExtension(cstr).get());
			}
		}
	}

	@Override
	public final void contradictionOccured(ContradictionException e) {
		if( updateWeightsCount == getExpectedUpdateWeightsCount() ) {
			addIncFailure(e.getDomOverDegContradictionCause());
		} else {
			//weights are already out-of-date
			addFailure(e.getDomOverDegContradictionCause());
		}
	}

	
	@Override
	public final void safeDelete() {
		solver.getPropagationEngine().removePropagationEngineListener(this);
	}
	//*****************************************************************//
	//*******************  Variable Selection *************************//
	//***************************************************************//

	public Object selectBranchingObject() throws ContradictionException {
		reinitBranching();
//		System.out.println(DomWDegUtils.checkVariableIncWDeg(solver));
		return ratioSelector.selectVar();
	}
	
	@Override
	public String toString() {
		return "nbUpdates: "+updateWeightsCount+"\n"+getVariableIncWDeg(solver) + "\n" + getConstraintFailures(solver);
	}



}