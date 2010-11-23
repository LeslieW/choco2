package choco.cp.solver.search;

import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createDomDegRatio;
import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createDomDynDegRatio;
import static choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createMinPreservedRatio;
import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.varselector.ratioselector.DomOverWDegSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.MinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandDomOverWDegSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandMinRatioSelector;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class VarSelectorFactory {

	private VarSelectorFactory() {
		super();
	}
	
	//*************************************************************************//

	public static MinRatioSelector domDegSel(Solver solver, IntDomainVar[] vars) {
		return new MinRatioSelector(solver, createDomDegRatio(vars));
	}
	
	public static RandMinRatioSelector domDegSel(Solver solver, IntDomainVar[] vars, long seed) {
		return new RandMinRatioSelector(solver, createDomDegRatio(vars), seed);
	}
	
	//*************************************************************************//
	
	public static MinRatioSelector domDDegSel(Solver solver, IntDomainVar[] vars) {
		return new MinRatioSelector(solver, createDomDynDegRatio(vars));
	}
	
	public static RandMinRatioSelector domDDegSel(Solver solver, IntDomainVar[] vars, long seed) {
		return new RandMinRatioSelector(solver, createDomDynDegRatio(vars), seed);
	}

	//*************************************************************************//
	
	public static MinRatioSelector domWDegSel(Solver solver, IntDomainVar[] vars) {
		return new DomOverWDegSelector(solver, vars);
	}
	
	public static RandMinRatioSelector domWDegSel(Solver solver, IntDomainVar[] vars, long seed) {
		return new RandDomOverWDegSelector(solver, vars, seed);
	}
	
	
	//*************************************************************************//
	
	public static MinRatioSelector minPreserved(Solver solver, ITemporalSRelation[] precedences) {
		return new MinRatioSelector(solver, createMinPreservedRatio(precedences));
	}
	
	public static RandMinRatioSelector minPreserved(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return new RandMinRatioSelector(solver, createMinPreservedRatio(precedences), seed);
	}
	
//*************************************************************************//
	
	public static MinRatioSelector maxPreserved(Solver solver, ITemporalSRelation[] precedences) {
		return new MinRatioSelector(solver, createMinPreservedRatio(precedences));
	}
	
	public static RandMinRatioSelector maxPreserved(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return new RandMinRatioSelector(solver, createMinPreservedRatio(precedences), seed);
	}
		
	
 
}
