package choco.cp.solver.search;

import static choco.cp.solver.search.VarSelectorFactory.domDDegSel;
import static choco.cp.solver.search.VarSelectorFactory.domDegSel;
import static choco.cp.solver.search.VarSelectorFactory.domWDegSel;
import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createMaxPreservedRatio;
import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createMinPreservedRatio;
import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createPreservedWDegRatio;
import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createSlackWDegRatio;
import static choco.kernel.common.util.tools.VariableUtils.getTaskVars;

import java.util.Arrays;
import java.util.Comparator;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarValPair;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.branching.domwdeg.DomOverWDegBinBranchingNew;
import choco.cp.solver.search.integer.branching.domwdeg.DomOverWDegBranchingNew;
import choco.cp.solver.search.integer.branching.domwdeg.TaskOverWDegBinBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.search.integer.varselector.ratioselector.DomOverWDegSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.MaxRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.MinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandDomOverWDegSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandMaxRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandMinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.CompositePrecValSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MaxPreservedRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MinPreservedRatio;
import choco.cp.solver.search.set.AssignSetVar;
import choco.cp.solver.search.set.MinEnv;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.cp.solver.search.set.StaticSetVarOrder;
import choco.cp.solver.search.task.OrderingValSelector;
import choco.cp.solver.search.task.SetTimes;
import choco.cp.solver.search.task.ordering.CentroidOrdering;
import choco.cp.solver.search.task.ordering.MaxPreservedOrdering;
import choco.cp.solver.search.task.ordering.MinPreservedOrdering;
import choco.kernel.common.util.comparator.TaskComparators;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;

public final class BranchingFactory {

	private BranchingFactory() {
		super();
	}

	//*************************************************************************//

	public static AssignVar minDomMinVal(Solver s, IntDomainVar[] vars) {
		return new AssignVar( new MinDomain(s, vars), new MinVal());
	}

	public static AssignVar minDomMinVal(Solver s) {
		return minDomMinVal(s, s.getIntDecisionVars());
	}

    //*************************************************************************//

    public static AssignVar minDomIncDom(Solver s, IntDomainVar[] vars) {
		return new AssignVar( new MinDomain(s, vars), new IncreasingDomain());
	}

	public static AssignVar minDomIncDom(Solver s) {
		return minDomMinVal(s, s.getIntDecisionVars());
	}

	//*************************************************************************//

	public static AssignVar lexicographic(Solver solver, IntDomainVar[] vars) {
		return new AssignVar( new StaticVarOrder(solver, vars), new MinVal());
	}

	public static AssignSetVar lexicographic(Solver solver, SetVar[] vars) {
		return new AssignSetVar( new StaticSetVarOrder(solver, vars), new MinEnv());
	}

	//*************************************************************************//

	public static AssignOrForbidIntVarVal randomIntBinSearch(Solver solver, long seed) {
		return randomBinSearch(solver, solver.getIntDecisionVars(), seed);
	}

	public static AssignOrForbidIntVarVal randomBinSearch(Solver solver, IntDomainVar[] vars, long seed) {
		return new AssignOrForbidIntVarVal( new RandomIntVarSelector(solver, vars, seed), new RandomIntValSelector(seed));
	}

	//*************************************************************************//

	public static AssignVar randomIntSearch(Solver solver, long seed) {
		return randomSearch(solver, solver.getIntDecisionVars(), seed);
	}

	public static AssignVar randomSearch(Solver solver, IntDomainVar[] vars, long seed) {
		return new AssignVar( new RandomIntVarSelector(solver, vars, seed), new RandomIntValSelector(seed));
	}

	//*************************************************************************//

	public static AssignSetVar randomSetSearch(Solver solver, long seed) {
		return randomSearch(solver, solver.getSetDecisionVars(), seed);
	}

	public static AssignSetVar randomSearch(Solver solver, SetVar[] vars, long seed) {
		return new AssignSetVar(new RandomSetVarSelector(solver, vars, seed), new RandomSetValSelector(seed));
	}
	//*****************************************************************//
	//******************** Domain Over Degree ************************//
	//***************************************************************//

	public static AssignOrForbidIntVarVal domDegBin(Solver solver) {
		return domDegBin(solver, new MinVal());
	}

	public static AssignOrForbidIntVarVal domDegBin(Solver solver, ValSelector valSel) {
		return domDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignOrForbidIntVarVal domDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignOrForbidIntVarVal( domDegSel(solver, vars), valSel);
	}

