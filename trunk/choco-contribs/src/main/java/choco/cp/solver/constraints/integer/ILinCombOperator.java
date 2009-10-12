package choco.cp.solver.constraints.integer;

import choco.IPretty;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.IntExp;

public interface ILinCombOperator extends IPretty {
	
	Boolean isEntailed(ILinCombSConstraint linComb);
	
	boolean isSatisfied(int val);
	
	boolean isConsistent(ILinCombSConstraint linComb);
	
	AbstractSConstraint opposite(Solver solver, IntExp leftMember, int rightMember);
	
	boolean filterOnImprovedLowerBound(ILinCombSConstraint linComb) throws ContradictionException;
	
	boolean filterOnImprovedUpperBound(ILinCombSConstraint linComb) throws ContradictionException;
	
}