	public static AssignOrForbidIntVarVal domDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignOrForbidIntVarVal( domDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//
	public static AssignVar domDeg(Solver solver) {
		return domDeg(solver, new IncreasingDomain());
	}

	public static AssignVar domDeg(Solver solver, ValIterator valSel) {
		return domDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new AssignVar( domDegSel(solver, vars), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new AssignVar( domDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//


	public static AssignVar domDeg(Solver solver, ValSelector valSel) {
		return domDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignVar( domDegSel(solver, vars), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignVar( domDegSel(solver, vars, seed), valSel);
	}
	//*****************************************************************//
	//******************** Domain Over Dynamic Degree ****************//
	//***************************************************************//

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver) {
		return domDDegBin(solver, new MinVal());
	}

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver, ValSelector valSel) {
		return domDDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignOrForbidIntVarVal( domDDegSel(solver, vars), valSel);
	}

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignOrForbidIntVarVal( domDDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//
	public static AssignVar domDDeg(Solver solver) {
		return domDDeg(solver, new IncreasingDomain());
	}

	public static AssignVar domDDeg(Solver solver, ValIterator valSel) {
		return domDDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new AssignVar( domDDegSel(solver, vars), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new AssignVar( domDDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//


	public static AssignVar domDDeg(Solver solver, ValSelector valSel) {
		return domDDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignVar( domDDegSel(solver, vars), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignVar( domDDegSel(solver, vars, seed), valSel);
	}


	//*****************************************************************//
	//******************** Domain Over Weighted Degree  **************//
	//***************************************************************//

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver) {
		return domWDegBin(solver, new MinVal());
	}

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver, ValSelector valSel) {
		return domWDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignOrForbidIntVarVal( new DomOverWDegSelector(solver, vars), valSel);
	}

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignOrForbidIntVarVal( new RandDomOverWDegSelector(solver, vars, seed), valSel);
	}

	//*************************************************************************//

	public static AssignVar domWDeg(Solver solver) {
		return domWDeg(solver, new IncreasingDomain());
	}

	public static AssignVar domWDeg(Solver solver, ValIterator valSel) {
		return domWDeg(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new AssignVar( domWDegSel(solver, vars), valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new AssignVar( domWDegSel(solver, vars, seed), valSel);
	}
	//*************************************************************************//

	public static AssignVar domWDeg(Solver solver, ValSelector valSel) {
		return domWDeg(solver,solver.getIntDecisionVars() , valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignVar( domWDegSel(solver, vars), valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignVar( domWDegSel(solver, vars, seed), valSel);
	}

	//*****************************************************************//
	//*******************  Task Domain Over Weighted Degree **********//
	//***************************************************************//


	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver) {
		return incDomWDegBin(solver, solver.getIntDecisionVars(), new MinVal());
	}

	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver, ValSelector valSel) {
		return incDomWDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new DomOverWDegBinBranchingNew(solver, vars, valSel, null);
	}

	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new DomOverWDegBinBranchingNew(solver, vars, valSel, seed);
	}

	//*************************************************************************//

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver) {
		return incDomWDeg(solver, new IncreasingDomain());
	}

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver, ValIterator valSel) {
		return incDomWDeg(solver, solver.getIntDecisionVars(), valSel);
	}

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new DomOverWDegBranchingNew(solver, vars, valSel, null);
	}

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new DomOverWDegBranchingNew(solver, vars, valSel, seed);
	}
	//*************************************************************************//

	public static TaskOverWDegBinBranching slackWDeg(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return slackWDeg(solver, precedences, new CentroidOrdering(seed));
	}

	public static TaskOverWDegBinBranching slackWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		return new TaskOverWDegBinBranching(solver, createSlackWDegRatio(precedences, true), valSel, null);
	}

	public static TaskOverWDegBinBranching slackWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		return new TaskOverWDegBinBranching(solver, createSlackWDegRatio(precedences, true), valSel, seed);
	}

	//*************************************************************************//

	public static TaskOverWDegBinBranching preservedWDeg(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return preservedWDeg(solver, precedences, new CentroidOrdering(seed));
	}

	public static TaskOverWDegBinBranching preservedWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		return new TaskOverWDegBinBranching(solver, createPreservedWDegRatio(precedences, true), valSel, null);
	}

	public static TaskOverWDegBinBranching preservedWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		return new TaskOverWDegBinBranching(solver, createPreservedWDegRatio(precedences, true), valSel, seed);
	}

	//*****************************************************************//
	//*******************  Preserved Heuristics **********************//
	//***************************************************************//

	public static AssignOrForbidIntVarValPair minPreserved(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return minPreserved(solver, precedences, new MinPreservedOrdering(seed), seed);
	}

	public static AssignOrForbidIntVarValPair minPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		final MinPreservedRatio[] ratios = createMinPreservedRatio(precedences);
		final MinRatioSelector varSel = new MinRatioSelector(solver, ratios);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}


	public static AssignOrForbidIntVarValPair minPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		final MinPreservedRatio[] ratios = createMinPreservedRatio(precedences);
		final RandMinRatioSelector varSel = new RandMinRatioSelector(solver, ratios, seed);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}

	//*************************************************************************//

	public static AssignOrForbidIntVarValPair maxPreserved(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return maxPreserved(solver, precedences, new MaxPreservedOrdering(seed), seed);
	}

	public static AssignOrForbidIntVarValPair maxPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		final MaxPreservedRatio[] ratios = createMaxPreservedRatio(precedences);
		final MaxRatioSelector varSel = new MaxRatioSelector(solver, ratios);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}

	public static AssignOrForbidIntVarValPair maxPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		final MaxPreservedRatio[] ratios = createMaxPreservedRatio(precedences);
		final RandMaxRatioSelector varSel = new RandMaxRatioSelector(solver, ratios, seed);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}

	
	//*************************************************************************//

	public static SetTimes setTimes(final Solver solver) {
		final TaskVar[] tasks = getTaskVars(solver);
		Arrays.sort(tasks,TaskComparators.makeRMinDurationCmp()); 
		return new SetTimes(solver,Arrays.asList(tasks) , TaskComparators.makeEarliestStartingTimeCmp(), false);
	}
	
	public static SetTimes setTimes(final Solver solver, final Comparator<ITask> comparator, final boolean randomized) {
		return setTimes(solver,getTaskVars(solver), comparator, randomized);
	}
	//FIXME set array in constructor
	public static SetTimes setTimes(final Solver solver, final TaskVar[] tasks, final Comparator<ITask> comparator, final boolean randomized) {
		return new SetTimes(solver, Arrays.asList(tasks), comparator, randomized);
	}


	
}
